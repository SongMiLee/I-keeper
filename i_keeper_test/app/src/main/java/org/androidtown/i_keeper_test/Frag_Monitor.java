package org.androidtown.i_keeper_test;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    int port=9999;
    SocketHandle sh;


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
        //소켓 쓰레드를 발생시킨다.
        sh =new SocketHandle();
        sh.start();

        // Inflate the layout for this fragment
        GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<DataPoint>(generateData());
        graph.addSeries(mSeries1);

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
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }

    class SocketHandle extends Thread{
        public void run(){
            try {
                //소켓 관련 writer, reader를 초기화 시킨다.
                socket=new Socket("104.155.212.106",port);//서버의 9999 포트로 접속
                writer=new PrintWriter(socket.getOutputStream());
                reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //접속할 때 먼저 _Refresh를 보내 새로운 정보를 보내도록 한다.
                writer.println("_Refresh="+user.returnUserID());
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
