var msg ={"keywords" : [{"keyword":"OOLS","importance":"0.0"},{"keyword":"Gmail","importance":"7.0"},{"keyword":"Viz","importance":"6.0"}]}



//Load the Visualization API and the piechart package.
google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
});
function drawChart1() {
	// console.log('You choose Bar or Column or ')
	var data = new google.visualization.DataTable();

	data.addColumn('string', 'keyword');
	data.addColumn('number', 'Importance');
	data.addRows(msg.keywords.length);
	for ( var j = 0; j < msg.keywords.length; j++) {
		// console.log(msg.IndividualContactImportance[j].email_id + " " +
		// parseInt(msg.IndividualContactImportance[j].importance));
		data.setValue(j, 0, msg.keywords[j].keyword);
		data.setValue(j, 1, parseInt(msg.keywords[j].importance));
	}
	var options = {
		'title' : 'Users Importance',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
	};

	// Instantiate and draw our chart, passing in some options.
	
		// console.log(type)
		var chart = new google.visualization.ColumnChart(document
				.getElementById('chart_div'));
	chart.draw(data, options);
}
