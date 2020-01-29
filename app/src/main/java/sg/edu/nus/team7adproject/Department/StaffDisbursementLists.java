package sg.edu.nus.team7adproject.Department;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.edu.nus.team7adproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffDisbursementLists extends Fragment {


    public StaffDisbursementLists() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_disbursement_lists, container, false);
    }


}
