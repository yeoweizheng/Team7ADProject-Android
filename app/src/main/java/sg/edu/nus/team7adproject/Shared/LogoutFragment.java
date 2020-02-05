package sg.edu.nus.team7adproject.Shared;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nus.team7adproject.Home.LoginFragment;
import sg.edu.nus.team7adproject.R;

public class LogoutFragment extends Fragment {
    LogoutFragment.ILogoutFragment iLogoutFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        logout();
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iLogoutFragment = (LogoutFragment.ILogoutFragment) context;
        iLogoutFragment.setFragment("logoutFragment", this);
    }
    public void logout(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try{
            body.put("action", "logout");
            request.put("url", "Logout");
            request.put("requestBody", body);
            request.put("callbackFragment", "logoutFragment");
            request.put("callbackMethod", "logoutCallback");
            iLogoutFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void logoutCallback(String response){
        iLogoutFragment.finish();
    }
    public interface ILogoutFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void finish();
    }
}
