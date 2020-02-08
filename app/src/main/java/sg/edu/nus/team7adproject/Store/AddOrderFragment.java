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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class AddOrderFragment extends Fragment implements View.OnClickListener{

    AddOrderFragment.IAddOrderFragment iAddOrderFragment;
    HashMap<Integer, EditText> quantityOrderedEditTexts;
    public AddOrderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        quantityOrderedEditTexts = new HashMap<Integer, EditText>();
        getStationeries();
        return inflater.inflate(R.layout.fragment_add_order, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        Button submitButton = view.findViewById(R.id.button_add_order_submit);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iAddOrderFragment = (AddOrderFragment.IAddOrderFragment) context;
        iAddOrderFragment.setFragment("addOrderFragment", this);
    }
    public void getStationeries(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStationeries");
            request.put("url", "GetStationeries");
            request.put("requestBody", body);
            request.put("callbackFragment", "addOrderFragment");
            request.put("callbackMethod", "getStationeriesCallback");
            iAddOrderFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStationeriesCallback(String response) throws JSONException{
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_add_order);
        JSONArray stationeries = new JSONArray(response);
        for(int i = 0; i < stationeries.length(); i++){
            JSONObject stationery =  stationeries.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationery.getInt("id"),
                    stationery.getString("description"),
                    stationery.getString("unitOfMeasure"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_add_order, rowItemList);
        listView.setAdapter(rowAdapter);
    }
    public void submitOrder(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        JSONArray orders = new JSONArray();
        try {
            for (int i : quantityOrderedEditTexts.keySet()) {
                String quantityOrderedStr = quantityOrderedEditTexts.get(i).getText().toString();
                if (quantityOrderedStr.isEmpty()) continue;
                int quantityOrdered = Integer.parseInt(quantityOrderedStr);
                orders.put(new JSONObject("{"
                        +"\"stationeryId\":" + i + ","
                        +"\"quantity\":" + quantityOrdered
                        + "}"));
            }
            body.put("action", "addOrder");
            body.put("orders", orders);
            request.put("url", "AddOrder");
            request.put("requestBody", body);
            request.put("callbackFragment", "addOrderFragment");
            request.put("callbackMethod", "submitOrderCallback");
            iAddOrderFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void submitOrderCallback(String response) throws JSONException{
        iAddOrderFragment.onBackPressed();
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_add_order_submit:
                submitOrder();
                break;
        }
    }
    public interface IAddOrderFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name);
        void onBackPressed();
    }
    public class RowItem{
        int id;
        String description;
        String unitOfMeasure;
        String[] suppliers;
        public RowItem(int id, String description, String unitOfMeasure){
            this.id = id;
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
                view = inflater.inflate(R.layout.rowitem_add_order, null);
                row = new RowItemView();
                row.descriptionView = view.findViewById(R.id.textview_add_order_description);
                row.unitOfMeasureView= view.findViewById(R.id.textview_add_order_unit_of_measure);
                row.quantityOrderedView = view.findViewById(R.id.edittext_add_order_quantity);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.descriptionView.setText(rowItem.description);
            row.unitOfMeasureView.setText(rowItem.unitOfMeasure);
            return view;
        }
    }

    public class RowItemView{
        TextView descriptionView;
        TextView unitOfMeasureView;
        EditText quantityOrderedView;
        Spinner supplierView;
    }
}
