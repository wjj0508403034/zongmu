'use strict';

zongmu.controller("registerController", ["$scope", "userService", "dialog", "passwordUtils",
  function($scope, userService, dialog, passwordUtils) {
    initView() && initData();

    function initView() {
      $scope.setTitle("用户注册");
      return true;
    }

    function initData() {

    }

    $scope.onLoginButtonClick = function() {
      window.location.href = "login.html";
    };

    $scope.onSubmit = function() {
      if(!$scope.email) {
        dialog.showError("邮箱地址不能为空。")
        return;
      }

      if(!$scope.password) {
        dialog.showError("密码不能为空。")
        return;
      }

      if(!$scope.repeatPassword) {
        dialog.showError("重复密码不能为空。")
        return;
      }

      if($scope.password !== $scope.repeatPassword) {
        dialog.showError("两次密码不一致。")
        return;
      }

      if($scope.password.length < 6) {
        dialog.showError("密码必须大于6位数。")
        return;
      }

      if(!passwordUtils.checkPassword($scope.repeatPassword)) {
        dialog.showError(passwordUtils.getMsg());
        return;
      }

      var param = {};
      param.email = $scope.email;
      param.password = $scope.password;
      param.repeatPassword = $scope.repeatPassword;
      $scope.showLoading();
      userService.register(param)
        .then(function() {
          $scope.hideLoading();
          window.location.href = "register.info.html?mail=" + $scope.email.replace("@", "____");
        });
    };

  }
]);