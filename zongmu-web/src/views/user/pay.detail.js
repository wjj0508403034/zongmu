'use strict';

zongmu.controller("payDetailController", ["$scope", "breadCrumb", "payService", "dialog",
  function($scope, breadCrumbProvider, payService, dialog) {
    var payId = $.url().param("payId");
    initView() && initData();

    function initView() {
      $scope.setTitle("提现申请记录");
      breadCrumbProvider.setHistories([{
        text: "提现申请记录",
        href: "point.request.list.html"
      }, {
        text: "详细信息",
        href: "#"
      }]);

      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");
      payService.getPay(payId)
        .then(function(data) {
          $scope.hideLoading();
          $scope.pay = data;
        });
    }

    $scope.onPayButtonClick = function() {
      window.location.href = "payed.html?payId=" + payId;
    };

  }
]);