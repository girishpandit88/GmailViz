$(document).ready(function() { 

	
		$.ajax({
			type : 'GET',
			url : "/GmailViz/userageimp",
			success : function(msg) {
				drawChart(msg);
			},
			error : function(msg) {
				alert('Failed');
			}
		});
});

google.load("visualization", "1", {packages:["corechart"]});

function drawChart(msg){
	console.log(msg)
	msg = eval('(' + msg + ')');
	console.log(msg)
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Last Replied');
	data.addColumn('number', 'Importance');
	data.addRows(msg.InboxAgeData.length);
	
	for ( var j = 0; j < msg.InboxAgeData.length; j++) {
		data.setValue(j, 0, parseFloat(msg.InboxAgeData[j].TimeStampValue));
		data.setValue(j, 1, parseFloat(msg.InboxAgeData[j].Importance));
//		data.setFormattedValue(j,1,"This user's importance is "+parseInt(msg.InboxAgeData[j].Importance)+" and you have last replied him since "+msg.InboxAgeData.TimeStamp[j]);
	}
	console.log(data)
	var options = {
		'title' : 'Users and when was his last replied',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'colors':['yellow','#004411'],
		'backgroundColor' : 'transparent',
		vAxis:{textStyle:{color: 'red'}, minValue:0, maxValue:1.5},
		hAxis:{textStyle:{color: 'red'}},
	};

	var chart = new google.visualization.ScatterChart(document.getElementById('chart'));
	chart.draw(data, options);
}