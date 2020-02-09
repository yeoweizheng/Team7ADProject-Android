package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sg.edu.nus.team7adproject.R;
public class OrderDetailFragment extends Fragment implements View.OnClickListener{

    OrderDetailFragment.IOrderDetailFragment iOrderDetailFragment;
    Button placeOrderButton;
    Button updateButton;
    int orderId;
    String orderStatus;
    LinkedHashMap<Integer, String> quantityReceivedInput;
    HashMap<Integer, EditText> quantityReceivedEditTexts;

    public OrderDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        quantityReceivedInput = new LinkedHashMap<Integer, String>();
        quantityReceivedEditTexts = new HashMap<Integer, EditText>();
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        orderId = OrderDetailFragmentArgs.fromBundle(getArguments()).getOrderId();
        placeOrderButton = view.findViewById(R.id.button_order_detail_place_order);
        updateButton = view.findViewById(R.id.button_order_detail_update);
        placeOrderButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        getOrderDetail();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iOrderDetailFragment = (OrderDetailFragment.IOrderDetailFragment) context;
        iOrderDetailFragment.setFragment("orderDetailFragment", this);
    }
    public void getOrderDetail(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getOrderDetail");
            body.put("orderId", orderId);
            request.put("url", "OrderDetail");
            request.put("requestBody", body);
            request.put("callbackFragment", "orderDetailFragment");
            request.put("callbackMethod", "getOrderDetailCallback");
            iOrderDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getOrderDetailCallback(String response) throws JSONException{
        JSONObject orderDetail = new JSONObject(response);
        JSONArray stationeryQuantities = orderDetail.getJSONArray("stationeryQuantities");
        TextView idView = getActivity().findViewById(R.id.textview_order_detail_id);
        TextView statusView = getActivity().findViewById(R.id.textview_order_detail_status);
        idView.setText(orderDetail.get("id").toString());
        statusView.setText(orderDetail.get("status").toString());
        orderStatus = orderDetail.get("status").toString();
        ListView listView = getActivity().findViewById(R.id.listview_order_detail);
        ArrayList<RowItem> rowItemList = new ArrayList<>();
        for(int i = 0; i < stationeryQuantities.length(); i++){
            JSONObject stationeryQuantity = stationeryQuantities.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationeryQuantity.getInt("stationeryId"),
                    stationeryQuantity.getString("description"),
                    stationeryQuantity.getString("quantityOrdered"),
                    stationeryQuantity.getString("quantityReceived"));
            rowItemList.add(rowItem);
            quantityReceivedInput.put(rowItem.id, rowItem.quantityReceived);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_order_detail, rowItemList);
        listView.setAdapter(rowAdapter);
        switch(orderStatus){
            case "Created":
                updateButton.setVisibility(View.GONE);
                placeOrderButton.setVisibility(View.VISIBLE);
                break;
            case "Ordered":
                updateButton.setVisibility(View.VISIBLE);
                placeOrderButton.setVisibility(View.GONE);
                break;
            case "Partially Received":
                updateButton.setVisibility(View.VISIBLE);
                placeOrderButton.setVisibility(View.GONE);
                break;
            default:
                updateButton.setVisibility(View.GONE);
                placeOrderButton.setVisibility(View.GONE);
                break;
        }
    }
    public void placeOrder() {
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "placeOrder");
            body.put("orderId", orderId);
            request.put("url", "PlaceOrder");
            request.put("requestBody", body);
            request.put("callbackFragment", "orderDetailFragment");
            request.put("callbackMethod", "placeOrderCallback");
            iOrderDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void placeOrderCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Order placed", Toast.LENGTH_SHORT).show();
            iOrderDetailFragment.onBackPressed();
        }
    }

    public void updateOrder() {
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        JSONArray quantitiesReceived = new JSONArray();
        for(Map.Entry<Integer, String> entry : quantityReceivedInput.entrySet()){
            quantitiesReceived.put(entry.getValue());
        }
        try {
            body.put("action", "updateOrder");
            body.put("orderId", orderId);
            body.put("quantitiesReceived", quantitiesReceived);
            request.put("url", "UpdateOrder");
            request.put("requestBody", body);
            request.put("callbackFragment", "orderDetailFragment");
            request.put("callbackMethod", "updateOrderCallback");
            iOrderDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void updateOrderCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Order updated", Toast.LENGTH_SHORT).show();
            iOrderDetailFragment.onBackPressed();
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_order_detail_place_order:
                placeOrder();
                break;
            case R.id.button_order_detail_update:
                updateOrder();
                break;
        }
    }
    public interface IOrderDetailFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void onBackPressed();
    }
    public class RowItem{
        int id;
        String description;
        String quantityOrdered;
        String quantityReceived;
        public RowItem(int id, String description, String quantityOrdered, String quantityReceived){
            this.id = id;
            this.description = description;
            this.quantityOrdered = quantityOrdered;
            this.quantityReceived = quantityReceived;
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
            final RowItem rowItem = (RowItem) getItem(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if(view == null){
                view = inflater.inflate(R.layout.rowitem_order_detail, null);
                row = new RowItemView();
                row.descriptionView = view.findViewById(R.id.textview_order_detail_description);
                row.quantityOrderedView = view.findViewById(R.id.textview_order_detail_quantity_ordered);
                row.quantityReceivedView = view.findViewById(R.id.textview_order_detail_quantity_received);
                row.quantityReceivedEditText = view.findViewById(R.id.edittext_order_detail_quantity_received);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.descriptionView.setText(rowItem.description);
            row.quantityOrderedView.setText(rowItem.quantityOrdered);
            row.quantityReceivedView.setText(rowItem.quantityReceived);
            if(!quantityReceivedEditTexts.containsKey(rowItem.id)){
                row.quantityReceivedEditText.setText(quantityReceivedInput.get(rowItem.id));
                row.quantityReceivedEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.toString().isEmpty()){
                            quantityReceivedInput.put(rowItem.id, "0");
                        } else {
                            quantityReceivedInput.put(rowItem.id, s.toString());
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) { }
                });
            }
            quantityReceivedEditTexts.put(rowItem.id, row.quantityReceivedEditText);
            if(orderStatus.equals("Ordered") || orderStatus.equals("Partially Received")){
                row.quantityReceivedEditText.setVisibility(View.VISIBLE);
                row.quantityReceivedView.setVisibility(View.GONE);
            } else {
                row.quantityReceivedEditText.setVisibility(View.GONE);
                row.quantityReceivedView.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }

    public class RowItemView{
        TextView descriptionView;
        TextView quantityOrderedView;
        TextView quantityReceivedView;
        EditText quantityReceivedEditText;
    }
}
