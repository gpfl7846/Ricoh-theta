package com.fourmob.colorpicker.sample;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore.Audio;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerService extends Service {

    static public final int STOPED = -1, PAUSED = 0, PLAYING = 1;
    private MediaPlayer mediaPlayer;
    private ArrayList<HashMap<String, String>> tracklist;
    private int status, currentTrackPosition;
    private boolean taken;
    private IBinder playerBinder;
    private SongsManager songsManager;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    static public String path="init";
    public String subject ="";
//    final String MEDIA_PATH =  Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator;
    private String mp3Pattern = ".mp4";

   // private Utilities utils;
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        tracklist = new ArrayList<HashMap<String, String>>();
        currentTrackPosition = -1;
        setStatus(STOPED);

        playerBinder = new PlayerBinder();
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                if (currentTrackPosition == tracklist.size()-1) {
                    stop();
                } else {
                    nextTrack();
                }
            }
        });
        addTrack();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return playerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void take() {
        taken = true;
    }

    private void untake() {
        synchronized (this) {
            taken = false;
            notifyAll();
        }
    }

    public boolean isTaken() {
        return taken;
    }

    private void setStatus(int s) {
        status = s;
    }

    public int getStatus() {
        return status;
    }

    public ArrayList<HashMap<String, String>> getTracklist() {
        getPath();
        return tracklist;
    }

    public void addTrack(){
       // songsManager = new SongsManager();
        tracklist = getPlayList();
    }


    public HashMap<String, String> getTrack(int pos) {
        return tracklist.get(pos);
    }

    public HashMap<String, String> getCurrentTrack() {
        if (currentTrackPosition < 0) {
            return null;
        } else {
            return tracklist.get(currentTrackPosition);
        }
    }


    public int getCurrentTrackPosition() {
        return currentTrackPosition;
    }


    public void removeTrack(int pos) {
        if (pos == currentTrackPosition) {
            stop();
        }
        if (pos < currentTrackPosition) {
            currentTrackPosition--;
        }
        tracklist.remove(pos);
        untake();
    }

    public void clearTracklist() {
        if (status > STOPED) {
            stop();
        }
        tracklist.clear();
        untake();
    }



    public String gettlqkf(){
        String tlqkf;
        tlqkf = "변수가 잘 전달되고 있나요? -서비스- ";
        return tlqkf;
    }


    public void playTrack(int pos) {
        if (status > STOPED) {
            stop();
        }
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(tracklist.get(pos).get("songPath")));
            mediaPlayer.setDataSource(file.getFD());
            mediaPlayer.prepare();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        currentTrackPosition = pos;
        setStatus(PLAYING);
        untake();
    }

    public void play(int pos) {
        playTrack(pos);
    }

    public void play() {
        switch (status) {
            case STOPED:
                if (!tracklist.isEmpty()) {
                    playTrack(0);
                    setStatus(PLAYING);
                }
                else{
                    Log.v("JinHee", "플레이할 파일이 없네유!!!!");
                }
                break;
            case PLAYING:
                mediaPlayer.pause();
                setStatus(PAUSED);
                break;
            case PAUSED:
                mediaPlayer.start();
                setStatus(PLAYING);
                break;
        }
        untake();
    }

    public void pause() {
        mediaPlayer.pause();
        setStatus(PAUSED);
        untake();
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        currentTrackPosition = -1;
        setStatus(STOPED);
        untake();
    }

    public void nextTrack() {
        // check if next song is there or not
        if(currentTrackPosition < (tracklist.size() - 1)){
            playTrack(currentTrackPosition + 1);
            currentTrackPosition = currentTrackPosition + 1;
        }else{
            // play first song
            playTrack(0);
            currentTrackPosition = 0;
        }
    }

    public void prevTrack() {
        // check if next song is there or not
        if(currentTrackPosition > 0){
            playTrack(currentTrackPosition - 1);
            currentTrackPosition = currentTrackPosition - 1;
        }else{
            // play last song
            playTrack(songsList.size() - 1);
            currentTrackPosition = songsList.size() - 1;
        }
    }

    public void forward(){
        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
            // forward song
            mediaPlayer.seekTo(currentPosition + seekForwardTime);
        } else {
            // forward to end position
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    public void backward() {
        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if (currentPosition - seekBackwardTime >= 0) {
            // forward song
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        } else {
            // backward to starting position
            mediaPlayer.seekTo(0);
        }
    }
    public int getCurrentTrackProgress() {
        if (status > STOPED) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getCurrentTrackDuration() {
        if (status > STOPED) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public void seekTrack(int p) {
        if (status > STOPED) {
            mediaPlayer.seekTo(p);
            untake();
        }
    }
/*
    public void storeTracklist() {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.onUpgrade(db, 1, 1);
        for (int i = 0; i < tracklist.size(); i++) {
            ContentValues c = new ContentValues();
            c.put(DbOpenHelper.KEY_POSITION, i);
            c.put(DbOpenHelper.KEY_FILE, tracklist.get(i).getPath());
            db.insert(DbOpenHelper.TABLE_NAME, null, c);
        }
        dbOpenHelper.close();
    }

    private void restoreTracklist() {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor c = db.query(DbOpenHelper.TABLE_NAME, null, null, null, null, null, null);
        tracklist.clear();
        while (c.moveToNext()) {
            tracklist.add(new Track(c.getString(1)));
        }
        dbOpenHelper.close();
    }*/



    public class PlayerBinder extends Binder {

        public PlayerService getService() {
            return PlayerService.this;
        }

        public ArrayList<HashMap<String, String>> getTracklist2() {
            songsManager = new SongsManager();
            tracklist = getPlayList();

            return tracklist;
        }

    }

    public class Track {

        private String path, artist, album, year, title, genre;
        private int id, duration;

        public Track(String p) {



            path = p;


            String[] proj = {Audio.Media.ARTIST, Audio.Media.ALBUM, Audio.Media.YEAR, Audio.Media.TITLE, Audio.Media.DURATION, Audio.Media._ID};


            Cursor trackCursor = getContentResolver().query(Audio.Media.EXTERNAL_CONTENT_URI, proj, Audio.Media.DATA+" = '"+path+"' ", null, null);




            trackCursor.moveToNext();



            artist = trackCursor.getString(0);
            album = trackCursor.getString(1);
            year = trackCursor.getString(2);
            title = trackCursor.getString(3);
            duration = Integer.parseInt(trackCursor.getString(4));
            id = Integer.parseInt(trackCursor.getString(5));
        }


        private void scanDirectory2(File directory) {
            if (directory != null) {
                File[] listFiles = directory.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File file : listFiles) {
                        if (file.isDirectory()) {
                            scanDirectory2(file);
                        } else {
                            addSongToList2(file);
                        }

                    }
                }
            }
        }

        private void addSongToList2(File song) {
            if (song.getName().endsWith(mp3Pattern)) {


                HashMap<String, String> songMap = new HashMap<String, String>();
                songMap.put("songTitle",
                        song.getName().substring(0, (song.getName().length() - 4)));
                songMap.put("songPath", song.getPath());

                // Adding each song to SongList
                // songsList.add(songMap);
                tracklist.add(songMap);
                path = song.getPath();
                title= song.getName().substring(0, (song.getName().length() - 4));
                // playerService.addTrack(playerService.new Track(song.getPath()));
                //   addTrack(this.new Track(song.getPath()));
                // addTrack(this.new Track(song.getAbsolutePath()));
            }
        }

        public String getPath() {
            return path;
        }


        public String getTitle() {
            return title;
        }

    }

    public static String formatTrackDuration(int d) {
        String min = Integer.toString((d / 1000) / 60);
        String sec = Integer.toString((d / 1000) % 60);
        if (sec.length() == 1) sec = "0"+sec;
        return min+":"+sec;
    }




    public void setPath(String s){
        subject = s;
        // String path;
        //path =  Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator+subject+ File.separator;
        path =Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator;
        //path = s;
        //Log.v("JinHee", "안녕하세요 하이를 불렀어요 ㅎㅎ 시발");

    }

    public String getPath(){
        return path;
    }


    public void setPlayList(String str) {
        subject = str;
        //String path2 = getPath();
         String path2 = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator+subject+File.separator;
        // String path2 = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MultiPlayer"+ File.separator;
        System.out.println(path2);
        if (path2 != null) {
            File home = new File(path2);
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    System.out.println(file.getAbsolutePath());
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }
                }
            }
        }
        // return songs list array
        //return songsList;
    }

    public ArrayList<HashMap<String, String>> getPlayList() {

















        // return songs list array
        return songsList;
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }

                }
            }
        }
    }

    private void addSongToList(File song) {
        if (song.getName().endsWith(mp3Pattern)) {


            HashMap<String, String> songMap = new HashMap<String, String>();
            songMap.put("songTitle",
                    song.getName().substring(0, (song.getName().length() - 4)));
            songMap.put("songPath", song.getPath());

            // Adding each song to SongList
            songsList.add(songMap);
//            playerService.addTrack(playerService.new Track(song.getPath()));
        }
    }
}
