package sg.edu.nus.team7adproject.Department;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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

public class StaffStationeryRequestsFragment extends Fragment {
    IStaffStationeryRequestsFragment iStaffStationeryRequestsFragment;
    public StaffStationeryRequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_stationery_requests, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStaffStationeryRequestsFragment = (StaffStationeryRequestsFragment.IStaffStationeryRequestsFragment) context;
        iStaffStationeryRequestsFragment.setFragment("staffStationeryRequestsFragment", this);
        getStationeryRequests();
    }
    public void getStationeryRequests(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStationeryRequests");
            request.put("url", "StaffStationeryRequests");
            request.put("requestBody", body);
            request.put("callbackFragment", "staffStationeryRequestsFragment");
            request.put("callbackMethod", "getStationeryRequestsCallback");
            iStaffStationeryRequestsFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStationeryRequestsCallback(String response){
        Log.d("weizheng", response);
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_staff_stationery_requests);
        try {
            JSONArray stationeryRequests = new JSONArray(response);
            for(int i = 0; i < stationeryRequests.length(); i++){
                JSONObject stationeryRequest = stationeryRequests.getJSONObject(i);
                RowItem rowItem = new RowItem(
                        stationeryRequest.getString("id"),
                        stationeryRequest.getString("date"),
                        stationeryRequest.getString("status"));
                rowItemList.add(rowItem);
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_staff_stationery_requests, rowItemList);
        listView.setAdapter(rowAdapter);
    }
    public interface IStaffStationeryRequestsFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
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

    public class RowAdapter extends ArrayAdapter{
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
                view = inflater.inflate(R.layout.rowitem_staff_stationery_request, null);
                row = new RowItemView();
                row.idView = view.findViewById(R.id.textview_staff_stationery_request_id);
                row.dateView = view.findViewById(R.id.textview_staff_stationery_request_date);
                row.statusView= view.findViewById(R.id.textview_staff_stationery_request_status);
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
