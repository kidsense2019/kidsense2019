package com.example.kidsense2019;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.example.kidsense2019.LoginLogoutRegistration.Guardian_SignIn;
import com.example.kidsense2019.LoginLogoutRegistration.f_kid_register;
import com.example.kidsense2019.LoginLogoutRegistration.f_partnership;
import com.example.kidsense2019.location.MapsActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Session session;
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

        session =new Session(MainActivity.this);
        session.saveIP();

        if(!session.loggedin()){
            logout();
        }
        else {
            if (savedInstanceState == null) {
                setTitle("Home");

                f_home F_home = f_home.newInstance("param1", "param2");
                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.layout_for_fragment, F_home,
                        F_home.getTag());
                transaction.commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void logout() {
        session.setLoggedIn(false);
        session.saveGuardianId(0);
        startActivity(new Intent(MainActivity.this, Guardian_SignIn.class));
        finish();
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
            f_home F_home = f_home.newInstance("param1", "param2");
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.layout_for_fragment, F_home,
                    F_home.getTag());
            transaction.commit();
        }
        else if (id == R.id.nav_register_kid) {
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
