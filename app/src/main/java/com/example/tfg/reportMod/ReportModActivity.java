package com.example.tfg.reportMod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.R;
import com.example.tfg.map.MapActivity;
import com.example.tfg.reportAdd.SelectPhotoActivity;
import com.google.android.material.snackbar.Snackbar;

public class ReportModActivity extends AppCompatActivity {

    private View backView;
    private Spinner motivoSpinner;
    private ImageView imageView;
    private Button selectLocationButton;
    private TextView ubiReportTextView;
    private TextView tipoContenedorTextView;
    private Spinner tipoSpinner;
    private TextView estadoContenedorTextView;
    private Spinner estadoSpinner;
    private EditText commentsText;
    private Button acceptButton;
    private int numberOfPhotos;
    private TextView imageCountTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update); // Assuming your XML is named activity_report_mod.xml

        // Initialize all the views
        imageView = findViewById(R.id.addPhotoImageView);
        ubiReportTextView = findViewById(R.id.ubiReportTextView);
        selectLocationButton = findViewById(R.id.selectLocationButton);
        tipoContenedorTextView = findViewById(R.id.tipoContenedor);
        tipoSpinner = findViewById(R.id.tipoSpinner);
        estadoContenedorTextView = findViewById(R.id.estadoSpinnerTitle);
        estadoSpinner = findViewById(R.id.estadoSpinner);
        backView = findViewById(R.id.backView);
        motivoSpinner = findViewById(R.id.motivoSpinner);
        commentsText = findViewById(R.id.commentsText);
        acceptButton = findViewById(R.id.aceptarButton);
        imageCountTextView = findViewById(R.id.imageCountTextView);

        // Retrieve data from the intent
        Intent intent = getIntent();
        String ubicacion = intent.getStringExtra("ubicacion");
        int tipoSelected = intent.getIntExtra("tipoSelected", 0);
        int estadoSelected = intent.getIntExtra("estadoSelected", 0);

        // Set initial values
        ubiReportTextView.setText(ubicacion);
        tipoSpinner.setSelection(tipoSelected);
        estadoSpinner.setSelection(estadoSelected);

        // Set initial visibility
        setInitialVisibility();

        motivoSpinner.setSelection(0);
        // Set listener for motivoSpinner
        motivoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateVisibilityBasedOnMotivo(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportModActivity.this, SelectPhotoActivity.class);
                selectPhotosLauncher.launch(intent);
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = tipoSpinner.getSelectedItem().toString();
                String status = estadoSpinner.getSelectedItem().toString();

                if (commentsText.length() < 15) {
                    Snackbar.make(findViewById(android.R.id.content), "Mínimo de 15 caracteres como descripción", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                int motivoPosition = motivoSpinner.getSelectedItemPosition();
                boolean isRequestValid = false;

                switch (motivoPosition) {
                    case 0:
                        isRequestValid = type.length() > 0 && numberOfPhotos >= 2;
                        break;
                    case 1:
                        isRequestValid = status.length() > 0 && numberOfPhotos >= 2;
                        break;
                    case 2:
                        isRequestValid = ubiReportTextView != null;
                        break;
                    case 3:
                        isRequestValid = true;
                        break;
                }

                if (isRequestValid) {
                    Snackbar.make(findViewById(android.R.id.content), "Solicitud enviada", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReportModActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Por favor, completa todos los campos", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClick listener to backView to close the activity
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setInitialVisibility() {
        // Set everything except the specified views to be invisible
        tipoContenedorTextView.setVisibility(View.GONE);
        tipoSpinner.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        selectLocationButton.setVisibility(View.GONE);
        ubiReportTextView.setVisibility(View.GONE);
        estadoContenedorTextView.setVisibility(View.GONE);
        estadoSpinner.setVisibility(View.GONE);
    }

    private void updateVisibilityBasedOnMotivo(int position) {
        // Hide all views initially
        setInitialVisibility();

        // Show views based on the selected position
        switch (position) {
            case 0:
                tipoContenedorTextView.setVisibility(View.VISIBLE);
                tipoSpinner.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                break;
            case 1:
                estadoContenedorTextView.setVisibility(View.VISIBLE);
                estadoSpinner.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                break;
            case 2:
                selectLocationButton.setVisibility(View.VISIBLE);
                ubiReportTextView.setVisibility(View.VISIBLE);
                break;
            case 3:
                break;
        }
    }

    private final ActivityResultLauncher<Intent> selectPhotosLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Aquí maneja el resultado
                    numberOfPhotos = result.getData().getIntExtra("numberOfPhotos", 0);
                    // Actualiza el textView imageCountTextView
                    imageCountTextView.setText("Imágenes: " + numberOfPhotos + "/2");
                }
            }
    );
}
