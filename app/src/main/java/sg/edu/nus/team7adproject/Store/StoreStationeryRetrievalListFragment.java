package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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

public class StoreStationeryRetrievalListFragment extends Fragment implements
    View.OnClickListener, AdapterView.OnItemClickListener {
    IStoreStationeryRetrievalListFragment iStoreStationeryRetrievalListFragment;
    HashMap<Integer, EditText> quantityRetrievedEditTexts;
    HashMap<Integer, Integer> quantityRequested;
    HashMap<Integer, Integer> suggestedQuantities;
    HashMap<Integer, Integer> quantityRetrievedInput;
    public StoreStationeryRetrievalListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        quantityRetrievedEditTexts = new HashMap<Integer, EditText>();
        quantityRequested = new HashMap<Integer, Integer>();
        suggestedQuantities = new HashMap<Integer, Integer>();
        quantityRetrievedInput = new HashMap<Integer, Integer>();
        getRetrievalList();
        getStationeryQuantities();
        return inflater.inflate(R.layout.fragment_store_stationery_retrieval_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Button updateButton = view.findViewById(R.id.button_store_stationery_retrieval_list_update);
        updateButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStoreStationeryRetrievalListFragment = (IStoreStationeryRetrievalListFragment) context;
        iStoreStationeryRetrievalListFragment.setFragment("storeStationeryRetrievalListFragment", this);
    }
    public void getRetrievalList(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getRetrievalList");
            request.put("url", "StationeryRetrievalList");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeStationeryRetrievalListFragment");
            request.put("callbackMethod", "getRetrievalListCallback");
            iStoreStationeryRetrievalListFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getRetrievalListCallback(String response) throws JSONException{
        ArrayList<RowItem1> rowItemList = new ArrayList<RowItem1>();
        ListView listView = getActivity().findViewById(R.id.listview1_store_stationery_retrieval_list);
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
        RowAdapter1 rowAdapter = new RowAdapter1(getActivity(), R.layout.fragment_store_stationery_retrieval_list, rowItemList);
        listView.setAdapter(rowAdapter);
        listView.setOnItemClickListener(this);
    }
    public void getStationeryQuantities(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getRetrievalList");
            request.put("url", "StationeryRetrievalStationeryQuantities");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeStationeryRetrievalListFragment");
            request.put("callbackMethod", "getStationeryQuantitiesCallback");
            iStoreStationeryRetrievalListFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    ListView listView2;
    ArrayList<RowItem2> rowItem2List;
    RowAdapter2 rowAdapter2;
    public void getStationeryQuantitiesCallback(String response) throws JSONException{
        rowItem2List = new ArrayList<RowItem2>();
        listView2 = getActivity().findViewById(R.id.listview2_store_stationery_retrieval_list);
        JSONObject responseObj  = new JSONObject(response);
        JSONArray stationeryQuantities = responseObj.getJSONArray("stationeryQuantities");
        for(int i = 0; i < stationeryQuantities.length(); i++){
            JSONObject stationeryQuantity = stationeryQuantities.getJSONObject(i);
            RowItem2 rowItem = new RowItem2(
                    stationeryQuantity.getInt("id"),
                    stationeryQuantity.getString("itemNumber"),
                    stationeryQuantity.getString("description"),
                    stationeryQuantity.getString("quantityRequested"));
                    quantityRequested.put(stationeryQuantity.getInt("id"), stationeryQuantity.getInt("quantityRequested"));
            rowItem2List.add(rowItem);
        }
        //rowAdapter2 = new RowAdapter2(getActivity(), R.layout.fragment_store_stationery_retrieval_list, rowItem2List);
        //listView2.setAdapter(rowAdapter);
        getStockLevel();
    }

    private void populateListView2(){
        rowAdapter2 = new RowAdapter2(getActivity(), R.layout.fragment_store_stationery_retrieval_list, rowItem2List);
        listView2.setAdapter(rowAdapter2);
    }

    public void getStockLevel(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStockLevel");
            request.put("url", "StockLevel");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeStationeryRetrievalListFragment");
            request.put("callbackMethod", "getStockLevelCallback");
            iStoreStationeryRetrievalListFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void getStockLevelCallback(String response) throws JSONException{
        JSONObject responseObj  = new JSONObject(response);
        JSONArray stationeryQuantities = responseObj.getJSONArray("stationeryQuantities");
        for(int i = 0; i < stationeryQuantities.length(); i++){
            JSONObject stationeryQuantity = stationeryQuantities.getJSONObject(i);
            if(!quantityRequested.containsKey(stationeryQuantity.getInt("stationeryId"))) continue;
            int quantityInStock = stationeryQuantity.getInt("quantity");
            int suggestedQty = 0;
            if(quantityInStock < quantityRequested.get(stationeryQuantity.getInt("stationeryId"))){
                suggestedQty = quantityInStock;
            } else {
                suggestedQty = quantityRequested.get(stationeryQuantity.getInt("stationeryId"));
            }
            suggestedQuantities.put(stationeryQuantity.getInt("stationeryId"), suggestedQty);
            quantityRetrievedInput.put(stationeryQuantity.getInt("stationeryId"), suggestedQty);
        }
        populateListView2();
    }

    public void updateRetrieval(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        JSONArray stationeryQuantities = new JSONArray();
        try {
            for (int i : quantityRetrievedInput.keySet()) {
                int quantityRetrieved = quantityRetrievedInput.get(i);
                if(quantityRetrieved > quantityRequested.get(i)){
                    Toast.makeText(getActivity().getApplicationContext(), "Quantity retrieved cannot be more than quantity requested", Toast.LENGTH_SHORT).show();
                    return;
                }
                stationeryQuantities.put(new JSONObject("{"
                        +"\"stationeryId\":" + i + ","
                        +"\"quantityRetrieved\":" + quantityRetrieved
                        + "}"));
            }
            body.put("action", "updateRetrieval");
            body.put("stationeryQuantities", stationeryQuantities);
            request.put("url", "UpdateRetrieval");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeStationeryRetrievalListFragment");
            request.put("callbackMethod", "updateRetrievalCallback");
            iStoreStationeryRetrievalListFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void updateRetrievalCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            iStoreStationeryRetrievalListFragment.gotoFragment("departmentRequests");
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Insufficient stock", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_store_stationery_retrieval_list_update:
                updateRetrieval();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id){
        RowItem1 rowItem = (RowItem1) adapter.getItemAtPosition(pos);
        iStoreStationeryRetrievalListFragment.gotoFragment("departmentRequestDetailFromRetrieval", Integer.parseInt(rowItem.id));
    }
    public interface IStoreStationeryRetrievalListFragment{
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
                view = inflater.inflate(R.layout.rowitem1_store_stationery_retrieval_list, null);
                row = new RowItem1View();
                row.idView = view.findViewById(R.id.textview_store_stationery_retrieval_list_id);
                row.dateView = view.findViewById(R.id.textview_store_stationery_retrieval_list_date);
                row.departmentView = view.findViewById(R.id.textview_store_stationery_retrieval_list_department);
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
        public RowItem2(int id, String itemNumber, String description, String quantityRequested){
            this.id = id;
            this.itemNumber = itemNumber;
            this.description = description;
            this.quantityRequested = quantityRequested;
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
            final RowItem2 rowItem = (RowItem2) getItem(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if(view == null){
                view = inflater.inflate(R.layout.rowitem2_store_stationery_retrieval_list, null);
                row = new RowItem2View();
                row.itemNumberView = view.findViewById(R.id.textview_store_stationery_retrieval_list_item_number);
                row.descriptionView = view.findViewById(R.id.textview_store_stationery_retrieval_list_description);
                row.quantityRequestedView = view.findViewById(R.id.textview_store_stationery_retrieval_list_quantity_requested);
                row.quantityRequestedEditText = view.findViewById(R.id.edittext_store_stationery_retrieval_quantity_retrieved);
                view.setTag(row);
            } else {
                row = (RowItem2View) view.getTag();
            }
            row.itemNumberView.setText(rowItem.itemNumber);
            row.descriptionView.setText(rowItem.description);
            row.quantityRequestedView.setText(rowItem.quantityRequested);
            if(!quantityRetrievedEditTexts.containsKey(rowItem.id)) {
                row.quantityRequestedEditText.setText(suggestedQuantities.get(rowItem.id) + "");
                row.quantityRequestedEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.toString().isEmpty()){
                            quantityRetrievedInput.put(rowItem.id, 0);
                        } else {
                            quantityRetrievedInput.put(rowItem.id, Integer.parseInt(s.toString()));
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) { }
                });
            }
            quantityRetrievedEditTexts.put(rowItem.id, row.quantityRequestedEditText);
            return view;
        }
    }

    public class RowItem2View{
        TextView itemNumberView;
        TextView descriptionView;
        TextView quantityRequestedView;
        EditText quantityRequestedEditText;
    }
}
