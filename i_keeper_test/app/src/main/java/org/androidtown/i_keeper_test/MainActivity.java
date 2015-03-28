package org.androidtown.i_keeper_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;


public class MainActivity extends Activity {
    //네트워크 연결을 위한 변수들
    Socket socket;
    int port =9999;
    PrintWriter writer;
    BufferedReader reader;

    //네트워크를 연결하기 위한 핸들러 Thread를 extends 한다.
    SocketHandler sh;
    ReaderHandler rh;

    //editText에서의 값을 가지기 위한 변수
    String text;
    EditText edttext;

    String page="t";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            startActivity(new Intent(this, SplashActivity.class));

            //소켓 핸들러 쓰레드를 생성 후 실행
            sh=new SocketHandler();
            sh.start();


            //UI의 값을 얻어서 저장
            edttext=(EditText)findViewById(R.id.TextView02);
            Button login_btn = (Button) findViewById(R.id.login_btn);

             //버튼 클릭 이벤트
            login_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                text= edttext.getText().toString();
                Log.i("Text=", text);
                //id 부분이 null 인 경우
                if(text.isEmpty() || text.length()==0){
                    Toast.makeText(getApplicationContext(),"ID를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                //id 부분이 null이 아닌 경우
                else
                {
                    //로그인 id 값을 서버에 보낸 후 버퍼를 비운다.
                    writer.println("_login=" + text);
                    writer.flush();

                    rh=new ReaderHandler();
                    rh.start();

                    Log.i("page : ", page);

                    if(page.equals("true"))
                        startActivity(new Intent(MainActivity.this, MonitorActivity.class));

                }
             }
        });


    }

    class SocketHandler extends Thread{
        public void run(){
            try {
                socket=new Socket("104.155.212.106",port);//서버의 9999 포트로 접속
                writer=new PrintWriter(socket.getOutputStream());
                reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class ReaderHandler extends Thread{
        public void run(){
            try{
                //page=reader.readLine();
                while((page=reader.readLine())!=null)
                    System.out.println(page);
            }catch(Exception e){
                e.printStackTrace();
            }
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
