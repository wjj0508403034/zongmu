'use strict';

angular.module('huoyun-ui').directive("widgetsNav", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/nav/nav.html",
    replace: true,
    scope: {
      "current": "@"
    },
    controller: "navController",
    link: function($scope, elem, attrs) {
      $scope.$watch("contentWidth", function() {
        elem.find("ul").css("width", $scope.contentWidth);
      });
    }
  };
});

angular.module('huoyun-ui').controller("navController", ["$scope", "nav", "page", function($scope, navProvider, pageProvider) {
  $scope.items = navProvider.getItems() || [];
  angular.forEach($scope.items, function(item, index) {
    item.className = item.name === $scope.current ? "active" : "";
  });

  $scope.contentWidth = pageProvider.getPageWidth();
}]);

angular.module('huoyun-ui').provider("nav", function() {
  this.items = [];

  this.setItems = function(items) {
    if (angular.isArray(items)) {
      this.items = items;
    }
  };

  this.getItems = function() {
    return this.items;
  };

  this.$get = function() {
    return this;
  };
});