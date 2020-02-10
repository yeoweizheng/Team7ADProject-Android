package sg.edu.nus.team7adproject.Department;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.DepartmentHeadActivity;
import sg.edu.nus.team7adproject.R;

public class StationeryRequestDetailFragment extends Fragment implements View.OnClickListener{

    StationeryRequestDetailFragment.IStationeryRequestDetailFragment iStationeryRequestDetailFragment;
    Button approveButton;
    Button rejectButton;
    EditText remarksEditText;
    int stationeryRequestId;
    boolean isStaffAuthorized;

    public StationeryRequestDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stationery_request_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        stationeryRequestId = StationeryRequestDetailFragmentArgs.fromBundle(getArguments()).getId();
        approveButton = view.findViewById(R.id.button_stationery_request_detail_approve);
        rejectButton = view.findViewById(R.id.button_stationery_request_detail_reject);
        remarksEditText = getActivity().findViewById(R.id.edittext_stationery_request_detail_remarks);
        approveButton.setOnClickListener(this);
        rejectButton.setOnClickListener(this);
        getStaffAuthorization();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStationeryRequestDetailFragment = (StationeryRequestDetailFragment.IStationeryRequestDetailFragment) context;
        iStationeryRequestDetailFragment.setFragment("stationeryRequestDetailFragment", this);
    }
    public void getStaffAuthorization(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStaffAuthorization");
            request.put("url", "GetStaffAuthorization");
            request.put("requestBody", body);
            request.put("callbackFragment", "stationeryRequestDetailFragment");
            request.put("callbackMethod", "getStaffAuthorizationCallback");
            iStationeryRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
        getStationeryRequestDetail();
    }
    public void getStaffAuthorizationCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        isStaffAuthorized = responseObj.getBoolean("isAuthorized");
    }
    public void getStationeryRequestDetail(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStationeryRequestDetail");
            body.put("stationeryRequestId", stationeryRequestId);
            request.put("url", "StationeryRequestDetail");
            request.put("requestBody", body);
            request.put("callbackFragment", "stationeryRequestDetailFragment");
            request.put("callbackMethod", "getStationeryRequestDetailCallback");
            iStationeryRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStationeryRequestDetailCallback(String response) throws JSONException{
        JSONObject stationeryRequestDetail = new JSONObject(response);
        JSONArray stationeryQuantities = stationeryRequestDetail.getJSONArray("stationeryQuantities");
        TextView idView = getActivity().findViewById(R.id.textview_stationery_request_detail_id);
        TextView dateView = getActivity().findViewById(R.id.textview_stationery_request_detail_date);
        TextView remarksView = getActivity().findViewById(R.id.textview_stationery_request_detail_remarks);
        TextView statusView = getActivity().findViewById(R.id.textview_stationery_request_detail_status);
        idView.setText(stationeryRequestDetail.getString("id"));
        dateView.setText(stationeryRequestDetail.getString("date"));
        remarksView.setText(stationeryRequestDetail.getString("remarks"));
        statusView.setText(stationeryRequestDetail.getString("status"));
        ListView listView = getActivity().findViewById(R.id.listview_stationery_request_detail);
        ArrayList<RowItem> rowItemList = new ArrayList<>();
        for(int i = 0; i < stationeryQuantities.length(); i++){
            JSONObject stationeryQuantity = stationeryQuantities.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationeryQuantity.getString("description"),
                    stationeryQuantity.getString("quantityRequested"),
                    stationeryQuantity.getString("unitOfMeasure"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_stationery_request_detail, rowItemList);
        listView.setAdapter(rowAdapter);
        if((iStationeryRequestDetailFragment.getClass().equals(DepartmentHeadActivity.class) || isStaffAuthorized)
            && stationeryRequestDetail.getString("status").equals("Pending")){
            remarksView.setVisibility(View.GONE);
            remarksEditText.setVisibility(View.VISIBLE);
            approveButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);
        } else {
            remarksView.setVisibility(View.VISIBLE);
            remarksEditText.setVisibility(View.GONE);
            approveButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
        }
    }
    public void approveStationeryRequest(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "approveStationeryRequest");
            body.put("stationeryRequestId", stationeryRequestId);
            body.put("remarks", remarksEditText.getText().toString());
            request.put("url", "ApproveStationeryRequest");
            request.put("requestBody", body);
            request.put("callbackFragment", "stationeryRequestDetailFragment");
            request.put("callbackMethod", "approveStationeryRequestCallback");
            iStationeryRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void approveStationeryRequestCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Approved stationery request", Toast.LENGTH_SHORT).show();
            getStationeryRequestDetail();
        }
    }
    public void rejectStationeryRequest(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "rejectStationeryRequest");
            body.put("stationeryRequestId", stationeryRequestId);
            body.put("remarks", remarksEditText.getText().toString());
            request.put("url", "RejectStationeryRequest");
            request.put("requestBody", body);
            request.put("callbackFragment", "stationeryRequestDetailFragment");
            request.put("callbackMethod", "rejectStationeryRequestCallback");
            iStationeryRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void rejectStationeryRequestCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Rejected stationery request", Toast.LENGTH_SHORT).show();
            getStationeryRequestDetail();
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_stationery_request_detail_approve:
                approveStationeryRequest();
                break;
            case R.id.button_stationery_request_detail_reject:
                rejectStationeryRequest();
                break;
        }
    }
    public interface IStationeryRequestDetailFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
    }
    public class RowItem{
        String description;
        String quantityRequested;
        String unitOfMeasure;
        public RowItem(String description, String quantityRequested, String unitOfMeasure){
            this.description = description;
            this.quantityRequested = quantityRequested;
            this.unitOfMeasure = unitOfMeasure;
        }
    }

    public class RowAdapter extends ArrayAdapter{
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
                view = inflater.inflate(R.layout.rowitem_stationery_request_detail, null);
                row = new RowItemView();
                row.descriptionView = view.findViewById(R.id.textview_stationery_request_description);
                row.quantityRequestedView = view.findViewById(R.id.textview_stationery_request_quantity_requested);
                row.unitOfMeasureView= view.findViewById(R.id.textview_stationery_request_unit_of_measure);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.descriptionView.setText(rowItem.description);
            row.quantityRequestedView.setText(rowItem.quantityRequested);
            row.unitOfMeasureView.setText(rowItem.unitOfMeasure);
            return view;
        }
    }

    public class RowItemView{
        TextView descriptionView;
        TextView quantityRequestedView;
        TextView unitOfMeasureView;
    }
}
