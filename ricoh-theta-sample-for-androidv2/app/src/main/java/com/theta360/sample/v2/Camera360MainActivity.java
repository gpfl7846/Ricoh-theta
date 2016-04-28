package com.theta360.sample.v2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.theta360.sample.v2.model.DatabaseHandler;
import com.theta360.sample.v2.model.ImageSize;
import com.theta360.sample.v2.network.DeviceInfo;
import com.theta360.sample.v2.network.HttpConnector;
import com.theta360.sample.v2.network.HttpEventListener;
import com.theta360.sample.v2.network.ImageInfo;
import com.theta360.sample.v2.network.StorageInfo;
import com.theta360.sample.v2.view.ImageListArrayAdapter;
import com.theta360.sample.v2.view.ImageRow;
import com.theta360.sample.v2.view.ImageSizeDialog;
import com.theta360.sample.v2.view.MJpegInputStream;
import com.theta360.sample.v2.view.MJpegView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Activity that displays the photo list
 */

public class Camera360MainActivity extends Activity implements ImageSizeDialog.DialogBtnListener {
    private ListView objectList;
    //private LogView logViewer;
    private String cameraIpAddress;
    private LinearLayout layoutCameraArea;
    private Button btnShoot;
    private TextView textCameraStatus;
    private Button btnImageSize;
    private ImageSize currentImageSize;
    private MJpegView mMv;
    private boolean mConnectionSwitchEnabled = false;
    private String foldername;
    private LoadObjectListTask sampleTask = null;
    private ShowLiveViewTask livePreviewTask = null;
    //private GetImageSizeTask getImageSizeTask = null;
    //get the masterfolder
    final DatabaseHandler db = new DatabaseHandler(this);

