package com.example.tfg.reportAdd;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.R;
import com.example.tfg.map.MapActivity;
import com.example.tfg.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private Button selectLocationButton;
    private Spinner typeSpinner;
    private Spinner statusSpinner;
    private ImageView addPhotoImageView;
    private Button acceptButton;
    private TextView ubiReportTextView;
    private int numberOfPhotos;
    private TextView imageCountTextView;



    private static final int REQUEST_LOCATION_PICK = 1;
    private static final int REQUEST_SELECT_PHOTOS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        selectLocationButton = findViewById(R.id.selectLocationButton);
        typeSpinner = findViewById(R.id.tipoSpinner);
        statusSpinner = findViewById(R.id.estadoSpinner);
        addPhotoImageView = findViewById(R.id.addPhotoImageView);
        acceptButton = findViewById(R.id.aceptarButton);
        ubiReportTextView = findViewById(R.id.ubiReportTextView);
        imageCountTextView = findViewById(R.id.imageCountTextView);

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setSelectedItemId(R.id.navigation_add);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(ReportActivity.this, MapActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.navigation_add) {
                    return true;
                } else if (id == R.id.navigation_profile) {
                    Intent intent = new Intent(ReportActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                return false;
            }
        });

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, SelectLocationActivity.class);
                startActivityForResult(intent, REQUEST_LOCATION_PICK);
            }
        });

        addPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, SelectPhotoActivity.class);
                selectPhotosLauncher.launch(intent);
            }
        });


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = typeSpinner.getSelectedItem().toString();
                String status = statusSpinner.getSelectedItem().toString();

                if (ubiReportTextView != null && numberOfPhotos >= 2 && type.length() > 0 && status.length() > 0) {
                    Snackbar.make(findViewById(android.R.id.content), "Solicitud enviada", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReportActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Por favor, completa todos los campos", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // Inicializar Spinners
        typeSpinner = findViewById(R.id.tipoSpinner);
        statusSpinner = findViewById(R.id.estadoSpinner);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION_PICK && resultCode == RESULT_OK && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            showAddressFromLocation(latitude, longitude); // Muestra la dirección en ubiReportTextView
        }

        if (requestCode == REQUEST_SELECT_PHOTOS && resultCode == RESULT_OK && data != null) {
            int imageCount = data.getIntExtra("imageCount", 0);
            imageCountTextView.setText("Imágenes: " + imageCount + "/2");
        }
    }

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
                ubiReportTextView.setText(addressBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final ActivityResultLauncher<Intent> selectPhotosLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Aquí maneja el resultado
                    numberOfPhotos = result.getData().getIntExtra("numberOfPhotos", 0);
                    // Actualiza el textView imageCountTextView
                    imageCountTextView.setText("Imágenes: " + numberOfPhotos + "/2");
                }
            }
    );


}




