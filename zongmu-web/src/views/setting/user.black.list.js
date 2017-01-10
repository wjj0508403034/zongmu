'use strict';

zongmu.controller("blackUserListController", ["$scope", "userService", "dialog", "breadCrumb",
  function($scope, userService, dialog, breadCrumbProvider) {
    var pageIndex = 0;
    initView();
    initData();

    function initView() {
      $scope.title = "黑名单列表";
      $scope.columns = [{
        name: "email",
        text: "账户"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "黑名单列表",
        href: "#"
      }]);
    }

    function initData() {
      userService.getBlackUserList(pageIndex)
        .then(function(result) {
          $scope.users = result.content;
          $scope.pageData = {
            totalPage: result.totalPages,
            pageIndex: result.number
          };
        });
    }

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.onViewButtonClick = function(user) {
      window.location.href = "users.detail.html?userId=" + user.id;
    };

    $scope.onRemoveBlackButtonClick = function(user) {
      userService.removeBlackList(user.id)
        .then(function() {
          dialog.showInfo("移除黑名单成功！")
            .then(function() {
              initData();
            });
        });
    };

  }
]);