<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
	<head>
		<title>Home Page</title>
		<link rel="stylesheet" href="./resources/css/demo.css" type="text/css" media="screen">
	    <link rel="stylesheet" href="./resources/css/demo-print.css" type="text/css" media="print">
	    <script type="text/javascript" src="http://code.jquery.com/jquery-1.6.4.js"></script>
	    <script src="./resources/raphael/raphael.js" type="text/javascript" charset="utf-8"></script>
		<script src="./scripts/homepagejs.js" type="text/javascript" charset="utf-8"></script>
		<script type="text/javascript">
		
		function play() {
			console.log("PLAY");
			for (var state in region) {
					region["00"].mouseover();
			}
		}
		</script>
	</head>
    <body>
    	<a href="#" onclick="play()">Play</a>
        <div id="canvas">
            <div id="paper">
            </div>
        </div>        
    </body>    
</html>
