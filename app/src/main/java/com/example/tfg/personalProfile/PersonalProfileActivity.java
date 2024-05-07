package com.example.tfg.personalProfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.R;
import com.example.tfg.personalHome.PersonalHomeActivity;
import com.example.tfg.profile.ProfileActivity;
import com.example.tfg.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class PersonalProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);


        BottomNavigationView navView = findViewById(R.id.personalNavigation);
        navView.setSelectedItemId(R.id.navigation_personal_profile);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_personal_home) {
                    Intent intent = new Intent(PersonalProfileActivity.this, PersonalHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else {
                    return true;
                }
                return false;
            }
        });

        TextView contenedoresCreados = findViewById(R.id.contedoresCreadosPersonal);
        TextView contenedoresModificados = findViewById(R.id.contenedoresModificadosPersonal);
        TextView contenedoresBorrados = findViewById(R.id.contenedoresBorradosPersonal);

        // Llamar a la función para cada TextView
        setFormattedText(contenedoresCreados, 5, "Creados");
        setFormattedText(contenedoresModificados, 3, "Modificados");
        setFormattedText(contenedoresBorrados, 2, "Borrados");


        LinearLayout activityContainer = findViewById(R.id.activityContainer);



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

    private void setFormattedText(TextView textView, int number, String description) {
        String text = number + "\n" + description;
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, String.valueOf(number).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#8A7D7D")), String.valueOf(number).length() + 1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalProfileActivity.this);
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Si el usuario hace clic en Sí, realiza el logout y cierra la sesión
                        Intent intent = new Intent(PersonalProfileActivity.this, LoginActivity.class);
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
