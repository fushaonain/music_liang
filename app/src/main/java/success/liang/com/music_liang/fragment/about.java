package success.liang.com.music_liang.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import success.liang.com.music_liang.R;

/**
 * Created by liangx on 2015/11/28.
 */
public class about extends Fragment{

    private Button btn_about;
    private ImageView iv_logo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_about,container,false);
        btn_about = (Button) root.findViewById(R.id.btn_about);
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        iv_logo = (ImageView) root.findViewById(R.id.iv_welcome_logo);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.animation);
        iv_logo.startAnimation(hyperspaceJumpAnimation);
        return root;
    }
}
