package org.androidtown.i_keeper_test;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    private AnimationDrawable frameAnimation;
    private ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 컨트롤 ImageView 객체를 가져온다
        view = (ImageView) findViewById(R.id.imageAnimation);

        // animation_list.xml 를 ImageView 백그라운드에 셋팅한다
        view.setBackgroundResource(R.drawable.frame_animation);

        // 이미지를 동작시키기위해  AnimationDrawable 객체를 가져온다.
        frameAnimation = (AnimationDrawable) view.getBackground();
        frameAnimation.start();

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();       // 5 초후 이미지를 닫아버림
            }
        }, 1650);
    }
}



  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}*/