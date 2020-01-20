package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
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

import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.facebook.FacebookSdk.getApplicationContext;

public class View_Capsule extends Fragment implements SensorEventListener {

    private ImageView compass;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    List<capsulelocdatas> allcapsules = new ArrayList<capsulelocdatas>();
    private double mylat;
    private double mylong;
    private double myal;

    private GpsTracker gpsTracker;


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
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        compass = (ImageView) view.findViewById(R.id.compass);

        //현재 위치가 뜨게 만들고
        //TODO : 모든 캡슐의 위치를 받아와야 합니다.(Retrofit를 써보도록 합시다.) (1번의 통신) Capsulelocdata
        //TODO : 현재 자신의 위치에서 가까운 녀석들만 떠야 합니다.
        //TODO : 계속해서 갱신해줘야 합니다.
        final TextView textview_address = (TextView)view.findViewById(R.id.textview);
        gpsTracker = new GpsTracker(getContext());
        mylat = gpsTracker.getLatitude();
        mylong = gpsTracker.getLongitude();
        myal = gpsTracker.getAltitude();
        String address = getCurrentAddress(mylat, mylong);
        textview_address.setText(""+mylat+" "+mylong);

        //모든 캡슐들을  appcapsules라는 변수안에 집어넣음.
        loadAllCapsules();




        return view;
    }


    @Override
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor==mAccelerometer){
            System.arraycopy(event.values, 0, mLastAccelerometer, 0 , event.values.length);
            mLastAccelerometerSet = true;
        }else if(event.sensor == mMagnetometer){
            System.arraycopy(event.values, 0, mLastMagnetometer, 0 , event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastMagnetometerSet && mLastAccelerometerSet){
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            float azimuthinDegress = (int) (Math.toDegrees(SensorManager.getOrientation(mR, mOrientation)[0])+ 360)%360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthinDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            ra.setDuration(250);
            ra.setFillAfter(true);
            compass.startAnimation(ra);
            mCurrentDegree = -azimuthinDegress;
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
}
