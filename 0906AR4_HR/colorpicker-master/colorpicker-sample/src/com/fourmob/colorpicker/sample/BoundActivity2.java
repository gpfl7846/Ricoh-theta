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
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.colorpicker.sample.BoundService.AudioBinder;

import java.io.File;
import java.util.Calendar;


public class BoundActivity2 extends Activity {

    public static Context mContext;

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
    private int FILE_WEEK = 1, FILE_COUNT = 1;
    private String fileName, fileDirectory;
    public static Calendar CLASS_TIME, OPEN_TIME, END_TIME;
    public static String path;

    //Show Recording Time
    public int cnt = 0;
    public CountDownTimer t;
    private long millis;

    // Insert BookMark(Memo) Count
    public static int BookmarkCount = 1;
    public static String memo = "중요";
    public static String k1, k2, k3;


    boolean isRecorder = false, isPausing = false;
    EditText editText;
    TextView tv_recordTime, tv_fileName;
    Button btnRecord, btnStop;
    Button btnMemo;
    RadioGroup radiogroup;
    RadioButton radio1, radio2, radio3;
    ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recording2);
        mContext = this;
        setRecordTime();
        setButtonHandlers();

        CLASS_TIME = Calendar.getInstance(); //date of today
        CLASS_TIME.add(Calendar.MONTH, 1);

        // init classOpen
        OPEN_TIME = Calendar.getInstance();
        END_TIME = Calendar.getInstance();

        resetClassOpen();
        resetClassClose();
        resetKeyword();

        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        resetKeyword();
        resetClassOpen();
        resetClassClose();
    }

    //resource 에서 가져와서 application 내의 View 변수에 설정
    private void setButtonHandlers() {
        editText = (EditText) findViewById(R.id.etMemo);
        tv_fileName = (TextView) findViewById(R.id.tv_fileName);
        tv_recordTime = (TextView) findViewById(R.id.tv_recordTime);
        (btnRecord = (Button) findViewById(R.id.btnRecord)).setOnClickListener(btnClick);
        (btnStop = (Button) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
        (btnMemo = (Button) findViewById(R.id.btnMemo)).setOnClickListener(btnClick);
        btnMemo.setEnabled(false);
        btnMemo.setBackgroundColor((Color.parseColor("#d0d2d2")));
        btnStop.setEnabled(false);
        btnRecord.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.INVISIBLE);
        editText.setText(memo + BookmarkCount);
        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio2);
        radio3 = (RadioButton) findViewById(R.id.radio3);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup1);
        img = (ImageView) findViewById(R.id.imageView);
        img.setVisibility(View.INVISIBLE);

        helper = new RecordTimeTable_Helper(BoundActivity2.this);

        String sql = "select keyword_one ,keyword_two, keyword_three from keywordTABLE";
        Cursor cursor = helper.db.rawQuery(sql, null);

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            k1 = cursor.getString(0);
            k2 = cursor.getString(1);
            k3 = cursor.getString(2);
            Log.i("AhReum", "keyword : " + k1 + "   " + "keyword1 : " + k2 + "   keyword2 : " + k3); //
        }

        radio1.setText(k1);
        radio2.setText(k2);
        radio3.setText(k3);
        memo = k1; // 선택된걸로 바꿔야함
        editText.setText(memo + BookmarkCount);


        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {

                    case R.id.radio1:
                        memo = radio1.getText().toString();
                        editText.setText(memo + BookmarkCount);
                        break;

                    case R.id.radio2:
                        memo = radio2.getText().toString();
                        editText.setText(memo + BookmarkCount);
                        break;

                    case R.id.radio3:
                        memo = radio3.getText().toString();
                        editText.setText(memo + BookmarkCount);
                        break;

                    default:
                        memo = "default";
                        editText.setText(memo + BookmarkCount);
                }
            }
        });
    }

    private void setRecordTime() {
        t = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                cnt++;
                millis = cnt;
                int seconds = (int) (millis / 60);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                tv_recordTime.setText(String.format("%02d:%02d:%02d" + " (%02d초)", minutes, seconds, millis - (seconds * 60), millis));
            }

            @Override
            public void onFinish() {
            }
        };
    }

    public View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnRecord: {

                    if (mBound) {
                        mService.startRecording(getFilename());
                        startRecording2();

                    } else {
                        Toast.makeText(BoundActivity2.this, "녹음을 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                case R.id.btnStop: {
                    stopRecording2();
                    mNM.cancel(mId);
                    if (mBound && isRecorder) {
                        mService.stopRecording();
                        isPausing = false;
                        isRecorder = false;

                    } else {
                        Toast.makeText(BoundActivity2.this, "녹음을 중지할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                case R.id.btnMemo: {
                    addBookmark();
                    break;
                }
            }
        }
    };

    public String getFilename() {

        // 값 초기화
        resetKeyword();
        resetClassOpen();
        resetClassClose();
        FILE_COUNT = helper.getFileId();

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
            fileName = FILE_SUBJECT + FILE_COUNT + AUDIO_RECORDER_FILE_EXT_MP4;

        } else {
            fileName = FILE_WEEK + "주차" + FILE_SUBJECT + FILE_COUNT + AUDIO_RECORDER_FILE_EXT_MP4;
        }
        FILE_COUNT = helper.getFileId() + 1;
        path = file2.getAbsolutePath() + "/" + fileName;

        return path;
    }

    public void startRecording2() {

        sendNotification(null);
        BookmarkCount = 1; // setting for each file bookmark

        //시간설정
        FILE_WEEK = CLASS_TIME.get(Calendar.WEEK_OF_YEAR) - OPEN_TIME.get(Calendar.WEEK_OF_YEAR) + 1;
        t.start();

        tv_fileName.setText(fileName);
        btnMemo.setEnabled(true);
        btnMemo.setBackgroundColor((Color.parseColor("#da6c58")));
        btnRecord.setVisibility(View.INVISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        btnRecord.setEnabled(false);
        btnStop.setEnabled(true);
        isRecorder = true;
        isPausing = false;

        helper.insertFile(fileName, fileDirectory, FILE_WEEK, FILE_WEEK, FILE_COUNT);

    }

    public void startRecording3(String fileName1, String fileDirectory1) {

        sendNotification(null);
        BookmarkCount = 1; // setting for each file bookmark

        //시간설정
        FILE_WEEK = CLASS_TIME.get(Calendar.WEEK_OF_YEAR) - OPEN_TIME.get(Calendar.WEEK_OF_YEAR) + 1;
        t.start();

        tv_fileName.setText(fileName1);
        btnMemo.setEnabled(true);
        btnMemo.setBackgroundColor((Color.parseColor("#da6c58")));
        btnRecord.setVisibility(View.INVISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        btnRecord.setEnabled(false);
        btnStop.setEnabled(true);
        isRecorder = true;
        isPausing = false;

        helper.insertFile(fileName1, fileDirectory1, FILE_WEEK, FILE_WEEK, FILE_COUNT);

    }

    public void stopRecording2() {
        BookmarkCount = 1;
        editText.setText(memo + BookmarkCount);
        cnt = 0;
        t.cancel();
        tv_recordTime.setText("00:00:00 (00초)");
        tv_fileName.setText("녹음파일명");
        btnMemo.setEnabled(false);
        btnMemo.setBackgroundColor((Color.parseColor("#d0d2d2")));
        btnStop.setEnabled(false);
        btnRecord.setEnabled(true);
        btnRecord.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.INVISIBLE);

    }

    public void query() {
        String sql = "select memo_id, file_fk, memo from memoTABLE";
        Cursor cursor = helper.db.rawQuery(sql, null);
        int recordCount = cursor.getCount();

        Log.v("AhReum", "레코드카운트 : " + Integer.toString(recordCount));

        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String str1 = cursor.getString(0);
            String str2 = cursor.getString(1);
            String str3 = cursor.getString(2);

        }
    }

    private void addBookmark() {
        //after getting memo, Initialize memo value
        helper.insertMemo(cnt, memo + BookmarkCount, fileName);
        query();
        BookmarkCount++;
        editText.setText(memo + BookmarkCount);
    }

    // 액티비티가 시작되면 서비스에 연결
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(BoundActivity2.this, BoundService.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    // 액티비티가 종료되면 서비스 연결을 해제
    @Override
    protected void onStop() {
        super.onStop();
        if (mBound && isRecorder) {
            //   Toast.makeText(BoundActivity2.this, "BoundActivity2 : onStop()", Toast.LENGTH_SHORT).show();
//            getApplicationContext().unbindService(mConnection);
//            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound && isRecorder) {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
    }

    // ServiceConnection 인터페이스를 구현하는 객체를 생성한다.
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioBinder binder = (AudioBinder) service;
            BoundActivity2.this.mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
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
        mNM.notify(mId, noti);
    }


    private void resetKeyword() {
        // reset new keyword datas from keywordTABLE database
        String sql = "select keyword_one ,keyword_two, keyword_three from keywordTABLE";
        Cursor cursor = helper.db.rawQuery(sql, null);

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            k1 = cursor.getString(0);
            k2 = cursor.getString(1);
            k3 = cursor.getString(2);
            Log.i("AhReum", "keyword : " + k1 + "   " + "keyword1 : " + k2 + "   keyword2 : " + k3); //
        }

        radio1.setText(k1);
        radio2.setText(k2);
        radio3.setText(k3);
        memo = k1; // 선택된걸로 바꿔야함
        editText.setText(memo + BookmarkCount);
    }

    private void resetClassOpen() {
        FILE_WEEK = CLASS_TIME.get(Calendar.WEEK_OF_YEAR) - OPEN_TIME.get(Calendar.WEEK_OF_YEAR) + 1;
        String str = helper.getClassOpen();
        String[] arr = str.split("-");
        OPEN_TIME.set(Calendar.YEAR, Integer.parseInt(arr[0]));
        OPEN_TIME.set(Calendar.MONTH, Integer.parseInt(arr[1]));
        OPEN_TIME.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[2]));
    }

    private void resetClassClose() {
        String str = helper.getClassClose();
        String[] arr = str.split("-");
        END_TIME.set(Calendar.YEAR, Integer.parseInt(arr[0]));
        END_TIME.set(Calendar.MONTH, Integer.parseInt(arr[1]));
        END_TIME.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[2]));
    }
}

