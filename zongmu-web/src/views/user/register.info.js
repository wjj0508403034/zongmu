'use strict';

zongmu.controller("registerInfoController", ["$scope", "userService", "dialog",
  function($scope, userService, dialog) {
    var email = $.url().param("mail");
    initView() && initData();

    function initView() {
      $scope.setTitle("用户注册");
      return true;
    }

    function initData() {
      if(email) {
        $scope.email = email.replace("____", "@");
      }
    }
  }
]);