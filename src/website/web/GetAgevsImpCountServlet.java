package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class GetAgevsImpCountServlet extends HttpServlet {
	
	private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1523:orcl";
	private static final String user = "gapandit";
	private static final String password = "001000715";
	private static final SimpleDateFormat monthDayYearformatter = new SimpleDateFormat("MMMMM dd, yyyy");
	private static final SimpleDateFormat monthDayformatter = new SimpleDateFormat("MMMMM dd")
	;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(getAgeVsCountScatterPlot("01-NOV-11 12.28.56.400000 AM","07-NOV-11 01.28.12.000000 AM",0.0,4.0));
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
	
	public static String getAgeVsCountScatterPlot(String start, String end, double fromimp,double toimp) throws Exception {
		String ResultString="";
		Timestamp maxtimestamp=new Timestamp((new java.util.Date()).getTime());
		Timestamp startinterval=gettimeStamp(start);
		Timestamp endinterval= gettimeStamp(end);
		Connection connection = connectToDatabase();
		Statement statement1 = connection.createStatement();
		Statement statement2 = connection.createStatement();		
		statement1.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		statement2.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		statement1.execute("commit");
		statement2.execute("commit");
		String query = "SELECT MAX(RECEIVEDATE)AS MAXTM FROM MAIL WHERE(RECEIVEDATE BETWEEN '"+new java.sql.Timestamp(startinterval.getTime())+"' AND '"+new java.sql.Timestamp(endinterval.getTime())+"') AND (IMPORTANCE >="+fromimp+" AND IMPORTANCE <"+toimp+")";
		System.out.println(query);
		ResultSet maxtimestampset = statement2.executeQuery(query);
		while(maxtimestampset.next()){
			maxtimestamp=maxtimestampset.getTimestamp("MAXTM");
		}
		maxtimestampset.close();
		ResultSet scatterset = statement1.executeQuery("SELECT RECEIVEDATE,IMPORTANCE,SUBJECT,SENDERNAME FROM MAIL WHERE(RECEIVEDATE BETWEEN '"+new java.sql.Timestamp(startinterval.getTime())+"' AND '"+new java.sql.Timestamp(endinterval.getTime())+"') AND (IMPORTANCE >="+fromimp+" AND IMPORTANCE <"+toimp+")");
		ResultString="{\"InboxAgeData\":[";
		while(scatterset.next()){
			double num=scatterset.getTimestamp("RECEIVEDATE").getTime();
			double den=maxtimestamp.getTime();
			double div=(den-num)/360000000;
			ResultString+="{\"TimeStamp\":\""+scatterset.getTimestamp("RECEIVEDATE")+"\",\"TimeStampValue\":\""+Double.toString(div)+"\",\"Importance\":\""+scatterset.getString("IMPORTANCE")+"\",\"Subject\":\""+scatterset.getString("SUBJECT")+"\",\"Sendername\":\""+scatterset.getString("SENDERNAME")+"\"},";
		}
		scatterset.close();
		ResultString = ResultString.substring(0, ResultString.length() -1);
		ResultString+="],\"success\": \"true\"}";
		System.out.println(ResultString);
		
		connection.close();
		return ResultString;
	}
	   @SuppressWarnings("deprecation")
	public static Timestamp gettimeStamp(String ss) {
    	
        DateTimeFormatter fullformatter = new DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral('-').appendMonthOfYearShortText().appendLiteral('-').appendYear(4,4).appendLiteral(' ').appendClockhourOfHalfday(2).appendLiteral('.').appendMinuteOfHour(2).appendLiteral('.').appendSecondOfMinute(2).appendLiteral('.').appendFractionOfSecond(6,6).appendLiteral(' ').appendHalfdayOfDayText().toFormatter();
        
        DateTime dt = fullformatter.parseDateTime(ss); 
        
        Timestamp finaltimestamp=new Timestamp(dt.getYear()+100,dt.getMonthOfYear()-1,dt.getDayOfMonth(),dt.getHourOfDay(),dt.getMinuteOfHour(),dt.getSecondOfMinute(),dt.getMillisOfSecond());
        //System.out.println(dt.getYear()+" "+dt.getMonthOfYear()+" "+dt.getDayOfMonth()+" "+dt.getHourOfDay()+" "+dt.getMinuteOfHour()+" "+dt.getSecondOfMinute()+" "+dt.getMillisOfSecond());
        //System.out.println(finaltimestamp.toString());
        return finaltimestamp;
     }	
}
