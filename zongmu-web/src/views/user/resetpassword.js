'use strict';

zongmu.controller("resetPasswordController", ["$scope", "breadCrumb", "userService", "dialog", "enumService", "passwordUtils",
  function($scope, breadCrumbProvider, userService, dialog, enumService, passwordUtils) {
    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "安全中心",
        href: "#"
      }]);
      return true;
    }

    function initData() {
      $scope.oldPassword = null;
      $scope.password = null;
      $scope.repeatPassword = null;
    }

    $scope.onSaveButtonClicked = function() {
      if(!$scope.oldPassword) {
        dialog.showError("旧密码不能为空！");
        return;
      }

      if(!$scope.password) {
        dialog.showError("新密码不能为空！");
        return;
      }

      if(!$scope.repeatPassword) {
        dialog.showError("重复新密码不能为空！");
        return;
      }

      if($scope.password !== $scope.repeatPassword) {
        dialog.showError("两次密码不一致！");
        return;
      }

      if(!passwordUtils.checkPassword($scope.repeatPassword)) {
        dialog.showError(passwordUtils.getMsg());
        return;
      }

      $scope.showLoading();
      $scope.setLoadingText("正在重置，请稍等...");
      userService.resetPassword({
          oldPassword: $scope.oldPassword,
          password: $scope.password,
          repeatPassword: $scope.repeatPassword
        })
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("密码重置成功！")
            .then(function() {
              initData();
            })
        });
    };

    $scope.onCancelClicked = function() {
      initData();
    }
  }
]);