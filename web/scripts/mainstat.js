//var msg = {"InboxAgeData":[{"TimeStamp":"2011-11-12 03:23:19.0","TimeStampMilliSec":"1321086199000","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"1"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-11 23:24:45.0","TimeStampMilliSec":"1321071885000","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"1"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-11 23:24:45.0","TimeStampMilliSec":"1321071885000","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"1"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-09 22:15:43.6","TimeStampMilliSec":"1320894943600","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"0"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-09 19:04:52.4","TimeStampMilliSec":"1320883492400","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"0"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-09 19:04:52.4","TimeStampMilliSec":"1320883492400","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"0"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-07 17:08:08.2","TimeStampMilliSec":"1320703688200","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"0"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-07 14:44:59.8","TimeStampMilliSec":"1320695099800","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"0"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-07 14:44:59.8","TimeStampMilliSec":"1320695099800","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"0"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-05 13:00:32.8","TimeStampMilliSec":"1320512432800","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"0"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-05 11:25:07.2","TimeStampMilliSec":"1320506707200","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"0"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-05 11:25:07.2","TimeStampMilliSec":"1320506707200","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"0"},{"Importance":"0.0","Count":"0"}]},{"TimeStamp":"2011-11-01 02:45:22.0","TimeStampMilliSec":"1320129922000","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"3"},{"Importance":"0.0","Count":"2"}]},{"TimeStamp":"2011-11-01 02:45:22.0","TimeStampMilliSec":"1320129922000","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"3"},{"Importance":"0.0","Count":"2"}]},{"TimeStamp":"2011-11-01 02:45:22.0","TimeStampMilliSec":"1320129922000","ImpCountData": [{"Importance":"7.0","Count":"0"},{"Importance":"5.0","Count":"0"},{"Importance":"2.0","Count":"3"},{"Importance":"0.0","Count":"2"}]}],"success": "true"};
$(document).ready(function() {
//	$.ajax({
//		type : 'GET',
//		url : "/GmailViz/impvage",
//		success : function(msg) {
////			console.log(msg)
//			drawChart(msg);
//			// drawPChart(msg); //individual importance
//		},
//		error : function(msg) {
//			alert('Failed');
//		}
//	});
	drawChart();
});

// google.load("visualization", "1", {packages:["corechart"]});
//google.load("visualization", "1", {packages : [ "treemap" ]});
//
//function drawChart(msg) {
//	// Create and populate the data table.
//	msg = eval('(' + msg + ')');
//	console.log(msg)
//	var data = new google.visualization.DataTable();
//	data.addColumn('string', 'Region');
//	data.addColumn('string', 'Parent');
//	data.addColumn('number', 'Importance (size)');
//	data.addColumn('number', 'Count (color)');
//	data.addRows([["Global", null,0,0]]);
//	console.log(msg.InboxAgeData.length)
//	for(var i=0;i < msg.InboxAgeData.length; i++) {
//		console.log(i)
//		data.addRows([[ msg.InboxAgeData[i].TimeStamp, "Global",0,0]]);		
//	}
//	console.log('Adding parent')
//	console.log(data)
//	for(var i=0; i<msg.InboxAgeData.length; i++){
////		console.log(msg.InboxAgeData[i].ImpCountData[0].Importance+ ' '+msg.InboxAgeData[i].TimeStampMilliSec+' '+parseInt(msg.InboxAgeData[i].ImpCountData[0].Count))
//		data.addRows([[msg.InboxAgeData[i].ImpCountData[0].Importance, msg.InboxAgeData[i].TimeStamp,parseInt(msg.InboxAgeData[i].ImpCountData[0].Importance),parseInt(msg.InboxAgeData[i].ImpCountData[0].Count)] 
//		              ]);
//		data.addRows([[msg.InboxAgeData[i].ImpCountData[1].Importance, msg.InboxAgeData[i].TimeStamp,parseInt(msg.InboxAgeData[i].ImpCountData[1].Importance),parseInt(msg.InboxAgeData[i].ImpCountData[1].Count)] 
//		              ]);
//		data.addRows([[msg.InboxAgeData[i].ImpCountData[2].Importance, msg.InboxAgeData[i].TimeStamp,parseInt(msg.InboxAgeData[i].ImpCountData[2].Importance),parseInt(msg.InboxAgeData[i].ImpCountData[2].Count)] 
//		              ]);
//		data.addRows([[msg.InboxAgeData[i].ImpCountData[3].Importance, msg.InboxAgeData[i].TimeStamp,parseInt(msg.InboxAgeData[i].ImpCountData[3].Importance),parseInt(msg.InboxAgeData[i].ImpCountData[3].Count)] 
//		              ]);
//	}
//	
////	var temp='';
////	for ( var i = 0; i < msg.InboxAgeData.length; i++) {
//
////		if (msg.InboxAgeData[i].Timestamp != temp) {
////			data.addRows([
////			              [ msg.InboxAgeData[i].TimeStamp, null,0,0],
////			             ]);
////			console.log('count: ' + msg.InboxAgeData[i].Count + ' Imp: '+ msg.InboxAgeData[i].Importance)
////			temp = msg.InboxAgeData[i].Timestamp;
////		}
//
////	}
//	
//	console.log(data)
//	// Create and draw the visualization.
//	var tree = new google.visualization.TreeMap(document
//			.getElementById('visualization'));
//	tree.draw(data, {
//		minColor : '#0000dd',
//		midColor : '#dd0000',
//		maxColor : '#00dd00',
//		headerHeight : 15,
//		fontColor : 'white',
//		fontSize : '8',
//		showScale : true
//	});
//}

