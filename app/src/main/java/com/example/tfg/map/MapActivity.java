package com.example.tfg.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.tfg.R;
import com.example.tfg.profile.ProfileActivity;
import com.example.tfg.reportAdd.ReportActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private MapViewModel viewModel;

    private Spinner distanceSpinner;
    private MultiAutoCompleteTextView multiSelectionDropdown;
    private MultiSelectArrayAdapter adapter;
    private ArrayList<String> selectedItems;
    private final ArrayList<String> items = new ArrayList<>(Arrays.asList("Orgánico", "Papel y cartón", "Vidrio", "Plástico"));
    private Drawable contenedorVerdeIcon;
    private Drawable contenedorAmarilloIcon;
    private Drawable contenedorAzulIcon;
    private int distance;
    private BitmapDescriptor contenedorVerde;
    private BitmapDescriptor contenedorAmarillo;
    private BitmapDescriptor contenedorAzul;

    private static final int REQUEST_CHECK_SETTINGS = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

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


        viewModel.getLocationSettingResponse().observe(this, isLocationSettingSatisfied -> {
            if (Boolean.TRUE.equals(isLocationSettingSatisfied)) {
                onLocationActivated();
                getDeviceLocation();
            } else {
                onLocationDeactivated();
            }
        });

        viewModel.getResolvableApiException().observe(this, resolvable -> {
            try {
                resolvable.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                onLocationDeactivated();
            }
        });

        selectedItems = new ArrayList<>();
        adapter = new MultiSelectArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items);
        multiSelectionDropdown = findViewById(R.id.multiSelectionDropdown);
        multiSelectionDropdown.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        multiSelectionDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiChoiceDialog();
            }
        });


        contenedorVerdeIcon = ContextCompat.getDrawable(this, R.drawable.contenedor_verde);
        contenedorAmarilloIcon = ContextCompat.getDrawable(this, R.drawable.contenedor_amarillo);
        contenedorAzulIcon = ContextCompat.getDrawable(this, R.drawable.contenedor_azul);

        contenedorVerde = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(contenedorVerdeIcon));
        contenedorAmarillo = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(contenedorAmarilloIcon));
        contenedorAzul = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(contenedorAzulIcon));

        distanceSpinner = findViewById(R.id.spinner);
        distanceSpinner.setSelection(0);

        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String distanciaSeleccionada = distanceSpinner.getSelectedItem().toString();
                switch (distanciaSeleccionada) {
                    case "100 m":
                        distance = 1;
                        agregarMarcadores(new LatLng(39.586917, -0.336444), null,  null, distance);
                        break;
                    case "500 m":
                        distance = 2;
                        agregarMarcadores(new LatLng(39.586917, -0.336444), new LatLng(39.5895698, -0.3365235), null, distance);
                        break;
                    case "1 km":
                        distance = 3;
                        agregarMarcadores(new LatLng(39.586917, -0.336444), new LatLng(39.5895698, -0.3365235), new LatLng(39.592216, -0.336297), distance);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No se seleccionó nada en el Spinner
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f));
                    Log.d("Zoom", "Haciendo zoom");
                }
            }
        });
    }

    public void onLocationActivated() {
        distanceSpinner.setEnabled(true);
        distanceSpinner.setAlpha(1.0f);
    }

    public void onLocationDeactivated() {
        distanceSpinner.setEnabled(false);
        distanceSpinner.setAlpha(0.5f);
    }

    private void showMultiChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elija los tipos:");

        boolean[] checkedItems = new boolean[items.size()];

        // Inicializar checkedItems basado en los elementos seleccionados en selectedItems
        for (int i = 0; i < items.size(); i++) {
            checkedItems[i] = selectedItems.contains(items.get(i));
        }

        builder.setMultiChoiceItems(items.toArray(new String[0]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String selectedItem = items.get(which);
                if (isChecked) {
                    // Agregar el elemento seleccionado al ArrayList selectedItems
                    selectedItems.add(selectedItem);
                } else {
                    // Remover el elemento deseleccionado del ArrayList selectedItems
                    selectedItems.remove(selectedItem);
                }
            }
        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int countSelected = selectedItems.size();
                String selections = "";
                if (countSelected == 1) {
                    selections = selectedItems.get(0);
                } if (countSelected > 1) {
                    selections = "Varios";
                }
                multiSelectionDropdown.setText(selections);
                agregarMarcadores(new LatLng(39.586917, -0.336444), new LatLng(39.5895698, -0.3365235), new LatLng(39.592216, -0.336297), distance);
            }
        });

        builder.setNegativeButton("Cancelar", null);

        builder.show();
    }

    // Agrega un marcador al mapa y elimina los marcadores anteriores si es necesario
    private void agregarMarcadores(LatLng punto1, LatLng punto2, LatLng punto3, int seleccion) {
        // Borra todos los marcadores existentes
        mMap.clear();

        // Agrega los marcadores según la selección del usuario
        if (checkSelectedItems(selectedItems, "Orgánico"))mMap.addMarker(new MarkerOptions().position(punto1).title("Contenedor verde").icon(contenedorVerde));
        if (checkSelectedItems(selectedItems,"Plástico") && seleccion >= 2) {
            mMap.addMarker(new MarkerOptions().position(punto2).title("Contenedor amarillo").icon(contenedorAmarillo));
        }
        if (checkSelectedItems(selectedItems,"Papel y cartón") && seleccion == 3) {
            mMap.addMarker(new MarkerOptions().position(punto3).title("Contenedor azul").icon(contenedorAzul));
        }
    }

    private boolean checkSelectedItems(ArrayList<String> selectedItems, String item) {
        if (selectedItems.size() == 0) return true;
        return selectedItems.contains(item);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
