package com.fourmob.colorpicker.sample;

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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.fourmob.colorpicker.sample.PlayerService.PlayerBinder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


public class Timetable extends Activity implements OnClickListener {

    private PlayerService playerService;
    private boolean mBound = false;
    private final String tag = "timetable.class";

    //DatabaseFile의 경로를 가져오기위한 변수
    private String db_name = "timetable.db";

    //Database를 생성 관리하는 클래스
    private RecordTimeTable_Helper helper;

    SQLiteDatabase db;
    Cursor cur;
    //리스트로 보기 변수

    ListView listView;
  //  ArrayList<String> arrayList = new ArrayList<String>(); //중복검사대상 어레이리스트
    HashMap<Integer,String> list = new HashMap<>();

    ArrayAdapter<String> adapter;
    //Button btnAdd;


    LinearLayout lay_time;

    String time_line[] = {"9", "10", "11", "12", "13",
            "14", "15", "16", "17", "18"};


    String day_line[] = {"시간", "월", "화", "수", "목", "금"};

    TextView time[] = new TextView[time_line.length];
    TextView day[] = new TextView[day_line.length];
    TextView inputdata[] = new TextView[time_line.length * day_line.length];


    int db_id, db_background;
    String db_classroom, db_subject, db_starttime, db_endtime, db_mon, db_tues, db_wednes, db_thus, db_fri,db_sat,db_sun;

    Boolean control = false;


