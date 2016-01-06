package success.liang.com.music_liang.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.List;

import success.liang.com.music_liang.R;
import success.liang.com.music_liang.ScrollingActivity;
import success.liang.com.music_liang.bean.songs;
import success.liang.com.music_liang.database.DBservice;

/**
 * Created by liangx on 2015/11/19.
 */
public class MyService extends Service {


    private boolean isPlaying = false;
    private Binder myBinder = new MyBinder();



    private MediaPlayer mediaPlayer;

    private int currentMusic;
    private int currentPosition;

    private static DBservice dBservice = new DBservice();
    private static List<songs> musicList = dBservice.getList_data();

    private int currentMode = 3;

    private Notification notification;

    public static final String[] MODE_DESC = {"单曲循环", "列表循环", "随机播放", "顺序播放"};

    public static final int MODE_ONE_LOOP = 0;
    public static final int MODE_ALL_LOOP = 1;
    public static final int MODE_RANDOM = 2;
    public static final int MODE_SEQUENCE = 3;

    public static final String ACTION_UPDATE_PROGRESS = "success.liang.com.music_liang.UPDATE_PROGRESS";
    public static final String ACTION_UPDATE_DURATION = "success.liang.com.music_liang.UPDATE_DURATION";
    public static final String ACTION_UPDATE_CURRENT_MUSIC = "success.liang.com.music_liang.UPDATE_CURRENT_MUSIC";
    public static final String ACTION_UPDATE_LRC = "success.liang.com.music_liang.UPDATE_LRC";
    private static final int updateProgress = 1;
    private static final int updateCurrentMusic = 2;
    private static final int updateDuration = 3;
    private static final int updateLrc = 4;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case updateProgress:
                    toUpdateProgress();
                    break;
                case updateDuration:
                    toUpdateDuration();
                    break;
                case updateCurrentMusic:
                    toUpdateCurrentMusic();
                    break;
                case updateLrc:
                    toUpdateLrc();
                    break;
            }
        }
    };
    private void toUpdateProgress(){
        if(mediaPlayer != null && isPlaying){
            int progress = mediaPlayer.getCurrentPosition();
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_PROGRESS);
            intent.putExtra(ACTION_UPDATE_PROGRESS,progress);
            sendBroadcast(intent);
            handler.sendEmptyMessageDelayed(updateProgress, 1000);
        }
    }
    private void toUpdateLrc(){
        if(mediaPlayer != null && isPlaying){
            String lrc_string = "/data/data/success.liang.com.music_liang/songs/" + musicList.get(currentMusic).getLrc();
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_LRC);
            intent.putExtra(ACTION_UPDATE_LRC, lrc_string);
            sendBroadcast(intent);
        }
    }


    private void toUpdateDuration(){
        if(mediaPlayer != null){
            int duration = mediaPlayer.getDuration();
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_DURATION);
            intent.putExtra(ACTION_UPDATE_DURATION, duration);
            sendBroadcast(intent);
        }
    }

    private void toUpdateCurrentMusic(){
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_CURRENT_MUSIC);
        intent.putExtra(ACTION_UPDATE_CURRENT_MUSIC,currentMusic);
        sendBroadcast(intent);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        initMediaPlayer();
        super.onCreate();
        Intent intent = new Intent(this, ScrollingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification = new Notification.Builder(this)
                .setTicker("UMI")
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("UMI 音乐")
                //.setContentText(musicList.get(currentMusic).getTitle() + " — " + musicList.get(currentMusic).getArtist())
                .setContentText("正在播放")
                .setContentIntent(pendingIntent)
                .getNotification();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1, notification);
    }
    @Override
    public void onDestroy(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                System.out.println("进入onPrepared");
                mediaPlayer.start();
                mediaPlayer.seekTo(currentPosition);
                handler.sendEmptyMessage(updateDuration);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.out.println("进入playback");
                if (isPlaying) {
                    switch (currentMode) {
                        case MODE_ONE_LOOP:
                            mediaPlayer.start();
                            break;
                        case MODE_ALL_LOOP:
                            play((currentMusic + 1) % musicList.size(), 0);
                            break;
                        case MODE_RANDOM:
                            play(getRandomPosition(), 0);
                            break;
                        case MODE_SEQUENCE:
                            if (currentMusic < musicList.size() - 1) {
                                playNext();
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                try {
                    System.out.println("进入error");
                    mediaPlayer.stop();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private int getRandomPosition(){
        int random = (int)(Math.random() * (musicList.size() - 1));
        return random;
    }
    private void play(int currentMusic, int pCurrentPosition) {
        currentPosition = pCurrentPosition;
        setCurrentMusic(currentMusic);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource("/data/data/success.liang.com.music_liang/songs/" + musicList.get(currentMusic).getUrl());
            handler.sendEmptyMessage(updateLrc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        handler.sendEmptyMessage(updateProgress);

        isPlaying = true;
    }
    private void setCurrentMusic(int pCurrentMusic){
        currentMusic = pCurrentMusic;
        handler.sendEmptyMessage(updateCurrentMusic);
    }
    private void stop(){
        //mediaPlayer.stop();
        mediaPlayer.pause();
        isPlaying = false;
    }
    private void endPlay(){
        mediaPlayer.stop();
        //mediaPlayer.pause();
        isPlaying = false;
    }
    private void playNext(){
        System.out.println(currentMusic);
        switch(currentMode){
            case MODE_ONE_LOOP:
                play(currentMusic, 0);
                break;
            case MODE_ALL_LOOP:
                if(currentMusic + 1 == musicList.size()){
                    play(0,0);
                }else{
                    play(currentMusic + 1, 0);
                }
                break;
            case MODE_SEQUENCE:
                if(currentMusic + 1 == musicList.size()){
                    Toast.makeText(this, "No more song.", Toast.LENGTH_SHORT).show();
                }else{
                    currentMusic++;
                    play(currentMusic, 0);
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition(), 0);
                break;
        }
    }

    private void playPrevious(){
        switch(currentMode){
            case MODE_ONE_LOOP:
                play(currentMusic, 0);
                break;
            case MODE_ALL_LOOP:
                if(currentMusic <= 0){
                    play(musicList.size() - 1, 0);
                }else{
                    play(currentMusic - 1, 0);
                }
                break;
            case MODE_SEQUENCE:
                if(currentMusic <= 0){
                    Toast.makeText(this, "No previous song.", Toast.LENGTH_SHORT).show();
                }else{
                    play(currentMusic - 1, 0);
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition(), 0);
                break;
        }
    }
    public class MyBinder extends Binder{

        public void startPlay(int currentMusic, int currentPosition){
            play(currentMusic,currentPosition);
        }

        public void stopPlay(){
            stop();
        }

        public void randomPlay(){
            play(getRandomPosition(), 0);
        }

        public void toNext(){
            playNext();
        }
        public void end(){
            endPlay();
        }

        public void toPrevious(){
            playPrevious();
        }
        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }

        /**
         * MODE_ONE_LOOP = 1;
         * MODE_ALL_LOOP = 2;
         * MODE_RANDOM = 3;
         * MODE_SEQUENCE = 4;
         */
        public void changeMode(){
            currentMode = (currentMode + 1) % 4;
            Toast.makeText(MyService.this, MODE_DESC[currentMode], Toast.LENGTH_SHORT).show();
        }

        /**
         * return the current mode
         * MODE_ONE_LOOP = 1;
         * MODE_ALL_LOOP = 2;
         * MODE_RANDOM = 3;
         * MODE_SEQUENCE = 4;
         * @return
         */
        public int getCurrentMode(){
            return currentMode;
        }

        /**
         * The service is playing the music
         * @return
         */
        public boolean isPlaying(){
            return isPlaying;
        }

        /**
         * Notify Activities to update the current music and duration when current activity changes.
         */
        public void notifyActivity(){
            toUpdateCurrentMusic();
            toUpdateDuration();
        }

        /**
         * Seekbar changes
         * @param progress
         */
        public void changeProgress(int progress){
            if(mediaPlayer != null){
                currentPosition = progress * 1000;
                if(isPlaying){
                    mediaPlayer.seekTo(currentPosition);
                }else{
                    play(currentMusic, currentPosition);
                }
            }
        }

    }

}
