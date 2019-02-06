package com.example.kidsense2019.general;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kidsense2019.R;
import com.example.kidsense2019.guardian.SignupRegister.Guardian_SignUp;
import com.example.kidsense2019.guardian.Guardian_MainActivity;
import com.example.kidsense2019.guardian.Session_Guardian;
import com.example.kidsense2019.general.connection.PostDataTask;
import com.example.kidsense2019.general.connection.PutDataTask;
import com.example.kidsense2019.kid.Kid_MainActivity;
import com.example.kidsense2019.kid.Session_Kid;

import org.json.JSONException;
import org.json.JSONObject;

public class signIn extends Activity {

    private Button signInKid, signInReq, signInGuardian;
    private EditText email, passwordGuardian, passwordKid;
    private TextView signUp_page, emailSignInTv;
    private Session_Guardian session_guardian;
    private Session_Kid session_kid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__sign_in);

        session_guardian = new Session_Guardian(signIn.this);
        session_kid = new Session_Kid(signIn.this);
        session_guardian.saveIP();

        emailSignInTv = (TextView)findViewById(R.id.email_singIn_tv);
        signInReq = (Button)findViewById(R.id.signIn_req);
        signInKid = (Button)findViewById(R.id.signIn_Kid);
        signInGuardian = (Button)findViewById(R.id.signIn_Guardian);
        email = (EditText)findViewById(R.id.email_SignIn);
        passwordGuardian = (EditText)findViewById(R.id.password_signIn_guardian);
        passwordKid = (EditText)findViewById(R.id.password_signIn_kid);
        signUp_page = (TextView)findViewById(R.id.signUp_page);

        signInReq.setOnClickListener(signInReqOnClickListener);
        signInKid.setOnClickListener(signInKidOnClickListener);
        signInGuardian.setOnClickListener(signInGuardianOnClickListener);
        signUp_page.setOnClickListener(SignUpPageOnClickListener);

        if(session_guardian.loggedin()) {
            System.out.println("guardian log In");
            startActivity(new Intent(signIn.this, Guardian_MainActivity.class));
            finish();
        }
        else if (session_kid.loggedin()) {
            System.out.println("Kid log In");
            startActivity(new Intent(signIn.this, Kid_MainActivity.class));
            finish();
        }

    }

    Button.OnClickListener signInReqOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String emailStr = email.getText().toString();

            if (emailStr.trim().isEmpty()) {
                email.setError("Please fill out your email or nickname");
            }
            else {
                if (!isEmailValid(emailStr )){
                    //then this is kid signIn
                    PostDataTask post = new PostDataTask(signIn.this);
                    JSONObject dataToSend = new JSONObject();

                    try {
                        dataToSend.put("nickName", emailStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    post.execute(session_guardian.getIP() + "/v1/kid/signIn/authReq",dataToSend);
                    post.getValue(new PostDataTask.setValue() {
                        @Override
                        public void update(String vData) {

                            try {
                                System.out.println("reply: "+vData);
                                JSONObject message = new JSONObject(vData);
                                String messageStr = message.getString("message");

                                if (messageStr.equals("4 digits code has been sent to your guardian's device")) {
                                    Toast.makeText(signIn.this, messageStr ,Toast.LENGTH_LONG).show();

                                    email.setVisibility(View.INVISIBLE);
                                    signInReq.setEnabled(false);
                                    signInReq.setVisibility(View.INVISIBLE);

                                    emailSignInTv.setText("Password");
                                    passwordKid.setVisibility(View.VISIBLE);
                                    signInKid.setEnabled(true);
                                    signInKid.setVisibility(View.VISIBLE);
                                }
                                else {
                                    Toast.makeText(signIn.this, messageStr ,Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else {
                    //then this is guardian signIn
                    email.setVisibility(View.INVISIBLE);
                    signInReq.setEnabled(false);
                    signInReq.setVisibility(View.INVISIBLE);

                    emailSignInTv.setText("Password");
                    passwordGuardian.setVisibility(View.VISIBLE);
                    signInGuardian.setEnabled(true);
                    signInGuardian.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    Button.OnClickListener signInKidOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String emailStr = email.getText().toString();
            String passwordStr = passwordKid.getText().toString();

            if (emailStr.trim().isEmpty()) {
                email.setError("Please fill out your nickname");
            }
            else if (passwordStr.length() != 4) {
                passwordKid.setError("Please fill out the 4-digit code");
            }
            else {
                int passwordInt = Integer.parseInt(passwordStr);

                PostDataTask post = new PostDataTask(signIn.this);
                JSONObject dataToSend = new JSONObject();

                try {
                    dataToSend.put("nickName", emailStr);
                    dataToSend.put("passcode", passwordInt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                post.execute(session_guardian.getIP() + "/v1/kid/signIn/",dataToSend);
                post.getValue(new PostDataTask.setValue() {
                    @Override
                    public void update(String vData) {

                        try {
                            System.out.println("reply: "+vData);
                            JSONObject message = new JSONObject(vData);
                            String messageStr = message.getString("message");

                            if (messageStr.equals("Auth successful")) {
                                session_kid.saveKidNickname(message.getString("nickName"));
                                session_kid.saveKidFullname(message.getString("fullName"));
                                session_kid.saveKidId(message.getInt("kidId"));

                                updateFCMToken(session_guardian.getIP() + "/v1/kid/" + session_kid.getKidId(), "kid");
                            }
                            else {
                                Toast.makeText(signIn.this, messageStr ,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    Button.OnClickListener signInGuardianOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String emailStr = email.getText().toString();
            String passwordStr = passwordGuardian.getText().toString();

            if (passwordStr.length() <= 5) {
                passwordGuardian.setError("Kidsense App has a minimum of 5-digit password");
            }
            else {
                PostDataTask post = new PostDataTask(signIn.this);
                JSONObject dataToSend = new JSONObject();

                try {
                    dataToSend.put("email", emailStr);
                    dataToSend.put("password", passwordStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                post.execute(session_guardian.getIP() + "/v1/guardian/signIn",dataToSend);
                post.getValue(new PostDataTask.setValue() {
                    @Override
                    public void update(String vData) {

                        try {
                            System.out.println("reply: "+vData);
                            JSONObject message = new JSONObject(vData);
                            String messageStr = message.getString("message");

                            if (messageStr.equals("Auth successful")) {
                                session_guardian.saveGuardianId(message.getInt("guardianId"));
                                session_guardian.saveGuardianEmail(message.getString("email"));
                                session_guardian.saveGuardianName(message.getString("name"));

                                updateFCMToken(session_guardian.getIP() + "/v1/guardian/" + session_guardian.getGuardianId(), "guardian");
                            }
                            else {
                                Toast.makeText(signIn.this, messageStr ,Toast.LENGTH_LONG).show();

                                email.setVisibility(View.VISIBLE);
                                signInReq.setEnabled(true);
                                signInReq.setVisibility(View.VISIBLE);

                                emailSignInTv.setText("Email / Nickname");
                                passwordGuardian.setVisibility(View.INVISIBLE);
                                passwordGuardian.getText().clear();
                                signInGuardian.setEnabled(false);
                                signInGuardian.setVisibility(View.INVISIBLE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    TextView.OnClickListener SignUpPageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startActivity(new Intent(signIn.this, Guardian_SignUp.class));
            finish();
        }
    };

    public void updateFCMToken(String url, final String loginType) {

        PutDataTask put = new PutDataTask(signIn.this);
        JSONObject dataToSend = new JSONObject();

        try {
            dataToSend.put("fcmClientToken", session_guardian.getFCM());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        put.execute(url ,dataToSend);
        put.getValue(new PutDataTask.setValue() {
            @Override
            public void update(String vData) {

                try {
                    JSONObject message = new JSONObject(vData);
                    String messageStr = message.getString("message");

                    if (messageStr.equals("FCM client token has been successfully updated")) {

                        if (loginType.equals("guardian")) {
                            session_guardian.setLoggedIn(true);
                            Intent intent = new Intent(signIn.this, Guardian_MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            session_kid.setLoggedIn(true);
                            Intent intent = new Intent(signIn.this, Kid_MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                    else {
                        Toast.makeText(signIn.this, messageStr ,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
