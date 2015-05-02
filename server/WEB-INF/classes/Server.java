import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
public class Server extends HttpServlet{
	static String test="Hello Server";
	String info;

	//DB Access
	String driverName="com.mysql.jdbc.Driver";
   	String dbURL="jdbc:mysql://173.194.85.216/ik";
	String User="toor";
   	String Password="toor";
	String SQL;
   	Connection conn;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out =resp.getWriter();

		out.println("<html>");
		out.println("<body>");
		out.println("<H2>"+test+"</H2>");
		out.println("</body>");
		out.println("</html>");

		out.close();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{

		PrintWriter spw=new PrintWriter(resp.getOutputStream());
		try{
		//read the value from stream
		info="test";
		info=req.getParameter("Mode");
		//init the db connection
                 Class.forName(driverName);
                 conn=DriverManager.getConnection(dbURL,User,Password);
	         Statement stmt=conn.createStatement();
	
		//search the db	
		 String result="tmp";

			if(info.equals("Login")){
			 String id=req.getParameter("id");

			  SQL="select * from cloinfo where id="+id;
			  ResultSet rs=stmt.executeQuery(SQL);

			  if(rs.next()!=false){
				result="true";
			  }
			  else
				result="false";

			  spw.write(result);
			  spw.flush();
			
			}/*
			else if(realinfo[0].equals("_Refresh")){

			  SQL="select * from pulse where id="+];
			  ResultSet rs=stmt.executeQuery(SQL);

			  while(rs.next()){
			     String id=rs.getString("id");
			     int data=rs.getInt("data");
			     Timestamp time=rs.getTimestamp("time");

			     result=time+"="+id+"="+data;
			    
			     spw.write(result);
			     spw.flush();

			  }
			}
			else if(realinfo[0].equals("_touchsensor")){
			}
			else if(realinfo[0].equals("_vibsensor")){
			}
			else if(realinfo[0].equals("_pulsesensor")){
			}*/

		}catch(Exception e) { e.printStackTrace(); }
		finally{	 }
	}
}
