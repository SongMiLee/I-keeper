package com.google.sampleapp.backend;

import javax.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.*;
import com.google.appengine.api.utils.SystemProperty;
/**
 * Created by user on 2015-05-13.
 */
public class MyServlet extends HttpServlet {
    String SQL;
    Statement stmt;
    Connection conn;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter spw=new PrintWriter(response.getOutputStream());
        try{
            //Google app engine에 올려서 사용할 때
            if(SystemProperty.environment.value()==SystemProperty.Environment.Value.Production) {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                conn = DriverManager.getConnection("jdbc:google:mysql://alert-height-91305:keeper/ik?user=root");
            }else {
                //Google app engine이 아닌 로컬에서 실행을 할때
                Class.forName("com.mysql.jdbc.Driver");
                conn=DriverManager.getConnection("jdbc:mysql://173.194.244.16/keeper","toor","toor");
            }
            stmt=conn.createStatement();

            String mode=request.getParameter("Mode");

            //Login일 때
            if(mode.equals("Login"))
            {
                //온 값에서 id 값을 분리해 낸다.
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
            //앱에서 TouchSensor_Request를 요청 할 때
            else if(mode.equals("TouchSensor_Request"))
            {
                String id="1st";//request.getParameter("id");
                String r1,r2,l1,l2;
                SQL="SELECT * FROM touchsensor WHERE id=\'"+id+"\'";
                ResultSet rs=stmt.executeQuery(SQL);
                while(rs.next())
                {
                    r1=rs.getString("sensor1");
                    r2=rs.getString("sensor2");
                    l1=rs.getString("sensor3");
                    l2=rs.getString("sensor4");

                    spw.print(r1+"\n"+r2+"\n"+l1+"\n"+l2+"\n");
                    spw.flush();
                }

                //spw.print("20\n30\n40\n50\n60\n70\n");
                //spw.flush();
            }
            //의류에서 TouchSensor 값을 보내고 DB에 작성하는 부분.
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
            stmt.close();
            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();

            StringBuffer sb=new StringBuffer();
            sb.append("\n");
            StackTraceElement element[]=e.getStackTrace();
            for(int i=0;i<element.length;i++)
            {
                sb.append(element[i].toString());
                sb.append("\n");
                spw.print(sb);
            }
        }

    }
}
