package com.theta360.sample.v2;

/**
 * Created by bailey on 3/30/16.
 */

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.theta360.sample.v2.model.DatabaseHandler;
import com.theta360.sample.v2.model.Folder;
import com.theta360.sample.v2.network.DeviceInfo;
import com.theta360.sample.v2.network.HttpConnector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private boolean mConnectionSwitchEnabled = false;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private String cameraIpAddress;
    private ConnectRicoh ConnectRicoh = null;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    Button camera360;
    private Uri fileUri; // file url to store image/video

    //private ImageView imgPreview;
    private VideoView videoPreview;
    private Button btnCapturePicture, btnRecordVideo;
    final DatabaseHandler db = new DatabaseHandler(this);
    private String foldername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraIpAddress = getResources().getString(R.string.theta_ip_address);
        //imgPreview = (ImageView) findViewById(R.id.imgPreview);
        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
        camera360 = (Button) findViewById(R.id.camera360);
        forceConnectToWifi();

        db.addContact(new Folder(1, "Reylabs"));


        Log.v("foldername", "setting in usual camera " + foldername);

        /*
         * Capture image button click event
		 */
        ActionBar actionBar = getActionBar();
        //액션바 객체 얻어서
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1f2332"))); // 색상 변경(색상코드)﻿
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                foldername = db.getContactname(1);
                Log.v("foldername", "ㅎㅎ 업데이트좀 " + foldername);
                captureImage();
            }
        });


        Button Setting = (Button) findViewById(R.id.SettingBtn);
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                i.putExtra("wifiname", getCurrentSsid(getApplicationContext()));
                startActivity(i);
            }
        });


        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(MainActivity.this,
                    "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectRicoh = new ConnectRicoh();
        ConnectRicoh.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mConnectionSwitchEnabled != false) {
            ConnectRicoh = new ConnectRicoh();
            ConnectRicoh.execute();
        }
    }


    @Override
    protected void onDestroy() {

        if (mConnectionSwitchEnabled != false) {
            ConnectRicoh.cancel(true);
        }

        super.onDestroy();
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /*
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                Log.v("foldername", "저장 누르면 저장할 폴더 이름 " + foldername);
                storeCapturedImage();


            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(MainActivity.this, "User cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(MainActivity.this,
                        "Sorry! Failed to save image", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    /*
     * Display image from a path to ImageView
     */
    private void storeCapturedImage() {
        Log.v("foldername", "저장할 폴더 이름 " + foldername);
        foldername = db.getContactname(1);
        File extStorageDirectory = new File(Environment.getExternalStorageDirectory() + "/" + foldername);

        boolean success = true;
        if (!extStorageDirectory.exists()) {
            success = extStorageDirectory.mkdir();
        }
        if (success) {
            // Do something on success
            try {
                File[] files = extStorageDirectory.listFiles();
                int numberOfImages = files.length;
                int cnt = 0;
                Log.v("foldername", "directory  " + String.valueOf(extStorageDirectory));
                Log.v("foldername", "how much int  " + numberOfImages);

                if (numberOfImages >= cnt) {
                    cnt = numberOfImages + 1;
                }
                File file = new File(extStorageDirectory, "/" + foldername + "-" + cnt + ".JPG");
                FileOutputStream outStream = new FileOutputStream(file);
                // hide video preview
                //videoPreview.setVisibility(View.GONE);
                //imgPreview.setVisibility(View.VISIBLE);

                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;

                final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                Toast.makeText(getApplicationContext(), "Image is saved",
                        Toast.LENGTH_LONG).show();
                //	imgPreview.setImageBitmap(bitmap);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Do something else on failure
        }

    }

    /**
     * ------------ Helper Methods ----------------------
     */

	/*
     * Creating file uri to store image/video
	 */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Force this applicatioin to connect to Wi-Fi
     * connect to wifi
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void forceConnectToWifi() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if ((info != null) && info.isAvailable()) {
                NetworkRequest.Builder builder = new NetworkRequest.Builder();
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                NetworkRequest requestedNetwork = builder.build();

                ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);

                        ConnectivityManager.setProcessDefaultNetwork(network);
                        mConnectionSwitchEnabled = true;
                        //invalidateOptionsMenu();
                        Log.v("mainwifi", "connect to Wi-Fi AP");
                    }

                    @Override
                    public void onLost(Network network) {
                        super.onLost(network);

                        mConnectionSwitchEnabled = false;
                        //invalidateOptionsMenu();
                        Log.v("mainwifi", "lost connection to Wi-Fi AP");
                    }
                };

                cm.requestNetwork(requestedNetwork, callback);
            }
        } else {
            mConnectionSwitchEnabled = true;
            //invalidateOptionsMenu();
            Log.v("mainwifi", "connect to Wi-Fi AP");


        }
    }

    private class ConnectRicoh extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpConnector camera = new HttpConnector(cameraIpAddress);
            Log.v("mainwifi", "rcameraIpAddress       :" + cameraIpAddress);

            DeviceInfo deviceInfo = camera.getDeviceInfo();
            Log.v("mainwifi", "deviceInfo       :" + deviceInfo);
            if (!deviceInfo.getModel().equals("")) {
                try {
                    Log.v("mainwifi", "------");
                    Log.v("mainwifi", "connecting to " + cameraIpAddress + "...");
                    Log.v("mainwifi", "------");
                    publishProgress();
                    //HttpConnector camera = new HttpConnector(cameraIpAddress);
                    //which we connected wifi name
                    return deviceInfo.getModel();

                } catch (Throwable throwable) {
                    String errorLog = Log.getStackTraceString(throwable);
                    //publishProgress(errorLog);
                    return null;
                }
            }
            return deviceInfo.getModel();
        }

        @Override
        protected void onPostExecute(final String result) {

            camera360.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // capture picture
                    if (result.equals(null)) {
                        Log.v("mainwifi", "STATE       :" + "null");
                        Toast.makeText(MainActivity.this, "Please connect to wifi", Toast.LENGTH_SHORT).show();
                    } else if (result.equals("")) {
                        Log.v("mainwifi", "STATE       :" + "NULL");
                        Toast.makeText(MainActivity.this, "Please connect to device", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v("mainwifi", "STATE       :" + result);
                        Log.v("mainwifi", "true??       " + String.valueOf(mConnectionSwitchEnabled));
                        Toast.makeText(MainActivity.this, "connected to the device", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, Camera360MainActivity.class);
                        i.putExtra("mConnectionSwitchEnabled", mConnectionSwitchEnabled);
                        startActivity(i);
                    }
                }
            });

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //get the wifi name
    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }


}
