package com.example.tfg.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.R;
import com.example.tfg.map.MapActivity;
import com.example.tfg.points.PointsActivity;
import com.example.tfg.reportAdd.ReportActivity;
import com.example.tfg.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView pointView;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        pointView = findViewById(R.id.pointsView);

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setSelectedItemId(R.id.navigation_profile);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(ProfileActivity.this, MapActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.navigation_add) {
                    Intent intent = new Intent(ProfileActivity.this, ReportActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.navigation_profile) {
                    return true;
                }
                return false;
            }
        });

        pointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PointsActivity.class);
                intent.putExtra("points", 2500);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        TextView contenedoresCreados = findViewById(R.id.contedoresCreadosTextView);
        TextView contenedoresModificados = findViewById(R.id.contenedoresModificadosTextView);
        TextView valoraciones = findViewById(R.id.valoracionesTextView);

        // Llamar a la función para cada TextView
        setFormattedText(contenedoresCreados, 2, "Creados");
        setFormattedText(contenedoresModificados, 1, "Modificados");
        setFormattedText(valoraciones, 7, "Valoraciones");

        BottomNavigationView logout = findViewById(R.id.logout);
        logout.setSelectedItemId(R.id.invisible);

        container = findViewById(R.id.container);
        // Ejemplo de añadir elementos dinámicamente
        addItem("2024-05-23", "Contenedor creado", R.drawable.contenedor_marron);
        addItem("2024-05-23", "Contenedor creado", R.drawable.contenedor_azul);
        addItem("2024-05-19", "Contenedor creado", R.drawable.contenedor_amarillo);

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

    private void addItem(String date, String content, int imageResource) {
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
        dateTextView.setText(date);
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

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Si el usuario hace clic en Sí, realiza el logout y cierra la sesión
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
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
