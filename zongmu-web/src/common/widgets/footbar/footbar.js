'use strict';

angular.module('huoyun-ui').directive("widgetsFootBar", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/footbar/footbar.html",
    replace: true,
    controller: "footBarController",
    link: function($scope, elem, attrs) {

    }
  };
});

angular.module('huoyun-ui').controller("footBarController", ["$scope", "footbar", function($scope, footbarProvider) {
  $scope.links = footbarProvider.getUsefulLinks();
  $scope.copyright = footbarProvider.getCopyRight();
  $scope.companyName = footbarProvider.getCompanyName();
  $scope.record = footbarProvider.getRecordInfo();
}]);

angular.module('huoyun-ui').provider("footbar", function() {
  var usefulLinks = [];
  var copyright = null;
  var companyName = null;
  var record = null; // 备案信息

  this.setUsefulLinks = function(links) {
    if (angular.isArray(links)) {
      usefulLinks = links;
    }
  };

  this.getUsefulLinks = function() {
    return usefulLinks;
  };

  this.setCopyRight = function(value) {
    copyright = value;
  };

  this.getCopyRight = function() {
    return copyright;
  };

  this.setRecordInfo = function(value) {
    record = value;
  };

  this.getRecordInfo = function() {
    return record;
  };

  this.setCompanyName = function(value) {
    companyName = value;
  };

  this.getCompanyName = function() {
    return companyName;
  };

  this.$get = function() {
    return this;
  };
});