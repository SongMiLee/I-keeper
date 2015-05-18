package com.google.sampleapp.backend;

import java.sql.*;
import java.util.List;

/**
 * Created by user on 2015-05-16.
 */
public class Data {
    String SQL;
    Connection conn;
    Statement stmt;

    List<String> devices;

    public List<String> returnValue(){
        try
        {
            SQL = "select * from Register";

            Class.forName("com.mysql.jdbc.GoogleDriver");
            conn=DriverManager.getConnection("jdbc:google:mysql://alert-height-91305:keeper/ik?user=root");
            stmt=conn.createStatement();

            ResultSet rs=stmt.executeQuery(SQL);

            while(rs.next()){
                devices.add(rs.getString("regid"));
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return devices;
    }
}
