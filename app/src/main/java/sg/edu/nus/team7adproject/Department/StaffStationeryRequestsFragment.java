package sg.edu.nus.team7adproject.Department;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nus.team7adproject.Home.LoginFragment;
import sg.edu.nus.team7adproject.R;

public class StaffStationeryRequestsFragment extends Fragment {
    IStaffStationeryRequestsFragment iStaffStationeryRequestsFragment;
    public StaffStationeryRequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_stationery_requests, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStaffStationeryRequestsFragment = (StaffStationeryRequestsFragment.IStaffStationeryRequestsFragment) context;
        iStaffStationeryRequestsFragment.setFragment("staffStationeryRequestsFragment", this);
    }
    public void getStationeryRequests(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStationeryRequests");
            request.put("url", "StaffStationeryRequests");
            request.put("requestBody", body);
            request.put("callbackFragment", "staffStationeryRequestsFragment");
            request.put("callbackMethod", "getStationeryRequestsCallback");
            iStaffStationeryRequestsFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStationeryRequestsCallback(String response){
        Log.d("weizheng", response);
    }
    public interface IStaffStationeryRequestsFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
    }
}
