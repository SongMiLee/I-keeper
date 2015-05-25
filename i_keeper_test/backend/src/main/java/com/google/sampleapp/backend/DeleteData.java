package com.google.sampleapp.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by user on 2015-05-16.
 */
public class DeleteData {
    String SQL;
    Connection conn;
    Statement stmt;
    Calendar cal;

    private static final Logger log = Logger.getLogger(DeleteData.class.getName());

    public void DeleteOld(){
        try
        {
            //db ����̹��� �ҷ����� �����Ѵ�.
            Class.forName("com.mysql.jdbc.GoogleDriver");
            conn= DriverManager.getConnection("jdbc:google:mysql://alert-height-91305:keeper/ik?user=root");

            log.log(Level.WARNING, "Delete old Data");
            //���� �ð� ���� �����´�.
            cal=Calendar.getInstance();
            //���� ��¥���� 3���� ����.
            cal.add(Calendar.DATE, -3);
            cal.add(Calendar.MONTH,1);

            log.log(Level.WARNING, cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DATE));

            //�� �������� 3���� ���� ������ �������ش�.
            SQL = "delete from Sensing where time<=\'"+cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DATE)+"\'";
            log.log(Level.WARNING,SQL);

            stmt=conn.createStatement();

            stmt.executeUpdate(SQL);
        }
        catch(Exception e)
        {
            //������
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
