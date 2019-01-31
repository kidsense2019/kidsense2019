package com.example.kidsense2019;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetDataTask extends AsyncTask<String, Void, String>{

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

    public GetDataTask(Context ctx) {

        this.ctx = ctx;
        activity = (Activity)ctx;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            return getData(params[0]);
        } catch (IOException ex) {
            return "Network error !";
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

    private String getData(String urlPath) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            // Initialize and config request, then connect to server
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(10000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json"); // set header
            urlConnection.connect();

            // Read data response from server
            InputStream inputStream;
            if(urlConnection.getResponseCode()<HttpURLConnection.HTTP_BAD_REQUEST){
                inputStream = urlConnection.getInputStream();
            }else {
                inputStream = urlConnection.getErrorStream();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return result.toString();
    }
}
