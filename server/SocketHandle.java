import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


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
			if(information.indexOf("_add")!=-1)
			{
				System.out.println("Add List");
				//접속시 add list
				String[] realinfo=information.split("=");
				addList(realinfo[1]);
				//System.out.println("Log "+realinfo[0]+" "+realinfo[1]);
				//showArrayList();
			}
			else if(information.indexOf("_delete")!=-1)
			{
				//소켓의 연결이 끊기면 delete
				System.out.println("delete List");
				String[] realinfo=information.split("=");
				delList(realinfo[1]);
			}
			else if(information.indexOf("_refresh")!=-1)
			{
				System.out.println("refresh List");
				//수정된 내용을 전달 
				refreshList();
			}
			//refreshList();
			showArrayList();
			for(int i=0; i<client_name.size(); i++)
			{
				if(!soc.isConnected())
				{
					String[] realinfo=information.split("=");
					delList(realinfo[1]);
				}
				
			}
			}catch(Exception e){}
		}
		//delList(soc.getInetAddress().toString());//연결이 끊기면 지워준다.
		//refreshList();
	}
	void addList(String arg){
		
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
	void delList(String arg){
		for(int i=0; i<client_name.size(); i++)
		{
			if(client_name.get(i).equals(arg))
			{
				client_name.remove(i);
			}
			
		}
	}
	void showArrayList()
	{
		for(int i=0; i<client_name.size(); i++)
		{
			System.out.println(client_name.get(i).toString());
		}
	}
	void refreshList()
	{
		for(int  i=0; i<client_name.size(); i++)
		{
			spw.println(client_name.get(i).toString());
		}
	
	
	}
}
