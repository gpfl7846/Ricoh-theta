package com.fourmob.colorpicker.sample;

import android.app.Activity;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class AudioRecording extends Activity {

    //Database를 생성 관리하는 클래스
    private RecordTimeTable_Helper helper;

    //kind of recorder file output format
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";

    //storage directory Top-folder
    private static final String AUDIO_RECORDER_FOLDER = "MultiPlayer";

    // for Filename Setting
    private String FILE_SUBJECT = "";
    private int  FILE_WEEK=1, FILE_COUNT=1;
    private String fileName, fileDirectory;
    public Calendar CLASS_TIME,OPEN_TIME, START_TIME, END_TIME, NOW_TIME,COM_TIME_START,COM_TIME_END;

    //Show Recording Time
    public int cnt=0;
    public CountDownTimer t;
    private long millis;

    // Recorording file format
    private MediaRecorder recorder = null;

    boolean isPausing=false;
    EditText editText;
    TextView textView, textView2, tv_fileName, tv_recordTime,tv_memoTable, tv_fileTable ;
    ImageButton btnRecord, btnStop;
    Button btnMemo;
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

    private Runnable r;
    private Handler mHandler;



    public DBAdapter.MainActivity mainActivity;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recording2);
        setRecordTime();
        setButtonHandlers();



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
        (btnStop = (ImageButton) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
        (btnMemo= (Button) findViewById(R.id.btnMemo)).setOnClickListener(btnClick);
        btnMemo.setEnabled(false);
        btnStop.setVisibility(View.INVISIBLE);
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

    //Filename Setting
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
        fileDirectory = file2.getAbsolutePath()+"/";
        FILE_SUBJECT=helper.whatSubject();
        FILE_COUNT = helper.getFileId()+1;
        fileName =FILE_WEEK+"주차"+FILE_SUBJECT+ FILE_COUNT + AUDIO_RECORDER_FILE_EXT_MP4;
        path=file2.getAbsolutePath()+"/"+fileName;

        return path;
    }

    private void startRecording() {

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        tv_fileName.setText(fileName);
        btnMemo.setEnabled(true);
        btnRecord.setBackgroundResource(R.drawable.record_pause);
        btnStop.setVisibility(View.VISIBLE);

        isPausing = false; // state change

        helper.insertFile(fileName, fileDirectory, FILE_WEEK,FILE_WEEK,FILE_COUNT);
        query0();

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {

        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }

        cnt = 0;
        t.cancel();
        tv_recordTime.setText("00:00:00 (00초)");
        tv_fileName.setText("녹음파일명");

        btnRecord.setBackgroundResource(R.drawable.record_start);
        btnMemo.setEnabled(false);
        btnStop.setVisibility(View.INVISIBLE);
        textView2.setText(R.string.app_info2);

    }


    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(AudioRecording.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(AudioRecording.this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    public View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            helper = new RecordTimeTable_Helper(AudioRecording.this);

            switch (v.getId()) {
                case R.id.btnRecord: {
                    if(recorder != null && !isPausing) {
                        t.cancel();
                        Toast.makeText(AudioRecording.this, "Pause Recording",
                                Toast.LENGTH_SHORT).show();
                        pauseRecording();
                        //restartRecording();
                        break;
                    }
                    else if(recorder!= null && isPausing) {
                        t.start();
                        Toast.makeText(AudioRecording.this, "Restart Recording",
                                Toast.LENGTH_SHORT).show();
                        restartRecording();
                        break;
                    }
                    else {
                        //Calendar OPEN_TIME = mainActivity.getOpenDate();
                        textView.setText("오늘 날짜 : " + String.format("%d / %d / %d", CLASS_TIME.get(Calendar.YEAR), CLASS_TIME.get(Calendar.MONTH), CLASS_TIME.get(Calendar.DAY_OF_MONTH)));
                        textView2.setText("개강 날짜 : " + String.format("%d / %d / %d", OPEN_TIME.get(Calendar.YEAR), OPEN_TIME.get(Calendar.MONTH), OPEN_TIME.get(Calendar.DAY_OF_MONTH)));

                        FILE_WEEK = CLASS_TIME.get(Calendar.WEEK_OF_YEAR) - OPEN_TIME.get(Calendar.WEEK_OF_YEAR)+1; //프래그먼트간 통신가능해야함 어떻게하지 ㅜ_ㅠ
                        t.start();
                        Toast.makeText(AudioRecording.this, "Start Recording",
                                Toast.LENGTH_SHORT).show();
                        startRecording();
                        break;
                    }

                }

                case R.id.btnStop: {
                    Toast.makeText(AudioRecording.this, "Stop Recording",
                            Toast.LENGTH_SHORT).show();
                    stopRecording();
                    break;
                }

                case R.id.btnMemo: {

                    //after getting memo, Initialize memo value
                    String memo = editText.getText().toString();
                    editText.setText("");

                    helper.insertMemo(cnt, memo, fileName);
                    query();
                    textView.setText(R.string.app_info);
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

    private void startRecording2() {


        tv_fileName.setText(fileName);
        btnMemo.setEnabled(true);
        btnRecord.setBackgroundResource(R.drawable.record_pause);
        btnStop.setVisibility(View.VISIBLE);

        isPausing = false; // state change

        helper.insertFile(fileName, fileDirectory, FILE_WEEK, FILE_WEEK, FILE_COUNT);
        query0();

    }

    private void stopRecording2() {

        cnt = 0;
        t.cancel();
        tv_recordTime.setText("00:00:00 (00초)");
        tv_fileName.setText("녹음파일명");

        btnRecord.setBackgroundResource(R.drawable.record_start);
        btnMemo.setEnabled(false);
        btnStop.setVisibility(View.INVISIBLE);
        textView2.setText(R.string.app_info2);

    }


    public void pauseRecording() {
        btnRecord.setBackgroundResource(R.drawable.record_start);
        btnMemo.setEnabled(false);
        btnStop.setVisibility(View.VISIBLE);
        isPausing = true;
    }

    public void restartRecording() {
        btnRecord.setBackgroundResource(R.drawable.record_pause);
        btnMemo.setEnabled(true);
        btnStop.setVisibility(View.VISIBLE);
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

}
