package org.androidtown.i_keeper_test;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Monitor extends Fragment {
    //for network

    BufferedReader socket_in;       //reader
    PrintWriter socket_out;     //writer

    //
    TextView output;
    Button btn_refresh;
    String str;

    //
    GraphView graph;
    //
    String readData="";
    ArrayList<String> arr = new ArrayList<String>();

    ArrayList<Double> darr = new ArrayList<Double>();

    ArrayList<String> left_t_sensor1 = new ArrayList<String>();
    ArrayList<String> left_t_sensor2 = new ArrayList<String>();
    ArrayList<String> right_t_sensor1 = new ArrayList<String>();
    ArrayList<String> right_t_sensor2 = new ArrayList<String>();
    ArrayList<String> pulse_sensor = new ArrayList<String>();
    ArrayList<String> user_status = new ArrayList<String>();


    //
    int temp=0;
    // Graph
    public final Handler mHandler = new Handler();
    public Runnable mTimer1;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean running = false;
    ProgressHandler handler;        //객체 생성
    //
    //HttpClient 인스턴스 생성
    public  HttpClient client=new DefaultHttpClient();
    // HTTP 메서드의 인스턴스 생성
    // URL과 함께 HttpClient 컴포넌트에서 제공하는 HTTP 메서드의 클래스의 생성자를 사용하여
    // HTTP 의 요청라인을 지원하는 HTTP 메시지의 인스턴스를 생성한다.
    public   HttpPost getMethod=new HttpPost("http://alert-height-91305.appspot.com/hello");

    //
    public LineGraphSeries<DataPoint> mSeries1;
    public LineGraphSeries<DataPoint> mSeries2;
    public LineGraphSeries<DataPoint> pulse_value;

    public double graph2LastXValue = 5d;


    //
    public Frag_Monitor() {
        // Required empty public constructor
    }
    //유저 정보
    UserInfo user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.frag_monitor, container, false);
        user=new UserInfo();
        output = (TextView) rootView.findViewById(R.id.output);
        btn_refresh = (Button) rootView.findViewById(R.id.btn_refresh);

        //
        handler = new ProgressHandler();

        //
        // Inflate the layout for this fragment
        graph = (GraphView) rootView.findViewById(R.id.graph);



        //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Thread temp_thread = new Thread();
        temp_thread.start();



