package com.fourmob.colorpicker.sample;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.media.RemoteControlClient;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
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

    int NOTIFICATION_ID = 1111;
    private ComponentName remoteComponentName;
    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;
    private RemoteControlClient remoteControlClient;
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
            newNotification(true);
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

            newNotification(false);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void newNotification(boolean state){
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        if(state){
		//String songName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTitle();
//		String albumName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbum();
            RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification);
            RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);
             notification = new NotificationCompat.Builder(getApplicationContext())
                     .setContentTitle("___과목 녹음중입니다.")
                     .setSmallIcon(R.drawable.ic_launcher)
                     .build();

            setListeners(simpleContentView);
            setListeners(expandedView);

            notification.contentView = simpleContentView;
            if(currentVersionSupportBigNotification){
                notification.bigContentView = expandedView;
            }

            try{
                long albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumId();
                //Bitmap albumArt = UtilFunctions.getAlbumart(getApplicationContext(), albumId);
                Bitmap albumArt = UtilFunctions.getAlbumart(getApplicationContext(), albumId);
                if(albumArt != null){
                    notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                    if(currentVersionSupportBigNotification){
                        notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                    }
                }else{
                    notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                    if(currentVersionSupportBigNotification){
                        notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            if(PlayerConstants.SONG_PAUSED){
                notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

                if(currentVersionSupportBigNotification){
                    notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                    notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
                }
            }else{
                notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

                if(currentVersionSupportBigNotification){
                    notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                    notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
                }
            }
//
	//	notification.contentView.setTextViewText(R.id.textSongName, songName);
//		notification.contentView.setTextViewText(R.id.textAlbumName, albumName);
//		if(currentVersionSupportBigNotification){
//			notification.bigContentView.setTextViewText(R.id.textSongName, songName);
//			notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);
//		}
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            manager.notify(NOTIFICATION_ID, notification);
        }else{
            manager.cancel(NOTIFICATION_ID);
        }
    }

    public void setListeners(RemoteViews view) {
//        Intent previous = new Intent(NOTIFY_PREVIOUS);
//        Intent delete = new Intent(NOTIFY_DELETE);
//        Intent pause = new Intent(NOTIFY_PAUSE);
//        Intent next = new Intent(NOTIFY_NEXT);
//        Intent play = new Intent(NOTIFY_PLAY);
//
//        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
//        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);
//
//        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
//        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);
//
//        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
//        view.setOnClickPendingIntent(R.id.btnPause, pPause);
//
//        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
//        view.setOnClickPendingIntent(R.id.btnNext, pNext);
//
//        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
//        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

    }


}