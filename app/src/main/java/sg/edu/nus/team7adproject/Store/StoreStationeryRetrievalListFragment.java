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

public class StoreStationeryRetrievalListFragment extends Fragment {
    IStoreStationeryRetrievalListFragment iStoreStationeryRetrievalListFragment;
    public StoreStationeryRetrievalListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getRetrievalList();
        return inflater.inflate(R.layout.fragment_store_stationery_retrieval_list, container, false);
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
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_store_stationery_retrieval_list);
        JSONObject responseObj  = new JSONObject(response);
        JSONArray departmentRequests = responseObj.getJSONArray("departmentRequests");
        for(int i = 0; i < departmentRequests.length(); i++){
            JSONObject departmentRequest = departmentRequests.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    departmentRequest.getString("id"),
                    departmentRequest.getString("date"),
                    departmentRequest.getString("department"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_store_stationery_retrieval_list, rowItemList);
        listView.setAdapter(rowAdapter);
    }
    public interface IStoreStationeryRetrievalListFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

    public class RowItem{
        String id;
        String date;
        String department;
        public RowItem(String id, String date, String department){
            this.id = id;
            this.date = date;
            this.department = department;
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
                view = inflater.inflate(R.layout.rowitem_store_stationery_retrieval_list, null);
                row = new RowItemView();
                row.idView = view.findViewById(R.id.textview_store_stationery_retrieval_list_id);
                row.dateView = view.findViewById(R.id.textview_store_stationery_retrieval_list_date);
                row.departmentView = view.findViewById(R.id.textview_store_stationery_retrieval_list_department);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.idView.setText(rowItem.id);
            row.dateView.setText(rowItem.date);
            row.departmentView.setText(rowItem.department);
            return view;
        }
    }

    public class RowItemView{
        TextView idView;
        TextView dateView;
        TextView departmentView;
    }
}
