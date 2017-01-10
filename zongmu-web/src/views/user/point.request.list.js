'use strict';

zongmu.controller("pointApproveListController", ["$scope", "breadCrumb", "payService", "dialog",
  function($scope, breadCrumbProvider, payService, dialog) {
    var pageIndex = 0;
    var tabIndex = 0;
    initView() && initData();

    function initView() {
      $scope.setTitle("提现申请记录");
      breadCrumbProvider.setHistories([{
        text: "提现申请记录",
        href: "#"
      }]);

      $scope.tabs = [{
        name: "all",
        text: "全部",
        active: true
      }, {
        name: "PENDING",
        text: "待支付"
      }, {
        name: "PAYED",
        text: "已支付"
      }];

      $scope.columns = [{
        name: "userName",
        text: "申请人"
      }, {
        name: "point",
        text: "提现金额"
      }, {
        name: "createTime",
        text: "申请时间"
      }, {
        name: "status",
        text: "状态"
      }, {
        name: "op",
        text: "操作"
      }];
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");
      payService.getList(pageIndex, tabIndex)
        .then(function(data) {
          $scope.hideLoading();
          $scope.paylist = data.content;
          $scope.pageData = {
            totalPage: data.totalPages,
            pageIndex: data.number
          };
        });
    }

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.$on("onTabChanged", function(tabScope, item, index) {
      pageIndex = 0;
      tabIndex = index;
      initData();
    });

    $scope.onPayButtonClick = function(rowData) {
      window.location.href = "payed.html?payId=" + rowData.id;
    };

    $scope.onViewButtonClick = function(rowData) {
      window.location.href = "pay.detail.html?payId=" + rowData.id;
    };

  }
]);