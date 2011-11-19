var SEARCH_PREFIX = "#search/";
var APPS_PREFIX = "https://mail.google.com/a/";
var GMAIL_PREFIX = "http://mail.google.com/mail/";
var timestamp = [];

$(document).ready(function(){

	console.log($("#slides-container").length)
	if($("#slides-container").length > 0)
	{
		$("#myController").jFlow({
			slides: "#slides",
			controller: ".jFlowControl", // must be class, use . sign
			slideWrapper : "#jFlowSlide", // must be id, use # sign
			selectedWrapper: "jFlowSelected",  // just pure text, no sign
			easing: "swing",
			width: "850px",
			auto: true, // set to false to disable auto-slide
			height: "315px",
			duration: 900,
			prev: ".jFlowPrev", // must be class, use . sign
			next: ".jFlowNext" // must be class, use . sign
		});
	}

	$('ul.dropdown').superfish({
		autoArrows: false 
	});  
	$(document).ready(function() {
		$.ajax({
			type : 'GET',
			url : "/GmailViz/userlastreplied",
			success : function(msg) {
				timestampmsg = msg;
				drawChart(msg);
				$.ajax({
					type : 'GET',
					url : "/GmailViz/ulreimp",
					success : function(msg) {
						drawChart1(msg); //individual importance
					},
					error : function(msg) {
						alert('Failed');
					}
				});	
			},
			error : function(msg) {
				alert('Failed');
			}
		});
		$.ajax({
			type : 'GET',
			url : "/GmailViz/impcontact",
			success : function(msg) {
				
					drawPieChart1(msg); //individual importance
//					drawPieChart2(msg); //grouped importance
//				if (type == 'BarChart' || type == 'LineChart'
//						|| type == 'ColumnChart') {
//					drawChart1(msg, type); //individual importance
//					drawChart2(msg, type); //grouped importance
//				}
			},
			error : function(msg) {
				alert('Failed');
			}
		});
		
	});

});


function runSearch(query) {
	console.log('inside RunSearch')
	console.log(MAIL_HOST)
	window.open(
       (MAIL_HOST == "gmail.com" ? GMAIL_PREFIX : APPS_PREFIX + MAIL_HOST) + SEARCH_PREFIX + encodeURIComponent(query) + MAIL_HOST);
}

google.load("visualization", "1", {packages:["corechart"]});


function drawChart(msg) {
	msg = eval('(' + msg + ')');
	console.log(msg)
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Email-id');
	data.addColumn('number', 'Last Replied');
	data.addRows(msg.UserLastReplied.length);
	
	for ( var j = 0; j < msg.UserLastReplied.length; j++) {
		var my_email=msg.UserLastReplied[j].email_id
		var ind=my_email.indexOf("@");
		var email_slice=my_email.slice(0,ind);
		data.setValue(j, 0, email_slice);
		data.setValue(j, 1, parseInt(msg.UserLastReplied[j].last_replied));
		timestamp[j] =parseInt(msg.UserLastReplied[j].last_replied);
	}
	
	var options = {
		'title' : 'Users and when was his last replied',
		'titleTextStyle':{color: 'white'},
		'width' : '100%',
		'height' : '100%',
		'backgroundColor':'transparent',
		'colors':['yellow','#004411'],
		'curveType': 'function',
        'vAxis':{textStyle:{color: 'white'}},
		'hAxis':{textStyle:{color: 'white'}},
		'legend': false,
	};

	var chart = new google.visualization.LineChart(document.getElementById('slide-image'));
	chart.draw(data, options);
	google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = "from:";
		value += data.getValue(selectedItem.row, 0);
		console.log(value)
		runSearch(value);
	});	
}
function drawChart1(msg) {
	// Create and populate the data table.
	msg = eval('(' + msg + ')');
	console.log(msg)
//	console.log('ulreimp: '+timestampmsg)
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Email-id');
	data.addColumn('number', 'Importance');
	data.addRows(msg.Importance.length);
	
	for ( var j = 0; j < msg.Importance.length; j++) {
		var my_email=msg.Importance[j].email_id
		var ind=my_email.indexOf("@");
		var email_slice=my_email.slice(0,ind);
		data.setValue(j, 0, email_slice);
		data.setValue(j, 1, parseInt(msg.Importance[j].user_imp));
		data.setFormattedValue(j,1,"This user's importance is "+parseInt(msg.Importance[j].user_imp)+" and you have last replied him since "+timestamp[j]);
	}
	var options = {
		'title' : 'Users and his importance in your inbox',
		'titleTextStyle':{color: 'white'},
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'backgroundColor':'transparent',
		'colors':['yellow','#004411'],
        vAxis:{textStyle:{color: 'white'}},
		hAxis:{textStyle:{color: 'white'}},
        'legend':'none',
        'curveType': 'function'
	};

	var chart = new google.visualization.LineChart(document.getElementById('slide-image slide-image-right'));
	chart.draw(data, options);
	google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = "from:";
		value += data.getValue(selectedItem.row, 0);
		console.log(value)
		runSearch(value);
	});	
}

function drawPieChart1(msg) {
	console.log('You choose Pie')
	var data = new google.visualization.DataTable();

	msg = eval('(' + msg + ')');
	console.log(msg);
	data.addColumn('string', 'Users');
	data.addColumn('number', 'Importance');

	for ( var j = 0; j < msg.IndividualContactImportance.length; j++) {
		data
				.addRows([
						[
								msg.IndividualContactImportance[j].email_id,
								parseInt(msg.IndividualContactImportance[j].importance) ], ]);
	}
	console.log(data)
	var options = {
		'title' : 'Individual Users importance',
		'is3D' : 'true',
		'width' : '100%',
		'height' : '100%',
		'backgroundColor' : 'transparent',
		'legendTextStyle' : {color: 'white', fontName: 'Verdana', fontSize: 10},
		'chartArea': {left:20,top:0,width:"70%",height:"75%"}
	};

	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.PieChart(document.getElementById('slide-image slide-image-right-pie1'));
	chart.draw(data, options);

	console.log('Youe exit pIE')
}
