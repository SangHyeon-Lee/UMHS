package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;

public class View_Capsule extends Fragment implements SensorEventListener, LocationListener {

    private ImageView compass;
    private SensorManager mSensorManager;
    private ImageView dot;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currentAzimuth = 0f;
    private TextView latitude_text;
    private TextView longitude_text;
    private LocationManager locationManager;
    private String provider;
    private double[] sampleGPS = {36.374510, 127.364821, 0};
    private static final int REQUEST_CODE_LOCATION = 2;

    public static View_Capsule newInstance() {
        View_Capsule fragmentFirst = new View_Capsule();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_view_capsule, container, false);

        ImageButton button = view.findViewById(R.id.button_addpost);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Add_Post.class);
                startActivity(intent);
            }
        });


        mSensorManager = (SensorManager) this.getContext().getSystemService(SENSOR_SERVICE);
        dot = (ImageView) view.findViewById(R.id.dot);
        compass = (ImageView) view.findViewById(R.id.compass);
        latitude_text = (TextView) view.findViewById(R.id.latitude);
        longitude_text = (TextView) view.findViewById(R.id.longitude);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);


        Location userLocation = getMyLocation();
        if (userLocation != null) {
            double latitude = userLocation.getLatitude();
            double longitude = userLocation.getLongitude();
            latitude_text.setText(""+ latitude);
            longitude_text.setText(""+ longitude);
            double dis_x = Get_Distance(latitude, sampleGPS[1], latitude, longitude);
            double dis_y = Get_Distance(sampleGPS[0], longitude, latitude, longitude);
            double dis = Get_Distance(sampleGPS[0], sampleGPS[1], latitude, longitude);

            if (sampleGPS[0]>latitude){
                dis_y = -dis_y;
            }
            if (sampleGPS[1]<longitude){
                dis_x = -dis_x;
            }
            dot.setX( 138f + (float) dis_x*107/200);
            dot.setY( 143f + (float) dis_y*107/200);
        }


        return view;
    }


    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates

        currentLocation = getLastKnownLocation();
        if (currentLocation != null) {
            double lng = currentLocation.getLongitude();
            double lat = currentLocation.getLatitude();
        }

        return currentLocation;
    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.

            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final float alpha = 0.97f;
        synchronized (this) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mGravity[0] = alpha * mGravity[0] + (1-alpha)*sensorEvent.values[0];
                mGravity[1] = alpha * mGravity[1] + (1-alpha)*sensorEvent.values[1];
                mGravity[2] = alpha * mGravity[2] + (1-alpha)*sensorEvent.values[2];
            }
            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1-alpha)*sensorEvent.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1-alpha)*sensorEvent.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1-alpha)*sensorEvent.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I,mGravity, mGeomagnetic);
            if (success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth+360)%360;

                Animation anim = new RotateAnimation(currentAzimuth, azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
                currentAzimuth = azimuth;
                anim.setDuration(500);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);

                compass.startAnimation(anim);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    public double Get_Distance(double _latitude1, double _longitude1, double _latitude2, double _longitude2){
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(_latitude1);
        startPos.setLongitude(_longitude1);
        endPos.setLatitude(_latitude2);
        endPos.setLongitude(_longitude2);

        double distance = startPos.distanceTo(endPos);

        return distance;
    }


    @Override
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        double altitude = location.getAltitude();

        latitude_text.setText((int) latitude);
        longitude_text.setText((int) longitude);
        double dis_x = Get_Distance(latitude, sampleGPS[1], latitude, longitude);
        double dis_y = Get_Distance(sampleGPS[0], longitude, latitude, longitude);
        if (sampleGPS[0]>latitude){
            dis_y = -dis_y;
        }
        if (sampleGPS[1]<longitude){
            dis_x = -dis_x;
        }
        dot.setX( 138f + (float) dis_x*107/200);
        dot.setY( 143f + (float) dis_y*107/200);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
