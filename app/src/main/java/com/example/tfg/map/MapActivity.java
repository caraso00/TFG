package com.example.tfg.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import com.example.tfg.R;
import com.example.tfg.profile.ProfileActivity;
import com.example.tfg.reportAdd.ReportActivity;
import com.example.tfg.ui.login.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Arrays;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private MapActivityViewModel viewModel;

    private Spinner distanceSpinner;
    private MultiAutoCompleteTextView multiSelectionDropdown;
    private MultiSelectArrayAdapter adapter;
    private boolean[] selectedItems;
    private final String[] items = {"Orgánico", "Papel y cartón", "Vidrio", "Papelera"};

    private static final int REQUEST_CHECK_SETTINGS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Inicializamos el ViewModel
        viewModel = new ViewModelProvider(this).get(MapActivityViewModel.class);

        // Llamada al ViewModel para pedir permisos
        viewModel.checkAndRequestPermissions(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    return true;
                } else if (id == R.id.navigation_add) {
                    Intent intent = new Intent(MapActivity.this, ReportActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else if (id == R.id.navigation_profile) {
                    Intent intent = new Intent(MapActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
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
                getDeviceLocation();
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

        selectedItems = new boolean[items.length];
        Arrays.fill(selectedItems, false);
        adapter = new MultiSelectArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items);
        multiSelectionDropdown = findViewById(R.id.multiSelectionDropdown);
        multiSelectionDropdown.setAdapter(adapter);
        multiSelectionDropdown.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        multiSelectionDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiChoiceDialog();
            }
        });
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
            getDeviceLocation();
            viewModel.checkLocationSettings(this);
        } else {
            viewModel.checkAndRequestPermissions(this);
        }
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f)); // Zoom de 15 (puedes ajustarlo según sea necesario)
                }
            }
        });
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

    private void showMultiChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elija los tipos:");

        builder.setMultiChoiceItems(items, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedItems[which] = isChecked; // Aquí actualizamos los ítems seleccionados
            }
        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selections = "";
                int countSelected = 0;
                int lastIndex = -1;
                for (int i = 0; i < items.length; i++) {
                    if (selectedItems[i]) {
                        countSelected++;
                        lastIndex = i;
                    }
                }

                if (countSelected == 0) {
                    selections = "";
                } else if (countSelected == 1) {
                    selections = items[lastIndex];
                } else {
                    selections = "Varios";
                }
                multiSelectionDropdown.setText(selections);
            }
        });

        builder.setNegativeButton("Cancelar", null);

        builder.show();
    }
}