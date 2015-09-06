package com.fourmob.colorpicker.sample;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import com.fourmob.colorpicker.ColorPickerDialog;
import com.fourmob.colorpicker.ColorPickerSwatch;

import java.util.Calendar;

/**
 * Created by bailey on 15. 5. 14..
 */
public class TimetableInput extends FragmentActivity {


    private Button StartTime;
    private Button EndTime;
    //각 셀의 시간 받기 위한 변수
    int mSelectedHour;
    int mSelectedMinutes;
    Calendar mCalendarOpeningTime;
    Calendar mCalendarClosingTime;

    int mSelectedHourplus;
    int mSelectedMinutesplus;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_input_dig);
        Button savebtn = (Button) findViewById(R.id.savebtn);
        StartTime = (Button) findViewById(R.id.start_time_btn);
        EndTime = (Button) findViewById(R.id.end_time_btn);
        Button deletebtn = (Button) findViewById(R.id.deletebtn);
        final Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        StartTime.setText(date);


        String[] time = date.split(":");
        int hour = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[1]);

        mSelectedHour = hour;
        mSelectedMinutes =min;

        mSelectedHourplus = hour+1;
        mSelectedMinutesplus =min;
        EndTime.setText(mSelectedHourplus + ":" + "0" + mSelectedMinutesplus);


        setOpeningAndClosingTimes();


        final CheckBox week1 = (CheckBox) findViewById(R.id.week1);
        final CheckBox week2 = (CheckBox) findViewById(R.id.week2);
        final CheckBox week3 = (CheckBox) findViewById(R.id.week3);
        final CheckBox week4 = (CheckBox) findViewById(R.id.week4);
        final CheckBox week5 = (CheckBox) findViewById(R.id.week5);



        final String monday_checked = intent.getStringExtra("monday_checked");

        final String tuesday_checked = intent.getStringExtra("tuesday_checked");

        final String wednesday_checked = intent.getStringExtra("wednesday_checked");

        final String thursday_checked = intent.getStringExtra("thursday_checked");

        final String friday_checked = intent.getStringExtra("friday_checked");

        if(monday_checked != null){
            if(monday_checked.equals("true")){

                week1.setChecked(!week1.isChecked());
                Log.v("checking", "monday is checked");
            }
        }

        if(tuesday_checked != null){
            if(tuesday_checked.equals("true")){
                week2.setChecked(!week2.isChecked());
                Log.v("checking", "tuesday is checked");
            }
        }

        if(wednesday_checked != null){
            if(wednesday_checked.equals("true")){

                week3.setChecked(!week3.isChecked());
                Log.v("checking", "wednesday is checked");
            }
        }

        if(thursday_checked != null){
            if(thursday_checked.equals("true")){

                week4.setChecked(!week4.isChecked());
                Log.v("checking", "thursday is checked");
            }
        }
        if(friday_checked != null){
            if(friday_checked.equals("true")){

                week5.setChecked(!week5.isChecked());
                Log.v("checking", "friday is checked");
            }
        }



        //저장 버튼을 누르게 되면 id,강의명,강의실,시작 시간 ,끝 시간 데이터 timetable.xml에 보내게 된다.
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("clicked","true");
                EditText put_subject = (EditText) findViewById(R.id.input_subject);
                EditText put_classroom = (EditText) findViewById(R.id.input_classroom);
                Button put_starttime = (Button) findViewById(R.id.start_time_btn);
                Button put_endtime = (Button) findViewById(R.id.end_time_btn);
                Button color_picker = (Button) findViewById(R.id.color_picker);

                int id = intent.getIntExtra("id", 0);
                String date = intent.getStringExtra("date");



                Boolean monday = week1.isChecked();
                String monday_boolean = String.valueOf(monday);

                Boolean tuesday = week2.isChecked();
                String tuesday_boolean = String.valueOf(tuesday);

                Boolean wednesday = week3.isChecked();
                String wednesday_boolean = String.valueOf(wednesday);

                Boolean thursday = week4.isChecked();
                String thursday_boolean = String.valueOf(thursday);

                Boolean friday= week5.isChecked();
                String friday_boolean = String.valueOf(friday);

                //월 -금 체크 여부를 보내주는 것
                intent.putExtra("monday_boolean", monday_boolean);
                intent.putExtra("tuesday_boolean", tuesday_boolean);
                intent.putExtra("wednesday_boolean", wednesday_boolean);
                intent.putExtra("thursday_boolean",thursday_boolean );
                intent.putExtra("friday_boolean", friday_boolean);
                intent.putExtra("id", id);
                intent.putExtra("subject", put_subject.getText().toString());
                intent.putExtra("classroom", put_classroom.getText().toString());
                intent.putExtra("starttime", put_starttime.getText().toString());
                intent.putExtra("endtime", put_endtime.getText().toString());


                setResult(RESULT_OK, intent);

                finish();


            }

        });// touch save button

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 여기서 this는 Activity의 this

