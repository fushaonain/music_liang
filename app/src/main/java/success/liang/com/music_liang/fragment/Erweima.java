package success.liang.com.music_liang.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import success.liang.com.music_liang.R;

/**
 * Created by liangx on 2015/11/28.
 */
public class Erweima extends Fragment{

    private ImageView iv_erweima;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.erweima,container,false);

        iv_erweima = (ImageView) root.findViewById(R.id.iv_erweima);
        iv_erweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return root;
    }
}
