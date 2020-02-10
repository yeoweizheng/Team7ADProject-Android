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
import sg.edu.nus.team7adproject.Shared.NotificationDetailFragment;
import sg.edu.nus.team7adproject.Shared.NotificationsFragment;
import sg.edu.nus.team7adproject.Shared.NotificationsFragmentDirections;
import sg.edu.nus.team7adproject.Store.AddOrderFragment;
import sg.edu.nus.team7adproject.Store.AdjustmentVoucherDetailFragment;
import sg.edu.nus.team7adproject.Store.AdjustmentVouchersFragment;
import sg.edu.nus.team7adproject.Store.AdjustmentVouchersFragmentDirections;
import sg.edu.nus.team7adproject.Store.OrderDetailFragment;
import sg.edu.nus.team7adproject.Store.StockDetailFragment;
import sg.edu.nus.team7adproject.Store.StockListFragment;
import sg.edu.nus.team7adproject.Store.StockListFragmentDirections;
import sg.edu.nus.team7adproject.Store.StoreOrdersFragment;
import sg.edu.nus.team7adproject.Store.StoreOrdersFragmentDirections;

public class StoreSupActivity extends AppCompatActivity
        implements ServiceConnection, ServerService.IServerService,
        StockListFragment.IStockListFragment,
        StockDetailFragment.IStockDetailFragment,
        AdjustmentVouchersFragment.IAdjustmentVouchersFragment,
        AdjustmentVoucherDetailFragment.IAdjustmentVoucherDetailFragment,
        StoreOrdersFragment.IStoreOrdersFragment,
        AddOrderFragment.IAddOrderFragment,
        OrderDetailFragment.IOrderDetailFragment,
        NotificationsFragment.INotificationsFragment,
        NotificationDetailFragment.INotificationDetailFragment,
        LogoutFragment.ILogoutFragment {
    private AppBarConfiguration appBarConfiguration;
    private ServerService serverService;
    private SharedPreferences serverAddressPref;
    private SharedPreferences sessionPref;
    private HashMap<String, Fragment> fragmentHashMap = new HashMap<String, Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_sup);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_store_sup);
        NavigationView navigationView = findViewById(R.id.nav_view_store_sup);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_sup);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_stock_list,
                R.id.nav_adjustment_vouchers,
                R.id.nav_store_orders,
                R.id.nav_notifications,
                R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        Intent intent = new Intent(StoreSupActivity.this, ServerService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        serverAddressPref = getSharedPreferences("serverAddress", Context.MODE_PRIVATE);
        sessionPref = getSharedPreferences("session", Context.MODE_PRIVATE);
    }
    public void onResume(){
        super.onResume();
        if(serverService != null) serverService.setCallback(this);
    }
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_sup);
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
                if (resObj.getString("result").equals("forbidden")) {
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_sup);
        switch(name) {
            case "stockDetail":
                action = StockListFragmentDirections.actionNavStockListToNavStockDetail(id);
                break;
            case "adjustmentVoucherDetail":
                action = AdjustmentVouchersFragmentDirections.actionNavAdjustmentVouchersToNavAdjustmentVoucherDetail(id);
                break;
            case "orderDetail":
                action = StoreOrdersFragmentDirections.actionNavStoreOrdersToNavOrderDetail(id);
                break;
            case "notificationDetail":
                action = NotificationsFragmentDirections.actionNavNotificationsToNavNotificationDetail(id);
                break;
        }
        navController.navigate(action);
    }
    @Override
    public void gotoFragment(String name) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_sup);
        switch(name){
            case "addOrder":
                navController.navigate(R.id.nav_add_order);
                break;
        }
    }
    @Override
    public void onBackPressed(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_store_sup);
        navController.navigateUp();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(this);
    }
}
