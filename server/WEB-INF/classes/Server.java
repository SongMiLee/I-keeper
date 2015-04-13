import java.io.IOException;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
public class Server extends HttpServlet{
	String test="Hello Server";
	String info;

	//DB Access
	String driverName="com.mysql.jdbc.Driver";
   	String dbURL="jdbc:mysql://173.194.85.216:3306/ik";
	String User="toor";
   	String Password="toor";
	String SQL;
   	Connection conn;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out=resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<H1>"+test+"</H1>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{

		ObjectInputStream oin = new ObjectInputStream(req.getInputStream());
		ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream());

		try{

		//read the value from stream
		info=(String)oin.readObject();

		//init the db connection
                 Class.forName(driverName);
                 conn=DriverManager.getConnection(dbURL,User,Password);
	         Statement stmt=conn.createStatement();
		
		//realinfo[0]=> command (if application, realinfo[1]=>id)
		 String[] realinfo=info.split("=");
	
		//search the db	
		 String result;

			if(realinfo[0].equals("_login")){

			  SQL="select * from cloinfo where id="+realinfo[1];
			  ResultSet rs=stmt.executeQuery(SQL);

			  if(rs.next()!=false){
				result="true";
			  }
			  else
				result="false";
			
			  oos.writeObject(result); 
			}
			else if(realinfo[0].equals("_Refresh")){

			  SQL="select * from pulse where id="+realinfo[1];
			  ResultSet rs=stmt.executeQuery(SQL);

			  while(rs.next()){
			     String id=rs.getString("id");
			     int data=rs.getInt("data");
			     Timestamp time=rs.getTimestamp("time");

			     result=time+"="+id+"="+data;
			     oos.writeObject(result);

			  }
			}
			else if(realinfo[0].equals("_touchsensor")){
			}
			else if(realinfo[0].equals("_vibsensor")){
			}
			else if(realinfo[0].equals("_pulsesensor")){
			}

		}catch(Exception e) { e.printStackTrace(); }
		finally{	oos.close(); }
		oin.close();
	}
}
