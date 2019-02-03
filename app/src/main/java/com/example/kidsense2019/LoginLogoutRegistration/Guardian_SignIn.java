package com.example.kidsense2019.LoginLogoutRegistration;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kidsense2019.MainActivity;
import com.example.kidsense2019.R;
import com.example.kidsense2019.Session;
import com.example.kidsense2019.connection.PostDataTask;
import com.example.kidsense2019.connection.PutDataTask;

import org.json.JSONException;
import org.json.JSONObject;

public class Guardian_SignIn extends Activity {

    private Button signIn;
    private EditText email, password;
    private TextView signUp_page;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian__sign_in);

        session = new Session(Guardian_SignIn.this);
        session.saveIP();

        signIn = (Button)findViewById(R.id.guardian_signIn);
        email = (EditText)findViewById(R.id.email_SignIn);
        password = (EditText)findViewById(R.id.password_signIn);
        signUp_page = (TextView)findViewById(R.id.signUp_page);

        signIn.setOnClickListener(signInOnClickListener);
        signUp_page.setOnClickListener(SignUpPageOnClickListener);

        if(session.loggedin()) {
            startActivity(new Intent(Guardian_SignIn.this, MainActivity.class));
            finish();
        }

    }

    Button.OnClickListener signInOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();

            if (!isEmailValid(emailStr )){
                email.setError("Invalid Email");
                Toast.makeText(Guardian_SignIn.this, "Please provide a valid email" ,Toast.LENGTH_LONG).show();
            }
            else if (passwordStr.length() <= 5) {
                password.setError("Invalid Password");
                Toast toast = Toast.makeText(Guardian_SignIn.this, "Kidsense App has a minimum of 5-digit password" ,Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                PostDataTask post = new PostDataTask(Guardian_SignIn.this);
                JSONObject dataToSend = new JSONObject();

                try {
                    dataToSend.put("email", emailStr);
                    dataToSend.put("password", passwordStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                post.execute(session.getIP() + "/v1/guardian/signIn",dataToSend);
                post.getValue(new PostDataTask.setValue() {
                    @Override
                    public void update(String vData) {

                        try {
                            System.out.println("reply: "+vData);
                            JSONObject message = new JSONObject(vData);
                            String messageStr = message.getString("message");

                            if (messageStr.equals("Auth successful")) {
                                session.saveGuardianId(message.getInt("guardianId"));
                                session.saveGuardianEmail(message.getString("email"));
                                session.saveGuardianName(message.getString("name"));

                                updateFCMToken();
                            }
                            else {
                                Toast.makeText(Guardian_SignIn.this, messageStr ,Toast.LENGTH_LONG).show();
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

            startActivity(new Intent(Guardian_SignIn.this, Guardian_SignUp.class));
            finish();
        }
    };

    public void updateFCMToken() {

        PutDataTask put = new PutDataTask(Guardian_SignIn.this);
        JSONObject dataToSend = new JSONObject();

        try {
            dataToSend.put("fcmClientToken", session.getFCM());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        put.execute(session.getIP() + "/v1/guardian/" + session.getGuardianId() ,dataToSend);
        put.getValue(new PutDataTask.setValue() {
            @Override
            public void update(String vData) {

                try {
                    JSONObject message = new JSONObject(vData);
                    String messageStr = message.getString("message");

                    if (messageStr.equals("FCM client token has been successfully updated")) {

                        session.setLoggedIn(true);
                        Intent intent = new Intent(Guardian_SignIn.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(Guardian_SignIn.this, messageStr ,Toast.LENGTH_LONG).show();
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
