'use strict';

angular.module('huoyun-ui').directive("huoyunAppendClass", function() {
  return {
    restrict: "A",
    controller: "huoyunAppendClassController",
    link: function($scope, elem, attrs) {
      if (attrs.huoyunAppendClass) {
        elem.addClass(attrs.huoyunAppendClass);
      }
    }
  };
});

angular.module('huoyun-ui').controller("huoyunAppendClassController", ["$scope", function($scope) {

}]);