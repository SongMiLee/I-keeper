package com.example.user.forsocket;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends ActionBarActivity {

    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    int port=52273;

    SocketHandler sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sh=new SocketHandler();
        sh.start();

    }

    class SocketHandler extends Thread{
        public void run(){
            try {
                socket=new Socket("10.30.115.209",port);
                Log.i("socket",socket.getLocalAddress().toString());
                writer=new PrintWriter(socket.getOutputStream(),true);
                reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));

                writer.print("_add=");
            } catch (IOException e) {
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
