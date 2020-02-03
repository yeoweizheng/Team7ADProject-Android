package sg.edu.nus.team7adproject.Store;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.json.JSONObject;

import sg.edu.nus.team7adproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreAdjustmentVouchersFragment extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener{
    IStoreAdjustmentVouchersFragment iStoreAdjustmentVouchersFragment;
    public StoreAdjustmentVouchersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store_adjustment_vouchers, container, false);
    }
    @Override
    public void onClick(View view) {

    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    public interface IStoreAdjustmentVouchersFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void gotoFragment(String name, int id);
        void gotoFragment(String name);
    }
}