//google.load("visualization", "1", {packages:["corechart"]});
//
//function drawChart(msg) {
//	msg = eval('(' + msg + ')');
//	console.log(msg)
//	console.log(msg.InboxAgeData.length)
//    var data = new google.visualization.DataTable();
//    data.addColumn('number', 'Time');
//    data.addColumn('number', 'Importance1');
//    data.addColumn('number', 'Importance2');
//    data.addColumn('number', 'Importance3');
//    data.addColumn('number', 'Importance4');
//    data.addRows(msg.InboxAgeData.length);
//    for(var j=0;j<msg.InboxAgeData.length;j++){
//    		data.setValue(j,0,msg.InboxAgeData[j].TimeStampMilliSec%10000000);
//    		data.setValue(j,1,parseInt(msg.InboxAgeData[j].ImpCountData[0].Importance));
//    		data.setValue(j,2,parseInt(msg.InboxAgeData[j].ImpCountData[1].Importance));
//    		data.setValue(j,3,parseInt(msg.InboxAgeData[j].ImpCountData[2].Importance));
//    		data.setValue(j,4,parseInt(msg.InboxAgeData[j].ImpCountData[3].Importance));
//    }
//    var chart = new google.visualization.ScatterChart(document.getElementById('visualization'));
//    chart.draw(data, {width: 600, height: 740,
//                      title: 'Age vs. Weight comparison',
//                      hAxis: {title: 'Age', minValue: 0, maxValue: 15},
//                      vAxis: {title: 'Weight', minValue: 0, maxValue: 15},
//                      legend: 'none'
//                     });
//  }
google.load("visualization", "1", {packages:["corechart"]});
var msg = {"ImpvsTS":[
                                 {"Sub":"abcd","sender":"abcd@gmail.com","importance":"1","timestamp":"1.8","date":"15 Nov 2011"},
                                 {"Sub":"hello","sender":"abcd@gmail.com","importance":"2","timestamp":"12.4","date":"21 August 2011"},
                                 {"Sub":"hi","sender":"acd@gmail.com","importance":"23.6","timestamp":"22.6","date":"1 Febuaray 2011"},
                                 {"Sub":"fu","sender":"ad@gmail.com","importance":"3.9","timestamp":"3.3","date":"21 October 2011"},
                                 {"Sub":"su","sender":"a@gmail.com","importance":"3.1","timestamp":"13.8","date":"31 August 2011"},
                                 {"Sub":"efg","sender":"abc@gmail.com","importance":"1.4","timestamp":"4.1","date":"3 Noc 2011"},
                                 {"Sub":"rafinha alcantara is the best","sender":"abd@gmail.com","importance":"19","timestamp":"10.9","date":"1 August 2011"},                      
                                 ]};

var SEARCH_PREFIX = "#search/";
var APPS_PREFIX = "https://mail.google.com/a/";
var GMAIL_PREFIX = "http://mail.google.com/mail/";

function runSearch(query) {
	console.log('inside RunSearch')
	console.log(MAIL_HOST)
	window.open(
       (MAIL_HOST == "gmail.com" ? GMAIL_PREFIX : APPS_PREFIX + MAIL_HOST) + 
       SEARCH_PREFIX +
       encodeURIComponent(query)
       // Use window name so that if left open, the instance will be reused and
       // Gmail doesn't have to be reloaded
       + MAIL_HOST);
	console.log('exiting runSearch')
 }

 function drawChart(){
	 console.log(msg)
   var data = new google.visualization.DataTable();
   data.addColumn('number', 'Sub');
   data.addColumn('number', 'Sender');
   data.addRows(msg.ImpvsTS.length);
   for(var j=0;j<msg.ImpvsTS.length;j++){
	   console.log(msg.ImpvsTS[j].date)
   		data.setValue(j,0,parseFloat(msg.ImpvsTS[j].importance),msg.ImpvsTS[j].date);
   		data.setValue(j,1,parseFloat(msg.ImpvsTS[j].timestamp),msg.ImpvsTS[j].date);
   		data.setFormattedValue(j,0,msg.ImpvsTS[j].sender);
   		data.setFormattedValue(j,1,msg.ImpvsTS[j].Sub);
   }
   var options = {
			'title' : 'Users and when was his last replied',
			is3D : true,
			'width' : '100%',
			'height' : '100%',
			'backgroundColor':'transparent',
			'colors':['yellow','#004411'],
			title: 'Age vs. Weight comparison',
            hAxis: {title: 'Time', minValue: 0, maxValue: 15},
            vAxis: {title: 'Importance', minValue: 0, maxValue: 15},
            legend: 'none'
   };
   var chart = new google.visualization.ScatterChart(document.getElementById('visualization'));
   chart.draw(data, options);
   google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = "subj:";
		value += data.getFormattedValue(selectedItem.row, 0);
		console.log(value)
		runSearch(value);
	});
}