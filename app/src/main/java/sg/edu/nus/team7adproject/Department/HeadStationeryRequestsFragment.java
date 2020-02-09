package sg.edu.nus.team7adproject.Department;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.R;


public class HeadStationeryRequestsFragment extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener {
    IHeadStationeryRequestsFragment iHeadStationeryRequestsFragment;
    public HeadStationeryRequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getStationeryRequests();
        return inflater.inflate(R.layout.fragment_head_stationery_requests, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iHeadStationeryRequestsFragment = (IHeadStationeryRequestsFragment) context;
        iHeadStationeryRequestsFragment.setFragment("headStationeryRequestsFragment", this);
    }
    public void getStationeryRequests(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStationeryRequests");
            request.put("url", "HeadStationeryRequests");
            request.put("requestBody", body);
            request.put("callbackFragment", "headStationeryRequestsFragment");
            request.put("callbackMethod", "getStationeryRequestsCallback");
            iHeadStationeryRequestsFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStationeryRequestsCallback(String response) throws JSONException{
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_head_stationery_requests);
        JSONArray stationeryRequests = new JSONArray(response);
        for(int i = 0; i < stationeryRequests.length(); i++){
            JSONObject stationeryRequest = stationeryRequests.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationeryRequest.getString("id"),
                    stationeryRequest.getString("date"),
                    stationeryRequest.getString("status"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_head_stationery_requests, rowItemList);
        listView.setAdapter(rowAdapter);
        listView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id){
        RowItem rowItem = (RowItem) adapter.getItemAtPosition(pos);
        iHeadStationeryRequestsFragment.gotoFragment("stationeryRequestDetail", Integer.parseInt(rowItem.id));
    }
    @Override
    public void onClick(View view){
    }
    public interface IHeadStationeryRequestsFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

    public class RowItem{
        String id;
        String date;
        String status;
        public RowItem(String id, String date, String status){
            this.id = id;
            this.date = date;
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
                view = inflater.inflate(R.layout.rowitem_head_stationery_requests, null);
                row = new RowItemView();
                row.idView = view.findViewById(R.id.textview_head_stationery_request_id);
                row.dateView = view.findViewById(R.id.textview_head_stationery_request_date);
                row.statusView= view.findViewById(R.id.textview_head_stationery_request_status);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.idView.setText(rowItem.id);
            row.dateView.setText(rowItem.date);
            row.statusView.setText(rowItem.status);
            return view;
        }
    }

    public class RowItemView{
        TextView idView;
        TextView dateView;
        TextView statusView;
    }
}

