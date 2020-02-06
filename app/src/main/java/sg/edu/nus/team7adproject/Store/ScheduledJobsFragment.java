package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class ScheduledJobsFragment extends Fragment implements View.OnClickListener {
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
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Button generateDeptRequestsButton = view.findViewById(R.id.button_scheduled_jobs_generate_dept_requests);
        generateDeptRequestsButton.setOnClickListener(this);
    }
    public void generateDepartmentRequests(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "generateDepartmentRequests");
            request.put("url", "GenerateDepartmentRequests");
            request.put("requestBody", body);
            request.put("callbackFragment", "scheduledJobsFragment");
            request.put("callbackMethod", "generateDepartmentRequestsCallback");
            iScheduledJobsFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void generateDepartmentRequestsCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Department requests generated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Failed to generate department requests", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_scheduled_jobs_generate_dept_requests:
                generateDepartmentRequests();
                break;
        }
    }
    public interface IScheduledJobsFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

}
