package com.example.landmarkremark.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.landmarkremark.helper.Constants;
import com.example.landmarkremark.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.landmarkremark.helper.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private MarkerOptions markerOptions;
    private LocationListener locationListener;

    private LatLng updatedLocation = new LatLng(-34, 151);     //Default LatLng of Sydney

    private boolean isFirstLocation = true;         //boolean to track if the current location is accessed for the first time

    private String address = "";

    private Marker currentMarker = null;

    //interface to interact with the MainActivity
    public interface LocationListener {
        public void onLocationObtained(LatLng location, String address);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //check if the MainActivity has implemented the interface from this fragment
        if (context instanceof LocationListener) {
            locationListener = (LocationListener) context;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        //setup locationRequest to implement LocationCallback
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //current location from the Location API
                        updatedLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        if (locationListener != null) {
                            String currentAddress = getLocationFromLatLng(updatedLocation.latitude, updatedLocation.longitude);

                            if (address.equalsIgnoreCase(currentAddress)) {
                                //if the address hasn't changed return from this calback method
                                //i.e. do nothing
                                return;
                            }

                            address = currentAddress;  //update the current address if the location has changed
                            locationListener.onLocationObtained(updatedLocation, address);      //return the result to the MainActivity through LocationListener interface
                            if(currentMarker !=null){
                                currentMarker.setPosition(updatedLocation);
                            }

                        }
                        if (isFirstLocation) {
                            //if the location is being accessed for the first time which means at the first installation of the app, move the map camera to the current location
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), Constants.ZOOM_LEVEL));
                            isFirstLocation = false;
                            //markerOptions to put a marker with a current location on a map
                            markerOptions = new MarkerOptions();

                            markerOptions.position(updatedLocation).title(address);
                            currentMarker = mMap.addMarker(markerOptions);


                        }
                    }
                }
            }
        };
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        //request for location updates every the fragment is in display
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationClient != null) {
            //remove for location updates is fragment is not in main stack or not in display
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(updatedLocation, Constants.ZOOM_LEVEL));
        } else {
            //prompt user to request location permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }

    //method to get the addressLine from the given latitude and longitude
    private String getLocationFromLatLng(Double lat, Double lng) {
        String addressLine = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault()); //Geocoder API to convert lat long to the place

        //getFromLocation returns multiple addresses within the surroundings of the lat long
        //we only require 1 address so maxResult= 1
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            addressLine = addresses.get(0).getAddressLine(0);  //may have multiple addresslines, but only access one
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressLine;
    }


}