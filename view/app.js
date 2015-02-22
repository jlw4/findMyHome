'use strict';

var app = angular.module('App',[]);
  
app.controller('MainController', ['$scope', '$location', '$timeout', '$http', '$interval', function($scope, $location, $timeout, $http, $interval) {
    
	$("#loadingThingy").fadeIn(2500);
	
	var defaultLat = 47.606168;
	var defaultLong = -122.329383;
	
    var map;
    var hostUrl = "http://54.149.5.12:8080";
    if ($location.$$protocol == "file") {
        hostUrl = "http://localhost:8080";
    }
    var animationDuration = 1500.0; // progress bar animation
    var durationInS = ( animationDuration / 1000 ) + "s";
    $(".progress-bar").css("-webkit-transition", "width " + durationInS + " ease-in-out");
    $(".progress-bar").css("transition", "width " + durationInS + " ease-in-out");
    
    function load() {
        loadNeighborhoodMap();
        loadNames();
    }
    
    function loadNames() {
    	console.log("fetching neighborhood names");
        var req = {
            method: 'GET',
            url: hostUrl + "/names"
        }
        $http(req).success(function(res){
            console.log("successfully fetched names");
            // put names in 4 lists for columns on browse page
            $scope.neighborhoodNames = res.slice();
            $scope.names = [];
            var size = res.length / 4;
            for (var i = 0; i < 4; i++) {
            	$scope.names[i] = [];
            	while (res.length > 0 && $scope.names[i].length < size) {
            		$scope.names[i].push(res.shift());
            	}
            }
            determineNavigation();
            $scope.loaded = true; // this hides spinner and displays body
        }).error(function(res){
        	$("#loadingThingy").hide();
            console.log("error loading names");
            console.log(res);
            $("#errorThingy").show();
        });
    }
    
    function determineNavigation() {
    	switch ($location.path()) {
		case ("/search"):
			$scope.goSearch();
			break;
		case ("/browse"):
			$scope.goBrowse();
			break;
		default:
			if ($location.path().length > 1) {
				var path = $location.path().substring(1);
				if ($scope.neighborhoodNames.indexOf(path) > -1) {
					$scope.goNeighborhood(path);
				}
			} else
				$scope.goHome();
			break;
    	}
    }
    
    function navigate(path) {
    	$("#neighborhoodNav").hide();
    	$(".displayBox").hide();
    	$(".navTab").removeClass("active");
    	$location.path(path);
    }
    
    function loadNeighborhoodMap() {
    	console.log("loading google map");
    	var mapProp = {
            center: new google.maps.LatLng(defaultLong, defaultLat),
            zoom: 14,
            mapTypeId:google.maps.MapTypeId.ROADMAP
        };
    	if ($scope.neighborhood != null)
    		mapProp.center = new google.maps.LatLng($scope.neighborhood.longitude,$scope.neighborhood.latitude);
        console.log(mapProp.center)
        map = new google.maps.Map(document.getElementById("neighborhoodMap"),mapProp);
        
        
        var kmlUrl = 'http://dl.dropboxusercontent.com/s/znszfb7dm871xel/Neighborhoods.kml?dl=0';
		var kmlOptions = {
  			preserveViewport: true,
  			map: map
		};
		var kmlLayer = new google.maps.KmlLayer(kmlUrl, kmlOptions);
		kmlLayer.setMap(map);
        
        
        console.log("successfully loaded map");
    }
    
    $scope.goHome = function() {
    	navigate("home");
    	$("#homeBox").show();
    	$("#homeNav").addClass("active");
    };
    
    $scope.goSearch = function() {
    	navigate("search");
    	$("#searchBox").show();
    	$("#searchNav").addClass("active");
    };
    
    $scope.goBrowse = function() {
    	navigate("browse");
    	$("#browseBox").show();
    	$("#browseNav").addClass("active");
    };
    
    $scope.goNeighborhood = function(name) {
    	navigate(name);
    	$("#neighborhoodNav").html("<a>" + name + "</a>");
    	$("#neighborhoodNav").show();
    	$("#neighborhoodNav").addClass("active");
    	console.log("fetching neighborhood " + name);
        var req = {
            method: 'GET',
            url: hostUrl + "/neighborhood",
            params: {name: name}
        }
        $http(req).success(function(res){
            console.log("successfully fetched " + name);
            console.log(res);
            $scope.neighborhood = res;
            loadNeighborhoodMap();
        	$(".progress-bar").addClass("progress-bar-striped");
    		$("#schoolBar").width($scope.neighborhood.schoolRating+ "%");
    		$timeout(function(){
        		$(".progress-bar").removeClass("progress-bar-striped");
        	}, animationDuration);
        }).error(function(res){
        	console.log("failed to fetch " + name);
        	console.log(res);
        	$scope.goHome();
        });
    	$("#neighborhoodBox").fadeIn();
    }

    load();
	
	$(".container-fluid").fadeIn();
}]);
