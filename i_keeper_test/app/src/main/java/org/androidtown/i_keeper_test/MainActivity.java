package org.androidtown.i_keeper_test;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class MainActivity extends Activity {
    //프로그래스 대화상자 객체
    ProgressDialog progressDialog;

    //editText에서의 값을 가지기 위한 변수
    String id;
    EditText edttext;

    String page="";
    //user의 id 정보를 저장하기 위한 클래스 변수
    UserInfo user;
    Handler handler;
    String line=null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
           startActivity(new Intent(this, MonitorActivity.class));

            user=new UserInfo();
            handler=new Handler();

            //UI의 값을 얻어서 저장
            edttext=(EditText)findViewById(R.id.TextView02);
            Button login_btn = (Button) findViewById(R.id.login_btn);

             //버튼 클릭 이벤트
            login_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                id= edttext.getText().toString();
                //id 부분이 null 인 경우
                if(id.isEmpty() || id.length()==0){
                    Toast.makeText(getApplicationContext(),"ID를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                //id 부분이 null이 아닌 경우
                else
                {
                    //프로그레스 다이얼로그를 띄운다.
                    showDialog(1001);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpClient client=new DefaultHttpClient();
                            HttpPost getMethod=new HttpPost("http://alert-height-91305.appspot.com/hello");

                            try{
                                ArrayList<NameValuePair> NameValuePairs=new ArrayList<NameValuePair>(2);
                                //보낼 값을 ArrayList에 키와 값으로 저장을 한다.
                                NameValuePairs.add(new BasicNameValuePair("Mode","Login"));
                                NameValuePairs.add(new BasicNameValuePair("id",id));

                                getMethod.setEntity(new UrlEncodedFormEntity(NameValuePairs));
                                //값을 보낸다.
                                HttpResponse response = client.execute(getMethod);

                                //값을 읽기 위한 cbr을 생성
                                BufferedReader cbr=new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));

                                //null이 아닐때 값을 읽어서 res에 넣는다.
                                while((line=cbr.readLine())!=null)
                                    page+=line;
                                System.out.println("result : "+page);

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //id를 찾은 경우
                                   if(page.equals("true"))
                                   {
                                        //프로그래스 창을 끈다.
                                        if(progressDialog!=null)
                                            progressDialog.dismiss();

                                        //user 정보를 저장한 뒤 다음 액티비티로 넘어간다.
                                        user.setUserID(id);
                                        startActivity(new Intent(MainActivity.this,MonitorActivity.class));
                                    }
                                    //id를 못 찾은 경우
                                    if(page.equals("false"))
                                    {
                                        if(progressDialog!=null)
                                            progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"id가 없습니다.",Toast.LENGTH_SHORT).show();

                                    }
                                    page="";

                                }
                            });

                        }
                    }).start();
                }
             }
        });
    }

   public Dialog onCreateDialog(int id){
        switch (id){
            case 1001 :
                progressDialog=new ProgressDialog(this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("데이터를 확인 중 입니다.");

                return progressDialog;
        }
        return null;
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
