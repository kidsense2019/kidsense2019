package com.example.kidsense2019.guardian;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.kidsense2019.R;
import com.example.kidsense2019.signIn;
import com.example.kidsense2019.guardian.SignupRegister.f_kid_register;
import com.example.kidsense2019.guardian.SignupRegister.f_partnership;
import com.example.kidsense2019.connection.PutDataTask;
import com.example.kidsense2019.guardian.location.MapsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Guardian_MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Session_Guardian session_guardian;
    TextView headerName, headerEmail;
    ImageView profilePicture;
    String profilePicturePath;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        session_guardian =new Session_Guardian(Guardian_MainActivity.this);
        session_guardian.saveIP();

        if(!session_guardian.loggedin()){
            logout();
        }
        else {
            if (savedInstanceState == null) {
                setTitle("Home");

                View header = navigationView.getHeaderView(0);

                headerName = (TextView)header.findViewById(R.id.header_name);
                headerEmail = (TextView)header.findViewById(R.id.header_email);
                headerName.setText(session_guardian.getGuardianName());
                headerEmail.setText(session_guardian.getGuardianEmail());

                profilePicture = (ImageView)header.findViewById(R.id.header_profile_picture);
                profilePicture.setOnClickListener(profilePictureOnClickListener);
                loadImageFromStorage(session_guardian.getProfilePicturePath());

                f_home F_home = f_home.newInstance("param1", "param2");
                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.layout_for_fragment, F_home,
                        F_home.getTag());
                transaction.commit();
            }
        }
    }

    public void logout() {
        PutDataTask put = new PutDataTask(Guardian_MainActivity.this);
        JSONObject dataToSend = new JSONObject();

        try {
            dataToSend.put("fcmClientToken", "N/A");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        put.execute(session_guardian.getIP() + "/v1/guardian/" + session_guardian.getGuardianId() ,dataToSend);
        put.getValue(new PutDataTask.setValue() {
            @Override
            public void update(String vData) {

                try {
                    JSONObject message = new JSONObject(vData);
                    String messageStr = message.getString("message");

                    if (messageStr.equals("FCM client token has been successfully updated")) {

                        session_guardian.setLoggedIn(false);
                        session_guardian.saveGuardianId(0);
                        session_guardian.saveGuardianEmail(null);
                        session_guardian.saveGuardianName(null);
                        deleteImageFromStorage(session_guardian.getProfilePicturePath());
                        startActivity(new Intent(Guardian_MainActivity.this, signIn.class));
                        finish();
                    }
                    else {
                        Toast.makeText(Guardian_MainActivity.this, messageStr ,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    ImageView.OnClickListener profilePictureOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            try {
                if (data != null) {
                    InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    profilePicturePath = saveToInternalStorage(bitmap);
                    session_guardian.saveProfilePicturePath(profilePicturePath);
                    loadImageFromStorage(profilePicturePath);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path)
    {
        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            profilePicture.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private void deleteImageFromStorage (String path) {
        File f =new File(path, "profile.jpg");
        f.delete();
        session_guardian.saveProfilePicturePath(null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the f_home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setTitle("Home");

            f_home F_home = f_home.newInstance("param1", "param2");
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.layout_for_fragment, F_home,
                    F_home.getTag());
            transaction.commit();
        }
        else if (id == R.id.nav_kid_register) {
            setTitle("Kid Register");

            f_kid_register F_kid_register = f_kid_register.newInstance("Test", "Test");
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.layout_for_fragment, F_kid_register,
                    F_kid_register.getTag());
            transaction.commit();

        } else if (id == R.id.nav_watch_location) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("content", "nav_watch_location");
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_partner) {
            setTitle("Partnership");

            f_partnership F_partnership = f_partnership.newInstance("param1", "param2");
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.layout_for_fragment, F_partnership,
                    F_partnership.getTag());
            transaction.commit();
        } else if (id == R.id.signOut) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
