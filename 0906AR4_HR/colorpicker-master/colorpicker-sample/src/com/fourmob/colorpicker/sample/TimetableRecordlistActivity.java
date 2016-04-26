package com.fourmob.colorpicker.sample;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fourmob.colorpicker.sample.PlayerService.PlayerBinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TimetableRecordlistActivity extends Activity {
    NotificationManager mNM;
    private int mId = 1;

    private ImageButton playButton, prevButton, nextButton, backwardButton, forwardButton;
    private ActionBar actionBar;
    private SeekBar trackSeek;
    private TextView currentTrackProgressView, currentTrackDurationView;
    private Timer progressRefresher;
    private int playerStatus;
    private UiRefresher uiRefresher;
    private PlayerService playerService;
    boolean mBound = false;
    private Spinner spin;
    private ArrayList<HashMap<String, String>> songsList2 = new ArrayList<HashMap<String,
            String>>();
    private ArrayList<HashMap<String, String>> songsList3 = new ArrayList<HashMap<String,
            String>>();
    private ArrayList<HashMap<String, String>> songsList4 = new ArrayList<HashMap<String,
            String>>();
    ListView list;
    RecordTimeTable_Helper dbHelper;
    private SimpleAdapter tracklistAdapter;

    String sql;
    Cursor cursor;
    String sub1 = "";

    int timeM;


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection playerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance

            PlayerBinder binder = (PlayerBinder) service;
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

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_timetable_recordlist);

        spin = (Spinner) findViewById(R.id.spinner1);
        progressRefresher = new Timer();
        playButton = (ImageButton) findViewById(R.id.playButton);
        prevButton = (ImageButton) findViewById(R.id.previousButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        backwardButton = (ImageButton) findViewById(R.id.backwardButton);
        forwardButton = (ImageButton) findViewById(R.id.forwardButton);
        trackSeek = (SeekBar) findViewById(R.id.track_seek);
        currentTrackProgressView = (TextView) findViewById(R.id.track_progress);
        currentTrackDurationView = (TextView) findViewById(R.id.track_duration);
        dbHelper = new RecordTimeTable_Helper(this);
        Log.v("JinHee", "songList2: " + songsList2);
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        progressRefresher.schedule(new TimerTask() {

            @Override
            public void run() {
                if (playerStatus == PlayerService.PLAYING) {
                    refreshTrack();
                }
            }
        }, 0, 500);

        list = (ListView) findViewById(R.id.indexlist);
        //timeM = cursor.getString(cursor.getColumnIndex("timeMachine"));
        timeM = dbHelper.queryTimeMachine();

        //인덱스 리스트 함수
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                String time = cursor.getString(cursor.getColumnIndex("memoTime"));
                int index2 = Integer.parseInt(time);
                int index3 = index2 * 1000;
                int timeM2 = timeM * 1000;
                int index4 = index3 - timeM2;
                playerService.seekTrack(index4);
                //Toast.makeText(TimetableRecordlistActivity.this, Integer.toString(index4), Toast.LENGTH_SHORT).show();
            }
        });

        //과목명 불러오는 함수
        final Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int id = intent.getIntExtra("id", 0);
        String subject = intent.getStringExtra("subject");
        TextView subjectwrite = (TextView) findViewById(R.id.subject);
        subjectwrite.setText(subject);


        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (playerStatus == PlayerService.PLAYING) {
                    playerService.pause();
                    // Changing button image to play button
                    playButton.setImageResource(R.drawable.btn_play);
//                    mNM.cancel(mId);

                } else if (playerStatus == PlayerService.PAUSED) {
                    // Resume song
                    playerService.play();
                    // Changing button image to pause button
                    playButton.setImageResource(R.drawable.btn_pause);
//                    sendNotification(null);
                }
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
                R.layout.spinner_dropdown_item, new String[]{"songTitle"}, new int[]
                {R.id.tracklist_item_title});
        tracklistAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spin.setAdapter(tracklistAdapter);


        //  int s_size = tracklistAdapter.size();
        //if (s_size == 0){
        // Toast.makeText(TimetableRecordlistActivity.this,"저장된 파일이 없습니다.", Toast.LENGTH_SHORT).show();
        // Toast.makeText(TimetableRecordlistActivity.this, Integer.toString(s_size), Toast.LENGTH_SHORT).show();
        //  }


        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                // playerService.play(pos);
                String text = spin.getSelectedItem().toString();
                String[] arr = text.split("=");
                String strsss = arr[2];
                int length1 = strsss.length();
                sub1 = strsss.substring(0, length1 - 1);
                selectDB(sub1);
                int songIndex = pos;
                playerService.play(songIndex);
                playerService.pause();
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
                        currentTrackProgressView.setText(PlayerService.formatTrackDuration
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
                if (playerStatus == PlayerService.PLAYING) {
                    refreshTrack();
                }
            }
        }, 0, 500);
    }// Oncreate End

    private void sendNotification(NotificationCompat.Style style) {
        Notification noti = new Notification(R.drawable.ic_launcher,
                "재생하고있습니다.", System.currentTimeMillis());
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(this, TimetableRecordlistActivity.class);
        intent.putExtra("UserName", "12345");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent content =
                PendingIntent.getActivity(this, 0, intent, 0);
        noti.setLatestEventInfo(this,
                "재생알림", "재생파일이있습니다.", content);
        mNM.notify(mId, noti);
    }

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
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        getApplicationContext().bindService(playerServiceIntent, playerServiceConnection,
                0);
        mBound = true;

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
//       getApplicationContext().unbindService(playerServiceConnection);
//        mBound=false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            playerService.stop();
            //Toast.makeText(TimetableRecordlistActivity.this,"서비스를 종료합니다.", Toast.LENGTH_SHORT).show();
            getApplicationContext().unbindService(playerServiceConnection);
            mBound = false;

        }

        return super.onKeyDown(keyCode, event);
    }


    private void refreshTrack() {

        final int progress = playerService.getCurrentTrackProgress(), max =
                playerService.getCurrentTrackDuration();
        final String durationText = PlayerService.formatTrackDuration
                (playerService.getCurrentTrackDuration()), progressText = PlayerService.formatTrackDuration
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
                    case PlayerService.PLAYING:
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
        final ArrayList<HashMap<String, String>> currentTracks = playerService.getTracklist();
        final int currentTrackPosition = playerService.getCurrentTrackPosition();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                songsList3.clear();
                for (int i = 0; i < currentTracks.size(); i++) {
                    HashMap<String, String> song = currentTracks.get(i);
                    songsList3.add(song);
                }

                tracklistAdapter.notifyDataSetChanged();
                spin.setSelection(currentTrackPosition);
            }

        });
    }


    private class UiRefresher implements Runnable {
        private boolean done = false;

        public void done() {
            done = true;
        }

        @Override
        public void run() {

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

    private void selectDB(String subject) {
        sql = "select DISTINCT oid as _id,memo, memoTime from memoTABLE where fileName='" + subject + ".mp4'";
        cursor = dbHelper.db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            startManagingCursor(cursor);
            DBAdapter dbAdapter = new DBAdapter(this, cursor, 0);
            list.setAdapter(dbAdapter);
            dbAdapter.notifyDataSetChanged();
        } else {
            list.setAdapter(null);

        }
    }

}