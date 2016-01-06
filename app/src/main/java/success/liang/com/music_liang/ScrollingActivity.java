package success.liang.com.music_liang;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import success.liang.com.music_liang.adapter.MyAdapter;
import success.liang.com.music_liang.bean.songs;
import success.liang.com.music_liang.database.DBservice;
import success.liang.com.music_liang.fragment.Erweima;
import success.liang.com.music_liang.fragment.about;
import success.liang.com.music_liang.service.MyService;
import success.liang.com.music_liang.service.MyService.MyBinder;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    private MyListView lv_songs;
    private MyAdapter myAdapter;
    private SeekBar sb_process;
    private MyBinder myBinder;
    private TextView tv_name;
    private TextView tv_writer;
    private Button btn_start_stop;
    private Button btn_next;
    private Button btn_logo;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private static DBservice dBservice = new DBservice();
    private static List<songs> list = dBservice.getList_data();
    private int currentMusic = 0; // The music that is playing.
    private int currentPosition; //The position of the music is playing.
    private int currentMax;
    private ProgressReceiver progressReceiver;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyBinder) service;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("优米音乐");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        connectToMyService();
        init();
    }
    public void onResume(){
        super.onResume();
        registerReceiver();
        if(myBinder != null){
            if(myBinder.isPlaying()){
                btn_start_stop.setBackgroundResource(R.mipmap.stop);
            }else{
                btn_start_stop.setBackgroundResource(R.mipmap.start);
            }
            myBinder.notifyActivity();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(progressReceiver);
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



    private void connectToMyService() {
        Intent intent = new Intent(ScrollingActivity.this, MyService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void init() {

        lv_songs = (MyListView) findViewById(R.id.lv_songs);
        myAdapter = new MyAdapter(ScrollingActivity.this,list);
        lv_songs.setAdapter(myAdapter);
        lv_songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myBinder.stopPlay();
                currentMusic = position;
                currentPosition = 0;
                myBinder.startPlay(position, currentPosition);
                if (myBinder.isPlaying()) {
                    btn_start_stop.setBackgroundResource(R.mipmap.stop);
                }
            }
        });

        sb_process = (SeekBar) findViewById(R.id.sb_process);
        sb_process.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    myBinder.changeProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btn_logo = (Button) findViewById(R.id.btn_logo);
        btn_logo.setOnClickListener(this);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setOnClickListener(this);

        tv_writer = (TextView) findViewById(R.id.tv_writer);
        tv_writer.setOnClickListener(this);

        btn_start_stop = (Button) findViewById(R.id.btn_start_stop);
        btn_start_stop.setOnClickListener(this);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = new Erweima();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.rl,fragment).commit();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_stop:
                play(currentMusic,R.id.btn_start_stop);
                break;
            case R.id.btn_next:
                myBinder.stopPlay();
                myBinder.toNext();
                break;
            case R.id.btn_logo:
                Intent intent = new Intent(ScrollingActivity.this,DetialActivity.class);
                intent.putExtra(DetialActivity.MUSIC_LENGTH, currentMax);
                intent.putExtra(DetialActivity.CURRENT_MUSIC, currentMusic);
                intent.putExtra(DetialActivity.CURRENT_POSITION, myBinder.getMediaPlayer().getCurrentPosition());
                startActivity(intent);
                break;
            case R.id.fab:
                myBinder.randomPlay();
                break;

        }

    }

    private void play(int currentMusic, int resId) {
        if(myBinder.isPlaying()){
            myBinder.stopPlay();
            btn_start_stop.setBackgroundResource(R.mipmap.start);
        }else{
            myBinder.startPlay(currentMusic,currentPosition);
            btn_start_stop.setBackgroundResource(R.mipmap.stop);
        }
    }
    private void registerReceiver(){
        progressReceiver = new ProgressReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.ACTION_UPDATE_PROGRESS);
        intentFilter.addAction(MyService.ACTION_UPDATE_DURATION);
        intentFilter.addAction(MyService.ACTION_UPDATE_CURRENT_MUSIC);
        registerReceiver(progressReceiver, intentFilter);
    }
    class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(MyService.ACTION_UPDATE_PROGRESS.equals(action)){
                int progress = intent.getIntExtra(MyService.ACTION_UPDATE_PROGRESS, 0);
                if(progress > 0){
                    currentPosition = progress; // Remember the current position
                    sb_process.setProgress(progress / 1000);
                }
            }else if(MyService.ACTION_UPDATE_CURRENT_MUSIC.equals(action)){
                //Retrive the current music and get the title to show on top of the screen.
                currentMusic = intent.getIntExtra(MyService.ACTION_UPDATE_CURRENT_MUSIC, 0);
                //tv_name.setText(musicList.get(currentMusic).getTitle());
                tv_name.setText(list.get(currentMusic).getTitle());
                tv_writer.setText(list.get(currentMusic).getArtist());
            }else if(MyService.ACTION_UPDATE_DURATION.equals(action)){
                //Receive the duration and show under the progress bar
                //Why do this ? because from the ContentResolver, the duration is zero.
                currentMax = intent.getIntExtra(MyService.ACTION_UPDATE_DURATION, 0);
                int max = currentMax / 1000;
                sb_process.setMax(max);
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (((keyCode == KeyEvent.KEYCODE_BACK) ||
                (keyCode == KeyEvent.KEYCODE_HOME))
                && event.getRepeatCount() == 0) {
            dialog_Exit(ScrollingActivity.this);
        }
        return false;

        //end onKeyDown
    }

    public static void dialog_Exit(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定要退出吗?");
        builder.setTitle("提示");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                    }
                });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
}
