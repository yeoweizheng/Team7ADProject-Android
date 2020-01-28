package sg.edu.nus.team7adproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import sg.edu.nus.team7adproject.Department.StaffStationeryRequestsFragment;
import sg.edu.nus.team7adproject.Home.LoginFragment;

public class DepartmentActivity extends AppCompatActivity
        implements ServiceConnection, ServerService.IServerService,
        StaffStationeryRequestsFragment.IStaffStationeryRequestsFragment
    {
    private AppBarConfiguration appBarConfiguration;
    private ServerService serverService;
    private SharedPreferences serverAddressPref;
    private SharedPreferences sessionPref;
    private HashMap<String, Fragment> fragmentHashMap = new HashMap<String, Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_department);
        NavigationView navigationView = findViewById(R.id.nav_view_department);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_department);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_staff_stationery_requests, R.id.nav_staff_disbursement_lists,
                R.id.nav_notifications, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        Intent intent = new Intent(DepartmentActivity.this, ServerService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        serverAddressPref = getSharedPreferences("serverAddress", Context.MODE_PRIVATE);
        sessionPref = getSharedPreferences("session", Context.MODE_PRIVATE);
    }
    @Override
    public void onResume(){
        super.onResume();
        if(serverService != null) serverService.setCallback(this);
    }
    @Override
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_department);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ServerService.LocalBinder binder = (ServerService.LocalBinder) service;
        if(binder != null){
            serverService = binder.getService();
            serverService.setCallback(this);
            while(!fragmentHashMap.containsKey("staffStationeryRequestsFragment")){
                try {
                    Thread.sleep(100);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            ((StaffStationeryRequestsFragment)fragmentHashMap.get("staffStationeryRequestsFragment")).getStationeryRequests();
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){
    }
    @Override
    public String getServerAddressFromSharedPref(){
        return serverAddressPref.getString("serverAddress", "");
    }
    @Override
    public void sendRequest(JSONObject request){
        try {
            JSONObject body = request.getJSONObject("requestBody");
            if(!body.has("sessionId")){
                body.put("sessionId", sessionPref.getString("sessionId", ""));
                request.remove("requestBody");
                request.put("requestBody", body);
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
        if(serverService != null){
            serverService.sendRequest(request);
        }
    }
    @Override
    public void handleResponse(String response, String callbackFragment, String callbackMethod){
        try {
            Method method = fragmentHashMap.get(callbackFragment).getClass().getMethod(callbackMethod, String.class);
            method.invoke(fragmentHashMap.get(callbackFragment), response);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public AppCompatActivity getActivity(){
        return this;
    }
    @Override
    public void setFragment(String name, Fragment fragment){
        fragmentHashMap.put(name, fragment);
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(this);
    }
}
