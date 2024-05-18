package com.example.tfg.route;

import com.google.android.gms.common.api.ApiException;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;

public class DirectionsHelper {
    public static DirectionsResult getDirections(com.google.maps.model.LatLng origin, com.google.maps.model.LatLng destination) throws InterruptedException, ApiException, IOException, com.google.maps.errors.ApiException {
        GeoApiContext context = new GeoApiContext.Builder()
                // Meter la key de google maps, no la pongo para que no me la robe ningun pirata :)
                .apiKey("Google key")
                .build();

        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(origin)
                .destination(destination)
                .mode(TravelMode.WALKING);

        return request.await();
    }
}
