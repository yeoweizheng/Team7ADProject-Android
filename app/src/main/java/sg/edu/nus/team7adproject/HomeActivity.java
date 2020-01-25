package sg.edu.nus.team7adproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import sg.edu.nus.team7adproject.Home.SettingsFragment;

public class HomeActivity extends AppCompatActivity
        implements ServiceConnection, SettingsFragment.ISettingsFragment {
    private AppBarConfiguration appBarConfiguration;
    private ServerService serverService;

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
    }
    @Override
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_home);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ServerService.LocalBinder binder = (ServerService.LocalBinder) service;
        if(binder != null){
            serverService = binder.getService();
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){
    }

    public void sendRequest(JSONObject request){
        if(serverService != null){
            serverService.sendRequest(request);
        }
    }
}
