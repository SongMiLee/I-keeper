package org.androidtown.i_keeper_test;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by 임은지 on 2015-04-02.
 */
public class SettingActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Setting");
    }
}