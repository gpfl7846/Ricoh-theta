package com.fourmob.colorpicker.sample;

/**
 * Created by bailey on 15. 9. 13..
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fourmob.colorpicker.sample.PlayerService.PlayerBinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Timetable2 extends Activity implements View.OnClickListener {
    private PlayerService playerService;
    private boolean mBound = false;
    private final String tag = "timetable.class";


    private String db_name = "timetable.db";


    private RecordTimeTable_Helper helper;

    SQLiteDatabase db;
    Cursor cur;
    int db_id, db_background;
    String db_classroom, db_subject, db_starttime, db_endtime, db_mon, db_tues, db_wednes, db_thus, db_fri, db_sat, db_sun;
    Boolean control = false;
    TextView textViews[] = new TextView[60];
    ImageView imageviews[] = new ImageView[60];
    ListView listView;
    HashMap<Integer, String> list = new HashMap<>();
    HashMap<String, Integer> list2 = new HashMap<>();
    HashMap<Integer, Integer> id_last = new HashMap<>();

    ArrayAdapter<String> adapter;
//    Boolean control = false;

    ArrayList<Integer> add_margin = new ArrayList();

    LinearLayout.LayoutParams params_10 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            25);
    LinearLayout.LayoutParams params_15 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            50);
    LinearLayout.LayoutParams params_30 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            100);
    LinearLayout.LayoutParams params_45 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            150);
    LinearLayout.LayoutParams params_50 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            160);
    LinearLayout.LayoutParams params_00 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            200);


    private ServiceConnection playerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            PlayerBinder playerBinder = (PlayerBinder) service;
            Timetable2.this.playerService = playerBinder.getService();
            // boundService = boundBinder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable2);

        String dbPath = getApplicationContext().getDatabasePath(db_name).getPath();
        Log.i("my db path=", "" + dbPath);

        helper = new RecordTimeTable_Helper(this);
        int counter = helper.getCounter();
        Log.i(tag, "counter = " + counter);


        helper.search_data();
        cur = helper.getAll();
        cur.moveToFirst();

        final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        final LinearLayout timetableview = (LinearLayout) findViewById(R.id.timetable1);
        final LinearLayout timetablelist = (LinearLayout) findViewById(R.id.listtimetable1);

        final Button timetableshow = (Button) findViewById(R.id.timetableshow);
        final Button listshow = (Button) findViewById(R.id.listshow);
        timetablelist.setVisibility(View.GONE);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    control = true;
                    Log.v("aaa", "control is true");
                    timetableshow.setVisibility(View.GONE);
                    listshow.setVisibility(View.GONE);

                } else {
                    control = false;
                    Log.v("aaa", "control is false");
                    timetableshow.setVisibility(View.VISIBLE);
                    listshow.setVisibility(View.VISIBLE);
                }
            }
        });

        //시간표버튼을 누르게 되면
        timetableshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timetableview.setVisibility(View.VISIBLE);
                timetablelist.setVisibility(View.GONE);
                toggle.setVisibility(View.VISIBLE);


            }
        });
        // 리스트 버튼을 클릭하게 되면
        listshow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                timetableview.setVisibility(View.GONE);
                timetablelist.setVisibility(View.VISIBLE);
                toggle.setVisibility(View.GONE);
                //adapter.notifyDataSetChanged();

            }
        });
        //params_1.weight =(float)2; //레이아웃의 weight를 동적으로 설정 (칸의 비율)


        textViews[0] = (TextView) findViewById(R.id.id_0);
        textViews[1] = (TextView) findViewById(R.id.id_1);
        textViews[2] = (TextView) findViewById(R.id.id_2);
        textViews[3] = (TextView) findViewById(R.id.id_3);
        textViews[4] = (TextView) findViewById(R.id.id_4);
        textViews[5] = (TextView) findViewById(R.id.id_5);
        textViews[6] = (TextView) findViewById(R.id.id_6);
        textViews[7] = (TextView) findViewById(R.id.id_7);
        textViews[8] = (TextView) findViewById(R.id.id_8);
        textViews[9] = (TextView) findViewById(R.id.id_9);
        textViews[10] = (TextView) findViewById(R.id.id_10);
        textViews[11] = (TextView) findViewById(R.id.id_11);
        textViews[12] = (TextView) findViewById(R.id.id_12);
        textViews[13] = (TextView) findViewById(R.id.id_13);
        textViews[14] = (TextView) findViewById(R.id.id_14);
        textViews[15] = (TextView) findViewById(R.id.id_15);
        textViews[16] = (TextView) findViewById(R.id.id_16);
        textViews[17] = (TextView) findViewById(R.id.id_17);
        textViews[18] = (TextView) findViewById(R.id.id_18);
        textViews[19] = (TextView) findViewById(R.id.id_19);
        textViews[20] = (TextView) findViewById(R.id.id_20);
        textViews[21] = (TextView) findViewById(R.id.id_21);
        textViews[22] = (TextView) findViewById(R.id.id_22);
        textViews[23] = (TextView) findViewById(R.id.id_23);
        textViews[24] = (TextView) findViewById(R.id.id_24);
        textViews[25] = (TextView) findViewById(R.id.id_25);
        textViews[26] = (TextView) findViewById(R.id.id_26);
        textViews[27] = (TextView) findViewById(R.id.id_27);
        textViews[28] = (TextView) findViewById(R.id.id_28);
        textViews[29] = (TextView) findViewById(R.id.id_29);
        textViews[30] = (TextView) findViewById(R.id.id_30);
        textViews[31] = (TextView) findViewById(R.id.id_31);
        textViews[32] = (TextView) findViewById(R.id.id_32);
        textViews[33] = (TextView) findViewById(R.id.id_33);
        textViews[34] = (TextView) findViewById(R.id.id_34);
        textViews[35] = (TextView) findViewById(R.id.id_35);
        textViews[36] = (TextView) findViewById(R.id.id_36);
        textViews[37] = (TextView) findViewById(R.id.id_37);
        textViews[38] = (TextView) findViewById(R.id.id_38);
        textViews[39] = (TextView) findViewById(R.id.id_39);
        textViews[40] = (TextView) findViewById(R.id.id_40);
        textViews[41] = (TextView) findViewById(R.id.id_41);
        textViews[42] = (TextView) findViewById(R.id.id_42);
        textViews[43] = (TextView) findViewById(R.id.id_43);
        textViews[44] = (TextView) findViewById(R.id.id_44);
        textViews[45] = (TextView) findViewById(R.id.id_45);
        textViews[46] = (TextView) findViewById(R.id.id_46);
        textViews[47] = (TextView) findViewById(R.id.id_47);
        textViews[48] = (TextView) findViewById(R.id.id_48);
        textViews[49] = (TextView) findViewById(R.id.id_49);
        textViews[50] = (TextView) findViewById(R.id.id_50);
        textViews[51] = (TextView) findViewById(R.id.id_51);
        textViews[52] = (TextView) findViewById(R.id.id_52);
        textViews[53] = (TextView) findViewById(R.id.id_53);
        textViews[54] = (TextView) findViewById(R.id.id_54);
        textViews[55] = (TextView) findViewById(R.id.id_55);
        textViews[56] = (TextView) findViewById(R.id.id_56);
        textViews[57] = (TextView) findViewById(R.id.id_57);
        textViews[58] = (TextView) findViewById(R.id.id_58);
        textViews[59] = (TextView) findViewById(R.id.id_59);


        imageviews[0] = (ImageView) findViewById(R.id.id_0_0);
        imageviews[1] = (ImageView) findViewById(R.id.id_1_1);
        imageviews[2] = (ImageView) findViewById(R.id.id_2_2);
        imageviews[3] = (ImageView) findViewById(R.id.id_3_3);
        imageviews[4] = (ImageView) findViewById(R.id.id_4_4);
        imageviews[5] = (ImageView) findViewById(R.id.id_5_5);
        imageviews[6] = (ImageView) findViewById(R.id.id_6_6);
        imageviews[7] = (ImageView) findViewById(R.id.id_7_7);
        imageviews[8] = (ImageView) findViewById(R.id.id_8_8);
        imageviews[9] = (ImageView) findViewById(R.id.id_9_9);
        imageviews[10] = (ImageView) findViewById(R.id.id_10_10);
        imageviews[11] = (ImageView) findViewById(R.id.id_11_11);
        imageviews[12] = (ImageView) findViewById(R.id.id_12_12);
        imageviews[13] = (ImageView) findViewById(R.id.id_13_13);
        imageviews[14] = (ImageView) findViewById(R.id.id_14_14);
        imageviews[15] = (ImageView) findViewById(R.id.id_15_15);
        imageviews[16] = (ImageView) findViewById(R.id.id_16_16);
        imageviews[17] = (ImageView) findViewById(R.id.id_17_17);
        imageviews[18] = (ImageView) findViewById(R.id.id_18_18);
        imageviews[19] = (ImageView) findViewById(R.id.id_19_19);
        imageviews[20] = (ImageView) findViewById(R.id.id_20_20);
        imageviews[21] = (ImageView) findViewById(R.id.id_21_21);
        imageviews[22] = (ImageView) findViewById(R.id.id_22_22);
        imageviews[23] = (ImageView) findViewById(R.id.id_23_23);
        imageviews[24] = (ImageView) findViewById(R.id.id_24_24);
        imageviews[25] = (ImageView) findViewById(R.id.id_25_25);
        imageviews[26] = (ImageView) findViewById(R.id.id_26_26);
        imageviews[27] = (ImageView) findViewById(R.id.id_27_27);
        imageviews[28] = (ImageView) findViewById(R.id.id_28_28);
        imageviews[29] = (ImageView) findViewById(R.id.id_29_29);
        imageviews[30] = (ImageView) findViewById(R.id.id_30_30);
        imageviews[31] = (ImageView) findViewById(R.id.id_31_31);
        imageviews[32] = (ImageView) findViewById(R.id.id_32_32);
        imageviews[33] = (ImageView) findViewById(R.id.id_33_33);
        imageviews[34] = (ImageView) findViewById(R.id.id_34_34);
        imageviews[35] = (ImageView) findViewById(R.id.id_35_35);
        imageviews[36] = (ImageView) findViewById(R.id.id_36_36);
        imageviews[37] = (ImageView) findViewById(R.id.id_37_37);
        imageviews[38] = (ImageView) findViewById(R.id.id_38_38);
        imageviews[39] = (ImageView) findViewById(R.id.id_39_39);
        imageviews[40] = (ImageView) findViewById(R.id.id_40_40);
        imageviews[41] = (ImageView) findViewById(R.id.id_41_41);
        imageviews[42] = (ImageView) findViewById(R.id.id_42_42);
        imageviews[43] = (ImageView) findViewById(R.id.id_43_43);
        imageviews[44] = (ImageView) findViewById(R.id.id_44_44);
        imageviews[45] = (ImageView) findViewById(R.id.id_45_45);
        imageviews[46] = (ImageView) findViewById(R.id.id_46_46);
        imageviews[47] = (ImageView) findViewById(R.id.id_47_47);
        imageviews[48] = (ImageView) findViewById(R.id.id_48_48);
        imageviews[49] = (ImageView) findViewById(R.id.id_49_49);
        imageviews[50] = (ImageView) findViewById(R.id.id_50_50);
        imageviews[51] = (ImageView) findViewById(R.id.id_51_51);
        imageviews[52] = (ImageView) findViewById(R.id.id_52_52);
        imageviews[53] = (ImageView) findViewById(R.id.id_53_53);
        imageviews[54] = (ImageView) findViewById(R.id.id_54_54);
        imageviews[55] = (ImageView) findViewById(R.id.id_55_55);
        imageviews[56] = (ImageView) findViewById(R.id.id_56_56);
        imageviews[57] = (ImageView) findViewById(R.id.id_57_57);
        imageviews[58] = (ImageView) findViewById(R.id.id_58_58);
        imageviews[59] = (ImageView) findViewById(R.id.id_59_59);

        //imageviews[0].setLayoutParams(params_00);

//        helper.saveId(1);
//        ArrayList getId=helper.GetId();

        // Log.v("iddd", String.valueOf(getId));
        ArrayList getId = helper.GetId();
        for (int i = 0; i < textViews.length; i++) {
            textViews[i].setId(i);//data[0]  =  0
            textViews[i].setTextSize(14);
            //textViews[i].setLayoutParams(params);
            textViews[i].setOnClickListener(this);
            textViews[i].setGravity(Gravity.CENTER);
            // textViews[i].setBackgroundColor(Color.parseColor("#EAEAEA"));

            if ((cur != null) && (!cur.isAfterLast())) {
                db_id = cur.getInt(0);
                db_subject = cur.getString(1);
                db_classroom = cur.getString(2);
                db_starttime = cur.getString(3);
                db_endtime = cur.getString(4);
                db_background = cur.getInt(5);
                db_mon = cur.getString(6);
                db_tues = cur.getString(7);
                db_wednes = cur.getString(8);
                db_thus = cur.getString(9);
                db_fri = cur.getString(10);
                db_sat = cur.getString(11);
                db_sun = cur.getString(12);
                list.put(db_id, db_subject);
                list2.put(db_subject, db_id);
                if (textViews[i].getId() == db_id) {
                    //arraylist로 데이터 넘기는 코드
                    helper.update(db_id, db_subject, db_classroom, db_starttime, db_endtime,
                            db_background, db_mon, db_tues, db_wednes, db_thus, db_fri, db_sat, db_sun);
                    for (int textid = 0; textid < getId.size(); textid++) {
                        int idvalue= (Integer) (getId.get(textid));
                        if (idvalue == i) {
                            textViews[i].setText(db_subject + "\n" + db_classroom);
                        }

                    }

                    imageviews[i].setBackgroundColor(db_background);
                    // imageviews[i].setLayoutParams(params_15);
                    cur.moveToNext();


                }
            } else if (cur.isAfterLast()) {
                cur.close();
            }

        }//End of First For

        Log.v("iddd", "원래의값:" + String.valueOf(getId));

        ArrayList result_List = new ArrayList(); //결과를 담을 어레이리스트
        HashSet hs = new HashSet(list.values());
        Log.v("subject", "과목만 가지고오기" + String.valueOf(list.values()));
        Iterator it = hs.iterator();

        while (it.hasNext()) {
            result_List.add(it.next());
        }
        result_List.add("기타");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result_List);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        for (Integer lastid : list2.values()) {
            String lasttime = helper.lasttime(lastid);
            String[] lasttime2 = lasttime.split(":");
            int endhour = Integer.parseInt(lasttime2[0]);
            int endmin = Integer.parseInt(lasttime2[1]);

            if (1 <= endmin && endmin < 10) {
                imageviews[lastid].setLayoutParams(params_10);
            } else if (10 <= endmin && endmin < 18) {
                imageviews[lastid].setLayoutParams(params_15);
            } else if (18 <= endmin && endmin < 33) {
                imageviews[lastid].setLayoutParams(params_30);
            } else if (33 <= endmin && endmin < 47) {
                imageviews[lastid].setLayoutParams(params_45);
            } else if (47 <= endmin && endmin < 59) {
                imageviews[lastid].setLayoutParams(params_50);
            } else if (0 == endmin) {
                imageviews[lastid].setLayoutParams(params_00);
            }


        }

        /* 리스트뷰 클릭시 인텐트호출 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Timetable2.this, TimetableRecordlistActivity.class);
                intent.putExtra("subject", (String) listView.getItemAtPosition(position)); //과목명 전달해야하는데 못하겠음 ㅠㅠ -진히
                String subject2 = (String) listView.getItemAtPosition(position);
                playerService.setPlayList(subject2);
                startActivity(intent);
            }


        });


    }

    @Override
    public void onClick(View view) {
        Cursor cursor = null;
        cursor = helper.getAll();//테이블의 모든 데이터를 커서로 리턴.
        int get[] = new int[textViews.length];

        String time_when[] = {"09:00", "09:00", "09:00", "09:00", "09:00",
                "10:00", "10:00", "10:00", "10:00", "10:00",
                "11:00", "11:00", "11:00", "11:00", "11:00",
                "12:00", "12:00", "12:00", "12:00", "12:00",
                "13:00", "13:00", "13:00", "13:00", "13:00",
                "14:00", "14:00", "14:00", "14:00", "14:00",
                "15:00", "15:00", "15:00", "15:00", "15:00",
                "16:00", "16:00", "16:00", "16:00", "16:00",
                "17:00", "17:00", "17:00", "17:00", "17:00",
                "18:00", "18:00", "18:00", "18:00", "18:00",
                "19:00", "19:00", "19:00", "19:00", "19:00",
                "20:00", "20:00", "20:00", "20:00", "20:00"};

        if (cursor != null) {
            Log.i(tag, "cursor is not null");
            cursor.moveToFirst();
            for (int i = 0; i < textViews.length; i++) {
                get[i] = '0';//배열 초기화
                Log.v("textView", "배열 초기화      :" + String.valueOf(i));
                Log.v("textView", "값     :" + String.valueOf(textViews.length));
            }
            //커서가 데이터의 마지막일때 까지 커서가 이동할 수있도록 해준다.
            while (!cursor.isAfterLast()) {
                //정수배열의 테이블의 id값의 배열에 id값을 넣어준다.(get[3]=3)
                get[cursor.getInt(0)] = cursor.getInt(0);
                Log.v("textView", "get " + get[cursor.getInt(0)]);
                cursor.moveToNext();//커서를 이동시켜준다.
            }
            for (int i = 0; i < textViews.length; i++) {//배열의 길이만큼

                Log.v("textView", "get[i] =" + get[i] +
                        "   view.getid =" + view.getId() +
                        "   data[i].getId() =" + textViews[i].getId());
             /*데이터를 수정, 삭제 하기 위한 다이얼로그*/
                Cursor c;//해당 뷰에 데이터가 있으면 다이얼로그 텍스트창에 출력해주기 위해 커서 사용.
                c = helper.getAll();//커서에 데이터베이스 테이블의 모든 데이터를 리턴해줌.

                //시간표에서 수정 버튼을 눌렀을 경우
                if (control == true) {
                    //배열에 데이터가 있고,클릭한곳에 데이터가 있을시
                    if ((get[i] != '0') && (get[i] == view.getId())) {
                        Intent intent = new Intent(Timetable2.this, TimetableChange.class);
                        // Toast.makeText(Timetable2.this, "이미 데이터가 있을 떄 " + view.getId(), Toast.LENGTH_SHORT).show();
                        intent.putExtra("id", view.getId());
                        if (c != null) {//커서의 데이터가 있으면
                            c.moveToFirst();//커서를 테이블 제일 처음, 즉 테이블의 제 1행을 가리키도록 한다.
                            while (!c.isAfterLast()) {//커서가 데이터의 마지막일때 까지 커서가 이동할 수있도록 해준다.
                            //커서가 가리키는 곳의 제 1열, id가 저장되어있는 열의 id값과 사용자가 누른곳의 id값이 같으면,
                            //사용자가 클릭한 곳에 데이터가 있을시 실행하고 반복문 종료
                                if (c.getInt(0) == view.getId()) {
                                    //2열 3열, 강의명,강의실명을 가져와 텍스트에 보여주도록 설정
                                    intent.putExtra("subject", c.getString(1));
                                    intent.putExtra("classroom", c.getString(2));
                                    intent.putExtra("starttime", c.getString(3));
                                    intent.putExtra("endtime", c.getString(4));
                                    intent.putExtra("colorpicker", c.getInt(5));
                                    intent.putExtra("monday_boolean", c.getString(6));
                                    intent.putExtra("tuesday_boolean", c.getString(7));
                                    intent.putExtra("wednesday_boolean", c.getString(8));
                                    intent.putExtra("thursday_boolean", c.getString(9));
                                    intent.putExtra("friday_boolean", c.getString(10));
                                    startActivityForResult(intent, 2);
                                    break;
                                }
                                c.moveToNext();//커서를 다음 행으로 이동시켜주는 역할
                            }
                        }


                        //update_timetable_dig(view.getId());
                        break;
                    }
                    //배열에 데이터가 없고,클릭한곳이 데이터가 없을때
                    else if ((get[i] == '0') && (view.getId() == textViews[i].getId())) {
                        Intent intent = new Intent(Timetable2.this, TimetableInput.class);

                        intent.putExtra("id", view.getId());
                        //Toast.makeText(Timetable2.this, "처음 클릭한거" + view.getId(), Toast.LENGTH_SHORT).show();
                        for (int x = 0; x < 11; x++) {
                            if (view.getId() == 5 * x) {
                                intent.putExtra("monday_checked", "true");
                            }
                            if (view.getId() == (5 * x) + 1) {
                                intent.putExtra("tuesday_checked", "true");
                            }
                            if (view.getId() == (5 * x) + 2) {
                                intent.putExtra("wednesday_checked", "true");
                            }
                            if (view.getId() == (5 * x) + 3) {
                                intent.putExtra("thursday_checked", "true");
                            }
                            if (view.getId() == (5 * x) + 4) {
                                intent.putExtra("friday_checked", "true");
                            }
                        }

                        intent.putExtra("id", view.getId());
                        intent.putExtra("date", time_when[i].toString());
                        startActivityForResult(intent, 1);


                        break;
                    }

                }//end of true

                else if (control == false) {

                    if ((get[i] != '0') && (get[i] == view.getId())) {
                        Intent intent = new Intent(this, TimetableRecordlistActivity.class);
                        intent.putExtra("id", view.getId());

                        if (c != null) {//커서의 데이터가 있으면
                            c.moveToFirst();//커서를 테이블 제일 처음, 즉 테이블의 제 1행을 가리키도록 한다.
                            while (!c.isAfterLast()) {
                                if (c.getInt(0) == view.getId()) {
                                    intent.putExtra("subject", c.getString(1));
                                    intent.putExtra("classroom", c.getString(2));
                                    intent.putExtra("starttime", c.getString(3));
                                    intent.putExtra("endtime", c.getString(4));
                                    intent.putExtra("colorpicker", c.getInt(5));
                                    String subject = c.getString(1);
                                    playerService.setPlayList(subject);

                                    break;
                                }

                                c.moveToNext();
                            }
                            startActivityForResult(intent, 3);
                            Log.v("aaa", "data place");
                        }
                    }

                    //배열에 데이터가 없고,클릭한곳이 데이터가 없을때
                    else if ((get[i] == '0') && (view.getId() == textViews[i].getId())) {
                        Log.v("aaa", "empty place");
                        break;
                    }

                    Log.v("aaa", "click here");
//
//                       }
//                    }
                }//control is false end for
            }//End of For
        }//End of   if(cursor!=null)


        //    Toast toast =Toast.makeText(getApplicationContext(), "아이디얌 "+view.getId(), Toast.LENGTH_LONG);
        //    toast.show();

    }//onclick 마지막

    int time_when[] = {
            10, 10, 10, 10, 10,
            11, 11, 11, 11, 11,
            12, 12, 12, 12, 12,
            13, 13, 13, 13, 13,
            14, 14, 14, 14, 14,
            15, 15, 15, 15, 15,
            16, 16, 16, 16, 16,
            17, 17, 17, 17, 17,
            18, 18, 18, 18, 18,
            19, 19, 19, 19, 19,
            20, 20, 20, 20, 20,
            21, 21, 21, 21, 21

    };
    int start_when[] = {

            9, 9, 9, 9, 9,
            10, 10, 10, 10, 10,
            11, 11, 11, 11, 11,
            12, 12, 12, 12, 12,
            13, 13, 13, 13, 13,
            14, 14, 14, 14, 14,
            15, 15, 15, 15, 15,
            16, 16, 16, 16, 16,
            17, 17, 17, 17, 17,
            18, 18, 18, 18, 18,
            19, 19, 19, 19, 19,
            20, 20, 20, 20, 20

    };

    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == this.RESULT_OK)
        // 액티비티가 정상적으로 종료되었을 경우
        {
            if (requestCode == 1)
            // 데이터가 없을 경우 처음 그 칸을 눌렀을 경우
            {

                Bundle extras = data.getExtras();
                String clicked = extras.getString("clicked");
                int id = extras.getInt("id");
                String subject = extras.getString("subject");
                String classroom = extras.getString("classroom");
                int colorpicker = extras.getInt("color");
                String starttime1 = extras.getString("starttime");
                String[] starttime = starttime1.split(":");
                int starthour = Integer.parseInt(starttime[0]);
                int startmin = Integer.parseInt(starttime[1]);
                String endtime1 = extras.getString("endtime");
                String[] endtime = endtime1.split(":");
                int endhour = Integer.parseInt(endtime[0]);
                int endmin = Integer.parseInt(endtime[1]);
                int get_id = textViews[id].getId();

                String monday_boolean = extras.getString("monday_boolean");
                String tuesday_boolean = extras.getString("tuesday_boolean");
                String wednesday_boolean = extras.getString("wednesday_boolean");
                String thursday_boolean = extras.getString("thursday_boolean");
                String friday_boolean = extras.getString("friday_boolean");

                String saturday_boolean = "false";
                String sunday_boolean = "false";

                String week[] = {monday_boolean, tuesday_boolean, wednesday_boolean,
                        thursday_boolean, friday_boolean};
                ArrayList<Integer> truelist = new ArrayList();
                ArrayList<Integer> biggerlist = new ArrayList();
                ArrayList<Integer> smallerlist = new ArrayList();
                ArrayList<Integer> add_id = new ArrayList();

                int me = (get_id % 5);

                //true를 체크한 모든 값을 저장함
                for (int i = 0; i < week.length; i++) {
                    if (week[i].equals("true")) {
                        truelist.add(i);
                        Log.v("subjectid", String.valueOf(truelist));

                    }
                }

                add_id.add(get_id);

                //자기 요일의 값을 기준으로해서 작은지 큰지를 비교함
                for (int bigsmall = 0; bigsmall < truelist.size(); bigsmall++) {

                    if (truelist.get(bigsmall) > me) {

                        biggerlist.add(truelist.get(bigsmall));
                        add_id.add(((truelist.get(bigsmall) - me) + get_id));

                        Log.v("subjectid", "나보다 크면" + String.valueOf(biggerlist));


                    } else if (truelist.get(bigsmall) < me) {

                        smallerlist.add(truelist.get(bigsmall));
                        add_id.add(get_id - (me - truelist.get(bigsmall)));

                        Log.v("subjectid", "나보다 작으면" + String.valueOf(bigsmall));
                    }

                }
                Log.v("subjectid", "사용할 아이디값" + String.valueOf(add_id));

                //과목에서 제일 위에 아이디값만 저장
                for (int i = 0; i < add_id.size(); i++) {
                    helper.saveId(add_id.get(i));
                }

                ArrayList getId = helper.GetId();
                Log.v("iddd", String.valueOf(getId));


                //만약 다른 시간에도 체크를 했을 겨웅
                for (int change_id = 0; change_id < add_id.size(); change_id++) {

                    imageviews[add_id.get(change_id)].setBackgroundColor(colorpicker);
                    textViews[add_id.get(change_id)].setText("" + subject + "\n" + classroom);
                    helper.add(add_id.get(change_id), subject, classroom, starttime1, endtime1, colorpicker,
                            monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean, saturday_boolean, sunday_boolean);


                    //둘다 다를 경우 끝나는 시간과 시작 시간이 달라서 둘다 바꿔줘야하는 경우
                    if ((endhour + 1) != time_when[add_id.get(change_id)] && starthour != start_when[add_id.get(change_id)]) {
                        int j = ((endhour + 1) - time_when[add_id.get(change_id)]);
                        int w = (start_when[add_id.get(change_id)] - starthour);
                        add_margin.add(add_id.get(change_id) + (5 * j));

                        if (endmin != 0) {
                            for (int gap = 1; gap <= (j); gap++) {

                                imageviews[add_id.get(change_id) + (5 * gap)].setBackgroundColor(colorpicker);
                                helper.add(add_id.get(change_id) + (5 * gap), subject, classroom, starttime1, endtime1, colorpicker,
                                        monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean, saturday_boolean, sunday_boolean);
                            }
                            if (add_id.get(change_id) - (5 * w) > 0) {
                                for (int gap1 = 1; gap1 <= w; gap1++) {
                                    imageviews[add_id.get(change_id) - (5 * gap1)].setBackgroundColor(colorpicker);
                                    helper.add(add_id.get(change_id) - (5 * gap1), subject, classroom, starttime1, endtime1, colorpicker,
                                            monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean, saturday_boolean, sunday_boolean);
                                }
                            }

                            if (1 <= endmin && endmin < 10) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_10);
                            } else if (10 <= endmin && endmin < 18) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_15);
                            } else if (18 <= endmin && endmin < 33) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_30);
                            } else if (33 <= endmin && endmin < 47) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_45);
                            } else if (47 <= endmin && endmin < 59) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_50);
                            }
                            textViews[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                            textViews[id].setText("" + subject + "\n" + classroom);
                        } else if (endmin == 0) {
                            id_last.put(change_id, (add_id.get(change_id) + (5 * (j))));
                            for (int gap = 1; gap <= (j); gap++) {

                                imageviews[add_id.get(change_id) + (5 * gap)].setBackgroundColor(colorpicker);
                                helper.add(add_id.get(change_id) + (5 * gap), subject, classroom, starttime1, endtime1, colorpicker,
                                        monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean, saturday_boolean, sunday_boolean);
                            }
                            if (add_id.get(change_id) - (5 * w) > 0) {
                                for (int gap1 = 1; gap1 <= w; gap1++) {
                                    imageviews[add_id.get(change_id) - (5 * gap1)].setBackgroundColor(colorpicker);
                                    helper.add(add_id.get(change_id) - (5 * gap1), subject, classroom, starttime1, endtime1, colorpicker,
                                            monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean, saturday_boolean, sunday_boolean);
                                }
                            }


                            imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_00);
                            textViews[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                            textViews[id].setText("" + subject + "\n" + classroom);
                        }
                    }

                    //시작시간은 같은데 끝나는 시간이 칸과 다를 경우
                    else if ((endhour + 1) != time_when[add_id.get(change_id)] && starthour == start_when[add_id.get(change_id)]) {
                        int j = ((endhour + 1) - time_when[add_id.get(change_id)]);

                        if (endmin != 0) {
                            id_last.put(change_id, (add_id.get(change_id) + (5 * (j))));
                            for (int gap = 1; gap <= (j); gap++) {

                                imageviews[add_id.get(change_id) + (5 * gap)].setBackgroundColor(colorpicker);
                                helper.add(add_id.get(change_id) + (5 * gap), subject, classroom, starttime1, endtime1, colorpicker,
                                        monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean, saturday_boolean, sunday_boolean);
                            }

                            if (1 <= endmin && endmin < 10) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_10);
                            } else if (10 <= endmin && endmin < 18) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_15);
                            } else if (18 <= endmin && endmin < 33) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_30);
                            } else if (33 <= endmin && endmin < 47) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_45);
                            } else if (47 <= endmin && endmin < 59) {
                                imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_50);
                            }
                            textViews[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                            textViews[id].setText("" + subject + "\n" + classroom);
                        } else if (endmin == 0) {
                            id_last.put(change_id, (add_id.get(change_id) + (5 * (j))));
                            for (int gap = 1; gap <= (j); gap++) {

                                imageviews[add_id.get(change_id) + (5 * gap)].setBackgroundColor(colorpicker);
                                helper.add(add_id.get(change_id) + (5 * gap), subject, classroom, starttime1, endtime1, colorpicker,
                                        monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean, saturday_boolean, sunday_boolean);
                            }

                            imageviews[add_id.get(change_id) + (5 * (j))].setLayoutParams(params_00);
                            textViews[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                            textViews[id].setText("" + subject + "\n" + classroom);
                        }
                    }

                    //끝나는 시간은 같은데 시작시간이 다른 경우
                    else if (endhour == time_when[add_id.get(change_id)] && starthour != start_when[add_id.get(change_id)]) {
                        int w = (start_when[add_id.get(change_id)] - starthour);
                        if (add_id.get(change_id) - (5 * w) > 0) {
                            for (int gap1 = 1; gap1 <= w; gap1++) {
                                imageviews[add_id.get(change_id) - (5 * gap1)].setBackgroundColor(colorpicker);
                                helper.add(add_id.get(change_id) - (5 * gap1), subject, classroom, starttime1, endtime1, colorpicker,
                                        monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean, saturday_boolean, sunday_boolean);
                            }
                        }

                        textViews[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                        textViews[id].setText("" + subject + "\n" + classroom);
                    }
                    //그냥 같은 경우
                    else {
                        imageviews[add_id.get(change_id)].setBackgroundColor(colorpicker);
                        if (1 <= endmin && endmin < 10) {
                            imageviews[id].setLayoutParams(params_10);
                        } else if (10 <= endmin && endmin < 18) {
                            imageviews[id].setLayoutParams(params_15);
                        } else if (18 <= endmin && endmin < 33) {
                            imageviews[id].setLayoutParams(params_30);
                        } else if (33 <= endmin && endmin < 47) {
                            imageviews[id].setLayoutParams(params_45);
                        } else if (47 <= endmin && endmin < 59) {
                            imageviews[id].setLayoutParams(params_50);
                        } else if (0 == endmin) {
                            imageviews[id].setLayoutParams(params_00);
                        }

                        textViews[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                        helper.add(add_id.get(change_id), subject, classroom, starttime1, endtime1, colorpicker,
                                monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean, saturday_boolean, sunday_boolean);
                    }

                    imageviews[add_id.get(change_id)].setBackgroundColor(colorpicker);
                    textViews[id].setText("" + subject + "\n" + classroom);

                }


                //입력된 값을 리스트에 추가하기 위해서 하는 작업
                // arrayList.add(subject);
                list.put(id, subject);
                // HashSet 데이터 형태로 생성되면서 중복 제거됨
                HashSet hs = new HashSet(list.values());
                // ArrayList 형태로 다시 생성
                Log.v("subject", "추가된 거 과목만 가지고오기1  " + String.valueOf(list.values()));

                ArrayList result_List = new ArrayList();
                Iterator it = hs.iterator();
                while (it.hasNext()) {
                    result_List.add(it.next());
                }
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result_List);
                listView.setAdapter(adapter);

                Set<Map.Entry<Integer, Integer>> set = id_last.entrySet();
                Iterator<Map.Entry<Integer, Integer>> it2 = set.iterator();

                // HashMap에 포함된 key, value 값을 호출 한다.
                while (it2.hasNext()) {
                    Map.Entry<Integer, Integer> e = (Map.Entry<Integer, Integer>) it2.next();
                    Log.v("subjectid", "맨마지막" + "이름 : " + e.getKey() + ", 점수 : " + e.getValue());
                }


            } //1일때

            else if (requestCode == 2) {

                Bundle extras = data.getExtras();
                int id = extras.getInt("id");
                String clicked = extras.getString("clicked");
                String subject = extras.getString("subject");
                String classroom = extras.getString("classroom");
                String starttimechange = extras.getString("starttime");
                String endtimechange = extras.getString("endtime");
                int colorpicker = extras.getInt("colorpicker");
                int get_id = textViews[id].getId();

                String monday_check = extras.getString("monday_boolean");
                String tuesday_check = extras.getString("tuesday_boolean");
                String wednesday_check = extras.getString("wednesday_boolean");
                String thursday_check = extras.getString("thursday_boolean");
                String friday_check = extras.getString("friday_boolean");

                String saturday_boolean = "false";
                String sunday_boolean = "false";

                textViews[id].setBackgroundColor(colorpicker);
                textViews[id].setText("" + subject + "\n" + classroom);

                //저장을 눌렀을 경우
                if (clicked.equals("true")) {
                    textViews[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                    helper.update(get_id, subject, classroom, starttimechange, endtimechange, colorpicker,
                            monday_check, tuesday_check, wednesday_check, thursday_check, friday_check, saturday_boolean, sunday_boolean);
                    imageviews[id].setBackgroundColor(colorpicker);
                    textViews[id].setText("" + subject + " \n " + classroom);

                }
                //삭제를 눌렀을 경우
                else if (clicked.equals("false")) {
                    //helper.delete(id);
                    String subjectName = helper.subjectName(get_id);
                    Log.v("subject", "id값이 뭐지 :" + get_id);
                    Log.v("subject", "삭제할 과목명 :" + subjectName);
                    ArrayList<Integer> Idlist = helper.AllsubjectId(subjectName);

                    for (int l : Idlist) {
                        Log.v("subject", "내가 삭제할 l값:" + l);
                        Log.v("subject", "내가 삭제할 l값의 과목명 :" + helper.subjectName(l));
                        helper.delete(l);
                        textViews[l].setText(null);
                        textViews[l].setBackgroundColor(Color.parseColor("#EAEAEA"));
                        imageviews[l].setBackgroundColor(Color.parseColor("#EAEAEA"));
                        //list.put(id,subject);
                        list.remove(l);


                    }


                    HashSet hs = new HashSet(list.values());
                    // ArrayList 형태로 다시 생성
                    Log.v("subject", "추가된 거 과목만 가지고오기" + String.valueOf(list.values()));

                    ArrayList result_List = new ArrayList();
                    Iterator it = hs.iterator();
                    while (it.hasNext()) {
                        result_List.add(it.next());
                    }
                    result_List.add("기타");
                    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result_List);
                    listView.setAdapter(adapter);


                    textViews[id].setText(null);
                    textViews[id].setBackgroundColor(Color.parseColor("#EAEAEA"));
                }
            } else if (requestCode == 3) {
                Bundle extras = data.getExtras();

            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        getApplicationContext().bindService(playerServiceIntent, playerServiceConnection, Context.BIND_AUTO_CREATE);
        //Toast.makeText(Timetable.this(), mBound, Toast.LENGTH_SHORT).show();
        if (mBound) {
            //  Toast.makeText(Timetable2.this, "시발", Toast.LENGTH_SHORT).show();//까페
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            getApplicationContext().unbindService(playerServiceConnection);
            mBound = false;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}