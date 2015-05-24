package org.androidtown.i_keeper_test;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Realtime extends Fragment {


    TextView p_sensor_result1;
    TextView user_status_result;
    String str;

    //유저 정보
    UserInfo user;
    String user_id ="1st";
    //
    String readData="";
    ArrayList<String> arr;
    ArrayList<String> pulse_sensor;
    ArrayList<String> user_status ;

    //
    private boolean isRunning = false;

    //
    //
    Handler handler;
    ProgressRunnable runnable;
    //
    String[] temp5;

    //
    public Frag_Realtime() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView =  inflater.inflate(R.layout.frag_realtime, container, false);
        user=new UserInfo();

        p_sensor_result1 = (TextView) rootView.findViewById(R.id.p_sensor_result1);
        p_sensor_result1.setText("");
        user_status_result = (TextView)rootView.findViewById(R.id.user_status);

        handler = new Handler();
        runnable = new ProgressRunnable();
        return rootView;

    }
    //
    //@Override
    public void onStart() {
        super.onStart();

        //스레드 시작
        Thread thread1 = new Thread(new Runnable(){
            public void run(){
                while(isRunning) {

                    try {
                        Thread.sleep(2000);
                        arr = new ArrayList<String>();
                        pulse_sensor = new ArrayList<String>();
                        user_status = new ArrayList<String>();


                        //HttpClient 인스턴스 생성
                        HttpClient client = new DefaultHttpClient();
                        // HTTP 메서드의 인스턴스 생성
                        // URL과 함께 HttpClient 컴포넌트에서 제공하는 HTTP 메서드의 클래스의 생성자를 사용하여
                        // HTTP 의 요청라인을 지원하는 HTTP 메시지의 인스턴스를 생성한다.
                        HttpPost getMethod = new HttpPost("http://alert-height-91305.appspot.com/hello");

                        ArrayList<NameValuePair> NameValuePairs = new ArrayList<NameValuePair>();
                        //보낼 값을 ArrayList에 키와 값으로 저장을 한다.
                        NameValuePairs.add(new BasicNameValuePair("id", user_id));
                        NameValuePairs.add(new BasicNameValuePair("Mode", "SensorValue_Request"));
                        getMethod.setEntity(new UrlEncodedFormEntity(NameValuePairs));
                        //값을 보낸다.
                        HttpResponse response = null;
                        try {
                            response = client.execute(getMethod);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //값을 읽기 위한 cbr을 생성
                        Thread.sleep(2000);

                        BufferedReader cbr = null;
                        try {
                            cbr = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            while ((readData = cbr.readLine()) != null) {
                                arr.add(readData);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //   System.out.println(arr);

                        client.getConnectionManager().shutdown();

                    } catch (Exception ex) {
                        Log.e("SampleThreadActivity", "Exception in processing message", ex);
                    }

                    //  System.out.println(arr.get(0)+"/"+arr.get(1));
                    ////////////////////////////////////////////////////////////////////////////////////////////////// data  processing start

                    if(arr.get(0).equals("error")){
                        pulse_sensor.add("정보를 받아오는 중입니다");
                        pulse_sensor.add("정보를 받아오는 중입니다");
                        user_status.add("정보를 받아오는 중입니다");
                        user_status.add("정보를 받아오는 중입니다");
                       /* temp1 = arr.get(i).split("=");
                        left_t_sensor1.add(temp1[1]);*/
                    }
                    else {
                        temp5 = arr.get(0).split("=");
                        for (int i = 0; i < temp5.length; i++) {
                            pulse_sensor.add(temp5[1]);
                        }

                        temp5 = arr.get(1).split("=");
                        for (int i = 0; i < temp5.length; i++) {
                            user_status.add(temp5[1]);
                        }


                    }
////////////////////////////////////////////////////////////////////////////////////////////////  data processing end

                    handler.post(runnable);
              /*      arr.clear();
                    left_t_sensor1.clear();*/
                }
            }

        });
        isRunning = true;
        thread1.start();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void onPause(){
        super.onPause();
        isRunning = false;
    }
    public void onStop(){
        super.onStop();

        isRunning = false;
    }

    // Runnable 인터페이스를 구현하는 새로운 클래스 정의
    public class ProgressRunnable implements  Runnable{
        public void run(){
            //            p_sensor_result1.append(readData+"\n");
            String temp = "";
            if(user_status.get(1).equals("FALSE")) {
                temp = "안정";
            }
            else if(user_status.get(1).equals("TRUE")){
                temp = "위험";
            }
            else{
                temp = "정보를 받아오는 중입니다.";
            }

            user_status_result.setText(temp+"\n");
            p_sensor_result1.setText("    "+pulse_sensor.get(1)+"\n");
 /*           "\n"+"                   현재 아이의 스트레스 지수 : \n"
                    +"                               "+*/
        }
    }
}