package com.example.tfg.reportAdd;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tfg.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private Button acceptLocationButton;
    private TextView ubiTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ubiTextView = findViewById(R.id.ubiTextView);
        acceptLocationButton = findViewById(R.id.acceptLocationButton);

        // Configurar el botón de aceptar ubicación
        acceptLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLatLng != null) {
                    returnSelectedLocation(selectedLatLng.latitude, selectedLatLng.longitude);
                } else {
                    getDeviceLocationAndReturn();
                }
            }
        });

        // Obtener y mostrar la ubicación actual del usuario
        getDeviceLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Habilitar la capa de ubicación del usuario en el mapa
        mMap.setMyLocationEnabled(true);

        // Obtener la ubicación actual del usuario y centrar el mapa en esa ubicación
        getDeviceLocation();

        // Manejar el evento de clic en el mapa
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

    // Obtener la ubicación actual del usuario y centrar el mapa en esa ubicación
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f)); // Centrar la cámara del mapa en la ubicación del usuario
                    showAddressFromLocation(latLng.latitude, latLng.longitude); // Mostrar la dirección correspondiente a las coordenadas
                }
            }
        });
    }

    // Obtener la ubicación actual del usuario y devolverla
    private void getDeviceLocationAndReturn() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    returnSelectedLocation(latitude, longitude); // Devolver las coordenadas de la ubicación actual del usuario
                } else {
                    Toast.makeText(SelectLocationActivity.this, "No se pudo obtener la ubicación actual del usuario", Toast.LENGTH_SHORT).show();
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

    // Devolver las coordenadas de la ubicación seleccionada al ReportActivity
    private void returnSelectedLocation(double latitude, double longitude) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitude", latitude);
        resultIntent.putExtra("longitude", longitude);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}