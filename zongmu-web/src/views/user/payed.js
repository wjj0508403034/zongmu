'use strict';

zongmu.controller("payedController", ["$scope", "breadCrumb", "payService", "dialog",
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
        href: "pay.detail.html?payId=" + payId
      }, {
        text: "支付",
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

    $scope.onSaveButtonClick = function() {
      if(!$scope.transcationNo) {
        dialog.showError("支付交易号不能为空。");
        return;
      }

      payService.pay(payId, {
          transcationNo: $scope.transcationNo,
          memo: $scope.memo
        })
        .then(function() {
          dialog.showInfo("保存成功！")
            .then(function() {
              window.location.href = "pay.detail.html?payId=" + payId;
            });
        });
    };

    $scope.onCancelButtonClick = function() {
      window.history.back();
    };

  }
]);