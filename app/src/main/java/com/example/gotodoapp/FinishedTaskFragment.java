package com.example.gotodoapp;

import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gotodoapp.ADAPTER.FinishedToDoListAdapter;
import com.example.gotodoapp.ADAPTER.ToDoListAdapter;
import com.example.gotodoapp.UtilsService.SharedPreference;
import com.example.gotodoapp.interfaces.RecyclerViewClickListner;
import com.example.gotodoapp.model.ToDoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FinishedTaskFragment extends Fragment implements RecyclerViewClickListner {
    SharedPreference sharedPreferenceClass;
    String token;
    FinishedToDoListAdapter todoListAdapter;
    RecyclerView recyclerView;
    TextView empty_tv;
    ProgressBar progressBar;
    ArrayList<ToDoModel> arrayList;

    public FinishedTaskFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_finished_task, container, false);


        sharedPreferenceClass = new SharedPreference(getContext());
        token = sharedPreferenceClass.getValue_string("token");

        recyclerView = view.findViewById(R.id.finished_recycler_view);
        empty_tv = view.findViewById(R.id.finished_empty_tv);
        progressBar = view.findViewById(R.id.finished_progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        getTasks();


        return  view;
    }
    public void getTasks() {
        arrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        String url = " https://gotodoapp.herokuapp.com/api/todo/finished";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")) {
                      //  Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        JSONArray jsonArray = response.getJSONArray("todos");

                        for(int i = 0; i < jsonArray.length(); i ++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            ToDoModel todoModel = new ToDoModel(
                                    jsonObject.getString("_id"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("description")
                            );
                            arrayList.add(todoModel);
                        }
//
                        todoListAdapter = new FinishedToDoListAdapter(getActivity(), arrayList,FinishedTaskFragment.this);
                        recyclerView.setAdapter(todoListAdapter);

                    }
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response=error.networkResponse;
                if(error==null||error.networkResponse==null){
                    return;
                }
                String body;
                //final String statusCode=String.valueOf(error.networkResponse.statusCode);
                try {
                    body=new String(error.networkResponse.data,"UTF-8");
                    JSONObject errorObject=new JSONObject(body);

                    if(errorObject.getString("msg").equals("Token not valid")){
                        sharedPreferenceClass.clear();
                        startActivity(new Intent(getActivity(),LoginActivity.class));
                        Toast.makeText(getActivity(), "Session expired", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e){

                }

                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        // set retry policy
        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // request add
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }
    public void showDeleteDialog(final String id,final int position) {

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Confirm to delete the task")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog)alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTodo(id,position);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();

    }

    private void deleteTodo(final String id,final int position){
        String url="https://gotodoapp.herokuapp.com/api/todo/"+id;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        Toast.makeText(getContext(), "msg", Toast.LENGTH_SHORT).show();
                        arrayList.remove(position);
                        todoListAdapter.notifyItemRemoved(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue=Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLongItemClick(int position) {
        showDeleteDialog(arrayList.get(position).getId(),position);
    }

    @Override
    public void onEditButtonClick(int position) {

    }

    @Override
    public void onDeleteButtonClick(int position) {
        showDeleteDialog(arrayList.get(position).getId(),position);
    }

    @Override
    public void onDoneButtonClick(int position) {

    }
}