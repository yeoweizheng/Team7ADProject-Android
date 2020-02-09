package sg.edu.nus.team7adproject.Department;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nus.team7adproject.R;

public class AuthorizeStaffDetailFragment extends Fragment implements View.OnClickListener{

    AuthorizeStaffDetailFragment.IAuthorizeStaffDetailFragment iAuthorizeStaffDetailFragment;
    int authorizeFormId;
    Button cancelButton;

    public AuthorizeStaffDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_authorize_staff_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        authorizeFormId = AuthorizeStaffDetailFragmentArgs.fromBundle(getArguments()).getAuthorizeFormId();
        cancelButton = view.findViewById(R.id.button_authorize_staff_detail_cancel);
        cancelButton.setOnClickListener(this);
        getAuthorizeStaffDetail();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iAuthorizeStaffDetailFragment = (AuthorizeStaffDetailFragment.IAuthorizeStaffDetailFragment) context;
        iAuthorizeStaffDetailFragment.setFragment("authorizeStaffDetailFragment", this);
    }
    public void getAuthorizeStaffDetail(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getAuthorizeStaffDetail");
            body.put("authorizeFormId", authorizeFormId);
            request.put("url", "AuthorizeStaffDetail");
            request.put("requestBody", body);
            request.put("callbackFragment", "authorizeStaffDetailFragment");
            request.put("callbackMethod", "getAuthorizeStaffDetailCallback");
            iAuthorizeStaffDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getAuthorizeStaffDetailCallback(String response) throws JSONException{
        JSONObject stationery = new JSONObject(response);
        TextView nameView = getActivity().findViewById(R.id.textview_authorize_staff_detail_name);
        TextView startDateView = getActivity().findViewById(R.id.textview_authorize_staff_detail_start_date);
        TextView endDateView = getActivity().findViewById(R.id.textview_authorize_staff_detail_end_date);
        nameView.setText(stationery.getString("name"));
        startDateView.setText(stationery.getString("startDate"));
        endDateView.setText(stationery.getString("endDate"));
    }

    public void cancelAuthorization(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "cancelAuthorization");
            body.put("authorizeFormId", authorizeFormId);
            request.put("url", "CancelAuthorization");
            request.put("requestBody", body);
            request.put("callbackFragment", "authorizeStaffDetailFragment");
            request.put("callbackMethod", "cancelAuthorizationCallback");
            iAuthorizeStaffDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void cancelAuthorizationCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            iAuthorizeStaffDetailFragment.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_authorize_staff_detail_cancel:
                cancelAuthorization();
                break;
        }
    }

    public interface IAuthorizeStaffDetailFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void onBackPressed();
    }
}
