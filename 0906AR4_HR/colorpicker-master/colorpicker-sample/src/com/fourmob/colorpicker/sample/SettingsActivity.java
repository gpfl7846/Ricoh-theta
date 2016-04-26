package com.fourmob.colorpicker.sample;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SettingsActivity extends Activity {

    //Database를 생성 관리하는 클래스
    private RecordTimeTable_Helper helper;
    RecordTimeTable_Helper dbHelper;
    // private TextView    mDateDisplay;
    private Button start_date, end_date;
    private int mYear, mYear2;
    private int mMonth, mMonth2;
    private int mDay, mDay2;
    static final int DATE_DIALOG_START = 0;
    static final int DATE_DIALOG_END = 1;
    public static Calendar OPEN_TIME11, OPEN_TIME12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        helper = new RecordTimeTable_Helper(SettingsActivity.this);

        start_date = (Button) findViewById(R.id.btn_start_date);
        end_date = (Button) findViewById(R.id.btn_end_date);

        dbHelper = new RecordTimeTable_Helper(this);

        TextView btn_dropbox = (TextView) findViewById(R.id.btn_dropbox);
        final TextView btn_before = (TextView) findViewById(R.id.btn_before);
        final TextView btn_del_info = (TextView) findViewById(R.id.btn_del_info);
        final TextView change_keyword = (TextView) findViewById(R.id.change_keyword);
        final TextView btn_totuial = (TextView) findViewById(R.id.btn_totuial);

        //  change_keyword
        ToggleButton toggle = (ToggleButton) findViewById(R.id.btn_auto);

        final Calendar c = Calendar.getInstance();

        //캘린더 값을 설정함

        c.set(Calendar.MONTH, 03);
        c.set(Calendar.DAY_OF_MONTH, 02);


        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        //캘린더 객체를 현재날짜로 얻음
        final Calendar c2 = Calendar.getInstance();

        //캘린더 값을 설정함

        c2.set(Calendar.MONTH, 06);
        c2.set(Calendar.DAY_OF_MONTH, 31);


        //값을 캘린더에서 얻어옴
        mYear2 = c2.get(Calendar.YEAR);
        mMonth2 = c2.get(Calendar.MONTH);
        mDay2 = c2.get(Calendar.DAY_OF_MONTH);


        //시작 시간 datepicker
        start_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(DATE_DIALOG_START);
            }
        });

        //끝나는 날짜 datepicker
        end_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(DATE_DIALOG_END);
            }
        });

        //드롭박스 동기화 하기
        btn_dropbox.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                if (v.isClickable()) {

                    Intent intent = new Intent(SettingsActivity.this, Dropboxmain.class);
                    startActivity(intent);

                }
            }

        });
        //자동 녹음 on.off
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                } else {

                }
            }
        });


        //몇초전 btn
        final int timeM = dbHelper.queryTimeMachine();
        final CharSequence[] items = {"0", "10", "30", "60"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        btn_before.setText(new StringBuilder().append("책갈피를 ").append(timeM).append("초 전으로 설정 합니다."));
        btn_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 여기서 부터는 알림창의 속성 설정
                builder.setTitle("몇 초전으로 설정할까요?")        // 제목 설정
                        .setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index) {
                                btn_before.setText(new StringBuilder()
                                        .append(items[index]).append("초 전으로 설정 합니다."));
                                int number = Integer.parseInt(items[index].toString());
                                helper.updateTimeMachine(number);

                            }
                        });
                final AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기

            }
        });


        //키워드 변경
        change_keyword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.isClickable()) {
                    Intent intent = new Intent(SettingsActivity.this, SettingsKeyword.class);
                    startActivity(intent);

                }
            }

        });

        btn_totuial.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, FirstStartActivity.class);
                startActivity(intent);
            }
        });

        //개발자 정보 페이지
        btn_del_info.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.isClickable()) {
                    Intent intent = new Intent(SettingsActivity.this, Developer_Info.class);
                    startActivity(intent);
                    setAlarm();

                }
            }

        });


    }

    private void updateDisplay() {
        String str = mYear + "-" + mMonth + "-" + mDay;
        start_date.setText(str);
        helper.updateClassOepn(str);
        Toast.makeText(SettingsActivity.this, "개강날짜가 [" + str + "]로 변경되었습니다.", Toast.LENGTH_SHORT).show();
        //Log.i("AhReum","개강날짜가 변경되었습니다.");
    }

    private void updateDisplay2() {
        String str = mYear2 + "-" + mMonth2 + "-" + mDay2;
        end_date.setText(str);
        helper.updateClassClose(str);
        Toast.makeText(SettingsActivity.this, "종강날짜가 [" + str + "]로 변경되었습니다.", Toast.LENGTH_SHORT).show();
        //Log.i("AhReum", "updateDisplay()");
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mYear = year;
            mMonth = monthOfYear + 1;
            mDay = dayOfMonth;
            updateDisplay();
            Log.i("AhReum", "개강날짜가 설정되었습니다");
            helper.queryClassdate();
        }
    };

    private DatePicker.OnDateChangedListener mdateSetListener = new DatePicker.OnDateChangedListener() {

        public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear + 1;
            mDay = dayOfMonth;
            updateDisplay();
            Log.i("AhReum", "개강날짜가 설정되었습니다");
            helper.queryClassdate();

        }
    };

    private DatePickerDialog.OnDateSetListener mDateSetListener2 =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    mYear2 = year;
                    mMonth2 = monthOfYear + 1;
                    mDay2 = dayOfMonth;
                    updateDisplay2();
                    Log.i("AhReum", "종강날짜가 설정되었습니다");
                }
            };


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_START:
                //Log.i("AhReum","개강날짜입니다");
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
            case DATE_DIALOG_END:
                //Log.i("AhReum","종강날짜입니다");
                return new DatePickerDialog(this, mDateSetListener2, mYear2, mMonth2, mDay2);
        }

        return null;
    }

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

                SettingsActivity.OPEN_TIME11 = Calendar.getInstance();
                SettingsActivity.OPEN_TIME11.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_hour));
                SettingsActivity.OPEN_TIME11.set(Calendar.MINUTE, Integer.parseInt(start_minutes));
                SettingsActivity.OPEN_TIME11.set(Calendar.SECOND, 00);

                //첫번째와 비교할 두번째시간
                String start_time2 = (String) starttime.get(time + 1);
                String start_hour2 = start_time2.substring(0, 2);
                String start_minutes2 = start_time2.substring(3, 5);

                SettingsActivity.OPEN_TIME12 = Calendar.getInstance();
                SettingsActivity.OPEN_TIME12.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_hour2));
                SettingsActivity.OPEN_TIME12.set(Calendar.MINUTE, Integer.parseInt(start_minutes2));
                SettingsActivity.OPEN_TIME12.set(Calendar.SECOND, 00);

                SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분 ss초");


                long nowtimer = OPEN_TIME11.getTimeInMillis();
                long opentimer = Calendar.getInstance().getTimeInMillis();
