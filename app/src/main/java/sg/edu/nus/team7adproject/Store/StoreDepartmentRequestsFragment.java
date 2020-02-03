package sg.edu.nus.team7adproject.Store;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nus.team7adproject.Department.StaffStationeryRequestsFragment;
import sg.edu.nus.team7adproject.R;

public class StoreDepartmentRequestsFragment extends Fragment implements View.OnClickListener{
    IStoreDepartmentRequestsFragment iStoreDepartmentRequestsFragment;
    public StoreDepartmentRequestsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store_department_requests, container, false);
    }

    /*
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        FloatingActionButton fab = view.findViewById(R.id.nav_store_department_requests);
        fab.setOnClickListener(this);
    }*/

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStoreDepartmentRequestsFragment = (StoreDepartmentRequestsFragment.IStoreDepartmentRequestsFragment) context;
        iStoreDepartmentRequestsFragment.setFragment("storeDepartmentRequestsFragment", this);
    }
    public void getStationeryRequests(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getDepartmentRequests");
            request.put("url", "DepartmentRequests");
            request.put("requestBody", body);
            request.put("callbackFragment", "DepartmentRequestsFragment");
            request.put("callbackMethod", "getDepartmentRequestsCallback");
            Log.d("xiaomin", request.toString());
            iStoreDepartmentRequestsFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View view) {

    }
    public interface IStoreDepartmentRequestsFragment {
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

}

