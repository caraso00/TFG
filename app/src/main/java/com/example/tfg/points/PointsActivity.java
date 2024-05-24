package com.example.tfg.points;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tfg.R;
import com.example.tfg.profile.ProfileActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PointsActivity extends AppCompatActivity {

    private ImageView backView;
    private TextView ubiTextView;
    private TextView pointsTextView;
    private ProgressBar pointsProgressBar;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        pointsTextView = findViewById(R.id.pointsTextView);
        pointsProgressBar = findViewById(R.id.pointsProgressBar);

        Intent intent = getIntent();
        int points = intent.getIntExtra("points", 0);

        // Set initial values
        pointsTextView.setText(String.valueOf(points));
        pointsProgressBar.setProgress(points);

        ubiTextView = findViewById(R.id.ubiTextView);
        // Verificar los permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no están concedidos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Iniciar la obtención de la ubicación
            getLocation();
        }

        container = findViewById(R.id.container);

        backView = findViewById(R.id.backView);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PointsActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
    }

    // Método para iniciar la obtención de la ubicación
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Actualizar el TextView con la ubicación actual
                String ubicacion = getAddressFromLocation(location.getLatitude(), location.getLongitude());
                ubiTextView.setText(ubicacion);

                // Detener las actualizaciones de ubicación después de la primera obtención
                if (ActivityCompat.checkSelfPermission(PointsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(this);
                }

                // Ejemplo de añadir elementos dinámicamente
                addItem(ubicacion, "Verduras Paco", R.drawable.grocery_store);
                addItem(ubicacion,"Suma", R.drawable.tienda);
                addItem(ubicacion,"Peluqería canina", R.drawable.perros);
            }
        };

        // Solicitar actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String street = address.getThoroughfare(); // Nombre de la calle
                String streetNumber = address.getSubThoroughfare(); // Número de la calle
                String locality = address.getLocality(); // Localidad / Ciudad
                return street + " " + streetNumber + ", " + locality;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Dirección no disponible";
    }

    private void addItem(String location, String content, int imageResource) {
        // Contenedor para cada elemento
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutParams.setMargins(0, 5, 0, 5); // Márgenes laterales y verticales
        itemLayout.setLayoutParams(itemLayoutParams);
        itemLayout.setPadding(16, 16, 16, 16);
        itemLayout.setBackgroundResource(R.drawable.border);
        itemLayout.setGravity(Gravity.CENTER);

        // ImageView para la imagen en la parte superior
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(300,200); // Puedes ajustar la altura según sea necesario
        imageLayoutParams.setMargins(0,0,0,32);
        imageView.setLayoutParams(imageLayoutParams);
        imageView.setImageResource(imageResource);


        // LinearLayout horizontal para la fecha y el contenido
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.HORIZONTAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textLayout.setPadding(8, 8, 8, 8);

        TextView dateTextView = new TextView(this);
        dateTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        dateTextView.setText(location);
        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        dateTextView.setTextColor(getResources().getColor(R.color.black));
        dateTextView.setTextSize(16);

        // Línea separadora
        View separator = new View(this);
        LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                1, // Ancho de la línea
                ViewGroup.LayoutParams.MATCH_PARENT);
        separatorParams.setMargins(-325, 0, 150, 0); // Márgenes de la línea
        separator.setLayoutParams(separatorParams);
        separator.setBackgroundColor(Color.BLACK);

        TextView contentTextView = new TextView(this);
        contentTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        contentTextView.setText(content);
        contentTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        contentTextView.setTextColor(getResources().getColor(R.color.black));
        contentTextView.setTextSize(16);

        // Agregar TextViews al layout horizontal
        textLayout.addView(dateTextView);
        textLayout.addView(separator);
        textLayout.addView(contentTextView);

        // Agregar ImageView y layout de textos al layout del elemento
        itemLayout.addView(imageView);
        itemLayout.addView(textLayout);

        // Agregar el layout del elemento al contenedor
        container.addView(itemLayout);
    }
}
