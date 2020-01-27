package sg.edu.nus.team7adproject.Department;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.edu.nus.team7adproject.R;

public class StaffStationeryRequestsFragment extends Fragment {

    public StaffStationeryRequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_stationery_requests, container, false);
    }

}
