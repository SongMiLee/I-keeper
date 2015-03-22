package org.androidtown.i_keeper_test;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.SocketHandler;


public class MainActivity extends Activity {

    EditText input_id;  // 입력된 IP주소
    Button login_btn;

    //소켓 통신을 위한 변수
    private Socket socket;      // 소켓을 생성
    int port=9909;
    String ip_addr="104.155.237.148";
    PrintWriter socket_out;     //서버에 데이터를 전송하기 위한 변수
    BufferedReader socket_in;     //서버로부터 온 데이터를 읽기위한 변수
    String data;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // SplashActivity 호출
           startActivity(new Intent(this, SplashActivity.class));




        input_id = (EditText)findViewById(R.id.input_id); //입력된 ID 값
        login_btn = (Button) findViewById(R.id.login_btn);


        // 로그인 버튼 클릭 이벤트
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼이 클릭되면 소켓에 데이터를 출력
                String data = input_id.getText().toString(); // 글자 입력칸에 있는 글자를 String 형태로 받아서 data에 저장
                Log.w("NETWORK"," "+data);
                if(data != null){   //만약 데이터가 입력된다면
                    socket_out.println(data);   //data를 stream 형태로 변형하여 전송
                }
            }
        });




        Thread worker = new Thread(){       //worker를 Thread로 생성
            public void run(){  //스레드 실행구문
                try{
                    //소켓을 생성하고 입출력 스트림을 소켓에 연결
                    socket = new Socket(ip_addr, port);
                    socket_out = new PrintWriter(socket.getOutputStream(),true);    //데이터를 전송시 stream형태로 변환해서 전송

                }catch(IOException e){
                    e.printStackTrace();
                }

            }

        };

        worker.start();


        /////////////////////////////////////////////////
                //
               // Intent monitorIntent = new Intent(getApplicationContext(), MonitorActivity.class);
               // startActivity(monitorIntent);

        /*
        Thread worker = new Thread(){
            public void run(){
                try{
                    socket = new Socket(ip_addr,port);
                    socket_out = new PrintWriter(socket.getOutputStream(),true);
                }catch(IOException e){
                    e.printStackTrace();
                }
                try{
                    while(true){
                        data=socket_in.readLine();
                    }
                }catch(Exception e){
                }
            }
        };
        worker.start();
*/
    }


  /*
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    @Override
    protected void onStop(){
        super.onStop();
        try{
            socket.close();//소켓을 닫는다
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}