'use strict';

angular.module('huoyun-ui').directive("widgetsPage", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/page/page.html",
    replace: true,
    scope: true,
    transclude: true,
    controller: "pageController",
    link: function($scope, elem, attrs) {

      $scope.$watch("width", function() {
        elem.css("width", $scope.width);
      });
    }
  };
});

angular.module('huoyun-ui').controller("pageController", ["$scope", "page", function($scope, pageProvider) {
  $scope.width = pageProvider.getPageWidth();
}]);

angular.module('huoyun-ui').provider("page", function() {

  var pageWidth = "960px";

  this.setPageWidth = function(width) {
    pageWidth = width;
  };

  this.getPageWidth = function() {
    return pageWidth;
  }

  this.$get = function() {
    return this;
  };
});