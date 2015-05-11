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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Monitor extends Fragment {
    //for network

    int port=9999;
   // SocketHandle sh;
   private Socket socket;
    BufferedReader socket_in;       //reader
    PrintWriter socket_out;     //writer

    //
    TextView output;
    Button btn_refresh;
    String str;

    //
    //
    private boolean running = false;
    ProgressHandler handler;        //객체 생성
    //

    //
    public Frag_Monitor() {
        // Required empty public constructor
    }
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
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
        return rootView;
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

    private DataPoint[] generateData() {
        int count = 40;         // 총 시간 간격을 나타냄
    //    double[] temp   = {2.0, 2.0, 2.0, 3.0};
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
         //   double y = temp[i%4]+2;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
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

        }
    }
}
