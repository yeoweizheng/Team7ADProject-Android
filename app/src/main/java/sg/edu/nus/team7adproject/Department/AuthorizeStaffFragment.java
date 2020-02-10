package sg.edu.nus.team7adproject.Department;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sg.edu.nus.team7adproject.R;

public class AuthorizeStaffFragment extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener{
    IAuthorizeStaffFragment iAuthorizeStaffFragment;
    Button addButton;
    public AuthorizeStaffFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getAuthorizeStaff();
        return inflater.inflate(R.layout.fragment_authorize_staff, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        addButton = view.findViewById(R.id.button_authorize_staff_add);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iAuthorizeStaffFragment = (IAuthorizeStaffFragment) context;
        iAuthorizeStaffFragment.setFragment("authorizeStaffFragment", this);
    }
    public void getAuthorizeStaff(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getAuthorizeStaff");
            request.put("url", "GetAuthorizedStaff");
            request.put("requestBody", body);
            request.put("callbackFragment", "authorizeStaffFragment");
            request.put("callbackMethod", "getAuthorizeStaffCallback");
            iAuthorizeStaffFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getAuthorizeStaffCallback(String response) throws JSONException{
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_authorize_staff);
        JSONArray authorizeForms = new JSONArray(response);
        for(int i = 0; i < authorizeForms.length(); i++){
            JSONObject authorizeForm = authorizeForms.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    authorizeForm.getInt("id"),
                    authorizeForm.getString("name"),
                    authorizeForm.getString("startDate"),
                    authorizeForm.getString("endDate"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_authorize_staff, rowItemList);
        listView.setAdapter(rowAdapter);
        listView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id){
        RowItem rowItem = (RowItem) adapter.getItemAtPosition(pos);
        iAuthorizeStaffFragment.gotoFragment("authorizeStaffDetail", rowItem.id);
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_authorize_staff_add:
                iAuthorizeStaffFragment.gotoFragment("addAuthorizedStaff");
                break;
        }
    }
    public interface IAuthorizeStaffFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

    public class RowItem{
        int id;
        String name;
        String startDate;
        String endDate;
        public RowItem(int id, String name, String startDate, String endDate){
            this.id = id;
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
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
                view = inflater.inflate(R.layout.rowitem_authorize_staff, null);
                row = new RowItemView();
                row.nameView = view.findViewById(R.id.textview_authorize_staff_name);
                row.startDateView = view.findViewById(R.id.textview_authorize_staff_start_date);
                row.endDateView = view.findViewById(R.id.textview_authorize_staff_end_date);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.nameView.setText(rowItem.name);
            row.startDateView.setText(rowItem.startDate);
            row.endDateView.setText(rowItem.endDate);
            return view;
        }
    }

    public class RowItemView{
        TextView nameView;
        TextView startDateView;
        TextView endDateView;
    }
}
