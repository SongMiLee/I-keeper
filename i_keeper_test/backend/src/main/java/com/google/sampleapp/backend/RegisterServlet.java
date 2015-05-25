package com.google.sampleapp.backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Created by user on 2015-05-15.
 */
public class RegisterServlet extends HttpServlet {
    private static final String PARAMETER_REG_ID = "regId";
    String SQL;
    Connection conn;
    Statement stmt;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter spw=new PrintWriter(response.getOutputStream());
        String regId = request.getParameter(PARAMETER_REG_ID);
        System.out.println("regId"+regId);
        try {
            Class.forName("com.mysql.jdbc.GoogleDriver");
            conn=DriverManager.getConnection("jdbc:google:mysql://alert-height-91305:keeper/ik?user=root");
            stmt=conn.createStatement();

            SQL="select * from Register where regid=\'"+regId+"\'";
            ResultSet rs=stmt.executeQuery(SQL);

            //SQL로 질의를 했을 때 등록이 안되있다면 등록을 해준다.
            if(!rs.next())
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
