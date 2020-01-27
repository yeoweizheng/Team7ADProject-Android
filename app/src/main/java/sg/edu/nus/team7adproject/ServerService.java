package sg.edu.nus.team7adproject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerService extends Service {
    private final IBinder binder = new LocalBinder();
    IServerService iServerService;
    public class LocalBinder extends Binder{
        ServerService getService(){
            return ServerService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }
    public void sendRequest(final JSONObject request){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String serverAddress = iServerService.getServerAddressFromSharedPref();
                    String urlString = "http://" + serverAddress + ":10000/Rest/" + request.get("url").toString();
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(request.toString());
                    writer.flush();
                    InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                    StringWriter stringWriter = new StringWriter();
                    int c;
                    while((c = reader.read()) != -1){
                        stringWriter.write(c);
                    }
                    String response = stringWriter.toString();
                    String callbackFragment = request.getString("callbackFragment");
                    String callbackMethod = request.getString("callbackMethod");
                    iServerService.handleResponse(response, callbackFragment, callbackMethod);
                    conn.disconnect();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void setCallback(IServerService iServerService){
        this.iServerService = iServerService;
    }
    public interface IServerService{
        String getServerAddressFromSharedPref();
        void handleResponse(String response, String callbackFragment, String callbackMethod);
    }
}
