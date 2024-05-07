package com.example.tfg.adminProfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.R;
import com.example.tfg.adminPanel.AdminPanelActivity;
import com.example.tfg.reportTemp.ReportTempActivity;
import com.example.tfg.route.RouteActivity;
import com.example.tfg.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminProfileActivity extends AppCompatActivity {

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
                } else if (id == R.id.navigation_admin_profile) {
                    return true;
                }
                return false;
            }
        });

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
