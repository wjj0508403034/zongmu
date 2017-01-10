'use strict';

angular.module('huoyun-ui').directive("widgetsLinkButton", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/linkButton/linkButton.html",
    replace: true,
    scope: {
      text: "@"
    },
    controller: "linkButtonController",
    link: function($scope, elem, attrs) {

    }
  };
});

angular.module('huoyun-ui').controller("linkButtonController", ["$scope", function($scope) {

}]);