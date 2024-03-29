package com.example.tfg.reportAdd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class ReportActivity extends AppCompatActivity {

    private Button selectLocationButton;
    private Spinner typeSpinner;
    private Spinner statusSpinner;
    private ImageView addPhotoImageView;
    private Button acceptButton;

    private Pair<Double, Double> selectedLocation = null;
    private Uri selectedImageUri = null;

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
        addPhotoImageView = findViewById(R.id.imageViewer);
        acceptButton = findViewById(R.id.aceptarButton);

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

        addPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Cámara", "Galería"};
                new AlertDialog.Builder(ReportActivity.this)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    if (ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        openCamera();
                                    } else {
                                        ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                                    }
                                } else if (which == 1) {
                                    if (ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                        openGallery();
                                    } else {
                                        ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
                                    }
                                }
                            }
                        })
                        .show();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = typeSpinner.getSelectedItem().toString();
                String status = statusSpinner.getSelectedItem().toString();

                if (selectedLocation != null && selectedImageUri != null && type.length() > 0 && status.length() > 0) {
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
                openGallery();
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setMessage("Necesitamos permiso para acceder a tus fotos.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
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
                    // Manejo del resultado de la cámara aquí
                    addPhotoImageView.setImageURI(Uri.parse(currentPhotoPath));
                    selectedImageUri = Uri.parse(currentPhotoPath);
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // Manejo del resultado de la galería aquí
                    selectedImageUri = result.getData().getData();
                    addPhotoImageView.setImageURI(selectedImageUri);
                }
            });


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION_PICK && resultCode == RESULT_OK && data != null) {
            selectedLocation = new Pair<>(data.getDoubleExtra("latitude", 0), data.getDoubleExtra("longitude", 0));
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            addPhotoImageView.setImageURI(Uri.parse(currentPhotoPath));
            selectedImageUri = Uri.parse(currentPhotoPath);
        }

        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                addPhotoImageView.setImageURI(selectedImageUri);
            }
        }
    }

}




