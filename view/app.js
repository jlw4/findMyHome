'use strict';

var app = angular.module('App',[]);
  
app.controller('MainController', ['$scope', '$location', '$timeout', '$http', '$interval', function($scope, $location, $timeout, $http, $interval) {
    
	$("#loadingThingy").fadeIn(2500);
	
	var defaultLat = 47.62;
	var defaultLong = -122.325;
	
    var map;
    var serverUrl = "http://54.149.5.12:8080";
    var defaultKmlUrl = serverUrl + "/kml?neighborhood=none";
    var defaultSmallUrl = serverUrl + "/smallKml?neighborhood=none";
    var hostUrl = serverUrl;
    if ($location.$$protocol == "file" || $location.host() == "localhost") {
        hostUrl = "http://localhost:8080";
    }
    var animationDuration = 1500.0; // progress bar animation
    var durationInS = ( animationDuration / 1000 ) + "s";
    $(".progress-bar").css("-webkit-transition", "width " + durationInS + " ease-in-out");
    $(".progress-bar").css("transition", "width " + durationInS + " ease-in-out");
    
    var selectedRatings = [];
	
    function load() {
        var req = {
            method: 'GET',
            url: hostUrl + "/names"
        }
        $http(req).success(function(res){
            console.log("loaded neighborhoods");
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
            loadMap();
            $scope.loaded = true; // this hides spinner and displays body
        }).error(function(res){
        	$("#loadingThingy").hide();
            console.log("error loading names");
            console.log(res);
            $("#errorThingy").show();
        });
        var req = {
            method: 'GET',
            url: hostUrl + "/ratings"
        }
        $http(req).success(function(res){
            console.log("successfully fetched ratings");
            $scope.ratings = res;
        }).error(function(res){
            $("#loadingThingy").hide();
            console.log("error loading ratings");
            console.log(res);
            $("#errorThingy").show();
        });

        var req = {
            method: 'GET',
            url: hostUrl + "/colors"
        }
        $http(req).success(function(res){
            console.log("successfully fetched colors");
            $scope.colors = res;
        }).error(function(res){
            $("#loadingThingy").hide();
            $(".container-fluid").hide();
            console.log("error loading colors");
            console.log(res);
            $("#errorThingy").show();
        });

        var req = {
            method: 'GET',
            url: hostUrl + "/container"
        }
        $http(req).success(function(res){
            console.log("successfully fetched container");
            $scope.neighborhoods = res;
            console.log(res);
        }).error(function(res){
            $("#loadingThingy").hide();
            $(".container-fluid").hide();
            console.log("error loading container");
            console.log(res);
            $("#errorThingy").show();
        });
    }
    
    $scope.getColor = function($index) {
    	console.log($index);
    	var color = $scope.colors[$index];
    	return { 'background-color':color,'color':color};
    }
    
    function loadMap() {
    	var mapProp = {
            center: new google.maps.LatLng(defaultLat, defaultLong),
            zoom: 11,
            mapTypeId:google.maps.MapTypeId.ROADMAP
        };

        map = new google.maps.Map(document.getElementById("map"),mapProp);
        
		var kmlOptions = {
  			preserveViewport: true,
  			map: map,
  			clickable: false
		};
		
		map.kmlLayer = new google.maps.KmlLayer(defaultKmlUrl, kmlOptions);
		
		google.maps.event.addListener(map, 'click', function(event) {
            // console.log(event);
            var lat = event.latLng.k;
            var long = event.latLng.D;
            var req = {
                    method: 'GET',
                    url: hostUrl + "/coordToHood",
                    params: {long: long, lat: lat}
                }
            $http(req).success(function(res){
                $scope.goNeighborhood(res.name);
            }).error(function(res){
                console.log("err");
                console.log(res);
            });
        });
		
		google.maps.event.addListener(map, 'mouseout', function(event) {
			$scope.mouseOverName = "";
			hideSmallLayer();
		});
        
        google.maps.event.addListener(map, 'mousemove', function(event) {
            map.mouseOverEvent = event;
        });
    }
    
    $scope.goSearch = function(rating) {
        var i = selectedRatings.indexOf(rating);
        if (selectedRatings.indexOf(rating) == -1) {
            selectedRatings.push(rating);
        }
        else {
            selectedRatings.splice(i,1);
        }
    	map.kmlLayer.setUrl(getSortedUrl());
        if (false && selectedRatings.length > 0) {
	        var req = {
	            method: 'GET',
	            url: hostUrl + "/query",
	            params: {"ratings" : selectedRatings}
	        }
	        $http(req).success(function(res){
	            console.log("successful query");
	            console.log(res);
	        }).error(function(res){
	            console.log("error query");
	            console.log(res);
	        });
        }
    }
    
    $scope.isSelected = function(rating) {
        return selectedRatings.indexOf(rating) > -1;
    }
    
    $scope.scrollToBottom = function() {
    	window.scroll(0,document.body.scrollHeight)
    };
    
    $scope.goNeighborhood = function(name) {
        var req = {
            method: 'GET',
            url: hostUrl + "/neighborhood",
            params: {name: name}
        }
        $http(req).success(function(res){
            console.log("successfully fetched " + name);
            console.log(res);
            $scope.neighborhood = res;
            map.panTo(new google.maps.LatLng(res.longitude, res.latitude));
            map.setZoom(13);
        	$(".progress-bar").addClass("progress-bar-striped");
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
    
    $scope.reset = function() {
    	$("#neighborhoodBox").hide();
    	selectedRatings = [];
    	loadMap();
    }
    
    function hideSmallLayer() {
    	if (map.smallLayer != null) {
			map.smallLayer.setMap(null);
			map.smallLayer = null;
		}
    }
    
    function getSortedUrl() {
    	if (selectedRatings.length == 0)
    		return defaultKmlUrl;
    	var url = serverUrl + "/sortedKml";
    	for (var i = 0; i < selectedRatings.length; i++) {
    		if (i == 0)
    			url += "?";
    		else
    			url += "&";
    		url += "ratings=" + selectedRatings[i];
    	}
    	console.log(url);
    	return url;
    }
    
    function getKmlUrl(name) {
        return serverUrl + "/kml?neighborhood=" + encodeURIComponent(name);
    }
    
    function getSmallUrl(name) {
        return serverUrl + "/smallKml?neighborhood=" + encodeURIComponent(name);
    }
    
    $interval(function() {
        if ($scope.loaded && map.mouseOverEvent != null) {
            var event = map.mouseOverEvent;
            var lat = event.latLng.k;
            var long = event.latLng.D;
            var req = {
                method: 'GET',
                url: hostUrl + "/coordToHood",
                params: {long: long, lat: lat}
            }
            $http(req).success(function(res) {
            	if (res.name == "none") {
            		hideSmallLayer();
            	}
            	else if ($scope.mouseOverName != res.name) {
                    $scope.mouseOverName = res.name;
                    if (map.smallLayer == null) {
                    	var kmlOptions = {
                  			preserveViewport: true,
                  			map: map,
                  			clickable: false
                		};
                		map.smallLayer = new google.maps.KmlLayer(getSmallUrl(res.name), kmlOptions);
                    }
                    else
                    	map.smallLayer.setUrl(getSmallUrl(res.name));
                }
            }).error(function(res) {
                console.log("error in mouseoverevent");
                console.log(res);
            });
            map.mouseOverEvent = null;
        }
    }, 30);

    load();
	
	$(".container-fluid").fadeIn();
}]);
