package sg.edu.nus.team7adproject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerService extends Service {
    private final IBinder binder = new LocalBinder();
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
                    String urlString = "http://10.0.2.2:10000/Rest/" + request.get("url").toString();
                    Log.d("weizheng", urlString);
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.connect();
                    byte[] input = request.toString().getBytes("UTF-8");
                    try(OutputStream os = conn.getOutputStream()) {
                        Log.d("weizheng", input.toString());
                        os.write(input);
                        os.flush();
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
