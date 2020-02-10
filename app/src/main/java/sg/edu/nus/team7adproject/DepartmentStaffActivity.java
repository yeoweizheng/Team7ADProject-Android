package sg.edu.nus.team7adproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
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
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import sg.edu.nus.team7adproject.Department.AddStationeryRequestFragment;
import sg.edu.nus.team7adproject.Department.StaffDepartmentRequestDetailFragment;
import sg.edu.nus.team7adproject.Department.StaffDepartmentRequestsFragment;
import sg.edu.nus.team7adproject.Department.StaffDepartmentRequestsFragmentDirections;
import sg.edu.nus.team7adproject.Department.StaffStationeryRequestsFragment;
import sg.edu.nus.team7adproject.Department.StaffStationeryRequestsFragmentDirections;
import sg.edu.nus.team7adproject.Department.StationeryRequestDetailFragment;
import sg.edu.nus.team7adproject.Shared.LogoutFragment;
import sg.edu.nus.team7adproject.Shared.NotificationDetailFragment;
import sg.edu.nus.team7adproject.Shared.NotificationsFragment;
import sg.edu.nus.team7adproject.Shared.NotificationsFragmentDirections;

public class DepartmentStaffActivity extends AppCompatActivity
        implements ServiceConnection, ServerService.IServerService,
        StaffStationeryRequestsFragment.IStaffStationeryRequestsFragment,
        StationeryRequestDetailFragment.IStationeryRequestDetailFragment,
        AddStationeryRequestFragment.IAddStationeryRequestFragment,
        StaffDepartmentRequestsFragment.IStaffDepartmentRequestsFragment,
        StaffDepartmentRequestDetailFragment.IStaffDepartmentRequestDetailFragment,
        NotificationsFragment.INotificationsFragment,
        NotificationDetailFragment.INotificationDetailFragment,
        LogoutFragment.ILogoutFragment {
    private AppBarConfiguration appBarConfiguration;
    private ServerService serverService;
    private SharedPreferences serverAddressPref;
    private SharedPreferences sessionPref;
    private HashMap<String, Fragment> fragmentHashMap = new HashMap<String, Fragment>();
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_staff);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_department_staff);
        navigationView = findViewById(R.id.nav_view_department_staff);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_department_staff);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_staff_stationery_requests, R.id.nav_staff_department_requests,
                R.id.nav_notifications, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        Intent intent = new Intent(DepartmentStaffActivity.this, ServerService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        serverAddressPref = getSharedPreferences("serverAddress", Context.MODE_PRIVATE);
        sessionPref = getSharedPreferences("session", Context.MODE_PRIVATE);
        getStaffAuthorization();
    }
    @Override
    public void onResume(){
        super.onResume();
        if(serverService != null) serverService.setCallback(this);
    }
    @Override
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_department_staff);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ServerService.LocalBinder binder = (ServerService.LocalBinder) service;
        if(binder != null){
            serverService = binder.getService();
            serverService.setCallback(this);
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
    public String getServerPortFromSharedPref(){
        return serverAddressPref.getString("port", "");
    }
    public void getStaffAuthorization(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStaffAuthorization");
            request.put("url", "GetStaffAuthorization");
            request.put("requestBody", body);
            request.put("callbackFragment", "departmentStaffActivity");
            request.put("callbackMethod", "getStaffAuthorizationCallback");
            sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStaffAuthorizationCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        Menu menu = navigationView.getMenu();
        if(responseObj.getBoolean("isRepresentative")){
            menu.findItem(R.id.nav_staff_department_requests).setVisible(true);
        }
        menu.getItem(0).setChecked(true);
    }
    @Override
    public void sendRequest(final JSONObject request){
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(serverService == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                serverService.sendRequest(request);
            }
        }).start();
    }
    @Override
    public void handleResponse(String response, String callbackFragment, String callbackMethod){
        try {
            try {
                JSONObject resObj = new JSONObject(response);
                if (resObj.getString("result").equals("forbidden")) {
                    finish();
                    return;
                }
            } catch(JSONException e){}
            if(callbackFragment.equals("departmentStaffActivity")){
                Method method = this.getClass().getMethod(callbackMethod, String.class);
                method.invoke(this, response);
            } else {
                Method method = fragmentHashMap.get(callbackFragment).getClass().getMethod(callbackMethod, String.class);
                method.invoke(fragmentHashMap.get(callbackFragment), response);
            }
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
    public void gotoFragment(String name, int id){
        NavDirections action = null;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_department_staff);
        switch(name){
            case "stationeryRequestDetail":
                action = StaffStationeryRequestsFragmentDirections.actionNavStaffStationeryRequestsToNavStationeryRequestDetail(id);
                break;
            case "departmentRequestDetail":
                action = StaffDepartmentRequestsFragmentDirections.actionNavStaffDepartmentRequestsToNavStaffDepartmentRequestDetail(id);
                break;
            case "notificationDetail":
                action = NotificationsFragmentDirections.actionNavNotificationsToNavNotificationDetail(id);
                break;
        }
        navController.navigate(action);
    }
    @Override
    public void gotoFragment(String name){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_department_staff);
        switch(name){
            case "addStationeryRequest":
                navController.navigate(R.id.nav_add_stationery_request);
                break;
            case "staffStationeryRequests":
                navController.navigate(R.id.nav_staff_stationery_requests);
                break;
        }
    }
    @Override
    public void onBackPressed(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_department_staff);
        navController.navigateUp();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(this);
    }
}
