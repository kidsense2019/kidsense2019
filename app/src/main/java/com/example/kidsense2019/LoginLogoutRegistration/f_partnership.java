package com.example.kidsense2019.LoginLogoutRegistration;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kidsense2019.R;
import com.example.kidsense2019.Session;
import com.example.kidsense2019.connection.GetDataTask;
import com.example.kidsense2019.connection.PostDataTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link f_partnership#newInstance} factory method to
 * create an instance of this fragment.
 */
public class f_partnership extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView listView;
    EditText editText;
    View view;

    Toolbar toolbar;
    ArrayAdapter<String> adapter;
    ArrayList<String> list_selected;
    ArrayList<String> list_name;
    String append="";
    Session session;


    public f_partnership() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment f_partnership.
     */
    // TODO: Rename and change types and number of parameters
    public static f_partnership newInstance(String param1, String param2) {
        f_partnership fragment = new f_partnership();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_f_partnership, container, false);

        listView = view.findViewById(R.id.list_kid);
        editText = view.findViewById(R.id.guardian_emailS_partner);

        list_name = new ArrayList<>();
        session = new Session(view.getContext());

        getKid(session.getIP() + "/v1/kid/admin/" + session.getGuardianId());

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
        Button b = view.findViewById(R.id.signUP_partnership);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pushToServer();
                System.out.println("Check David Emma : ");
                for(int i = 0; i<list_selected.size();i++) {
                    System.out.println("name : " + list_selected.get(i));
                    System.out.println("number : " +  i);
                }
            }
        });

        return view;
    }

    private void init() {

        //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,list);
        adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_activated_1,list_name);
        list_selected = new ArrayList<>();
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

    private void pushToServer() {
        for(int i = 0; i<list_selected.size();i++){
            PostDataTask post = new PostDataTask(view.getContext());
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
                        System.out.println("reply: "+vData);
                        JSONObject message = new JSONObject(vData);
                        Toast.makeText(view.getContext(),message.getString("message"),Toast.LENGTH_SHORT).show();
                        //editText.setText(message.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void getKid(String url){

        GetDataTask get = new GetDataTask(view.getContext());
        // "2" adalah guardian ID
        get.execute(url);
        get.getValue(new GetDataTask.setValue() {
            @Override
            public void update(String vData) {
                try {
                    JSONObject jsonObject = new JSONObject(vData);
                    JSONArray jsonArray =jsonObject.getJSONArray("message");

                    for(int i = 0; i<jsonArray.length();i++){
                        JSONObject JO = jsonArray.getJSONObject(i);
                        list_name.add(JO.getString("name"));
                    }
                    init();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

}
