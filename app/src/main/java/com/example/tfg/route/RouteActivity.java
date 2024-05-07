package com.example.tfg.route;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.tfg.R;
import com.example.tfg.adminPanel.AdminPanelActivity;
import com.example.tfg.adminProfile.AdminProfileActivity;
import com.example.tfg.reportTemp.ReportTempActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RouteViewModel viewModel;
    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private TextView ubiTextView;
    private static final int REQUEST_CHECK_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ubiTextView = findViewById(R.id.ubiTextViewRoute);


        viewModel = new ViewModelProvider(this).get(RouteViewModel.class);

        BottomNavigationView navView = findViewById(R.id.adminNavigation);
        navView.setSelectedItemId(R.id.navigation_route);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_admin_panel) {
                    Intent intent = new Intent(RouteActivity.this, AdminPanelActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.navigation_add_temporal) {
                    Intent intent = new Intent(RouteActivity.this, ReportTempActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.navigation_route) {
                    return true;
                } else if (id == R.id.navigation_admin_profile) {
                    Intent intent = new Intent(RouteActivity.this, AdminProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                return false;
            }
        });

        viewModel.getLocationSettingResponse().observe(this, isLocationSettingSatisfied -> {
            if (Boolean.TRUE.equals(isLocationSettingSatisfied)) {
                getDeviceLocation();
            }
        });

        viewModel.getResolvableApiException().observe(this, resolvable -> {
            try {
                resolvable.startResolutionForResult(RouteActivity.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                sendEx.printStackTrace();
            }
        });
    }

    private BroadcastReceiver locationProviderChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    viewModel.checkLocationSettings(RouteActivity.this);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(locationProviderChangeReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        viewModel.checkLocationSettings(this);
        viewModel.checkAndRequestPermissions(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationProviderChangeReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
            viewModel.checkLocationSettings(this);

        } else {
            viewModel.checkAndRequestPermissions(this);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear(); // Limpiar marcadores anteriores
                mMap.addMarker(new MarkerOptions().position(latLng)); // Agregar marcador en la ubicación del clic
                selectedLatLng = latLng; // Guardar las coordenadas seleccionadas
                showAddressFromLocation(selectedLatLng.latitude, selectedLatLng.longitude); // Mostrar dirección correspondiente a las coordenadas
            }
        });
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null && mMap != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f));
                    Log.d("Zoom", "Haciendo zoom");
                }
            }
        });
    }

    // Mostrar la dirección correspondiente a las coordenadas en el TextView
    private void showAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressBuilder = new StringBuilder();

                // Obtener la calle y el número de la dirección
                if (address.getThoroughfare() != null) {
                    addressBuilder.append(address.getThoroughfare());
                    if (address.getSubThoroughfare() != null) {
                        addressBuilder.append(", ").append(address.getSubThoroughfare());
                    }
                }

                // Obtener la localidad de la dirección
                if (address.getLocality() != null) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(", ");
                    }
                    addressBuilder.append(address.getLocality());
                }

                // Mostrar la dirección en el TextView
                ubiTextView.setText(addressBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
