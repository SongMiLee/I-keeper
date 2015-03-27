import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.SocketHandler;


public class Server {
	
	public static void main(String[] args) throws IOException
	{
		ServerSocket ss=new ServerSocket(52273);
		SocketHandle handler=null;
		Socket ssoc;
		System.out.println("서버 : 클라이언트의 접속을 기다림");
		//
		while(true){
			ssoc=ss.accept(); //클라이언트와 연결될 때 까지 대기
			System.out.println("Server Accept!");
			handler=new SocketHandle(ssoc);
			handler.start();
		}
		
}

}
