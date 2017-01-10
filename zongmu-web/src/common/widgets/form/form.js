'use strict';

/*
 * fields:所有字段的meta，例如
 * fields: [{
 *   name: "firstName",
 *   label: "姓",
 *   type: "text",
 *   required: true,
 *   
 * }]
 */
angular.module('huoyun-ui').directive("widgetsForm", ["huoyunUtil", function(huoyunUtil) {
  return {
    restrict: "A",
    templateUrl: "widgets/form/form.html",
    replace: true,
    scope: {
      "title": "@",
      "fields": "=",
      "data": "="
    },
    controller: "formController",
    link: function($scope, elem, attrs) {

    }
  };
}]);

angular.module('huoyun-ui').controller("formController", ["$scope", function($scope) {

}]);