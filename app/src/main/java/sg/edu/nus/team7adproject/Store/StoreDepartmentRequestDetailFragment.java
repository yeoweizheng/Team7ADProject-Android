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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.Department.StationeryRequestDetailFragmentArgs;
import sg.edu.nus.team7adproject.R;

public class StoreDepartmentRequestDetailFragment extends Fragment {

    IStoreDepartmentRequestDetailFragment iStoreDepartmentRequestDetailFragment;
    Button addToRetrievalButton;
    Button addToDisbursementButton;

    public StoreDepartmentRequestDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_department_request_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        int id = StoreDepartmentRequestDetailFragmentArgs.fromBundle(getArguments()).getDepartmentRequestId();
        getStoreDepartmentRequestDetail(id);
        addToRetrievalButton = view.findViewById(R.id.button_store_department_request_detail_add_to_retrieval);
        addToDisbursementButton = view.findViewById(R.id.button_store_department_request_detail_add_to_disbursement);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStoreDepartmentRequestDetailFragment = (IStoreDepartmentRequestDetailFragment) context;
        iStoreDepartmentRequestDetailFragment.setFragment("storeDepartmentRequestDetailFragment", this);
    }
    public void getStoreDepartmentRequestDetail(int id){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStoreDepartmentRequestDetail");
            body.put("departmentRequestId", id);
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
                    stationeryQuantity.getString("unitOfMeasure"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_store_department_request_detail, rowItemList);
        listView.setAdapter(rowAdapter);
        switch(storeDepartmentRequestDetail.get("status").toString()){
            case "Not Retrieved":
                addToRetrievalButton.setVisibility(View.VISIBLE);
                break;
            case "Retrieved":
                addToDisbursementButton.setVisibility(View.VISIBLE);
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
        String unitOfMeasure;
        public RowItem(String description, String quantityRequested, String unitOfMeasure){
            this.description = description;
            this.quantityRequested = quantityRequested;
            this.unitOfMeasure = unitOfMeasure;
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
                row.unitOfMeasureView= view.findViewById(R.id.textview_store_department_request_unit_of_measure);
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
