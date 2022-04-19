package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_hola.ObjectForApp.Content;
import com.example.app_hola.ObjectForApp.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {
    GoogleMap map;
    Location location;
    EditText edit;
    Button btnSearch, btnOK;
    String status;

    int REQUEST_CODE_TAKE_LOCATION=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.myMap);
        mapFragment.getMapAsync(this);
        View view = mapFragment.getView();
        view.setClickable(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        location = (Location) intent.getSerializableExtra("location");
        status = intent.getStringExtra("status");
        if(status.equals("read") && !location.getName().equals(""))
            actionBar.setTitle(location.getName());
        edit = (EditText) findViewById(R.id.edit);
        btnSearch= (Button) findViewById(R.id.btn_search);
        btnOK =  (Button) findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                intent1.putExtra("location",location);
                setResult(RESULT_OK,intent1);
                finish();
            }
        });
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH)
                {
                    List<Address> listAddress;
                    Geocoder geocoder = new Geocoder(GoogleMapActivity.this);
                    if(edit.getText().toString().replace(" ","").isEmpty())
                        return false;
                    else {
                        try {
                            listAddress = geocoder.getFromLocationName(edit.getText().toString(), 1);
                            LatLng address = new LatLng(listAddress.get(0).getLatitude(), listAddress.get(0).getLongitude());
                            location.setLatitude(listAddress.get(0).getLatitude());
                            location.setLongitude(listAddress.get(0).getLongitude());
                            Toast.makeText(GoogleMapActivity.this, listAddress.get(0).getLatitude() + " " +
                                    listAddress.get(0).getLongitude(), Toast.LENGTH_LONG).show();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 15));
                            MarkerOptions marker = new MarkerOptions().position(address).draggable(true);
                            map.addMarker(marker);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Address> listAddress;
                Geocoder geocoder = new Geocoder(GoogleMapActivity.this);
                if(edit.getText().toString().replace(" ","").isEmpty())
                    return;
                else {
                    try {
                        listAddress = geocoder.getFromLocationName(edit.getText().toString(), 1);
                        LatLng address = new LatLng(listAddress.get(0).getLatitude(), listAddress.get(0).getLongitude());
                        location.setLatitude(listAddress.get(0).getLatitude());
                        location.setLongitude(listAddress.get(0).getLongitude());
                        Toast.makeText(GoogleMapActivity.this, listAddress.get(0).getLatitude() + " " +
                                listAddress.get(0).getLongitude(), Toast.LENGTH_LONG).show();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 15));
                        MarkerOptions marker = new MarkerOptions().position(address).draggable(true);
                        map.addMarker(marker);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if(status.equals("read")) {
            LinearLayout ln = (LinearLayout) findViewById(R.id.linear);
            ln.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (status.equals("create")) {
            map.setOnMapClickListener(this::onMapClick);
            map.setOnMarkerDragListener(this);
            map.setOnMarkerClickListener(this::onMarkerClick);
        }
        if(location!=null)
        {
            LatLng address = new LatLng(location.getLatitude(),location.getLongitude());
            map.addMarker(new MarkerOptions().position(address)
            .draggable(status.equals("create")).title(location.getName()));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(address,17));
        }
        else
        {
            location = new Location();
            location.setLatitude(14.058);
            location.setLongitude(108.277);
            LatLng address = new LatLng(14.058,108.277);
            map.addMarker(new MarkerOptions().position(address)
                    .draggable(status.equals("create")).title(getResources().getString(R.string.viet_nam)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(address,6));
        }
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        location.setLatitude(marker.getPosition().latitude);
        location.setLongitude(marker.getPosition().longitude);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_name_location);
        dialog.show();
        EditText editName = (EditText) dialog.findViewById(R.id.edit_name);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editName.getText().toString().replace(" ","").isEmpty())
                {
                    location.setName(editName.getText().toString());
                    dialog.dismiss();
                    marker.setTitle(editName.getText().toString());
                }
            }
        });
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_name_location);
        dialog.show();
        EditText editName = (EditText) dialog.findViewById(R.id.edit_name);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editName.getText().toString().replace(" ","").isEmpty())
                {
                    location.setName(editName.getText().toString());
                    dialog.dismiss();
                    marker.setTitle(editName.getText().toString());
                }
            }
        });
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}