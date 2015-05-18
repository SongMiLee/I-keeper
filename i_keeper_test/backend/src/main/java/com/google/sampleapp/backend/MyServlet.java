package com.google.sampleapp.backend;

import javax.servlet.http.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import javax.servlet.*;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.datastore.*;

/**
 * Created by user on 2015-05-13.
 */

public class MyServlet extends HttpServlet {
    String SQL;
    Statement stmt;
    Connection conn;

    static int number=0;

    private static final Logger log = Logger.getLogger(MyServlet.class.getName());

    private static final String API_KEY = System.getProperty("gcm.api.key");

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url");
    }
    /**
     * list ������ ��ϵ� key ���� �� �־��ֱ�
     * ���� �ۼ��� �Լ�
     * */
    public void setArray(List<String> list){
        try{
            Class.forName("com.mysql.jdbc.GoogleDriver");
            conn = DriverManager.getConnection("jdbc:google:mysql://alert-height-91305:keeper/ik?user=root");
            stmt=conn.createStatement();

            SQL="select * from Register";
            ResultSet rs=stmt.executeQuery(SQL);

            while(rs.next()){
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
     * Push ���� �Լ�
     * */
    public void sendPush(String text, String id)
    {
        //�߱޵� api key ������ ������ ����Ʈ�� �ʱ�ȭ
        List<String> list=new ArrayList<String>();
        //DB�� Register ���̺��� �����Ͽ� list�� �߰����ش�.
        setArray(list);
        Sender sender=new Sender(API_KEY);

        Message.Builder messageBuilder=new Message.Builder();
        messageBuilder.delayWhileIdle(false);
        messageBuilder.timeToLive(9000);
        messageBuilder.addData("id", id);
        messageBuilder.addData("info", text);

        log.log(Level.WARNING, "send to device");
        try{
            //���� �� �ִ� ��ġ�� ������ 1�� �̻��� ��
            if(list.size()>1)
            {
                log.log(Level.WARNING,"Many devices");
                MulticastResult result = sender.send(messageBuilder.build(), list, 5);
                for (Result r : result.getResults()) {
                    printResult(r);
                }
            }
            //���� �� �ִ� ��ġ�� 1���� ��
            else if(list.size()==1)
            {
                log.log(Level.WARNING,"1 device");
                log.log(Level.WARNING, list.get(0));
                Result result= sender.send(messageBuilder.build(),list.get(0) ,5);
                printResult(result);
            }
            //���� �� �ִ� ��ġ�� ���� ��
            else
            {
                log.log(Level.WARNING,"no devices");
            }
            log.log(Level.WARNING,messageBuilder.build().toString().length()+" ");

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
    * send�� ���� ��, ����� ���� ��������
    * Ȯ���ϱ� ���� ������ �Լ�
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
     * Application�� ����Ʈ�Ƿ��� ���� ���� HTTP ó�� �Լ�
     * */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, FileNotFoundException {
        PrintWriter spw=new PrintWriter(response.getOutputStream());

        try{
            //Google app engine�� �÷��� ����� ��
            if(SystemProperty.environment.value()==SystemProperty.Environment.Value.Production) {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                conn = DriverManager.getConnection("jdbc:google:mysql://alert-height-91305:keeper/ik?user=root");
            }else {
                //Google app engine�� �ƴ� ���ÿ��� ������ �Ҷ�
                Class.forName("com.mysql.jdbc.Driver");
                conn=DriverManager.getConnection("jdbc:mysql://173.194.244.16/keeper","toor","toor");
            }
            stmt=conn.createStatement();

            String mode=request.getParameter("Mode");

            /*
            * �ȵ���̵忡�� Login�� ������
            * DB�� cloinfo���� id ���� ������ ���� ���� ������
            * true ���� �����ְ� ������ false ���� �����ش�.
             */
            if(mode.equals("Login"))
            {
                //�� ������ id ���� �и��� ����.
                String id=request.getParameter("id");
                SQL="SELECT * FROM cloinfo WHERE id=\'"+id+"\'";
                ResultSet rs=stmt.executeQuery(SQL);
                if(rs.next())
                {
                    spw.print("true");
                    spw.flush();
                }
                else
                {
                    spw.print("false");
                    spw.flush();
                }
                rs.close();
            }
            //�ۿ��� TouchSensor_Request�� ��û �� ��
            else if(mode.equals("TouchSensor_Request"))
            {
                for(int i=0;i<120;i++)
                {
                    if(i==10) {
                        log.log(Level.FINEST,"send push");
                        sendPush("It's Danger",  "1st");
                    }
                    spw.println(i % 3 + " ");
                }
                spw.flush();
            }
            /*
            *�Ƿ��� ���� ���� ����(�з� ����, �ɹڼ���, ���»�Ȳ �Ǵ� ��)�� ��Ƽ� ������ �����ָ�
            * ������ DB�� Sensor ���̺� �ۼ����ش�.
             */
            else if(mode.equals("Sensor")){
                int left_t_sensor1,left_t_sensor2, right_t_sensor1, right_t_sensor2, p_sensor;
                //request�� ���� ���� �޾ƿ´�.
                String id=request.getParameter("id");
                String left1=request.getParameter("left1");
                String left2=request.getParameter("left2");
                String right1=request.getParameter("right1");
                String right2=request.getParameter("right2");
                String psensor=request.getParameter("psensor");
                String violence=request.getParameter("violence");

                log.log(Level.WARNING,id+" "+left1+" "+left2+" "+right1+" "+right2+" "+psensor+" "+violence);
                //�ʿ� ������ �� ��ȯ �����ش�.
                left_t_sensor1=Integer.parseInt(left1);
                left_t_sensor2=Integer.parseInt(left2);
                right_t_sensor1=Integer.parseInt(right1);
                right_t_sensor2=Integer.parseInt(right2);
                p_sensor=Integer.parseInt(psensor);

                SQL="insert into Sensor values(default, \'"+id+"\',"+left_t_sensor1+","+left_t_sensor2+","+right_t_sensor1+","+right_t_sensor2+","+p_sensor+",\'"+violence+"\')";
                stmt.executeUpdate(SQL);

                spw.println("ok");
                spw.flush();
            }
            /*��û�� sensor ����(�з� ����, �ɹ� ����, ���� ��Ȳ �Ǵ� ��)
            * ��� ���� ������.
            * */
            else if(mode.equals("SensorValue_Request"))
            {
                String id=request.getParameter("id");
                log.log(Level.WARNING,"get "+id);
                int left_t_sensor1,left_t_sensor2, right_t_sensor1, right_t_sensor2, p_sensor;
                String violence;

                SQL="select * from Sensor where id=\'"+id+"\' order by time desc";
                ResultSet rs=stmt.executeQuery(SQL);

                log.log(Level.WARNING, "Db start");
                if(rs.next())
                {
                    log.log(Level.WARNING,"Query the DB");
                    left_t_sensor1=rs.getInt(3);
                    left_t_sensor2=rs.getInt(4);
                    right_t_sensor1=rs.getInt(5);
                    right_t_sensor2=rs.getInt(6);
                    p_sensor=rs.getInt(7);
                    violence=rs.getString(8);
                    spw.println("left_t_sensor1 "+left_t_sensor1+"\nleft_t_sensor2 "+left_t_sensor2+"\nright_t_sensor1 " + right_t_sensor1+"\nright_t_sensor2 "+right_t_sensor2+"\np_sensor "+p_sensor+"\nviolence "+violence);
                    log.log(Level.WARNING,"left_t_sensor1 "+left_t_sensor1+"\nleft_t_sensor2 "+left_t_sensor2+"\nright_t_sensor1 " + right_t_sensor1+"\nright_t_sensor2 "+right_t_sensor2+"\np_sensor "+p_sensor+"\nviolence "+violence);
                    spw.flush();
                }
                log.log(Level.WARNING,"DB end");
            }
            //�Ƿ����� TouchSensor ���� ������ DB�� �ۼ��ϴ� �κ�.
            else if(mode.equals("TouchSensor"))
            {

                String id=request.getParameter("id");
                int r1=Integer.parseInt(request.getParameter("R1"));
                int r2=Integer.parseInt(request.getParameter("R2"));
                int l1=Integer.parseInt(request.getParameter("L1"));
                int l2=Integer.parseInt(request.getParameter("L2"));

                SQL="insert into touchsensor values(default,\'"+id+"\',"+l1+","+l2+","+r1+","+r2+")";

                stmt.executeUpdate(SQL);
                spw.write("Okay ! Write the DB");
                spw.flush();
            }
            else if(mode.equals("voice"))
            {
                String id=request.getParameter("id");
                //�����͸� �б� ���ؼ� request�� input stream�� ����.
                InputStream inputStream=request.getInputStream();
                //�� ������ ����.
                FileOutputStream outputStream=new FileOutputStream(id+number);

                byte[] buffer=new byte[4096];
                int bytesRead=-1;
                System.out.println("Receiving data......");
                while((bytesRead=inputStream.read(buffer))!=-1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                spw.println("Write the voice to DB");
                spw.flush();
            }
            /**
             * GCM ���񽺸� �ϱ� ���Ͽ� ������ key ����
             * DB�� Register ���̺� �������ش�.
             */
            else if(mode.equals("Register"))
            {
                String regId=request.getParameter("regId");
                SQL="select * from Register where regid=\'"+regId+"\'";

                ResultSet rs=stmt.executeQuery(SQL);

                //SQL�� ���Ǹ� ���� �� ����� �ȵ��ִٸ� ����� ���ش�.
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
