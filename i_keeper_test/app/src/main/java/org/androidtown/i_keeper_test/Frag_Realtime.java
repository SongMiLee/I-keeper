package org.androidtown.i_keeper_test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


        Thread worker = new Thread() {
            public void run() {
                // 메인 스레드가 처리해야 할 루틴

                //HttpClient 인스턴스 생성
                HttpClient client=new DefaultHttpClient();
                // HTTP 메서드의 인스턴스 생성
                // URL과 함께 HttpClient 컴포넌트에서 제공하는 HTTP 메서드의 클래스의 생성자를 사용하여
                // HTTP 의 요청라인을 지원하는 HTTP 메시지의 인스턴스를 생성한다.
                HttpPost getMethod=new HttpPost("http://alert-height-91305.appspot.com/hello");

                try{
                    // 서버에 시간데이터를 보내고 해당 시간에 대한 센서값들을 얻어 온다.
                    String inTime = new SimpleDateFormat("HH:mm:ss").format(new java.util.Date());  // 현재 시간을 얻어옴(시,분,초)
                    String []tmp = inTime.split(":");
                    int time_hour = Integer.parseInt(tmp[0]);           // 현재 시간(시)
                    int time_minute = Integer.parseInt(tmp[1]);     // 현재 시간(분)
                    output.setText("현재시간  : "+time_hour+"시 "+time_minute+"분");

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
                    System.out.println("arr : "+ arr);
<<<<<<< HEAD
=======
                    t_sensor1.setText(arr.get(0));
                    t_sensor2.setText(arr.get(1));
                    t_sensor3.setText(arr.get(2));
                    t_sensor4.setText(arr.get(3));
                    t_sensor5.setText(arr.get(4));
                    t_sensor6.setText(arr.get(5));
>>>>>>> 99dd66c7282d17eb417dd214bc846cf1558e1586

                } catch(Exception e){
                    e.printStackTrace();
                }

                mHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            t_sensor1.setText(arr.get(0));
                            t_sensor2.setText(arr.get(1));
                            t_sensor3.setText(arr.get(2));
                            t_sensor4.setText(arr.get(3));
                            t_sensor5.setText(arr.get(4));
                            t_sensor6.setText(arr.get(5));


                        }
                });

            }
        };
        worker.start();



        return rootView;
        //  return inflater.inflate(R.layout.frag_realtime, container, false);
    }
    //@Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);

        super.onPause();
    }

}