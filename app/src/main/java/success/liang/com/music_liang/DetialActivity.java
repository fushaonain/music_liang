package success.liang.com.music_liang;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import success.liang.com.music_liang.bean.FormatHelper;
import success.liang.com.music_liang.bean.LrcView;
import success.liang.com.music_liang.bean.songs;
import success.liang.com.music_liang.database.DBservice;
import success.liang.com.music_liang.fragment.about;
import success.liang.com.music_liang.service.MyService;

public class DetialActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String MUSIC_LENGTH = "success.liang.com.music_liang.DetialActivity.MUSIC_LENGTH";
    public static final String CURRENT_POSITION = "success.liang.com.music_liang.DetialActivity.CURRENT_POSITION";
    public static final String CURRENT_MUSIC = "success.liang.com.music_liang.DetialActivity.CURRENT_MUSIC";

    private static DBservice dBservice = new DBservice();
    private static List<songs> musicList = dBservice.getList_data();

    private MyService.MyBinder myBinder;

    private ProgressReceiver progressReceiver;

    private Button btn_startstop;
    private TextView tv_left;
    private TextView tv_right;
    private Toolbar toolbar;
    private SeekBar sb_process_two;
    private Button btn_right;
    private Button btn_left;
    private LrcView mLrc;
    private LinearLayout linearLayout;
    private Button btn_about;
    private Button btn_mode;

    private int[] back = {R.mipmap.detial_back_seven,R.mipmap.detial_back_six,R.mipmap.detial_back_five,R.mipmap.detial_back_four,R.mipmap.detial_back_three,R.mipmap.detial_back_two,R.mipmap.detial_back_one};
    private int back_int = 0;
    private static final int TIME_BACK = 30000;
    private static final int UPDATE_BACK = 110;
    private String lrc_string = "/data/data/success.liang.com.music_liang/songs/" + musicList.get(0).getLrc();

    private String mDir = Environment.getExternalStorageDirectory() + File.separator + "Download" + File.separator;

    private int currentMusic;
    private int currentPosition;

    private int[] mode = {R.mipmap.detial_danqu,R.mipmap.detial_xunhuan,R.mipmap.detial_random,R.mipmap.detial_shunxu};

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case UPDATE_BACK:
                    update();
                    break;
            }
        }
    };
    private void update(){
        linearLayout.setBackgroundResource(back[back_int]);
        if(back_int == 6){
            back_int = 0;
        }else
            back_int++;
        handler.sendEmptyMessageDelayed(UPDATE_BACK, TIME_BACK);


    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyService.MyBinder) service;
            if(myBinder.isPlaying()){
                btn_startstop = (Button) findViewById(R.id.btn_detial_startstop);
                btn_startstop.setBackgroundResource(R.mipmap.detial_start);
                //btn_startstop.setFlagStart(false);
                btn_mode.setBackgroundResource(mode[myBinder.getCurrentMode()]);
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial);
        this.getSupportActionBar().hide();

        linearLayout = (LinearLayout) findViewById(R.id.ll_detial);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        overridePendingTransition(R.anim.push_right_in, R.anim.hold);

        connectToNatureService();

        init();

        initLrc();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initLrc() {
        mLrc = (LrcView) findViewById(R.id.lrc);

    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(progressReceiver);
        overridePendingTransition(R.anim.hold, R.anim.push_right_out);
    }

    @Override
    public void onResume(){
        super.onResume();
        initReceiver();
    }


    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(myBinder != null){
            unbindService(serviceConnection);
        }
    }

    private void initReceiver() {
        progressReceiver = new ProgressReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.ACTION_UPDATE_PROGRESS);
        intentFilter.addAction(MyService.ACTION_UPDATE_DURATION);
        intentFilter.addAction(MyService.ACTION_UPDATE_CURRENT_MUSIC);
        intentFilter.addAction(MyService.ACTION_UPDATE_LRC);
        registerReceiver(progressReceiver, intentFilter);
    }

    private void init() {
        currentMusic = getIntent().getIntExtra(CURRENT_MUSIC,0);

        toolbar.setTitle(musicList.get(currentMusic).getTitle());
        toolbar.setSubtitle(musicList.get(currentMusic).getArtist());

        tv_right = (TextView) findViewById(R.id.tv_right);
        int max = getIntent().getIntExtra(MUSIC_LENGTH, 0);
        tv_right.setText(FormatHelper.formatDuration(max));

        sb_process_two = (SeekBar) findViewById(R.id.sb_process_two);
        sb_process_two.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    myBinder.changeProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sb_process_two.setMax(max / 1000);

        currentPosition = getIntent().getIntExtra(CURRENT_POSITION,0);
        sb_process_two.setProgress(currentPosition / 1000);

        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_left.setText(FormatHelper.formatDuration(currentPosition));

        btn_startstop = (Button) findViewById(R.id.btn_detial_startstop);
        btn_startstop.setOnClickListener(this);

        btn_right = (Button) findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);

        btn_left = (Button) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);

        btn_about = (Button) findViewById(R.id.btn_about);
        btn_about.setOnClickListener(this);

        btn_mode = (Button)findViewById(R.id.btn_mode);
        btn_mode.setOnClickListener(this);

        handler.sendEmptyMessageDelayed(UPDATE_BACK,TIME_BACK);

    }

    private void connectToNatureService(){
        Intent intent = new Intent(DetialActivity.this, MyService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_detial_startstop:
                play(currentMusic,R.id.btn_detial_startstop);
                break;
            case R.id.btn_right:
                myBinder.stopPlay();
                myBinder.toNext();
                break;
            case R.id.btn_left:
                myBinder.stopPlay();
                myBinder.toPrevious();
                break;
            case R.id.btn_about:
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = new about();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.ll_detial,fragment).commit();
                break;
            case R.id.btn_mode:
                myBinder.changeMode();
                btn_mode.setBackgroundResource(mode[myBinder.getCurrentMode()]);
                break;

        }


    }
    private void play(int currentMusic, int resId){
        if(myBinder.isPlaying()){
            myBinder.stopPlay();
            btn_startstop.setBackgroundResource(R.mipmap.detial_stop);
        }else{
            myBinder.startPlay(currentMusic,currentPosition);
            btn_startstop.setBackgroundResource(R.mipmap.detial_start);
        }
    }

    class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(MyService.ACTION_UPDATE_PROGRESS.equals(action)){
                int progress = intent.getIntExtra(MyService.ACTION_UPDATE_PROGRESS, currentPosition);
                lrc_string = "/data/data/success.liang.com.music_liang/songs/" + musicList.get(currentMusic).getLrc();
                try {
                    mLrc.setLrcPath(lrc_string);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mLrc.changeCurrent(progress);
                if(progress > 0){
                    currentPosition = progress; // Remember the current position
                    tv_left.setText(FormatHelper.formatDuration(progress));
                    sb_process_two.setProgress(progress / 1000);

                }
            }else if(MyService.ACTION_UPDATE_CURRENT_MUSIC.equals(action)){
                //Retrieve the current music and get the title to show on top of the screen.
                currentMusic = intent.getIntExtra(MyService.ACTION_UPDATE_CURRENT_MUSIC, 0);
                toolbar.setTitle(musicList.get(currentMusic).getTitle());
                toolbar.setSubtitle(musicList.get(currentMusic).getArtist());
                lrc_string = "/data/data/success.liang.com.music_liang/songs/" + musicList.get(currentMusic).getLrc();
                try {
                    mLrc.setLrcPath(lrc_string);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(MyService.ACTION_UPDATE_LRC.equals(action)){
                //Retrieve the current music and get the title to show on top of the screen.
                String lrc_ = intent.getStringExtra(MyService.ACTION_UPDATE_CURRENT_MUSIC);
                lrc_string = lrc_;
                try {
                    mLrc.startLrc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onResume();
            }else if(MyService.ACTION_UPDATE_DURATION.equals(action)){
                //Receive the duration and show under the progress bar
                //Why do this ? because from the ContentResolver, the duration is zero.
                int duration = intent.getIntExtra(MyService.ACTION_UPDATE_DURATION, 0);
                tv_right.setText(FormatHelper.formatDuration(duration));
                sb_process_two.setMax(duration / 1000);
            }
        }

    }



}
