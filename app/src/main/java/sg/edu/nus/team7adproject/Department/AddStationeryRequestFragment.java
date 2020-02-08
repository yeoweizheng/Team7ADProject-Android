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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class AddStationeryRequestFragment extends Fragment implements View.OnClickListener{

    AddStationeryRequestFragment.IAddStationeryRequestFragment iAddStationeryRequestFragment;
    HashMap<Integer, EditText> quantityRequestedEditTexts;
    public AddStationeryRequestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        quantityRequestedEditTexts = new HashMap<Integer, EditText>();
        getStationeries();
        return inflater.inflate(R.layout.fragment_add_stationery_request, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        Button submitButton = view.findViewById(R.id.button_add_stationery_request_submit);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iAddStationeryRequestFragment = (AddStationeryRequestFragment.IAddStationeryRequestFragment) context;
        iAddStationeryRequestFragment.setFragment("addStationeryRequestFragment", this);
    }
    public void getStationeries(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStationeries");
            request.put("url", "GetStationeries");
            request.put("requestBody", body);
            request.put("callbackFragment", "addStationeryRequestFragment");
            request.put("callbackMethod", "getStationeriesCallback");
            iAddStationeryRequestFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStationeriesCallback(String response) throws JSONException{
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_add_stationery_request);
        JSONArray stationeries = new JSONArray(response);
        for(int i = 0; i < stationeries.length(); i++){
            JSONObject stationery =  stationeries.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationery.getInt("id"),
                    stationery.getString("category"),
                    stationery.getString("description"),
                    stationery.getString("unitOfMeasure"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_add_stationery_request, rowItemList);
        listView.setAdapter(rowAdapter);
    }
    public void submitStationeryRequest(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        JSONArray stationeryRequests = new JSONArray();
        try {
            for (int i : quantityRequestedEditTexts.keySet()) {
                String quantityRequestedStr = quantityRequestedEditTexts.get(i).getText().toString();
                if (quantityRequestedStr.isEmpty()) continue;
                int quantityRequested = Integer.parseInt(quantityRequestedStr);
                stationeryRequests.put(new JSONObject("{"
                        +"\"stationeryId\":" + i + ","
                        +"\"quantity\":" + quantityRequested
                        + "}"));
            }
            body.put("action", "addStationeryRequest");
            body.put("stationeryRequests", stationeryRequests);
            request.put("url", "AddStationeryRequest");
            request.put("requestBody", body);
            request.put("callbackFragment", "addStationeryRequestFragment");
            request.put("callbackMethod", "submitStationeryRequestCallback");
            iAddStationeryRequestFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void submitStationeryRequestCallback(String response) throws JSONException{
        iAddStationeryRequestFragment.onBackPressed();
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_add_stationery_request_submit:
                submitStationeryRequest();
                break;
        }
    }
    public interface IAddStationeryRequestFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name);
        void onBackPressed();
    }
    public class RowItem{
        int id;
        String category;
        String description;
        String unitOfMeasure;
        public RowItem(int id, String category, String description, String unitOfMeasure){
            this.id = id;
            this.category = category;
            this.description = description;
            this.unitOfMeasure = unitOfMeasure;
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
                view = inflater.inflate(R.layout.rowitem_add_stationery_request, null);
                row = new RowItemView();
                row.categoryView = view.findViewById(R.id.textview_add_stationery_request_category);
                row.descriptionView = view.findViewById(R.id.textview_add_stationery_request_description);
                row.unitOfMeasureView= view.findViewById(R.id.textview_add_stationery_request_unit_of_measure);
                row.quantityRequestedView = view.findViewById(R.id.edittext_add_stationery_request_quantity_requested);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.categoryView.setText(rowItem.category);
            row.descriptionView.setText(rowItem.description);
            row.unitOfMeasureView.setText(rowItem.unitOfMeasure);
            quantityRequestedEditTexts.put(rowItem.id, row.quantityRequestedView);
            return view;
        }
    }

    public class RowItemView{
        TextView categoryView;
        TextView descriptionView;
        TextView unitOfMeasureView;
        EditText quantityRequestedView;
    }
}
