package com.example.tfg.reportTemp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tfg.R;
import com.example.tfg.adminPanel.AdminPanel;
import com.example.tfg.map.MapActivity;
import com.example.tfg.reportAdd.ReportActivity;
import com.example.tfg.reportAdd.SelectLocationActivity;
import com.example.tfg.reportAdd.SelectPhotoActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportTempActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Spinner typeSpinner;
    private EditText dateInicioEditText;
    private EditText dateFinEditText;
    private EditText fechaSeleccionadaEditText; // Variable para rastrear el EditText seleccionado
    private Button selectLocationButton;
    private ImageView addPhotoImageView;
    private Button acceptButton;
    private TextView ubiReportTextView;
    private int numberOfPhotos;
    private TextView imageCountTextView;


    private static final int REQUEST_LOCATION_PICK = 1;
    private static final int REQUEST_SELECT_PHOTOS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_report);

        selectLocationButton = findViewById(R.id.selectLocationButton);
        typeSpinner = findViewById(R.id.tipoSpinner);
        addPhotoImageView = findViewById(R.id.addPhotoImageView);
        acceptButton = findViewById(R.id.aceptarButton);
        ubiReportTextView = findViewById(R.id.ubiReportTextView);
        imageCountTextView = findViewById(R.id.imageCountTextView);
        dateInicioEditText = findViewById(R.id.dateInicioEditText);


        BottomNavigationView navView = findViewById(R.id.adminNavigation);
        navView.setSelectedItemId(R.id.navigation_add_temporal);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_admin_panel) {
                    Intent intent = new Intent(ReportTempActivity.this, AdminPanel.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.navigation_add_temporal) {
                    return true;
                } else if (id == R.id.navigation_truck) {
                    return true;
                } else if (id == R.id.navigation_admin_profile) {
                    return true;
                }
                return false;
            }
        });

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportTempActivity.this, SelectLocationActivity.class);
                startActivityForResult(intent, REQUEST_LOCATION_PICK);
            }
        });

        addPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportTempActivity.this, SelectPhotoActivity.class);
                selectPhotosLauncher.launch(intent);
            }
        });

        dateInicioEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fechaSeleccionadaEditText = dateInicioEditText;
                showDatePicker("Inicio");
            }
        });

        dateFinEditText = findViewById(R.id.dateFinEditText);
        dateFinEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fechaSeleccionadaEditText = dateFinEditText;
                showDatePicker("Fin");
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = typeSpinner.getSelectedItem().toString();

                if (!checkDate()) {
                    Toast.makeText(ReportTempActivity.this, "Fecha de inicio menor o igual que la fecha de fin", Toast.LENGTH_LONG).show();
                }
                else if (ubiReportTextView != null && numberOfPhotos >= 2 && type.length() > 0) {
                    Toast.makeText(ReportTempActivity.this, "Contenedor creado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReportTempActivity.this, AdminPanel.class);

                    // Crear el cuadro de diálogo emergente
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportTempActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_content_layout, null);
                    builder.setView(dialogView);

                    //Configurar los textos
                    EditText tituloEditText = dialogView.findViewById(R.id.tituloEditText);
                    EditText descripcionEditText = dialogView.findViewById(R.id.descripcionEditText);

                    // Configurar textos clicables
                    TextView acceptTextView = dialogView.findViewById(R.id.acceptTextView);
                    TextView cancelTextView = dialogView.findViewById(R.id.cancelTextView);

                    // Crear el cuadro de diálogo
                    AlertDialog dialog = builder.create();

                    // Configurar acciones para los textos clicables
                    acceptTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String titulo = tituloEditText.getText().toString();
                            String descripcion = descripcionEditText.getText().toString();

                            if (titulo.length() < 3) {
                                // Si el título tiene menos de 3 caracteres, mostrar un mensaje de error
                                Toast.makeText(ReportTempActivity.this, "El título debe tener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
                            } else if (descripcion.length() < 10) {
                                // Si la descripción tiene menos de 10 caracteres, mostrar un mensaje de error
                                Toast.makeText(ReportTempActivity.this, "La descripción debe tener al menos 10 caracteres", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(ReportTempActivity.this, "Aviso creado", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ReportTempActivity.this, AdminPanel.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                    cancelTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent(ReportTempActivity.this, AdminPanel.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    // Mostrar el cuadro de diálogo emergente
                    dialog.show();
                } else {
                    Toast.makeText(ReportTempActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION_PICK && resultCode == RESULT_OK && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            showAddressFromLocation(latitude, longitude); // Muestra la dirección en ubiReportTextView
        }

        if (requestCode == REQUEST_SELECT_PHOTOS && resultCode == RESULT_OK && data != null) {
            int imageCount = data.getIntExtra("imageCount", 0);
            imageCountTextView.setText("Imágenes: " + imageCount + "/2");
        }
    }

    private void showDatePicker(String tipo) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ReportTempActivity.this,
                this, year, month, dayOfMonth);

        // Configurar el título del DatePickerDialog dependiendo del tipo de fecha
        if (tipo.equals("inicio")) {
            datePickerDialog.setTitle("Seleccionar fecha de inicio");
        } else if (tipo.equals("fin")) {
            datePickerDialog.setTitle("Seleccionar fecha de fin");
        }

        datePickerDialog.show();
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

                // Mostrar la dirección en el TextView
                ubiReportTextView.setText(addressBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDate() {
        EditText dateInicioEditText = findViewById(R.id.dateInicioEditText);
        EditText dateFinEditText = findViewById(R.id.dateFinEditText);

        String fechaInicioStr = dateInicioEditText.getText().toString();
        String fechaFinStr = dateFinEditText.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date fechaInicio = sdf.parse(fechaInicioStr);
            Date fechaFin = sdf.parse(fechaFinStr);

            // La fecha de inicio es menor o igual que la fecha de fin
            if (fechaInicio.compareTo(fechaFin) <= 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // El usuario ha seleccionado una fecha
        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

        // Actualizar el EditText correspondiente con la fecha seleccionada
        if (fechaSeleccionadaEditText != null) {
            fechaSeleccionadaEditText.setText(selectedDate);
        }
    }
}
