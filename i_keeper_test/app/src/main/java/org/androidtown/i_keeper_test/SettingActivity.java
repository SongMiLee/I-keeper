package org.androidtown.i_keeper_test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.CheckBox;

/**
 * Created by 임은지 on 2015-04-02.
 */
public class SettingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        //"pref" 이름을 가지는 Shared Preferences 객체 생성
        SharedPreferences pref = getSharedPreferences("pref", 0);

        CheckBox soundCheck = (CheckBox)findViewById(R.id.soundCheckBox);
        CheckBox vibrateCheck = (CheckBox)findViewById(R.id.vibrateCheckBox);

        //저장된 값들을 불러옵니다.
        Boolean sCheck = pref.getBoolean("soundCheck", false);
        Boolean vCheck = pref.getBoolean("vibrateCheck", true);

        soundCheck.setChecked(sCheck);
        vibrateCheck.setChecked(vCheck);
    }

    @Override
    public void onStop(){
        super.onStop();
        SharedPreferences pref = getSharedPreferences("pref", 0);
        //SharedPreferences 객체를 수정하려면, Editor 객체를 받아와야 한다.
        SharedPreferences.Editor editor = pref.edit();

        CheckBox soundCheck = (CheckBox)findViewById(R.id.soundCheckBox);
        CheckBox vibrateCheck = (CheckBox)findViewById(R.id.vibrateCheckBox);

        editor.putBoolean("soundCheck", soundCheck.isChecked());
        editor.putBoolean("vibrateCheck", vibrateCheck.isChecked());

        editor.apply();
        editor.commit();
    }
}