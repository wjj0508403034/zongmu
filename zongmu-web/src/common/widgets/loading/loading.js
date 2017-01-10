'use strict';

angular.module('huoyun-ui').directive("widgetsLoading", ["$log", function($log) {
  return {
    restrict: "A",
    templateUrl: "widgets/loading/loading.html",
    replace: true,
    scope: {
      text: "@",
      show: "="
    },
    controller: "loadingController",
    link: function($scope, elem, attrs) {
      $scope.$watch("show",function(){
        if($scope.show !== true){
          elem.css("display","none");
        }else{
          elem.css("display","block");
        }
      });
    }
  }
}]);

angular.module('huoyun-ui').controller("loadingController", ["$scope", "$log", function($scope, $log) {
 
}]);