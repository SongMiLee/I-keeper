import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.sql.*;


public class SocketHandle extends Thread {
	
	public static ArrayList<String> client_name;
	Socket soc;
	PrintWriter spw;
	BufferedReader sbr;

	//DB variable
	String driverName;
	String DBName;
	String dbURL;
	String SQL;
	//생성자
	SocketHandle(){client_name=new ArrayList<String>();};
	SocketHandle(Socket s)
	{
		 //DB 연동
                 driverName = "com.mysql.jdbc.Driver"; // 드라이버 이름 지정
                 DBName = "ik";
                 dbURL = "jdbc:mysql://173.194.233.38/"+DBName; // URL 지정

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
					String[] realinfo=information.split("=");
					System.out.println("Login " + realinfo[1]);
					
					SQL = "select id from cloinfo where id='"+realinfo[1]+"';";			
	               			Class.forName(driverName); // 드라이버 로드
			                Connection con  = DriverManager.getConnection(dbURL,"toor","toor"); // 연결
	       			        System.out.println("Mysql DB Connection.");
	               
	             			Statement stmt = con.createStatement();
			                stmt.executeQuery(SQL);//execute the SQL Query
	                
	               			ResultSet result = stmt.executeQuery(SQL);
			                if(!result.next()){//If db didn't find the id
	       			         	System.out.println("There is no id "+realinfo[1]);
	                			spw.println("false");
						spw.flush();
	       			         }else{//db find the id
	                			System.out.println("There is id "+realinfo[1]);
			                	spw.println("true");
						spw.flush();
	                	
	               			 }
					con.close();
				}
				else if(information.indexOf("_sensor")!=-1)
				{
					System.out.println(information);
					spw.println(information);
					spw.flush();	
				}
				else if(information.indexOf("_Refresh")!=-1)
				{
					String[] realinfo=information.split("=");
					System.out.println("Refresh "+realinfo[1]);//clothes ID

					SQL = "select id from pulse_sensor where id='"+realinfo[1]+"';";             
 
                                        Class.forName(driverName); // 드라이버 로드
                                        Connection con  = DriverManager.getConnection(dbURL,"toor","toor"); //DB Connection

                                        System.out.println("Mysql DB Connection.");
                                        Statement stmt = con.createStatement();
                                        stmt.executeQuery(SQL);//execute the SQL Query
                                        ResultSet result = stmt.executeQuery(SQL);
					
				}
		
			}catch(Exception e){e.printStackTrace();}
		}
	
	}
	void addList(String arg)
	{
		
		for(int i=0; i<client_name.size(); i++)
		{
			//중복 검사
			//System.out.println(arg);
			if(client_name.get(i).matches(arg))
				return ;
		}
		client_name.add(arg);
		//client_name.add(arg);
	}
	
}