// 여기서 부터는 알림창의 속성 설정
                builder/*.setTitle("종료 확인 대화 상자") */        // 제목 설정
                        .setMessage("일정을 삭제하시겠습니까?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                intent.putExtra("clicked", "false");
                                setResult(RESULT_OK, intent);
                                finish();

                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            // 취소 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                final AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기

//                intent.putExtra("clicked","false");
//                setResult(RESULT_OK, intent);
//                finish();

            }
        });




        //시작하는 시간을 저장
        StartTime.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                showTimePickerDialog(mSelectedHour, mSelectedMinutes, true, mOnTimeSetListener);

            }
        });

        //끝나는 시간을 지정하는 리스너
        EndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePickerDialog(mSelectedHourplus, mSelectedMinutesplus, true, mOnTimeSetListener1);

            }
        });




        //배경 색깔을 정하기 위한 색깔 지정 함수
        final Button color_picker = (Button)findViewById(R.id.color_picker);
        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.dialog_title, new int[]{Color.rgb(253,189,224),Color.rgb(207,232,128) ,
                        Color.rgb(168,170,244), Color.rgb(253,243,146), Color.rgb(254,177,149),
                        Color.rgb(240,240,238), Color.rgb(191,234,243), Color.rgb(112,177,199), Color.rgb(114,196,190),
                        Color.rgb(245,159,160), Color.rgb(179,143,181),Color.rgb(255,204,204)},
                Color.rgb(255,204,204), 3, 2);

        //색깔을 클릭하게 되면 보여주게 하는 것+__+
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                findViewById(R.id.color_picker).setBackgroundColor(color);
                //Toast.makeText(TimetableInput.this, "selectedColor : " + color, Toast.LENGTH_SHORT).show();
                intent.putExtra("color", color);

            }


        });

        color_picker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");
            }
        });
    }//oncreate마지막

    //timepicker 지정하는 함수인데 아직 실행 안됨
    private void setOpeningAndClosingTimes() {
        mCalendarOpeningTime = Calendar.getInstance();
        mCalendarOpeningTime.set(Calendar.HOUR, 9);
        mCalendarOpeningTime.set(Calendar.MINUTE, 00);
        //   mCalendarClosingTime.set(Calendar.AM_PM, Calendar.AM);

        mCalendarClosingTime = Calendar.getInstance();
        mCalendarClosingTime.set(Calendar.HOUR, 18);
        mCalendarClosingTime.set(Calendar.MINUTE, 00);
        // mCalendarClosingTime.set(Calendar.AM_PM, Calendar.PM);
    }

    private void updateTimeUI() {
        String hour = (mSelectedHour > 9) ? ""+mSelectedHour: "0"+mSelectedHour ;
        String minutes = (mSelectedMinutes > 9) ?""+mSelectedMinutes : "0"+mSelectedMinutes;
        StartTime.setText(hour+":"+minutes);
    }

    private void updateTimeUI1() {
        String hour = (mSelectedHourplus > 9) ? ""+mSelectedHourplus: "0"+mSelectedHourplus ;
        String minutes = (mSelectedMinutesplus > 9) ?""+mSelectedMinutesplus : "0"+mSelectedMinutesplus;
        EndTime.setText(hour + ":" + minutes);
    }

    // initialize the TimePickerDialog

    private TimePickerDialog showTimePickerDialog(int hour, int min, boolean is24Hour,
                                                  TimePickerDialog.OnTimeSetListener listener) {
        TimePickerDialog dialog = new TimePickerDialog(this, listener, hour, min, is24Hour);

        dialog.show();
        return dialog;
    }



    final TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour, int min) {
            // update the current variables (hour and minutes)
            mSelectedHour = hour;
            mSelectedMinutes = min;

            // update txtTime with the selected time
            updateTimeUI();
        }
    };
    final TimePickerDialog.OnTimeSetListener mOnTimeSetListener1 = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour, int min) {
            // update the current variables (hour and minutes)
            mSelectedHourplus = hour;
            mSelectedMinutesplus = min;

            // update txtTime with the selected time
            updateTimeUI1();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



} //END of Timetable Class
