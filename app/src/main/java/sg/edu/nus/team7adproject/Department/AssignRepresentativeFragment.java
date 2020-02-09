package sg.edu.nus.team7adproject.Department;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class AssignRepresentativeFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, View.OnClickListener{
    IAssignRepresentativeFragment iAssignRepresentativeFragment;
    TextView currentRepView;
    Spinner newRepSpinner;
    HashMap<Integer, Integer> posToStaffId;
    int selectedStaffId;
    Button assignButton;

    public AssignRepresentativeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        posToStaffId = new HashMap<Integer, Integer>();
        getAssignRepresentative();
        getDepartmentStaff();
        return inflater.inflate(R.layout.fragment_assign_representative, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        currentRepView = view.findViewById(R.id.textview_assign_representative_current_rep);
        newRepSpinner = view.findViewById(R.id.spinner_assign_representative_new_rep);
        assignButton = view.findViewById(R.id.button_assign_representative_assign);
        assignButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iAssignRepresentativeFragment = (IAssignRepresentativeFragment) context;
        iAssignRepresentativeFragment.setFragment("assignRepresentativeFragment", this);
    }
    public void getAssignRepresentative(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getAssignRepresentative");
            request.put("url", "GetAssignRepresentative");
            request.put("requestBody", body);
            request.put("callbackFragment", "assignRepresentativeFragment");
            request.put("callbackMethod", "getAssignRepresentativeCallback");
            iAssignRepresentativeFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getAssignRepresentativeCallback(String response) throws JSONException{
        JSONObject currentRep = new JSONObject(response);
        currentRepView.setText(currentRep.getString("name"));
    }
    public void getDepartmentStaff(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getDepartmentStaff");
            request.put("url", "GetDepartmentStaff");
            request.put("requestBody", body);
            request.put("callbackFragment", "assignRepresentativeFragment");
            request.put("callbackMethod", "getDepartmentStaffCallback");
            iAssignRepresentativeFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getDepartmentStaffCallback(String response) throws JSONException{
        JSONArray departmentStaffs = new JSONArray(response);
        String staffStr[] = new String[departmentStaffs.length()];
        for(int i = 0; i < departmentStaffs.length(); i++){
            JSONObject departmentStaff = departmentStaffs.getJSONObject(i);
            staffStr[i] = departmentStaff.getString("name");
            posToStaffId.put(i, departmentStaff.getInt("id"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, staffStr);
        newRepSpinner.setAdapter(adapter);
        newRepSpinner.setOnItemSelectedListener(this);
    }

    public void assignRepresentative(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "assignRepresentative");
            body.put("staffId", selectedStaffId);
            request.put("url", "AssignRepresentative");
            request.put("requestBody", body);
            request.put("callbackFragment", "assignRepresentativeFragment");
            request.put("callbackMethod", "assignRepresentativeCallback");
            iAssignRepresentativeFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void assignRepresentativeCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Assigned new representative", Toast.LENGTH_SHORT).show();
            getAssignRepresentative();
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_assign_representative_assign:
                assignRepresentative();
                break;
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapter, View view, int pos, long id){
        selectedStaffId = posToStaffId.get(pos);
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0){}
    public interface IAssignRepresentativeFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

}
