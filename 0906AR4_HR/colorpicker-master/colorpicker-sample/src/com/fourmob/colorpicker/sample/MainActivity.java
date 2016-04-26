package com.fourmob.colorpicker.sample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends TabActivity {
    private static final int ANIMATION_TIME = 240;
    private View previousView;
    private View currentView;
    private int currentTab;
    Calendar cur_cal = new GregorianCalendar();
    public static Calendar OPEN_TIME11, OPEN_TIME12;

    static final String TAG = "Timeusing";

    //Database를 생성 관리하는 클래스
    private RecordTimeTable_Helper helper;

    // for Filename Setting
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "MultiPlayer";
    private static String FILE_SUBJECT = "fileName";
    private static int FILE_WEEK = 1, FILE_COUNT = 1;
    private static String fileName, fileDirectory;
    public static Calendar CLASS_TIME, OPEN_TIME, END_TIME;
    public static String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        helper = new RecordTimeTable_Helper(MainActivity.this);

        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, BoundActivity2.class);
        spec = tabHost.newTabSpec("녹음기").setIndicator("", res.getDrawable(R.drawable.ic_tab_one)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Timetable2.class);
        spec = tabHost.newTabSpec("시간표").setIndicator(res.getString(R.string.timetable), res.getDrawable(R.drawable.ic_tab_two)).setContent(intent);
        //spec = tabHost.newTabSpec("library_browser");
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, SettingsActivity.class);
        spec = tabHost.newTabSpec("설정").setIndicator(res.getString(R.string.timetable), res.getDrawable(R.drawable.ic_tab_three)).setContent(intent);
        // spec = tabHost.newTabSpec("file_browser");
        tabHost.addTab(spec);

        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#e85c42"));
        tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#e85c42"));
        tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#e85c42"));

        tabHost.setCurrentTab(0);

        this.getTabHost().setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                View currentView = MainActivity.this.getTabHost().getCurrentView();
                if (MainActivity.this.getTabHost().getCurrentTab() > MainActivity.this.currentTab) {
                    currentView.setAnimation(MainActivity.this.inFromRightAnimation());
                } else {
                    currentView.setAnimation(MainActivity.this.outToRightAnimation());
                }

                MainActivity.this.currentTab = MainActivity.this.getTabHost().getCurrentTab();
            }
        });


        SharedPreferences preference = getSharedPreferences("a", MODE_PRIVATE);
        int firstviewshow = preference.getInt("First", 0);

        if (firstviewshow != 1) {
            intent = new Intent(MainActivity.this, FirstStartActivity.class);
            startActivity(intent);
        }

        setAlarm(); //autoRecroding
    }


    public Animation inFromRightAnimation() {
        TranslateAnimation inFromRight = new TranslateAnimation(2, 1.0F, 2, 0.0F, 2, 0.0F, 2, 0.0F);
        inFromRight.setDuration(240L);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation outToRightAnimation() {
        TranslateAnimation outtoLeft = new TranslateAnimation(2, -1.0F, 2, 0.0F, 2, 0.0F, 2, 0.0F);
        outtoLeft.setDuration(240L);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

//   /*
//    * AutoRecording

//   public void setAlarm(Context context) {

    public void setAlarm() {

        Log.v("Timeusing", "setAlarm()");

        ArrayList starttime = helper.whatStartTime();
        Log.v("Timeusing", "starttime 사이즈" +
                "    :  " + starttime.size());

        if (starttime.size() > 1) {
            for (int time = 1; time < starttime.size() - 1; time++) {

                String start_time0 = "init";
                if (time != 1) {
                    start_time0 = (String) starttime.get(time - 1);
                }

                //첫번째시간
                String start_time = (String) starttime.get(time);
                //Log.v("Timeusing", start_time);
                String start_hour = start_time.substring(0, 2);
                String start_minutes = start_time.substring(3, 5);

                MainActivity.OPEN_TIME11 = Calendar.getInstance();
                MainActivity.OPEN_TIME11.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_hour));
                MainActivity.OPEN_TIME11.set(Calendar.MINUTE, Integer.parseInt(start_minutes));
                MainActivity.OPEN_TIME11.set(Calendar.SECOND, 00);

                //첫번째와 비교할 두번째시간
                String start_time2 = (String) starttime.get(time + 1);
                String start_hour2 = start_time2.substring(0, 2);
                String start_minutes2 = start_time2.substring(3, 5);

                MainActivity.OPEN_TIME12 = Calendar.getInstance();
                MainActivity.OPEN_TIME12.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_hour2));
                MainActivity.OPEN_TIME12.set(Calendar.MINUTE, Integer.parseInt(start_minutes2));
                MainActivity.OPEN_TIME12.set(Calendar.SECOND, 00);

                SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분 ss초");


                long nowtimer = OPEN_TIME11.getTimeInMillis();
                long opentimer = Calendar.getInstance().getTimeInMillis();
//
                if (start_time.equals(start_time2) && !(start_time.equals(start_time0)) && opentimer < nowtimer) {

                    Intent intent2 = new Intent(MainActivity.this, BoundService2.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pintent2 = PendingIntent.getService(MainActivity.this, time, intent2, 0);
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    String today2 = df.format(OPEN_TIME11.getTime());
                    Log.v("Timeusing", today2);
                    //am.setRepeating(AlarmManager.RTC_WAKEUP, SettingsActivity.OPEN_TIME11.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pintent2);
                    am.set(AlarmManager.RTC_WAKEUP, MainActivity.OPEN_TIME11.getTimeInMillis() + 2000, pintent2);
                    Log.v("Timeusing", "MainActivity : " + "start_time : " + start_time + "시 알람 설정");

                    time += 1; //겹치지 않게 set

                } else if (start_time.equals(start_time0)) {
                    Log.v("Timeusing", "Skip");

                } else if (opentimer < nowtimer) {
                    Intent intent2 = new Intent(MainActivity.this, BoundService2.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pintent2 = PendingIntent.getService(MainActivity.this, time, intent2, 0);
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    String today2 = df.format(OPEN_TIME11.getTime());
                    Log.v("Timeusing", today2);
//                        am.setRepeating(AlarmManager.RTC_WAKEUP, SettingsActivity.OPEN_TIME11.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pintent2); //am.cancel(pIntent2); 알람해제
                    am.set(AlarmManager.RTC_WAKEUP, MainActivity.OPEN_TIME11.getTimeInMillis() + 2000, pintent2); //am.cancel(pIntent2); 알람해제
                    Log.v("Timeusing", "MainActivity : " + "start_time : " + start_time + "시 알람 설정");
                }


            }
        }
    }


    public void getFilename() {

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
//        FILE_COUNT = helper.getFileId()+1;
        FILE_COUNT++;
        path = file2.getAbsolutePath() + "/" + fileName;
        //Log.i("AhReum",path.toString());

        //Toast.makeText(getParent(),"파일명 : "+fileName,Toast.LENGTH_SHORT).show();

        //return path;
    }


}
