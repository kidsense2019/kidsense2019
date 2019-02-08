package com.example.kidsense2019.guardian.location;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kidsense2019.general.Session;
import com.example.kidsense2019.general.connection.GetDataTask;
import com.example.kidsense2019.guardian.Guardian_MainActivity;
import com.example.kidsense2019.R;
import com.example.kidsense2019.guardian.Session_Guardian;
import com.example.kidsense2019.general.connection.PostDataTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.example.kidsense2019.general.fcmObjects.CustomInfoWindowAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat, lng, latBound, lngBound;
    private String sdId = "Surabaya", stmp = "undefined", snippet = "", slat = "-7.265237", slng = "112.7472288";
    private ImageView mInfo;
    private Marker mMarker;
    private Geocoder geocoder;
    private List<Address> addresses;
    private Session_Guardian session_guardian;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setTitle("Watch Location");

        Intent intent = getIntent();
        String source = intent.getStringExtra("content");

        if (source.equals("Location")) {
            sdId = intent.getStringExtra("name");
            slat = intent.getStringExtra("latitude");
            slng = intent.getStringExtra("longitude");
            stmp = intent.getStringExtra("timestamp");
        }

        try {
            lat = Double.parseDouble(slat);
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            lng = Double.parseDouble(slng);
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // to make sure that we need both lat and lng
        if (lat == 0.0) {
            lng = 0.0;
        }
        else if (lng == 0.0) {
            lat = 0.0;
        }

        System.out.println("lat : " + lat + " lng : " + lng);

        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!addresses.isEmpty()) {
            String address = addresses.get(0).getAddressLine(0);
            String postalCode = addresses.get(0).getPostalCode();
            String phone = addresses.get(0).getPhone();

            String phoneSnippet = null;
            if (phone == null) {
                phoneSnippet = "-"; }
            else {
                phoneSnippet = phone; }

            snippet = "Address : " + address + "\n"
                    + "Postal Code : " + postalCode + "\n"
                    + "Lat, Lng : " + lat + ", " + lng + "\n"
                    + "Phone : " + phoneSnippet + "\n"
                    + "When : " + stmp;
        }
        else {
            errorMessage2("We can't find this location");
        }

        System.out.println("lat : " + lat);
        System.out.println("lng : " + lng);

        mInfo = (ImageView)findViewById(R.id.place_info);

        session = new Session(MapsActivity.this);
        session_guardian = new Session_Guardian(MapsActivity.this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // set the default map type

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions // here to request the missing permissions, and then overriding //   public void onRequestPermissionsResult(int requestCode, String[] permissions, // int[] grantResults) // to handle the case where the user grants the permission. See the documentation // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // set the location
        LatLng wLocation = new LatLng(lat, lng);

        // add marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(wLocation).title(sdId).snippet(snippet);
        mMarker = mMap.addMarker(markerOptions);

        // set custom info window
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        // move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wLocation,18));

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    }
                    else {
                        mMarker.showInfoWindow();
                    }
                }catch (NullPointerException e) {
                    System.out.println("onClick : NullPointerException : " + e.getMessage());
                }
            }
        });

        // set boundary
        final EditText editText = (EditText) findViewById(R.id.EditMapLocation);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    try {
                        geoLocate();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsActivity.this, " Long-press to set the boundary here", Toast.LENGTH_LONG).show();
            }
        });

        // set boundary
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                getKid(session.getIP() + "/v1/kid/admin/" + session_guardian.getGuardianId(),
                        "bound_infoWindow", session.getIP() + "/v1/sensorLocation/bound");
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.nav_refresh:
                getKid(session.getIP() + "/v1/kid/user/" + session_guardian.getGuardianId(),
                        "refresh", session.getIP() + "/v1/sensorLocation/request");
                break;
            case R.id.nav_periodic_set:
                getKid(session.getIP() + "/v1/kid/admin/" + session_guardian.getGuardianId(),
                        "periodic set", session.getIP() + "/v1/sensorLocation/state");
                break;
            case R.id.nav_periodic_unset:
                getKid(session.getIP() + "/v1/kid/admin/" + session_guardian.getGuardianId(),
                        "periodic unset", session.getIP() + "/v1/sensorLocation/state");
                break;
            case R.id.nav_bound_unset:
                getKid(session.getIP() + "/v1/kid/admin/" + session_guardian.getGuardianId(),
                        "bound unset", session.getIP() + "/v1/sensorLocation/state");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getKid(String UrlGet, final String nav_message, final String urlPost) {

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.select_dialog_item);
        GetDataTask get = new GetDataTask(MapsActivity.this);
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

                        if (nav_message.equals("bound_search")) {
                            kidListBound(arrayAdapter, urlPost, latBound, lngBound); // post data
                        }
                        else if (nav_message.equals("bound_infoWindow")) {
                            kidListBound(arrayAdapter, urlPost, lat, lng); // post data
                        }
                        else {
                            kidList(arrayAdapter, nav_message, urlPost); // post data
                        }
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

    public void kidList(final ArrayAdapter<String> arrayAdapter, final String message , final String url) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapsActivity.this);
        builderSingle.setIcon(R.drawable.ic_kid_blue);
        builderSingle.setTitle("Select One Name:-");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(MapsActivity.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Kid is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                        PostDataTask post = new PostDataTask(MapsActivity.this);
                        JSONObject dataToSend = new JSONObject();

                        if (message.equals("refresh")) {
                            try {
                                dataToSend.put("nickName", strName);
                                dataToSend.put("guardianId", session_guardian.getGuardianId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (message.equals("periodic set")) {
                            try {
                                dataToSend.put("nickName", strName);
                                dataToSend.put("message", message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (message.equals("periodic unset")) {
                            try {
                                dataToSend.put("nickName", strName);
                                dataToSend.put("message", message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (message.equals("bound unset")) {
                            try {
                                dataToSend.put("nickName", strName);
                                dataToSend.put("message", message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        post.execute(url,dataToSend);
                        post.getValue(new PostDataTask.setValue() {
                            @Override
                            public void update(String vData) {
                                try {
                                    System.out.println("reply: "+vData);
                                    JSONObject message = new JSONObject(vData);
                                    Toast.makeText(MapsActivity.this,message.getString("message"),Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        System.out.println("Check This Value David : " + strName + " " + message);
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }


    public void kidListBound(final ArrayAdapter<String> arrayAdapter, final String url, final double lat, final double lng) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapsActivity.this);
        builderSingle.setIcon(R.drawable.ic_kid_blue);
        builderSingle.setTitle("Select One Name:-");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(MapsActivity.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Kid is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                        PostDataTask post = new PostDataTask(MapsActivity.this);
                        JSONObject dataToSend = new JSONObject();

                        try {
                            dataToSend.put("nickName", strName);
                            dataToSend.put("latitude", lat);
                            dataToSend.put("longitude", lng);
                            dataToSend.put("guardianId", session_guardian.getGuardianId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        post.execute(url,dataToSend);
                        post.getValue(new PostDataTask.setValue() {
                            @Override
                            public void update(String vData) {
                                try {
                                    System.out.println("reply: "+vData);
                                    JSONObject message = new JSONObject(vData);

                                    if (message.getString("message").equals("Your request has been sent successfully")) {
                                        message("Location Bound", message.getString("message"));
                                    }
                                    else {
                                        message("We are sorry", message.getString("message"));
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
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapsActivity.this);
        builderSingle.setIcon(R.drawable.ic_kid_blue);
        builderSingle.setTitle(title);
        builderSingle.setMessage(message);

        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

    public void errorMessage2(String message) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapsActivity.this);
        builderSingle.setIcon(R.drawable.ic_kid_blue);
        builderSingle.setTitle("We are sorry");
        builderSingle.setMessage(message);

        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
                Intent intent = new Intent(MapsActivity.this, Guardian_MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        builderSingle.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Guardian_MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void geoLocate() throws IOException {

        EditText editLocation = (EditText) findViewById(R.id.EditMapLocation);
        String location = editLocation.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        if (!list.isEmpty()) {
            Address address = list.get(0);

//            String addressLine = address.getAddressLine(0);
//            Toast.makeText(this, "The boundary has been set at : " + addressLine, Toast.LENGTH_LONG).show();

            getKid(session.getIP() + "/v1/kid/admin/" + session_guardian.getGuardianId(),
                    "bound_search", session.getIP() + "/v1/sensorLocation/bound");

            latBound = address.getLatitude();
            lngBound = address.getLongitude();
        }
        else {
            message("We are sorry","We can't find this location");
        }
    }
}