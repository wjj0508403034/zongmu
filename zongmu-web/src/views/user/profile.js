'use strict';

zongmu.controller("profileController", ["$scope", "breadCrumb", "userService", "dialog", "enumService",
  function($scope, breadCrumbProvider, userService, dialog, enumService) {
    initView() && initData();

    function initView() {
      $scope.sexes = enumService.getSexes();
      breadCrumbProvider.setHistories([{
        text: "个人信息",
        href: "#"
      }]);
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");
      userService.getProfile()
        .then(function(data) {
          $scope.hideLoading();
          $scope.user = data;
        });
    }

    $scope.onCancelClicked = function() {
      initData();
    };

    $scope.onSaveButtonClicked = function() {
      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍等...");
      userService.updateProfile($scope.user)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("保存成功！")
            .then(function() {
              initData();
            });

        });
    };

  }
]);