'use strict';

angular.module('huoyun-ui').directive("widgetsSideBar", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/sidebar/sidebar.html",
    replace: true,
    scope: {
      groups: "=",
      currentGroup: "@",
      currentItem: "@"
    },
    controller: "sideBarController",
    link: function($scope, elem, attrs) {}
  };
});

angular.module('huoyun-ui').controller("sideBarController", ["$scope", function($scope) {

}]);