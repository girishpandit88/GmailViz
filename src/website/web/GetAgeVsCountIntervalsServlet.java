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

public class GetAgeVsCountIntervalsServlet extends HttpServlet {
	
	private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1523:orcl";
	private static final String user = "gapandit";
	private static final String password = "001000715";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(getAgeVsCount());
		}
		catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"failure\": \"true\"}");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(jdbcURL, user, password);
		return connection;
	}
	
	public static String getAgeVsCount() throws Exception {
		String ResultString="";
		float mostimp=7;
		float imp=5;
		float leastimp=2;
		float notimp=0;
		Connection connection = connectToDatabase();
		Statement statement1 = connection.createStatement();
		//ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY
		statement1.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		//statement1.executeUpdate("commit");
		//get all time intervals from INBOX AGE table
		ResultSet timeintervalset = statement1.executeQuery("SELECT * FROM INBOXAGE ORDER BY FROMINTERVAL DESC");
		ResultString="{\"InboxAgeData\":[";
		while(timeintervalset.next()){
			Timestamp frominterval=timeintervalset.getTimestamp("FROMINTERVAL");
			Timestamp tointerval=timeintervalset.getTimestamp("TOINTERVAL");
			
			ResultString+="{\"TimeStamp\":\""+frominterval+"\",\"TimeStampMilliSec\":\""+frominterval.getTime()+"\",\"ImpCountData\": [";
		
			Statement statement2 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			statement2.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
			statement2.executeUpdate("commit");
			System.out.println("SELECT COUNT(*) AS QTY FROM MAIL WHERE (RECEIVEDATE BETWEEN '"+frominterval+"' AND '"+tointerval+"') AND (IMPORTANCE >="+mostimp+")");
			ResultSet mailcountset7 =statement2.executeQuery("SELECT COUNT(*) AS QTY FROM MAIL WHERE (RECEIVEDATE BETWEEN '"+frominterval+"' AND '"+tointerval+"') AND (IMPORTANCE >="+mostimp+")");
			
			Statement statement3 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			statement3.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
			statement3.executeUpdate("commit");
			System.out.println("SELECT COUNT(*) AS QTY FROM MAIL WHERE (RECEIVEDATE BETWEEN '"+frominterval+"' AND '"+tointerval+"') AND (IMPORTANCE >="+imp+" AND IMPORTANCE <"+mostimp+")");
			ResultSet mailcountset5 =statement3.executeQuery("SELECT COUNT(*) AS QTY FROM MAIL WHERE (RECEIVEDATE BETWEEN '"+frominterval+"' AND '"+tointerval+"') AND (IMPORTANCE >="+imp+" AND IMPORTANCE <"+mostimp+")");
			
			Statement statement4 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			statement4.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
			statement4.executeUpdate("commit");
			System.out.println("SELECT COUNT(*) AS QTY FROM MAIL WHERE (RECEIVEDATE BETWEEN '"+frominterval+"' AND '"+tointerval+"') AND (IMPORTANCE >="+leastimp+" AND IMPORTANCE <"+imp+")");
			ResultSet mailcountset2 =statement4.executeQuery("SELECT COUNT(*) AS QTY FROM MAIL WHERE (RECEIVEDATE BETWEEN '"+frominterval+"' AND '"+tointerval+"') AND (IMPORTANCE >="+leastimp+" AND IMPORTANCE <"+imp+")");
			
			Statement statement5 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			statement5.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
			statement5.executeUpdate("commit");
	    	System.out.println("SELECT COUNT(*) AS QTY FROM MAIL WHERE (RECEIVEDATE BETWEEN '"+frominterval+"' AND '"+tointerval+"') AND (IMPORTANCE >="+notimp+" AND IMPORTANCE <"+leastimp+")");
			ResultSet mailcountset0 =statement5.executeQuery("SELECT COUNT(*) AS QTY FROM MAIL WHERE (RECEIVEDATE BETWEEN '"+frominterval+"' AND '"+tointerval+"') AND (IMPORTANCE >="+notimp+" AND IMPORTANCE <"+leastimp+")");
			
			while(mailcountset7.next()){
				ResultString+="{\"Importance\":\""+mostimp+"\",\"Count\":\""+mailcountset7.getString("QTY")+"\"},";	
			}
			mailcountset7.close();
			while(mailcountset5.next()){
				ResultString+="{\"Importance\":\""+imp+"\",\"Count\":\""+mailcountset5.getString("QTY")+"\"},";	
			}
			mailcountset5.close();
			
			while(mailcountset2.next()){
				ResultString+="{\"Importance\":\""+leastimp+"\",\"Count\":\""+mailcountset2.getString("QTY")+"\"},";
			}
			mailcountset2.close();
			
			while(mailcountset0.next()){
				ResultString+="{\"Importance\":\""+notimp+"\",\"Count\":\""+mailcountset0.getString("QTY")+"\"}";	
			}
			mailcountset0.close();
			ResultString+="]},";
		}
		ResultString = ResultString.substring(0, ResultString.length() -1);
		ResultString+="],\"success\": \"true\"}";
		System.out.println(ResultString);
		connection.close();
		return ResultString;
	}
}