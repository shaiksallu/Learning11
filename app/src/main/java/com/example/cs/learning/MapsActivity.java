package com.example.cs.learning;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,OnStreetViewPanoramaReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mf;
    GPSTracker gps;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);*/

        mf = ((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map));
        mMap = mf.getMap();

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);



        gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){

             latitude = gps.getLatitude();
             longitude = gps.getLongitude();

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

      //  mf.getMapAsync(this);
        setupmap();
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    private void setupmap() {
        // TODO Auto-generated method stub
        // Getting status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapsActivity.this);

        // Showing status
        if(status== ConnectionResult.SUCCESS)
        {
            try{
                CameraPosition cameraPosition = new CameraPosition.Builder()

                        .target(new LatLng(latitude,longitude))    // Sets the center of the map to Mountain View
                        .zoom(15)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                MarkerOptions marker=new MarkerOptions().position( new LatLng(latitude,longitude
                )).title("Salman");
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                mMap.addMarker(marker);

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

            }catch(NumberFormatException nfe){

            }
            // Setting a custom info window adapter for the google map
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {

                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();

                    // Getting reference to the TextView to set latitude
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                    // Getting reference to the TextView to set longitude
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

                    // Setting the latitude
                    tvLat.setText("\n("+latLng+")");

                    // Setting the longitude
                    tvLng.setText(arg0.getTitle());

                    // Returning the view containing InfoWindow contents
                    return v;

                }
            });
        }
        else{

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, MapsActivity.this, requestCode);
            dialog.show();
        }


    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

        Toast.makeText(getApplicationContext(), "Your streetViewPanorama is - \nLat: "
                + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        //streetViewPanorama.setPosition(new LatLng(latitude, longitude));
        streetViewPanorama.setPosition(new LatLng(-33.87365, 151.20689));

        StreetViewPanoramaCamera.Builder builder = new StreetViewPanoramaCamera.Builder( streetViewPanorama.getPanoramaCamera() );
        builder.tilt( 0.0f );
        builder.zoom( 0.0f );
        builder.bearing( 0.0f );
        streetViewPanorama.animateTo( builder.build(), 0 );

        streetViewPanorama.setPosition( new LatLng(-33.87365, 151.20689), 300 );
        streetViewPanorama.setStreetNamesEnabled( true );


    }
}
