package com.example.myapplication;

import androidx.annotation.NonNull;
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
import android.location.Address;
import android.location.Geocoder;

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
import android.widget.Toast;

import com.example.myapplication.dbmodels.capsuledatas;
import com.example.myapplication.dbmodels.capsulelocdatas;
import com.example.myapplication.network.RetrofitService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.facebook.FacebookSdk.getApplicationContext;

public class View_Capsule extends Fragment implements SensorEventListener, LocationListener {

    private ImageView compass;
    private SensorManager mSensorManager;

    List<capsulelocdatas> allcapsules = new ArrayList<capsulelocdatas>();
    private double mylat;
    private double mylong;
    private double myal;

    private GpsTracker gpsTracker;

    private ArrayList<ImageView> dots;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currentAzimuth = 0f;
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

        dots = new ArrayList<>();
        dots.add((ImageView) view.findViewById(R.id.dot0));
        dots.add((ImageView) view.findViewById(R.id.dot1));
        dots.add((ImageView) view.findViewById(R.id.dot2));
        dots.add((ImageView) view.findViewById(R.id.dot3));
        dots.add((ImageView) view.findViewById(R.id.dot4));
        dots.add((ImageView) view.findViewById(R.id.dot5));
        dots.add((ImageView) view.findViewById(R.id.dot6));
        dots.add((ImageView) view.findViewById(R.id.dot7));
        dots.add((ImageView) view.findViewById(R.id.dot8));
        dots.add((ImageView) view.findViewById(R.id.dot9));

        mSensorManager = (SensorManager) this.getContext().getSystemService(SENSOR_SERVICE);
        compass = (ImageView) view.findViewById(R.id.compass);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        for (int i =0; i<dots.size(); i++){
            dots.get(i).setVisibility(View.INVISIBLE);
        }

        //현재 위치가 뜨게 만들고
        //TODO : 모든 캡슐의 위치를 받아와야 합니다.(Retrofit를 써보도록 합시다.) (1번의 통신) Capsulelocdata
        //TODO : 현재 자신의 위치에서 가까운 녀석들만 떠야 합니다.
        //TODO : 계속해서 갱신해줘야 합니다.

        gpsTracker = new GpsTracker(getContext());
        mylat = gpsTracker.getLatitude();
        mylong = gpsTracker.getLongitude();
        myal = gpsTracker.getAltitude();
        String address = getCurrentAddress(mylat, mylong);

        //모든 캡슐들을  appcapsules라는 변수안에 집어넣음.
        loadAllCapsules();

        for (int j = 0; j< allcapsules.size(); j++){
            double cap_lat = allcapsules.get(j).getLatitude();
            double cap_long = allcapsules.get(j).getLongtitude();

            double dis_x = Get_Distance(mylat, cap_long, mylat, mylong);
            double dis_y = Get_Distance(cap_lat, mylong, mylat, mylong);
            double dis = Get_Distance(cap_lat, cap_long, mylat, mylong);

            if (dis>200){
                continue;
            }

            if (sampleGPS[0]>mylat){
                dis_y = -dis_y;
            }
            if (sampleGPS[1]<mylong){
                dis_x = -dis_x;
            }
            dots.get(j).setVisibility(View.VISIBLE);
            dots.get(j).setX( 138f + (float) dis_x*107/200);
            dots.get(j).setY( 143f + (float) dis_y*107/200);
        }
        Button show = view.findViewById(R.id.show_capsule);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllCapsules();
                Intent intent2 = new Intent(getContext(), Posts_CardView.class);
                intent2.putExtra("id", allcapsules.get(0).getCapsuleId());
                startActivity(intent2);
            }
        });


        return view;
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


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }

    public void loadAllCapsules(){
        //모든 캡슐의 위치정보를 받아옵니다.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitExService = retrofit.create(RetrofitService.class);
        retrofitExService.getCapsules().enqueue(new Callback<List<capsulelocdatas>>() {
            @Override
            public void onResponse(@NonNull Call<List<capsulelocdatas>> call, @NonNull Response<List<capsulelocdatas>> response) {
                if (response.isSuccessful()) {
                    List<capsulelocdatas> body = response.body();
                    if (body != null) {
                        allcapsules = body;
                        Toast.makeText(getApplicationContext(),
                                "total length is "+allcapsules.size(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<capsulelocdatas>> call, @NonNull Throwable t) {
            }
        });
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
        for (int i =0; i<dots.size(); i++){
            dots.get(i).setVisibility(View.INVISIBLE);
        }
        for (int j = 0; j< allcapsules.size(); j++){
            double cap_lat = allcapsules.get(j).getLatitude();
            double cap_long = allcapsules.get(j).getLongtitude();

            double dis_x = Get_Distance(mylat, cap_long, mylat, mylong);
            double dis_y = Get_Distance(cap_lat, mylong, mylat, mylong);
            double dis = Get_Distance(cap_lat, cap_long, mylat, mylong);

            if (dis>200){
                continue;
            }

            if (sampleGPS[0]>mylat){
                dis_y = -dis_y;
            }
            if (sampleGPS[1]<mylong){
                dis_x = -dis_x;
            }
            dots.get(j).setVisibility(View.VISIBLE);
            dots.get(j).setX( 138f + (float) dis_x*107/200);
            dots.get(j).setY( 143f + (float) dis_y*107/200);
        }
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
