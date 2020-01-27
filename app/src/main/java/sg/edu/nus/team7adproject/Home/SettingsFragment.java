package sg.edu.nus.team7adproject.Home;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nus.team7adproject.R;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    ISettingsFragment iSettingsFragment;
    SharedPreferences serverAddressPref;
    SharedPreferences.Editor serverAddressPrefEditor;
    EditText urlEditText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Button saveSettingsBtn = view.findViewById(R.id.button_save_settings);
        saveSettingsBtn.setOnClickListener(this);
        serverAddressPref = this.getActivity().getSharedPreferences("serverAddress", Context.MODE_PRIVATE);
        serverAddressPrefEditor = serverAddressPref.edit();
        urlEditText = view.findViewById(R.id.editText_url);
        String storedURL = serverAddressPref.getString("serverAddress", "");
        if(storedURL != ""){
            urlEditText.setText(storedURL);
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iSettingsFragment = (ISettingsFragment) context;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_save_settings: saveSettings(); break;
        }
    }

    private void saveSettings(){
        String urlString = urlEditText.getText().toString();
        serverAddressPrefEditor.putString("serverAddress", urlString);
        serverAddressPrefEditor.commit();
    }

    public interface ISettingsFragment{
        void sendRequest(JSONObject request);
    }
}
