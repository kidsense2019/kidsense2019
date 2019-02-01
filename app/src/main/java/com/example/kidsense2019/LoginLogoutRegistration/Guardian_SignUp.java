package com.example.kidsense2019.LoginLogoutRegistration;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kidsense2019.MainActivity;
import com.example.kidsense2019.R;
import com.example.kidsense2019.Session;

public class Guardian_SignUp extends Activity {

    private Button signUp;
    private EditText email, password;
    private TextView signIn_page;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian__sign_up);

        session = new Session(Guardian_SignUp.this);

        signUp = (Button)findViewById(R.id.guardian_signUp);
        signIn_page = (TextView)findViewById(R.id.signIn_page);

        signUp.setOnClickListener(signUpOnClickListener);
        signIn_page.setOnClickListener(SignInPageOnClickListener);
    }

    Button.OnClickListener signUpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {



            Intent intent = new Intent(Guardian_SignUp.this, Guardian_SignIn.class);
            startActivity(intent);
            finish();

        }
    };

    TextView.OnClickListener SignInPageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startActivity(new Intent(Guardian_SignUp.this, Guardian_SignIn.class));
            finish();
        }
    };


}
