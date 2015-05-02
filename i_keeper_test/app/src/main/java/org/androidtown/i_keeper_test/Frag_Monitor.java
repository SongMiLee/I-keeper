package org.androidtown.i_keeper_test;


import android.os.Bundle;
import android.os.Handler;
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

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //접속할 때 먼저 _Refresh를 보내 새로운 정보를 보내도록 한다.
                socket_out.println("_Refresh="+user.returnUserID());
                socket_out.flush();

            }
        });
        Thread worker = new Thread() {
            public void run() {
                try {
                    socket = new Socket("104.155.212.106", port);
                    socket_out = new PrintWriter(socket.getOutputStream(), true);
                    socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {


                    while ((str=socket_in.readLine())!=null) {

                        output.post(new Runnable() {
                            public void run() {
                                //"="로 값을 나눈다.
                                String [] tmp=str.split("=");
                                output.setText("VALUE  : "+tmp[0]+", "+tmp[1]+", "+tmp[2]);
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        };
        worker.start();


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
//
  /*  class SocketHandle extends Thread{
        public void run(){
            try {
                //소켓 관련 writer, reader를 초기화 시킨다.
                socket=new Socket("104.155.212.106",port);//서버의 9999 포트로 접속
                writer=new PrintWriter(socket.getOutputStream());
                reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //접속할 때 먼저 _Refresh를 보내 새로운 정보를 보내도록 한다.
                writer.println("_Refresh="+user.returnUserID());
                writer.flush();

                //이 아래에 있는 소스의 위치는 바뀔 수 있다.
                String str;
                //서버에서 보낸 값을 읽는다.
                while((str=reader.readLine())!=null){
                    //"="로 값을 나눈다.
                    String [] tmp=str.split("=");
                    //디버그용으로 로그에 찍힘. ex) a, b, c
                        System.out.println(tmp[0]+", "+tmp[1]+", "+tmp[2]);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }*/

}
