package com.example.kidsense2019.guardian.sensor;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kidsense2019.R;
import com.example.kidsense2019.general.Session;
import com.example.kidsense2019.general.connection.GetDataTask;
import com.example.kidsense2019.guardian.Session_Guardian;
import com.example.kidsense2019.guardian.f_home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link f_heart_rate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class f_heart_rate extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private Session session;
    private Session_Guardian session_guardian;
    private TextView tv_nickName;
    private ListView lv_heartRate;
    private LinearLayout h_linearLayout;
    private heart_rate_array_adapter Heart_rate_array_adapter;

    public f_heart_rate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment f_heart_rate.
     */
    // TODO: Rename and change types and number of parameters
    public static f_heart_rate newInstance(String param1, String param2) {
        f_heart_rate fragment = new f_heart_rate();
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
        view = inflater.inflate(R.layout.fragment_f_heart_rate, container, false);

        session = new Session(view.getContext());
        session_guardian = new Session_Guardian(view.getContext());

        tv_nickName = (TextView)view.findViewById(R.id.h_nickName);
        lv_heartRate = (ListView)view.findViewById(R.id.lv_heart_rate);
        h_linearLayout = (LinearLayout)view.findViewById(R.id.h_linear_layout);

        Heart_rate_array_adapter = new heart_rate_array_adapter(view.getContext(), R.layout.heart_rate_layout);

        getKid(session.getIP() + "/v1/kid/user/" + session_guardian.getGuardianId(),
                "refresh");


        return view;
    }

    public void getKid(String UrlGet, final String nav_message) {

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.select_dialog_item);
        GetDataTask get = new GetDataTask(view.getContext());
        get.execute(UrlGet);
        get.getValue(new GetDataTask.setValue() {
            @Override
            public void update(String vData) {

                try {
                    JSONObject message = new JSONObject(vData);

                    try {
                        JSONArray kids = message.getJSONArray("message");
                        for (int i = 0; i < kids.length(); i++) {
                            JSONObject kid = kids.getJSONObject(i);
                            arrayAdapter.add(kid.getString("nickName"));
                        }

                        kidList(arrayAdapter); // post data
                    }
                    catch (JSONException e1) {
                        message("We are sorry", message.getString("message"));
                        e1.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void kidList(final ArrayAdapter<String> arrayAdapter) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());
        builderSingle.setIcon(R.drawable.ic_kid_blue);
        builderSingle.setTitle("Select One Name:-");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                f_home F_home = f_home.newInstance("param1", "param2");
                android.support.v4.app.FragmentManager manager = getFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.layout_for_fragment, F_home,
                        F_home.getTag());
                transaction.commit();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(view.getContext());
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Kid is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                        GetDataTask get = new GetDataTask(view.getContext());
                        get.execute(session.getIP() + "/v1/sensorHeartRate/" + strName);
                        get.getValue(new GetDataTask.setValue() {
                            @Override
                            public void update(String vData) {

                                try {
                                    JSONObject message = new JSONObject(vData);

                                    try {
                                        tv_nickName.setText(strName);
                                        h_linearLayout.setVisibility(View.VISIBLE);
                                        tv_nickName.setVisibility(View.VISIBLE);

                                        JSONArray heartRates = message.getJSONArray("heartRates");

                                        for (int i = 0; i < heartRates.length(); i++) {
                                            JSONObject heartRate = heartRates.getJSONObject(i);

                                            TimeZone tz = TimeZone.getTimeZone("UTC");
                                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                            df.setTimeZone(tz);

                                            try {
                                                Date date = df.parse(heartRate.getString("recordDateTime"));
                                                Calendar cal = Calendar.getInstance();
                                                cal.setTime(date);
                                                String datePrint = cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH ) + " " + cal.get(Calendar.YEAR);
                                                String timePrint = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "." + cal.get(Calendar.MILLISECOND) + " " + cal.getTimeZone().getID();

                                                heart_rate_data_struct Heart_rate_data_struct = new heart_rate_data_struct(datePrint, timePrint, heartRate.getString("heartRate"));
                                                Heart_rate_array_adapter.add(Heart_rate_data_struct);
                                                lv_heartRate.setAdapter(Heart_rate_array_adapter);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    catch (JSONException e1) {
                                        h_linearLayout.setVisibility(View.INVISIBLE);
                                        tv_nickName.setVisibility(View.INVISIBLE);

                                        message("We are sorry", strName + "\n\n" + message.getString("message"));
                                        e1.printStackTrace();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }


    public void message(String title, String message) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());
        builderSingle.setIcon(R.drawable.ic_kid_blue);
        builderSingle.setTitle(title);
        builderSingle.setMessage(message);

        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                f_home F_home = f_home.newInstance("param1", "param2");
                android.support.v4.app.FragmentManager manager = getFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.layout_for_fragment, F_home,
                        F_home.getTag());
                transaction.commit();

                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

}
