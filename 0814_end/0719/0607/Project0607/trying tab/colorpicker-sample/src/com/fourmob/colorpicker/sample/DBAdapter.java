package com.fourmob.colorpicker.sample;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RemoteControlClient;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;


public class DBAdapter extends CursorAdapter {


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DBAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final ImageView image = (ImageView)view.findViewById(R.id.image);
        final TextView memo = (TextView)view.findViewById(R.id.memo);
        final TextView time = (TextView)view.findViewById(R.id.time);

        image.setImageResource(R.drawable.ic_launcher);
        memo.setText("메모 : "+cursor.getString(cursor.getColumnIndex("memo")));
        time.setText("시간 : "+cursor.getString(cursor.getColumnIndex("memoTime")));

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.listlayout, parent, false);
        return v;
    }

    public static class BoundActivity2 extends Activity {

        private BoundService mService;    // 연결 타입 서비스
        private boolean mBound = false;    // 서비스 연결 여부

        //Database를 생성 관리하는 클래스
        private RecordTimeTable_Helper helper;

        // for Filename Setting
        private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
        private static final String AUDIO_RECORDER_FOLDER = "MultiPlayer";
        private String FILE_SUBJECT = "fileName";
        private int  FILE_WEEK=1, FILE_COUNT=1;
        private String fileName, fileDirectory;
        public Calendar CLASS_TIME,OPEN_TIME;

        //Show Recording Time
        public int cnt=0;
        public CountDownTimer t;
        private long millis;

        // Insert BookMark(Memo) Count
        public static int BookmarkCount =1;


        boolean isRecorder= false, isPausing=false;
        EditText editText;
        TextView textView, textView2,tv_fileName, tv_recordTime,tv_memoTable, tv_fileTable ;
        ImageButton btnRecord, btnPause, btnStop ;
        Button btnMemo, btnDelete;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.audio_recording2);
            setRecordTime();
            setButtonHandlers();

            CLASS_TIME = Calendar.getInstance(); //date of today
            CLASS_TIME.add(Calendar.MONTH, 1);

            OPEN_TIME = Calendar.getInstance();
            OPEN_TIME.set(Calendar.YEAR, 2015);
            OPEN_TIME.set(Calendar.MONTH, 3);
            OPEN_TIME.set(Calendar.DAY_OF_MONTH, 2);

        }

        //resource 에서 가져와서 application 내의 View 변수에 설정
        private void setButtonHandlers() {
            editText=(EditText) findViewById(R.id.etMemo);
            textView=(TextView) findViewById(R.id.textView);
            textView2=(TextView) findViewById(R.id.textView2);
            tv_fileName=(TextView) findViewById(R.id.tv_fileName);
            tv_memoTable = (TextView) findViewById(R.id.tv_memoTable);
            tv_fileTable = (TextView) findViewById(R.id.tv_fileTable);
            tv_recordTime = (TextView) findViewById(R.id.tv_recordTime);
            (btnRecord = (ImageButton) findViewById(R.id.btnRecord)).setOnClickListener(btnClick);
            (btnPause = (ImageButton) findViewById(R.id.btnPause)).setOnClickListener(btnClick);
            (btnStop = (ImageButton) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
            (btnMemo= (Button) findViewById(R.id.btnMemo)).setOnClickListener(btnClick);
            (btnDelete= (Button) findViewById(R.id.btnDelete)).setOnClickListener(btnClick);
            btnMemo.setEnabled(false);
            btnPause.setVisibility(View.INVISIBLE);
            btnPause.setEnabled(false);
            btnStop.setVisibility(View.INVISIBLE);
            btnStop.setEnabled(false);
            editText.setText("중요" + BookmarkCount);
        }

        private void setRecordTime() {
            t = new CountDownTimer( Long.MAX_VALUE , 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    cnt++;
                    millis = cnt;
                    int seconds = (int) (millis / 60);
                    int minutes = seconds / 60;
                    seconds     = seconds % 60;

                    tv_recordTime.setText(String.format("%02d:%02d:%02d" + " (%02d초)", minutes, seconds, millis - (seconds * 60), millis));
                }

                @Override
                public void onFinish() {            }
            };
        }

        public View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                helper = new RecordTimeTable_Helper(BoundActivity2.this);

                switch (v.getId()) {
                    case R.id.btnRecord: {
    //                    if(isRecorder && !isPausing) {
    //                        t.cancel();
    //                        pauseRecording();
    //
    //                    }
    //                    else if(isRecorder && isPausing) {
    //                        t.start();
    //                        restartRecording();
    //                        break;
    //                    }
    //                    else if(!isRecorder && !isPausing){
    //                        textView.setText("오늘 날짜 : " + String.format("%d / %d / %d", CLASS_TIME.get(Calendar.YEAR), CLASS_TIME.get(Calendar.MONTH), CLASS_TIME.get(Calendar.DAY_OF_MONTH)));
    //                        textView2.setText("개강 날짜 : " + String.format("%d / %d / %d", OPEN_TIME.get(Calendar.YEAR), OPEN_TIME.get(Calendar.MONTH), OPEN_TIME.get(Calendar.DAY_OF_MONTH)));
    //                        FILE_WEEK = CLASS_TIME.get(Calendar.WEEK_OF_YEAR) - OPEN_TIME.get(Calendar.WEEK_OF_YEAR)+1;
    //                        t.start();
    //
    //                        if (mBound) {
    //                            mService.startRecording(getFilename());
    //                        }
    //                        startRecording2();
    //                        break;
    //                    }

                        textView.setText("오늘 날짜 : " + String.format("%d / %d / %d", CLASS_TIME.get(Calendar.YEAR), CLASS_TIME.get(Calendar.MONTH), CLASS_TIME.get(Calendar.DAY_OF_MONTH)));
                        textView2.setText("개강 날짜 : " + String.format("%d / %d / %d", OPEN_TIME.get(Calendar.YEAR), OPEN_TIME.get(Calendar.MONTH), OPEN_TIME.get(Calendar.DAY_OF_MONTH)));
                        FILE_WEEK = CLASS_TIME.get(Calendar.WEEK_OF_YEAR) - OPEN_TIME.get(Calendar.WEEK_OF_YEAR)+1;
                        t.start();

                        if (mBound) {
                            mService.startRecording(getFilename());
                        }
                        startRecording2();
                        break;

                    }

                    case R.id.btnPause: {
                        pauseRecording();
                        break;
                    }

                    case R.id.btnStop: {
                        stopRecording2();
                        if(mBound && isRecorder) {
                            mService.stopRecording();
                            isPausing=false;
                            isRecorder=false;
                        }
                        break;
                    }

                    case R.id.btnMemo: {
                        //after getting memo, Initialize memo value
                        String memo = editText.getText().toString();

                        helper.insertMemo(cnt, memo, fileName);
                        query();
                        textView.setText(R.string.app_info);
                        Toast.makeText(BoundActivity2.this, "북마크를 추가하였습니다", Toast.LENGTH_SHORT).show();
                        BookmarkCount++;
                        editText.setText("중요"+BookmarkCount);

                        break;
                    }

                    case R.id.btnDelete: {
                        helper.deleteTable();
                        textView.setText("recordTimeTable.db - memoTABLE Delete");
                        textView2.setText("recordTimeTable.db - fileTABLE Delete");
                        tv_fileTable.setText("");
                        tv_memoTable.setText("");
                    }
                }
            }
        };

        private String getFilename() {

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

            String path;
            fileDirectory = file.getAbsolutePath()+"/";
            FILE_SUBJECT=helper.whatSubject();
            FILE_COUNT = helper.getFileId()+1;
            fileName =FILE_WEEK+"주차"+FILE_SUBJECT+ FILE_COUNT + AUDIO_RECORDER_FILE_EXT_MP4;
            path=file2.getAbsolutePath()+"/"+fileName;

            //Toast.makeText(getParent(),"파일명 : "+fileName,Toast.LENGTH_SHORT).show();

            return path;
        }

        private void startRecording2() {

            BookmarkCount =1; // setting for each file bookmark

            tv_fileName.setText(fileName);
            btnMemo.setEnabled(true);
            //btnRecord.setBackgroundResource(R.drawable.record_pause);
            btnRecord.setVisibility(View.INVISIBLE);
            btnRecord.setEnabled(false );
            btnPause.setVisibility(View.VISIBLE);
            btnPause.setEnabled(true);
            btnStop.setVisibility(View.VISIBLE);
            btnStop.setEnabled(true);
            isRecorder=true;
            isPausing=false;

            helper.insertFile(fileName, fileDirectory, FILE_WEEK, FILE_WEEK, FILE_COUNT);
            query0();

        }

        private void stopRecording2() {
            BookmarkCount =1;
            editText.setText("중요"+ BookmarkCount);
            cnt = 0;
            t.cancel();
            tv_recordTime.setText("00:00:00 (00초)");
            tv_fileName.setText("녹음파일명");

            //btnRecord.setBackgroundResource(R.drawable.record_start);
            btnMemo.setEnabled(false);
            btnPause.setVisibility(View.INVISIBLE);
            btnPause.setEnabled(false);
            btnStop.setVisibility(View.INVISIBLE);
            btnStop.setEnabled(false);
            btnRecord.setVisibility(View.VISIBLE);
            btnRecord.setEnabled(true);

        }

        public void pauseRecording() {
            Toast.makeText(BoundActivity2.this, "pauseRecording()", Toast.LENGTH_SHORT).show();
            //btnRecord.setBackgroundResource(R.drawable.record_start);
            btnRecord.setVisibility(View.INVISIBLE);
            btnRecord.setEnabled(false);
            btnPause.setVisibility(View.INVISIBLE);
            btnPause.setEnabled(false);
            btnStop.setVisibility(View.VISIBLE);
            btnStop.setEnabled(true);
            btnMemo.setEnabled(false);
            isPausing = true;
            isRecorder = true;
        }

        public void restartRecording() {
            Toast.makeText(BoundActivity2.this, "restartRecording()", Toast.LENGTH_SHORT).show();
            //btnRecord.setBackgroundResource(R.drawable.record_pause);
            btnStop.setVisibility(View.VISIBLE);
            btnStop.setEnabled(true);
            btnMemo.setEnabled(true);
            isRecorder=true;
            isPausing = false;
        }

        public void query0(){
            String sql="select file_id ,subject_fk, fileName from fileTABLE";
            Cursor cursor=helper.db.rawQuery(sql, null);
            tv_fileTable.setText("");

            int recordCount=cursor.getCount();
            for(int i=0; i<recordCount; i++){
                cursor.moveToNext();
                String str1=cursor.getString(0);
                String str2=cursor.getString(1);
                String str3=cursor.getString(2);
                tv_fileTable.append( "file_id : " + str1 + "   " + "subject_fk : " + str2 + "   fileName : " + str3+"\n");
            }
        }

        public void query(){
            String sql = "select memo_id, file_fk, memo from memoTABLE";
            Cursor cursor=helper.db.rawQuery(sql, null);
            tv_memoTable.setText("");
            int recordCount=cursor.getCount();

            Log.v("AhReum", "레코드카운트 : " + Integer.toString(recordCount));

            for(int i=0; i<recordCount; i++){
                cursor.moveToNext();
                String str1=cursor.getString(0);
                String str2=cursor.getString(1);
                String str3=cursor.getString(2);
                tv_memoTable.append( "memo_id : " + str1 + "   " + "file_fk : " + str2 + "   memo : " + str3+"\n");
            }
        }

        // 액티비티가 시작되면 서비스에 연결
        @Override
        protected void onStart(){
            super.onStart();
            Intent intent = new Intent(BoundActivity2.this, BoundService.class);
            getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Toast.makeText(BoundActivity2.this, "BoundActivity2 : onStart()", Toast.LENGTH_SHORT).show();
        }

        // 액티비티가 종료되면 서비스 연결을 해제
        @Override
        protected void onStop(){
            super.onStop();
            if(mBound && isRecorder){
                Toast.makeText(BoundActivity2.this, "BoundActivity2 : onStop()", Toast.LENGTH_SHORT).show();
    //            getApplicationContext().unbindService(mConnection);
    //            mBound = false;
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if(mBound && isRecorder){
                Toast.makeText(BoundActivity2.this, "BoundActivity2 : onDestroy()", Toast.LENGTH_SHORT).show();
                getApplicationContext().unbindService(mConnection);
                mBound = false;
            }
        }

        // ServiceConnection 인터페이스를 구현하는 객체를 생성한다.
        private ServiceConnection mConnection = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BoundService.AudioBinder binder = (BoundService.AudioBinder) service;
                BoundActivity2.this.mService = binder.getService();
                mBound = true;

                //Toast.makeText(BoundActivity.this, mBound,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0){
                mBound = false;
            }
        };




    }

    public static class MainActivity extends TabActivity {

        private Runnable r;
        private Handler mHandler;
        public Calendar CLASS_TIME,OPEN_TIME, START_TIME, END_TIME, NOW_TIME,COM_TIME_START,COM_TIME_END;
        private TextView mDateDisplay;
        private TextView mTimeDisplay;
        private int mYear;
        private int mMonth;
        private int mDay;

        private int mHour;
        private int mMinute;
        private int mSecond;
        static final int DATE_DIALOG_ID = 0;
        static final int TIME_DIALOG_ID = 0;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            Resources res = getResources();
            TabHost tabHost = getTabHost();

            TabHost.TabSpec spec;
            Intent intent;


            CLASS_TIME = Calendar.getInstance(); //date of today
            CLASS_TIME.add(Calendar.MONTH, 1);
            START_TIME = Calendar.getInstance();
            END_TIME = Calendar.getInstance();
            NOW_TIME = Calendar.getInstance();
            OPEN_TIME = Calendar.getInstance();
            OPEN_TIME.set(Calendar.YEAR, 2015);
            OPEN_TIME.set(Calendar.MONTH, 3);
            OPEN_TIME.set(Calendar.DAY_OF_MONTH, 2);
            RecordTimeTable_Helper helper
                    = new RecordTimeTable_Helper(MainActivity.this);

            ArrayList<String> startTime = helper.whatStartTime();
            final ArrayList<String> endTime = helper.whatEndTime();
    //        for (String s : startTime)
    //            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(MainActivity.this, BoundService.class);

            final AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            //알람등록

            if (startTime.size() == 1) {
                PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent1, 0);
                alarm.cancel(pintent);
                Log.v("whattime", "오늘 수업이 없음");
            }else if (startTime.size() != 1) {
                for (int i = 1; i < startTime.size(); i++) {
                    String start_time = startTime.get(i);
                    String start_hour = start_time.substring(0, 2);
                    String start_minutes = start_time.substring(3, 5);
                    OPEN_TIME = Calendar.getInstance();
                    OPEN_TIME.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_hour));
                    OPEN_TIME.set(Calendar.MINUTE, Integer.parseInt(start_minutes));
                    PendingIntent pintent = PendingIntent.getService(MainActivity.this, i, intent1, 0);
                    // alarm.setRepeating(AlarmManager.RTC_WAKEUP,OPEN_TIME.getTimeInMillis(),7 * 24 * 60 * 60 * 1000, pintent);
                    alarm.set(AlarmManager.RTC, OPEN_TIME.getTimeInMillis(), pintent);
                    Log.v("whattime", i + " : " + OPEN_TIME.getTime());

                }

            }//end of NONE 1

            intent = new Intent().setClass(this, BoundActivity2.class);
            spec = tabHost.newTabSpec("녹음기").setIndicator("", res.getDrawable(R.drawable.ic_tab_one)).setContent(intent);
            tabHost.addTab(spec);

            intent = new Intent().setClass(this, Timetable.class);
            spec = tabHost.newTabSpec("시간표").setIndicator(res.getString(R.string.timetable), res.getDrawable(R.drawable.ic_tab_two)).setContent(intent);
            //spec = tabHost.newTabSpec("library_browser");
            tabHost.addTab(spec);

            intent = new Intent().setClass(this, Dropboxmain.class);
            spec = tabHost.newTabSpec("설정").setIndicator(res.getString(R.string.timetable), res.getDrawable(R.drawable.ic_tab_three)).setContent(intent);
            // spec = tabHost.newTabSpec("file_browser");
            tabHost.addTab(spec);

            tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#de4e43"));
            tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#de4e43"));
            tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#de4e43"));


            tabHost.setCurrentTab(0);
        }


        private Calendar updateDisplay() {

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mSecond = c.get(Calendar.SECOND);
            c.set(Calendar.HOUR_OF_DAY, mHour);
            c.set(Calendar.MINUTE, mMinute);
            c.set(Calendar.SECOND, mSecond);
            mHandler.postDelayed(r, 1000);
            return c;
        }
    }

    /**
     * Created by hanor_000 on 2015-08-15.
     */
    public static class BoundService extends Service {


        //    private MediaRecorder recorder = null;
        private MediaRecorder recorder;
        private Boolean isRecorder = false;
        private static final int RECORDING_NOTIFICATION_ID = 1;
        private final IBinder mBinder = new AudioBinder();    // 컴포넌트에 반환되는 IBinder
        private final static int ID_REMOTSERVICE = 1;
        private RemoteViews _smallView, _bigView;
        private Notification _notification;
        private Intent _playIntent;
        private PendingIntent _playPendingIntent;
        private ComponentName remoteComponentName;
        private RemoteControlClient remoteControlClient;
        private static boolean currentVersionSupportBigNotification = false;
        int NOTIFICATION_ID = 1111;

        // 컴포넌트에 반환해줄 IBinder를 위한 클래스
        public class AudioBinder extends Binder {
            BoundService getService(){
                return BoundService.this;
            }
        }

        private final Random rand = new Random();

        public void onCreate() {
            //여기에 미디어 생성 //서비스생성시 1번만 실행
            super.onCreate();
            recorder = new MediaRecorder();
            Toast.makeText(getApplicationContext(), "BoundService : onCreate()", Toast.LENGTH_SHORT).show();
        }

        @Override
        public IBinder onBind(Intent intent){
            Toast.makeText(getApplicationContext(), "BoundService : onBind()", Toast.LENGTH_SHORT).show();
            return mBinder;
        }

        @Override
        public boolean onUnbind(Intent intent) {
            Toast.makeText(getApplicationContext(), "BoundService : onUnbind()", Toast.LENGTH_SHORT).show();
            return super.onUnbind(intent);
        }

        @Override
        public void onDestroy() {
            Toast.makeText(getApplicationContext(), "BoundService : onDestroy()", Toast.LENGTH_SHORT).show();
            super.onDestroy();
        }

        // 컴포넌트에 난수를 반환해 줄 메소드
        public int getRandomNumber(){
            return rand.nextInt(1000);
        }



        public void startRecording(String path) {

            Toast.makeText(getApplicationContext(), "Start Recording", Toast.LENGTH_SHORT).show();

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
                //newNotification();
                updateNotification(true);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stopRecording() {

            Toast.makeText(getApplicationContext(), "Stop Recording", Toast.LENGTH_SHORT).show();

    //        if (null != recorder) {
    //            recorder.stop();
    //            recorder.reset();
    //            recorder.release();
    //            recorder = null;
    //        }
            try {
                recorder.stop();
                recorder.reset();
                updateNotification(false);
                //recorder.release();
                //
                // recorder = null;
            }catch (RuntimeException stopException){
                //recorder.stop();
                //do it
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void updateNotification(Boolean status)
        {
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
            RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);
            Notification notification = null;


            if (status) {
                notification = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_music)
                        .setContentTitle("안녕").build();
                setListeners(simpleContentView);
                setListeners(expandedView);

                notification.contentView = simpleContentView;


                if (currentVersionSupportBigNotification) {
                    notification.bigContentView = expandedView;
                }

                try {
                    long albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumId();
    //            Bitmap albumArt = UtilFunctions.getAlbumart(getApplicationContext(), albumId);
    //            if(albumArt != null){
    //                notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
    //                if(currentVersionSupportBigNotification){
    //                    notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
    //                }
    //            }else{
    //                notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
    //                if(currentVersionSupportBigNotification){
    //                    notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
    //                }
    //            }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (PlayerConstants.SONG_PAUSED) {
                    notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
                    notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

                    if (currentVersionSupportBigNotification) {
                        notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                        notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
                    }
                } else {
                    notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                    notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

                    if (currentVersionSupportBigNotification) {
                        notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                        notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
                    }
                }

    //        notification.contentView.setTextViewText(R.id.textSongName, songName);
    //        notification.contentView.setTextViewText(R.id.textAlbumName, albumName);
                if (currentVersionSupportBigNotification) {
    //            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
    //            notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);
                }
                notification.flags |= Notification.FLAG_ONGOING_EVENT;
                manager.notify(NOTIFICATION_ID, notification);

            }
            else{
                manager.cancel(NOTIFICATION_ID);

            }

        }
        private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
            }
        };

        private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
            }
        };
        @SuppressLint("NewApi")
        private void RegisterRemoteClient(){
            remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
            try {
                if(remoteControlClient == null) {
                  //  audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                    Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    mediaButtonIntent.setComponent(remoteComponentName);
                    PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                    remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                    //audioManager.registerRemoteControlClient(remoteControlClient);
                }
                remoteControlClient.setTransportControlFlags(
                        RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                                RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                                RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                                RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                                RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                                RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
            }catch(Exception ex) {
            }
        }
        @SuppressLint("NewApi")
    //    private void newNotification() {
    //        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification);
    //    RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);
    //
    //    setListeners(simpleContentView);
    //    setListeners(expandedView);
    //
    //    notification.contentView = simpleContentView;
    //    if(currentVersionSupportBigNotification){
    //        notification.bigContentView = expandedView;
    //    }
    //
    //    try{
    //        long albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumId();
    ////            Bitmap albumArt = UtilFunctions.getAlbumart(getApplicationContext(), albumId);
    ////            if(albumArt != null){
    ////                notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
    ////                if(currentVersionSupportBigNotification){
    ////                    notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
    ////                }
    ////            }else{
    ////                notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
    ////                if(currentVersionSupportBigNotification){
    ////                    notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
    ////                }
    ////            }
    //    }catch(Exception e){
    //        e.printStackTrace();
    //    }
    //    if(PlayerConstants.SONG_PAUSED){
    //        notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
    //        notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
    //
    //        if(currentVersionSupportBigNotification){
    //            notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
    //            notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
    //        }
    //    }else{
    //        notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
    //        notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);
    //
    //        if(currentVersionSupportBigNotification){
    //            notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
    //            notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
    //        }
    //    }
    //
    ////        notification.contentView.setTextViewText(R.id.textSongName, songName);
    ////        notification.contentView.setTextViewText(R.id.textAlbumName, albumName);
    //    if(currentVersionSupportBigNotification){
    ////            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
    ////            notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);
    //    }
    //    notification.flags |= Notification.FLAG_ONGOING_EVENT;
    //    startForeground(NOTIFICATION_ID, notification);
    //}


        public void setListeners(RemoteViews view) {
            //Intent previous = new Intent(NOTIFY_PREVIOUS);
    //        Intent delete = new Intent(NOTIFY_DELETE);
    //        Intent pause = new Intent(NOTIFY_PAUSE);
    //        Intent next = new Intent(NOTIFY_NEXT);
    //        Intent play = new Intent(NOTIFY_PLAY);

            //  PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
            // view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);
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

    public static class PlayerService extends Service {

        static public final int STOPED = -1, PAUSED = 0, PLAYING = 1;
        private MediaPlayer mediaPlayer;
        private ArrayList<HashMap<String, String>> tracklist;
        private int status, currentTrackPosition;
        private boolean taken;
        private IBinder playerBinder;
        private SongsManager songsManager;
        private int seekForwardTime = 5000; // 5000 milliseconds
        private int seekBackwardTime = 5000; // 5000 milliseconds
        private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
        static public String path="init";
        public String subject ="";
    //    final String MEDIA_PATH =  Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator;
        private String mp3Pattern = ".mp4";

       // private Utilities utils;
        @Override
        public void onCreate() {
            super.onCreate();
            mediaPlayer = new MediaPlayer();
            tracklist = new ArrayList<HashMap<String, String>>();
            currentTrackPosition = -1;
            setStatus(STOPED);

            playerBinder = new PlayerBinder();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer arg0) {
                    if (currentTrackPosition == tracklist.size()-1) {
                        stop();
                    } else {
                        nextTrack();
                    }
                }
            });
            addTrack();
        }

        @Override
        public IBinder onBind(Intent intent) {
            return playerBinder;
        }

        @Override
        public boolean onUnbind(Intent intent) {
            return super.onUnbind(intent);
        }

        public void take() {
            taken = true;
        }

        private void untake() {
            synchronized (this) {
                taken = false;
                notifyAll();
            }
        }

        public boolean isTaken() {
            return taken;
        }

        private void setStatus(int s) {
            status = s;
        }

        public int getStatus() {
            return status;
        }

        public ArrayList<HashMap<String, String>> getTracklist() {
            getPath();
            return tracklist;
        }

        public void addTrack(){
           // songsManager = new SongsManager();
            tracklist = getPlayList();
        }


        public HashMap<String, String> getTrack(int pos) {
            return tracklist.get(pos);
        }

        public HashMap<String, String> getCurrentTrack() {
            if (currentTrackPosition < 0) {
                return null;
            } else {
                return tracklist.get(currentTrackPosition);
            }
        }


        public int getCurrentTrackPosition() {
            return currentTrackPosition;
        }


        public void removeTrack(int pos) {
            if (pos == currentTrackPosition) {
                stop();
            }
            if (pos < currentTrackPosition) {
                currentTrackPosition--;
            }
            tracklist.remove(pos);
            untake();
        }

        public void clearTracklist() {
            if (status > STOPED) {
                stop();
            }
            tracklist.clear();
            untake();
        }



        public String gettlqkf(){
            String tlqkf;
            tlqkf = "변수가 잘 전달되고 있나요? -서비스- ";
            return tlqkf;
        }


        public void playTrack(int pos) {
            if (status > STOPED) {
                stop();
            }
            FileInputStream file = null;
            try {
                file = new FileInputStream(new File(tracklist.get(pos).get("songPath")));
                mediaPlayer.setDataSource(file.getFD());
                mediaPlayer.prepare();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            currentTrackPosition = pos;
            setStatus(PLAYING);
            untake();
        }

        public void play(int pos) {
            playTrack(pos);
        }

        public void play() {
            switch (status) {
                case STOPED:
                    if (!tracklist.isEmpty()) {
                        playTrack(0);
                        setStatus(PLAYING);
                    }
                    else{
                        Log.v("JinHee", "플레이할 파일이 없네유!!!!");
                    }
                    break;
                case PLAYING:
                    mediaPlayer.pause();
                    setStatus(PAUSED);
                    break;
                case PAUSED:
                    mediaPlayer.start();
                    setStatus(PLAYING);
                    break;
            }
            untake();
        }

        public void pause() {
            mediaPlayer.pause();
            setStatus(PAUSED);
            untake();
        }

        public void stop() {
            mediaPlayer.stop();
            mediaPlayer.reset();
            currentTrackPosition = -1;
            setStatus(STOPED);
            untake();
        }

        public void nextTrack() {
            // check if next song is there or not
            if(currentTrackPosition < (tracklist.size() - 1)){
                playTrack(currentTrackPosition + 1);
                currentTrackPosition = currentTrackPosition + 1;
            }else{
                // play first song
                playTrack(0);
                currentTrackPosition = 0;
            }
        }

        public void prevTrack() {
            // check if next song is there or not
            if(currentTrackPosition > 0){
                playTrack(currentTrackPosition - 1);
                currentTrackPosition = currentTrackPosition - 1;
            }else{
                // play last song
                playTrack(songsList.size() - 1);
                currentTrackPosition = songsList.size() - 1;
            }
        }

        public void forward(){
            // get current song position
            int currentPosition = mediaPlayer.getCurrentPosition();
            // check if seekForward time is lesser than song duration
            if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
                // forward song
                mediaPlayer.seekTo(currentPosition + seekForwardTime);
            } else {
                // forward to end position
                mediaPlayer.seekTo(mediaPlayer.getDuration());
            }
        }

        public void backward() {
            // get current song position
            int currentPosition = mediaPlayer.getCurrentPosition();
            // check if seekBackward time is greater than 0 sec
            if (currentPosition - seekBackwardTime >= 0) {
                // forward song
                mediaPlayer.seekTo(currentPosition - seekBackwardTime);
            } else {
                // backward to starting position
                mediaPlayer.seekTo(0);
            }
        }
        public int getCurrentTrackProgress() {
            if (status > STOPED) {
                return mediaPlayer.getCurrentPosition();
            } else {
                return 0;
            }
        }

        public int getCurrentTrackDuration() {
            if (status > STOPED) {
                return mediaPlayer.getDuration();
            } else {
                return 0;
            }
        }

        public void seekTrack(int p) {
            if (status > STOPED) {
                mediaPlayer.seekTo(p);
                untake();
            }
        }
    /*
        public void storeTracklist() {
            DbOpenHelper dbOpenHelper = new DbOpenHelper(getApplicationContext());
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            dbOpenHelper.onUpgrade(db, 1, 1);
            for (int i = 0; i < tracklist.size(); i++) {
                ContentValues c = new ContentValues();
                c.put(DbOpenHelper.KEY_POSITION, i);
                c.put(DbOpenHelper.KEY_FILE, tracklist.get(i).getPath());
                db.insert(DbOpenHelper.TABLE_NAME, null, c);
            }
            dbOpenHelper.close();
        }

        private void restoreTracklist() {
            DbOpenHelper dbOpenHelper = new DbOpenHelper(getApplicationContext());
            SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
            Cursor c = db.query(DbOpenHelper.TABLE_NAME, null, null, null, null, null, null);
            tracklist.clear();
            while (c.moveToNext()) {
                tracklist.add(new Track(c.getString(1)));
            }
            dbOpenHelper.close();
        }*/



        public class PlayerBinder extends Binder {

            public PlayerService getService() {
                return PlayerService.this;
            }

            public ArrayList<HashMap<String, String>> getTracklist2() {
                songsManager = new SongsManager();
                tracklist = getPlayList();

                return tracklist;
            }

        }

        public class Track {

            private String path, artist, album, year, title, genre;
            private int id, duration;

            public Track(String p) {



                path = p;


                String[] proj = {MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID};


                Cursor trackCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Audio.Media.DATA+" = '"+path+"' ", null, null);




                trackCursor.moveToNext();



                artist = trackCursor.getString(0);
                album = trackCursor.getString(1);
                year = trackCursor.getString(2);
                title = trackCursor.getString(3);
                duration = Integer.parseInt(trackCursor.getString(4));
                id = Integer.parseInt(trackCursor.getString(5));
            }


            private void scanDirectory2(File directory) {
                if (directory != null) {
                    File[] listFiles = directory.listFiles();
                    if (listFiles != null && listFiles.length > 0) {
                        for (File file : listFiles) {
                            if (file.isDirectory()) {
                                scanDirectory2(file);
                            } else {
                                addSongToList2(file);
                            }

                        }
                    }
                }
            }

            private void addSongToList2(File song) {
                if (song.getName().endsWith(mp3Pattern)) {


                    HashMap<String, String> songMap = new HashMap<String, String>();
                    songMap.put("songTitle",
                            song.getName().substring(0, (song.getName().length() - 4)));
                    songMap.put("songPath", song.getPath());

                    // Adding each song to SongList
                    // songsList.add(songMap);
                    tracklist.add(songMap);
                    path = song.getPath();
                    title= song.getName().substring(0, (song.getName().length() - 4));
                    // playerService.addTrack(playerService.new Track(song.getPath()));
                    //   addTrack(this.new Track(song.getPath()));
                    // addTrack(this.new Track(song.getAbsolutePath()));
                }
            }

            public String getPath() {
                return path;
            }


            public String getTitle() {
                return title;
            }

        }

        public static String formatTrackDuration(int d) {
            String min = Integer.toString((d / 1000) / 60);
            String sec = Integer.toString((d / 1000) % 60);
            if (sec.length() == 1) sec = "0"+sec;
            return min+":"+sec;
        }




        public void setPath(String s){
            subject = s;
            // String path;
            //path =  Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator+subject+ File.separator;
            path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator;
            //path = s;
            //Log.v("JinHee", "안녕하세요 하이를 불렀어요 ㅎㅎ 시발");

        }

        public String getPath(){
            return path;
        }


        public void setPlayList(String str) {
            subject = str;
            //String path2 = getPath();
             String path2 = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator+subject+ File.separator;
            // String path2 = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator;
            System.out.println(path2);
            if (path2 != null) {
                File home = new File(path2);
                File[] listFiles = home.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File file : listFiles) {
                        System.out.println(file.getAbsolutePath());
                        if (file.isDirectory()) {
                            scanDirectory(file);
                        } else {
                            addSongToList(file);
                        }
                    }
                }
            }
            // return songs list array
            //return songsList;
        }

        public ArrayList<HashMap<String, String>> getPlayList() {

















            // return songs list array
            return songsList;
        }

        private void scanDirectory(File directory) {
            if (directory != null) {
                File[] listFiles = directory.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File file : listFiles) {
                        if (file.isDirectory()) {
                            scanDirectory(file);
                        } else {
                            addSongToList(file);
                        }

                    }
                }
            }
        }

        private void addSongToList(File song) {
            if (song.getName().endsWith(mp3Pattern)) {


                HashMap<String, String> songMap = new HashMap<String, String>();
                songMap.put("songTitle",
                        song.getName().substring(0, (song.getName().length() - 4)));
                songMap.put("songPath", song.getPath());

                // Adding each song to SongList
                songsList.add(songMap);
    //            playerService.addTrack(playerService.new Track(song.getPath()));
            }
        }
    }
}
