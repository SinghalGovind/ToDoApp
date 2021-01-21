
package com.example.gotodoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gotodoapp.UtilsService.SharedPreference;
import com.example.gotodoapp.UtilsService.UtilService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView reg,log;
    private EditText email,password;
    private ProgressDialog mProgress;
    UtilService utilService;
    String name_s,email_s,password_s;
    SharedPreference sharedPreference;
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //toolbar
        mToolbar=(Toolbar)findViewById(R.id.login_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login Back");
        //............................

        reg=findViewById(R.id.login_reg);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        email=(EditText)findViewById(R.id.login_email);
        password=(EditText)findViewById(R.id.login_password);
        utilService=new UtilService();
        sharedPreference=new SharedPreference(this);
        mProgress=new ProgressDialog(this);
        
        log=findViewById(R.id.login_login);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilService.hideKeyboard(view,LoginActivity.this);
                email_s=email.getText().toString();
                password_s=password.getText().toString();
                if(validate(view)){
                    mProgress.setTitle("Logging In User");
                    mProgress.setMessage("Please wait while you are being logged in!");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    loginUser(view);
                }
            }
        });
    }
    private boolean validate(View view) {
        boolean isValid;
            if(!TextUtils.isEmpty(email_s)){
                if(!TextUtils.isEmpty(password_s)){
                    isValid=true;
                }
                else{
                    utilService.showSnackBar(view,"Please enter password");
                    isValid=false;
                }
            }
            else{
                utilService.showSnackBar(view,"Please enter email");
                isValid=false;
            }

        return isValid;
    }
    private void loginUser(View view) {
        HashMap<String,String> params=new HashMap<>();

        params.put("email",email_s);
        params.put("password",password_s);
        String api_key=" https://gotodoapp.herokuapp.com/api/todo/auth/login";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, api_key, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        String token=response.getString("token");
                        sharedPreference.setValue_string("token",token);

                        Toast.makeText(LoginActivity.this,token,Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }
                    mProgress.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response=error.networkResponse;
                if(error instanceof ServerError && response!=null){
                    try{
                        String res=new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));
                        JSONObject obj=new JSONObject(res);
                        Toast.makeText(LoginActivity.this,obj.getString("msg"),Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }catch(JSONException | UnsupportedEncodingException je){
                        je.printStackTrace();
                        mProgress.dismiss();
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String>headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                return  headers;

            }
        };


        //set retry policy<-in cae of server error
        int socketTime=3000;
        RetryPolicy policy=new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        //request add
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);


    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences todo_pref=getSharedPreferences("user_todo",MODE_PRIVATE);
        if(todo_pref.contains("token")){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
}