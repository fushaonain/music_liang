package success.liang.com.music_liang.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import success.liang.com.music_liang.bean.songs;

/**
 * Created by liangx on 2015/11/20.
 */
public class DBservice {

    private SQLiteDatabase db;


    public DBservice() {
        this.db = SQLiteDatabase.openDatabase("/data/data/success.liang.com.music_liang/databases/music.db",null,SQLiteDatabase.OPEN_READWRITE);
    }

    public List<songs> getList_data(){

        List<songs> list = new ArrayList<songs>();

        Cursor cursor = db.rawQuery("select * from music",null);
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            int count = cursor.getCount();
            for (int i = 0;i < count;i++)
            {
                cursor.moveToPosition(i);
                songs list_data = new songs();
                list_data.setId(cursor.getInt(cursor.getColumnIndex("id")));
                list_data.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                list_data.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
                list_data.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                list_data.setSize(cursor.getInt(cursor.getColumnIndex("size")));
                list_data.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
                list_data.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                list_data.setLrc(cursor.getString(cursor.getColumnIndex("lrc")));
                list.add(list_data);
            }
        }
        return list;

    }
}
