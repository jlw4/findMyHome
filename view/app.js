'use strict';

var app = angular.module('App',[]);
  
app.controller('MainController', ['$scope', '$location', '$timeout', '$http', '$interval', function($scope, $location, $timeout, $http, $interval) {
    
	$("#loadingThingy").fadeIn(2500);
	
	var defaultLat = 47.62;
	var defaultLong = -122.325;
	
    var map;
    var homeMap;
    var serverUrl = "http://54.149.5.12:8080";
    var hostUrl = serverUrl;
    if ($location.$$protocol == "file" || $location.host() == "localhost") {
        hostUrl = "http://localhost:8080";
    }
    var animationDuration = 1500.0; // progress bar animation
    var durationInS = ( animationDuration / 1000 ) + "s";
    $(".progress-bar").css("-webkit-transition", "width " + durationInS + " ease-in-out");
    $(".progress-bar").css("transition", "width " + durationInS + " ease-in-out");
    
    function load() {
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
    
    function navigate(path) {
    	$("#neighborhoodNav").hide();
    	$(".displayBox").hide();
    	$(".navTab").removeClass("active");
    	$location.path(path);
    }
    
    function loadHomeMap() {
    	var mapProp = {
            center: new google.maps.LatLng(defaultLat, defaultLong),
            zoom: 11,
            mapTypeId:google.maps.MapTypeId.ROADMAP
        };

        homeMap = new google.maps.Map(document.getElementById("homeMap"),mapProp);
        var kmlUrl = serverUrl + "/kml?neighborhood=none";
        
		var kmlOptions = {
  			preserveViewport: true,
  			map: homeMap
		};
		var kmlLayer = new google.maps.KmlLayer(kmlUrl, kmlOptions);
		kmlLayer.setMap(homeMap);
		google.maps.event.addListener(kmlLayer, 'click', function(kmlEvent) {
			$scope.goNeighborhood(kmlEvent.featureData.name);
		});
		google.maps.event.addListener(homeMap, 'mousemove', function(event) {
			console.log(event);
			var lat = event.latLng.k;
			var long = event.latLng.D;
			var req = {
		            method: 'GET',
		            url: hostUrl + "/coordToHood",
		            params: {long: long, lat: lat}
		        }
		        $http(req).success(function(res){
		            console.log(res);
		        }).error(function(res){
		        	console.log("err");
		        	console.log(res);
		        });
		});
    }
    
    function loadNeighborhoodMap() {
    	var mapProp = {
            center: new google.maps.LatLng(defaultLong, defaultLat),
            zoom: 14,
            mapTypeId:google.maps.MapTypeId.ROADMAP
        };
    	if ($scope.neighborhood != null)
    		mapProp.center = new google.maps.LatLng($scope.neighborhood.longitude,$scope.neighborhood.latitude);
        console.log(mapProp.center)
        map = new google.maps.Map(document.getElementById("neighborhoodMap"),mapProp);
        
        
        var kmlUrl = serverUrl + "/kml";
        if ($scope.neighborhood != null) {
        	 kmlUrl += "?neighborhood=" + encodeURIComponent($scope.neighborhood.name);
        }
        
		var kmlOptions = {
  			preserveViewport: true,
  			map: map
		};
		var kmlLayer = new google.maps.KmlLayer(kmlUrl, kmlOptions);
		kmlLayer.setMap(map);
		
		google.maps.event.addListener(kmlLayer, 'click', function(kmlEvent) {
			$scope.goNeighborhood(kmlEvent.featureData.name);
		});
    }
    
    $scope.goHome = function() {
    	loadHomeMap();
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
    
    $scope.scrollToBottom = function() {
    	window.scroll(0,document.body.scrollHeight)
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
    		// $("#schoolBar").width($scope.neighborhood.schoolRating+ "%");
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
					break;
				}
			}
			$scope.goHome();
			break;
    	}
    }

    load();
	
	$(".container-fluid").fadeIn();
}]);
