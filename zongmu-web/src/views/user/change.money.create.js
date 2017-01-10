'use strict';

zongmu.controller("changeMoneyCreateController", ["$scope", "breadCrumb", "userPointService", "dialog", "payService",
  function($scope, breadCrumbProvider, userPointService, dialog, payService) {
    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "会员积分",
        href: "point.html"
      }, {
        text: "积分提现",
        href: "#"
      }]);

      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");
      userPointService.getAvailablePoint()
        .then(function(data) {
          $scope.availablePoint = data;
          $scope.hideLoading();
        });
    }

    $scope.onSubmitClicked = function() {
      if($scope.money === null || $scope.money === undefined || $scope.money <= 0) {
        dialog.showError("提现积分不能为空,并且不能为负数。");
        return;
      }

      if($scope.money > $scope.availablePoint) {
        dialog.showError("提现积分不能大于可用积分。");
        return;
      }

      if($scope.money % 100 !== 0) {
        dialog.showError("提现积分必须是100的整数倍。");
        return;
      }

      payService.requestPay({
        point: $scope.money
      }).then(function() {
        dialog.showInfo("提交提现请求成功")
          .then(function() {
            window.location.href = "point.html";
          });
      });
    };

    $scope.onCancelClicked = function() {
      window.location.href = "point.html";
    }

  }
]);