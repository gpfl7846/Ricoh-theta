package  com.fourmob.colorpicker.sample;

        import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//
//        Intent playerServiceIntent = new Intent(this, PlayerService.class);
//        startService(playerServiceIntent);
//
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        //tabHost.setBackgroundColor(Color.BLUE);
        //tabHost.setDrawingCacheBackgroundColor(Color.GREEN);
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, BoundActivity2.class);
        spec = tabHost.newTabSpec("녹음기").setIndicator("", res.getDrawable(R.drawable.ic_tab_one)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Timetable.class);
        spec = tabHost.newTabSpec("시간표").setIndicator(res.getString(R.string.timetable), res.getDrawable(R.drawable.ic_tab_two)).setContent(intent);
        //spec = tabHost.newTabSpec("library_browser");
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Dropboxmain.class);
        spec = tabHost.newTabSpec("설정").setIndicator(res.getString(R.string.timetable), res.getDrawable(R.drawable.ic_tab_three)).setContent(intent);
        // spec = tabHost.newTabSpec("file_browser");
        tabHost.addTab(spec);

        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#de4e43"));
        tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#de4e43"));
        tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#de4e43"));


        tabHost.setCurrentTab(0);
    }
}
