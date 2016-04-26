package com.fourmob.colorpicker.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by hanor_000 on 2015-08-15.
 */
public class BoundService2 extends Service {
    NotificationManager mNM;
    private int mI1 = 2;
    static final String TAG = "Timeusing";

    private int mId = 1;

    //    private MediaRecorder recorder = null;
    private MediaRecorder recorder;
    private Boolean isRecorder = false;

    private final IBinder mBinder = new AudioBinder();    // 컴포넌트에 반환되는 IBinder

    //새로생성했다
    BoundActivity2 RecordActivity = (BoundActivity2) new BoundActivity2();

    //Database를 생성 관리하는 클래스
    private RecordTimeTable_Helper helper;

    // for Filename Setting
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "MultiPlayer";
    private String FILE_SUBJECT = "fileName";
    private int FILE_WEEK = 1, FILE_COUNT = 1;
    private String fileName, fileDirectory;
    public static Calendar CLASS_TIME, OPEN_TIME, END_TIME;
    public static String path;

    // 컴포넌트에 반환해줄 IBinder를 위한 클래스
    public class AudioBinder extends Binder {
        BoundService2 getService() {
            return BoundService2.this;
        }

    }


    public void onCreate() {
        //여기에 미디어 생성 //서비스생성시 1번만 실행
        super.onCreate();
        recorder = new MediaRecorder();
        helper = new RecordTimeTable_Helper(BoundService2.this);

        Thread thr = new Thread(null, mTask, "AlarmService_Service");
        thr.start();
        Toast.makeText(getApplicationContext(), "자동녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show();

    }


    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(getApplicationContext(), "onUnbind()", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "자동녹음이 종료되었습니다.", Toast.LENGTH_SHORT).show();
        //stopRecording();
        //RecordActivity.stopRecording2();
    }

    public void startRecording(String path) {

        if (null != recorder) {

            //sendNotification(null);
            //Toast.makeText(getApplicationContext(), "BoundService2 ->녹음이 시작",Toast.LENGTH_SHORT).show();
            Log.v(TAG, "BoundService2 녹음시작");

            //recorder = new MediaRecorder(); isRecorder = true;
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(path);
            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);

            try {
                recorder.prepare();
                recorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "녹음이 진행중입니다", Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(getApplicationContext(), "BoundService2 ->서비스를 죽임",Toast.LENGTH_SHORT).show();
//        stopSelf();
//        Log.v(TAG, "서비스를  죽임");

    }

    public void stopRecording() {

        Log.v("Timeusing", "녹음중지");
        try {
            recorder.stop();
            recorder.reset();

        } catch (RuntimeException stopException) {
            //recorder.stop();
            //do it
        }
    }


    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            //   Toast.makeText(getApplicationContext(),"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            //Toast.makeText(getApplicationContext(),"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private void sendNotification(NotificationCompat.Style style) {
        // send notification
        Notification noti = new Notification(R.drawable.ic_launcher,
                "MultiPlayer 녹음을 시작합니다.", System.currentTimeMillis());
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent content =
                PendingIntent.getActivity(this, 0, intent, 0);
        noti.setLatestEventInfo(this,
                "MultiPlayer", "녹음 중입니다.", content);
        //mNM.notify(mId, noti);
    }


    public String getFilename() {

        //Create Folder
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filepath2 = filepath + "/" + AUDIO_RECORDER_FOLDER + "/";
        File file2 = new File(filepath2, helper.whatSubject());
        if (!file2.exists()) {
            file2.mkdirs();
        }


        fileDirectory = file.getAbsolutePath() + "/";
        FILE_SUBJECT = helper.whatSubject();


        //등록된 시간표가 아닐때
        if (FILE_SUBJECT == "") {
            file2 = new File(filepath2, "기타");
            if (!file2.exists()) {
                file2.mkdirs();
            }

            FILE_SUBJECT = "기타";
            fileName = FILE_SUBJECT + FILE_COUNT + "[자동녹음]" + AUDIO_RECORDER_FILE_EXT_MP4;

        } else {
            fileName = FILE_WEEK + "주차" + FILE_SUBJECT + FILE_COUNT + "[자동녹음]" + AUDIO_RECORDER_FILE_EXT_MP4;
        }
        FILE_COUNT++;
        path = file2.getAbsolutePath() + "/" + fileName;
        return path;
    }

    Runnable mTask = new Runnable() {
        public void run() {


            startRecording(getFilename());
            long endTime = System.currentTimeMillis() + 5 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (mBinder) {
                    try {
                        mBinder.wait(endTime - System.currentTimeMillis());
                        stopRecording();

                    } catch (Exception e) {
                    }
                }
            }

            BoundService2.this.stopSelf();
            //Toast.makeText(getApplicationContext(), "BoundService2 ->서비스를 죽임",Toast.LENGTH_SHORT).show();
            Log.v(TAG, "서비스를  죽임");

        }
    };


}