package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class ScheduledJobsFragment extends Fragment {
    IScheduledJobsFragment iScheduledJobsFragment;

    public ScheduledJobsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scheduled_jobs, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iScheduledJobsFragment = (IScheduledJobsFragment) context;
        iScheduledJobsFragment.setFragment("scheduledJobsFragment", this);
    }
    public void getScheduledJobs(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getScheduledJobs");
            request.put("url", "GetStationeries");
            request.put("requestBody", body);
            request.put("callbackFragment", "scheduledJobsFragment");
            request.put("callbackMethod", "getScheduledJobsCallback");
            iScheduledJobsFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getScheduledJobsCallback(String response) throws JSONException{
    }
    public interface IScheduledJobsFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

}
