import java.sql.*;
public class DBaccess{
   //for DB connect
   String driverName="com.mysql.jdbc.Driver";
   String dbURL="jdbc:mysql://173.194.85.216:3306/ik";
   String User="toor";
   String Password="toor";
   Connection conn;

   String[] getInfo;

   //init the class
   public DBaccess(){
	//init the DB 
	try{
		Class.forName(driverName);
		conn=DriverManager.getConnection(dbURL,User,Password);
	}catch(Exception e){}
	
   }
   
   //return the Login value
   public String ResultLogin(String info){
	String result="false";

	try{
		Statement stmt=conn.createStatement();
		ResultSet rs=stmt.executeQuery("select * from cloinfo where="+info);

		if(rs.next()!=false){
		  result="true";
		}
	}catch(Exception e){ e.printStackTrace();}
	return result;
   }

  public void finalize(){
	try{
		conn.close();
	}catch(Exception e){ e.printStackTrace();}
   }
}
