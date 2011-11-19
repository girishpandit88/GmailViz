package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class GmailServlet extends HttpServlet {
	Properties databaseConfig = new Properties();

	private static String jdbcURL;
	private static String user;
	private static String password;

	private static int parentMessageCounter = 0;
	private static int childMessageCounter = 0;

	private static HashMap<String, String> contactsMap = new HashMap<String, String>();

	public void initialize() {
		jdbcURL = getServletConfig().getInitParameter("JDBC_URL");
		user = getServletConfig().getInitParameter("user");
		password = getServletConfig().getInitParameter("password");
	}

	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(jdbcURL, user,
				password);
		return connection;
	}

	public static void getTimestampInterval() throws Exception {
		int intervals = 5;
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		java.sql.Timestamp minTimeStamp = null;
		java.sql.Timestamp maxTimeStamp = null;

		ResultSet minTimeStampResultSet = statement
				.executeQuery("SELECT MIN(RECEIVEDATE) AS MINRECEIVE FROM MAIL");
		if (minTimeStampResultSet.next()) {
			minTimeStamp = minTimeStampResultSet.getTimestamp("MINRECEIVE");
		}
		minTimeStampResultSet.close();

		ResultSet maxTimeStampResultSet = statement
				.executeQuery("SELECT MAX(RECEIVEDATE) AS MAXRECEIVE FROM MAIL");
		if (maxTimeStampResultSet.next()) {
			maxTimeStamp = maxTimeStampResultSet.getTimestamp("MAXRECEIVE");
		}
		maxTimeStampResultSet.close();

		System.out.println("Min : " + minTimeStamp);
		System.out.println("Max : " + maxTimeStamp);

		long totalDiff = maxTimeStamp.getTime() - minTimeStamp.getTime();
		long increment = totalDiff / intervals;

		java.sql.Timestamp fromTimeStamp, toTimeStamp = null;
		fromTimeStamp = minTimeStamp;
		toTimeStamp = maxTimeStamp;

		System.out.println("From : " + fromTimeStamp);

		for (int i = 1; i <= intervals; i++) {
			toTimeStamp.setTime(fromTimeStamp.getTime() + increment);
			System.out.println("To : " + toTimeStamp);
			insertTimestampsIntoDB(statement, fromTimeStamp, toTimeStamp);
			fromTimeStamp = toTimeStamp;
			System.out.println("From : " + fromTimeStamp);
		}
		statement.executeUpdate("COMMIT");
		connection.close();
	}

	public static void insertTimestampsIntoDB(Statement statement,
			java.sql.Timestamp from, java.sql.Timestamp to) throws Exception {
		statement
				.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		System.out
				.println("INSERT INTO INBOXAGE (FROMINTERVAL, TOINTERVAL) VALUES('"
						+ from + "','" + to + "')");
		statement
				.executeUpdate("INSERT INTO INBOXAGE (FROMINTERVAL, TOINTERVAL) VALUES('"
						+ from + "','" + to + "')");
	}

	// accept get request
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String userName = request.getParameter("loginUsername");
		String password = request.getParameter("loginPassword");
		HttpSession session = request.getSession(true);
		String param1 = (String) session.getAttribute("email_id");
		String param2 = (String) session.getAttribute("password");
		if (session.getAttribute("email_id")!=null && session.getAttribute("password")!=null) {
			session.setAttribute("email_id", new String(userName));
			session.setAttribute("password", new String(password));
			param1 = (String) session.getAttribute("email_id");
			param1 = (String) session.getAttribute("password");
		} else {
			param1 = userName;
			param2 = password;
			session.setAttribute("email_id", param1);
			session.setAttribute("password", param2);
		}
		PrintWriter pw = null;
		try {
			initialize();
			pw = response.getWriter();
			accessGmail(userName, password);
			pw.write("{\"success\": \"true\"}");
		} catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"failure\": \"true\"}");
		}
	}

	// accept post request
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		doGet(request, response);
	}

	// main batch function: this function triggers all other small batch task
	public void accessGmail(String userName, String password) throws Exception {
		System.out.println("Access Gmail");
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");

		Session session = Session.getDefaultInstance(props, null);

		Store store = session.getStore("imaps");
		store.connect("imap.gmail.com", userName, password);
		Folder[] allFolders = store.getDefaultFolder().list();

		deleteEverything(); // initialize database; only for testing purposes

		loadContacts(); // load contacts into contact table

		for (Folder f : allFolders) {
			if (!f.getName().equals("[Gmail]")) {
				if (f.getName().equals("INBOX")) { // access only the INBOX
													// folder for parent threads
					System.out.println("Parent INBOX");
					getContacts(f);
					getParentMails(f);
				}
			} else {
				Folder[] gmailSubFolders = f.list();
				for (Folder gmailSubFolder : gmailSubFolders) {
					if (gmailSubFolder.getName().equals("Sent Mail")) { // access
																		// only
																		// the
																		// Sent
																		// Mail
																		// for
																		// parent
																		// threads
						System.out.println("Parent Sent mail");
						getContacts(gmailSubFolder);
						getParentMails(gmailSubFolder);
					}
				}
			}
		}

		for (Folder f : allFolders) {
			if (!f.getName().equals("[Gmail]")) {
				if (f.getName().equals("INBOX")) { // access only the INBOX for
													// child threads
					System.out.println("Child INBOX");
					getContacts(f);
					getChildMails(f);
				}
			} else {
				Folder[] gmailSubFolders = f.list();
				for (Folder gmailSubFolder : gmailSubFolders) {
					if (gmailSubFolder.getName().equals("Sent Mail")) { // access
																		// only
																		// Sent
																		// Mail
																		// for
																		// child
																		// threads
						System.out.println("Child Sent Mail");
						getContacts(gmailSubFolder);
						getChildMails(gmailSubFolder);
					}
				}
			}
		}
		getImportance(userName); //
		normalizeMailAverage();
		updateContactImportance();

		getTimestampInterval();

		store.close();
	}

	// delete the database records
	private void deleteEverything() throws Exception {
		System.out.println("Deleteeverything");
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		statement.executeUpdate("DELETE FROM REPLYMAIL");
		statement.executeUpdate("DELETE FROM MAIL");
		statement.executeUpdate("DELETE FROM CONTACT");
		statement.executeUpdate("DELETE FROM INBOXAGE");
		statement.executeUpdate("COMMIT");
		connection.close();
	}

	// load contacts into local hashMap
	private static void loadContacts() throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		ResultSet contactResultSet = statement
				.executeQuery("SELECT contactemail FROM contact");

		while (contactResultSet.next()) {
			String email = contactResultSet.getString("contactemail");
			contactsMap.put(email, email);
		}
		connection.close();
	}

	// get parent thread mails
	public static void getParentMails(Folder folder) throws Exception {
		folder.open(Folder.READ_ONLY);
		Message messages[] = folder.getMessages();

		dumpParentMails(messages, folder.getName());
		folder.close(true);
	}

	@SuppressWarnings("unchecked")
	private static void getContacts(Folder folder) throws Exception {
		String senderNames = null, receiverNames = null;

		folder.open(Folder.READ_ONLY);
		Connection connection = connectToDatabase();
		for (Message message : folder.getMessages()) {
			// access message header to get From and To headers.
			Enumeration<Header> headerEnumeration = message.getAllHeaders();
			while (headerEnumeration.hasMoreElements()) {
				Header header = headerEnumeration.nextElement();
				if (header.getName().equals("From")) {
					senderNames = header.getValue();
				}
				if (header.getName().equals("To")) {
					receiverNames = header.getValue();
				}
			}
			String[] senderName = senderNames.split(",");
			String[] receiverName = receiverNames.split(",");

			java.util.Arrays.sort(receiverName);

			// put sender name in local hashMap
			for (String sender : senderName) {
				String senderEmail = sender.trim();
				if (sender.indexOf("<") != -1) {
					senderEmail = sender.substring(sender.indexOf("<") + 1,
							sender.indexOf(">")).trim();
				}

				if (!contactsMap.containsKey(senderEmail)) {
					putContactsIntoDB(senderEmail, connection);
					contactsMap.put(senderEmail, senderEmail);
				}
			}

			// put receiver name in local hashMap
			receiverNames = "";
			for (String receiver : receiverName) {
				String receiverEmail = receiver.trim();
				if (receiver.indexOf("<") != -1) {
					receiverEmail = receiver.substring(
							receiver.indexOf("<") + 1, receiver.indexOf(">"))
							.trim();
				}
				receiverNames = receiverNames + receiverEmail + ",";

				if (!contactsMap.containsKey(receiverEmail)) {
					putContactsIntoDB(receiverEmail, connection);
					contactsMap.put(receiverEmail, receiverEmail);
				}
			}
			receiverNames = receiverNames.substring(0,
					receiverNames.length() - 1);

			if (!contactsMap.containsKey(receiverNames)) {
				putContactsIntoDB(receiverNames, connection);
				contactsMap.put(receiverNames, receiverNames);
			}
		}
		connection.close();
		folder.close(true);
	}

	// put Contact in the contact table
	private static void putContactsIntoDB(String contact, Connection connection)
			throws Exception {
		Statement statement = connection.createStatement();
		System.out.println("contactemail:"+contact);
		statement
				.executeUpdate("INSERT INTO CONTACT (contactemail, contactimp) VALUES ('"
						+ contact + "', 0)");
		statement.close();
	}

	// get all replies from the parent threads
	private static void getChildMails(Folder folder) throws Exception {
		folder.open(Folder.READ_ONLY);
		Message messages[] = folder.getMessages();
		dumpChildMails(messages, folder.getName());
		folder.close(true);
	}

	@SuppressWarnings("unchecked")
	public static void dumpParentMails(Message[] messages, String folderName)
			throws Exception {
		Connection connection = connectToDatabase();
		String senderName = null;
		String receiverName = null;

		for (Message message : messages) {
			Enumeration<Header> headerEnumeration = message.getAllHeaders();

			while (headerEnumeration.hasMoreElements()) {
				Header header = headerEnumeration.nextElement();
				if (header.getName().equals("From")) {
					senderName = header.getValue();
					if (senderName.indexOf("<") != -1) {
						senderName = senderName.substring(
								senderName.indexOf("<") + 1,
								senderName.indexOf(">")).trim();
					}
				}
				if (header.getName().equals("To")) {
					receiverName = header.getValue().trim();
					String[] receivers = receiverName.split(",");
					java.util.Arrays.sort(receivers);

					receiverName = "";
					for (int i = 0; i < receivers.length; i++) {
						if (receivers[i].indexOf("<") != -1) {
							receiverName = receiverName
									+ receivers[i].substring(
											receivers[i].indexOf("<") + 1,
											receivers[i].indexOf(">")).trim()
									+ ",";
						} else {
							receiverName = receiverName + receivers[i].trim()
									+ ",";
						}
					}
					if (receiverName != "") {
						receiverName = receiverName.substring(0,
								receiverName.length() - 1).trim();
					}
				}
			}
			if (senderName != null) {
				if (message.getSubject() == null) {
					insert(message, senderName, receiverName,
							parentMessageCounter, connection, folderName);
					parentMessageCounter++;
				} else if ((message.getSubject() != null)
						&& (!message.getSubject().startsWith("Re: "))) {
					insert(message, senderName, receiverName,
							parentMessageCounter, connection, folderName);
					parentMessageCounter++;
				}
			}
		}
		connection.close();
	}

	@SuppressWarnings("unchecked")
	public static void dumpChildMails(Message[] messages, String folderName)
			throws Exception {
		Connection connection = connectToDatabase();

		String senderName = null;
		String receiverName = null;

		if (messages.length > 0) {
			for (Message message : messages) {
				Enumeration<Header> headerEnumeration = message.getAllHeaders();
				while (headerEnumeration.hasMoreElements()) {
					Header header = headerEnumeration.nextElement();

					if (header.getName().equals("From")) {
						senderName = header.getValue();
						if (senderName.indexOf("<") != -1) {
							senderName = senderName.substring(
									senderName.indexOf("<") + 1,
									senderName.indexOf(">")).trim();
						}
					}
					if (header.getName().equals("To")) {
						receiverName = header.getValue();
						String[] receivers = receiverName.split(",");
						java.util.Arrays.sort(receivers);

						receiverName = "";
						for (int i = 0; i < receivers.length; i++) {
							if (receivers[i].indexOf("<") != -1) {
								receiverName = receiverName
										+ receivers[i].substring(
												receivers[i].indexOf("<") + 1,
												receivers[i].indexOf(">"))
												.trim() + ",";
							} else {
								receiverName = receiverName
										+ receivers[i].trim() + ",";
							}
						}
						if (receiverName != "") {
							receiverName = receiverName.substring(0,
									receiverName.length() - 1).trim();
						}
					}
				}
				if (senderName != null) {
					if ((message.getSubject() != null)
							&& (message.getSubject().startsWith("Re:"))) {
						if (senderName.indexOf("<") != -1) {
							insertReply(message, senderName, receiverName,
									childMessageCounter, connection, folderName);
							childMessageCounter++;
						} else {
							insertReply(message, senderName, receiverName,
									childMessageCounter, connection, folderName);
							childMessageCounter++;
						}
					}
				}
			}
		}
		connection.close();
	}

	private static String findParentMessageID(String subject,
			Connection connection) {
		try {
			Statement statement = connection.createStatement();
			ResultSet parentMessage = statement
					.executeQuery("SELECT DISTINCT MESSAGEID FROM MAIL WHERE SUBJECT LIKE '"
							+ subject + "%'");

			if (parentMessage.next()) {
				return parentMessage.getString("MESSAGEID");
			}
			parentMessage.close();
		} catch (Exception e) {

		}
		return "";
	}

	private static void insertReply(Message message, String senderName,
			String receiverName, Integer messageCount, Connection connection,
			String folderName) throws Exception {

		Statement statement = connection.createStatement();
		// System.out.println(message.getSubject().replaceAll("^Re: ", ""));

		String parentMessageID = findParentMessageID(message.getSubject()
				.replaceAll("^Re: ", ""), connection);
		// System.out.println("ParentMessage : " + parentMessageID);

		if (parentMessageID.equals("")) {
			// System.out.println("Cant find parent");
		} else {
			// System.out.println("INSERT INTO REPLYMAIL (PARENTMESSAGEID, MESSAGEID, SUBJECT, SENDERNAME, RECEIVERNAME, CONTENTTYPE, FLAGS, RECEIVEDATE, SENDDATE, MAILSIZE) VALUES('"
			// + parentMessageID
			// + "','"
			// + folderName + messageCount
			// + "','"
			// + message.getSubject()
			// + "','"
			// + senderName
			// + "','"
			// + receiverName
			// + "','"
			// + message.getContentType()
			// + "','"
			// + message.getFlags()
			// + "','"
			// + new java.sql.Timestamp(message.getReceivedDate().getTime())
			// + "','"
			// + new java.sql.Timestamp(message.getSentDate().getTime())
			// + "','"
			// + message.getSize() + "')");

			statement
					.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
			statement.executeUpdate("commit");

			statement
					.executeUpdate("INSERT INTO REPLYMAIL (PARENTMESSAGEID, MESSAGEID, SUBJECT, SENDERNAME, RECEIVERNAME, CONTENTTYPE, FLAGS, RECEIVEDATE, SENDDATE, MAILSIZE) VALUES('"
							+ parentMessageID
							+ "','"
							+ folderName
							+ messageCount
							+ "','"
							+ message.getSubject()
							+ "','"
							+ senderName
							+ "','"
							+ receiverName
							+ "','"
							+ message.getContentType()
							+ "','"
							+ message.getFlags()
							+ "','"
							+ new java.sql.Timestamp(message.getReceivedDate()
									.getTime())
							+ "','"
							+ new java.sql.Timestamp(message.getSentDate()
									.getTime())
							+ "','"
							+ message.getSize()
							+ "')");
			statement.executeUpdate("commit");
		}
	}

	public static void insert(Message message, String senderName,
			String receiverName, int messageCount, Connection connection,
			String folderName) throws Exception {
		Statement statement = connection.createStatement();

		// System.out.println("Time : " + message.getReceivedDate().getTime() +
		// "," + new java.sql.Date(message.getReceivedDate().getTime()));
		// System.out.println("INSERT INTO MAIL (MESSAGEID, SUBJECT, SENDERNAME, RECEIVERNAME, CONTENTTYPE, FLAGS, RECEIVEDATE, SENDDATE, MAILSIZE) VALUES('"
		// + folderName + messageCount
		// + "','"
		// + message.getSubject()
		// + "','"
		// + senderName
		// + "','"
		// + receiverName
		// + "','"
		// + message.getContentType()
		// + "','"
		// + message.getFlags()
		// + "','"
		// + new java.sql.Timestamp(message.getReceivedDate().getTime())
		// + "','"
		// + new java.sql.Timestamp(message.getSentDate().getTime())
		// + "','"
		// + message.getSize() + "')");

		statement
				.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		statement.executeUpdate("commit");

		statement
				.executeUpdate("INSERT INTO MAIL (MESSAGEID, SUBJECT, SENDERNAME, RECEIVERNAME, CONTENTTYPE, FLAGS, RECEIVEDATE, SENDDATE, MAILSIZE) VALUES('"
						+ folderName
						+ messageCount
						+ "','"
						+ message.getSubject()
						+ "','"
						+ senderName
						+ "','"
						+ receiverName
						+ "','"
						+ message.getContentType()
						+ "','"
						+ message.getFlags()
						+ "','"
						+ new java.sql.Timestamp(message.getReceivedDate()
								.getTime())
						+ "','"
						+ new java.sql.Timestamp(message.getSentDate()
								.getTime()) + "','" + message.getSize() + "')");

		statement.executeUpdate("commit");
	}

	public static void getImportance(String username) throws Exception {
		System.out.println("getImportance Mail");
		Connection connection = connectToDatabase();
		Statement parentstmt = connection.createStatement();
		ResultSet parentMessage = parentstmt.executeQuery("SELECT * from MAIL");
		long time1 = 0, sign = 0;

		while (parentMessage.next()) {
			int c = 0, evenc = 0;
			String j = "";

			Statement replystmt = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet replyMessage = replystmt
					.executeQuery("SELECT * from REPLYMAIL where PARENTMESSAGEID = '"
							+ parentMessage.getString("MESSAGEID") + "'");

			int replyMessageCount = 0;
			if (!(parentMessage.getString("SENDERNAME").contains(username))) // When
																				// user
																				// receives
																				// a
																				// message
																				// first
			{

				while (replyMessage.next()) {
					replyMessageCount++;
					// System.out.println("Child message : " +
					// replyMessage.getString("MESSAGEID"));
					if (c == 0) // for calculating the time between his reply
								// time - message received time
					{
						time1 = replyMessage.getTimestamp("SENDDATE").getTime()
								- parentMessage.getTimestamp("RECEIVEDATE")
										.getTime();
						// System.out.println("IF time in if c=0, " + time1);
						c++;
						sign = 1;
					} else {
						evenc++;
						if (sign == 1) {
							sign = -1;
							j = "RECEIVEDATE";
							// System.out.println("IF receivedate : " +
							// replyMessage.getTimestamp(j).getTime());
						} else if (sign != 1) {
							sign = 1;
							j = "SENDDATE";
							// System.out.println("IF senddate : " +
							// replyMessage.getTimestamp(j).getTime());
						}

						if (replyMessage.isLast() && (evenc % 2) != 0) {
							// System.out.println("IF skip");
						} else {
							time1 = time1
									+ (sign * replyMessage.getTimestamp(j)
											.getTime());
						}
						// System.out.println("IF time in if c>0 , "+time1);
					}
				}
				replyMessage.close();
			} else { // When user sends a message first
				sign = -1;
				while (replyMessage.next()) {
					replyMessageCount++;
					evenc++;
					if (sign == 1) {
						sign = -1;
						j = "RECEIVEDATE";
						// System.out.println("ELSE receivedate : " +
						// replyMessage.getTimestamp(j).getTime());
					} else {
						sign = 1;
						j = "SENDDATE";
						// System.out.println("ELSE senddate : " +
						// replyMessage.getTimestamp(j).getTime());
					}

					if (replyMessage.isLast() && (evenc % 2) != 0) {
						// System.out.println("ELSE skip");
					} else {
						time1 = time1
								+ (sign * replyMessage.getTimestamp(j)
										.getTime());
					}
					// System.out.println("ELSE time in if c>0 , "+time1);
				}
				replyMessage.close();
			}
			replyMessage = null;

			Double average = (double) (0);
			if (replyMessageCount != 0) {
				average = (double) time1 / replyMessageCount;
			}
			Statement updateImportance = connection.createStatement();
			updateImportance
					.executeUpdate("UPDATE MAIL SET AVG_RESPONSE_TIME = '"
							+ average + "' WHERE MESSAGEID = '"
							+ parentMessage.getString("MESSAGEID") + "'");

			updateImportance.close();
		}
		parentstmt.executeUpdate("COMMIT");
		connection.close();
	}

	public void normalizeMailAverage() throws Exception {
		System.out.println("Normalized average");
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		Double maxAvgResponse = 0.0;
		ResultSet importanceSet = statement
				.executeQuery("SELECT MAX(AVG_RESPONSE_TIME) AS MAX FROM MAIL");
		if (importanceSet.next()) {
			maxAvgResponse = (double) importanceSet.getFloat("MAX");
		}
		// System.out.println("Max : " + maxAvgResponse);
		importanceSet.close();

		ResultSet individualSet = statement
				.executeQuery("SELECT MESSAGEID, SUM(AVG_RESPONSE_TIME) AS SUM FROM MAIL GROUP BY MESSAGEID");
		HashMap<String, Double> importanceHash = new HashMap<String, Double>();

		while (individualSet.next()) {
			importanceHash.put(individualSet.getString("MESSAGEID"),
					(double) (maxAvgResponse - individualSet.getFloat("SUM")));
		}
		individualSet.close();

		updateMailNormalizedImportance(importanceHash);

		statement.executeUpdate("COMMIT");
		System.out.println("Over");
		statement.close();
		connection.close();
	}

	private void updateMailNormalizedImportance(
			HashMap<String, Double> importanceHash) throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		Set<Map.Entry<String, Double>> set = importanceHash.entrySet();
		Iterator<Map.Entry<String, Double>> setIterator1 = set.iterator();
		Double sum = 0.0;

		while (setIterator1.hasNext()) {
			Map.Entry<String, Double> me = (Map.Entry<String, Double>) setIterator1
					.next();
			sum = sum + me.getValue();
		}

		Iterator<Map.Entry<String, Double>> setIterator2 = set.iterator();

		while (setIterator2.hasNext()) {
			Map.Entry<String, Double> me = (Map.Entry<String, Double>) setIterator2
					.next();
			statement.executeUpdate("UPDATE MAIL SET IMPORTANCE = '"
					+ (me.getValue() * 100) / (sum) + "' WHERE MESSAGEID='"
					+ me.getKey() + "'");
		}
		statement.close();
		connection.close();
	}

	private void updateContactImportance(String contact, Double importance)
			throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		// System.out.println("UPDATE CONTACT SET CONTACTIMP = (CONTACTIMP + " +
		// importance + ")/2 WHERE CONTACTEMAIL ='" + contact + "'");

		statement
				.executeUpdate("UPDATE CONTACT SET CONTACTIMP = (CONTACTIMP + "
						+ importance + ")/2 WHERE CONTACTEMAIL ='" + contact
						+ "'");

		statement.close();
		connection.close();
	}

	public void updateContactImportance() throws Exception {
		System.out.println("in update contact");
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		Set<Map.Entry<String, String>> contactSet = contactsMap.entrySet();
		Iterator<Map.Entry<String, String>> contactIterator = contactSet
				.iterator();
		while (contactIterator.hasNext()) {
			Double importance1 = 0.0;
			Double importance2 = 0.0;
			Double sum = 0.0;

			Map.Entry<String, String> me = contactIterator.next();
			ResultSet sumResultSet = statement
					.executeQuery("SELECT MAX(IMPORTANCE) AS MAX FROM MAIL");

			if (sumResultSet.next()) {
				sum = (double) sumResultSet.getFloat("MAX");
			}
			sumResultSet.close();

			ResultSet contactReceiverResultSet = statement
					.executeQuery("SELECT MAX(IMPORTANCE) AS MAX FROM MAIL WHERE RECEIVERNAME LIKE '%"
							+ me.getKey()
							+ "%' and MESSAGEID LIKE 'Sent Mail%'");
			if (contactReceiverResultSet.next()) {
				importance1 = (((double) contactReceiverResultSet
						.getFloat("MAX") * 10) / sum);
			}
			contactReceiverResultSet.close();
			ResultSet contactSenderResultSet = statement
					.executeQuery("SELECT AVG(IMPORTANCE) AS AVG FROM MAIL WHERE SENDERNAME LIKE '%"
							+ me.getKey() + "%' and MESSAGEID LIKE 'INBOX%'");

			if (contactSenderResultSet.next()) {
				importance2 = (((double) contactReceiverResultSet
						.getFloat("AVG") * 10) / sum);
			}
			System.out.println(importance1 + "," + importance2);
			updateContactImportance(me.getValue(),
					((importance1 + importance2) / 2));
			statement.executeUpdate("COMMIT");
			contactSenderResultSet.close();
		}
		connection.close();
		System.out.println("Over");
	}
}