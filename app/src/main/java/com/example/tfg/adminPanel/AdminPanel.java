package com.example.tfg.adminPanel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.R;
import com.example.tfg.map.MapActivity;
import com.example.tfg.points.PointsActivity;
import com.example.tfg.profile.ProfileActivity;
import com.example.tfg.reportAdd.ReportActivity;
import com.example.tfg.reportTemp.ReportTempActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminPanel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        BottomNavigationView navView = findViewById(R.id.adminNavigation);
        navView.setSelectedItemId(R.id.navigation_admin_panel);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_admin_panel) {
                    return true;
                } else if (id == R.id.navigation_add_temporal) {
                    Intent intent = new Intent(AdminPanel.this, ReportTempActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else if (id == R.id.navigation_truck) {
                    return true;
                } else if (id == R.id.navigation_admin_profile) {
                    return true;
                }
                return false;
            }
        });
    }
}