    /**
     * onCreate Method
     *
     * @param savedInstanceState onCreate Status value
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        //action bar
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#cb002f"))); // 색상 변경(색상코드)﻿
        actionBar.setDisplayShowHomeEnabled(false);

        setContentView(R.layout.activity_camera_360);
        cameraIpAddress = getResources().getString(R.string.theta_ip_address);
        //getActionBar().setTitle(cameraIpAddress);

        layoutCameraArea = (LinearLayout) findViewById(R.id.shoot_area);
        textCameraStatus = (TextView) findViewById(R.id.camera_status);
        btnShoot = (Button) findViewById(R.id.btn_shoot);

        Log.v("foldername", "location in 360 Ricoh-theta " + foldername);
        //take a picture in the applications setOnclickListener
        btnShoot.setOnClickListener(new OnClickListener() {
            //when it is false we say NOT CONNECTED
            @Override
            public void onClick(View v) {
                btnShoot.setEnabled(false);
                textCameraStatus.setText(R.string.text_camera_synthesizing);
                new ShootTask().execute();

            }
        });

        mMv = (MJpegView) findViewById(R.id.live_view);
        mConnectionSwitchEnabled = getIntent().getExtras().getBoolean("mConnectionSwitchEnabled");

        //wifi name
        getCurrentSsid(getApplicationContext());
        //GET WIFI NAME
        appendLogView("NAME" + getCurrentSsid(getApplicationContext()));
        //to see a gallery
        Button gallery = (Button)findViewById(R.id.gallery);

        gallery.setOnClickListener(new OnClickListener() {
            //when it is false we say NOT CONNECTED
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Camera360MainActivity.this, Album_ImageList.class);
                startActivity(i);
            }
        });

        objectList = (ListView) findViewById(R.id.object_list);
        ImageListArrayAdapter empty = new ImageListArrayAdapter(Camera360MainActivity.this, R.layout.listlayout_object, new ArrayList<ImageRow>());
        objectList.setAdapter(empty);

        layoutCameraArea.setVisibility(View.VISIBLE);
        //check it is connected
        if (sampleTask == null) {
            sampleTask = new LoadObjectListTask();
            sampleTask.execute();

        }
        if (livePreviewTask == null) {
            livePreviewTask = new ShowLiveViewTask();
            livePreviewTask.execute(cameraIpAddress);


        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mMv.stopPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMv.play();

        if (livePreviewTask != null) {
            livePreviewTask.cancel(true);
            livePreviewTask = new ShowLiveViewTask();
            livePreviewTask.execute(cameraIpAddress);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GLPhotoActivity.REQUEST_REFRESH_LIST) {
            if (sampleTask != null) {
                sampleTask.cancel(true);
            }
            sampleTask = new LoadObjectListTask();
            sampleTask.execute();
        }
    }

    @Override
    protected void onDestroy() {
        if (sampleTask != null) {
            sampleTask.cancel(true);
        }

        if (livePreviewTask != null) {
            livePreviewTask.cancel(true);
        }

        super.onDestroy();
    }


    private void changeCameraStatus(final String resid) {
        runOnUiThread(new Runnable() {
            public void run() {
                //textCameraStatus.setText(resid+""+getCurrentSsid(getApplicationContext()));
                textCameraStatus.setText(resid);
            }
        });
    }

    private void appendLogView(final String log) {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.v("logviewer", "Thread		 " + log);
                //logViewer.append(log);
            }
        });
    }

    @Override
    public void onDialogCommitClick(ImageSize imageSize) {
        currentImageSize = imageSize;
        // new ChangeImageSizeTask().execute(currentImageSize);
    }

    private class ShowLiveViewTask extends AsyncTask<String, String, MJpegInputStream> {
        @Override
        protected MJpegInputStream doInBackground(String... ipAddress) {
            MJpegInputStream mjis = null;
            final int MAX_RETRY_COUNT = 20;

            for (int retryCount = 0; retryCount < MAX_RETRY_COUNT; retryCount++) {
                try {
                    publishProgress("start Live view");
                    HttpConnector camera = new HttpConnector(ipAddress[0]);
                    InputStream is = camera.getLivePreview();
                    mjis = new MJpegInputStream(is);
                    retryCount = MAX_RETRY_COUNT;
                } catch (IOException e) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (JSONException e) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            return mjis;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (String log : values) {
                Log.v("logviewer", "ShowLiveViewTask		" + log);
                //logViewer.append(log);
            }
        }

        @Override
        protected void onPostExecute(MJpegInputStream mJpegInputStream) {
            if (mJpegInputStream != null) {
                mMv.setSource(mJpegInputStream);
            } else {
                Log.v("logviewer", "failed to start live view");
                //logViewer.append("failed to start live view");
            }
        }
    }

    private class LoadObjectListTask extends AsyncTask<Void, String, List<ImageRow>> {

        private ProgressBar progressBar;

        public LoadObjectListTask() {
            progressBar = (ProgressBar) findViewById(R.id.loading_object_list_progress_bar);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<ImageRow> doInBackground(Void... params) {
            HttpConnector camera = new HttpConnector(cameraIpAddress);
            DeviceInfo deviceInfo = camera.getDeviceInfo();
            if (!deviceInfo.getModel().equals("")) {
                try {
                    publishProgress("------");
                    publishProgress("connecting to " + cameraIpAddress + "...");
                    //HttpConnector camera = new HttpConnector(cameraIpAddress);
                    //which we connected wifi name
                    changeCameraStatus("Connected:	" + getCurrentSsid(getApplicationContext()));

                    publishProgress("connected.");
                    publishProgress(deviceInfo.getClass().getSimpleName() + ":<" + deviceInfo.getModel() + ", " + deviceInfo.getDeviceVersion() + ", " + deviceInfo.getSerialNumber() + ">");

                    List<ImageRow> imageRows = new ArrayList<>();

                    StorageInfo storage = camera.getStorageInfo();
                    ImageRow storageCapacity = new ImageRow();
                    int freeSpaceInImages = storage.getFreeSpaceInImages();
                    int megaByte = 1024 * 1024;
                    long freeSpace = storage.getFreeSpaceInBytes() / megaByte;
                    long maxSpace = storage.getMaxCapacity() / megaByte;
                    storageCapacity.setFileName("Free space: " + freeSpaceInImages + "[shots] (" + freeSpace + "/" + maxSpace + "[MB])");
                    imageRows.add(storageCapacity);

                    ArrayList<ImageInfo> objects = camera.getList();
                    int objectSize = objects.size();

                    for (int i = 0; i < objectSize; i++) {
                        ImageRow imageRow = new ImageRow();
                        ImageInfo object = objects.get(i);
                        imageRow.setFileId(object.getFileId());
                        imageRow.setFileSize(object.getFileSize());
                        imageRow.setFileName(object.getFileName());
                        imageRow.setCaptureDate(object.getCaptureDate());
                        publishProgress("<ImageInfo: File ID=" + object.getFileId() + ", filename=" + object.getFileName() + ", capture_date=" + object.getCaptureDate()
                                + ", image_pix_width=" + object.getWidth() + ", image_pix_height=" + object.getHeight() + ", object_format=" + object.getFileFormat()
                                + ">");

                        if (object.getFileFormat().equals(ImageInfo.FILE_FORMAT_CODE_EXIF_JPEG)) {
                            imageRow.setIsPhoto(true);
                            Bitmap thumbnail = camera.getThumb(object.getFileId());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            final byte[] thumbnailImage = baos.toByteArray();
                            imageRow.setThumbnail(thumbnailImage);

                        } else {
                            imageRow.setIsPhoto(false);
                        }
                        imageRows.add(imageRow);

                        publishProgress("getList: " + (i + 1) + "/" + objectSize);
                    }
                    return imageRows;

                } catch (Throwable throwable) {
                    String errorLog = Log.getStackTraceString(throwable);
                    publishProgress(errorLog);
                    return null;
                }
            } else {

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (String log : values) {
                Log.v("logviewer", "List	" + log);
                //logViewer.append(log);
            }

        }


        @Override
        protected void onPostExecute(List<ImageRow> imageRows) {
            if (imageRows != null) {
                TextView storageInfo = (TextView) findViewById(R.id.storage_info);
                final String info = imageRows.get(0).getFileName();
                imageRows.remove(0);
                storageInfo.setText(info);

                ImageListArrayAdapter imageListArrayAdapter = new ImageListArrayAdapter(Camera360MainActivity.this, R.layout.listlayout_object, imageRows);
                objectList.setAdapter(imageListArrayAdapter);
                objectList.setOnItemClickListener(new OnItemClickListener() {
                    //LIST CLICK
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ImageRow selectedItem = (ImageRow) parent.getItemAtPosition(position);
                        if (selectedItem.isPhoto()) {
                            final byte[] thumbnail = selectedItem.getThumbnail();
                            final String fileId = selectedItem.getFileId();

                            GLPhotoActivity.startActivityForResult(Camera360MainActivity.this, cameraIpAddress, fileId, thumbnail, false);

                        } else {
                            Toast.makeText(getApplicationContext(), "This isn't a photo.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                objectList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    private String mFileId;

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ImageRow selectedItem = (ImageRow) parent.getItemAtPosition(position);
                        mFileId = selectedItem.getFileId();
                        String fileName = selectedItem.getFileName();

                        new AlertDialog.Builder(Camera360MainActivity.this)
                                .setTitle(fileName)
                                .setMessage(R.string.delete_dialog_message)
                                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DeleteObjectTask deleteTask = new DeleteObjectTask();
                                        deleteTask.execute(mFileId);
                                    }
                                })
                                .show();
                        return true;
                    }
                });
            } else {
                Log.v("logviewer", "failed to get image list");
            }

            progressBar.setVisibility(View.GONE);
        }


        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.GONE);
        }

    }

    private class DeleteObjectTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... fileId) {
            publishProgress("start delete file");
            DeleteEventListener deleteListener = new DeleteEventListener();
            HttpConnector camera = new HttpConnector(getResources().getString(R.string.theta_ip_address));
            camera.deleteFile(fileId[0], deleteListener);

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (String log : values) {
                Log.v("logviewer", "DeleteObjectTask	" + log);
                //logViewer.append(log);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //logViewer.append("done");
            Log.v("logviewer", "DeleteObjectTask	done");
        }

        private class DeleteEventListener implements HttpEventListener {
            @Override
            public void onCheckStatus(boolean newStatus) {
                if (newStatus) {
                    appendLogView("deleteFile:FINISHED");
                } else {
                    appendLogView("deleteFile:IN PROGRESS");
                }
            }

            @Override
            public void onObjectChanged(String latestCapturedFileId) {
                appendLogView("delete " + latestCapturedFileId);
            }

            @Override
            public void onCompleted() {
                appendLogView("deleted.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new LoadObjectListTask().execute();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                appendLogView("delete error " + errorMessage);
            }
        }
    }

    private class DisConnectTask extends AsyncTask<Void, String, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                publishProgress("disconnected.");
                return true;

            } catch (Throwable throwable) {
                String errorLog = Log.getStackTraceString(throwable);
                publishProgress(errorLog);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (String log : values) {
                Log.v("logviewer", "DisConnectTask	" + log);
                //logViewer.append(log);
            }
        }
    }

    private class ShootTask extends AsyncTask<Void, Void, HttpConnector.ShootResult> {

        @Override
        protected void onPreExecute() {
            Log.v("logviewer", "takePicture");
            //logViewer.append("takePicture");
        }

        @Override
        protected HttpConnector.ShootResult doInBackground(Void... params) {
            CaptureListener postviewListener = new CaptureListener();
            HttpConnector camera = new HttpConnector(getResources().getString(R.string.theta_ip_address));
            HttpConnector.ShootResult result = camera.takePicture(postviewListener);
            return result;
        }

        @Override
        protected void onPostExecute(HttpConnector.ShootResult result) {
            if (result == HttpConnector.ShootResult.FAIL_CAMERA_DISCONNECTED) {
                Log.v("logviewer", "takePicture:FAIL_CAMERA_DISCONNECTED");

            } else if (result == HttpConnector.ShootResult.FAIL_STORE_FULL) {
                Log.v("logviewer", "takePicture:FAIL_STORE_FULL");

            } else if (result == HttpConnector.ShootResult.FAIL_DEVICE_BUSY) {
                Log.v("logviewer", "takePicture:FAIL_DEVICE_BUSY");

            } else if (result == HttpConnector.ShootResult.SUCCESS) {
                Log.v("logviewer", "takePicture:SUCCESS");

            }
        }

        private class CaptureListener implements HttpEventListener {
            private String latestCapturedFileId;
            private boolean ImageAdd = false;

            @Override
            public void onCheckStatus(boolean newStatus) {
                if (newStatus) {
                    appendLogView("takePicture:FINISHED");
                } else {
                    appendLogView("takePicture:IN PROGRESS");
                }
            }

            @Override
            public void onObjectChanged(String latestCapturedFileId) {
                this.ImageAdd = true;
                this.latestCapturedFileId = latestCapturedFileId;
                appendLogView("ImageAdd:FileId " + this.latestCapturedFileId);
            }

            @Override
            public void onCompleted() {
                appendLogView("CaptureComplete");
                if (ImageAdd) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnShoot.setEnabled(true);
                            textCameraStatus.setText(R.string.text_camera_standby);
                            new GetThumbnailTask(latestCapturedFileId).execute();
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                appendLogView("CaptureError " + errorMessage);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnShoot.setEnabled(true);
                        textCameraStatus.setText(R.string.text_camera_standby);
                    }
                });
            }
        }

    }

    //After take the picture show the image
    private class GetThumbnailTask extends AsyncTask<Void, String, Void> {

        private String fileId;

        public GetThumbnailTask(String fileId) {
            this.fileId = fileId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpConnector camera = new HttpConnector(getResources().getString(R.string.theta_ip_address));
            Bitmap thumbnail = camera.getThumb(fileId);
            String foldername = db.getContactname(1);
            File extStorageDirectory = new File(Environment.getExternalStorageDirectory() + "/" + foldername);
            if (thumbnail != null) {

                boolean success = true;
                if (!extStorageDirectory.exists()) {
                    success = extStorageDirectory.mkdir();
                }
                if (success) {


                    File[] files = extStorageDirectory.listFiles();
                    int numberOfImages = files.length;
                    int cnt = 0;
                    Log.v("foldername", "get a directory from btn  " + String.valueOf(extStorageDirectory));
                    Log.v("foldername", "get sum of count from directiory" + numberOfImages);


                    if (numberOfImages >= cnt) {
                        cnt = numberOfImages + 1;
                    }
                    try {
                        File file = new File(extStorageDirectory, "/" + foldername + "-" + cnt + ".jpeg");
                        Log.v("foldername", "to save   :   " + file);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbnailImage = baos.toByteArray();
                        baos.flush();
                        baos.close();
                        GLPhotoActivity.startActivityForResult(Camera360MainActivity.this, cameraIpAddress, fileId, thumbnailImage, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.v("error1", String.valueOf(thumbnail));
                }

            } else {
                publishProgress("failed to get file data.");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (String log : values) {
                Log.v("logviewer", "GetThumbnailTask	" + log);
                //logViewer.append(log);
            }
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
