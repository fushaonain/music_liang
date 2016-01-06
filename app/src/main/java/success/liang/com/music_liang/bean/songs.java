package success.liang.com.music_liang.bean;

/**
 * Created by liangx on 2015/11/4.
 */
public class songs {

    private long id;        //id号
    private String title;   //名字
    private String album;   //心情
    private int duration;   //持续时间
    private long size;      //大小
    private String artist;  //作者
    private String url;     //地址
    private String lrc;

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }



    public songs() {
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getArtist() {
        return artist;
    }

    public long getSize() {
        return size;
    }

    public String getAlbum() {
        return album;
    }

    public int getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public songs(long id, String url, String artist, long size, int duration, String album, String title,String lrc) {
        this.id = id;
        this.url = url;
        this.artist = artist;
        this.size = size;
        this.duration = duration;
        this.album = album;
        this.title = title;
        this.lrc = lrc;
    }


}
