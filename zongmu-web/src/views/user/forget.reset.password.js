'use strict';

zongmu.controller("forgetResetPasswordController", ["$scope", "userService", "dialog", "passwordUtils",
  function($scope, userService, dialog, passwordUtils) {
    var email = $.url().param("mail");
    var code = $.url().param("code");
    initView() && initData();

    function initView() {
      $scope.setTitle("重置密码");
      return true;
    }

    function initData() {

    }

    $scope.onSubmit = function() {
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

      if(!passwordUtils.checkPassword($scope.repeatPassword)) {
        dialog.showError(passwordUtils.getMsg());
        return;
      }

      var param = {};
      param.email = (email || "").replace("____", "@");
      param.code = code;
      param.password = $scope.password;
      param.repeatPassword = $scope.repeatPassword;
      $scope.showLoading();
      userService.forgetResetPassword(param)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("重置密码成功！")
            .then(function() {
              window.location.href = "login.html";
            });

        });
    };

  }
]);