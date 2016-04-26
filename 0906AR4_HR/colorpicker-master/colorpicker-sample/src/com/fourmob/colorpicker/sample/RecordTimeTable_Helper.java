package com.fourmob.colorpicker.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class RecordTimeTable_Helper extends SQLiteOpenHelper {

    //private final String tag = "DB_helper.java";
    private final static String db_name = "recordTimeTable.db";
    private final String db_table_name = "schedule";
    private final String db_table_name2 = "memoTABLE";
    private final String db_table_name3 = "fileTABLE";
    private final String db_table_name4 = "keywordTABLE";
    private final String db_table_name5 = "classdateTABLE";
    private final String db_table_name6 = "timeMachineTABLE";
    private final String db_table_name7 = "autoRecordTABLE";
    private final String db_table_name8 = "idTABLE";


    SQLiteDatabase db;
    static String result;
    static Time nowTime = new Time(System.currentTimeMillis());
    static Time startTime = new Time(System.currentTimeMillis());
    static Time endTime = new Time(System.currentTimeMillis());

    public RecordTimeTable_Helper(Context context) {
        super(context, db_name, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sql = "create table if not exists " + db_table_name + "("
                + " _id integer PRIMARY KEY ,"
                + " subject text, "
                + " classroom text,"
                + " startime  TIME,"
                + " endtime  TIME ,"
                + " color  int,"
                + " monday text ,"
                + " tuesday text ,"
                + " wednesday text ,"
                + " thursday text ,"
                + " friday text ,"
                + " saturday text ,"
                + " sunday text )";
        db.execSQL(sql);

        Log.v("AhReum", "Create fileTable");
        sql = "create table fileTABLE ("
                + " file_id integer PRIMARY KEY autoincrement,"
                + " subject_fk integer,"
                + " fileName text,"
                + " fileDirectory text,"
                + " what_week integer,"
                + " day_of_week integer,"
                + " fileOrder integer,"
                + " FOREIGN KEY(subject_fk) REFERENCES schedule(_id) );";
        db.execSQL(sql);

        sql = "create table memoTABLE ("
                + " memo_id integer PRIMARY KEY autoincrement,"
                + " file_fk integer,"
                + " memoTime integer,"
                + " memo text,"
                + " fileName text,"
                + " FOREIGN KEY(file_fk) REFERENCES fileTABLE(file_id) );";
        db.execSQL(sql);

        //"keywordTABLE";
        Log.i("AhReum", "Create " + db_table_name4);
        sql = "create table if not exists " + db_table_name4 + "("
                + " keyword_id integer PRIMARY KEY,"
                + " keyword_one text,"
                + " keyword_two text,"
                + " keyword_three text );";
        Log.i("AhReum", sql);
        db.execSQL(sql);

        //"classdateTABLE";
        Log.i("AhReum", "Create " + db_table_name5);
        sql = "create table if not exists " + db_table_name5 + "("
                + " classdate_id integer PRIMARY KEY,"
                + " classOpen text,"
                + " classClose text);";
        db.execSQL(sql);

        //"timeMachineTABLE";
        Log.i("AhReum", "Create " + db_table_name6);
        sql = "create table if not exists " + db_table_name6 + "("
                + " timeMachine_id integer PRIMARY KEY,"
                + " timeMachine integer);";
        db.execSQL(sql);

        //"autoRecordTABLE";
        Log.i("AhReum", "Create " + db_table_name7);
        sql = "create table if not exists " + db_table_name7 + "("
                + " autoRecord_id integer PRIMARY KEY,"
                + " autoRecord text);";
        db.execSQL(sql);

        //
        Log.i("AhReum", "Create " + db_table_name8);
        sql = "create table if not exists " + db_table_name8 + "("
                + " id_id integer PRIMARY KEY,"
                + " saveid integer);";
        db.execSQL(sql);


        //=====================================기본값설정===========================================

        int _id = 1;

        // keywordTABLE 기본값 설정 "2015-09-02", "2015-12-12");
        String keyword1 = "중요";
        String keyword2 = "시험에나옴";
        String keyword3 = "과제설명";
        String sql4 = "insert into keywordTABLE(keyword_id, keyword_one, keyword_two, keyword_three) values ('" + _id + "','" + keyword1 + "', '" + keyword2 + "', '" + keyword3 + "');";
        db.execSQL(sql4);
        Log.i("AhReum", sql4);

        // classdateTABLE 기본값 설정 "2015-09-02", "2015-12-12");
        keyword1 = "2015-09-02";
        keyword2 = "2015-12-12";
        sql4 = "insert into classdateTABLE(classdate_id, classOpen, classClose) values ('" + _id + "','" + keyword1 + "', '" + keyword2 + "');";
        db.execSQL(sql4);
        Log.i("AhReum", sql4);

        // timeMachineTABLE 기본값 설정 - 타임머신 "0초");
        int timeMachine = 0;
        sql4 = "insert into timeMachineTABLE(timeMachine_id, timeMachine) values ('1', '" + timeMachine + "');";
        db.execSQL(sql4);
        Log.i("AhReum", sql4);

        // autoRecordTABLE 기본값 설정 - 자동녹음 "true");
        keyword1 = "true";
        sql4 = "insert into autoRecordTABLE(autoRecord_id, autoRecord) values ('" + _id + "', '" + keyword1 + "');";
        db.execSQL(sql4);
        Log.i("AhReum", sql4);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS" + db_table_name);
        db.execSQL("DROP TABLE IF EXISTS" + db_table_name2);
        db.execSQL("DROP TABLE IF EXISTS" + db_table_name3);
        db.execSQL("DROP TABLE IF EXISTS" + db_table_name4);
        db.execSQL("DROP TABLE IF EXISTS" + db_table_name5);
        db.execSQL("DROP TABLE IF EXISTS" + db_table_name8);
        onCreate(db);
    }

    //DB에 데이터 더하기
    //id + 과목 + 강의 실명   --> 여기에 시간까지 더하기
    public void add(int id, String a, String b, String c, String d, int e,
                    String mon, String tues, String wednes, String thurs, String fri, String sat, String sun) {
        ContentValues val = new ContentValues();
        val.put("_id", id);
        val.put("subject", a);
        val.put("classroom", b);
        val.put("startime", c);
        val.put("endtime", d);
        val.put("color", e);
        val.put("monday", mon);
        val.put("tuesday", tues);
        val.put("wednesday", wednes);
        val.put("thursday", thurs);
        val.put("friday", fri);
        val.put("saturday", sat);
        val.put("sunday", sun);
        db.insert(db_table_name, null, val);
        search_data();
    }


    //DB 업데이트 하기??
    // db값이 변경되었을 때 다시 불러오는 함수
    public void update(long rawId, String a, String b, String c, String d, int e,
                       String mon, String tues, String wednes, String thurs, String fri, String sat, String sun) {
        ContentValues val = new ContentValues();
        val.put("_id", rawId);
        val.put("subject", a);
        val.put("classroom", b);
        val.put("startime", c);
        val.put("endtime", d);
        val.put("color", e);
        val.put("monday", mon);
        val.put("tuesday", tues);
        val.put("wednesday", wednes);
        val.put("thursday", thurs);
        val.put("friday", fri);
        val.put("saturday", sat);
        val.put("sunday", sun);
        db.update(db_table_name, val, "_id = " + rawId, null);
        search_data();
    }

    //DB에서의 삭제
    public void delete(long rawId) {
        //DB에서 아이디를 삭제하는것 같음
        db.delete(db_table_name, "_id = " + rawId, null);
        search_data();

    }

    public void search_data() {
        String sql = "select * from " + db_table_name;
        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        //마지막 레코드인지 확인
        while (!cur.isAfterLast()) {
            //cursor.getInt(or getString)(ȣ);
            String subject = cur.getString(1);
            String classroom = cur.getString(2);
            String startime = cur.getString(3);
            String endtime = cur.getString(4);
            result = (subject + "   " + classroom);
            //Log.i(tag, result);
            cur.moveToNext();
        }
        cur.close();
    }


    //DB에 있는 모든 것 가지고 오기
    public Cursor getAll() {

        return db.query(db_table_name, null, null, null, null, null, null, null);

    }

    // db에 있는 아이디 가지고 오기
    public Cursor getId(int id) {
        Cursor cur = db.query(db_table_name, null, "_id = " + id, null, null, null, null, null);
        if (cur != null && cur.getCount() != 0)
            cur.moveToNext();
        return cur;

    }

    public int getCounter() {
        Cursor cur = null;
        String sql = "select * from " + db_table_name;
        //rawQuery 메소드를 사용해 SELECT 구문을 직접 실행
        cur = db.rawQuery(sql, null);
        int counter = 0;

        while (!cur.isAfterLast()) {
            cur.moveToNext();
            counter++;
        }
        return counter;
    }

    public void insertMemo(int memoTime, String memo, String fileName) {
        ContentValues val = new ContentValues();
        val.put("file_fk", whatFileId());
        val.put("memoTime", memoTime);
        val.put("memo", memo);
        val.put("fileName", fileName);
        db.insert("memoTABLE", null, val);
        //Log.v("AhReum", "whatFileId() : " + whatFileId());
    }

    public void insertFile(String fileName, String fileDirectory, int whatWeek, int dayOfWeek, int fileOrder) {
        ContentValues val = new ContentValues();
        val.put("subject_fk", whatSubjectId());
        val.put("fileName", fileName);
        val.put("fileDirectory", fileDirectory);
        val.put("what_week", whatWeek);
        val.put("day_of_week", dayOfWeek);
        val.put("fileOrder", fileOrder);
        db.insert(db_table_name3, null, val);
    }

    //0705 파일분류를 위한 해당과목찾기
    public String whatSubject() {
//        String sql="select startime, endtime, subject " +
//                "from schedule"+
//                "where "+getDay()+" like 'true';";
        String sql = "select startime, endtime, subject " +
                "from schedule " +
                "where " + getDay() + " = 'true';";
        Log.i("AhReum", sql);
        String startime, endtime, subject, return_sub = "";
        Cursor cursor = db.rawQuery(sql, null);

        nowTime = new Time(System.currentTimeMillis());
        startTime = new Time(System.currentTimeMillis());
        endTime = new Time(System.currentTimeMillis());

        int recordCount = cursor.getCount();
        Log.i("ar", "recordCount : " + Integer.toString(recordCount));
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            startime = cursor.getString(0);
            endtime = cursor.getString(1);
            subject = cursor.getString(2);
            Log.v("AhReum", startime);
            Log.v("AhReum", endtime);
            Log.v("AhReum", subject);

            startTime.setHours(Integer.parseInt(startime.substring(0, 2)));
            startTime.setMinutes(Integer.parseInt(startime.substring(3)));
            startTime.setSeconds(0);
            endTime.setHours(Integer.parseInt(endtime.substring(0, 2)));
            endTime.setMinutes(Integer.parseInt(endtime.substring(3)));
            endTime.setSeconds(0);

            if (nowTime.after(startTime) && nowTime.before(endTime)) {
                return_sub = subject;
            }
        }
        return return_sub;
    }

    public int whatSubjectId() {
        whatSubject();
        String sql = "select _id, startime, endtime " +
                "from schedule " +
                "where " + getDay() + " like 'true';";

        String startime, endtime;
        int id = 0, return_sub = 0;
        Cursor cursor = db.rawQuery(sql, null);

        startTime = new Time(System.currentTimeMillis());

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            id = cursor.getInt(0);
            startime = cursor.getString(1);
            endtime = cursor.getString(2);

            startTime.setHours(Integer.parseInt(startime.substring(0, 2)));
            startTime.setMinutes(Integer.parseInt(startime.substring(3)));
            startTime.setSeconds(0);
            endTime.setHours(Integer.parseInt(endtime.substring(0, 2)));
            endTime.setMinutes(Integer.parseInt(endtime.substring(3)));
            endTime.setSeconds(0);

            if (nowTime.after(startTime) && nowTime.before(endTime)) {
                return_sub = id;
                //Log.v("AhReum",Integer.toString(return_sub));
            }
        }
        Log.v("AhReum", "subject_fk : " + Integer.toString(return_sub));
        return return_sub;
    }

    //memoTABLE에 넣을 file_fk값 구하기
    public int whatFileId() {

        String sql = "select file_id from fileTABLE where subject_fk = '" + whatSubjectId() + "';";
        int return_sub = 123;
        Cursor cursor = db.rawQuery(sql, null);

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String str1 = cursor.getString(0);
            Log.i("AhReum", "file_id : " + str1);
            if (nowTime.after(startTime) && nowTime.before(endTime)) {
                return_sub = Integer.parseInt(str1);
            }
        }

        //Log.i("AhReum","final file_id : "+Integer.toString(return_sub));
        return return_sub;
    }

    // getFileName() for counting
    public int getFileId() {
        int fileid = 0;

        String sql = "select file_id "
                + "from " + db_table_name3;
        Cursor cur = db.rawQuery(sql, null);

        int recordCount = cur.getCount();
        for (int i = 0; i < recordCount; i++) {
            cur.moveToNext();
            fileid = Integer.parseInt((cur.getString(0)));
        }

        Log.v("AhReum", "file_id : " + Integer.toString(fileid));
        return fileid;

    }

    public String getDay() {
        Calendar cal = Calendar.getInstance();
        int cnt = cal.get(Calendar.DAY_OF_WEEK) - 1;
        String[] week = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
        return week[cnt];
    }

    public void deleteTable() {

        String sql = "DELETE FROM memoTABLE;";
        db.execSQL(sql);
        sql = "DELETE FROM fileTABLE;";
        db.execSQL(sql);
        //추후 cascade적용해야함!!
    }

    public ArrayList whatStartTime() {
        String sql = "select startime " +
                "from schedule " +
                "where " + getDay() + " like 'true';";

        String str = "babo";
        ArrayList<String> startime = new ArrayList<String>();
        startime.add("00:00");
        Cursor cursor = db.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        Log.i("ar", "recordCount : " + Integer.toString(recordCount));
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            startime.add(cursor.getString(0));
        }
        cursor.close();
        return startime;

    }


    public ArrayList whatEndTime() {
        String sql = "select endtime " +
                "from schedule " +
                "where " + getDay() + " like 'true';";

        ArrayList<String> endtime = new ArrayList<String>();
        endtime.add("00:00");
        Cursor cursor = db.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        Log.i("ar", "recordCount : " + Integer.toString(recordCount));
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            endtime.add(cursor.getString(0));

        }
        cursor.close();
        return endtime;

    }


    public String whatID() {
        String sql = "select session_id " +
                "from sessionTABLE " + ";";

        String id = "";
        Cursor cursor = db.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            id = cursor.getString(0);

        }
        cursor.close();
        return id;
    }

    public String whatPW() {
        String sql = "select session_pw " +
                "from sessionTABLE " + ";";

        String pw = "";
        Cursor cursor = db.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            pw = cursor.getString(0);
        }
        cursor.close();
        return pw;
    }

    public ArrayList getAllSubject() {
        String sql = "select subject " +
                "from schedule " + ";";

        ArrayList<String> getAll = new ArrayList<String>();
        Cursor cursor = db.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        getAll.add("기타");
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            getAll.add(cursor.getString(0));

        }
        cursor.close();
        return getAll;
    }

    public String subjectName(int id) {
        String sql = "select subject " +
                "from schedule " +
                "where _id " + "like " + id + ";";

        String subject = "";
        Cursor cursor = db.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            subject = cursor.getString(0);
        }
        cursor.close();
        return subject;
    }

    public ArrayList AllsubjectId(String subject) {
        String sql = "select _id " +
                "from schedule " +
                "where subject " + "like " + "'%" + subject + "%'" + ";";

        ArrayList<Integer> getAll = new ArrayList<Integer>();
        Cursor cursor = db.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            getAll.add(cursor.getInt(0));

        }
        cursor.close();
        return getAll;
    }

    public String lasttime(int id) {
        String sql = "select endtime " +
                "from schedule " +
                "where _id " + "like " + id + ";";

        String subject = "";
        Cursor cursor = db.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            subject = cursor.getString(0);
        }
        cursor.close();
        return subject;
    }


    // db값이 변경되었을 때 다시 불러오는 함수
    public void updateKeyword(String keyword1, String keyword2, String keyword3) {
        ContentValues val = new ContentValues();
        val.put("keyword_id", 1);
        val.put("keyword_one", keyword1);
        val.put("keyword_two", keyword2);
        val.put("keyword_three", keyword3);
        db.update(db_table_name4, val, "keyword_id = " + 1, null);
        //search_data();
    }

    // db값이 변경되었을 때 다시 불러오는 함수
    public void updateClassdate(String classOpen, String classClose) {
        ContentValues val = new ContentValues();
        val.put("classOpen", classOpen);
        val.put("classClose", classClose);
        db.update(db_table_name5, val, "classdate_id = " + 1, null);
        //search_data();
    }

    public void updateClassOepn(String classOpen) {
        ContentValues val = new ContentValues();
        val.put("classOpen", classOpen);
        //val.put("classClose", classClose);
        db.update(db_table_name5, val, "classdate_id = " + 1, null);
        //search_data();
    }

    public void updateClassClose(String classClose) {
        ContentValues val = new ContentValues();
        //val.put("classOpen", classOpen);
        val.put("classClose", classClose);
        db.update(db_table_name5, val, "classdate_id = " + 1, null);
        //search_data();
    }

    // ★★★ 지니 update 내용수정함수
    public void updateTimeMachine(int keyword1) {
        ContentValues val = new ContentValues();
        val.put("timeMachine_id", 1);
        val.put("timeMachine", keyword1);
        db.update(db_table_name6, val, "timeMachine_id = " + 1, null);
        //search_data();
    }

    // ★★★ 혜리 update 내용수정함수
    public void updateAutoRecord(String keyword1) {
        ContentValues val = new ContentValues();
        val.put("autoRecord_id", 1);
        val.put("autoRecord", keyword1);
        db.update(db_table_name7, val, "autoRecord_id = " + 1, null);
        //search_data();
    }

    public void queryKeywrod() {
        String sql = "select keyword_id ,keyword_one ,keyword_two, keyword_three from keywordTABLE";
        Cursor cursor = db.rawQuery(sql, null);

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String str1 = cursor.getString(0);
            String str2 = cursor.getString(1);
            String str3 = cursor.getString(2);
            String str4 = cursor.getString(3);
            Log.i("AhReum", "keyword : " + str1 + "   " + "keyword1 : " + str2 + "   keyword2 : " + str3 + "   keyword3 : " + str4); //
        }
    }

    public void queryClassdate() {
        String sql = "select classdate_id ,classOpen ,classClose from classdateTABLE";
        Cursor cursor = db.rawQuery(sql, null);

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String str1 = cursor.getString(0);
            String str2 = cursor.getString(1);
            String str3 = cursor.getString(2);
            Log.i("AhReum", "classdate : " + str1 + "   " + "keyword1 : " + str2 + "   keyword2 : " + str3); //
        }
    }

    public String getClassOpen() {
        String sql = "select classdate_id, classOpen ,classClose from classdateTABLE";
        String str = "";
        Cursor cursor = db.rawQuery(sql, null);

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String str1 = cursor.getString(0);
            str = cursor.getString(1);
            String str3 = cursor.getString(2);
            Log.i("AhReum", "classdate : " + str1 + "   " + "classOpen : " + str + "   classClose : " + str3); //
        }
        return str;
    }

    public String getClassClose() {
        String sql = "select classdate_id ,classOpen ,classClose from classdateTABLE";
        String str = "";
        Cursor cursor = db.rawQuery(sql, null);

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String str1 = cursor.getString(0);
            String str2 = cursor.getString(1);
            str = cursor.getString(2);
            Log.i("AhReum", "classdate : " + str1 + "   " + "classOpen : " + str2 + "   classClose : " + str);
        }

        return str;
    }


    // ★★★ 지니 query 내용불러오기함수
    public int queryTimeMachine() {
        String sql = "select timeMachine_id, timeMachine  from timeMachineTABLE";
        Cursor cursor = db.rawQuery(sql, null);

        int str2 = 0;

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String str1 = cursor.getString(0);
            str2 = Integer.parseInt(cursor.getString(1));
            Log.i("AhReum", "queryTimeMachine : " + str1 + " timeMachine : " + str2); //
        }
        return str2;
    }

    // ★★★ 혜리 query 내용불러오기함수
    public String queryAutoRecord() {
        String sql = "select autoRecord_id, autoRecord  from autoRecordTABLE";
        String str2 = "";
        Cursor cursor = db.rawQuery(sql, null);


        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String str1 = cursor.getString(0);
            str2 = cursor.getString(1);
            Log.i("AhReum", "queryAutoRecord : " + str1 + " autoRecord : " + str2); //
        }
        return str2;
    }


    public void saveId(int keyword1) {
        ContentValues val = new ContentValues();
        val.put("saveid", keyword1);
        db.insert(db_table_name8, null, val);
        search_data();
        //  db.update(db_table_name8, val, "saveid = " + 1, null);
        //search_data();
    }


    public ArrayList GetId() {
        String sql = "select saveid from idTABLE";

        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Integer> getId = new ArrayList<Integer>();


        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            getId.add(cursor.getInt(0));
            //Log.i("AhReum", "queryAutoRecord : " + str1+ " autoRecord : " + str2); //
        }
        return getId;
    }


}


/*
    지니꺼
     ========================================================================
    ★DB설계도★
    [timeMachineTABLE]
    - timeMachine_id integer PRIMARY KEY
    - timeMachine integer
    ★DB 관련 함수★ // RecordTimeTable_Helper.java에 존재
    queryTimeMachine() // 내용불러오기함수
    updateTimeMachine(int keyword1) // 내용수정함수
    =================================================================11m    =======

    혜리꺼
    ========================================================================
    ★DB설계도★
    [autoRecordTABLE]
    - autoRecord_id integer PRIMARY KEY
    - autoRecord text
    ★DB 관련 함수★ // RecordTimeTable_Helper.java에 존재
    queryAutoRecord() // 내용불러오기함수
    updateAutoRecord(String keyword1)  // 내용수정함수
    ========================================================================
 */