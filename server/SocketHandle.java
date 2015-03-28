import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.sql.*;


public class SocketHandle extends Thread {
	
	//public static String[] client_name;
	public static ArrayList<String> client_name;
	//client_name.add(" ");
	Socket soc;
	PrintWriter spw;
	BufferedReader sbr;
	//생성자
	SocketHandle(){client_name=new ArrayList<String>();};
	SocketHandle(Socket s)
	{
		this.soc=s;
		if(client_name==null){
		client_name=new ArrayList<String>(); //
		client_name.add(" ");
		}
		System.out.println("list 초기화 완료.....");
		try{
		spw=new PrintWriter(soc.getOutputStream(),true);
		sbr=new BufferedReader(new InputStreamReader(soc.getInputStream()));
		System.out.println("spw,sbr 초기화 완료.....");
		//client_name.add(" ");
		}catch(Exception e){}
	}
	
	public void run()
	{
		while(true)//연결 되 있으면 계속 루프를 돈다
		{
			try{				
				String information=sbr.readLine();
				System.out.println(information);
				if(information.indexOf("_login")!=-1)
				{
					System.out.println("Add List");
					//접속시 add list
					String[] realinfo=information.split("=");
					
					//DB 연동
					String driverName = "com.mysql.jdbc.Driver"; // 드라이버 이름 지정
	                String DBName = "ik";
	                String dbURL = "jdbc:mysql://173.194.233.38/"+DBName; // URL 지정
	                String SQL = "select id from cloinfo where id='"+realinfo[1]+"';";
					
	                Class.forName(driverName); // 드라이버 로드
	                Connection con  = DriverManager.getConnection(dbURL,"root",""); // 연결
	                System.out.println("Mysql DB Connection.");
	               
	                Statement stmt = con.createStatement();
	                stmt.executeQuery(SQL);
	                
	                ResultSet result = stmt.executeQuery(SQL);
	                if(!result.next()){
	                	System.out.println("There is no id "+realinfo[1]);
	                	spw.println(0);
	                	spw.flush();
	                }else{
	                	System.out.println("There is id "+realinfo[1]);
	                	spw.println(1);
	                	spw.flush();
	                	
	                }
				}
				else if(information.indexOf("_delete")!=-1)
				{
					
				}
				else if(information.indexOf("_refresh")!=-1)
				{
					
				}
		
			}catch(Exception e){}
		}
	
	}
	
}