//
                if (start_time.equals(start_time2) && !(start_time.equals(start_time0)) && opentimer < nowtimer) {

                    Intent intent2 = new Intent(SettingsActivity.this, BoundService2.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pintent2 = PendingIntent.getService(SettingsActivity.this, time, intent2, 0);
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    String today2 = df.format(OPEN_TIME11.getTime());
                    Log.v("Timeusing", today2);
                    //am.setRepeating(AlarmManager.RTC_WAKEUP, SettingsActivity.OPEN_TIME11.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pintent2);
                    am.set(AlarmManager.RTC_WAKEUP, SettingsActivity.OPEN_TIME11.getTimeInMillis(), pintent2);
                    Log.v("Timeusing", "SettingActivity : " + "start_time : " + start_time + "시 알람 설정");

                    time += 1; //겹치지 않게 set

                } else if (start_time.equals(start_time0)) {
                    Log.v("Timeusing", "Skip");

                } else if (opentimer < nowtimer) {
                    Intent intent2 = new Intent(SettingsActivity.this, BoundService2.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pintent2 = PendingIntent.getService(SettingsActivity.this, time, intent2, 0);
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    String today2 = df.format(OPEN_TIME11.getTime());
                    Log.v("Timeusing", today2);
//                        am.setRepeating(AlarmManager.RTC_WAKEUP, SettingsActivity.OPEN_TIME11.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pintent2); //am.cancel(pIntent2); 알람해제
                    am.set(AlarmManager.RTC_WAKEUP, SettingsActivity.OPEN_TIME11.getTimeInMillis(), pintent2); //am.cancel(pIntent2); 알람해제
                    Log.v("Timeusing", "SettingsActivity : " + "start_time : " + start_time + "시 알람 설정");
                }


            }
        }
    }

}

