package com.fourmob.colorpicker.sample;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class DBAdapter extends CursorAdapter {


    public DBAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final ImageView image = (ImageView)view.findViewById(R.id.image);
        final TextView memo = (TextView)view.findViewById(R.id.memo);
        final TextView time = (TextView)view.findViewById(R.id.time);

        image.setImageResource(R.drawable.ic_launcher);
        memo.setText("메모 : "+cursor.getString(cursor.getColumnIndex("memo")));
        time.setText("시간 : "+cursor.getString(cursor.getColumnIndex("memoTime")));

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.listlayout, parent, false);
        return v;
    }
}
