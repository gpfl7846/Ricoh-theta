package com.fourmob.colorpicker.sample;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TimetableRecordlistActivity extends Activity {
    private ImageButton playButton, prevButton, nextButton, backwardButton, forwardButton;
    private ActionBar actionBar;
    private SeekBar trackSeek;
    private TextView currentTrackProgressView, currentTrackDurationView;
    private Timer progressRefresher;
    private int playerStatus;
    private UiRefresher uiRefresher;
    private DBAdapter.PlayerService playerService;
    private ListView tracklistView;

    // private int currentSongIndex = 0;
    //public SongsManager songManager;
    boolean mBound = false;
    private Spinner spin;
    private ArrayList<HashMap<String, String>> songsList2 = new ArrayList<HashMap<String,
            String>>();
    private ArrayList<HashMap<String, String>> songsList3 = new ArrayList<HashMap<String,
            String>>();
    private ArrayAdapter<DBAdapter.PlayerService.Track> songsList;
    ListView list;
    RecordTimeTable_Helper dbHelper;

    final String tlqkf ="";

    //  private ArrayAdapter<Track> tracklistAdapter;
    private SimpleAdapter tracklistAdapter;
//    private ArrayAdapter<HashMap<String, String>> tracklistAdapter;

    String sql;
    Cursor cursor;
    String sub1="";
    final static String dbName = "memoDB.db";
    final static int dbVersion = 2;






    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection playerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance

            DBAdapter.PlayerService.PlayerBinder binder = (DBAdapter.PlayerService.PlayerBinder) service;
            playerService = binder.getService();
            uiRefresher = new UiRefresher();
            (new Thread(uiRefresher)).start();
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

/*    public player() {
    }*/

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_timetable_recordlist);

        spin = (Spinner) findViewById(R.id.spinner1);
        progressRefresher = new Timer();

        tracklistView = (ListView)findViewById(R.id.indexlist);
        playButton = (ImageButton)findViewById(R.id.playButton);
        prevButton = (ImageButton)findViewById(R.id.previousButton);
        nextButton = (ImageButton)findViewById(R.id.nextButton);
        backwardButton = (ImageButton)findViewById(R.id.backwardButton);
        forwardButton = (ImageButton)findViewById(R.id.forwardButton);
        trackSeek = (SeekBar)findViewById(R.id.track_seek);
        currentTrackProgressView = (TextView)findViewById(R.id.track_progress);
        currentTrackDurationView = (TextView)findViewById(R.id.track_duration);
        // actionBar = getActionBar();
        // actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#de4e43")));
        dbHelper = new RecordTimeTable_Helper(this);
//        selectDB(sub1);
        //final Timetable tt = new Timetable();
        //songManager = new SongsManager();
