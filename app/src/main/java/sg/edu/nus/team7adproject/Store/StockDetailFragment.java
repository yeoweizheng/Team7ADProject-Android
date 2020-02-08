package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.R;

public class StockDetailFragment extends Fragment{

    StockDetailFragment.IStockDetailFragment iStockDetailFragment;
    int stationeryId;

    public StockDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stock_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        stationeryId = StockDetailFragmentArgs.fromBundle(getArguments()).getStationeryId();
        getStockDetail();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iStockDetailFragment = (StockDetailFragment.IStockDetailFragment) context;
        iStockDetailFragment.setFragment("stockDetailFragment", this);
    }
    public void getStockDetail(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getStockDetail");
            body.put("stationeryId", stationeryId);
            request.put("url", "StockDetail");
            request.put("requestBody", body);
            request.put("callbackFragment", "stockDetailFragment");
            request.put("callbackMethod", "getStockDetailCallback");
            iStockDetailFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getStockDetailCallback(String response) throws JSONException{
        JSONObject stationery = new JSONObject(response);
        TextView itemNumberView = getActivity().findViewById(R.id.textview_stock_detail_item_number);
        TextView categoryView = getActivity().findViewById(R.id.textview_stock_detail_category);
        TextView descriptionView = getActivity().findViewById(R.id.textview_stock_detail_description);
        TextView unitOfMeasureView = getActivity().findViewById(R.id.textview_stock_detail_unit_of_measure);
        TextView quantityInStockView = getActivity().findViewById(R.id.textview_stock_detail_quantity_in_stock);
        itemNumberView.setText(stationery.getString("itemNumber"));
        categoryView.setText(stationery.getString("category"));
        descriptionView.setText(stationery.getString("description"));
        unitOfMeasureView.setText(stationery.getString("unitOfMeasure"));
        quantityInStockView.setText(stationery.getString("quantityInStock"));
    }

    public interface IStockDetailFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
    }
}
