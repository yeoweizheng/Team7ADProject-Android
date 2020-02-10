package sg.edu.nus.team7adproject.Shared;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nus.team7adproject.R;

public class NotificationDetailFragment extends Fragment{

    NotificationDetailFragment.INotificationDetailFragment iNotificationDetailFragment;
    int notificationStatusId;

    public NotificationDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        notificationStatusId = NotificationDetailFragmentArgs.fromBundle(getArguments()).getNotificationStatusId();
        getNotificationDetail();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iNotificationDetailFragment = (NotificationDetailFragment.INotificationDetailFragment) context;
        iNotificationDetailFragment.setFragment("notificationDetailFragment", this);
    }
    public void getNotificationDetail(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getNotificationDetail");
            body.put("notificationStatusId", notificationStatusId);
            request.put("url", "NotificationDetail");
            request.put("requestBody", body);
            request.put("callbackFragment", "notificationDetailFragment");
            request.put("callbackMethod", "getNotificationDetailCallback");
            iNotificationDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getNotificationDetailCallback(String response) throws JSONException{
        JSONObject notificationStatus = new JSONObject(response);
        TextView dateView = getActivity().findViewById(R.id.textview_notification_detail_date);
        TextView subjectView = getActivity().findViewById(R.id.textview_notification_detail_subject);
        TextView senderView = getActivity().findViewById(R.id.textview_notification_detail_sender);
        TextView messageView = getActivity().findViewById(R.id.textview_notification_detail_message);
        dateView.setText(notificationStatus.getString("date"));
        subjectView.setText(notificationStatus.getString("subject"));
        senderView.setText(notificationStatus.getString("sender"));
        messageView.setText(notificationStatus.getString("message"));
    }

    public interface INotificationDetailFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
    }
}
