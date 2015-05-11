﻿package org.androidtown.i_keeper_test;


import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Realtime extends Fragment {


    TextView output;
    TextView t_sensor1, t_sensor2,t_sensor3,t_sensor4,t_sensor5,t_sensor6;
    String str;
    int port=9999;
    private Socket socket;
    BufferedReader socket_in;       //reader
    PrintWriter socket_out;     //writer
    //유저 정보
    UserInfo user;

    //
    String readData="";
    ArrayList<String> arr = new ArrayList<String>();
    //

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
//
    private boolean running = false;
    ProgressHandler handler;        //객체 생성
    //
    // 메인 스레드가 처리해야 할 루틴

    //HttpClient 인스턴스 생성
    HttpClient client=new DefaultHttpClient();
    // HTTP 메서드의 인스턴스 생성
    // URL과 함께 HttpClient 컴포넌트에서 제공하는 HTTP 메서드의 클래스의 생성자를 사용하여
    // HTTP 의 요청라인을 지원하는 HTTP 메시지의 인스턴스를 생성한다.
    HttpPost getMethod=new HttpPost("http://alert-height-91305.appspot.com/hello");

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
        output = (TextView) rootView.findViewById(R.id.output);
        t_sensor1 = (TextView) rootView.findViewById(R.id.t_sensor1);
        t_sensor2 = (TextView) rootView.findViewById(R.id.t_sensor2);
        t_sensor3 = (TextView) rootView.findViewById(R.id.t_sensor3);
        t_sensor4 = (TextView) rootView.findViewById(R.id.t_sensor4);
        t_sensor5 = (TextView) rootView.findViewById(R.id.t_sensor5);
        t_sensor6 = (TextView) rootView.findViewById(R.id.t_sensor6);

        handler = new ProgressHandler();

        return rootView;
        //  return inflater.inflate(R.layout.frag_realtime, container, false);
    }

    public void onStart(){
        super.onStart();
        Thread thread1 = new Thread(new Runnable(){
            public void run(){

                try{

                    ArrayList<NameValuePair> NameValuePairs=new ArrayList<NameValuePair>(1);
                    //보낼 값을 ArrayList에 키와 값으로 저장을 한다.
                    NameValuePairs.add(new BasicNameValuePair("Mode","TouchSensor_Request"));
                    getMethod.setEntity(new UrlEncodedFormEntity(NameValuePairs));
                    //값을 보낸다.
                    HttpResponse response = client.execute(getMethod);

                    //값을 읽기 위한 cbr을 생성
                    BufferedReader cbr=new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));
                    while((readData=cbr.readLine())!=null){
                        arr.add("value : "+readData);
                    }
                    System.out.println("arr : " + arr);

                    //

                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);

                    //

                } catch(Exception e){
                    e.printStackTrace();
                }

            }

        });

        thread1.start();
    }


    //@Override
    public void onStop() {
        super.onStop();

        running = false;

    }

    public class ProgressHandler extends Handler{ //Handler 클래스를 상속하여 새로운 핸들러 클래스를 정의
        public void handleMessage(Message msg){
            // 여기서 setTExt
            t_sensor1.setText(arr.get(0));
            t_sensor2.setText(arr.get(1));
            t_sensor3.setText(arr.get(2));
            t_sensor4.setText(arr.get(3));
            t_sensor5.setText(arr.get(4));
            t_sensor6.setText(arr.get(5));

        }
    }
}