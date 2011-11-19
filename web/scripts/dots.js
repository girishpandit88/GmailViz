var msg = {"ImpvsTS":[
                      {"Sub":"abcd","sender":"abcd@gmail.com","importance":"1","timestamp":"1.8"},
                      {"Sub":"hello","sender":"abcd@gmail.com","importance":"2","timestamp":"2.4"},
                      {"Sub":"hi","sender":"acd@gmail.com","importance":"3.6","timestamp":"2.6"},
                      {"Sub":"fu","sender":"ad@gmail.com","importance":"3.9","timestamp":"3.3"},
                      {"Sub":"su","sender":"a@gmail.com","importance":"3.1","timestamp":"3.8"},
                      {"Sub":"efg","sender":"abc@gmail.com","importance":"1.4","timestamp":"4.1"},
                      {"Sub":"rafinha alcantara is the best","sender":"abd@gmail.com","importance":"3","timestamp":"4.9"},                      
                      ]};

console.log(msg)
$(function () {
    // Grab the data
	var R = Raphael("chart", window.innerWidth, window.innerHeight);        
    var importances = 4;
    var timelines = 5;
    var importanceList = ["Not Important", "Less Important", "Important", "Very Importance"];
    window.onload = function () {
       
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
        
        var region = {};
        var path = "";
        for(var imp=0; imp<importances; imp++) {
        	for(var timeline=0; timeline<timelines; timeline ++ ) {
	        	instanceOriginX = originX + timeline*length;
	        	instanceOriginY = originY + imp*height;
//	        	path = "M" + instanceOriginX + "," + instanceOriginY + "," +
//	        			"L" + (instanceOriginX + length) + "," + instanceOriginY + "," +
//	        			"L" + (instanceOriginX + length) + "," + (instanceOriginY + height) + "," +
//	        			"L" + (instanceOriginX) + "," + (instanceOriginY + height) + "," +
//	        			"L" + (instanceOriginX) + "," + instanceOriginY;
//	        	region[imp + "" + timeline] = R.path(path);
        	}
        }
        
        var current = null;
        for (var state in region) {
            region[state].color = Raphael.getColor();
            (function (st, state) {
                st[0].style.cursor = "pointer";
                st[0].onmouseover = function () {
                	current = state;
                	console.log(state + " mouseover");
                    region[current].animate({fill: "#333", stroke: "#666"}, 500);
                    st.animate({fill: st.color, stroke: "#ccc"}, 500);
                    st.toFront();
                    R.safari();
                    
                };
                st[0].onmouseout = function () {
                	console.log(state + " mouseoout");
                	st.animate({fill: "#333", stroke: "#666"}, 500);
                    st.toFront();
                    R.safari();
                };
                if (state == "00") {
                    st[0].onmouseover();
                }
            })(region[state], state);
        }
    };
    
//	$.ajax({
//		type : "POST",
//		url : "/GmailViz/intervals",
//		success : function(msg) {												
//			msg = eval('(' + msg + ')');
			console.log(msg.ImpvsTS.length);
			
	        var originX = 200;
	        var originY = 150;
	        var length = 150;
	        var height = 100;
	        var textY = originY + (height/2);			        
	        var textX = (originX)/2;
	        
	        for(var i = 0; i < importances; i ++ ) {
	        	console.log(importanceList[i]);
	        	var t = R.text(textX, textY, importanceList[i]);
	        	t.attr({ "font-size": 16, "font-family": "Arial, Helvetica, sans-serif" });
	        	textY = textY + height;
	        }
	        
	        textY = originY + (4*height) + 10;
	        
	        for(var i = 0; i < msg.ImpvsTS.length; i++) {
	        	
	        	textX = originX + ((i*length));
	        	console.log(parseInt(msg.ImpvsTS[i].timestamp) + " " + textX + " " + textY);
	        	var t = R.text(textX, textY, msg.ImpvsTS[i].timestamp);
	        	t.attr({ "font-size": 16, "font-family": "Arial, Helvetica, sans-serif" });
//	        	console.log('LLLLLasdasd'+msg.ImpvsTS[i].timestamp)
	        }
	        
	        for(var i=0;i<msg.ImpvsTS.length;i++){
	        	console.log(msg.ImpvsTS[i].timestamp)
	        	console.log(msg.ImpvsTS[i].importance)
	        	textY = originY + (parseInt(msg.ImpvsTS[i].importance)*height);
	        	textX = originX + ((timelines - parseInt(msg.ImpvsTS[i].timestamp))*length);
	        	console.log(parseInt(msg.ImpvsTS[i].timestamp) + " " + textX + " " + textY);
	        	var dot = R.circle(textX, textY, parseInt(msg.ImpvsTS[i].importance)*5);
	        	var text = R.text(textX, textY, msg.ImpvsTS[i].Sub);
	        	text.attr({ "font-size": 16, "font-family": "Arial, Helvetica, sans-serif" });
	        	text.hide();
	        	dot.attr("fill", "#f00");
	        	dot.attr("stroke", "#fff");
	        	dot[0].onmouseover = function(){
	        		text.show();
	        	};
	        	dot[0].onmouseout = function(){
	        		text.hide();
	        	};
	        }
//		},
//		failure : function(msg) {
//			console.log('Failure : ' + msg);
//		},
//		callback : function(msg) {
//			console.log('Callback : ' + msg);
//		}
//	});
});