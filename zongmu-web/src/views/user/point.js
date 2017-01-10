'use strict';

zongmu.controller("userPointController", ["$scope", "breadCrumb", "userPointService", "dialog", "enumService",
  function($scope, breadCrumbProvider, userPointService, dialog, enumService) {
    var pageIndex = 0;
    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "会员积分",
        href: "#"
      }]);

      $scope.columns = [{
        name: "createTime",
        text: "时间"
      }, {
        name: "point",
        text: "收入/支出"
      }, {
        name: "memo",
        text: "详细说明"
      }];
      return true;
    }

    function initData() {
      $scope.sexes = enumService.getSexes();
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");

      userPointService.getMyPoints(pageIndex)
        .then(function(data) {
          $scope.hideLoading();
          $scope.user = data.user;
          $scope.myPoint = data;
          $scope.pageData = {
            totalPage: data.points.totalPages,
            pageIndex: data.points.number
          };
        });
    }

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.onChangedMoneyButtonClicked = function() {
      if(!$scope.user.alipayAccount) {
        dialog.showInfo("请先设置支付宝账号，然后提现！")
          .then(function() {
            window.location.href = "profile.html";
          });
        return;
      }

      if($scope.myPoint.point < 100) {
        dialog.showError("积分余额不足100，不能提现！");
        return;
      }

      window.location.href = "change.money.create.html";
    };

  }
]);