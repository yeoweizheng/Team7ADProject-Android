package sg.edu.nus.team7adproject.Department;


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

public class StaffDepartmentRequestDetailFragment extends Fragment implements View.OnClickListener{

    StaffDepartmentRequestDetailFragment.IStaffDepartmentRequestDetailFragment iStaffDepartmentRequestDetailFragment;
    Button acceptButton;
    Button rejectButton;
    int departmentRequestId;

    public StaffDepartmentRequestDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_department_request_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        departmentRequestId = StaffDepartmentRequestDetailFragmentArgs.fromBundle(getArguments()).getDepartmentRequestId();
        acceptButton = view.findViewById(R.id.button_staff_department_request_detail_accept);
        rejectButton = view.findViewById(R.id.button_staff_department_request_detail_reject);
        getStaffDepartmentRequestDetail();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStaffDepartmentRequestDetailFragment = (StaffDepartmentRequestDetailFragment.IStaffDepartmentRequestDetailFragment) context;
        iStaffDepartmentRequestDetailFragment.setFragment("staffDepartmentRequestDetailFragment", this);
    }
    public void getStaffDepartmentRequestDetail(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStaffDepartmentRequestDetail");
            body.put("departmentRequestId", departmentRequestId);
            request.put("url", "DepartmentRequestDetail");
            request.put("requestBody", body);
            request.put("callbackFragment", "staffDepartmentRequestDetailFragment");
            request.put("callbackMethod", "getStaffDepartmentRequestDetailCallback");
            iStaffDepartmentRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStaffDepartmentRequestDetailCallback(String response) throws JSONException{
        JSONObject stationeryRequestDetail = new JSONObject(response);
        JSONArray stationeryQuantities = stationeryRequestDetail.getJSONArray("stationeryQuantities");
        TextView idView = getActivity().findViewById(R.id.textview_staff_department_request_detail_id);
        TextView dateView = getActivity().findViewById(R.id.textview_staff_department_request_detail_date);
        TextView remarksView = getActivity().findViewById(R.id.textview_staff_department_request_detail_remarks);
        TextView statusView = getActivity().findViewById(R.id.textview_staff_department_request_detail_status);
        idView.setText(stationeryRequestDetail.get("id").toString());
        dateView.setText(stationeryRequestDetail.get("date").toString());
        remarksView.setText(stationeryRequestDetail.get("remarks").toString());
        statusView.setText(stationeryRequestDetail.get("status").toString());
        ListView listView = getActivity().findViewById(R.id.listview_staff_department_request_detail);
        ArrayList<RowItem> rowItemList = new ArrayList<>();
        for(int i = 0; i < stationeryQuantities.length(); i++){
            JSONObject stationeryQuantity = stationeryQuantities.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationeryQuantity.getInt("stationeryId"),
                    stationeryQuantity.getString("description"),
                    stationeryQuantity.getString("quantityRequested"),
                    stationeryQuantity.getString("quantityRetrieved"),
                    stationeryQuantity.getString("quantityDisbursed"));
            rowItemList.add(rowItem);
        }
        if(stationeryRequestDetail.get("status").toString().equals("Pending Acceptance")){
            acceptButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);
            acceptButton.setOnClickListener(this);
            rejectButton.setOnClickListener(this);
        } else {
            acceptButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_staff_department_request_detail, rowItemList);
        listView.setAdapter(rowAdapter);
    }

    public void acceptDepartmentRequest(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "acceptDepartmentRequest");
            body.put("departmentRequestId", departmentRequestId);
            request.put("url", "AcceptDepartmentRequest");
            request.put("requestBody", body);
            request.put("callbackFragment", "staffDepartmentRequestDetailFragment");
            request.put("callbackMethod", "acceptDepartmentRequestCallback");
            iStaffDepartmentRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void acceptDepartmentRequestCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Accepted department request", Toast.LENGTH_SHORT).show();
            getStaffDepartmentRequestDetail();
        }
    }

    public void rejectDepartmentRequest(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "rejectDepartmentRequest");
            body.put("departmentRequestId", departmentRequestId);
            request.put("url", "RejectDepartmentRequest");
            request.put("requestBody", body);
            request.put("callbackFragment", "staffDepartmentRequestDetailFragment");
            request.put("callbackMethod", "rejectDepartmentRequestCallback");
            iStaffDepartmentRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void rejectDepartmentRequestCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Rejected department request", Toast.LENGTH_SHORT).show();
            getStaffDepartmentRequestDetail();
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_staff_department_request_detail_accept:
                acceptDepartmentRequest();
                break;
            case R.id.button_staff_department_request_detail_reject:
                rejectDepartmentRequest();
                break;
        }
    }
    public interface IStaffDepartmentRequestDetailFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
    }
    public class RowItem{
        int id;
        String description;
        String quantityRequested;
        String quantityRetrieved;
        String quantityDisbursed;
        public RowItem(int id, String description, String quantityRequested, String quantityRetrieved, String quantityDisbursed){
            this.id = id;
            this.description = description;
            this.quantityRequested = quantityRequested;
            this.quantityRetrieved = quantityRetrieved;
            this.quantityDisbursed = quantityDisbursed;
        }
    }

    public class RowAdapter extends ArrayAdapter {
        Context context;
        // Disable listView recycling
        @Override
        public int getViewTypeCount(){
            return getCount();
        }
        @Override
        public int getItemViewType(int position){
            return position;
        }
        public RowAdapter(Context context, int resourceId, List<RowItem> items){
            super(context, resourceId, items);
            this.context = context;
        }
        @Override
        public View getView(int position, View view, ViewGroup parent){
            RowItemView row = null;
            RowItem rowItem = (RowItem) getItem(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if(view == null){
                view = inflater.inflate(R.layout.rowitem_staff_department_request_detail, null);
                row = new RowItemView();
                row.descriptionView = view.findViewById(R.id.textview_staff_department_request_detail_description);
                row.quantityRequestedView = view.findViewById(R.id.textview_staff_department_request_detail_quantity_requested);
                row.quantityRetrievedView = view.findViewById(R.id.textview_staff_department_request_detail_quantity_retrieved);
                row.quantityDisbursedView = view.findViewById(R.id.textview_staff_department_request_detail_quantity_disbursed);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.descriptionView.setText(rowItem.description);
            row.quantityRequestedView.setText(rowItem.quantityRequested);
            row.quantityRetrievedView.setText(rowItem.quantityRetrieved);
            row.quantityDisbursedView.setText(rowItem.quantityDisbursed);
            return view;
        }
    }

    public class RowItemView{
        TextView descriptionView;
        TextView quantityRequestedView;
        TextView quantityRetrievedView;
        TextView quantityDisbursedView;
    }
}
