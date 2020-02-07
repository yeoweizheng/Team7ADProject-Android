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

import sg.edu.nus.team7adproject.Department.StationeryRequestDetailFragmentArgs;
import sg.edu.nus.team7adproject.R;

public class StoreDepartmentRequestDetailFragment extends Fragment
    implements View.OnClickListener{

    IStoreDepartmentRequestDetailFragment iStoreDepartmentRequestDetailFragment;
    Button addToRetrievalButton;
    Button addToDisbursementButton;
    Button removeFromRetrievalButton;
    Button removeFromDisbursementButton;
    int departmentRequestId;

    public StoreDepartmentRequestDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_department_request_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        this.departmentRequestId = StoreDepartmentRequestDetailFragmentArgs.fromBundle(getArguments()).getDepartmentRequestId();
        getStoreDepartmentRequestDetail();
        addToRetrievalButton = view.findViewById(R.id.button_store_department_request_detail_add_to_retrieval);
        addToDisbursementButton = view.findViewById(R.id.button_store_department_request_detail_add_to_disbursement);
        removeFromRetrievalButton = view.findViewById(R.id.button_store_department_request_detail_remove_from_retrieval);
        removeFromDisbursementButton = view.findViewById(R.id.button_store_department_request_detail_remove_from_disbursement);
        addToRetrievalButton.setOnClickListener(this);
        addToDisbursementButton.setOnClickListener(this);
        removeFromRetrievalButton.setOnClickListener(this);
        removeFromDisbursementButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStoreDepartmentRequestDetailFragment = (IStoreDepartmentRequestDetailFragment) context;
        iStoreDepartmentRequestDetailFragment.setFragment("storeDepartmentRequestDetailFragment", this);
    }
    public void getStoreDepartmentRequestDetail(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStoreDepartmentRequestDetail");
            body.put("departmentRequestId", departmentRequestId);
            request.put("url", "DepartmentRequestDetail");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeDepartmentRequestDetailFragment");
            request.put("callbackMethod", "getStoreDepartmentRequestDetailCallback");
            iStoreDepartmentRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStoreDepartmentRequestDetailCallback(String response) throws JSONException{
        JSONObject storeDepartmentRequestDetail = new JSONObject(response);
        JSONArray stationeryQuantities = storeDepartmentRequestDetail.getJSONArray("stationeryQuantities");
        TextView idView = getActivity().findViewById(R.id.textview_store_department_request_detail_id);
        TextView dateView = getActivity().findViewById(R.id.textview_store_department_request_detail_date);
        TextView remarksView = getActivity().findViewById(R.id.textview_store_department_request_detail_remarks);
        TextView statusView = getActivity().findViewById(R.id.textview_store_department_request_detail_status);
        idView.setText(storeDepartmentRequestDetail.get("id").toString());
        dateView.setText(storeDepartmentRequestDetail.get("date").toString());
        remarksView.setText(storeDepartmentRequestDetail.get("remarks").toString());
        statusView.setText(storeDepartmentRequestDetail.get("status").toString());
        ListView listView = getActivity().findViewById(R.id.listview_store_department_request_detail);
        ArrayList<RowItem> rowItemList = new ArrayList<>();
        for(int i = 0; i < stationeryQuantities.length(); i++){
            JSONObject stationeryQuantity = stationeryQuantities.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationeryQuantity.getString("description"),
                    stationeryQuantity.getString("quantityRequested"),
                    stationeryQuantity.getString("quantityRetrieved"),
                    stationeryQuantity.getString("quantityDisbursed"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_store_department_request_detail, rowItemList);
        listView.setAdapter(rowAdapter);
        addToRetrievalButton.setVisibility(View.GONE);
        addToDisbursementButton.setVisibility(View.GONE);
        removeFromRetrievalButton.setVisibility(View.GONE);
        removeFromDisbursementButton.setVisibility(View.GONE);
        switch(storeDepartmentRequestDetail.get("status").toString()){
            case "Not Retrieved":
                addToRetrievalButton.setVisibility(View.VISIBLE);
                break;
            case "Added to Retrieval":
                removeFromRetrievalButton.setVisibility(View.VISIBLE);
                break;
            case "Retrieved":
                addToDisbursementButton.setVisibility(View.VISIBLE);
                break;
            case "Added to Disbursement":
                removeFromDisbursementButton.setVisibility(View.VISIBLE);
                break;
        }
    }
    public void addToRetrieval(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "addToRetrieval");
            body.put("departmentRequestId", departmentRequestId);
            request.put("url", "AddToRetrieval");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeDepartmentRequestDetailFragment");
            request.put("callbackMethod", "addToRetrievalCallback");
            iStoreDepartmentRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void addToRetrievalCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Added to retrieval", Toast.LENGTH_SHORT).show();
            getStoreDepartmentRequestDetail();
        }
    }
    public void removeFromRetrieval(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "removeFromRetrieval");
            body.put("departmentRequestId", departmentRequestId);
            request.put("url", "RemoveFromRetrieval");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeDepartmentRequestDetailFragment");
            request.put("callbackMethod", "removeFromRetrievalCallback");
            iStoreDepartmentRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void removeFromRetrievalCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Removed from retrieval", Toast.LENGTH_SHORT).show();
            getStoreDepartmentRequestDetail();
        }
    }
    public void removeFromDisbursement(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "removeFromDisbursement");
            body.put("departmentRequestId", departmentRequestId);
            request.put("url", "RemoveFromDisbursement");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeDepartmentRequestDetailFragment");
            request.put("callbackMethod", "removeFromDisbursementCallback");
            iStoreDepartmentRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void removeFromDisbursementCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Removed from disbursement", Toast.LENGTH_SHORT).show();
            getStoreDepartmentRequestDetail();
        }
    }
    public void addToDisbursement(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "addToDisbursement");
            body.put("departmentRequestId", departmentRequestId);
            request.put("url", "AddToDisbursement");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeDepartmentRequestDetailFragment");
            request.put("callbackMethod", "addToDisbursementCallback");
            iStoreDepartmentRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void addToDisbursementCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Added to disbursement", Toast.LENGTH_SHORT).show();
            getStoreDepartmentRequestDetail();
        }
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_store_department_request_detail_add_to_retrieval:
                addToRetrieval();
                break;
            case R.id.button_store_department_request_detail_remove_from_retrieval:
                removeFromRetrieval();
                break;
            case R.id.button_store_department_request_detail_add_to_disbursement:
                addToDisbursement();
                break;
            case R.id.button_store_department_request_detail_remove_from_disbursement:
                removeFromDisbursement();
                break;
        }
    }
    public interface IStoreDepartmentRequestDetailFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
    }
    public class RowItem{
        String description;
        String quantityRequested;
        String quantityRetrieved;
        String quantityDisbursed;
        public RowItem(String description, String quantityRequested, String quantityRetrieved, String quantityDisbursed){
            this.description = description;
            this.quantityRequested = quantityRequested;
            this.quantityRetrieved = quantityRetrieved;
            this.quantityDisbursed = quantityDisbursed;
        }
    }

    public class RowAdapter extends ArrayAdapter {
        Context context;
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
                view = inflater.inflate(R.layout.rowitem_store_department_request_detail, null);
                row = new RowItemView();
                row.descriptionView = view.findViewById(R.id.textview_store_department_request_description);
                row.quantityRequestedView = view.findViewById(R.id.textview_store_department_request_quantity_requested);
                row.quantityRetrievedView = view.findViewById(R.id.textview_store_department_request_quantity_retrieved);
                row.quantityDisbursedView = view.findViewById(R.id.textview_store_department_request_quantity_disbursed);
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
