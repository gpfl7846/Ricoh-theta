package com.fourmob.colorpicker.sample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.fourmob.colorpicker.sample.BoundService.AudioBinder;

import java.io.File;
import java.util.Calendar;


public class BoundActivity extends Activity {

    private BoundService mService;    // 연결 타입 서비스
    private boolean mBound = false;    // 서비스 연결 여부
    private Button btnStart, btnStop;

    //Database를 생성 관리하는 클래스
    private RecordTimeTable_Helper helper;

    // for Filename Setting
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "MultiPlayer";
    private String FILE_SUBJECT = "fileName";
    private int  FILE_WEEK=1, FILE_COUNT=1;
    private String fileName, fileDirectory;
    public Calendar CLASS_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btnStart = (Button)findViewById(R.id.button1);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    //int num = mService.getRandomNumber();
                    //Toast.makeText(getParent(), "Random Number = " + num, Toast.LENGTH_SHORT).show();

                    mService.startRecording(getFilename());

                }

            }
        });

        btnStop = (Button)findViewById(R.id.button2);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getParent(),"Stop Recording",Toast.LENGTH_SHORT).show();
                mService.stopRecording();
            }
        });

        helper = new RecordTimeTable_Helper(this);
    }

    // 액티비티가 시작되면 서비스에 연결
    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(BoundActivity.this, BoundService.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    // 액티비티가 종료되면 서비스 연결을 해제
    @Override
    protected void onStop(){
        super.onStop();
        if(mBound){
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
    }


    // ServiceConnection 인터페이스를 구현하는 객체를 생성한다.
    private ServiceConnection mConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioBinder binder = (AudioBinder) service;
            BoundActivity.this.mService = binder.getService();
            mBound = true;

            //Toast.makeText(BoundActivity.this, mBound,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
            mBound = false;
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
        path=file.getAbsolutePath()+"/"+fileName;

        Toast.makeText(getParent(),"파일명 : "+path,Toast.LENGTH_SHORT).show();

        return path;
    }
}
