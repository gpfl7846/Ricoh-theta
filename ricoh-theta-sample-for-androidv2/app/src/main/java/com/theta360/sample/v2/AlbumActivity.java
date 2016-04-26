package com.theta360.sample.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bailey on 3/14/16.
 */
public class AlbumActivity extends Activity {
    private ListView objectList;
    ListView lv;
    // Make sure the request was successful


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        lv = (ListView) findViewById(R.id.album_list);

        List<String> SettingRows = new ArrayList<>();
        SettingRows.add("Photos");
        // Assign adapter to ListView
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, SettingRows);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                int itemPosition = position;
                String itemValue = (String) lv.getItemAtPosition(position);
                Intent i = new Intent(AlbumActivity.this, Album_ImageList.class);
                startActivity(i);
                // ListView Clicked item value
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
