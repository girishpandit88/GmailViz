var R;	        
	        var importances = 4;
	        var timelines = 5;
	        var importanceList = ["Not Important", "Less Important", "Important", "Very Important"];
	        var timestampList = [];
	        var region = {};
	        
		    $(document).ready(function () {
			    $.ajax({
					type : "POST",
					url : "/GmailViz/userageimp",
					success : function(msg) {												
						msg = eval(' (' + msg + ') ');
						
				        var originX = 200;
				        var originY = 150;
				        var length = 150;
				        var height = 100;
				        var textY = originY + (height/2);			        
				        var textX = originX/2;
				        
				        for(var i = 0; i < importances; i ++ ) {
				        	//console.log( importanceList[i] + " " + textX + " " + textY);
				        	var t = R.text(textX, textY, importanceList[i]);
				        	t.attr({"font-size": 16, "font-family": "Arial, Helvetica, sans-serif"});
				        	textY = textY + height;
				        }
				        
				        textY = originY + (4*height) + 10;
				        
				        for(var i = 0; i < msg.InboxAgeData.length; i++) {
				        	textX = originX + ((i*length));
				        	//console.log(msg.InboxAgeData[i].timestamp + " " + textX + " " + textY);
				        	var t = R.text(textX, textY, msg.InboxAgeData[i].TimeStamp);
				        	timestampList[i] = parseInt(msg.InboxAgeData[i].TimeStampMilliSec);
				        	
				        	t.attr({"font-size": 16, "font-family": "Arial, Helvetica, sans-serif"});
				        }
					},
					failure : function(msg) {
						console.log('Failure : ' + msg);
					},
					callback : function(msg) {
						console.log('Callback : ' + msg);
					}
				});
			    
		        R = Raphael("paper", window.innerWidth, window.innerHeight);
		        var attr = {
		            fill: "#333",
		            stroke: "#666",
		            "stroke-width": 1,
		            "stroke-linejoin": "round"
		        };
		        var originX = 200;
		        var originY = 150;
		        
		        var instanceOriginX;
		        var instanceOriginY;
		        
		        var length = 150;
		        var height = 100;
		        var path = "";
		        //var r = Raphael("holder", 620, 250);
		        for(var imp=0; imp < importances; imp++) {
		        	for(var timeline=0; timeline < timelines; timeline ++ ) {
			        	instanceOriginX = originX + timeline*length;
			        	instanceOriginY = originY + imp*height;
			        	path =  "M" + instanceOriginX + "," + instanceOriginY + "," +
			        			"L" + (instanceOriginX + length) + "," + instanceOriginY + "," +
			        			"L" + (instanceOriginX + length) + "," + (instanceOriginY + height) + "," +
			        			"L" + (instanceOriginX) + "," + (instanceOriginY + height) + "," +
			        			"L" + (instanceOriginX) + "," + instanceOriginY;
			        	region[imp + "" + timeline] = R.path(path).attr(attr);
		        	}
		        }
		        var current = null;
		        for (var state in region) {
		            region[state].color = Raphael.getColor();
		            (function (st, state) {
		                st[0].style.cursor = "pointer";
		                st[0].onmouseover = function () {
		                	current = state;
		                    region[current].animate({fill: "#333", stroke: "#666"}, 500);
		                    st.animate({fill: st.color, stroke: "#ccc"}, 500);
		                    st.toFront();
		                };
		                st[0].onmousedown = function () {
		                	window.location = location.protocol + '//' + location.host + "/GmailViz/mainstat.jsp?importance=" + importanceList[state.substr(0,1)] + "&from=" + timestampList[state.substr(1,1)] + "&to=" + timestampList[parseInt(state.substr(1,1)) + 1];
		                };
		                st[0].onmouseout = function () {
		                	st.animate({fill: "#333", stroke: "#666"}, 500);
		                    st.toFront();
		                };
		            })(region[state], state);
		        }
		    });

			function sleep(milliSeconds){
				var startTime = new Date().getTime();
				while (new Date().getTime() < startTime + milliSeconds);
			}
