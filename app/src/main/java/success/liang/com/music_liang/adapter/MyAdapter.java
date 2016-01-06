package success.liang.com.music_liang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import success.liang.com.music_liang.R;
import success.liang.com.music_liang.bean.songs;

/**
 * Created by liangx on 2015/11/5.
 */
public class MyAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private List<songs> songsList = new ArrayList<songs>();


    public MyAdapter(Context context,List<songs> songsList){

        this.mInflater = LayoutInflater.from(context);
        this.songsList = songsList;
    }

    public MyAdapter(Context context){

        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return songsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.items_one, null);
        }

        songs song = songsList.get(position);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_songs_name);
        textView.setText(song.getTitle() + " — " + song.getArtist());
        return convertView;
    }
}
