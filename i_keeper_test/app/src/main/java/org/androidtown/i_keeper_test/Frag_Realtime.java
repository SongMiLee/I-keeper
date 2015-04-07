package org.androidtown.i_keeper_test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Realtime extends Fragment {


    TextView output;
    String str;
    int port=9999;
    private Socket socket;
    BufferedReader socket_in;       //reader
    PrintWriter socket_out;     //writer
    //유저 정보
    UserInfo user;


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


        Thread worker = new Thread() {
            public void run() {
                try {
                    socket = new Socket("104.155.212.106", port);
                    socket_out = new PrintWriter(socket.getOutputStream(), true);
                    socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    socket_out.println("_Refresh="+user.returnUserID());
                    socket_out.flush();

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



        return rootView;
        //  return inflater.inflate(R.layout.frag_realtime, container, false);
    }
    //@Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);

        super.onPause();
    }

}
