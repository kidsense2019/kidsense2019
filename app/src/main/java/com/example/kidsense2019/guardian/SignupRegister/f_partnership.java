package com.example.kidsense2019.guardian.SignupRegister;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.kidsense2019.guardian.Session_Guardian;
import com.example.kidsense2019.general.connection.GetDataTask;
import com.example.kidsense2019.general.connection.PostDataTask;

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
    EditText assignGuardianEmail;
    View view;

    Toolbar toolbar;
    ArrayAdapter<String> adapter;
    ArrayList<String> list_selected;
    ArrayList<String> list_name;
    String append="";
    Session_Guardian session_guardian;
    Button assignPartnership;

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

        session_guardian = new Session_Guardian(view.getContext());
        list_name = new ArrayList<>();

        listView = view.findViewById(R.id.list_kid);
        assignGuardianEmail = view.findViewById(R.id.assign_guardian_email);

        getKid(session_guardian.getIP() + "/v1/kid/admin/" + session_guardian.getGuardianId());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if(list_selected.contains(selected)){
                    list_selected.remove(selected);
                }else{
                    list_selected.add(selected);
                }
                append="";
            }
        });
        assignPartnership = view.findViewById(R.id.assign_partnership);
        assignPartnership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String guardianEmail = assignGuardianEmail.getText().toString();
                boolean valid = isValid(guardianEmail);

                if (valid) {
                    pushToServer(guardianEmail, session_guardian.getIP() + "/v1/partnership/register");
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

    private void pushToServer(String guardianEmail, String url) {
        for(int i = 0; i<list_selected.size();i++){
            PostDataTask post = new PostDataTask(view.getContext());
            JSONObject dataToSend = new JSONObject();

            try {
                dataToSend.put("assignee", session_guardian.getGuardianId());
                dataToSend.put("guardian", guardianEmail);
                dataToSend.put("nickName", list_selected.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            post.execute(url ,dataToSend);
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

                    try {
                        JSONArray kids = jsonObject.getJSONArray("message");
                        for(int i = 0; i<kids.length();i++){
                            JSONObject JO = kids.getJSONObject(i);
                            list_name.add(JO.getString("nickName"));
                        }
                    }
                    catch (JSONException e1) {
                        errorMessage(jsonObject.getString("message"));
                        assignPartnership.setEnabled(false);
                        assignGuardianEmail.setEnabled(false);
                        e1.printStackTrace();
                    }

                    init();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void errorMessage(String message) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());
        builderSingle.setIcon(R.drawable.ic_kid_blue);
        builderSingle.setTitle("We are sorry");
        builderSingle.setMessage(message);

        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

    public boolean isValid(String emailStr) {

        boolean flag=true;

        if (!isEmailValid(emailStr )){
            assignGuardianEmail.setError("Please provide a valid email");
            flag=false;
        }
        return flag;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
