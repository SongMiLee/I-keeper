package org.androidtown.i_keeper_test;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GCMIntentService extends IntentService{

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent start");
        Bundle extras = intent.getExtras();
        //GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.

        Log.e(TAG, "onHandleIntent after make Google Cloud Messaging");
        //이 getMessageType() intent parameter은 반드시 BroadcastReceiver에서 당신이
        //받는 매개변수가 되어야 한다.
        //String 변수! getMessageTag로 intent에 저장되어 있는 messageType을 구한다.
        String msg = intent.getExtras().getString("info");
        sendNotification("Warning!");
        Log.i("GcmIntentService", "Received: " + msg);
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent); //스마트폰 정지 상태일 때 깨운다.

    }

    private void sendNotification(String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);

        //설정 창과 동기화 하기 위한 SharedPreference 파일
        SharedPreferences pref = getSharedPreferences("pref", 0);

        //저장된 값들을 불러옵니다.
        Boolean sCheck = pref.getBoolean("soundCheck", false);
        Boolean vCheck = pref.getBoolean("vibrateCheck", true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), MonitorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", msg);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Monitor Your Child!!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true);

        if(sCheck == true)
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        if(vCheck == true)
            mBuilder.setVibrate(new long[] {0, 500});

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }
}
