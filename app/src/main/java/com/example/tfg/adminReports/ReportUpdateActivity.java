package com.example.tfg.adminReports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.R;
import com.google.android.material.snackbar.Snackbar;

public class ReportUpdateActivity extends AppCompatActivity {

    private Button acceptButton;
    private Button rejectButton;
    private Spinner estadoSpinner;
    private EditText descriptionText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_container_report);

        acceptButton = findViewById(R.id.aceptarButton);
        rejectButton = findViewById(R.id.rechazarButton);
        estadoSpinner = findViewById(R.id.estadoSpinner);
        descriptionText = findViewById(R.id.commentsText);

        descriptionText.setText("Como se puede observar en las imágenes el estado real del contenedor es peor al que se estableció en un principio. Por eso creo este reporte para que sea editado y colocado como Sucio");

        // Set initial values
        estadoSpinner.setSelection(2);

        acceptButton.setOnClickListener(v -> {
            Snackbar.make(findViewById(android.R.id.content), "Reporte aceptado y actualizado con éxito", Snackbar.LENGTH_SHORT).show();
            finish();
        });

        rejectButton.setOnClickListener(v -> {
            Snackbar.make(findViewById(android.R.id.content), "Reporte rechazado", Snackbar.LENGTH_SHORT).show();
            finish();
        });

    }
}
