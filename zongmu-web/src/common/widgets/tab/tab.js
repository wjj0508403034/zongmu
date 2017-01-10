'use strict';

angular.module('huoyun-ui').directive("widgetsTab", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/tab/tab.html",
    replace: true,
    scope: {
      "items": "="
    },
    controller: "tabController",
    link: function($scope, elem, attrs) {

      $scope.$watchCollection("items", function() {
        if($scope.items) {
          elem.find("li").css("width", (100 / $scope.items.length) + "%");
        }
      });
    }
  };
});

angular.module('huoyun-ui').controller("tabController", ["$scope", function($scope) {

  $scope.onTabItemClicked = function(item, index) {
    ($scope.items || []).forEach(function(it, itIndex) {
      it.active = index === itIndex;
    });

    $scope.$emit("onTabChanged", item, index);
  };

}]);