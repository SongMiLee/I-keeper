import java.io.IOException;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Server extends HttpServlet{
	DBaccess db;
	String test="Hello Server";
	String info;
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

		 info=(String)oin.readObject();
		 String[] realinfo=info.split("=");
	
		//create the db	
		 db=new DBaccess();
		 String result="wait";

			if(realinfo[0].equals("_login")){
			   result=db.ResultLogin(realinfo[1]);	
			}
		 oos.writeObject(result);
		}catch(Exception e) { e.printStackTrace(); }
		finally{	oos.close(); }
		oin.close();
	}
}
