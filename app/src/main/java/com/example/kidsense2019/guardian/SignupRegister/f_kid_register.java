package com.example.kidsense2019.guardian.SignupRegister;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.kidsense2019.R;
import com.example.kidsense2019.guardian.Session_Guardian;
import com.example.kidsense2019.general.connection.PostDataTask;
import com.example.kidsense2019.guardian.f_home;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link f_kid_register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class f_kid_register extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private EditText nickName, fullName, bornDate, weight, height;
    private Button register;
    private Calendar myCalendar;
    private long timestamp = 0;
    private RadioButton genderMale;
    private int selectedGender;
    private int heightInt, weightInt;
    Session_Guardian session_guardian;

    public f_kid_register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment f_kid_register.
     */
    // TODO: Rename and change types and number of parameters
    public static f_kid_register newInstance(String param1, String param2) {
        f_kid_register fragment = new f_kid_register();
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
        view = inflater.inflate(R.layout.fragment_f_kid_register, container, false);

        session_guardian = new Session_Guardian(view.getContext());

        nickName = (EditText)view.findViewById(R.id.register_nickName);
        fullName = (EditText)view.findViewById(R.id.register_fullname);
        bornDate = (EditText)view.findViewById(R.id.register_bornDate);
        weight = (EditText)view.findViewById(R.id.register_weight);
        height = (EditText)view.findViewById(R.id.register_height);

        genderMale = (RadioButton) view.findViewById(R.id.register_gender_male);

        register = (Button)view.findViewById(R.id.kid_signUp);
        register.setOnClickListener(registerOnClickListener);

        // set and get born date
        bornDate.setKeyListener(null);
        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

                long millistimeStamp = myCalendar.getTimeInMillis();
                timestamp = TimeUnit.MILLISECONDS.toSeconds(millistimeStamp);
            }
        };
        bornDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(view.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return view;
    }

    Button.OnClickListener registerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String nickNameStr = nickName.getText().toString();
            String fullNameStr = fullName.getText().toString();
            String weightStr = weight.getText().toString();
            String heightStr = height.getText().toString();

            selectedGender = 0;
            if (genderMale.isChecked()) {
                selectedGender = 1;
            }

            boolean valid = isValid(nickNameStr, fullNameStr, weightStr, heightStr);

            if (valid) {
                PostDataTask post = new PostDataTask(view.getContext());
                JSONObject dataToSend = new JSONObject();

                try {
                    dataToSend.put("fullName", fullNameStr);
                    dataToSend.put("bornDate", timestamp);
                    dataToSend.put("weight", weightInt);
                    dataToSend.put("height", heightInt);
                    dataToSend.put("gender", selectedGender);
                    dataToSend.put("guardianId", session_guardian.getGuardianId());
                    dataToSend.put("nickName", nickNameStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                post.execute(session_guardian.getIP() + "/v1/kid/signUp/",dataToSend);
                post.getValue(new PostDataTask.setValue() {
                    @Override
                    public void update(String vData) {

                        try {
                            System.out.println("reply: "+vData);
                            JSONObject message = new JSONObject(vData);
                            String messageStr = message.getString("message");

                            if (messageStr.equals("New kid has been created")) {
                                newRegister(messageStr);
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

    public void newRegister(String message) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());
        builderSingle.setIcon(R.drawable.ic_kid_blue);
        builderSingle.setTitle(message);
        builderSingle.setMessage("Do you want to register another kid?");

        builderSingle.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().setTitle("Home");

                f_home F_home = f_home.newInstance("param1", "param2");
                android.support.v4.app.FragmentManager manager = getFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.layout_for_fragment, F_home,
                        F_home.getTag());
                transaction.commit();

                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                nickName.getText().clear();
                fullName.getText().clear();
                bornDate.getText().clear();
                weight.getText().clear();
                height.getText().clear();
                genderMale.setChecked(true);

                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    public boolean isValid(String nickNameStr, String fullNameStr, String weightStr, String heightStr) {

        boolean flag=true;
        Date currentDate= Calendar.getInstance().getTime();

        if (nickNameStr.trim().isEmpty()) {
            nickName.setError("Please fill out the nickname");
            flag=false;
        }
        if (fullNameStr.trim().isEmpty()) {
            fullName.setError("Please fill out your kid's fullname");
            flag=false;
        }

        if (timestamp == 0) {
            bornDate.setError("Please fill out your kid's born date");
            flag=false;
        }
        else if (currentDate.before(myCalendar.getTime())) {
            bornDate.setError("This kid's born date is not realistic");
            flag=false;
        }
        else {
            bornDate.setError(null);
        }

        if (weightStr.trim().isEmpty()) {
            weight.setError("Please fill out your kid's wight");
            flag=false;
        }
        else {
            weightInt = Integer.parseInt(weightStr);
            if (weightInt <= 0) {
                weight.setError("Your kid's wight must be more than 0 kg");
                flag=false;
            }
        }

        if (heightStr.trim().isEmpty()) {
            height.setError("Please fill out your kid's height");
            flag=false;
        }
        else {
            heightInt = Integer.parseInt(heightStr);
            if (heightInt <= 0) {
                height.setError("Your kid's height must be more than 0 m");
                flag=false;
            }
        }

        return flag;
    }

    private void updateLabel() {
        String myFormat = "EEEE, dd MMMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        bornDate.setText(sdf.format(myCalendar.getTime()));
    }
}
