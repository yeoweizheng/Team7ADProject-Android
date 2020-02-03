package sg.edu.nus.team7adproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import sg.edu.nus.team7adproject.Home.LoginFragment;
import sg.edu.nus.team7adproject.Home.SettingsFragment;

public class HomeActivity extends AppCompatActivity
        implements ServiceConnection, ServerService.IServerService,
        LoginFragment.ILoginFragment {
    private AppBarConfiguration appBarConfiguration;
    private ServerService serverService;
    private SharedPreferences serverAddressPref;
    private HashMap<String, Fragment> fragmentHashMap = new HashMap<String, Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_home);
        NavigationView navigationView = findViewById(R.id.nav_view_home);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_home);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_login, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        Intent intent = new Intent(HomeActivity.this, ServerService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        serverAddressPref = getSharedPreferences("serverAddress", Context.MODE_PRIVATE);
    }
    @Override
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_home);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
    @Override
    public void onResume(){
        super.onResume();
        if(serverService != null) serverService.setCallback(this);
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
    @Override
    public void sendRequest(final JSONObject request){
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
            Method method = fragmentHashMap.get(callbackFragment).getClass().getMethod(callbackMethod, String.class);
            method.invoke(fragmentHashMap.get(callbackFragment), response);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void setFragment(String name, Fragment fragment){
        fragmentHashMap.put(name, fragment);
    }
    @Override
    public void onBackPressed(){
        finish();
    }
    @Override
    public void launchActivity(String userType){
        switch(userType){
            case "departmentStaff":
                Intent intent = new Intent(this, DepartmentActivity.class);
                startActivity(intent);
                break;
            case "storeClerk":
                Intent intent1 = new Intent(this, StoreActivity.class);
                startActivity(intent1);
                break;
        }
    }
    @Override
    public AppCompatActivity getActivity(){
        return this;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(this);
    }
}
