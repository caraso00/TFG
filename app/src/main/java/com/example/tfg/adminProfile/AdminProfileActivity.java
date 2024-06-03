package com.example.tfg.adminProfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.adminPanel.AdminPanelActivity;
import com.example.tfg.binDetails.BinDetails;
import com.example.tfg.reportTemp.ReportTempActivity;
import com.example.tfg.route.RouteActivity;
import com.example.tfg.route.RouteAdapter;
import com.example.tfg.route.RouteDetails;
import com.example.tfg.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminProfileActivity extends AppCompatActivity {

    private RouteAdapter routeAdapter;
    private RecyclerView routeRecyclerView;
    private final List<RouteDetails> routes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        BottomNavigationView navView = findViewById(R.id.adminNavigation);
        navView.setSelectedItemId(R.id.navigation_admin_profile);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_admin_panel) {
                    Intent intent = new Intent(AdminProfileActivity.this, AdminPanelActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.navigation_add_temporal) {
                    Intent intent = new Intent(AdminProfileActivity.this, ReportTempActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.navigation_route) {
                    Intent intent = new Intent(AdminProfileActivity.this, RouteActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else return id == R.id.navigation_admin_profile;
                return false;
            }
        });

        List<BinDetails> binDetailsList = new ArrayList<>();
        binDetailsList.add(new BinDetails(
                "Contenedor marrón",
                showAddressFromLocation(39.586917, -0.336444),
                null,
                null,
                0,
                (float) 0
        ));
        binDetailsList.add(new BinDetails(
                "Contenedor amarillo",
                showAddressFromLocation(39.5895698, -0.3365235),
                null,
                null,
                0,
                (float) 0
        ));

        routes.add(new RouteDetails("Prueba", binDetailsList));
        routes.add(new RouteDetails("Prueba 2", binDetailsList));

        routeRecyclerView = findViewById(R.id.routesRecyclerView);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        routeAdapter = new RouteAdapter(routes);
        routeRecyclerView.setAdapter(routeAdapter);

        BottomNavigationView logout = findViewById(R.id.logout);
        logout.setSelectedItemId(R.id.invisible);

        logout.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_back) {
                    showLogoutConfirmationDialog(); // Mostrar el diálogo de confirmación antes de realizar el logout
                }
                return false;
            }
        });
    }

    public void modifyRoute(int position) {
        RouteDetails routeDetails = routes.get(position);

        Intent intent = new Intent(this, RouteActivity.class);
        intent.putExtra("routeName", routeDetails.getTitulo());
        intent.putExtra("bins", new ArrayList<>(routeDetails.getBinDetailsList()));
        startActivity(intent);

        finish();
    }

    public void removeRoute(int position) {
        routes.remove(position);
        routeAdapter.notifyItemRemoved(position);
    }

    private String showAddressFromLocation(double latitude, double longitude) {
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

                return addressBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminProfileActivity.this);
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Si el usuario hace clic en Sí, realiza el logout y cierra la sesión
                        Intent intent = new Intent(AdminProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Si el usuario hace clic en No, cancela la acción y cierra el diálogo
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
