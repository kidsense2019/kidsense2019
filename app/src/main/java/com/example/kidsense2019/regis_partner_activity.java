package com.example.kidsense2019;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kidsense2019.LoginLogoutRegistration.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class regis_partner_activity extends AppCompatActivity {

    ListView listView;
    EditText editText;


    Toolbar toolbar;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;
    ArrayList<String> list_selected;
    ArrayList<String> list_name;
    ArrayList<String> id;
    String append="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partnership);

        listView = findViewById(R.id.list_kid);
        editText = findViewById(R.id.guardian_emailS_partner);
        getKid();
        init();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if(list_selected.contains(selected)){
                    list_selected.remove(selected);
                }else{
                    list_selected.add(selected);
                }
                //Toast.makeText(regis_partner_activity.this,String.valueOf(list_selected.size()) ,Toast.LENGTH_SHORT).show();
                append="";
            }
        });
        Button b = findViewById(R.id.signUP_partnership);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushToServer();
                /*
                for (int i =0 ;i<list_selected.size();i++){
                    if(i==0){
                        append=list_selected.get(i);
                    }else{
                        append=append+"-"+list_selected.get(i);
                    }
                }
                editText.setText(append);
                append="";
                */
            }
        });
    }

    private void init() {
        list = new ArrayList<>();
        list_name = new ArrayList<>();
        id = new ArrayList<>();

        list.add("ian");
        list.add("ren");
        list.add("kez");

        //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,list);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,list_name);
        list_selected = new ArrayList<>();
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

    private void pushToServer() {
        for(int i = 0; i<list_selected.size();i++){
            PostDataTask post = new PostDataTask(regis_partner_activity.this);
            JSONObject dataToSend = new JSONObject();

            try {
                dataToSend.put("assignee", 1);
                dataToSend.put("guardian", editText.getText().toString());
                dataToSend.put("nickname", list_selected.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            post.execute("http://203.189.123.200:3000/v1/partnership/register",dataToSend);
            post.getValue(new PostDataTask.setValue() {
                @Override
                public void update(String vData) {

                    try {
                        System.out.println("balesan: "+vData);
                        JSONObject message = new JSONObject(vData);
                        Toast.makeText(regis_partner_activity.this,message.getString("message"),Toast.LENGTH_SHORT).show();
                        //editText.setText(message.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void getKid(){

        GetDataTask get = new GetDataTask(regis_partner_activity.this);
        // "2" adalah guardian ID
        get.execute("http://203.189.123.200:3000/v1/kid/admin/2");
        get.getValue(new GetDataTask.setValue() {
            @Override
            public void update(String vData) {
                try {
                    JSONObject jsonObject = new JSONObject(vData);
                    JSONArray jsonArray =jsonObject.getJSONArray("message");

                    for(int i = 0; i<jsonArray.length();i++){
                        JSONObject JO = jsonArray.getJSONObject(i);
                        list_name.add(JO.getString("name"));
                        id.add(JO.getString("kidID"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
