'use strict';

zongmu.controller("loginController", ["$scope", "userService", "dialog", "serviceUrl", "application",
  function($scope, userService, dialog, serviceUrl, applicationProvider) {
    initView() && initData();

    function initView() {
      $scope.setTitle("用户登录");
      return true;
    }

    function initData() {

    }

    $scope.onRegisterButtonClick = function() {
      window.location.href = "register.html";
    };

    $scope.onForgetPasswordButtonClick = function() {
      window.location.href = "forget.password.html";
    };

    $(document).on("keydown", function(event) {
      event.stopPropagation();
      if($(".ngdialog.huoyun-dialog").length === 0) {
        if(event.keyCode === 13) {
          $scope.onSubmit();
        }
      }
    });

    $scope.onSubmit = function() {
      if(!$scope.email) {
        dialog.showError("邮箱不能为空。")
        return;
      }

      if(!$scope.password) {
        dialog.showError("密码不能为空。")
        return;
      }

      var param = {};
      param.email = $scope.email;
      param.password = $scope.password;

      $scope.showLoading();
      userService.login(param)
        .then(function(user) {
          $scope.hideLoading();
          applicationProvider.setLogin(true);
          applicationProvider.setUserName(user.email, user.businessRole);
          window.location.href = serviceUrl + "page/home/index.html"
        });
    };

  }
]);