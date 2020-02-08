package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.edu.nus.team7adproject.R;
import sg.edu.nus.team7adproject.StoreSupActivity;

public class AdjustmentVoucherDetailFragment extends Fragment
        implements View.OnClickListener{

    IAdjustmentVoucherDetailFragment iAdjustmentVoucherDetailFragment;
    int adjustmentVoucherId;
    TextView itemNumberView;
    TextView itemView;
    TextView quantityView;
    TextView reasonView;
    TextView statusView;
    Button approveButton;
    Button rejectButton;
    public AdjustmentVoucherDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adjustment_voucher_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        approveButton = view.findViewById(R.id.button_adjustment_voucher_detail_approve);
        rejectButton = view.findViewById(R.id.button_adjustment_voucher_detail_reject);
        approveButton.setOnClickListener(this);
        rejectButton.setOnClickListener(this);
        itemNumberView = view.findViewById(R.id.textview_adjustment_voucher_detail_item_number);
        itemView = view.findViewById(R.id.textview_adjustment_voucher_detail_item);
        quantityView = view.findViewById(R.id.textview_adjustment_voucher_detail_quantity);
        reasonView = view.findViewById(R.id.textview_adjustment_voucher_detail_reason);
        statusView = view.findViewById(R.id.textview_adjustment_voucher_detail_status);
        this.adjustmentVoucherId = AdjustmentVoucherDetailFragmentArgs.fromBundle(getArguments()).getAdjustmentVoucherId();
        getAdjustmentVoucherDetail();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iAdjustmentVoucherDetailFragment = (IAdjustmentVoucherDetailFragment) context;
        iAdjustmentVoucherDetailFragment.setFragment("adjustmentVoucherDetailFragment", this);
    }
    public void getAdjustmentVoucherDetail(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getAdjustmentVoucherDetail");
            body.put("adjustmentVoucherId", adjustmentVoucherId);
            request.put("url", "AdjustmentVoucherDetail");
            request.put("requestBody", body);
            request.put("callbackFragment", "adjustmentVoucherDetailFragment");
            request.put("callbackMethod", "getAdjustmentVoucherDetailCallback");
            iAdjustmentVoucherDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getAdjustmentVoucherDetailCallback(String response) throws JSONException{
        JSONObject adjustmentVoucher = new JSONObject(response);
        itemNumberView.setText(adjustmentVoucher.getString("itemNumber"));
        itemView.setText(adjustmentVoucher.getString("item"));
        quantityView.setText(adjustmentVoucher.getString("quantity"));
        reasonView.setText(adjustmentVoucher.getString("reason"));
        statusView.setText(adjustmentVoucher.getString("status"));
        if(iAdjustmentVoucherDetailFragment.getClass() == StoreSupActivity.class &&
                adjustmentVoucher.getString("status").equals("Pending")){
            approveButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);
        } else {
            approveButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
        }
    }
    public void approveAdjustmentVoucher(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "approveAdjustmentVoucher");
            body.put("adjustmentVoucherId", adjustmentVoucherId);
            request.put("url", "ApproveAdjustmentVoucher");
            request.put("requestBody", body);
            request.put("callbackFragment", "adjustmentVoucherDetailFragment");
            request.put("callbackMethod", "approveAdjustmentVoucherCallback");
            iAdjustmentVoucherDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void approveAdjustmentVoucherCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Approved adjustment voucher", Toast.LENGTH_SHORT).show();
            getAdjustmentVoucherDetail();
        }
    }
    public void rejectAdjustmentVoucher(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "rejectAdjustmentVoucher");
            body.put("adjustmentVoucherId", adjustmentVoucherId);
            request.put("url", "RejectAdjustmentVoucher");
            request.put("requestBody", body);
            request.put("callbackFragment", "adjustmentVoucherDetailFragment");
            request.put("callbackMethod", "rejectAdjustmentVoucherCallback");
            iAdjustmentVoucherDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void rejectAdjustmentVoucherCallback(String response) throws JSONException{
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.getString("result").equals("success")){
            Toast.makeText(getActivity().getApplicationContext(), "Rejected adjustment voucher", Toast.LENGTH_SHORT).show();
            getAdjustmentVoucherDetail();
        }
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_adjustment_voucher_detail_approve:
                approveAdjustmentVoucher();
                break;
            case R.id.button_adjustment_voucher_detail_reject:
                rejectAdjustmentVoucher();
                break;
        }
    }
    public interface IAdjustmentVoucherDetailFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
    }
}