//
        return rootView;
    }

    //@Override
    public void onStart(){
        super.onStart();
        //
        // 메인 스레드가 처리해야 할 루틴

      /*  Thread thread1 = new Thread(new Runnable(){
            public void run(){

                try{

                    ArrayList<NameValuePair> NameValuePairs=new ArrayList<NameValuePair>();
                    //보낼 값을 ArrayList에 키와 값으로 저장을 한다.
                    NameValuePairs.add(new BasicNameValuePair("id","1st"));
                    NameValuePairs.add(new BasicNameValuePair("Mode","SensorValue_Request"));
                       Thread.sleep(1000);
                    temp++;
                    System.out.println(temp);
                    getMethod.setEntity(new UrlEncodedFormEntity(NameValuePairs));
                    //값을 보낸다.
                    HttpResponse response = client.execute(getMethod);


                    //값을 읽기 위한 cbr을 생성
                    BufferedReader cbr=new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));

                    while((readData=cbr.readLine())!=null){
                        arr.add(readData);
                        System.out.println(readData);
                    }
////////////////////////////////////////////////////////////////////////////////////////////////// data  processing start

                    String[] temp1=arr.get(0).split(" ");
                    for(int i=0 ; i<temp1.length ; i++){
                        left_t_sensor1.add(temp1[i]);
                    }


                    String[] temp2=arr.get(1).split(" ");
                    for(int i=0 ; i<temp2.length ; i++){
                        left_t_sensor2.add(temp2[i]);
                    }



                    String[] temp3=arr.get(2).split(" ");
                    for(int i=0 ; i<temp3.length ; i++){
                        right_t_sensor1.add(temp3[i]);
                    }

                    String[] temp4=arr.get(3).split(" ");
                    for(int i=0 ; i<temp4.length ; i++){
                        right_t_sensor2.add(temp4[i]);
                    }

                    String[] temp5=arr.get(4).split(" ");
                    for(int i=0 ; i<temp5.length ; i++){
                        pulse_sensor.add(temp5[i]);
                    }

                    String[] temp6=arr.get(5).split(" ");
                    for(int i=0 ; i<temp6.length ; i++){
                        user_status.add(temp6[i]);
                    }
////////////////////////////////////////////////////////////////////////////////////////////////  data processing end
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);

                } catch(Exception e){
                    e.printStackTrace();
                }

            }

        });

        thread1.start();
*/
    }

    //@Override
    public void onResume() {
        super.onResume();

        running = true;

        Thread temp_thread = new BackgroundThread();
        temp_thread.start();
    }

    //@Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        running = false;
        super.onPause();
    }


    //@Override
    public void onStop(){
        super.onStop();

        running = false;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////  Graph Generate Processing Start
    // 120 -> 200
    // 20->20     /       40 -> 40
    // 60 -> 80
    private DataPoint[] generateData() {
        int count = 40;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;

            //           double y = darr.get(i)*0.1;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
    //
    private DataPoint[] generateData2() {
        int count = 40;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y=1.0;
           /* double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;*/
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    private DataPoint[] pulse_generateData() {
        int count = 40;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y=2.0;

            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////  //   Graph Generate Processing End
    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }


    public class BackgroundThread extends Thread{
        public void run(){
            while(running){
                try{

                    // 수행할 내용

                    ArrayList<NameValuePair> NameValuePairs=new ArrayList<NameValuePair>();
                    //보낼 값을 ArrayList에 키와 값으로 저장을 한다.
                    NameValuePairs.add(new BasicNameValuePair("id","1st"));
                    NameValuePairs.add(new BasicNameValuePair("Mode","SensorValue_Request"));
                    Thread.sleep(3000);
                    temp++;
                    System.out.println(temp);
                    getMethod.setEntity(new UrlEncodedFormEntity(NameValuePairs));
                    //값을 보낸다.
                    HttpResponse response = client.execute(getMethod);


                    //값을 읽기 위한 cbr을 생성
                    BufferedReader cbr=new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));

                    while((readData=cbr.readLine())!=null){
                        arr.add(readData);
                        System.out.println(readData);
                    }
////////////////////////////////////////////////////////////////////////////////////////////////// data  processing start

                    String[] temp1=arr.get(0).split(" ");
                    for(int i=0 ; i<temp1.length ; i++){
                        left_t_sensor1.add(temp1[i]);
                    }


                    String[] temp2=arr.get(1).split(" ");
                    for(int i=0 ; i<temp2.length ; i++){
                        left_t_sensor2.add(temp2[i]);
                    }



                    String[] temp3=arr.get(2).split(" ");
                    for(int i=0 ; i<temp3.length ; i++){
                        right_t_sensor1.add(temp3[i]);
                    }

                    String[] temp4=arr.get(3).split(" ");
                    for(int i=0 ; i<temp4.length ; i++){
                        right_t_sensor2.add(temp4[i]);
                    }

                    String[] temp5=arr.get(4).split(" ");
                    for(int i=0 ; i<temp5.length ; i++){
                        pulse_sensor.add(temp5[i]);
                    }

                    String[] temp6=arr.get(5).split(" ");
                    for(int i=0 ; i<temp6.length ; i++){
                        user_status.add(temp6[i]);
                    }
////////////////////////////////////////////////////////////////////////////////////////////////  data processing end
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);


                } catch(InterruptedException ex){
                    Log.e("SampleJavaThread", "Exception in thread", ex);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ProgressHandler extends Handler{ //Handler 클래스를 상속하여 새로운 핸들러 클래스를 정의
        public void handleMessage(Message msg) {
            // 여기서 setTExt
            output.setText(" \n");
            output.append(left_t_sensor1.get(0)+" : "+left_t_sensor1.get(1)+"\n");
            output.append(left_t_sensor2.get(0)+" : "+left_t_sensor2.get(1)+"\n");
            output.append(right_t_sensor1.get(0)+" : "+right_t_sensor1.get(1)+"\n");
            output.append(right_t_sensor2.get(0)+" : "+right_t_sensor2.get(1)+"\n");
            output.append(pulse_sensor.get(0)+" : "+pulse_sensor.get(1)+"\n");

            mSeries1 = new LineGraphSeries<DataPoint>(generateData());
            graph.addSeries(mSeries1);

            mSeries2 = new LineGraphSeries<DataPoint>(generateData2());
            graph.addSeries(mSeries2);


            pulse_value = new LineGraphSeries<DataPoint>(pulse_generateData());
            graph.addSeries(pulse_value);


            mTimer1 = new Runnable() {
                //   @Override
                public void run() {
                    mSeries1.resetData(generateData());
                    mSeries2.resetData(generateData2());
                    mHandler.postDelayed(this, 300);
                }
            };
            mHandler.postDelayed(mTimer1, 300);


        }
    }
    //////////////////////////////////////////////////////////////
    class temp_thread  extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
////////////////////