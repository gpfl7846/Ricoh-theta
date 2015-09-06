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
        String sql = "create table if not exists "+ db_table_name + "("
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS" + db_table_name);
        db.execSQL("DROP TABLE IF EXISTS" + db_table_name2);
        db.execSQL("DROP TABLE IF EXISTS" + db_table_name3);
        onCreate(db);
    }

    //DB에 데이터 더하기
    //id + 과목 + 강의 실명   --> 여기에 시간까지 더하기
    public void add(int id, String a, String b,String c,String d,int e,
                    String mon,String tues,String  wednes,String thurs,String  fri,String sat, String sun){
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
    public void update(long rawId, String a, String b,String c,String d,int e,
                       String mon,String tues,String  wednes,String thurs,String  fri,String sat, String sun){
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
        db.update(db_table_name, val, "_id = "+ rawId, null);
        search_data();
    }

    //DB에서의 삭제
    public void delete(long rawId){
        //DB에서 아이디를 삭제하는것 같음
        db.delete(db_table_name, "_id = "+ rawId , null);
        search_data();

    }

    public void search_data(){
        String sql = "select * from "+ db_table_name;
        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();


        //마지막 레코드인지 확인
        while(!cur.isAfterLast()){

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
    public Cursor getAll(){

        return db.query(db_table_name, null, null,null,null,null,null,null);

    }

    // db에 있는 아이디 가지고 오기
    public Cursor getId(int id){
        Cursor cur = db.query(db_table_name , null, "_id = " + id , null,null,null,null,null);
        if(cur!=null&&cur.getCount() !=0)
            cur.moveToNext();
        return cur;

    }

    public int getCounter(){
        Cursor cur = null;
        String sql = "select * from "+ db_table_name;
        //rawQuery 메소드를 사용해 SELECT 구문을 직접 실행
        cur = db.rawQuery(sql, null);
        int counter = 0;

        while(!cur.isAfterLast()){
            cur.moveToNext();
            counter++;
        }
        return counter;
    }

    public void insertMemo(int memoTime, String memo, String fileName){
        ContentValues val = new ContentValues();
        val.put("file_fk", whatFileId());
        val.put("memoTime", memoTime);
        val.put("memo", memo);
        val.put("fileName", fileName);
        db.insert("memoTABLE", null, val);
        //Log.v("AhReum", "whatFileId() : " + whatFileId());
    }

    public void insertFile(String fileName, String fileDirectory,int whatWeek, int dayOfWeek, int fileOrder) {
        ContentValues val = new ContentValues();
        val.put("subject_fk",whatSubjectId());
        val.put("fileName", fileName);
        val.put("fileDirectory", fileDirectory);
        val.put("what_week", whatWeek);
        val.put("day_of_week", dayOfWeek);
        val.put("fileOrder", fileOrder);
        db.insert(db_table_name3, null, val);
    }

//    private void query(){
//        String sql="select memo, memoTime,fileName from memoTABLE";
//        Cursor cursor=db.rawQuery(sql, null);
//
//        int recordCount=cursor.getCount();
//        for(int i=0; i<recordCount; i++){
//            cursor.moveToNext();
//            String str1=cursor.getString(0);
//            String str2=cursor.getString(1);
//            String str3=cursor.getString(2);
//            Log.v("AhReum","recordCount : " +recordCount+"   memoTime : "+str1+"   memoTime : "+str2+"   fileName : "+str3);
//            //textView.append("\n" + "메모 : " + str1 +"\n"+ "인덱스시간 : "+ str2 +" 파일명 : "+str3);
//        }}

    //0705 파일분류를 위한 해당과목찾기
    public String whatSubject(){
//        String sql="select startime, endtime, subject " +
//                "from schedule"+
//                "where "+getDay()+" like 'true';";
        String sql="select startime, endtime, subject " +
                "from schedule "+
                "where "+getDay()+" = 'true';";
        Log.i("AhReum",sql);
        String startime, endtime, subject, return_sub="";
        Cursor cursor=db.rawQuery(sql, null);

        nowTime = new Time(System.currentTimeMillis());
        startTime = new Time(System.currentTimeMillis());
        endTime = new Time(System.currentTimeMillis());

        int recordCount=cursor.getCount();
        Log.i("ar", "recordCount : "+Integer.toString(recordCount));
        for(int i=0; i<recordCount; i++){
            cursor.moveToNext();
            startime =cursor.getString(0);
            endtime  =cursor.getString(1);
            subject  =cursor.getString(2);
            Log.v("AhReum",startime); Log.v("AhReum",endtime); Log.v("AhReum",subject);

            startTime.setHours(Integer.parseInt(startime.substring(0,2))); startTime.setMinutes(Integer.parseInt(startime.substring(3))); startTime.setSeconds(0);
            endTime.setHours(Integer.parseInt(endtime.substring(0,2))); endTime.setMinutes(Integer.parseInt(endtime.substring(3))); endTime.setSeconds(0);

            if( nowTime.after(startTime) && nowTime.before(endTime)) {
                return_sub = subject;
            }
        }
        return return_sub;
    }

    public int whatSubjectId(){
        whatSubject();
        String sql="select _id, startime, endtime " +
                "from schedule "+
                "where "+getDay()+" like 'true';";

        String startime, endtime;
        int id=0, return_sub=0;
        Cursor cursor=db.rawQuery(sql, null);

        startTime = new Time(System.currentTimeMillis());

        int recordCount=cursor.getCount();
        for(int i=0; i<recordCount; i++){
            cursor.moveToNext();
            id =cursor.getInt(0);
            startime  =cursor.getString(1);
            endtime  =cursor.getString(2);

            startTime.setHours(Integer.parseInt(startime.substring(0, 2))); startTime.setMinutes(Integer.parseInt(startime.substring(3))); startTime.setSeconds(0);
            endTime.setHours(Integer.parseInt(endtime.substring(0, 2))); endTime.setMinutes(Integer.parseInt(endtime.substring(3))); endTime.setSeconds(0);

            if( nowTime.after(startTime) && nowTime.before(endTime)) {
                return_sub = id;
                //Log.v("AhReum",Integer.toString(return_sub));
            }
        }
        Log.v("AhReum","subject_fk : "+Integer.toString(return_sub));
        return return_sub;
    }

    //memoTABLE에 넣을 file_fk값 구하기
    public int whatFileId(){

        String sql="select file_id from fileTABLE where subject_fk = '"+whatSubjectId()+"';";
        int return_sub=123;
        Cursor cursor=db.rawQuery(sql, null);

        int recordCount=cursor.getCount();
        for(int i=0; i<recordCount; i++){
            cursor.moveToNext();
            String str1=cursor.getString(0);
            Log.i("AhReum", "file_id : " + str1 );
            if( nowTime.after(startTime) && nowTime.before(endTime)) {
                return_sub = Integer.parseInt(str1);
            }
        }

        //Log.i("AhReum","final file_id : "+Integer.toString(return_sub));
        return return_sub;
    }

    // getFileName() for counting
    public int getFileId(){
        int fileid = 0;

        String sql= "select file_id "
                + "from "+ db_table_name3;
        Cursor cur=db.rawQuery(sql, null);

        int recordCount=cur.getCount();
        for(int i=0; i<recordCount; i++){
            cur.moveToNext();
            fileid = Integer.parseInt((cur.getString(0)));
        }

        Log.v("AhReum", "file_id : " + Integer.toString(fileid));
        return fileid;

    }

    public String getDay(){
        Calendar cal = Calendar.getInstance();
        int cnt = cal.get(Calendar.DAY_OF_WEEK) - 1;
        String[] week = { "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday" };
        return week[cnt];
    }

    public void deleteTable(){

        String sql = "DELETE FROM memoTABLE;";
        db.execSQL(sql);
        sql = "DELETE FROM fileTABLE;";
        db.execSQL(sql);
        //추후 cascade적용해야함!!
    }
    public ArrayList whatStartTime(){
        String sql="select startime " +
                "from schedule "+
                "where "+getDay()+" like 'true';";

        String str="babo";
        ArrayList<String> startime = new ArrayList<String>();
        startime.add("00:00");
        Cursor cursor=db.rawQuery(sql, null);
        int recordCount=cursor.getCount();
        Log.i("ar", "recordCount : " + Integer.toString(recordCount));
        for(int i=0; i<recordCount; i++){
            cursor.moveToNext();
            startime.add(cursor.getString(0));
        }
        cursor.close();
        return startime;

    }


    public ArrayList whatEndTime(){
        String sql="select endtime " +
                "from schedule "+
                "where "+getDay()+" like 'true';";

        ArrayList<String> endtime = new ArrayList<String>();
        endtime.add("00:00");
        Cursor cursor=db.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        Log.i("ar", "recordCount : " + Integer.toString(recordCount));
        for(int i=0; i<recordCount; i++) {
            cursor.moveToNext();
            endtime.add(cursor.getString(0));

        }
        cursor.close();
        return endtime;

    }



    public String whatID(){
        String sql = "select session_id " +
                "from sessionTABLE "+";";

        String id = "";
        Cursor cursor=db.rawQuery(sql, null);
        int recordCount=cursor.getCount();
        for(int i=0; i<recordCount; i++){
            cursor.moveToNext();
            id= cursor.getString(0);

        }
        cursor.close();
        return id;
    }
    public String whatPW(){
        String sql = "select session_pw " +
                "from sessionTABLE "+";";

        String pw = "";
        Cursor cursor=db.rawQuery(sql, null);
        int recordCount=cursor.getCount();
        for(int i=0; i<recordCount; i++){
            cursor.moveToNext();
            pw= cursor.getString(0);
        }
        cursor.close();
        return pw;
    }


    public ArrayList getAllSubject(){
        String sql = "select subject " +
                "from schedule "+";";

        ArrayList<String> getAll = new ArrayList<String>();
        Cursor cursor=db.rawQuery(sql, null);
        int recordCount=cursor.getCount();
        for(int i=0; i<recordCount; i++){
            cursor.moveToNext();
            getAll.add(cursor.getString(0));

        }
        cursor.close();
        return getAll;
    }

}