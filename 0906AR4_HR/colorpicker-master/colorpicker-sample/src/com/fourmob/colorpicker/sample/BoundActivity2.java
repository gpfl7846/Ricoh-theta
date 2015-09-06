package com.fourmob.colorpicker.sample;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.colorpicker.sample.BoundService.AudioBinder;

import java.io.File;
import java.util.Calendar;


public class BoundActivity2 extends Activity {

    NotificationManager mNM;
    private int mId = 1;

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
    Button btnRecord, btnStop ;
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


        mNM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


    }

    private void sendNotification(NotificationCompat.Style style) {
        Notification noti = new Notification(R.drawable.ic_launcher,
                "새글이 있습니다.", System.currentTimeMillis());
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(this, BoundActivity2.class);
        intent.putExtra("UserName", "1234");
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent content =
                PendingIntent.getActivity(this, 0, intent, 0);
        noti.setLatestEventInfo(this,
                "새글알림", "새로운 글이 있습니다.", content);
        mNM.notify(mId, noti);
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
        (btnRecord = (Button) findViewById(R.id.btnRecord)).setOnClickListener(btnClick);
        (btnStop = (Button) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
        (btnMemo= (Button) findViewById(R.id.btnMemo)).setOnClickListener(btnClick);
        (btnDelete= (Button) findViewById(R.id.btnDelete)).setOnClickListener(btnClick);
        btnMemo.setEnabled(false);
        //btnStop.setVisibility(View.INVISIBLE);
        btnStop.setEnabled(false);
        btnRecord.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.INVISIBLE);
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

                tv_recordTime.setText(String.format("%02d:%02d:%02d"+" (%02d초)", minutes, seconds, millis-(seconds*60),millis));
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
                    sendNotification(null);
                    btnRecord.setVisibility(View.INVISIBLE);
                    btnStop.setVisibility(View.VISIBLE);
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
                    Toast.makeText(BoundActivity2.this, "북마크를 추가하였습니다",Toast.LENGTH_SHORT).show();
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


        //등록된 시간표가 아닐때
        if(FILE_SUBJECT==""){
            file2 = new File(filepath2, "기타");
            if (!file2.exists()) {
                file2.mkdirs();
            }

            FILE_SUBJECT="기타";
            fileName =FILE_SUBJECT+ FILE_COUNT + AUDIO_RECORDER_FILE_EXT_MP4;

        }
        else{
            fileName =FILE_WEEK+"주차"+FILE_SUBJECT+ FILE_COUNT + AUDIO_RECORDER_FILE_EXT_MP4;
        }
        FILE_COUNT = helper.getFileId()+1;
        path=file2.getAbsolutePath()+"/"+fileName;
        //Log.i("AhReum",path.toString());

        //Toast.makeText(getParent(),"파일명 : "+fileName,Toast.LENGTH_SHORT).show();

        return path;
    }

    private void startRecording2() {

        BookmarkCount =1; // setting for each file bookmark

        tv_fileName.setText(fileName);
        btnMemo.setEnabled(true);
        //btnRecord.setBackgroundResource(R.drawable.record_pause);
        btnRecord.setVisibility(View.INVISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        btnRecord.setEnabled(false );
        btnStop.setEnabled(true);
        isRecorder=true;
        isPausing=false;

        helper.insertFile(fileName, fileDirectory, FILE_WEEK, FILE_WEEK, FILE_COUNT);
        query0();

    }

    private void stopRecording2() {
        BookmarkCount =1;
        editText.setText("중요" + BookmarkCount);
        cnt = 0;
        t.cancel();
        tv_recordTime.setText("00:00:00 (00초)");
        tv_fileName.setText("녹음파일명");

        //btnRecord.setBackgroundResource(R.drawable.record_start);
        btnMemo.setEnabled(false);
        btnStop.setEnabled(false);
        btnRecord.setEnabled(true);
        btnRecord.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.INVISIBLE);

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
            Toast.makeText(BoundActivity2.this,"BoundActivity2 : onStop()",Toast.LENGTH_SHORT).show();
//            getApplicationContext().unbindService(mConnection);
//            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBound && isRecorder){
            Toast.makeText(BoundActivity2.this,"BoundActivity2 : onDestroy()",Toast.LENGTH_SHORT).show();
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
    }

    // ServiceConnection 인터페이스를 구현하는 객체를 생성한다.
    private ServiceConnection mConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioBinder binder = (AudioBinder) service;
            BoundActivity2.this.mService = binder.getService();
            mBound = true;

            //Toast.makeText(BoundActivityNo.this, mBound,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
            mBound = false;
        }
    };




}
