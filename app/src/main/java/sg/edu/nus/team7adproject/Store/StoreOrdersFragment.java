package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class StoreOrdersFragment extends Fragment implements AdapterView.OnItemClickListener {
    IStoreOrdersFragment iStoreOrdersFragment;

    public StoreOrdersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getStoreOrders();
        return inflater.inflate(R.layout.fragment_store_orders, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStoreOrdersFragment = (IStoreOrdersFragment) context;
        iStoreOrdersFragment.setFragment("storeOrdersFragment", this);
    }
    public void getStoreOrders(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStoreOrders");
            request.put("url", "Orders");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeOrdersFragment");
            request.put("callbackMethod", "getStoreOrdersCallback");
            iStoreOrdersFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStoreOrdersCallback(String response) throws JSONException{
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_store_orders);
        JSONArray orders = new JSONArray(response);
        for(int i = 0; i < orders.length(); i++){
            JSONObject order = orders.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    order.getInt("orderId"),
                    order.getString("dateCreated"),
                    order.getString("supplier"),
                    order.getString("status"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_store_orders, rowItemList);
        listView.setAdapter(rowAdapter);
        listView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id){
        RowItem rowItem = (RowItem) adapter.getItemAtPosition(pos);
        iStoreOrdersFragment.gotoFragment("stockDetail", rowItem.id);
    }
    public interface IStoreOrdersFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

    public class RowItem{
        int id;
        String dateCreated;
        String supplier;
        String status;
        public RowItem(int id, String dateCreated, String supplier, String status){
            this.id = id;
            this.dateCreated = dateCreated;
            this.supplier = supplier;
            this.status = status;
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
                view = inflater.inflate(R.layout.rowitem_store_orders, null);
                row = new RowItemView();
                row.orderIdView = view.findViewById(R.id.textview_store_orders_order_id);
                row.dateCreatedView = view.findViewById(R.id.textview_store_orders_date_created);
                row.supplierView = view.findViewById(R.id.textview_store_orders_supplier);
                row.statusView = view.findViewById(R.id.textview_store_orders_status);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.orderIdView.setText(rowItem.id + "");
            row.dateCreatedView.setText(rowItem.dateCreated);
            row.supplierView.setText(rowItem.supplier);
            row.statusView.setText(rowItem.status);
            return view;
        }
    }

    public class RowItemView{
        TextView orderIdView;
        TextView dateCreatedView;
        TextView supplierView;
        TextView statusView;
    }
}
