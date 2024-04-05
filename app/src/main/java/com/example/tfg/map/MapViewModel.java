package com.example.tfg.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class MapViewModel extends ViewModel {

    private MutableLiveData<Boolean> locationSettingResponse = new MutableLiveData<>();
    private MutableLiveData<ResolvableApiException> resolvableApiException = new MutableLiveData<>();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public LiveData<Boolean> getLocationSettingResponse() {
        return locationSettingResponse;
    }

    public LiveData<ResolvableApiException> getResolvableApiException() {
        return resolvableApiException;
    }

    public void checkAndRequestPermissions(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void checkLocationSettings(Context context) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            MapViewModel.this.locationSettingResponse.setValue(true);
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                MapViewModel.this.resolvableApiException.setValue((ResolvableApiException) e);
            } else {
                MapViewModel.this.locationSettingResponse.setValue(false);
            }
        });
    }
}
