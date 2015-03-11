'use strict';

var app = angular.module('App',[]);
  
app.controller('MainController', ['$scope', '$location', '$timeout', '$http', '$interval', function($scope, $location, $timeout, $http, $interval) {
    
	$("#loadingThingy").fadeIn(2500);
	
	var defaultLat = 47.62;
	var defaultLong = -122.325;
	
    var map;
    var serverUrl = "http://54.149.5.12:8080";
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
            loadMap();
            $scope.loaded = true; // this hides spinner and displays body
        }).error(function(res){
        	$("#loadingThingy").hide();
            console.log("error loading names");
            console.log(res);
            $("#errorThingy").show();
        });
        console.log("fetching rating names");
        var req = {
                method: 'GET',
                url: hostUrl + "/ratings"
            }
            $http(req).success(function(res){
                console.log("successfully fetched names");
                $scope.ratings = res;
            }).error(function(res){
                $("#loadingThingy").hide();
                console.log("error loading ratings");
                console.log(res);
                $("#errorThingy").show();
            });
    }
    
    function loadMap() {
    	var mapProp = {
            center: new google.maps.LatLng(defaultLat, defaultLong),
            zoom: 11,
            mapTypeId:google.maps.MapTypeId.ROADMAP
        };

        map = new google.maps.Map(document.getElementById("map"),mapProp);
        
        var kmlUrl = serverUrl + "/kml?neighborhood=none";
        if ($scope.neighborhood != null) {
        	 kmlUrl = getKmlUrl($scope.neighborhood.name);
        }
        
		var kmlOptions = {
  			preserveViewport: true,
  			map: map,
  			clickable: false
		};
		var kmlLayer = new google.maps.KmlLayer(kmlUrl, kmlOptions);
		
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
            delete selectedRatings[i];
        }
        console.log(selectedRatings);
    }
    
    $scope.isSelected = function(rating) {
        return selectedRatings.indexOf(rating) > -1;
    }
    
    $scope.scrollToBottom = function() {
    	window.scroll(0,document.body.scrollHeight)
    };
    
    $scope.goNeighborhood = function(name) {
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
    
    function getKmlUrl(name) {
        return serverUrl + "/kml?neighborhood=" + encodeURIComponent(name);
    }
    
    function getSmallUrl(name) {
        return serverUrl + "/smallKml?neighborhood=" + encodeURIComponent(name);
    }
    
    $interval(function() {
        return;
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
                    // remove layer and marker
                    // map.marker.setMap(null);
                    map.smallLayer.setMap(null);
                }
                else if (map.marker.labelContent != res.name) {
                    // update layer and marker
                    /*map.marker.labelContent = res.name;
                    map.marker.position = new google.maps.LatLng(res.longitude,res.latitude);
                    map.marker.setMap(map); */
                    map.smallLayer.setUrl(getSmallUrl(res.name));
                    map.smallLayer.setMap(map);
                }
            }).error(function(res) {
                // map.marker.setMap(null);
                map.smallLayer.setMap(null);
            });
            map.mouseOverEvent = null;
        }
    }, 20);

    load();
	
	$(".container-fluid").fadeIn();
}]);
