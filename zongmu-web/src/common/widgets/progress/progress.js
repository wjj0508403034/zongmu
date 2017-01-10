'use strict';

angular.module('huoyun-ui').directive("widgetsProgress", ["huoyunUtil", "$sce", "$log", function(huoyunUtil, $sce, $log) {
  return {
    restrict: "A",
    templateUrl: "widgets/progress/progress.html",
    replace: true,
    scope: {
      percent: "@"
    },
    controller: "progressController",
    link: function($scope, elem, attrs) {
      $scope.$watch("percent", function() {
        var progressBar = elem.find(".huoyun-progress-bar");
        var positionBtn = elem.find(".position-btn");
        if (progressBar && $scope.percent) {
          progressBar.css("width", $scope.percent + "%");
          positionBtn.css("left", $scope.percent + "%");
        }
      });
      
      elem.on("mousedown",function(event){
        var precent = event.offsetX / elem.width();
        $scope.$emit("onPrecentChanged",precent);
      });
    }
  };
}]);

angular.module('huoyun-ui').controller("progressController", ["$scope", function($scope) {

}]);