package com.fourmob.colorpicker.sample;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class StartServiceActivity extends Activity {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seriveex);
        Calendar cur_cal = new GregorianCalendar();
        // cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar
        cur_cal.set(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        cur_cal.set(Calendar.HOUR_OF_DAY, 10);
        cur_cal.set(Calendar.MINUTE, 20);
        cur_cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
//        cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
//        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
//        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
        final Calendar cur_cal2 = new GregorianCalendar();
        cur_cal2.set(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        cur_cal2.set(Calendar.HOUR_OF_DAY, 10);
        cur_cal2.set(Calendar.MINUTE, 22);
        cur_cal2.set(Calendar.SECOND, 10);


//
//        Intent intent = new Intent(StartServiceActivity.this, MyService.class);
//        PendingIntent pintent = PendingIntent.getService(StartServiceActivity.this, 0, intent, 0);
//        //서비스를 등록하는것
//        final AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        //알람등록
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cur_cal.getTimeInMillis(), 24 * 60 * 60 * 1000, pintent);
//
//
//      //  mDateDisplay = (TextView) findViewById(R.id.dateDisplay1);
//      mTimeDisplay = (TextView) findViewById(R.id.timeDisplay2);
//        final Calendar c = Calendar.getInstance();
//        mYear = c.get(Calendar.YEAR);
//        mMonth = c.get(Calendar.MONTH);
//        mDay = c.get(Calendar.DAY_OF_MONTH);
//        mHour = c.get(Calendar.HOUR_OF_DAY);
//        mMinute = c.get(Calendar.MINUTE);
//        mSecond = c.get(Calendar.SECOND);
//
//        mHandler = new Handler();
//        r = new Runnable() {
//            @Override
//            public void run() {
//               //updateDisplay();
//                //Log.i("sss", "실시간 시간   " + String.valueOf(updateDisplay().getTime()));
//                long difference = Math.abs(updateDisplay().getTimeInMillis() - cur_cal2.getTimeInMillis())/1000;
//                Log.i("sss", "차이 " + String.valueOf(difference));
//                if(difference==0)
//                {
//                    Toast.makeText(StartServiceActivity.this, "same2", Toast.LENGTH_LONG).show();
//                    stopService(new Intent(getBaseContext(), MyService.class));
//
//                }
//
//
//            }
//        };
//        mHandler.postDelayed(r, 1000);
//        Log.i("sss", "시간이얌   " + String.valueOf(cur_cal2.getTime()));
//        //Toast.makeText(MainActivity.this, , Toast.LENGTH_LONG).show();
//
//
//
//

    // click listener for the button to stop service
    Button btnStop = (Button) findViewById(R.id.button2);
    btnStop.setOnClickListener(new View.OnClickListener()

                               {
                                   @Override
                                   public void onClick(View v) {
                                       stopService(new Intent(getBaseContext(), DBAdapter.BoundService.class));
                                   }
                               }

    );

        // click listener for the button to stop service
        Button btnStop2 = (Button) findViewById(R.id.button1);
        btnStop2.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick (View v){
                                          startService(new Intent(getBaseContext(), DBAdapter.BoundService.class));
                                       }
                                   }

        );
}


    private Calendar updateDisplay() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);
//
//        mDateDisplay.setText(
//                new StringBuilder()
//                        .append(mYear).append("-")
//                        .append(mMonth + 1).append("-")
//                        .append(mDay).append(" "));
//
        mTimeDisplay.setText(
                new StringBuilder()
                        .append(pad(mHour)).append(":")
                        .append(pad(mMinute)).append(":")
                        .append(pad(mSecond)));


        c.set(Calendar.HOUR_OF_DAY, mHour);
        c.set(Calendar.MINUTE, mMinute);
        c.set(Calendar.SECOND, mSecond);
        mHandler.postDelayed(r, 1000);
        return c;


    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}


