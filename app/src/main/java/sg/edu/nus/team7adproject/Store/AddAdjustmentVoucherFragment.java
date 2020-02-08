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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class AddAdjustmentVoucherFragment extends Fragment implements
        View.OnClickListener, AdapterView.OnItemSelectedListener {
    IAddAdjustmentVoucherFragment iAddAdjustmentVoucherFragment;
    Spinner spinner;
    Button raiseButton;
    EditText quantityEditText;
    EditText reasonEditText;
    int selectedStationeryId;
    HashMap<Integer, Integer> posToStationeryId;
    public AddAdjustmentVoucherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        posToStationeryId = new HashMap<Integer, Integer>();
        getStationeries();
        return inflater.inflate(R.layout.fragment_add_adjustment_voucher, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        spinner = view.findViewById(R.id.spinner_add_adjustment_voucher_item);
        raiseButton = view.findViewById(R.id.button_add_adjustment_voucher_raise);
        quantityEditText = view.findViewById(R.id.edittext_add_adjustment_voucher_quantity);
        reasonEditText = view.findViewById(R.id.edittext_add_adjustment_voucher_reason);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iAddAdjustmentVoucherFragment = (AddAdjustmentVoucherFragment.IAddAdjustmentVoucherFragment) context;
        iAddAdjustmentVoucherFragment.setFragment("addAdjustmentVoucherFragment", this);
    }
    public void getStationeries(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStationeries");
            request.put("url", "GetStationeries");
            request.put("requestBody", body);
            request.put("callbackFragment", "addAdjustmentVoucherFragment");
            request.put("callbackMethod", "getStationeriesCallback");
            iAddAdjustmentVoucherFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStationeriesCallback(String response) throws JSONException{
        ArrayList<String> stationeriesList = new ArrayList<String>();
        JSONArray stationeries = new JSONArray(response);
        String stationeriesStr[] = new String[stationeries.length()];
        for(int i = 0; i < stationeries.length(); i++){
            JSONObject stationery = stationeries.getJSONObject(i);
            stationeriesStr[i] = stationery.getString("description");
            posToStationeryId.put(i, stationery.getInt("id"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, stationeriesStr);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        raiseButton.setOnClickListener(this);
    }
    public void raiseAdjustmentVoucher(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "raiseAdjustmentVoucher");
            body.put("stationeryId", selectedStationeryId);
            body.put("quantity", quantityEditText.getText().toString());
            body.put("reason", reasonEditText.getText().toString());
            request.put("url", "RaiseAdjustmentVoucher");
            request.put("requestBody", body);
            request.put("callbackFragment", "addAdjustmentVoucherFragment");
            request.put("callbackMethod", "raiseAdjustmentVoucherCallback");
            iAddAdjustmentVoucherFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void raiseAdjustmentVoucherCallback(String response) throws JSONException{
        iAddAdjustmentVoucherFragment.onBackPressed();
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_add_adjustment_voucher_raise:
                raiseAdjustmentVoucher();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapter, View view, int pos, long id){
        selectedStationeryId = posToStationeryId.get(pos);
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0){}

    public interface IAddAdjustmentVoucherFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
        void onBackPressed();
    }

}
