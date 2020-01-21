package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.opengl.GLES20;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;




import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;



import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;

import static android.content.Context.SENSOR_SERVICE;
import static com.example.myapplication.ARNode.modelRenderableCompletableFuture;

public class View_Capsule extends Fragment implements SensorEventListener, Scene.OnUpdateListener {

    //, ImageTrackerListener, ExternalRendering
    private ImageView compass;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private LocationManager locationManager;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    private ArSceneView arView;
    private LocationScene locationScene;
    private Session session;
    private boolean shouldConfigureSession = false;


    public static View_Capsule newInstance() {
        View_Capsule fragmentFirst = new View_Capsule();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) this.getContext().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

        //view
        arView = (ArSceneView) view.findViewById(R.id.arView);

        //request permission
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setupSession();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        initSceneView();
        compass = (ImageView) view.findViewById(R.id.compass);

        return view;
    }

    private void initSceneView() {
        arView.getScene().addOnUpdateListener(this);
    }

    @Override
    public void onUpdate (FrameTime frameTime) {
        Frame frame = arView.getArFrame();
        Collection<AugmentedImage> updateAugmentedImg = frame.getUpdatedTrackables(AugmentedImage.class);


        //node.setImage(image);

        if (locationScene == null) {

            locationScene = new LocationScene(getActivity(), arView);

            ARNode node = new ARNode(getContext(), R.raw.bottle5);
            //node.setWorldScale(new Vector3(0.1f, 0.1f, 0.1f));
            node.setNode();
            node.setOnTapListener((v, event) -> {
                Toast.makeText(getContext(), "capsule touched", Toast.LENGTH_LONG).show();
            });


            // 특정 위치에 캡슐 달기
            //locationScene.mLocationMarkers.add(new LocationMarker(36.3740013, 127.3658097, node));
            //locationScene.mLocationMarkers.add(new LocationMarker(36.3740187, 127.3658176, node));
            locationScene.mLocationMarkers.add(new LocationMarker(36.3740131, 127.365795, node));
            Log.i("location","added location 2222");


        }


        if (locationScene != null) {
            locationScene.processFrame(frame);
        }


    }

    private void setupSession() {
        if (session == null) {
            try {
                session = new Session (getContext());
            } catch (UnavailableArcoreNotInstalledException e) {
                e.printStackTrace();
            } catch (UnavailableApkTooOldException e) {
                e.printStackTrace();
            } catch (UnavailableDeviceNotCompatibleException e) {
                e.printStackTrace();
            } catch (UnavailableSdkTooOldException e) {
                e.printStackTrace();
            }

            shouldConfigureSession = true;
        }

        if (shouldConfigureSession) {
            configSession();
            shouldConfigureSession = false;
            arView.setupSession(session);
        }

        try {
            session.resume();
            arView.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
            session = null;
        }
    }

    private void configSession() {
        Config config = new Config(session);
        config.setFocusMode(Config.FocusMode.AUTO);
//        if (!buildDatabase(config)) {
//            //
//        }

        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        session.configure(config);
    }

    private boolean buildDatabase(Config config) {
        AugmentedImageDatabase augmentedImageDatabase;
//        Bitmap bitmap = loadImage();
//        if (bitmap == null) {
//            return false;
//        }
//        augmentedImageDatabase = new AugmentedImageDatabase(session);
//        //augmentedImageDatabase.addImage(s)
//        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }

//    private Bitmap loadImage() {
//        InputStream is = getAssets().open();
//    }


    @Override
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setupSession();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);

        if (session != null) {
            arView.pause();
            session.pause();
        }

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


}