    private ServiceConnection playerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            PlayerBinder playerBinder = (PlayerBinder)service;
            Timetable.this.playerService = playerBinder.getService();
            // boundService = boundBinder.getService();
            mBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound=false;
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable);


        //
        final LinearLayout timetableview = (LinearLayout) findViewById(R.id.timetable);
        final LinearLayout timetablelist = (LinearLayout)findViewById(R.id.listtimetable);

        final Button timetableshow = (Button) findViewById(R.id.timetableshow);
        final Button listshow = (Button) findViewById(R.id.listshow);

        final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        timetablelist.setVisibility(View.GONE);


        //db파일이 저장된 곳의 위치를 읽어와서 String변수 dbPath에 넣어준다.
        String dbPath = this.getApplicationContext().getDatabasePath(db_name).getPath();
        Log.i("my db path=", "" + dbPath);

        //DataBase 관리 클래스의 객체 생성을 해줌으로써 timetable.db파일과 schedule 테이블 생성
        helper = new RecordTimeTable_Helper(this);
        int counter = helper.getCounter();
        Log.i(tag, "counter = " + counter);

        //현재 들어있는 데이터를 log창으로 확인함
        //Helper클래스에 search함수에 가보면 자세한 설명있음.


        helper.search_data();




        //요일의 레이아웃을 어떻게 그릴 지 설정
        @SuppressWarnings("deprecation")
        LayoutParams params_2 = new LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);

        params_2.weight = 1; //레이아웃의 weight를 동적으로 설정 (칸의 비율)
        params_2.width = getLcdSizeWidth() / 6;
        params_2.height = getLcdSizeHeight() / 20;
        params_2.setMargins(1, 1, 1, 1);

        //시간의 레이아웃을 어떻게 그릴지 설정
        @SuppressWarnings("deprecation")
        LayoutParams params_1 = new LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        params_1.weight = 1; //레이아웃의 weight를 동적으로 설정 (칸의 비율)
        params_1.width = getLcdSizeWidth() / 6;
        params_1.height = getLcdSizeHeight() / 14;
        params_1.setMargins(1, 1, 1, 1);
        params_1.gravity = 1; //표가 뒤틀리는 것을 방지

        //레이아웃 배열로 선언
        lay_time = (LinearLayout) findViewById(R.id.lay_time);
        final LinearLayout lay[] = new LinearLayout[10];
        lay[0] = (LinearLayout) findViewById(R.id.lay_0);
        lay[1] = (LinearLayout) findViewById(R.id.lay_1);
        lay[2] = (LinearLayout) findViewById(R.id.lay_2);
        lay[3] = (LinearLayout) findViewById(R.id.lay_3);
        lay[4] = (LinearLayout) findViewById(R.id.lay_4);
        lay[5] = (LinearLayout) findViewById(R.id.lay_5);
        lay[6] = (LinearLayout) findViewById(R.id.lay_6);
        lay[7] = (LinearLayout) findViewById(R.id.lay_7);
        lay[8] = (LinearLayout) findViewById(R.id.lay_8);
        lay[9] = (LinearLayout) findViewById(R.id.lay_9);

        //요일 생성
        for (int i = 0; i < day.length; i++) {
            day[i] = new TextView(this);
            day[i].setText(day_line[i]);//텍스트에 보여줄 내용
            day[i].setGravity(Gravity.CENTER);//정렬
            day[i].setBackgroundColor(Color.parseColor("#E8EAEF"));//배경색
            day[i].setTextSize(10);//글자크기
            lay_time.addView(day[i], params_2);//레이아웃에 출력
        }

        //교시 생성
        for (int i = 0; i < time.length; i++)

        {
            time[i] = new TextView(this);
            time[i].setText(time_line[i]);
            time[i].setGravity(Gravity.CENTER_HORIZONTAL);
            time[i].setBackgroundColor(Color.parseColor("#EAEAEA"));
            time[i].setTextSize(10);
            lay[i].addView(time[i], params_1);
        }


        cur = helper.getAll();
        cur.moveToFirst();
        // 토글 버튼으로 시간표와 리스트로 보여주
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
        timetableshow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                timetableview.setVisibility(View.VISIBLE);
                timetablelist.setVisibility(View.GONE);
                toggle.setVisibility(View.VISIBLE);


            }
        });


        // 리스트 버튼을 클릭하게 되면
        listshow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                timetableview.setVisibility(View.GONE);
                timetablelist.setVisibility(View.VISIBLE);
                toggle.setVisibility(View.GONE);
                //adapter.notifyDataSetChanged();

            }
        });




        // data값 생성
        for (int i = 0, id = 0; i < lay.length; i++) { //10개 id 바꾸기
            for (int j = 1; j < day_line.length; j++) { //6개
                inputdata[id] = new TextView(this);

                inputdata[id].setId(id);//data[0]  =  0
                //inputdata[id].setText(time_when[i]); // 시간 9:00- 18:00
                inputdata[id].setOnClickListener(this);
                inputdata[id].setGravity(Gravity.CENTER);
                inputdata[id].setBackgroundColor(Color.parseColor("#EAEAEA"));

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
                    //arrayList.add(db_subject);
                    list.put(db_id,db_subject);
                    if (inputdata[id].getId() == db_id) {
                        //timetable에 보여주는 과목이다.
                        inputdata[id].setText(db_subject + "\n" + db_classroom);
                        inputdata[id].setBackgroundColor(db_background);
                        //arraylist로 데이터 넘기는 코드
                        helper.update(db_id, db_subject, db_classroom, db_starttime, db_endtime,
                                db_background, db_mon, db_tues, db_wednes, db_thus, db_fri,db_sat,db_sun);
                        cur.moveToNext();
                    }
                } else if (cur.isAfterLast()) {
                    cur.close();
                }
                lay[i].addView(inputdata[id], params_1); //시간표 데이터 출력


                id++;

            }//End of Second For
        }//End of First For


        ArrayList result_List = new ArrayList(); //결과를 담을 어레이리스트
        HashSet hs = new HashSet(list.values());
        Log.v("subject", "과목만 가지고오기"+String.valueOf(list.values()));
        Iterator it = hs.iterator();
        while (it.hasNext()) {
            result_List.add(it.next());
        }


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result_List);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);



        /* 리스트뷰 클릭시 인텐트호출 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Timetable.this, TimetableRecordlistActivity.class);
                intent.putExtra("subject", (String) listView.getItemAtPosition(position)); //과목명 전달해야하는데 못하겠음 ㅠㅠ -진히
                startActivity(intent);
            }


        });

        //리스트
       Log.v("subject", String.valueOf(list));



    }//End of OnCreate Method

    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }


    @Override
    public void onClick(View view) {
        Cursor cursor = null;
        cursor = helper.getAll();//테이블의 모든 데이터를 커서로 리턴.
        int get[] = new int[50];
        //각 행의 시간을 부여하기 위한 배열
        String time_when[] = {"09:00", "09:00", "09:00", "09:00", "09:00",
                "10:00", "10:00", "10:00", "10:00", "10:00",
                "11:00", "11:00", "11:00", "11:00", "11:00",
                "12:00", "12:00", "12:00", "12:00", "12:00",
                "13:00", "13:00", "13:00", "13:00", "13:00",
                "14:00", "14:00", "14:00", "14:00", "14:00",
                "15:00", "15:00", "15:00", "15:00", "15:00",
                "16:00", "16:00", "16:00", "16:00", "16:00",
                "17:00", "17:00", "17:00", "17:00", "17:00",
                "18:00", "18:00", "18:00", "18:00", "18:00"};


        if (cursor != null) {
            Log.i(tag, "cursor is not null");
            cursor.moveToFirst();
            for (int i = 0; i < 50; i++) {
                get[i] = '0';//배열 초기화
            }
            //커서가 데이터의 마지막일때 까지 커서가 이동할 수있도록 해준다.
            while (!cursor.isAfterLast()) {
                //정수배열의 테이블의 id값의 배열에 id값을 넣어준다.(get[3]=3)
                get[cursor.getInt(0)] = cursor.getInt(0);
                Log.i(tag, "get " + get[cursor.getInt(0)]);
                cursor.moveToNext();//커서를 이동시켜준다.
            }
            for (int i = 0; i < 50; i++) {//배열의 길이만큼

                Log.i(tag, "get[i] =" + get[i] +
                        "   view.getid =" + view.getId() +
                        "   data[i].getId() =" + inputdata[i].getId());

                Cursor c;//해당 뷰에 데이터가 있으면 다이얼로그 텍스트창에 출력해주기 위해 커서 사용.
                c = helper.getAll();//커서에 데이터베이스 테이블의 모든 데이터를 리턴해줌.

                if (control == true) {
                    //배열에 데이터가 있고,클릭한곳에 데이터가 있을시
                    if ((get[i] != '0') && (get[i] == view.getId())) {
                        Intent intent = new Intent(this, TimetableChange.class);
                        intent.putExtra("id", view.getId());
                  /*데이터를 수정, 삭제 하기 위한 다이얼로그*/

                        //  Toast.makeText(Timetable.this, "selectedColor : " + , Toast.LENGTH_SHORT).show();

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

                                    // Toast.makeText(getActivity(), "moday? : " + c.getString(6), Toast.LENGTH_LONG).show();
                                    startActivityForResult(intent, 2);
                                    //  startActivityForResult(intent2, 3);
                                    break;
                                }
                                c.moveToNext();//커서를 다음 행으로 이동시켜주는 역할
                            }
                        }


                        //update_timetable_dig(view.getId());
                        break;
                    }

                    //배열에 데이터가 없고,클릭한곳이 데이터가 없을때
                    else if ((get[i] == '0') && (view.getId() == inputdata[i].getId())) {

                        Intent intent = new Intent(this, TimetableInput.class);
                        //선택된 곳에 대한 요일을 checkbox에 넣기 위한 for문
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

                        // add_timetable_dig(view.getId(), time_when[i].toString());//해당 다이얼로그를 불러줌


                        break;
                    }

                }  //end of if control true
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

                                    Toast.makeText(Timetable.this,subject,Toast.LENGTH_SHORT).show(); //까페

