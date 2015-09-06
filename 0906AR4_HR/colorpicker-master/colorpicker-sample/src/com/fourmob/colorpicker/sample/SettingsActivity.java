package com.fourmob.colorpicker.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;

public class SettingsActivity extends Activity {

   // private TextView    mDateDisplay;
    private Button      start_date, end_date;
    private int         mYear,mYear2;
    private int         mMonth,mMonth2;
    private int         mDay,mDay2;
    static final int DATE_DIALOG_START = 0;
    static final int DATE_DIALOG_END = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);


        start_date = (Button)findViewById(R.id.btn_start_date);
        end_date = (Button)findViewById(R.id.btn_end_date);

        TextView btn_dropbox = (TextView)findViewById(R.id.btn_dropbox);
        final TextView btn_before = (TextView)findViewById(R.id.btn_before);
        final TextView btn_del_info = (TextView)findViewById(R.id.btn_del_info);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.btn_auto);

        final Calendar c = Calendar.getInstance();

        //캘린더 값을 설정함

        c.set(Calendar.MONTH,03);
        c.set(Calendar.DAY_OF_MONTH,02);


        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        //캘린더 객체를 현재날짜로 얻음
        final Calendar c2 = Calendar.getInstance();

        //캘린더 값을 설정함

        c2.set(Calendar.MONTH,06);
        c2.set(Calendar.DAY_OF_MONTH,31);


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
        final CharSequence[] items = {"0초", "5초", "10초","15초"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        btn_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            // 여기서 부터는 알림창의 속성 설정
                builder.setTitle("몇 초전으로 설정할까요?")        // 제목 설정
                        .setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index) {
                                btn_before.setText(new StringBuilder()
                                        .append(items[index]).append("전으로 설정 합니다."));

                            }
                        });

                final AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기



            }
        });

        //개발자 정보 페이지
        btn_del_info.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.isClickable()) {
                    Intent intent = new Intent(SettingsActivity.this, Developer_Info.class);
                    startActivity(intent);

                }
            }

        });
        updateDisplay();
        updateDisplay2();

    }

    private void updateDisplay()
    {
        start_date.setText(new StringBuilder()
                .append(mYear).append("-")
                .append(mMonth).append("-")
                .append(mDay).append(" "));
    }

    private void updateDisplay2()
    {
        end_date.setText(new StringBuilder()
                .append(mYear2).append("-")
                .append(mMonth2).append("-")
                .append(mDay2).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };

    private DatePickerDialog.OnDateSetListener mDateSetListener2 =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    mYear2 = year;
                    mMonth2 = monthOfYear;
                    mDay2 = dayOfMonth;
                    updateDisplay2();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch(id)
        {
            case DATE_DIALOG_START:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
            case DATE_DIALOG_END:
                return new DatePickerDialog(this, mDateSetListener2, mYear2, mMonth2, mDay2);
        }

        return null;
    }


}

