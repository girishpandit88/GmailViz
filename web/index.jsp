<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Gmail Viz - An analysis of your Inbox</title>
<link rel="stylesheet" href="./resources/css/styles.css" type="text/css" />

<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.6.4.js"></script>
<script type="text/javascript" src="./scripts/slider.js"></script>
<script type="text/javascript" src="./scripts/superfish.js"></script>
<script type="text/javascript">
	    var MAIL_HOST = "${host}";
</script>
<script type="text/javascript" src="./scripts/custom.js"></script>

<!--
widget, a free CSS web template by spyka Webmaster (www.spyka.net)

Download: http://www.spyka.net/web-templates/widget/

License: Creative Commons Attribution
//-->
</head>

<body class="homepage">
	<div id="container">
		<div id="header">
			<h1>
				<a href="/">Gmail<strong>Viz</strong>
				</a>
			</h1>
			<h2>Helps you analyze your inbox...</h2>
			<div class="clear"></div>
		</div>
		<div id="nav">
			<ul class="sf-menu dropdown">
				<li class="selected"><a href="index.jsp">Home</a>
				</li>
				<li><a class="has_submenu" href="examples.html">Aggregates</a>
					<ul>
						<li><a href="user_importance.jsp">User Importance</a>
						</li>
						<li><a href="noslides.html">User Last Replied</a>
						</li>
						<li><a href="#">Keyword Analysis</a>
						</li>
					</ul></li>
				<li><a class="has_submenu" href="#">Other Aggregates</a>
					<ul>
						<li><a href="#">Aggregate 1</a>
						</li>
						<li><a href="#">Aggregate 2</a>
						</li>
						<li><a href="#">Aggregate 3</a>
						</li>
					</ul></li>
				<li><a href="#">About</a>
				</li>
				<li><a href="#">Contact</a>
				</li>
				<li><a href="login.jsp">Logout</a>
				</li>
			</ul>
		</div>

		<div id="slides-container" class="slides-container">
			<div id="slides">
				<div>
					<img src="./resources/images/gmail.PNG"
						style="height: 71px; width: 409px;" /> <br><br><br><br>
									<h2>Welcome to Gmail Viz</h2>
									<p>
										A project for Web Viz class at NC-State University that helps
										you analyze your inbox for fun!<br> Disclaimer: No
											malicious attempt to get your username and password is made
											while we analyse your inbox...
									</p>
				</div>
				<div>
					<div class="slide-image" id="slide-image"></div>
					<div class="slide-text">
						<h2>User and last time he was replied back</h2>
						<p>This chart gives the top 10 contact present in your inbox
							and the last you contacted them. The user id is on hAxis while
							the time elasped since you contacted them is on vAxis</p>
					</div>
				</div>

				<div>
					<div class="slide-image slide-image-right" id="slide-image slide-image-right"></div>
					<div class="slide-text">
						<h2>User and his importance in your inbox</h2>
						<p>This chart gives the top 10 contacts present in your inbox
							and their importance. If a particular user in on left side of
							hAxis and his importance is closer to zero on vAxis then he is
							more important to you.</p>

					</div>
				</div>
					<div>
					<div class="slide-image slide-image-right" id="slide-image slide-image-right-pie1"></div>
					<div class="slide-text">
						<h2>User Importances</h2>
						<p>User Importance according to how often you contact them and
							 how quickly you respond to them after they have send a mail
							 to them. You can edit user importance <a href="imp_contact.jsp">here</a> 
						</p>
					</div>
				</div>
			</div>
			<div class="controls">
				<span class="jFlowNext"><span>Next</span>
				</span><span class="jFlowPrev"><span>Prev</span>
				</span>
			</div>
			<div id="myController" class="hidden">
				<span class="jFlowControl">Slide 1</span><span class="jFlowControl">Slide
					1</span><span class="jFlowControl">Slide 1</span><span class="jFlowControl">Slide 1</span>
			</div>
		</div>

		<div id="body">
			<div id="content">
				<div class="box">
					<h2>Introduction</h2>
					<p>
						Gmail Viz is a project that is aimed to analyze for free!!!<br>
							We ask you to login to your gmail account and once your sign-in
							details are authenticated with Gmail servers we run our
							algorithms to scan your inbox and build results for the analysis.<br>
								We have used oracle10g database in the backend to store your
								data in the backend. Once you logout the database is cleared. So
								you are assured that we will not be using your account details
								in any malicious manner. 
					</p>

				</div>
			</div>

			<div class="sidebar">
				<ul>
					<li>
						<h4>
							<span>Team <strong>Members</strong>
							</span>
						</h4>
						<ul>
							<li>
								<p style="margin: 0;">
								<ul>
								<li><a href="http://www4.ncsu.edu/~nrpathak">Nidhi Pathak</a></li>
								<li><a href="http://www4.ncsu.edu/~vsingh3">Vartika Singh</a></li>
								<li><a href="http://www4.ncsu.edu/~sgpai">Sarvesh Pai</a></li>
								<li><a href="http://www4.ncsu.edu/~gapandit">Girish Pandit</a></li>
								</ul></p></li>
						</ul></li>
				</ul>
			</div>
			<div class="clear"></div>
		</div>
	</div>
	<div id="footer">

		<div id="footer-links">

			<!-- A link to http://www.spyka.net must remain. To remove link see http://www.spyka.net/licensing -->
			<p>
				GmailViz &copy; 2011. Website powered by design provided on <a
					href="http://www.spyka.net">spyka.net</a> |
			</p>
		</div>
	</div>
	<div id="popupContact">
		<div id="chart_div"></div>
	</div>
	<div id="backgroundPopup"></div>
</body>
</html>
