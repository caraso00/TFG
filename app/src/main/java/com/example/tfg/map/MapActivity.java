package com.example.tfg.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tfg.R;
import com.example.tfg.register.RegisterActivity;
import com.example.tfg.ui.login.LoginActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private MapActivityViewModel viewModel;

    private Spinner distanceSpinner;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final int REQUEST_CHECK_SETTINGS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Inicializamos el ViewModel
        viewModel = new ViewModelProvider(this).get(MapActivityViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Llamada al ViewModel para pedir permisos
        viewModel.checkAndRequestPermissions(this);

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    return true;
                } else if (id == R.id.navigation_add) {
                    return true;
                } else if (id == R.id.navigation_avatar) {
                    return true;
                }
                return false;
            }
        });

        BottomNavigationView top_navView = findViewById(R.id.top_navigation);
        top_navView.setSelectedItemId(R.id.invisible);

        top_navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_back) {
                    Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                return false;
            }
        });

        // Inicializar Spinner
        distanceSpinner = findViewById(R.id.spinner);
        // Observamos el LiveData para la configuración de ubicación
        viewModel.getLocationSettingResponse().observe(this, isLocationSettingSatisfied -> {
            if (Boolean.TRUE.equals(isLocationSettingSatisfied)) {
                onLocationActivated();
            } else {
                onLocationDeactivated();
            }
        });

        // Observamos el LiveData para excepciones resolubles
        viewModel.getResolvableApiException().observe(this, resolvable -> {
            try {
                resolvable.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                onLocationDeactivated();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                // enable location layer if permission granted
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    private BroadcastReceiver locationProviderChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    viewModel.checkLocationSettings(MapActivity.this);
                } else {
                    onLocationDeactivated();
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
            viewModel.checkLocationSettings(this);
        } else {
            viewModel.checkAndRequestPermissions(this);
        }
    }

    // Cuando se active la ubicación, se habilita el Spinner:
    public void onLocationActivated() {
        distanceSpinner.setEnabled(true);
        distanceSpinner.setAlpha(1.0f); // Esto hará que se vea "normal".
    }

    // Y cuando se desactive la ubicación, se deshabilita el Spinner:
    public void onLocationDeactivated() {
        distanceSpinner.setEnabled(false);
        distanceSpinner.setAlpha(0.5f); // Esto hará que se vea más "oscuro" o "apagado".
    }
}