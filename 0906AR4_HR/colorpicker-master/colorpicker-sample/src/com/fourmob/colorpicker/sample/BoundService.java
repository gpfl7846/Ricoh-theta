package com.fourmob.colorpicker.sample;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import java.io.IOException;
import java.util.Random;

/**
 * Created by hanor_000 on 2015-08-15.
 */
public class BoundService extends Service{


//    private MediaRecorder recorder = null;
    private MediaRecorder recorder;
    private Boolean isRecorder = false;

    private final IBinder mBinder = new AudioBinder();    // 컴포넌트에 반환되는 IBinder

    // 컴포넌트에 반환해줄 IBinder를 위한 클래스
    public class AudioBinder extends Binder{
        BoundService getService(){
            return BoundService.this;
        }
    }

    private final Random rand = new Random();

    public void onCreate() {
        //여기에 미디어 생성 //서비스생성시 1번만 실행
        super.onCreate();
        recorder = new MediaRecorder();
        Toast.makeText(getApplicationContext(), "BoundService : onCreate()",Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent){
        Toast.makeText(getApplicationContext(),"BoundService : onBind()",Toast.LENGTH_SHORT).show();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(getApplicationContext(),"BoundService : onUnbind()",Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(),"BoundService : onDestroy()",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    // 컴포넌트에 난수를 반환해 줄 메소드
    public int getRandomNumber(){
        return rand.nextInt(1000);
    }



    public void startRecording(String path) {

        Toast.makeText(getApplicationContext(),"Start Recording",Toast.LENGTH_SHORT).show();

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
    }

    public void stopRecording() {

        Toast.makeText(getApplicationContext(),"Stop Recording",Toast.LENGTH_SHORT).show();
//        if (null != recorder) {
//            recorder.stop();
//            recorder.reset();
//            recorder.release();
//            recorder = null;
//        }
        try {
            recorder.stop();
            recorder.reset();
            //recorder.release();
            //
            // recorder = null;
        }catch (RuntimeException stopException){
            //recorder.stop();
            //do it
        }
    }


    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(getApplicationContext(),"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(getApplicationContext(),"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };
}