package sg.edu.nus.team7adproject.Store;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import sg.edu.nus.team7adproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreStockListsFragment extends Fragment {


    public StoreStockListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store_stock_lists, container, false);
    }

    public interface IStoreStockListsFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
    }
}
