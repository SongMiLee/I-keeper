package org.androidtown.i_keeper_test;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * 푸시 메시지를 받는 Receiver 정의
 *
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "GCMBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) { //상대방이 메시지 보낼 때 intent의 부가적인 정보로 사용
        String action = intent.getAction();
        Log.d(TAG, "action : " + action);

        if(action != null){
            if(action.equals("com.google.android.c2dm.intent.RECEIVE")){

            }
        }
    }
}
