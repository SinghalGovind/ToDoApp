package com.example.gotodoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gotodoapp.UtilsService.SharedPreference;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

private androidx.appcompat.widget.Toolbar toolbar;
private DrawerLayout drawerLayout;
private ActionBarDrawerToggle drawerToggle;
private NavigationView navigationView;
private Button logout;
SharedPreference sharedPreference;
private CircleImageView circleImageView;
private TextView side_ename,side_uemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView=(NavigationView)findViewById(R.id.navigationView);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreference=new SharedPreference(this);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sharedPreference.clear();
//                startActivity(new Intent(MainActivity.this,LoginActivity.class));
//                finish();
//            }
//        });

View header_view=navigationView.getHeaderView(0);
        side_ename=(TextView)header_view.findViewById(R.id.side_username);
        side_uemail=(TextView)header_view.findViewById(R.id.side_email);
        circleImageView=(CircleImageView)header_view.findViewById(R.id.avatar);


                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setDrawerClick(item.getItemId());
                item.setChecked(true);
                drawerLayout.closeDrawers();
            return true;
            }
        });initDrawer();

        getUserProfile();

    }

    private void getUserProfile() {
        String url="https://gotodoapp.herokuapp.com/api/todo/auth/";
        final String token=sharedPreference.getValue_string("token");
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        JSONObject user_obj=response.getJSONObject("user");
                        side_ename.setText(user_obj.getString("username"));
                        side_uemail.setText(user_obj.getString("email"));
                        Picasso.with(getApplicationContext()).load(user_obj.getString("avatar"))
                                .placeholder(R.drawable.logo_full)
                        .error(R.drawable.logo_full)
                        .into(circleImageView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, error.toString()+"", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("Authorization",token);
                return  params;

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

    private void initDrawer() {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction ft=manager.beginTransaction();
        ft.replace(R.id.content,new HomeFragment());
        ft.commit();

        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setDrawerClick(int itemId) {

        switch (itemId){
            case R.id.action_finished_task:
                getSupportFragmentManager().beginTransaction().replace(R.id.content,new FinishedTaskFragment()).commit();
                break;
            case R.id.action_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.content,new HomeFragment()).commit();
                break;
            case R.id.action_logout:
                sharedPreference.clear();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                Intent sharingIntent=new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody="Hello try this app project by GOVIND SINGHAL(DATED:22/10/20)";
                sharingIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(sharingIntent,"header"));
                return true;
            case R.id.refresh_menu:
                getSupportFragmentManager().beginTransaction().replace(R.id.content,new HomeFragment()).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}