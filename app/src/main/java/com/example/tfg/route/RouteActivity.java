package com.example.tfg.route;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.adminPanel.AdminPanelActivity;
import com.example.tfg.adminProfile.AdminProfileActivity;
import com.example.tfg.binDetails.BinDetails;
import com.example.tfg.reportTemp.ReportTempActivity;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private RouteViewModel viewModel;
    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private static final int REQUEST_CHECK_SETTINGS = 1;
    private BinAdapter binAdapter;
    private Button addToRouteButton;
    private RecyclerView binsRecyclerView;
    private List<BinDetails> selectedBins = new ArrayList<>();
    private List<LatLng> routePoints = new ArrayList<>();
    private Drawable contenedorMarronIcon;
    private Drawable contenedorAmarilloIcon;
    private Drawable contenedorAzulIcon;
    private BitmapDescriptor contenedorMarron;
    private BitmapDescriptor contenedorAmarillo;
    private BitmapDescriptor contenedorAzul;
    private Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        binsRecyclerView = findViewById(R.id.binsRecyclerView);
        binsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binAdapter = new BinAdapter(selectedBins);
        binsRecyclerView.setAdapter(binAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();
                moveItem(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                removeBin(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(binsRecyclerView);

        addToRouteButton = findViewById(R.id.addToRouteButton);

        addToRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMarker != null) {
                    LatLng selectedLatLng = selectedMarker.getPosition(); // Obtenemos la posición del marcador seleccionado
                    String address = getAddressFromLocation(selectedLatLng.latitude, selectedLatLng.longitude); // Obtenemos la dirección correspondiente a la posición

                    BinDetails binDetails = new BinDetails(selectedMarker.getTitle(), address, "Tipo", "Estado",R.drawable.ic_launcher_foreground);
                    addBinToLayout(binDetails, selectedBins); // Agregamos el contenedor al diseño

                    //  Hay que pagar y lo va a hacer tu padre
                    //drawRoute(); // Dibujamos la ruta en el mapa
                    //routePoints.add(selectedLatLng);

                    selectedMarker = null; // Reiniciamos después de añadir a la ruta
                    addToRouteButton.setEnabled(false); // Ocultamos el botón después de añadir a la ruta
                }
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

        setupMap();
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("MarkerClick", "Marker clicked: " + marker.getPosition().toString());
        addToRouteButton.setEnabled(true);
        selectedMarker = marker; // Guardar el marcador seleccionado
        return true; // Devolver true para indicar que hemos manejado el clic
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
                }
            }
        });
    }

    private void setupMap() {
        contenedorMarronIcon = ContextCompat.getDrawable(this, R.drawable.contenedor_marron);
        contenedorAmarilloIcon = ContextCompat.getDrawable(this, R.drawable.contenedor_amarillo);
        contenedorAzulIcon = ContextCompat.getDrawable(this, R.drawable.contenedor_azul);

        contenedorMarron = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(contenedorMarronIcon));
        contenedorAmarillo = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(contenedorAmarilloIcon));
        contenedorAzul = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(contenedorAzulIcon));

        mMap.addMarker(new MarkerOptions().position(new LatLng(39.586917, -0.336444)).title("Contenedor marrón").icon(contenedorMarron));
        mMap.addMarker(new MarkerOptions().position(new LatLng(39.5895698, -0.3365235)).title("Contenedor amarillo").icon(contenedorAmarillo));
        mMap.addMarker(new MarkerOptions().position(new LatLng(39.592216, -0.336297)).title("Contenedor azul").icon(contenedorAzul));
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // Obtener la dirección correspondiente a las coordenadas
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Dirección no disponible";
    }

    // Agregar detalles del contenedor al LinearLayout
    private void addBinToLayout(BinDetails binDetails, List<BinDetails> selectedBins) {
        if (selectedBins.contains(binDetails)) {
            Toast.makeText(this, "No se puede añadir dos veces el mismo contenedor", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedBins.add(binDetails);
        binAdapter.notifyItemInserted(selectedBins.size() - 1);
    }

    public void removeBin(int position) {
        Log.d("BinAdapter", "Removing item at position " + position);
        selectedBins.remove(position);
        binAdapter.notifyItemRemoved(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        Log.d("BinAdapter", "Moving item from " + fromPosition + " to " + toPosition);
        Collections.swap(selectedBins, fromPosition, toPosition);
        binAdapter.notifyItemMoved(fromPosition, toPosition);
        Log.d("BinAdapter", selectedBins.toString());
    }

    private void drawRoute() {
        if (mMap != null && routePoints.size() >= 2) {
            List<com.google.maps.model.LatLng> waypoints = new ArrayList<>(routePoints.size());
            for (LatLng point : routePoints) {
                waypoints.add(new com.google.maps.model.LatLng(point.latitude, point.longitude));
            }

            try {
                DirectionsResult result = DirectionsHelper.getDirections(waypoints.get(0), waypoints.get(waypoints.size() - 1));
                if (result != null) {
                    List<LatLng> path = new ArrayList<>();
                    for (DirectionsRoute route : result.routes) {
                        for (DirectionsLeg leg : route.legs) {
                            for (DirectionsStep step : leg.steps) {
                                for (com.google.maps.model.LatLng point : step.polyline.decodePath()) {
                                    path.add(new LatLng(point.lat, point.lng));
                                }
                            }
                        }
                    }

                    // Añadir polilínea al mapa
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(path)
                            .width(5) // Ancho de la línea en píxeles
                            .color(Color.RED); // Color de la línea
                    mMap.addPolyline(polylineOptions);
                }
             } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
