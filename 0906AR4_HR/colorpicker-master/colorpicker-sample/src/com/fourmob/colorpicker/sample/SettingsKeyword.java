package com.fourmob.colorpicker.sample;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsKeyword extends Activity {

//    final static int INTENT_CODE = 1;

    //Database를 생성 관리하는 클래스
    private RecordTimeTable_Helper helper;

    private BoundActivity2 boundActivity2;
    private EditText etRadio1, etRadio2, etRadio3;
    private Button btnSave;

    static String sqlRadio1, sqlRadio2, sqlRadio3;
    static String strRadio1, strRadio2, strRadio3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_keyword);
        helper = new RecordTimeTable_Helper(SettingsKeyword.this);
        queryKeywrod();
        setButtonHandlers();

    }

    private void setButtonHandlers() {
        // get object
        etRadio1 = (EditText) findViewById(R.id.et_radio1);
        etRadio2 = (EditText) findViewById(R.id.et_radio2);
        etRadio3 = (EditText) findViewById(R.id.et_radio3);
        btnSave = (Button) findViewById(R.id.btnSave);

        // set valsue from keywordTABLE database table
        etRadio1.setText(sqlRadio1);
        etRadio2.setText(sqlRadio2);
        etRadio3.setText(sqlRadio3);

        // if clickEvent occur, set new keyword into keywordTABLE database table
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AhReum", "SettingKeyword & btnSave");
                //boundActivity2.setBookmarkKeyword(strRadio1, strRadio2);

                strRadio1 = etRadio1.getText().toString();
                strRadio2 = etRadio2.getText().toString();
                strRadio3 = etRadio3.getText().toString();

                helper.updateKeyword(strRadio1, strRadio2, strRadio3);
                helper.queryKeywrod();

                Toast.makeText(SettingsKeyword.this, "책갈피 키워드가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
    }

    public void queryKeywrod() {
        String sql = "select keyword_one ,keyword_two, keyword_three from keywordTABLE";
        Cursor cursor = helper.db.rawQuery(sql, null);

        int recordCount = cursor.getCount();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            sqlRadio1 = cursor.getString(0);
            sqlRadio2 = cursor.getString(1);
            sqlRadio3 = cursor.getString(2);
            Log.i("AhReum", "sqlRadio1 : " + sqlRadio1 + "   " + "sqlRadio2 : " + sqlRadio2 + "   sqlRadio3 : " + sqlRadio3);
        }
    }

}
