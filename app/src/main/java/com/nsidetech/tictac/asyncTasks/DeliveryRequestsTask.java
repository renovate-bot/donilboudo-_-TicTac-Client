package com.nsidetech.tictac.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.nsidetech.tictac.util.JsonUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DeliveryRequestsTask extends AsyncTask<String, Void, String> {
    private String endpointUrl = "";//EndpointConstants.remoteEndpointUrl + "/mobile/user/deliveryRequests";

    public interface AsyncResponse {
        void processFinish(String output);
    }

    private AsyncResponse delegate = null;

    public DeliveryRequestsTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @SafeVarargs
    @Override
    protected final String doInBackground(String... args) {
        //10.0.2.2 is the localhost of my computer
        try
        {
            URL url = new URL(endpointUrl);

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("deviceId", args[0]);

            Log.e("params", postDataParams.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000 /* milliseconds */);
            conn.setConnectTimeout(30000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(JsonUtil.getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            //if the server respond by 200, we will get the data and use it
            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder("");
                String line;

                while ((line = in.readLine()) != null)
                {
                    sb.append(line);
                }

                in.close();

                return sb.toString();
            }

            return "" + responseCode;
        }
        catch (Exception e)
        {
            String msg = e != null ? e.getMessage() : "";
            return "Exception: " + msg;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}