package sg.edu.nus.team7adproject.Shared;


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

public class NotificationsFragment extends Fragment {
    public INotificationsFragment iNotificationsFragment;
    public NotificationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getNotifications();
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iNotificationsFragment = (INotificationsFragment) context;
        iNotificationsFragment.setFragment("notificationsFragment", this);
    }
    public void getNotifications(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getNotifications");
            request.put("url", "Notifications");
            request.put("requestBody", body);
            request.put("callbackFragment", "notificationsFragment");
            request.put("callbackMethod", "getNotificationsCallback");
            iNotificationsFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getNotificationsCallback(String response) throws JSONException{
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_notifications);
        JSONArray notifications = new JSONArray(response);
        for(int i = 0; i < notifications.length(); i++){
            JSONObject notification = notifications.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    notification.getString("date"),
                    notification.getString("subject"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_notifications, rowItemList);
        listView.setAdapter(rowAdapter);
    }
    public interface INotificationsFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

    public class RowItem{
        String date;
        String subject;
        public RowItem(String date, String subject){
            this.date = date;
            this.subject = subject;
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
                view = inflater.inflate(R.layout.rowitem_notifications, null);
                row = new RowItemView();
                row.dateView = view.findViewById(R.id.textview_notifications_date);
                row.subjectView= view.findViewById(R.id.textview_notifications_subject);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.dateView.setText(rowItem.date);
            row.subjectView.setText(rowItem.subject);
            return view;
        }
    }

    public class RowItemView{
        TextView dateView;
        TextView subjectView;
    }
}
