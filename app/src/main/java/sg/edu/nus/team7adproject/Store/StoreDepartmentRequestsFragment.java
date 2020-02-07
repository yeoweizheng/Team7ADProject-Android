package sg.edu.nus.team7adproject.Store;


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

import sg.edu.nus.team7adproject.Department.StaffStationeryRequestsFragment;
import sg.edu.nus.team7adproject.R;

public class StoreDepartmentRequestsFragment extends Fragment implements
    AdapterView.OnItemClickListener {
    IStoreDepartmentRequestsFragment iStoreDepartmentRequestsFragment;
    public StoreDepartmentRequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDepartmentRequests();
        return inflater.inflate(R.layout.fragment_store_department_requests, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStoreDepartmentRequestsFragment = (IStoreDepartmentRequestsFragment) context;
        iStoreDepartmentRequestsFragment.setFragment("storeDepartmentRequestsFragment", this);
    }
    public void getDepartmentRequests(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getDepartmentRequests");
            request.put("url", "StoreDepartmentRequests");
            request.put("requestBody", body);
            request.put("callbackFragment", "storeDepartmentRequestsFragment");
            request.put("callbackMethod", "getDepartmentRequestsCallback");
            iStoreDepartmentRequestsFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getDepartmentRequestsCallback(String response) throws JSONException{
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_store_department_requests);
        JSONArray departmentRequests = new JSONArray(response);
        for(int i = 0; i < departmentRequests.length(); i++){
            JSONObject departmentRequest = departmentRequests.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    departmentRequest.getString("id"),
                    departmentRequest.getString("date"),
                    departmentRequest.getString("department"),
                    departmentRequest.getString("status"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_store_department_requests, rowItemList);
        listView.setAdapter(rowAdapter);
        listView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id){
        RowItem rowItem = (RowItem) adapter.getItemAtPosition(pos);
        iStoreDepartmentRequestsFragment.gotoFragment("departmentRequestDetailFromDepartmentRequests", Integer.parseInt(rowItem.id));
    }
    public interface IStoreDepartmentRequestsFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

    public class RowItem{
        String id;
        String date;
        String department;
        String status;
        public RowItem(String id, String date, String department, String status){
            this.id = id;
            this.date = date;
            this.department = department;
            this.status = status;
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
                view = inflater.inflate(R.layout.rowitem_store_department_requests, null);
                row = new RowItemView();
                row.idView = view.findViewById(R.id.textview_store_department_request_id);
                row.dateView = view.findViewById(R.id.textview_store_department_request_date);
                row.departmentView = view.findViewById(R.id.textview_store_department_request_department);
                row.statusView= view.findViewById(R.id.textview_store_department_request_status);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.idView.setText(rowItem.id);
            row.dateView.setText(rowItem.date);
            row.departmentView.setText(rowItem.department);
            row.statusView.setText(rowItem.status);
            return view;
        }
    }

    public class RowItemView{
        TextView idView;
        TextView dateView;
        TextView departmentView;
        TextView statusView;
    }
}
