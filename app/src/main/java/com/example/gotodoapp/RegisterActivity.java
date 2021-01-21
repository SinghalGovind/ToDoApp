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

public class RegisterActivity extends AppCompatActivity {
TextView login,register;
private EditText name,email,password;
UtilService utilService;
String name_s,email_s,password_s;
SharedPreference sharedPreference;
private Toolbar mToolbar;
private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        login=findViewById(R.id.reg_loginbtn);

        //toolbar
        mToolbar=(Toolbar)findViewById(R.id.register_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Crete Account");
        //............................

        mProgress=new ProgressDialog(this);

        name=(EditText)findViewById(R.id.reg_name);
        email=(EditText)findViewById(R.id.reg_email);
        password=(EditText)findViewById(R.id.reg_password);
        register=findViewById(R.id.reg_registerBtn);
        utilService=new UtilService();
sharedPreference=new SharedPreference(this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilService.hideKeyboard(view,RegisterActivity.this);
                name_s=name.getText().toString();
                email_s=email.getText().toString();
                password_s=password.getText().toString();
                if(validate(view)){
                    mProgress.setTitle("Registering User");
                    mProgress.setMessage("Please wait while we create your account");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    registerUser(view);
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }

    private void registerUser(View view) {

        HashMap<String,String> params=new HashMap<>();
        params.put("username",name_s);
        params.put("email",email_s);
        params.put("password",password_s);
        String api_key=" https://gotodoapp.herokuapp.com/api/todo/auth/register";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, api_key, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        String token=response.getString("token");
                        sharedPreference.setValue_string("token",token);
                        Toast.makeText(RegisterActivity.this,token,Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
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
                        Toast.makeText(RegisterActivity.this,obj.getString("msg"),Toast.LENGTH_SHORT).show();
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

    public boolean validate(View view){
        boolean isValid;
        if(!TextUtils.isEmpty(name_s)){
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
        }
        else{
            utilService.showSnackBar(view,"Please enter name");
            isValid=false;
        }
        return isValid;
    }
    protected void onStart() {
        super.onStart();
        SharedPreferences todo_pref=getSharedPreferences("user_todo",MODE_PRIVATE);
        if(todo_pref.contains("token")){
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        }
    }
}