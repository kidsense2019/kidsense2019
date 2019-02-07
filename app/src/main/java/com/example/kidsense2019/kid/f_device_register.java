package com.example.kidsense2019.kid;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kidsense2019.R;
import com.example.kidsense2019.general.Session;
import com.example.kidsense2019.general.connection.PostDataTask;
import com.example.kidsense2019.guardian.Guardian_MainActivity;
import com.example.kidsense2019.guardian.Session_Guardian;
import com.example.kidsense2019.guardian.SignupRegister.Guardian_SignUp;
import com.example.kidsense2019.guardian.location.MapsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link f_device_register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class f_device_register extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText MAC1, MAC2, MAC3, MAC4, MAC5, MAC6, DeviceType;
    private Button register;
    private Session_Kid session_kid;
    private Session session;
    private View view;


    public f_device_register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment f_device_register.
     */
    // TODO: Rename and change types and number of parameters
    public static f_device_register newInstance(String param1, String param2) {
        f_device_register fragment = new f_device_register();
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
        view = inflater.inflate(R.layout.fragment_f_device_register, container, false);

        session = new Session((view.getContext()));
        session_kid = new Session_Kid(view.getContext());

        MAC1 = (EditText)view.findViewById(R.id.MAC_1);
        MAC2 = (EditText)view.findViewById(R.id.MAC_2);
        MAC3 = (EditText)view.findViewById(R.id.MAC_3);
        MAC4 = (EditText)view.findViewById(R.id.MAC_4);
        MAC5 = (EditText)view.findViewById(R.id.MAC_5);
        MAC6 = (EditText)view.findViewById(R.id.MAC_6);
        DeviceType = (EditText)view.findViewById(R.id.device_type);
        register = (Button)view.findViewById(R.id.MAC_Register);

        register.setOnClickListener(registerOnClickListener);

        return view;
    }

    Button.OnClickListener registerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String mac1 = MAC1.getText().toString();
            String mac2 = MAC2.getText().toString();
            String mac3 = MAC3.getText().toString();
            String mac4 = MAC4.getText().toString();
            String mac5 = MAC5.getText().toString();
            String mac6 = MAC6.getText().toString();
            String deviceType = DeviceType.getText().toString();

            boolean valid = isValid(mac1, mac2, mac3, mac4, mac5, mac6, deviceType);

            if (valid) {
                String mac = mac1 + ":" + mac2 + ":" + mac3 + ":" + mac4 + ":" + mac5 + ":" + mac6;

                PostDataTask post = new PostDataTask(view.getContext());
                JSONObject dataToSend = new JSONObject();

                try {
                    dataToSend.put("kidId", session_kid.getKidId());
                    dataToSend.put("MAC", mac);
                    dataToSend.put("type", deviceType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                post.execute(session.getIP() + "/v1/device/register",dataToSend);
                post.getValue(new PostDataTask.setValue() {
                    @Override
                    public void update(String vData) {

                        try {
                            System.out.println("reply: "+vData);
                            JSONObject message = new JSONObject(vData);
                            String messageStr = message.getString("message");

                            if (messageStr.equals("Your device has been successfully recorded")) {

                                finishMessage(messageStr);

                            }
                            else {
                                Toast.makeText(view.getContext(), messageStr ,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }
    };

    public boolean isValid(String mac1, String mac2, String mac3, String mac4, String mac5, String mac6, String deviceType) {

        boolean flag=true;

        if (mac1.length() < 2) {
            MAC1.setError("Please fill out this field");
            flag=false;
        }
        if (mac2.length() < 2) {
            MAC2.setError("Please fill out this field");
            flag=false;
        }
        if (mac3.length() < 2) {
            MAC3.setError("Please fill out this field");
            flag=false;
        }
        if (mac4.length() < 2) {
            MAC4.setError("Please fill out this field");
            flag=false;
        }
        if (mac5.length() < 2) {
            MAC5.setError("Please fill out this field");
            flag=false;
        }
        if (mac6.length() < 2) {
            MAC6.setError("Please fill out this field");
            flag=false;
        }
        if (deviceType.trim().isEmpty()) {
            DeviceType.setError("Please fill out your device type");
            flag=false;
        }

        return flag;
    }

    public void finishMessage(String message) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());
        builderSingle.setIcon(R.drawable.ic_kid_blue);
        builderSingle.setTitle("Thank you");
        builderSingle.setMessage(message);

        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
                f_home_kid F_home_kid = f_home_kid.newInstance("param1", "param2");
                android.support.v4.app.FragmentManager manager = getFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.layout_for_fragment, F_home_kid,
                        F_home_kid.getTag());
                transaction.commit();
            }
        });

        builderSingle.show();
    }

}