//        songsList2 = songManager.getPlayList();
        Log.v("JinHee", "songList2: " + songsList2);

        progressRefresher.schedule(new TimerTask() {

            @Override
            public void run() {
                if (playerStatus == DBAdapter.PlayerService.PLAYING) {
                    refreshTrack();
                }
            }
        }, 0, 500);




        //     registerForContextMenu(spin);

        list = (ListView)findViewById(R.id.indexlist);
        //인덱스 리스트 함수
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                cursor.moveToPosition(position);
                // String str = cursor.getString(cursor.getColumnIndex("memo"));
                String time = cursor.getString(cursor.getColumnIndex("memoTime"));
                int index2 = Integer.parseInt(time);
                int index3 = index2 * 1000;
                // Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), time, Toast.LENGTH_SHORT).show();

                playerService.seekTrack(index3);

            }
        });

        //과목명 불러오는 함수
        final Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        String subject= intent.getStringExtra("subject");
        TextView subjectwrite = (TextView) findViewById( R.id.subject);
        subjectwrite.setText(subject);


        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View arg0){
                // check for already playing
                if (playerStatus == DBAdapter.PlayerService.PLAYING) {
                    playerService.pause();
                    // Changing button image to play button
                    playButton.setImageResource(R.drawable.btn_play);
                } else {
                    // Resume song
                    playerService.play();
                    // Changing button image to pause button
                    playButton.setImageResource(R.drawable.btn_pause);
                }


/*                final ArrayList<HashMap<String, String>> list3 =
playerService.getTracklist();
                Log.v("JinHee", "서비스는 정상이길 ㅎㅎㅎㅎㅎ 시발 : "+ list3);*/

            }
        });



        /**
         * Forward button click event
         * Forwards song specified seconds
         * */
        forwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                playerService.forward();
            }
        });

        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
        backwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                playerService.backward();
            }
        });


        /**
         * Prev Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        prevButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                playerService.prevTrack();
            }
        });

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                playerService.nextTrack();
            }
        });



        /**
         * PlayList Setting into spinner
         *
         * */


        tracklistAdapter = new SimpleAdapter(this, songsList3,
                R.layout.spinner_dropdown_item, new String[] { "songTitle" }, new int[]
                {R.id.tracklist_item_title });
        tracklistAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spin.setAdapter(tracklistAdapter);


        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                // playerService.play(pos);

                String text = spin.getSelectedItem().toString();
                String[] arr = text.split("=");
                String strsss = arr[2];
                int length1 = strsss.length();
                sub1 = strsss.substring(0,length1-1);
                selectDB(sub1);
                int songIndex = pos;
                playerService.playTrack(songIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         * SeekBar function
         *
         * */
        trackSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar arg0, final int pos, boolean user) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        currentTrackProgressView.setText(DBAdapter.PlayerService.formatTrackDuration
                                (pos));
                    }
                });
                if (user) {
                    playerService.seekTrack(pos);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }
        });
        progressRefresher.schedule(new TimerTask() {

            @Override
            public void run() {
                if (playerStatus == DBAdapter.PlayerService.PLAYING) {
                    refreshTrack();
                }
            }
        }, 0, 500);
    }// Oncreate End

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent playerServiceIntent = new Intent(this, DBAdapter.PlayerService.class);
        getApplicationContext().bindService(playerServiceIntent, playerServiceConnection,
                0);
        mBound=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (playerService != null) {
            synchronized (playerService) {
                playerService.notifyAll();
                uiRefresher.done();
            }
        }
        if (playerService != null) {
            getApplicationContext().unbindService(playerServiceConnection);
            mBound=false;
            //playerService.storeTracklist();
        }

    }
/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(playerService != null){
            getApplicationContext().unbindService(playerServiceConnection);
            mBound=false;

        }
    }
*/

    private void refreshTrack() {

        final int progress = playerService.getCurrentTrackProgress(), max =
                playerService.getCurrentTrackDuration();
        final String durationText = DBAdapter.PlayerService.formatTrackDuration
                (playerService.getCurrentTrackDuration()), progressText = DBAdapter.PlayerService.formatTrackDuration
                (playerService.getCurrentTrackProgress());
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                currentTrackDurationView.setText(durationText);
                currentTrackProgressView.setText(progressText);
                trackSeek.setMax(max);
                trackSeek.setProgress(progress);
            }
        });
    }
    private void refreshButtons() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                switch (playerStatus) {
                    case DBAdapter.PlayerService.PLAYING:
                        playButton.setImageResource(R.drawable.btn_pause);
                        break;
                    default:
                        playButton.setImageResource(R.drawable.btn_play);
                        break;
                }

            }
        });
    }
    private void refreshTracklist() {

        final ArrayList<HashMap<String, String>> currentTracks = playerService.getTracklist
                ();
        final int currentTrackPosition = playerService.getCurrentTrackPosition();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                songsList3.clear();
                Log.v("JinHee", "널떠라 ㅎㅎ " + songsList3);
                for (int i = 0; i < currentTracks.size(); i++) {
                    HashMap<String, String> song = currentTracks.get(i);
                    songsList3.add(song);
                }

                tracklistAdapter.notifyDataSetChanged();
                spin.setSelection(currentTrackPosition);
                // final String test = playerService.getPath();
                Log.v("JinHee", "Run을 돌고있는 songsList3입니다. : " + songsList3);
            }

        });
    }

    private class UiRefresher implements Runnable {
        private boolean done = false;

        public void done() {
            done = true;
        }

        @Override
        public void run () {

            while (!done) {
                synchronized (playerService) {
                    playerStatus = playerService.getStatus();
                    refreshTrack();
                    refreshTracklist();
                    refreshButtons();
                    playerService.take();
                    try {
                        playerService.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void selectDB(String subject){
        sql = "select DISTINCT oid as _id,memo, memoTime from memoTABLE where fileName='"+subject+".mp4'";
        cursor = dbHelper.db.rawQuery(sql, null);
        if(cursor.getCount() > 0){
            Log.v("JinHee", "GetCount??" + cursor.getCount());
            startManagingCursor(cursor);
            DBAdapter dbAdapter = new DBAdapter(this, cursor,0);
            list.setAdapter(dbAdapter);
            dbAdapter.notifyDataSetChanged();
        }
        else {
            list.setAdapter(null);

        }
    }

}