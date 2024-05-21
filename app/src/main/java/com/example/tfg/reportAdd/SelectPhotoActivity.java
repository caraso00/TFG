package com.example.tfg.reportAdd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tfg.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectPhotoActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private RecyclerView photoRecyclerView;
    private Button cameraButton;
    private Button galleryButton;
    private Button saveButton;
    private String currentPhotoPath;
    private LinearLayout imageContainer;
    private ScrollView scrollView;
    private ImageView backView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        //photoRecyclerView = findViewById(R.id.photoRecyclerView);
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);
        saveButton = findViewById(R.id.saveButton);
        imageContainer = findViewById(R.id.imageContainer);
        backView = findViewById(R.id.backPhotoView);
        scrollView = findViewById(R.id.scrollView);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGalleryPermission();
            }
        });

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageContainer.getChildCount() >= 2) {
                    // Si hay al menos dos imágenes, mostrar un mensaje de imágenes correctas
                    Snackbar.make(findViewById(android.R.id.content), "Imágenes correctas", Snackbar.LENGTH_SHORT).show();
                    // Volver a ReportActivity
                    Intent intent = new Intent();
                    intent.putExtra("numberOfPhotos", imageContainer.getChildCount());
                    setResult(RESULT_OK, intent);
                    finish(); // Finalizar la actividad actual
                } else {
                    // Si hay menos de dos imágenes, mostrar un mensaje de advertencia
                    Snackbar.make(findViewById(android.R.id.content), "Se requieren al menos 2 imágenes", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void checkCameraPermission() {
        // Comprobar permisos de la cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void checkGalleryPermission() {
        // Comprobar permisos de la galería
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_PERMISSION_REQUEST_CODE);
        }
    }

    // Manejo de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    new AlertDialog.Builder(this)
                            .setMessage("Necesitamos permiso para acceder a tu cámara")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(SelectPhotoActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
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
                            .setMessage("Necesitamos permiso para acceder a tus fotos")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(SelectPhotoActivity.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_PERMISSION_REQUEST_CODE);
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
                Snackbar.make(findViewById(android.R.id.content), "Hubo un error al crear el archivo de la imagen", Snackbar.LENGTH_SHORT).show();
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
                    addPhotoToContainer(Uri.fromFile(new File(currentPhotoPath)));
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        // Múltiples imágenes seleccionadas
                        ClipData clipData = result.getData().getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri selectedImageUri = clipData.getItemAt(i).getUri();
                            addPhotoToContainer(selectedImageUri);
                        }
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Mínimo de dos imágenes", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void addPhotoToContainer(Uri selectedImageUri) {
        // Crear ImageView
        ImageView imageView = new ImageView(SelectPhotoActivity.this);

        // Obtener dimensiones del contenedor de imágenes
        int containerWidth = scrollView.getWidth();
        int containerHeight = scrollView.getHeight();

        // Configurar opciones de carga de Glide
        RequestOptions options = new RequestOptions()
                .override(containerWidth, containerHeight) // Ajustar tamaño
                .centerCrop(); // Centrar la imagen

        // Cargar la imagen utilizando Glide
        Glide.with(this)
                .load(selectedImageUri)
                .apply(options)
                .into(imageView);

        // Configurar parámetros de diseño del ImageView
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(1, 1, 1, 1); // Establecer márgenes entre imágenes
        imageView.setLayoutParams(layoutParams);

        // Agregar OnClickListener para eliminar la imagen cuando se hace clic en ella
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhotoFromContainer(v);
            }
        });

        // Agregar ImageView al contenedor de imágenes
        imageContainer.addView(imageView, 0); // Agregar ImageView al diseño
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Verificar si hay una ruta de imagen actual
            if (currentPhotoPath != null) {
                // Mostrar la imagen capturada en el ImageView
                addPhotoToContainer(Uri.fromFile(new File(currentPhotoPath)));
            }
        }

        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Verificar si se seleccionó una imagen de la galería
            if (data != null && data.getData() != null) {
                // Obtener la URI de la imagen seleccionada
                Uri selectedImageUri = data.getData();
                // Mostrar la imagen seleccionada en el ImageView
                addPhotoToContainer(selectedImageUri);
            }
        }
    }

    private void deletePhotoFromContainer(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectPhotoActivity.this);
        builder.setTitle("Eliminar foto");
        builder.setMessage("¿Estás seguro de que deseas eliminar esta foto?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario confirma la eliminación, eliminar la imagen
                removeImageView(view);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario cancela la eliminación, cerrar el cuadro de diálogo
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void removeImageView(View view) {
        if (view.getParent() instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) view.getParent();
            parentView.removeView(view);
        }
    }

}