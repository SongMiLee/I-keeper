package org.androidtown.i_keeper_test;


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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 *  안드로이드 생명주기
 *  - 액티비티 시작 -> onCreate() -> onStart() -> onResume() -> 액티비티 동작
 *    -> (다른 액티비티가 앞으로 올 경우 -> onPause() -> (액티비티가 더이상 보이지 않을 경우)
 *    -> onStop() -> onDestory() -> 액티비티 종료
 */


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Monitor extends Fragment {
    //for network
    String readData="";
    ArrayList<String> arr = new ArrayList<String>();
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private boolean running = false;
    ProgressHandler handler;        //객체 생성
    //HttpClient 인스턴스 생성
    HttpClient client=new DefaultHttpClient();
    // HTTP 메서드의 인스턴스 생성
    // URL과 함께 HttpClient 컴포넌트에서 제공하는 HTTP 메서드의 클래스의 생성자를 사용하여
    // HTTP 의 요청라인을 지원하는 HTTP 메시지의 인스턴스를 생성한다.
    HttpPost getMethod=new HttpPost("http://alert-height-91305.appspot.com/hello");

    //
    TextView output;
    Button btn_refresh;
    String str;

    //
    //
    //

    //
    public Frag_Monitor() {
        // Required empty public constructor
    }

    private LineGraphSeries<DataPoint> mSeries1;

    private double graph2LastXValue = 5d;
    //유저 정보
    UserInfo user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.frag_monitor, container, false);
        user=new UserInfo();

        // Inflate the layout for this fragment
        GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<DataPoint>(generateData());
        graph.addSeries(mSeries1);


        //
        output = (TextView) rootView.findViewById(R.id.output);
        btn_refresh = (Button) rootView.findViewById(R.id.btn_refresh);

//
        handler = new ProgressHandler();
//
        return rootView;
    }

    //@Override
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
                        arr.add(readData);
                    }
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



    // @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new Runnable() {
            //   @Override
            public void run() {
                mSeries1.resetData(generateData());
                mHandler.postDelayed(this, 300);
            }
        };
        mHandler.postDelayed(mTimer1, 300);


    }

    //@Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);

        super.onPause();
    }
    //@Override
    public void onStop(){
        super.onStop();

        running  = false;
    }


    private DataPoint[] generateData() {
        int count =40;         // 총 시간 간격을 나타냄
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count ; i++) {

           double  y= Double.parseDouble(arr.get(i%6));
            double x = i;
            /*double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;*/
            DataPoint v = new DataPoint(x,y);
            values[i] = v;
        }
        return values;
    }
    //


    //

    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }


    public class ProgressHandler extends Handler{ //Handler 클래스를 상속하여 새로운 핸들러 클래스를 정의
        public void handleMessage(Message msg){
            // 여기서 setTExt
            output.setText("");
            for(int i=0; i<6 ; i+=2) {
                output.append("  value" + i + " : " +Double.valueOf(arr.get(i)).doubleValue()+" /   value" + (i +1)+ " : " + arr.get(i+1)+"\n");
            }

/*
            for(int i=0 ; i<30 ; i++) {
                p_sensor_result1.setText("  value"+i+" : "+arr.get(i));
            }
            for(int i=30 ; i<60 ; i++) {
                p_sensor_result2.setText("  value"+i+" : "+arr.get(i));
            }
*/

        }
    }
}
