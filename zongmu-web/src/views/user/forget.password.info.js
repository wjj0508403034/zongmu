'use strict';

zongmu.controller("forgetPasswordInfoController", ["$scope", "userService", "dialog",
  function($scope, userService, dialog) {
    var email = $.url().param("mail");
    initView() && initData();

    function initView() {
      $scope.setTitle("重置密码");
      return true;
    }

    function initData() {
      if(email) {
        $scope.email = email.replace("____", "@");
      }
    }
  }
]);