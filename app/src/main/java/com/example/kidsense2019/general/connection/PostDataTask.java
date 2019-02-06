package com.example.kidsense2019.general.connection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.common.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostDataTask extends AsyncTask<Object, Void, String> {

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

    public PostDataTask(Context ctx) {

        this.ctx = ctx;
        activity = (Activity)ctx;
    }

    @Override
    protected String doInBackground(Object... obj) {
        try {
            String url = (String) obj[0];
            JSONObject json = (JSONObject) obj[1];
            return postData(url,json);
        } catch (IOException ex) {
            return "Network error !";
        } catch (JSONException e) {
            return "Data invalid !";
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Sending data...");
        progressDialog.show();
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // return value to Guardian_MainActivity
        mSetValue.update(result);

        //cancel progress dialog
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private String postData(String urlPath,JSONObject dataToSend) throws IOException, JSONException {

        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            //Create data to send to server


            //Initialize and config request, then connect to server
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(10000 /*milliseconds*/);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true); // enable output (body data)
            urlConnection.setRequestProperty("Content-Type", "application/json"); // set header
            urlConnection.connect();

            //Write data into server
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(dataToSend.toString());
            bufferedWriter.flush();

            //Read data response from server
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
            // close resource
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }

        return result.toString();
    }
}
