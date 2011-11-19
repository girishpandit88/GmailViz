var SEARCH_PREFIX = "#search/";
var APPS_PREFIX = "https://mail.google.com/a/";
var GMAIL_PREFIX = "http://mail.google.com/mail/";
var timestamp = [];
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
		data.setValue(j, 0, msg.UserLastReplied[j].email_id);
		data.setValue(j, 1, parseInt(msg.UserLastReplied[j].last_replied));
		timestamp[j] =parseInt(msg.UserLastReplied[j].last_replied);
	}
	
	var options = {
		'title' : 'Users and when was his last replied',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'backgroundColor':'transparent',
		'colors':['yellow','#004411'],
	};

	var chart = new google.visualization.LineChart(document.getElementById('chart_div1'));
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
		data.setValue(j, 0, msg.Importance[j].email_id);
		data.setValue(j, 1, parseInt(msg.Importance[j].user_imp));
		data.setFormattedValue(j,1,"This user's importance is "+parseInt(msg.Importance[j].user_imp)+" and you have last replied him since "+timestamp[j]);
	}
	var options = {
		'title' : 'Users and when was his last replied',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'backgroundColor':'transparent',
		'colors':['yellow','#004411'],
		vAxis:{title:'Losses',textStyle:{color: 'white'}},
        hAxis:{title:'Losses',textStyle:{color: 'white'}},
        'legend':'none'
	};

	var chart = new google.visualization.LineChart(document.getElementById('chart_div2'));
	chart.draw(data, options);
	google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = "from:";
		value += data.getValue(selectedItem.row, 0);
		console.log(value)
		runSearch(value);
	});	
}