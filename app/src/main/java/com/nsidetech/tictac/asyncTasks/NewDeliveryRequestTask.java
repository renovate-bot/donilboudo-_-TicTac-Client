package com.nsidetech.tictac.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.nsidetech.tictac.util.JsonUtil;

public class NewDeliveryRequestTask extends AsyncTask<String, Void, String> {
    private String endpointUrl = ""; //EndpointConstants.remoteEndpointUrl + "/mobile/newDeliveryRequest";

    public interface AsyncResponse {
        void processFinish(String output);
    }

    private AsyncResponse delegate = null;

    public NewDeliveryRequestTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    protected void onPreExecute() {

    }

    protected String doInBackground(String... args) {
        try
        {
            //10.0.2.2 is the localhost of my computer
            URL url = new URL(endpointUrl);

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("requestDate", args[0]);
            postDataParams.put("senderName", args[1]);
            postDataParams.put("senderNumber", args[2]);
            postDataParams.put("senderAddress", args[3]);
            postDataParams.put("receiverName", args[4]);
            postDataParams.put("receiverAddress", args[5]);
            postDataParams.put("senderComments", args[6]);
            postDataParams.put("packageType", args[7]);
            postDataParams.put("receiverNumber", args[8]);
            postDataParams.put("deviceId", args[9]);
            postDataParams.put("requesterToken", args[10]);

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

            return "";
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