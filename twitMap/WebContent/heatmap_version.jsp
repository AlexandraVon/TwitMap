<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Heatmaps</title>
    <style>
      html, body, #map-canvas {
        height: 100%;
        margin: 0px;
        padding: 0px
      }
      #panel {
        position: absolute;
        top: 5px;
        left: 50%;
        margin-left: -180px;
        z-index: 5;
        background-color: #fff;
        padding: 5px;
        border: 1px solid #999;
      }
    </style>
    
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=visualization"></script>
    
    <script src="//code.jquery.com/jquery-1.11.2.min.js"></script>
	<script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
    
    <!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="css/clean-blog.min.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="http://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href='http://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
    
    <script>
// Adding 500 Data Points
var map, pointarray, heatmap;

var taxiData = [];
var layers = [];

var chatClient = new WebSocket("ws://52.0.79.15:8080/TwitMap");
var markers = [];

var curKW = "";
var reset = false;

chatClient.onopen = function(){
	setInterval(function(){
		if (reset){
			if(layers.length>0){
				for(i=0;i<layers.length;i++){
					layers[i].setMap(null);
					layers[i] = null;
				}
				layers = [];
			}
			reset = false;
		}
		chatClient.send(curKW);
	},3000);
}

chatClient.onmessage = function(evt) {
	taxiData = [];
    var rawString = evt.data;
  	var rawJSON = JSON.parse(rawString);
    for (var loc in rawJSON){
    	var lat = rawJSON[loc].latitude;
    	var lng = rawJSON[loc].longitude;
    	if(lat&&lng){
    		var latlng = new google.maps.LatLng(lat,lng);
    		taxiData.push(latlng);
    	}
    }
    console.log(taxiData)
    if(taxiData){
    	pointArray.clear();
    	for (i=0; i<taxiData.length; i++){
    		pointArray.push(taxiData[i]);
    	}
    }
}

function kwselect(kw){
	reset = true;
	taxiData = [];
	curKW = kw;
}

function initialize() {
  var mapOptions = {
    zoom: 2,
    center: new google.maps.LatLng(46, 0),
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    noClear:false
  };

  map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);

  pointArray = new google.maps.MVCArray(taxiData);

  heatmap = new google.maps.visualization.HeatmapLayer({
    data: pointArray
  });

  heatmap.setMap(map);
}

google.maps.event.addDomListener(window, 'load', initialize);

    </script>
  </head>

  <body>
  <!-- Navigation -->
    <nav class="navbar navbar-default navbar-custom navbar-fixed-top">
        <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header page-scroll">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
            </div>
        </div>
        <!-- /.container -->
    </nav>

    <!-- Page Header -->
    <!-- Set your background image for this header on the line below. -->
    <header class="intro-header" style="background-image: url('img/home-bg.png')">
        <div class="container">
            <div class="row">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                    <div class="site-heading">
                        <h1>TwitMap</h1>
                        <hr class="small">
                        <span class="subheading">Twit the word, twit the world.</span>
                        <div class="dropdown" style="top:140px">
  						<button class="btn btn-info dropdown-toggle" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-expanded="true">
    						Choose Your Keyword
    						<span class="caret"></span>
  						</button>
  						<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu2" style="left:294px">
    					<li role="presentation"><a role="menuitem" tabindex="-1" href="#" style="text-align:center" onclick="kwselect('love')">Love</a></li>
    					<li role="presentation"><a role="menuitem" tabindex="-1" href="#" style="text-align:center" onclick="kwselect('snow')">Snow</a></li>
    					<li role="presentation"><a role="menuitem" tabindex="-1" href="#" style="text-align:center" onclick="kwselect('friend')">Friend</a></li>
    					<li role="presentation"><a role="menuitem" tabindex="-1" href="#" style="text-align:center" onclick="kwselect('')">All</a></li>
  						</ul>
						</div>
                    </div>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content -->
        <div id="map-canvas" style="margin-top:-50px;"></div>

    <hr>

    <!-- Footer -->
    <footer>
        <div class="container">
            <div class="row">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                    <ul class="list-inline text-center">
                        <li>
                            <a href="#">
                                <span class="fa-stack fa-lg">
                                    <i class="fa fa-circle fa-stack-2x"></i>
                                    <i class="fa fa-twitter fa-stack-1x fa-inverse"></i>
                                </span>
                            </a>
                        </li>
                        <li>
                            <a href="#">
                                <span class="fa-stack fa-lg">
                                    <i class="fa fa-circle fa-stack-2x"></i>
                                    <i class="fa fa-facebook fa-stack-1x fa-inverse"></i>
                                </span>
                            </a>
                        </li>
                        <li>
                            <a href="#">
                                <span class="fa-stack fa-lg">
                                    <i class="fa fa-circle fa-stack-2x"></i>
                                    <i class="fa fa-github fa-stack-1x fa-inverse"></i>
                                </span>
                            </a>
                        </li>
                    </ul>
                    <p class="copyright text-muted">Copyright &copy; Yuan Feng 2015</p>
                </div>
            </div>
        </div>
    </footer>

    <!-- jQuery -->
    <script src="js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.min.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="js/clean-blog.min.js"></script>

</body>

</html>