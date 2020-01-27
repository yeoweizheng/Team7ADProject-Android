package sg.edu.nus.team7adproject.Home;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import sg.edu.nus.team7adproject.R;


public class LoginFragment extends Fragment implements View.OnClickListener {
    LoginFragment.ILoginFragment iLoginFragment;
    EditText usernameEditText;
    EditText passwordEditText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button loginButton = (Button) view.findViewById(R.id.button_login);
        loginButton.setOnClickListener(this);
        usernameEditText = (EditText) view.findViewById(R.id.edittext_username);
        passwordEditText = (EditText) view.findViewById(R.id.edittext_password);
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iLoginFragment = (LoginFragment.ILoginFragment) context;
        iLoginFragment.setFragment("loginFragment", this);
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_login: login(); break;
        }
    }
    private void login(){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        JSONObject request = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
            request.put("url", "Login");
            request.put("requestBody", body.toString());
            request.put("callbackFragment", "loginFragment");
            request.put("callbackMethod", "loginCallback");
            iLoginFragment.sendRequest(request);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void loginCallback(String response){
        try {
            JSONObject responseObj = new JSONObject(response);
            if(!responseObj.has("Username")){
                getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                return;
            }
            iLoginFragment.launchActivity(responseObj.get("UserType").toString());
        } catch(JSONException e){
            e.printStackTrace();
        }
    }
    public interface ILoginFragment{
        void sendRequest(JSONObject request);
        void setFragment(String name, Fragment fragment);
        void launchActivity(String userType);
    }
}
