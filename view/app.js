'use strict';

var app = angular.module('App',[]);
  
app.controller('MainController', ['$scope', '$location', '$timeout', '$http', '$interval', function($scope, $location, $timeout, $http, $interval) {
    
	$("#loadingThingy").fadeIn(2500);
	
	var defaultLat = 47.62;
	var defaultLong = -122.325;
	
    var map;
    var serverUrl = "http://54.149.5.12:8080";
    var defaultKmlUrl = serverUrl + "/sortedKml";
    var defaultSmallUrl = serverUrl + "/smallKml?neighborhood=none";
    var defaultCenter = new google.maps.LatLng(defaultLat, defaultLong);
    var defaultZoom = 11;
    var hostUrl = serverUrl;
    if ($location.$$protocol == "file" || $location.host() == "localhost") {
        hostUrl = "http://localhost:8080";
    }
    var animationDuration = 1500.0; // progress bar animation
    var durationInS = ( animationDuration / 1000 ) + "s";
    $(".progress-bar").css("-webkit-transition", "width " + durationInS + " ease-in-out");
    $(".progress-bar").css("transition", "width " + durationInS + " ease-in-out");
    
    var selectedRatings = [];
	$scope.mouseOverName = "none";
    
    function load() {
        if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
            console.log("on mobile");
            $scope.mobile = true;
            $("#mouseOverBox").hide();
        } else {
            console.log("not mobile");
            $scope.mobile = false;
            
        }
        var req = {
            method: 'GET',
            url: hostUrl + "/names"
        }
        $http(req).success(function(res) {
            console.log("loaded neighborhoods");
            // put names in 3 lists for columns on browse page
            $scope.neighborhoodNames = res.slice();
            $scope.names = [];
            var size = res.length / 3;
            for (var i = 0; i < 3; i++) {
            	$scope.names[i] = [];
            	while (res.length > 0 && $scope.names[i].length < size) {
            		$scope.names[i].push(res.shift());
            	}
            }
            loadMap();
            $scope.loaded = true; // this hides spinner and displays body
        }).error(function(res) {
            console.log(res);
            loadingFailed();
        });
        var req = {
            method: 'GET',
            url: hostUrl + "/ratings"
        }
        $http(req).success(function(res) {
            console.log("successfully fetched ratings");
            $scope.ratings = res;
        }).error(function(res){
            console.log(res);
            loadingFailed();
        });

        var req = {
            method: 'GET',
            url: hostUrl + "/colors"
        }
        $http(req).success(function(res) {
            console.log("successfully fetched colors");
            $scope.colors = res;
        }).error(function(res){
            console.log("error loading colors");
            console.log(res);
            loadingFailed();
        });

        var req = {
            method: 'GET',
            url: hostUrl + "/container"
        }
        $http(req).success(function(res) {
            console.log("successfully fetched container");
            $scope.neighborhoods = res;
        }).error(function(res){
            console.log("error loading container");
            console.log(res);
            loadingFailed();
        });
    }
    
    $scope.getColor = function($index) {
    	console.log($index);
    	var color = $scope.colors[$index];
    	return { 'background-color':color,'color':color};
    }
    
    $scope.getColorText = function() {
        if ($scope.mobile)
            return "m";
        else
            return "mmm";
    }
    
    function loadingFailed() {
        $("#loadingThingy").hide();
        $(".container-fluid").hide();
        $("#errorThingy").show();
    }
    
    function loadMap() {
    	var mapProp = {
            center: defaultCenter,
            zoom: defaultZoom,
            mapTypeId:google.maps.MapTypeId.ROADMAP
        };
    	if ($scope.mobile) {
    	    mapProp.draggable = false;
    	}
        map = new google.maps.Map(document.getElementById("map"),mapProp);
		var KmlOptions = {
  			preserveViewport: true,
  			map: map,
  			clickable: false
		};
		map.KmlLayer = new google.maps.KmlLayer(defaultKmlUrl, KmlOptions);
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
			$scope.mouseOverName = "none";
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
        updateKml();
        moveWindow();
        if (selectedRatings.length == 0) {
            $("#helper").text("");
            $("#helper").fadeOut();
        }
        else {
            var time = new Date().getTime();
            var text = "Displaying: " + selectedRatings[0];
            for (var i = 1; i < selectedRatings.length; i++) {
                if (i == selectedRatings.length - 1)
                    text += " AND ";
                else
                    text += ", ";
                text += selectedRatings[i];
            }
            $("#helper").text(text);
            $("#helper").fadeIn();
            destroyHelper(time);
        }
    }
    
    function destroyHelper(time) {
        $scope.lastHelperChange = time;
        $timeout(function() {
            if ($scope.lastHelperChange == time)
                $("#helper").fadeOut();
        }, 5000);
    }
    
    $scope.isSelected = function(rating) {
        return selectedRatings.indexOf(rating) > -1;
    }
    
    $scope.scrollToBottom = function() {
    	window.scroll(0,document.body.scrollHeight)
    };
    
    $scope.goNeighborhood = function(name) {
        moveWindow();
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
            updateKml();
            moveWindow();
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
    	$("#citationBox").fadeIn();
    }
    
    $scope.reset = function() {
    	$("#neighborhoodBox").hide();
        $("#citationBox").hide();
    	selectedRatings = [];
    	$scope.neighborhood = null;
    	map.panTo(defaultCenter);
    	map.setZoom(defaultZoom);
    	updateKml();
    	moveWindow();
    	$("#helper").fadeOut();
    }
    
    function moveWindow() {
        window.scrollTo(0,100);
    }
    
    function getKmlUrl() {
    	var url = serverUrl + "/sortedKml";
    	for (var i = 0; i < selectedRatings.length; i++) {
    		if (i == 0)
    			url += "?";
    		else
    			url += "&";
    		url += "ratings=" + selectedRatings[i];
    	}
    	if ($scope.neighborhood != null) {
    	    if (selectedRatings.length == 0)
    	        url += "?";
    	    else
    	        url += "&";
    	    url += "neighborhood=" + encodeURIComponent($scope.neighborhood.name);
    	}
    	return url;
    }
    
    function getSmallUrl() {
        return serverUrl + "/smallKml?neighborhood=" + encodeURIComponent($scope.mouseOverName);
    }
    
    function updateKml() {
        map.KmlLayer.setUrl(getKmlUrl());
    }
    
    function getNeighborhood(lat, long) {
        for (var name in $scope.neighborhoods) {
            if (isInsideBoundary(lat,long,$scope.neighborhoods[name].boundary))
                return name;
        }
        return "none";
    }
    
    function isInsideBoundary(lat,long,boundary) {
        if (boundary.length < 3) {
            return false;
        }
        
        var i, j, nvert = boundary.length;
        var px = long;
        var py = lat;
        var c = false;

        for (i = 0, j = nvert - 1; i < nvert; j = i++) {
            var iy, ix, jy, jx;
            iy = boundary[i].latitude;
            ix = boundary[i].longitude;
            jy = boundary[j].latitude;
            jx = boundary[j].longitude;

            if (((iy >= py) != (jy >= py)) && (px <= (jx - ix) * (py - iy) / (jy - iy) + ix)) {
                c = !c;
            }
        }
        return c;
    }
    
    function hideSmallLayer() {
        if (map.smallLayer != null) {
            map.smallLayer.setMap(null);
            map.smallLayer = null;
        }
    }
    
    $scope.menuMouseOut = function() {
        $scope.mouseOverName = "none";
        hideSmallLayer();
    }
    
    $scope.menuMouseOver = function(name) {
        if (name != $scope.mouseOverName) {
            $scope.mouseOverName = name;
            if (name == "none") {
                hideSmallLayer();
            } else {
                var KmlOptions = {
                    preserveViewport: true,
                    map: map,
                    clickable: false
                };
                if (map.smallLayer == null)
                    map.smallLayer = new google.maps.KmlLayer(getSmallUrl(), KmlOptions);
                else
                    map.smallLayer.setUrl(getSmallUrl());
            }
        }
    }
    
    // handling mousemove events in an interval
    $interval(function() {
        if ($scope.loaded) {
            if (map.mouseOverEvent != null) {
                var event = map.mouseOverEvent;
                var lat = event.latLng.k;
                var long = event.latLng.D;
                var name = getNeighborhood(lat,long);
                $scope.menuMouseOver(name);
                map.mouseOverEvent = null;
            }
        }
    }, 20);

    load();
    destroyHelper(new Date().getTime());
	
	$(".container-fluid").fadeIn();
}]);
