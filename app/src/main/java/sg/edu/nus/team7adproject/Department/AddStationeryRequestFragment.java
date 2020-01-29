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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class AddStationeryRequestFragment extends Fragment {

    AddStationeryRequestFragment.IAddStationeryRequestFragment iAddStationeryRequestFragment;
    public AddStationeryRequestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getStationeries();
        return inflater.inflate(R.layout.fragment_add_stationery_request, container, false);
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
        Log.d("weizheng", response);
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_add_stationery_request);
        JSONArray stationeries = new JSONArray(response);
        for(int i = 0; i < stationeries.length(); i++){
            JSONObject stationery =  stationeries.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationery.getString("category"),
                    stationery.getString("description"),
                    stationery.getString("unitOfMeasure"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_add_stationery_request, rowItemList);
        listView.setAdapter(rowAdapter);
    }
    public void addStationeryRequest(){
    }
    public void addStationeryRequestCallback(String response) throws JSONException{
    }
    public interface IAddStationeryRequestFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
    }
    public class RowItem{
        String category;
        String description;
        String unitOfMeasure;
        public RowItem(String category, String description, String unitOfMeasure){
            this.category = category;
            this.description = description;
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
                view = inflater.inflate(R.layout.rowitem_add_stationery_request, null);
                row = new RowItemView();
                row.categoryView = view.findViewById(R.id.textview_add_stationery_request_category);
                row.descriptionView = view.findViewById(R.id.textview_add_stationery_request_description);
                row.unitOfMeasureView= view.findViewById(R.id.textview_add_stationery_request_unit_of_measure);
                row.quantityRequested = view.findViewById(R.id.edittext_add_stationery_request_quantity_requested);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.categoryView.setText(rowItem.category);
            row.descriptionView.setText(rowItem.description);
            row.unitOfMeasureView.setText(rowItem.unitOfMeasure);
            return view;
        }
    }

    public class RowItemView{
        TextView categoryView;
        TextView descriptionView;
        TextView unitOfMeasureView;
        EditText quantityRequested;
    }
}
