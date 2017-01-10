'use strict';

zongmu.controller("forgetPasswordController", ["$scope", "userService", "dialog",
  function($scope, userService, dialog) {
    initView() && initData();

    function initView() {
      $scope.setTitle("忘记密码");
      return true;
    }

    function initData() {

    }

    $scope.onSubmit = function() {
      if(!$scope.email) {
        dialog.showError("邮箱地址不能为空。")
        return;
      }

      var param = {};
      param.email = $scope.email;
      param.password = $scope.password;
      param.repeatPassword = $scope.repeatPassword;

      $scope.showLoading();
      userService.forgetPassword(param)
        .then(function() {
          $scope.hideLoading();
          window.location.href = "forget.password.info.html?mail=" + $scope.email.replace("@", "____");
        });
    };

  }
]);