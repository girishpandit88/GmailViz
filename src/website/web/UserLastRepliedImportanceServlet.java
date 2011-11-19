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

public class UserLastRepliedImportanceServlet extends HttpServlet {

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
		String[] user = new String[100];
		Float[] importance = new Float[100];
		int i=0;
		
		jsonreply = jsonreply + "{\"" + "Importance\":[";
		Statement imp = connection.createStatement();
		ResultSet simp = imp.executeQuery("SELECT DISTINCT CONTACTEMAIL, CONTACTIMP FROM CONTACT WHERE CONTACTEMAIL NOT LIKE '%com,%' ORDER BY CONTACTIMP ASC");
		while(simp.next())
		{
			importance[i] = simp.getFloat("CONTACTIMP");
			user[i] = simp.getString("CONTACTEMAIL");
			jsonreply += "{\"email_id\":\""+user[i]+"\",\"user_imp\":\""+importance[i]+"\"},";
			i++;

		}	
	
		jsonreply = jsonreply.substring(0, jsonreply.length() -1);
		jsonreply+= "]}";
		System.out.println(jsonreply);
		connection.close();
		return jsonreply;
	}

}