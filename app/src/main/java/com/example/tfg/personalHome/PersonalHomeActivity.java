package com.example.tfg.personalHome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.tfg.adminReports.ReportAddActivity;
import com.example.tfg.personalProfile.PersonalProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class PersonalHomeActivity extends AppCompatActivity {

    private LinearLayout container;
    private boolean prueba = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_home);

        BottomNavigationView navView = findViewById(R.id.personalNavigation);
        navView.setSelectedItemId(R.id.navigation_personal_home);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_personal_home) {
                    return true;
                } else {
                    Intent intent = new Intent(PersonalHomeActivity.this, PersonalProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                return false;
            }
        });

        container = findViewById(R.id.container);
        // Ejemplo de añadir elementos dinámicamente
        addItem("2024-05-23", "Contenedor creado", R.drawable.contenedor_marron);
        addItem("2024-05-23", "Contenedor creado", R.drawable.contenedor_azul);
        addItem("2024-05-19", "Contenedor creado", R.drawable.contenedor_amarillo);

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

        // ImageView para el icono en la esquina superior derecha
        ImageView iconView = new ImageView(this);
        LinearLayout.LayoutParams imageLayoutParamsIcon = new LinearLayout.LayoutParams(80,80); // Puedes ajustar la altura según sea necesario
        iconView.setLayoutParams(imageLayoutParamsIcon);
        iconView.setImageResource(R.drawable.info_icon);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción al pulsar el icono
                if (prueba) {
                    Intent intent = new Intent(PersonalHomeActivity.this, ReportAddActivity.class);
                    startActivity(intent);
                    prueba = false;
                } else {
                    Intent intent = new Intent(PersonalHomeActivity.this, ReportAddActivity.class);
                    startActivity(intent);
                }
            }
        });

        // LinearLayout para alinear el icono a la derecha
        LinearLayout iconLayout = new LinearLayout(this);
        iconLayout.setOrientation(LinearLayout.HORIZONTAL);
        iconLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        iconLayout.setGravity(Gravity.END);
        iconLayout.addView(iconView);

        // Línea separadora
        View separator = new View(this);
        LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                1, // Ancho de la línea
                ViewGroup.LayoutParams.MATCH_PARENT);
        separatorParams.setMargins(-280, 0, 150, 0); // Márgenes de la línea
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
        textLayout.addView(iconLayout);

        // Agregar ImageView y layout de textos al layout del elemento
        itemLayout.addView(imageView);
        itemLayout.addView(textLayout);

        // Agregar el layout del elemento al contenedor
        container.addView(itemLayout);
    }
}
