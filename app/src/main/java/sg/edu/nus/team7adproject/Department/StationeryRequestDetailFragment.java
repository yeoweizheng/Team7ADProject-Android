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

public class StationeryRequestDetailFragment extends Fragment {

    StationeryRequestDetailFragment.IStationeryRequestDetailFragment iStationeryRequestDetailFragment;
    public StationeryRequestDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stationery_request_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        int id = StationeryRequestDetailFragmentArgs.fromBundle(getArguments()).getId();
        getStationeryRequestDetail(id);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStationeryRequestDetailFragment = (StationeryRequestDetailFragment.IStationeryRequestDetailFragment) context;
        iStationeryRequestDetailFragment.setFragment("stationeryRequestDetailFragment", this);
    }
    public void getStationeryRequestDetail(int id){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStationeryRequestDetail");
            body.put("stationeryRequestId", id);
            request.put("url", "StationeryRequestDetail");
            request.put("requestBody", body);
            request.put("callbackFragment", "stationeryRequestDetailFragment");
            request.put("callbackMethod", "getStationeryRequestDetailCallback");
            iStationeryRequestDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStationeryRequestDetailCallback(String response) throws JSONException{
        JSONObject stationeryRequestDetail = new JSONObject(response);
        JSONArray stationeryQuantities = stationeryRequestDetail.getJSONArray("stationeryQuantities");
        TextView idView = getActivity().findViewById(R.id.textview_stationery_request_detail_id);
        TextView dateView = getActivity().findViewById(R.id.textview_stationery_request_detail_date);
        TextView remarksView = getActivity().findViewById(R.id.textview_stationery_request_detail_remarks);
        TextView statusView = getActivity().findViewById(R.id.textview_stationery_request_detail_status);
        idView.setText(stationeryRequestDetail.get("id").toString());
        dateView.setText(stationeryRequestDetail.get("date").toString());
        remarksView.setText(stationeryRequestDetail.get("remarks").toString());
        statusView.setText(stationeryRequestDetail.get("status").toString());
        Log.d("weizheng", stationeryRequestDetail.toString());
        Log.d("weizheng", stationeryQuantities.toString());
        ListView listView = getActivity().findViewById(R.id.listview_stationery_request_detail);
        ArrayList<RowItem> rowItemList = new ArrayList<>();
        for(int i = 0; i < stationeryQuantities.length(); i++){
            JSONObject stationeryQuantity = stationeryQuantities.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    stationeryQuantity.getString("description"),
                    stationeryQuantity.getString("quantityRequested"),
                    stationeryQuantity.getString("unitOfMeasure"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_stationery_request_detail, rowItemList);
        listView.setAdapter(rowAdapter);
    }
    public interface IStationeryRequestDetailFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
    }
    public class RowItem{
        String description;
        String quantityRequested;
        String unitOfMeasure;
        public RowItem(String description, String quantityRequested, String unitOfMeasure){
            this.description = description;
            this.quantityRequested = quantityRequested;
            this.unitOfMeasure = unitOfMeasure;
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
                view = inflater.inflate(R.layout.rowitem_stationery_request_detail, null);
                row = new RowItemView();
                row.descriptionView = view.findViewById(R.id.textview_stationery_request_stationery_description);
                row.quantityRequestedView = view.findViewById(R.id.textview_stationery_request_quantity_requested);
                row.unitOfMeasureView= view.findViewById(R.id.textview_stationery_request_stationery_unit_of_measure);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.descriptionView.setText(rowItem.description);
            row.quantityRequestedView.setText(rowItem.quantityRequested);
            row.unitOfMeasureView.setText(rowItem.unitOfMeasure);
            return view;
        }
    }

    public class RowItemView{
        TextView descriptionView;
        TextView quantityRequestedView;
        TextView unitOfMeasureView;
    }
}
