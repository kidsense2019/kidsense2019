package com.example.kidsense2019;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PutDataTask extends AsyncTask<String, Void, String>{

    ProgressDialog progressDialog;
    Context ctx;
    Activity activity;
    setValue mSetValue;

    public void getValue(setValue mSetValue){
        this.mSetValue = mSetValue;
    }

    public interface setValue {
        public void update(String vData);
    }

    public PutDataTask(Context ctx) {

        this.ctx = ctx;
        activity = (Activity)ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Updating data...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            return putData(params[0], params[1], params[2], params[3], params[4]);
        } catch (IOException ex) {
            return "Network error !";
        } catch (JSONException ex) {
            return "Data invalid !";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // return value to MainActivity
        mSetValue.update(result);

        //cancel progress dialog
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private String putData(String urlPath, String fbname, String content, String likes,
                           String comments) throws IOException, JSONException {

        String result = null;
        BufferedWriter bufferedWriter = null;

        try {
            //Create data to update
            JSONObject dataToSend = new JSONObject();
            dataToSend.put("fbname", fbname);
            dataToSend.put("content", content);
            dataToSend.put("likes", likes);
            dataToSend.put("comments", comments);

            //Initialize and config request, then connect to server
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(10000 /*milliseconds*/);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoOutput(true); // enable output (body data)
            urlConnection.setRequestProperty("Content-Type", "application/json"); // set header
            urlConnection.connect();

            //Write data into server
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(dataToSend.toString());
            bufferedWriter.flush();

            if (urlConnection.getResponseCode() == 200) {
                return "Update successfully !";
            } else {
                return "Update failed !";
            }
        } finally {
            // close resource
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }

    }

}
