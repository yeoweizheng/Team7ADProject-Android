package sg.edu.nus.team7adproject.Department;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import sg.edu.nus.team7adproject.R;

public class AddAuthorizeStaffFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, View.OnClickListener{
    IAddAuthorizeStaffFragment iAddAuthorizeStaffFragment;
    Spinner staffSpinner;
    HashMap<Integer, Integer> posToStaffId;
    int selectedStaffId;
    Button addButton;
    EditText startDateEditText;
    EditText endDateEditText;
    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;
    SimpleDateFormat dateFormatter;

    public AddAuthorizeStaffFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dateFormatter = new SimpleDateFormat("dd-MMM-yy", Locale.US);
        posToStaffId = new HashMap<Integer, Integer>();
        getDepartmentStaff();
        return inflater.inflate(R.layout.fragment_add_authorize_staff, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        staffSpinner = view.findViewById(R.id.spinner_add_authorize_staff_name);
        addButton = view.findViewById(R.id.button_add_authorize_staff_add);
        startDateEditText = view.findViewById(R.id.edittext_add_authorize_staff_start_date);
        endDateEditText = view.findViewById(R.id.edittext_add_authorize_staff_end_date);
        addButton.setOnClickListener(this);
        startDateEditText.setOnClickListener(this);
        endDateEditText.setOnClickListener(this);
        setDateTimeField();
    }

    void setDateTimeField(){
        Calendar calendar = Calendar.getInstance();
        startDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                startDateEditText.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        endDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                endDateEditText.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iAddAuthorizeStaffFragment = (IAddAuthorizeStaffFragment) context;
        iAddAuthorizeStaffFragment.setFragment("addAuthorizeStaffFragment", this);
    }
    public void getDepartmentStaff(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getDepartmentStaff");
            request.put("url", "GetDepartmentStaff");
            request.put("requestBody", body);
            request.put("callbackFragment", "addAuthorizeStaffFragment");
            request.put("callbackMethod", "getDepartmentStaffCallback");
            iAddAuthorizeStaffFragment.sendRequest(request);
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
        staffSpinner.setAdapter(adapter);
        staffSpinner.setOnItemSelectedListener(this);
    }
    public void addAuthorizedtaff(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "addAuthorizedStaff");
            body.put("staffId", selectedStaffId);
            body.put("startDate", startDateEditText.getText().toString());
            body.put("endDate", endDateEditText.getText().toString());
            request.put("url", "AddAuthorizedStaff");
            request.put("requestBody", body);
            request.put("callbackFragment", "addAuthorizeStaffFragment");
            request.put("callbackMethod", "addAuthorizedStaffCallback");
            iAddAuthorizeStaffFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void addAuthorizedStaffCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Added authorized staff", Toast.LENGTH_SHORT).show();
            iAddAuthorizeStaffFragment.onBackPressed();
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_add_authorize_staff_add:
                addAuthorizedtaff();
                break;
            case R.id.edittext_add_authorize_staff_start_date:
                startDatePickerDialog.show();
                break;
            case R.id.edittext_add_authorize_staff_end_date:
                endDatePickerDialog.show();
                break;
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapter, View view, int pos, long id){
        selectedStaffId = posToStaffId.get(pos);
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0){}
    public interface IAddAuthorizeStaffFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
        void onBackPressed();
    }

}
