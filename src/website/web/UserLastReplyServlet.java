package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserLastReplyServlet extends HttpServlet {

	private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1523:orcl";
	private static final String user = "gapandit";
	private static final String password = "001000715";
	static String param1 = null;
	static String param2 = null;
	protected void doGet(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter pw = null;
		String finduser = null;

		HttpSession session = request.getSession(true);
		param1 = (String) session.getAttribute("email_id");
		param2 = (String) session.getAttribute("password");
		if(param1!=null&&param2!=null)
			System.out.println("email: "+param1+" password: "+param2);
		try {
			pw = response.getWriter();
			pw.write(getLastReply());
		} catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"failure\": \"true\"}");
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		doGet(request, response);
	}

	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(jdbcURL, user,
				password);
		return connection;
	}

	public static String getLastReply() throws Exception
	{
		String jsonreply = "";
		Connection connection = connectToDatabase();
		Statement mstmt = connection.createStatement();

		Timestamp[] lastreplys = new Timestamp[100];
		Timestamp[] lastreplyr = new Timestamp[100];
		Timestamp[] lastreply = new Timestamp[100];

		String[] receiver = new String[100];
		String[] sender = new String[100];
		String[] user = new String[100];
		
		long replys, replyr;
		int counts=0, countr=0, count=0, i, j;
		
		ResultSet sets = mstmt.executeQuery("SELECT DISTINCT SENDERNAME FROM MAIL");
		while(sets.next())
		{	
			Statement stmt = connection.createStatement();
			ResultSet mails = stmt.executeQuery("SELECT * FROM MAIL ORDER BY SENDDATE ASC");
			while(mails.next())
			{			
				if((mails.getString("MESSAGEID").contains("INBOX")) && (sets.getString("SENDERNAME").equals(mails.getString("SENDERNAME"))))
				{
					lastreplys[counts] = mails.getTimestamp("SENDDATE");
					sender[counts] = mails.getString("SENDERNAME");
				}
			}
			mails.close();
			counts++;
		}
		sets.close();

		ResultSet setr = mstmt.executeQuery("SELECT DISTINCT RECEIVERNAME FROM MAIL WHERE RECEIVERNAME NOT LIKE '%com,%'");
		while(setr.next())
		{	
			Statement stmt = connection.createStatement();
			ResultSet mailr = stmt.executeQuery("SELECT * FROM MAIL ORDER BY RECEIVEDATE ASC");
			while(mailr.next())
			{			
				if((mailr.getString("MESSAGEID").contains("Sent Mail")) && (setr.getString("RECEIVERNAME").equals(mailr.getString("RECEIVERNAME"))))
				{
					receiver[countr] = mailr.getString("RECEIVERNAME");
					lastreplyr[countr] = mailr.getTimestamp("RECEIVEDATE");
				}
				
			}
			mailr.close();
			countr++;
		}
		setr.close();
				
		for(i=0;i<counts;i++)
		{
			for(j=0;j<countr;j++)
			{
				if(sender[i].equals(receiver[j]))
				{
					if(lastreplys[i].after(lastreplyr[j]))
					{
						replys = lastreplys[i].getTime();
						lastreply[count] = new Timestamp(replys);
						lastreply[count].setTime(replys);
						user[count] = sender[i];
					}
					else
					{
						replyr = lastreplyr[j].getTime();
						lastreply[count] = new Timestamp(replyr);
						lastreply[count].setTime(replyr);
						user[count] = receiver[j];
					}
				 }
				else
				{
					user[count] = sender[i];
					replys = lastreplys[i].getTime();
					lastreply[count] = new Timestamp(replys);
					lastreply[count].setTime(replys);
				}
			}
			count++;
		 }
		
		for(i=0;i<count;i++)
			System.out.println("User: "+user[i]+" Time: "+lastreply[i]);
		
		jsonreply = jsonreply + "{\"email_id\":\"" +param1+"\",\"password\":\""+param2 + "\",\"UserLastReplied\":[";
		for(i=0;i<count;i++)
		{
			jsonreply += "{\"email_id\":\""+user[i]+"\",\"last_replied\":\""+lastreply[i].getTime()+"\"},";
		}
		jsonreply = jsonreply.substring(0, jsonreply.length() -1);
		jsonreply+= "]}";
		System.out.println(jsonreply);
		connection.close();
		return jsonreply;
	}
}