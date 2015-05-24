package com.google.sampleapp.backend;

import javax.servlet.http.*;

import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import javax.servlet.*;

import com.google.android.gcm.server.*;
import com.google.android.gcm.server.Message;
import com.google.appengine.api.utils.SystemProperty;

/**
 * Created by user on 2015-05-13.
 */

public class MyServlet extends HttpServlet {
    //DB에 연결하기 위한 변수
    String SQL;
    Statement stmt;
    Connection conn;
    //로그를 찍기 위한 변수
    private static final Logger log = Logger.getLogger(MyServlet.class.getName());
    //푸시 메시지를 보내기 위해 api_key 값을 가져온다.
    private static final String API_KEY = System.getProperty("gcm.api.key");

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url");
    }
    /**
     * list에 등록되어 있는 디바이스를 넣어주는 함수
     * */
    public void setArray(List<String> list){
        try{
            Class.forName("com.mysql.jdbc.GoogleDriver");
            conn = DriverManager.getConnection("jdbc:google:mysql://alert-height-91305:keeper/ik?user=root");
            stmt=conn.createStatement();

            SQL="select * from Register";
            ResultSet rs=stmt.executeQuery(SQL);

            while(rs.next()){
                //DB에 존재하는 만큼 list에 추가해준다.
                log.log(Level.WARNING, "Add to list");
                String regId=rs.getString(2);
                list.add(regId);
            }
        }catch(Exception e){
            StringBuffer sb=new StringBuffer();
            sb.append("\n");
            StackTraceElement element[]=e.getStackTrace();
            for(int i=0;i<element.length;i++)
            {
                sb.append(element[i].toString());
                sb.append("\n");
            }
            log.log(Level.WARNING,sb.toString());

        }
    }
    /**
     * Push 메시지를 보내기 위한 함수
     * */
    public void sendPush(String text, String id)
    {
        List<String> list=new ArrayList<String>();
        //DB의 Register 테이블에 등록되어 있는 list를 가져온다.
        setArray(list);
        Sender sender=new Sender(API_KEY);

        //보낼 메시지를 다음과 같이 구성한다.
        Message message=new Message.Builder()
                .collapseKey("Value Come")
                .timeToLive(600)
                .delayWhileIdle(true)
                .addData("id",id)
                .addData("info",text)
                .build();

        log.log(Level.WARNING, "send to device");
        try{
            //푸시 메시지를 보낼 수 있는 디바이스가 하나 일 때
            if(list.size()>1)
            {
                log.log(Level.WARNING,"Many devices");
                MulticastResult result = sender.send(message, list, 2);
                for (Result r : result.getResults()) {
                    printResult(r);
                }
            }
            //푸시 메시지를 보낼 수 있는 디바이스가 한 개 이상일 때
            else if(list.size()==1)
            {
                log.log(Level.WARNING,"1 device");
                log.log(Level.WARNING, list.get(0));
                Result result= sender.send(message,list.get(0) ,2);
                printResult(result);
            }
            //보낼 수 있는 디바이스가 없을 때
            else
            {
                log.log(Level.WARNING,"no devices");
            }
            log.log(Level.WARNING,message.toString());

        }
        catch (Exception e){
            log.log(Level.WARNING, "send wrong");
            StringBuffer sb=new StringBuffer();
            sb.append("\n");
            StackTraceElement element[]=e.getStackTrace();
            for(int i=0;i<element.length;i++)
            {
                sb.append(element[i].toString());
                sb.append("\n");
            }
            log.log(Level.WARNING,sb.toString());
        }
    }
    /*
    * sendPush로 보낸 값이 제대로 보내 졌는지 확인하기 위한 함수
    */
    public static void printResult(Result result){

        System.out.println(result.getCanonicalRegistrationId());

        String messageId=result.getMessageId();

        if(messageId!=null){

            System.out.println("MessageId = "+messageId);
            log.log(Level.WARNING,"MessageId = " + messageId);
        }
        else {
            System.out.println(result.getErrorCodeName());
            log.log(Level.WARNING, result.getErrorCodeName());
        }

    }

    /**
     * HTTP POST 요청이 왔을 때 처리하는 함수
     * */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, FileNotFoundException {
        PrintWriter spw=new PrintWriter(response.getOutputStream());
        new DeleteData().DeleteOld();
        try{
            //Google app engine 환경일 때 com.mysql.jdbc.GoogleDriver 사용
            if(SystemProperty.environment.value()==SystemProperty.Environment.Value.Production) {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                conn = DriverManager.getConnection("jdbc:google:mysql://alert-height-91305:keeper/ik?user=root");
            }else {
                //Google app engine 환경이 아닐 때 com.mysql.jdbc.Driver 사용
                Class.forName("com.mysql.jdbc.Driver");
                conn=DriverManager.getConnection("jdbc:mysql://173.194.244.16/keeper","toor","toor");
            }
            stmt=conn.createStatement();

            String mode=request.getParameter("Mode");

            /*
            * 어플리케이션으로 부터 Login요청이 들어올 때
            * DB의 cloinfo 테이블에서 id 값을 확인한다.
            * id가 있으면 true 없으면 false 이다.
             */
            if(mode.equals("Login"))
            {
                //id를 추출한다.
                String id=request.getParameter("id");
                SQL="SELECT * FROM cloinfo WHERE id=\'"+id+"\'";
                ResultSet rs=stmt.executeQuery(SQL);
                if(rs.next())// 값이 있을 때
                {
                    spw.print("true");
                    spw.flush();
                }
                else//없을 때
                {
                    spw.print("false");
                    spw.flush();
                }
                rs.close();
            }
            /*
            * 스마트 의류로부터 들어온 Sensor 값들을
            * DB의 Sensing 테이블에 넣어준다.
             */
            else if(mode.equals("Sensor")){
                log.log(Level.WARNING,"Sensing");
                int left_t_sensor1,left_t_sensor2, right_t_sensor1, right_t_sensor2, p_sensor;
                //String으로 값을 받아오는 과정
                String id=request.getParameter("id");
                String left1=request.getParameter("left1");
                String left2=request.getParameter("left2");
                String right1=request.getParameter("right1");
                String right2=request.getParameter("right2");
                String psensor=request.getParameter("psensor");
                String violence=request.getParameter("violence");

                log.log(Level.WARNING,id+" "+left1+" "+left2+" "+right1+" "+right2+" "+psensor+" "+violence);
                //String을 Int로 바꿔준다.
                left_t_sensor1=Integer.parseInt(left1);
                left_t_sensor2=Integer.parseInt(left2);
                right_t_sensor1=Integer.parseInt(right1);
                right_t_sensor2=Integer.parseInt(right2);
                p_sensor=Integer.parseInt(psensor);

                SQL="insert into Sensing values(default, default, \'"+id+"\',"+left_t_sensor1+","+left_t_sensor2+","+right_t_sensor1+","+right_t_sensor2+","+p_sensor+",\'"+violence+"\')";
                stmt.executeUpdate(SQL);

                if(violence.equals("TRUE")) {
                    log.log(Level.WARNING, "send push");
                    sendPush("It's Danger", id);
                }

                spw.println("ok");
                spw.flush();
            }
            /*
            * FragMonitor에 DB에 저장된 최근의 60개 값을
            * 보내주는 역할을 한다.
            * */
            else if(mode.equals("FragMonitor_Request"))
            {
                int cnt=0;
                log.log(Level.WARNING, "fragMonitor Request");
                String id=request.getParameter("id");

                SQL="select * from Sensing where id=\'"+id+"\'"+"order by time desc";
                ResultSet rs=stmt.executeQuery(SQL);
                int []left_t_sensor1 =new int[60];
                int []left_t_sensor2=new int [60];
                int []right_t_sensor1=new int[60];
                int []right_t_sensor2=new int[60];
                int []p_sensor= new int [60];

                int Max_left1=0, Max_left2=0, Max_right1=0, Max_right2=0;
                while(rs.next())
                {
                    //DB로 부터 값을 받아온다.
                    left_t_sensor1[cnt]=rs.getInt(3);
                    left_t_sensor2[cnt]=rs.getInt(4);
                    right_t_sensor1[cnt]=rs.getInt(5);
                    right_t_sensor2[cnt]=rs.getInt(6);
                    p_sensor[cnt]=rs.getInt(7);

                    //가장 큰 값을 찾는다.
                    if(Max_left1<left_t_sensor1[cnt])
                        Max_left1=left_t_sensor1[cnt];
                    if(Max_left2<left_t_sensor2[cnt])
                        Max_left2=left_t_sensor2[cnt];
                    if(Max_right1<right_t_sensor1[cnt])
                        Max_right1=right_t_sensor1[cnt];
                    if(Max_right2<right_t_sensor2[cnt])
                        Max_right2=right_t_sensor2[cnt];

                    cnt++;
                    //60개 값이 되면 질의를 끝낸다.
                    if(cnt==59)
                        break;
                }

                for(int j=cnt;j>=0;j--)
                    spw.println("p_sensor=" + p_sensor[j]+"\n");
                spw.println("left_t_sensor1="+Max_left1+"\nleft_t_sensor2="+Max_left2+"\nright_t_sensor1="+Max_right1+"\nright_t_sensor2"+Max_right2+"\n");
                spw.flush();

            }
            /*
            * Frag_Realtime에 정보를 보내주기 위한 부분.
            * DB에 저장되어 있는 가장 최신의 값을 보내준다.
            * */
            else if(mode.equals("SensorValue_Request"))
            {
                String id=request.getParameter("id");
                log.log(Level.WARNING,"get "+id);
                int p_sensor;
                String violence;

                SQL="select * from Sensing where id=\'"+id+"\' order by time desc";
                ResultSet rs=stmt.executeQuery(SQL);
                if (rs.next())
                {
                    //질의로부터 나온 심박센서 값과 폭력 상황 값을 가져온다.
                    p_sensor=rs.getInt(8);
                    violence=rs.getString(9);

                    if(violence.equals("TRUE")) {
                        log.log(Level.WARNING,"send push");
                        sendPush("It's Danger",  id);
                    }
                    spw.println("p_sensor="+p_sensor+"\nviolence="+violence);
                    spw.flush();
                }
            }
            else if(mode.equals("voice"))
            {
                spw.write("Voice starnt\n");
                spw.flush();
                log.log(Level.WARNING,"start voice");
                String id=request.getParameter("id");
                String fsize=request.getParameter("filesize");
                String name=request.getParameter("name");

                //Integer로 파싱해준다.
                int size=Integer.getInteger(fsize);

                InputStream inputStream=request.getInputStream();
                OutputStream outputStream=response.getOutputStream();

                byte[] buffer=new byte[4096];
                int bytesRead=-1;

                log.log(Level.INFO, "Receiving data......");

                while((bytesRead=inputStream.read(buffer))!=-1) {
                    outputStream.write(buffer,0, bytesRead);
                }
                log.log(Level.INFO, outputStream.toString());
                SQL="insert into voice values(default,\'"+id+"\',\'"+name+"\',"+size+","+outputStream+")";
                stmt.executeUpdate(SQL);

                spw.println("Write the voice to DB");
                spw.flush();
            }

            /**
             * GCM 을 하기위한 key 등록 부분
             * DB의 Register 테이블에 저장한다.
             **/
            else if(mode.equals("Register"))
            {
                String regId=request.getParameter("regId");
                SQL="select * from Register where regid=\'"+regId+"\'";

                ResultSet rs=stmt.executeQuery(SQL);

                //키가 등록이 되어 있지 않다면 키를 등록해준다.
                if(rs.next()==false)
                {
                    SQL="insert into Register values(default,\'"+regId+"\')";
                    stmt.executeUpdate(SQL);
                    System.out.println("Register Your ID");
                    spw.print("Register your id\n");
                    spw.flush();
                }
                else {
                    System.out.println("You already register ID");
                    spw.print("You already register your id\n");
                    spw.flush();
                }
            }
            stmt.close();
            conn.close();
        }
        catch(Exception e)
        {
            spw.println("error");
            spw.flush();
            /**
             * 오류를 로그에 찍기.
             * */
            StringBuffer sb=new StringBuffer();
            sb.append("\n");
            StackTraceElement element[]=e.getStackTrace();
            for(int i=0;i<element.length;i++)
            {
                sb.append(element[i].toString());
                sb.append("\n");
            }
            log.log(Level.WARNING,sb.toString());
        }
    }
}
