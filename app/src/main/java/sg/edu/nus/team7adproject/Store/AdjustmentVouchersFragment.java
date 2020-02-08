package sg.edu.nus.team7adproject.Store;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.team7adproject.R;
import sg.edu.nus.team7adproject.StoreClerkActivity;
import sg.edu.nus.team7adproject.StoreSupActivity;

public class AdjustmentVouchersFragment extends Fragment implements
        View.OnClickListener, AdapterView.OnItemClickListener {
    IAdjustmentVouchersFragment iAdjustmentVouchersFragment;
    Button addButton;
    public AdjustmentVouchersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getAdjustmentVouchers();
        return inflater.inflate(R.layout.fragment_adjustment_vouchers, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        addButton = view.findViewById(R.id.button_adjustment_vouchers_add);
        addButton.setOnClickListener(this);
        if(iAdjustmentVouchersFragment.getClass() == StoreSupActivity.class){
            addButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iAdjustmentVouchersFragment = (AdjustmentVouchersFragment.IAdjustmentVouchersFragment) context;
        iAdjustmentVouchersFragment.setFragment("adjustmentVouchersFragment", this);
    }
    public void getAdjustmentVouchers(){
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("action", "getAdjustmentVouchers");
            request.put("url", "AdjustmentVouchers");
            request.put("requestBody", body);
            request.put("callbackFragment", "adjustmentVouchersFragment");
            request.put("callbackMethod", "getAdjustmentVouchersCallback");
            iAdjustmentVouchersFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void getAdjustmentVouchersCallback(String response) throws JSONException{
        ArrayList<RowItem> rowItemList = new ArrayList<RowItem>();
        ListView listView = getActivity().findViewById(R.id.listview_adjustment_vouchers);
        JSONArray adjustmentVouchers = new JSONArray(response);
        for(int i = 0; i < adjustmentVouchers.length(); i++){
            JSONObject adjustmentVoucher = adjustmentVouchers.getJSONObject(i);
            RowItem rowItem = new RowItem(
                    adjustmentVoucher.getInt("id"),
                    adjustmentVoucher.getString("item"),
                    adjustmentVoucher.getString("quantity"),
                    adjustmentVoucher.getString("status"));
            rowItemList.add(rowItem);
        }
        RowAdapter rowAdapter = new RowAdapter(getActivity(), R.layout.fragment_adjustment_vouchers, rowItemList);
        listView.setAdapter(rowAdapter);
        listView.setOnItemClickListener(this);
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_adjustment_vouchers_add:
                iAdjustmentVouchersFragment.gotoFragment("addAdjustmentVoucher");
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id){
        RowItem rowItem = (RowItem) adapter.getItemAtPosition(pos);
        iAdjustmentVouchersFragment.gotoFragment("adjustmentVoucherDetail", rowItem.id);
    }
    public interface IAdjustmentVouchersFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }

    public class RowItem{
        int id;
        String item;
        String quantity;
        String status;
        public RowItem(int id, String item, String quantity, String status){
            this.id = id;
            this.item = item;
            this.quantity = quantity;
            this.status = status;
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
                view = inflater.inflate(R.layout.rowitem_adjustment_vouchers, null);
                row = new RowItemView();
                row.itemView = view.findViewById(R.id.textview_adjustment_vouchers_item);
                row.quantityView = view.findViewById(R.id.textview_adjustment_vouchers_quantity);
                row.statusView= view.findViewById(R.id.textview_adjustment_vouchers_status);
                view.setTag(row);
            } else {
                row = (RowItemView) view.getTag();
            }
            row.itemView.setText(rowItem.item);
            row.quantityView.setText(rowItem.quantity);
            row.statusView.setText(rowItem.status);
            return view;
        }
    }

    public class RowItemView{
        TextView itemView;
        TextView quantityView;
        TextView statusView;
    }
}
