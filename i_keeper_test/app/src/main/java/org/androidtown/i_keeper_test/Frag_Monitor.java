package org.androidtown.i_keeper_test;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Monitor extends Fragment {
    //for network

    //HttpClient 인스턴스 생성
    HttpClient client;
    // HTTP 메서드의 인스턴스 생성
    // URL과 함께 HttpClient 컴포넌트에서 제공하는 HTTP 메서드의 클래스의 생성자를 사용하여
    // HTTP 의 요청라인을 지원하는 HTTP 메시지의 인스턴스를 생성한다.
    HttpPost getMethod;

    //
    TextView output;
    Button btn_refresh;
    String str;

    //
    GraphView pulse_graph;
    GraphView touch_graph;
    //
    String readData="";
    ArrayList<String> arr = new ArrayList<String>();

    ArrayList<String> left_t_sensor1 ;
    ArrayList<String> left_t_sensor2 ;
    ArrayList<String> right_t_sensor1 ;
    ArrayList<String> right_t_sensor2 ;
    ArrayList<String> pulse_sensor;
    ArrayList<Double> darr = new ArrayList<Double>();
    //
    String[] temp1={"null","null"} ;
    // Graph
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    ProgressRunnable runnable;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean running = false;
    ProgressHandler handler;        //객체 생성

    //
    //
    Calendar c;

    int cur_hour, cur_min, cur_sec;
    String[] x_value_temp={"null","null"} ;
    Double x_value=0.0;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeries3;
    private LineGraphSeries<DataPoint> mSeries4;
    private LineGraphSeries<DataPoint> mSeries5;


    //
    public Frag_Monitor() {
        // Required empty public constructor
    }

    private double graph2LastXValue = 5d;
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
        runnable = new ProgressRunnable();
        //
        // Inflate the layout for this fragment
        pulse_graph = (GraphView) rootView.findViewById(R.id.pulse_graph);
        touch_graph = (GraphView) rootView.findViewById(R.id.touch_graph);

        //Graph x,y축 범위 설정

        pulse_graph.getViewport().setXAxisBoundsManual(true);
        pulse_graph.getViewport().setMinX(0);
        pulse_graph.getViewport().setMaxX(60);
/*

        pulse_graph.getViewport().setYAxisBoundsManual(true);
        pulse_graph.getViewport().setMinY(0);
        pulse_graph.getViewport().setMaxY(20);
*/

        touch_graph.getViewport().setXAxisBoundsManual(true);
        touch_graph.getViewport().setMinX(0);
        touch_graph.getViewport().setMaxX(60);

        touch_graph.getViewport().setYAxisBoundsManual(true);
        touch_graph.getViewport().setMinY(0);
        touch_graph.getViewport().setMaxY(20);

//
        //pulse_sensor
        mSeries1 = new LineGraphSeries<DataPoint>(initial_generateData());
        pulse_graph.addSeries(mSeries1);

        // left_touch_sensor 1
        mSeries2 = new LineGraphSeries<DataPoint>(initial_generateData());
        mSeries2.setColor(Color.rgb(0, 136, 101));
        touch_graph.addSeries(mSeries2);

        // left_touch_sensor 2
        mSeries3 = new LineGraphSeries<DataPoint>(initial_generateData());
        mSeries3.setColor(Color.rgb(0,159,222));
        touch_graph.addSeries(mSeries3);

        // right_touch_sensor 1
        mSeries4 = new LineGraphSeries<DataPoint>(initial_generateData());
        mSeries4.setColor(Color.rgb(252, 221, 115));
        touch_graph.addSeries(mSeries4);

        // right_touch_sensor 2
        mSeries5 = new LineGraphSeries<DataPoint>(initial_generateData());
        mSeries5.setColor(Color.rgb(251,105,98));
        touch_graph.addSeries(mSeries5);
        //
        return rootView;
    }

    //@Override
    public void onStart(){
        super.onStart();

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread1 = new Thread(new Runnable(){
                    public void run(){
                        arr = new ArrayList<String>();
                        left_t_sensor1 = new ArrayList<String>();
                        left_t_sensor2 = new ArrayList<String>();
                        right_t_sensor1 = new ArrayList<String>();
                        right_t_sensor2 = new ArrayList<String>();
                        pulse_sensor = new ArrayList<String>();
                        try{

                            //HttpClient 인스턴스 생성
                            HttpClient client=new DefaultHttpClient();
                            // HTTP 메서드의 인스턴스 생성
                            // URL과 함께 HttpClient 컴포넌트에서 제공하는 HTTP 메서드의 클래스의 생성자를 사용하여
                            // HTTP 의 요청라인을 지원하는 HTTP 메시지의 인스턴스를 생성한다.
                            HttpPost getMethod=new HttpPost("http://alert-height-91305.appspot.com/hello");

                            ArrayList<NameValuePair> NameValuePairs=new ArrayList<NameValuePair>(1);
                            //보낼 값을 ArrayList에 키와 값으로 저장을 한다.
                            NameValuePairs.add(new BasicNameValuePair("id", "1st"));
                            NameValuePairs.add(new BasicNameValuePair("Mode", "FragMonitor_Request"));
                            getMethod.setEntity(new UrlEncodedFormEntity(NameValuePairs));
                            //값을 보낸다.

                            HttpResponse response = client.execute(getMethod);

                            //값을 읽기 위한 cbr을 생성
                            BufferedReader cbr=new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));
                            while((readData=cbr.readLine())!=null){
                                arr.add(readData);
                            }

                            System.out.println(arr);
                            //     System.out.println("Arr Size : "+arr.size());
                            //System.out.println(arr);
                            // client.getConnectionManager().shutdown();

///////////////////////////////////////////////////////////////////////////////////////////////// data  processing start
                            for(int i=0 ; i<300 ; i++){
                                if(i%5==0){
                                    temp1 = arr.get(i).split("=");
                                    left_t_sensor1.add(temp1[1]);
                                }
                                else if(i%5==1){
                                    temp1 = arr.get(i).split("=");
                                    left_t_sensor2.add(temp1[1]);
                                }
                                else if(i%5==2){
                                    temp1 = arr.get(i).split("=");
                                    right_t_sensor1.add(temp1[1]);
                                }
                                else if(i%5==3){
                                    temp1 = arr.get(i).split("=");
                                    right_t_sensor2.add(temp1[1]);
                                }
                                else if(i%5==4){
                                    temp1 = arr.get(i).split("=");
                                    pulse_sensor.add(temp1[1]);
                                }
                            }

/////////////////////////////////////////////////////////////////////////////////////////////////  data processing end


                            handler.post(runnable);
                            //

                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                thread1.start();
            }
        });
    }




    // @Override
    public void onResume() {
        super.onResume();
    }


    //@Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);

        super.onPause();
    }


    //@Override
    public void onStop(){
        super.onStop();

        running = false;
    }
    // 120 -> 200
    // 20->20     /       40 -> 40
    // 60 -> 80

    /////////////////////////////////////////////////////////////////////////////////          Draw graph using data from server
    private DataPoint[] initial_generateData() {
        int count = 60;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = 0.0;
            double y = 0.0;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
    private DataPoint[] pulse_generateData() {
        int count = 60;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y = Double.valueOf(pulse_sensor.get(i))*0.01;
            //double y  = 1.5;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
    // Color : RED
    private DataPoint[] leftTouch1_generateData() {
        int count = 60;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            int x = i;
            double y = Double.valueOf(left_t_sensor1.get(i))*0.01;

//            System.out.println("left_t_1: "+left_t_sensor1);
       /*     double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3 +5;
       */     DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
    // COLOR : BLUE
    private DataPoint[] leftTouch2_generateData() {
        int count = 60;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y = Double.valueOf(left_t_sensor2.get(i))*0.01;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
    // COLOR : MAGENTA
    private DataPoint[] rightTouch1_generateData() {
        int count = 60;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y = Double.valueOf(right_t_sensor1.get(i))*0.01;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
    private DataPoint[] rightTouch2_generateData() {
        int count = 60;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            //           double f = mRand.nextDouble()*0.15+0.3;
            double y = Double.valueOf(right_t_sensor2.get(i))*0.01;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
    //
    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }



    public class ProgressHandler extends Handler{ //Handler 클래스를 상속하여 새로운 핸들러 클래스를 정의
        public void handleMessage(Message msg) {

           /* SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
            Date currentTime = new Date ( );
            String mTime = mSimpleDateFormat.format ( currentTime );
*/
/*


            mTimer1 = new Runnable() {
                //   @Override
                public void run() {

                }
            };
            mHandler.postDelayed(mTimer1, 300);
*/

        }
    }

    // Runnable 인터페이스를 구현하는 새로운 클래스 정의
    public class ProgressRunnable implements  Runnable{
        public void run() {
            c = Calendar.getInstance();
            cur_hour = c.get(Calendar.HOUR_OF_DAY);
            cur_min = c.get(Calendar.MINUTE);
            cur_sec = c.get(Calendar.SECOND);

            System.out.println("cur_hour  :"+cur_hour);

            System.out.println("cur_min : "+cur_min);
            System.out.println("cur_sec : "+cur_sec);
            output.setText("현재 시간 : "+cur_hour + "시 " + cur_min + "분  "+cur_sec+"초");


            //    x_value_temp = mTime.split(" ");
            //System.out.println(x_value_temp[1]);



            mSeries1.resetData(pulse_generateData());
            mSeries2.resetData(leftTouch1_generateData());
            mSeries3.resetData(leftTouch2_generateData());
            mSeries4.resetData(rightTouch1_generateData());
            mSeries5.resetData(rightTouch2_generateData());

            //mHandler.postDelayed(this, 300);
        }
    }
}