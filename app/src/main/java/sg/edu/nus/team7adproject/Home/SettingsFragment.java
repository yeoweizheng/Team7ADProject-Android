package sg.edu.nus.team7adproject.Home;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nus.team7adproject.R;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    ISettingsFragment iSettingsFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Button testConnectionBtn = getView().findViewById(R.id.button_test_connection);
        testConnectionBtn.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iSettingsFragment = (ISettingsFragment) context;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_test_connection: testConnection(); break;
        }
    }

    private void testConnection(){
        JSONObject request = new JSONObject();
        try {
            request.put("url", "TestConnection");
            request.put("header", "header");
            request.put("body", "testing");
            iSettingsFragment.sendRequest(request);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public interface ISettingsFragment{
        void sendRequest(JSONObject request);
    }
}
