package success.liang.com.music_liang;

import android.content.Context;
import android.widget.ListView;

/**
 * Created by liangx on 2015/11/18.
 */
public class MyListView extends ListView {
    public MyListView(Context context,android.util.AttributeSet attrs) {
        super(context,attrs);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);

    }
}
