package com.example.tfg.reportAdd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.tfg.R;
import com.example.tfg.map.MapActivity;
import com.example.tfg.profile.ProfileActivity;
import com.example.tfg.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private Button selectLocationButton;
    private Spinner typeSpinner;
    private Spinner statusSpinner;
    // private ImageView addPhotoImageView;
    private Button acceptButton;
    private TextView ubiReportTextView;
    private Uri selectedImageUri = null;

    private LinearLayout imageContainer;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 2;
    private static final int REQUEST_LOCATION_PICK = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        selectLocationButton = findViewById(R.id.selectLocationButton);
        typeSpinner = findViewById(R.id.tipoSpinner);
        statusSpinner = findViewById(R.id.estadoSpinner);
        imageContainer = findViewById(R.id.imageContainer);
        // addPhotoImageView = findViewById(R.id.reportImage);
        acceptButton = findViewById(R.id.aceptarButton);
        ubiReportTextView = findViewById(R.id.ubiReportTextView);

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setSelectedItemId(R.id.navigation_add);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(ReportActivity.this, MapActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else if (id == R.id.navigation_add) {
                    return true;
                } else if (id == R.id.navigation_profile) {
                    Intent intent = new Intent(ReportActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
                return false;
            }
        });

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, SelectLocationActivity.class);
                startActivityForResult(intent, REQUEST_LOCATION_PICK);
            }
        });

        /* addPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Cámara", "Galería"};
                new AlertDialog.Builder(ReportActivity.this)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // Verificar permisos de la cámara
                                    if (ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        openCamera();
                                    } else {
                                        ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                                    }
                                } else if (which == 1) {
                                    // Verificar permisos de la galería
                                    if (ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                                        openGallery();
                                    } else {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_PERMISSION_REQUEST_CODE);
                                        }
                                    }
                                }
                            }
                        })
                        .show();
            }
        }); */

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = typeSpinner.getSelectedItem().toString();
                String status = statusSpinner.getSelectedItem().toString();

                if (ubiReportTextView != null && selectedImageUri != null && type.length() > 0 && status.length() > 0) {
                    Intent intent = new Intent(ReportActivity.this, MapActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ReportActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inicializar Spinners
        typeSpinner = findViewById(R.id.tipoSpinner);
        statusSpinner = findViewById(R.id.estadoSpinner);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    new AlertDialog.Builder(this)
                            .setMessage("Necesitamos permiso para acceder a tu cámara.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                                }
                            })
                            .show();
                }
            }
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    new AlertDialog.Builder(this)
                            .setMessage("Necesitamos permiso para acceder a tus fotos.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_PERMISSION_REQUEST_CODE);
                                }
                            })
                            .show();
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Crea un nombre de archivo de imagen único
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Guarda una referencia al path de la imagen para su uso posterior
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Permitir la selección múltiple
        galleryLauncher.launch(pickPhoto);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(ReportActivity.this, "Hubo un error al crear el archivo de la imagen.", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.tfg.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Mostrar la imagen capturada en el ImageView
                    ImageView imageView = new ImageView(ReportActivity.this);
                    imageView.setImageURI(Uri.parse(currentPhotoPath));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 0, 0, 16); // Margen inferior entre imágenes
                    imageView.setLayoutParams(layoutParams);
                    imageContainer.addView(imageView); // Agregar la imagen al contenedor
                }
            });

    /* private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // Manejar el resultado de la galería
                    if (result.getData().getData() != null) {
                        // Una sola imagen seleccionada
                        Uri selectedImageUri = result.getData().getData();
                        addPhotoImageView.setImageURI(selectedImageUri);
                    } else if (result.getData().getClipData() != null) {
                        // Múltiples imágenes seleccionadas
                        ClipData clipData = result.getData().getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri selectedImageUri = clipData.getItemAt(i).getUri();
                            // Mostrar cada imagen seleccionada en el ImageView
                            // Aquí puedes guardar o mostrar las imágenes como prefieras
                            // Por ejemplo, puedes crear un nuevo ImageView para cada imagen seleccionada
                            ImageView imageView = new ImageView(ReportActivity.this);
                            imageView.setImageURI(selectedImageUri);
                            // Agregar el ImageView al diseño
                            // En este ejemplo, estoy agregando el ImageView a un LinearLayout llamado imageContainer
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            layoutParams.setMargins(0, 0, 0, 16); // Margen inferior entre imágenes
                            imageView.setLayoutParams(layoutParams);
                            imageContainer.addView(imageView); // imageContainer es el contenedor donde se mostrarán las imágenes
                        }
                    }
                }
            }
    ); */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION_PICK && resultCode == RESULT_OK && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            showAddressFromLocation(latitude, longitude); // Muestra la dirección en ubiReportTextView
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Verificar si hay una ruta de imagen actual
            if (currentPhotoPath != null) {
                // Mostrar la imagen capturada en el ImageView
                addPhotoImageView.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
            }
        }

        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Verificar si se seleccionó una imagen de la galería
            if (data != null && data.getData() != null) {
                // Obtener la URI de la imagen seleccionada
                Uri selectedImageUri = data.getData();
                // Mostrar la imagen seleccionada en el ImageView
                addPhotoImageView.setImageURI(selectedImageUri);
            }
        }
    }

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

}




