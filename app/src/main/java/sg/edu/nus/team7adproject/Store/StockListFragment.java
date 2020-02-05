package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class StockListFragment extends Fragment {
    IStockListFragment iStockListFragment;

    public StockListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getStockList();
        return inflater.inflate(R.layout.fragment_stock_list, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStockListFragment = (IStockListFragment) context;
        iStockListFragment.setFragment("stockListFragment", this);
    }
    public void getStockList(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStockList");
            request.put("url", "GetStationeries");
            request.put("requestBody", body);
            request.put("callbackFragment", "stockListFragment");
            request.put("callbackMethod", "getStockListCallback");
            iStockListFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStockListCallback(String response) throws JSONException{
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_stock_list);
        JSONArray stationerys = new JSONArray(response);
        for(int i = 0; i < stationerys.length(); i++){
            JSONObject stationery = stationerys.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationery.getString("itemNo"),
                    stationery.getString("category"),
                    stationery.getString("description"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_stock_list, rowItemList);
        listView.setAdapter(rowAdapter);
    }
    public interface IStockListFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

    public class RowItem{
        String itemNo;
        String category;
        String description;
        public RowItem(String itemNo, String category, String description){
            this.itemNo = itemNo;
            this.category = category;
            this.description = description;
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
                view = inflater.inflate(R.layout.rowitem_stock_list, null);
                row = new RowItemView();
                row.itemNoView = view.findViewById(R.id.textview_stock_list_item_no);
                row.categoryView = view.findViewById(R.id.textview_stock_list_category);
                row.descriptionView= view.findViewById(R.id.textview_stock_list_description);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.itemNoView.setText(rowItem.itemNo);
            row.categoryView.setText(rowItem.category);
            row.descriptionView.setText(rowItem.description);
            return view;
        }
    }

    public class RowItemView{
        TextView itemNoView;
        TextView categoryView;
        TextView descriptionView;
    }
}
