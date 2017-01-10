'use strict';

zongmu.controller("helpController", ["$scope", function($scope) {
  initView();

  function initView() {
    $scope.setTitle("帮助中心");
  }
}]);