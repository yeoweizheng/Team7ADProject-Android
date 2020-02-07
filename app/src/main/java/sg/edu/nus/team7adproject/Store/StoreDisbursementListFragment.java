package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

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
import java.util.HashMap;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class StoreDisbursementListFragment extends Fragment implements
        AdapterView.OnItemClickListener {
    IStoreDisbursementListFragment iStoreDisbursementListFragment;
    public StoreDisbursementListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDisbursementList();
        getStationeryQuantities();
        return inflater.inflate(R.layout.fragment_store_disbursement_list, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStoreDisbursementListFragment = (IStoreDisbursementListFragment) context;
        iStoreDisbursementListFragment.setFragment("storeDisbursementListFragment", this);
    }
    public void getDisbursementList(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getDisbursementList");
            request.put("url", "DisbursementList");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeDisbursementListFragment");
            request.put("callbackMethod", "getDisbursementListCallback");
            iStoreDisbursementListFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getDisbursementListCallback(String response) throws JSONException{
        ArrayList<RowItem1> rowItemList = new ArrayList<RowItem1>();
        ListView listView = getActivity().findViewById(R.id.listview1_store_disbursement_list);
        JSONObject responseObj  = new JSONObject(response);
        JSONArray departmentRequests = responseObj.getJSONArray("departmentRequests");
        for(int i = 0; i < departmentRequests.length(); i++){
            JSONObject departmentRequest = departmentRequests.getJSONObject(i);
            RowItem1 rowItem = new RowItem1(
                    departmentRequest.getString("id"),
                    departmentRequest.getString("date"),
                    departmentRequest.getString("department"));
            rowItemList.add(rowItem);
        }
        RowAdapter1 rowAdapter = new RowAdapter1(getActivity(), R.layout.fragment_store_disbursement_list, rowItemList);
        listView.setAdapter(rowAdapter);
        listView.setOnItemClickListener(this);
    }
    public void getStationeryQuantities(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getDisbursementList");
            request.put("url", "DisbursementStationeryQuantities");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeDisbursementListFragment");
            request.put("callbackMethod", "getStationeryQuantitiesCallback");
            iStoreDisbursementListFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStationeryQuantitiesCallback(String response) throws JSONException{
        ArrayList<RowItem2> rowItemList = new ArrayList<RowItem2>();
        ListView listView2 = getActivity().findViewById(R.id.listview2_store_disbursement_list);
        JSONObject responseObj  = new JSONObject(response);
        JSONArray stationeryQuantities = responseObj.getJSONArray("stationeryQuantities");
        for(int i = 0; i < stationeryQuantities.length(); i++){
            JSONObject stationeryQuantity = stationeryQuantities.getJSONObject(i);
            RowItem2 rowItem = new RowItem2(
                    stationeryQuantity.getInt("id"),
                    stationeryQuantity.getString("itemNumber"),
                    stationeryQuantity.getString("description"),
                    stationeryQuantity.getString("quantityRequested"),
                    stationeryQuantity.getString("quantityRetrieved"));
            rowItemList.add(rowItem);
        }
        RowAdapter2 rowAdapter = new RowAdapter2(getActivity(), R.layout.fragment_store_disbursement_list, rowItemList);
        listView2.setAdapter(rowAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id){
        RowItem1 rowItem = (RowItem1) adapter.getItemAtPosition(pos);
        iStoreDisbursementListFragment.gotoFragment("departmentRequestDetailFromDisbursement", Integer.parseInt(rowItem.id));
    }
    public interface IStoreDisbursementListFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

    public class RowItem1{
        String id;
        String date;
        String department;
        public RowItem1(String id, String date, String department){
            this.id = id;
            this.date = date;
            this.department = department;
        }
    }

    public class RowAdapter1 extends ArrayAdapter {
        Context context;
        public RowAdapter1(Context context, int resourceId, List<RowItem1> items){
            super(context, resourceId, items);
            this.context = context;
        }
        @Override
        public View getView(int position, View view, ViewGroup parent){
            RowItem1View row = null;
            RowItem1 rowItem = (RowItem1) getItem(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if(view == null){
                view = inflater.inflate(R.layout.rowitem1_store_disbursement_list, null);
                row = new RowItem1View();
                row.idView = view.findViewById(R.id.textview_store_disbursement_list_id);
                row.dateView = view.findViewById(R.id.textview_store_disbursement_list_date);
                row.departmentView = view.findViewById(R.id.textview_store_disbursement_list_department);
                view.setTag(row);
            } else {
                row = (RowItem1View) view.getTag();
            }
            row.idView.setText(rowItem.id);
            row.dateView.setText(rowItem.date);
            row.departmentView.setText(rowItem.department);
            return view;
        }
    }

    public class RowItem1View{
        TextView idView;
        TextView dateView;
        TextView departmentView;
    }

    public class RowItem2{
        int id;
        String itemNumber;
        String description;
        String quantityRequested;
        String quantityRetrieved;
        public RowItem2(int id, String itemNumber, String description, String quantityRequested, String quantityRetrieved){
            this.id = id;
            this.itemNumber = itemNumber;
            this.description = description;
            this.quantityRequested = quantityRequested;
            this.quantityRetrieved = quantityRetrieved;
        }
    }

    public class RowAdapter2 extends ArrayAdapter {
        Context context;
        public RowAdapter2(Context context, int resourceId, List<RowItem2> items){
            super(context, resourceId, items);
            this.context = context;
        }
        @Override
        public View getView(int position, View view, ViewGroup parent){
            RowItem2View row = null;
            RowItem2 rowItem = (RowItem2) getItem(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if(view == null){
                view = inflater.inflate(R.layout.rowitem2_store_disbursement_list, null);
                row = new RowItem2View();
                row.itemNumberView = view.findViewById(R.id.textview_store_disbursement_list_item_number);
                row.descriptionView = view.findViewById(R.id.textview_store_disbursement_list_description);
                row.quantityRequestedView = view.findViewById(R.id.textview_store_disbursement_list_quantity_requested);
                row.quantityRetrievedView = view.findViewById(R.id.textview_store_disbursement_list_quantity_retrieved);
                view.setTag(row);
            } else {
                row = (RowItem2View) view.getTag();
            }
            row.itemNumberView.setText(rowItem.itemNumber);
            row.descriptionView.setText(rowItem.description);
            row.quantityRequestedView.setText(rowItem.quantityRequested);
            row.quantityRetrievedView.setText(rowItem.quantityRetrieved);
            return view;
        }
    }

    public class RowItem2View{
        TextView itemNumberView;
        TextView descriptionView;
        TextView quantityRequestedView;
        TextView quantityRetrievedView;
    }
}
