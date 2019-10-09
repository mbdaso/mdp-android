/*
 * Mobile Devices Programming
 * Antonio da Silva, Ana Belén García
 * Course 2018-2019
 * API Google Maps example
 * Class automatically generated by AndroidStudio
 */

package dte.masteriot.mdp.mapdemo;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.Vector;

public class    MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    boolean delete = false;

    Vector<Polyline> lastPoly;
    LatLng ETSITCoord = new LatLng(40.389877, -3.629053);

    String TAG = "pepe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.addMarker(new MarkerOptions().position(ETSITCoord).title("Marker in ETSIT UPM"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ETSITCoord));

        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick( LatLng point ) {
//        if(delete){
//            for(Polyline p : lastPoly){
//                p.remove();
//            }
//            lastPoly.clear();
//        }
//        else{
//            lastPoly.add(mMap.addPolyline(new PolylineOptions()
//                    .add(new LatLng(point.latitude-10, point.longitude), new LatLng(point.latitude, point.longitude+10))
//                    .width(5)
//                    .color(Color.RED)));
//            lastPoly.add(mMap.addPolyline(new PolylineOptions()
//                    .add(new LatLng(point.latitude, point.longitude+10), new LatLng(point.latitude+10, point.longitude))
//                    .width(5)
//                    .color(Color.RED)));
//            lastPoly.add(mMap.addPolyline(new PolylineOptions()
//                    .add(new LatLng(point.latitude+10, point.longitude), new LatLng(point.latitude, point.longitude-10))
//                    .width(5)
//                    .color(Color.RED)));
//            lastPoly.add(mMap.addPolyline(new PolylineOptions()
//                    .add(new LatLng(point.latitude, point.longitude-10), new LatLng(point.latitude-10, point.longitude))
//                    .width(5)
//                    .color(Color.RED)));
//        }
        delete = !delete;
        Toast.makeText(this, "Map Click: "+ point.toString(), Toast.LENGTH_SHORT).show();
    }
}
