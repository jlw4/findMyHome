'use strict';

var app = angular.module('App',[]);
  
app.controller('MainController', ['$scope', '$location', '$timeout', '$http', function($scope, $location, $timeout, $http) {
    
    var map;
    var hostUrl = "http://54.149.5.12:8080";
    if ($location.$$protocol == "file") {
        hostUrl = "http://localhost:8080";
    }
    
    $scope.getLongLat = function() {
        var req = {
            method: 'GET',
            url: hostUrl + "/coordinate",
            params: {address: $scope.address},
            headers: {
              'Content-Type': undefined
            }
        }
        console.log(req);
        $http(req).success(function(res){
            console.log("success");
            $scope.long = res.longitude;
            $scope.lat = res.latitude;
            var mapProp = {
                center: new google.maps.LatLng($scope.long,$scope.lat),
                zoom: 14,
                mapTypeId:google.maps.MapTypeId.ROADMAP
            };
            console.log(mapProp.center)
            map = new google.maps.Map(document.getElementById("googleMap"),mapProp);
            // google.maps.event.addDomListener(window, 'load', initialize);
        }).error(function(res){
            console.log("error");
            console.log(res);
        });
    };
    
	$scope.loaded = true;
}]);
