package org.androidtown.i_keeper_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * 푸시 메시지를 받는 Receiver 정의
 *
 * @author Mike
 *
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver { //GCM 메시지를 조작한다.
    private static final String TAG = "GCMBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {		//상대방이 메시지 보낼때  intent의 부가적인 정보로 사용
        Log.e("GCMBroadcastReceiver","onReceive start");
        /*ComponentName comp = new ComponentName(context.getPackageName(),
                GCMBroadcastReceiver.class.getName());*/
        Intent service = new Intent(context, GCMIntentService.class);
        startWakefulService(context, service);
        setResultCode(Activity.RESULT_OK);
    }

}

