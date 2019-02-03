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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class Guardian_SignUp extends Activity {

    private Button signUp;
    private EditText email, name, password, confPassword;
    private TextView signIn_page;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian__sign_up);

        session = new Session(Guardian_SignUp.this);

        signUp = (Button)findViewById(R.id.guardian_signUp);
        signIn_page = (TextView)findViewById(R.id.signIn_page);
        email = (EditText) findViewById(R.id.email_signUp);
        name = (EditText)findViewById(R.id.name_signUp);
        password = (EditText)findViewById(R.id.password_signUp);
        confPassword = (EditText)findViewById(R.id.conf_password_signUp);

        signUp.setOnClickListener(signUpOnClickListener);
        signIn_page.setOnClickListener(SignInPageOnClickListener);
    }

    Button.OnClickListener signUpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String emailStr = email.getText().toString();
            String nameStr = name.getText().toString();
            String passwordStr = password.getText().toString();
            String confPasswordStr = confPassword.getText().toString();

            boolean valid = isValid(emailStr, nameStr, passwordStr, confPasswordStr);

            if (valid) {
                PostDataTask post = new PostDataTask(Guardian_SignUp.this);
                JSONObject dataToSend = new JSONObject();

                try {
                    dataToSend.put("email", emailStr);
                    dataToSend.put("name", nameStr);
                    dataToSend.put("password", passwordStr);
                    dataToSend.put("fcmClientToken", session.getFCM());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                post.execute(session.getIP() + "/v1/guardian/signUp",dataToSend);
                post.getValue(new PostDataTask.setValue() {
                    @Override
                    public void update(String vData) {

                        try {
                            System.out.println("reply: "+vData);
                            JSONObject message = new JSONObject(vData);
                            String messageStr = message.getString("message");

                            if (messageStr.equals("Thanks! You have successfully signed up")) {
                                session.saveGuardianId(message.getInt("guardianId"));
                                session.saveGuardianEmail(message.getString("email"));
                                session.saveGuardianName(message.getString("name"));
                                session.setLoggedIn(true);

                                Toast.makeText(Guardian_SignUp.this, messageStr ,Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Guardian_SignUp.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else {
                                Toast.makeText(Guardian_SignUp.this, messageStr ,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };


    TextView.OnClickListener SignInPageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startActivity(new Intent(Guardian_SignUp.this, Guardian_SignIn.class));
            finish();
        }
    };

    public boolean isValid(String emailStr, String nameStr, String passwordStr, String confPasswordStr) {

        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        boolean flag=true;

        if (!isEmailValid(emailStr )){
            email.setError("Please provide a valid email");
            flag=false;
        }
        if (nameStr.trim().isEmpty()) {
            name.setError("Please fill out your name");
            flag=false;
        }
        if (passwordStr.length() < 5) {
            password.setError("Password must be at least 5 characters long");
            flag=false;
        }
        else if (!specailCharPatten.matcher(passwordStr).find()) {
            password.setError("Password must have at least one specail character");
            flag=false;
        }
        else if (!UpperCasePatten.matcher(passwordStr).find()) {
            password.setError("Password must have at least one uppercase character");
            flag=false;
        }
        else if (!lowerCasePatten.matcher(passwordStr).find()) {
            password.setError("Password must have atleast one lowercase character");
            flag=false;
        }
        else if (!digitCasePatten.matcher(passwordStr).find()) {
            password.setError("Password must have atleast one digit number");
            flag=false;
        }
        if (!passwordStr.equals(confPasswordStr)) {
            confPassword.setError("Password and confirm password do not match");
            flag=false;
        }

        return flag;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