//                                    String str = songManager.getPath();
//                                    Toast.makeText(Timetable.this, str, Toast.LENGTH_SHORT).show();//이닛

                                    playerService.setPlayList(subject);
                                    Toast.makeText(Timetable.this,subject,Toast.LENGTH_SHORT).show();//까페

                                    // String str = playerService.getPath();
                                    //  Toast.makeText(Timetable.this, str, Toast.LENGTH_SHORT).show();//까페




                                    break;
                                }

                                c.moveToNext();
                            }
                            startActivityForResult(intent, 3);
                            Log.v("aaa", "data place");
                        }
                    }

                    //배열에 데이터가 없고,클릭한곳이 데이터가 없을때
                    else if ((get[i] == '0') && (view.getId() == inputdata[i].getId())) {
                        Log.v("aaa", "empty place");
                        break;
                    }

                    Log.v("aaa", "click here");
//
//                       }
//                    }
                }//control is false end for

            } // End of For 50
        } //End of   if(cursor!=null)


    }//End of OnClick

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
            19, 19, 19, 19, 19

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
            19, 19, 19, 19, 19

    };


    @Override
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
                String endtime1 = extras.getString("endtime");
                String[] endtime = endtime1.split(":");
                int endhour = Integer.parseInt(endtime[0]);
                int get_id = inputdata[id].getId();

                String monday_boolean = extras.getString("monday_boolean");
                String tuesday_boolean = extras.getString("tuesday_boolean");
                String wednesday_boolean = extras.getString("wednesday_boolean");
                String thursday_boolean = extras.getString("thursday_boolean");
                String friday_boolean = extras.getString("friday_boolean");

                String saturday_boolean= "false";
                String sunday_boolean= "false";

                //저장을 눌렀을 경우

                if (clicked.equals("true")) {
                    String week[] = {monday_boolean, tuesday_boolean, wednesday_boolean,
                            thursday_boolean, friday_boolean};
                    //만약 다른 시간에도 체크를 했을 겨웅
                    if (week[(get_id % 5)].equals("true")) {

                        inputdata[get_id].setBackgroundColor(colorpicker);
                        inputdata[get_id].setText("" + subject + "\n" + classroom);
                        helper.add(get_id, subject, classroom, starttime1, endtime1, colorpicker,
                                monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean,saturday_boolean,sunday_boolean);


                        //둘다 다를 경우 끝나는 시간과 시작 시간이 달라서 둘다 바꿔줘야하는 경우
                        if (endhour != time_when[get_id] && starthour != start_when[get_id]) {
                            int j = (endhour - time_when[get_id]);
                            int w = (start_when[get_id] - starthour);

                            for (int gap = 1; gap <= j; gap++) {
                                inputdata[get_id + (5 * gap)].setBackgroundColor(colorpicker);
                                helper.add(get_id + (5 * gap), subject, classroom, starttime1, endtime1, colorpicker,
                                        monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean,saturday_boolean,sunday_boolean);
                            }
                            if (get_id - (5 * w) > 0) {
                                for (int gap1 = 1; gap1 <= w; gap1++) {
                                    inputdata[get_id - (5 * gap1)].setBackgroundColor(colorpicker);
                                    helper.add(get_id - (5 * gap1), subject, classroom, starttime1, endtime1, colorpicker,
                                            monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean,saturday_boolean,sunday_boolean);
                                }
                            }

                            inputdata[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                            inputdata[id].setText("" + subject + "\n" + classroom);
                        }

                        //시작시간은 같은데 끝나는 시간이 칸과 다를 경우
                        else if (endhour != time_when[get_id] && starthour == start_when[get_id]) {
                            int j = (endhour - time_when[get_id]);
                            for (int gap = 1; gap <= j; gap++) {
                                inputdata[get_id + (5 * gap)].setBackgroundColor(colorpicker);
                                helper.add(get_id + (5 * gap), subject, classroom, starttime1, endtime1, colorpicker,
                                        monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean,saturday_boolean,sunday_boolean);
                            }

                            inputdata[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                            inputdata[id].setText("" + subject + "\n" + classroom);
                        }
                        //끝나는 시간은 같은데 시작시간이 다른 경우
                        else if (endhour == time_when[get_id] && starthour != start_when[get_id]) {
                            int w = (start_when[get_id] - starthour);
                            if (get_id - (5 * w) > 0) {
                                for (int gap1 = 1; gap1 <= w; gap1++) {
                                    inputdata[get_id - (5 * gap1)].setBackgroundColor(colorpicker);
                                    helper.add(get_id - (5 * gap1), subject, classroom, starttime1, endtime1, colorpicker,
                                            monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean,saturday_boolean,sunday_boolean);
                                }
                            }

                            inputdata[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                            inputdata[id].setText("" + subject + "\n" + classroom);
                        }
                        //그냥 같은 경우
                        else {
                            inputdata[get_id].setBackgroundColor(colorpicker);
                            inputdata[get_id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                            helper.add(get_id, subject, classroom, starttime1, endtime1, colorpicker,
                                    monday_boolean, tuesday_boolean, wednesday_boolean, thursday_boolean, friday_boolean,saturday_boolean,sunday_boolean);
                        }



                    }
                    inputdata[id].setBackgroundColor(colorpicker);
                    inputdata[id].setText("" + subject + "\n" + classroom);


                    //입력된 값을 리스트에 추가하기 위해서 하는 작업
                   // arrayList.add(subject);
                    list.put(id,subject);
                    // HashSet 데이터 형태로 생성되면서 중복 제거됨
                    HashSet hs = new HashSet(list.values());
                    // ArrayList 형태로 다시 생성
                    Log.v("subject", "추가된 거 과목만 가지고오기1  "+String.valueOf(list.values()));

                    ArrayList result_List = new ArrayList();
                    Iterator it = hs.iterator();
                    while (it.hasNext()) {
                        result_List.add(it.next());
                    }
                    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result_List);
                    listView.setAdapter(adapter);

                }

            }
            //데이터가 있는 곳에서 수정을 할 경우
            else if (requestCode == 2) {

                Bundle extras = data.getExtras();
                int id = extras.getInt("id");
                String clicked = extras.getString("clicked");
                String subject = extras.getString("subject");
                String classroom = extras.getString("classroom");
                String starttimechange = extras.getString("starttime");
                String endtimechange = extras.getString("endtime");
                int colorpicker = extras.getInt("colorpicker");
                int get_id = inputdata[id].getId();

                String monday_check = extras.getString("monday_boolean");
                String tuesday_check = extras.getString("tuesday_boolean");
                String wednesday_check = extras.getString("wednesday_boolean");
                String thursday_check = extras.getString("thursday_boolean");
                String friday_check = extras.getString("friday_boolean");

                String saturday_boolean= "false";
                String sunday_boolean= "false";

                inputdata[id].setBackgroundColor(colorpicker);
                inputdata[id].setText("" + subject + "\n" + classroom);

                //저장을 눌렀을 경우
                if (clicked.equals("true")) {
                    inputdata[id].setText(data.getStringExtra("" + subject + "\n" + classroom));
                    helper.update(get_id, subject, classroom, starttimechange, endtimechange, colorpicker,
                            monday_check, tuesday_check, wednesday_check, thursday_check, friday_check,saturday_boolean,sunday_boolean);
                    inputdata[id].setBackgroundColor(colorpicker);
                    inputdata[id].setText("" + subject + "\n" + classroom);

                }
                //삭제를 눌렀을 경우
                else if (clicked.equals("false")) {
                    //helper.delete(id);
                    String subjectName = helper.subjectName(get_id);
                    Log.v("subject","id값이 뭐지 :"+get_id);
                    Log.v("subject","삭제할 과목명 :"+subjectName);
                    ArrayList<Integer> Idlist =helper.AllsubjectId(subjectName);

                    for(int l:Idlist){
                        Log.v("subject", "내가 삭제할 l값:" + l);
                        Log.v("subject", "내가 삭제할 l값의 과목명 :" + helper.subjectName(l));
                        helper.delete(l);
                        inputdata[l].setText(null);
                        inputdata[l].setBackgroundColor(Color.parseColor("#EAEAEA"));
                        //list.put(id,subject);
                        list.remove(l);


                    }


                    HashSet hs = new HashSet(list.values());
                    // ArrayList 형태로 다시 생성
                    Log.v("subject", "추가된 거 과목만 가지고오기"+String.valueOf(list.values()));

                    ArrayList result_List = new ArrayList();
                    Iterator it = hs.iterator();
                    while (it.hasNext()) {
                        result_List.add(it.next());
                    }
                    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result_List);
                    listView.setAdapter(adapter);


                    inputdata[id].setText(null);
                    inputdata[id].setBackgroundColor(Color.parseColor("#EAEAEA"));
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
        if(mBound){
            Toast.makeText(Timetable.this, "시발", Toast.LENGTH_SHORT).show();//까페
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound){
            getApplicationContext().unbindService(playerServiceConnection);
            mBound=false;
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




    /*정렬해주기 위해 디스플레이의 가로 세로 정보를 리턴해주는 함수*/
    @SuppressWarnings("deprecation")
    public int getLcdSizeWidth() {
// TODO Auto-generated method stub
        return ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }//End of getLcdSizeWidth Method

    @SuppressWarnings("deprecation")
    public int getLcdSizeHeight() {
// TODO Auto-generated method stub
        return ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }//End of getLcdSizeHeight Method



}
