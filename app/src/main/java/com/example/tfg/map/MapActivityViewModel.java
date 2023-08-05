package com.example.tfg.map;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ResolvableApiException;

public class MapActivityViewModel extends ViewModel {

    private MapRepository mapRepository;
    private MutableLiveData<Boolean> locationSettingResponse = new MutableLiveData<>();
    private MutableLiveData<ResolvableApiException> resolvableApiException = new MutableLiveData<>();

    public MapActivityViewModel() {
        mapRepository = new MapRepository();
    }

    public LiveData<Boolean> getPermissionsStatus() {
        return mapRepository.getPermissionsStatus();
    }

    public void checkAndRequestPermissions(Activity activity) {
        mapRepository.checkAndRequestPermissions(activity);
    }

    public LiveData<Boolean> getLocationSettingResponse() {
        return locationSettingResponse;
    }

    public LiveData<ResolvableApiException> getResolvableApiException() {
        return resolvableApiException;
    }

    public void checkLocationSettings(Context context) {
        mapRepository.checkLocationSettings(context, new MapRepository.LocationSettingsCallback() {
            @Override
            public void onLocationSettingsSuccess() {
                locationSettingResponse.setValue(true);
            }

            @Override
            public void onResolvableApiException(ResolvableApiException e) {
                resolvableApiException.setValue(e);
            }

            @Override
            public void onLocationSettingsFailed() {
                locationSettingResponse.setValue(false);
            }
        });
    }

}
