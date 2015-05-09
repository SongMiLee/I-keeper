/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.google.sampleapp.backend;

import java.io.IOException;

import javax.servlet.http.*;
import java.sql.*;
import java.io.PrintWriter;

public class MyServlet extends HttpServlet {
    String SQL="";
    Statement stmt;
    Connection conn;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        PrintWriter spw=new PrintWriter(resp.getOutputStream());
        try{
            Class.forName("com.mysql.jdbc.GoogleDriver");
            conn=DriverManager.getConnection("jdbc:google:mysql://alert-height-91305:keeper/ik?user=root");
            stmt=conn.createStatement();

            String mode=req.getParameter("Mode");
            if(mode.equals("Login"))
            {
                String id=req.getParameter("id");
                System.out.println(id);
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
                stmt.close();
            }
            conn.close();

        }
        catch(Exception e)
        {
            StringBuffer sb=new StringBuffer();
            sb.append("\n");
            StackTraceElement element[]=e.getStackTrace();
            for(int i=0;i<element.length;i++){
                sb.append(element[i].toString());
                sb.append("\n");
                spw.print(sb);
            }
        }

    }
}
