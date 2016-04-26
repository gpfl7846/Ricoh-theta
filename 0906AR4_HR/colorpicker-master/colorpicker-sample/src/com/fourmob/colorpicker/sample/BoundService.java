package com.fourmob.colorpicker.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.util.Random;

/**
 * Created by hanor_000 on 2015-08-15.
 */
public class BoundService extends Service {
    NotificationManager mNM;
    private int mI1 = 2;

    //    private MediaRecorder recorder = null;
    private MediaRecorder recorder;
    private Boolean isRecorder = false;

    private final IBinder mBinder = new AudioBinder();    // 컴포넌트에 반환되는 IBinder

    // 컴포넌트에 반환해줄 IBinder를 위한 클래스
    public class AudioBinder extends Binder {
        BoundService getService() {
            return BoundService.this;
        }
    }

    private final Random rand = new Random();

    public void onCreate() {
        //여기에 미디어 생성 //서비스생성시 1번만 실행
        super.onCreate();
        recorder = new MediaRecorder();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // 컴포넌트에 난수를 반환해 줄 메소드
    public int getRandomNumber() {
        return rand.nextInt(1000);
    }


    public void startRecording(String path) {

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
    }

    public void stopRecording() {
        try {
            recorder.stop();
            recorder.reset();

        } catch (RuntimeException stopException) {
             recorder.stop();
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
        Notification noti = new Notification(R.drawable.ic_launcher,
                "새글이 있습니다.", System.currentTimeMillis());
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("UserName", "1234");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent content =
                PendingIntent.getActivity(this, 0, intent, 0);
        noti.setLatestEventInfo(this,
                "새글알림", "새로운 글이 있습니다.", content);
        //mNM.notify(mI1, noti);
    }

}