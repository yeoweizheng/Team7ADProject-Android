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

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import sg.edu.nus.team7adproject.Shared.LogoutFragment;
import sg.edu.nus.team7adproject.Shared.NotificationsFragment;
import sg.edu.nus.team7adproject.Store.ScheduledJobsFragment;
import sg.edu.nus.team7adproject.Store.StoreAdjustmentVouchersFragment;
import sg.edu.nus.team7adproject.Store.StoreDepartmentRequestsFragment;
import sg.edu.nus.team7adproject.Store.StoreDisbursementListsFragment;
import sg.edu.nus.team7adproject.Store.StoreOrdersFragment;
import sg.edu.nus.team7adproject.Store.StoreStationeryRetrievalListsFragment;
import sg.edu.nus.team7adproject.Store.StockListFragment;

public class StoreClerkActivity extends AppCompatActivity
    implements ServiceConnection, ServerService.IServerService,
        StoreDepartmentRequestsFragment.IStoreDepartmentRequestsFragment,
        StoreStationeryRetrievalListsFragment.IStoreStationeryRetrievalListsFragment,
        StoreDisbursementListsFragment.IStoreDisbursementListsFragment,
        StockListFragment.IStockListFragment,
        StoreAdjustmentVouchersFragment.IStoreAdjustmentVouchersFragment,
        StoreOrdersFragment.IStoreOrdersFragment,
        NotificationsFragment.INotificationsFragment,
        ScheduledJobsFragment.IScheduledJobsFragment,
        LogoutFragment.ILogoutFragment
{
    private AppBarConfiguration appBarConfiguration;
    private ServerService serverService;
    private SharedPreferences serverAddressPref;
    private SharedPreferences sessionPref;
    private HashMap<String, Fragment> fragmentHashMap = new HashMap<String, Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_clerk);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_store_clerk);
        NavigationView navigationView = findViewById(R.id.nav_view_store_clerk);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_clerk);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_store_adjustment_vouchers, R.id.nav_store_department_requests,
                R.id.nav_store_disbursement_lists, R.id.nav_store_stationery_retrieval_lists,
                R.id.nav_store_orders, R.id.nav_stock_list,
                R.id.nav_notifications, R.id.nav_scheduled_jobs,
                R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        Intent intent = new Intent(StoreClerkActivity.this, ServerService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        serverAddressPref = getSharedPreferences("serverAddress", Context.MODE_PRIVATE);
        sessionPref = getSharedPreferences("session", Context.MODE_PRIVATE);
    }
    public void onResume(){
        super.onResume();
        if(serverService != null) serverService.setCallback(this);
    }
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_clerk);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ServerService.LocalBinder binder = (ServerService.LocalBinder) service;
        if (binder != null) {
            serverService = binder.getService();
            serverService.setCallback(this);
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){
        }

    @Override
    public String getServerAddressFromSharedPref() {
        return serverAddressPref.getString("serverAddress", "");
    }

    @Override
    public String getServerPortFromSharedPref() {
            return serverAddressPref.getString("port", "");
    }

    @Override
    public void handleResponse(String response, String callbackFragment, String callbackMethod) {
        try {
            try {
                JSONObject resObj = new JSONObject(response);
                if (resObj.getString("result").equals("failed")) {
                    finish();
                    return;
                }
            } catch(JSONException e){}
            Method method = fragmentHashMap.get(callbackFragment).getClass().getMethod(callbackMethod, String.class);
            method.invoke(fragmentHashMap.get(callbackFragment), response);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public AppCompatActivity getActivity() {
        return this;
    }

    @Override
    public void sendRequest(final JSONObject request) {
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
    public void setFragment(String name, Fragment fragment) {
        fragmentHashMap.put(name, fragment);
    }
    @Override
    public void gotoFragment(String name, int id) {
        NavDirections action = null;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_clerk);

    }
    @Override
    public void gotoFragment(String name) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_clerk);
    }
    @Override
    public void onBackPressed(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_clerk);
        navController.navigateUp();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(this);
    }
}
