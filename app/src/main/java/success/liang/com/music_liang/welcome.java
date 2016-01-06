package success.liang.com.music_liang;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import success.liang.com.music_liang.bean.songs;
import success.liang.com.music_liang.database.DBservice;

public class welcome extends AppCompatActivity {

    private List<songs> list;
    private int[] array = {R.raw.a,R.raw.b,R.raw.c,R.raw.d,R.raw.e,R.raw.f,R.raw.g,R.raw.h
            ,R.raw.i,R.raw.j,R.raw.k,R.raw.l,R.raw.m};
    private int[] array_lrc = {R.raw.aa,R.raw.bb,R.raw.cc,R.raw.dd,R.raw.ee,R.raw.ff,R.raw.gg,R.raw.hh
            ,R.raw.ii,R.raw.jj,R.raw.kk,R.raw.ll,R.raw.mm};
    private static final int TIME = 3000;
    private static final int GO_HOME = 1000;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case GO_HOME:
                    goHome();
                    break;
            }
        }
    };

    private void goHome() {
        startActivity(new Intent(welcome.this,ScrollingActivity.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportActionBar().hide();

        initSQ();
        DBservice dBservice = new DBservice();
        list = dBservice.getList_data();
        initSONGS();
        init();
        //startActivity(new Intent(welcome.this,ScrollingActivity.class));
    }

    private void init() {
        handler.sendEmptyMessageDelayed(GO_HOME,TIME);
    }

    private void initSONGS() {
        String SONGS_PATH = "/data/data/success.liang.com.music_liang/songs/";
        for(int i = 0;i < list.size();i++){
            String SONGS_NAME = list.get(i).getUrl();
            if ((new File(SONGS_PATH,SONGS_NAME).exists()) == false)
            {
                File dir = new File(SONGS_PATH);
                if (!dir.exists())
                {
                    dir.mkdir();
                }

                try {
                    /*InputStream is = this.getResources().openRawResource(
                            );*/
                    InputStream is = this.getResources().openRawResource(array[i]);
                    FileOutputStream  os = new FileOutputStream(SONGS_PATH + SONGS_NAME);
                    byte[] buffer = new byte[2014];
                    int length;

                    while ((length = is.read(buffer)) > 0)
                    {
                        os.write(buffer,0,length);
                    }
                    os.flush();
                    os.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for(int i = 0;i < list.size();i++){
            String SONGS_NAME = list.get(i).getLrc();
            if ((new File(SONGS_PATH,SONGS_NAME).exists()) == false)
            {
                File dir = new File(SONGS_PATH);
                if (!dir.exists())
                {
                    dir.mkdir();
                }

                try {
                    /*InputStream is = this.getResources().openRawResource(
                            );*/
                    InputStream is = this.getResources().openRawResource(array_lrc[i]);
                    FileOutputStream  os = new FileOutputStream(SONGS_PATH + SONGS_NAME);
                    byte[] buffer = new byte[2014];
                    int length;

                    while ((length = is.read(buffer)) > 0)
                    {
                        os.write(buffer,0,length);
                    }
                    os.flush();
                    os.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initSQ() {
        String DB_PATH = "/data/data/success.liang.com.music_liang/databases/";
        String DB_NAME = "music.db";

        if ((new File(DB_PATH,DB_NAME).exists()) == false)
        {
            File dir = new File(DB_PATH);
            if (!dir.exists())
            {
                dir.mkdir();
            }

            try {
                InputStream is = getBaseContext().getAssets().open(DB_NAME);
                OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                byte[] buffer = new byte[2014];
                int length;

                while ((length = is.read(buffer)) > 0)
                {
                    os.write(buffer,0,length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
