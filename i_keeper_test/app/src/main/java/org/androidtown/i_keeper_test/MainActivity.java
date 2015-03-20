package org.androidtown.i_keeper_test;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.SocketHandler;


public class MainActivity extends Activity {

    EditText input_id;
    Button login_btn;

    //소켓 통신을 위한 변수
   private Socket socket;
    int port=9999;
    String ip_addr="104.155.237.148";
    PrintWriter socket_out;
    BufferedReader socket_in;
    String data;
    //
    SocketHandler socket_handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // SplashActivity 호출
        startActivity(new Intent(this, SplashActivity.class));



      input_id = (EditText)findViewById(R.id.input_id); //입력된 ID 값
      login_btn = (Button) findViewById(R.id.login_btn);

        // Login 버튼 클릭 이벤트 : ID 서버로 전송, MonitorActivity로 전환
        login_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "연결중입니다.",
                        Toast.LENGTH_LONG).show();

                //
                    String data = input_id.getText().toString();

                    if(data!=null){
                        socket_out.println(data);
                    }
                //
              Intent monitorIntent = new Intent(getApplicationContext(), MonitorActivity.class);
                startActivity(monitorIntent);
            }
        });


        class ClickHandler implements View.OnClickListener{
                public void onClick(View v){
                    String data = input_id.getText().toString();
                    socket_out.println(data);
                }
        }
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
