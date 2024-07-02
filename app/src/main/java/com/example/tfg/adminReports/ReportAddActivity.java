package com.example.tfg.adminReports;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.R;
import com.google.android.material.snackbar.Snackbar;

public class ReportAddActivity extends AppCompatActivity {

    private Button acceptButton;
    private Button rejectButton;
    private Spinner tipoSpinner;
    private Spinner estadoSpinner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_container_report);

        acceptButton = findViewById(R.id.aceptarButton);
        rejectButton = findViewById(R.id.rechazarButton);
        tipoSpinner = findViewById(R.id.tipoSpinner);
        estadoSpinner = findViewById(R.id.estadoSpinner);

        // Set initial values
        tipoSpinner.setSelection(1);
        estadoSpinner.setSelection(1);

        acceptButton.setOnClickListener(v -> {
            Snackbar.make(findViewById(android.R.id.content), "Reporte aceptado y creado con éxito", Snackbar.LENGTH_SHORT).show();
            finish();
        });

        rejectButton.setOnClickListener(v -> {
            Snackbar.make(findViewById(android.R.id.content), "Reporte rechazado", Snackbar.LENGTH_SHORT).show();
            finish();
        });

    }

}

