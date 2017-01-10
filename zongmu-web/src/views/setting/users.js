'use strict';

zongmu.controller("userListController", ["$scope", "userService", "dialog", "breadCrumb",
  function($scope, userService, dialog, breadCrumbProvider) {
    var pageIndex = 0;
    var tabIndex = 0;
    initView();
    initData();

    function initView() {
      $scope.title = "用户列表";
      $scope.columns = [{
        name: "email",
        text: "账户"
      }, {
        name: "businessRole",
        text: "角色"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "用户列表",
        href: "#"
      }]);

      $scope.tabs = [{
        name: "all",
        text: "全部",
        active: true
      }, {
        name: "ADMIN",
        text: "管理员"
      }, {
        name: "FINANCE",
        text: "财务人员"
      }, {
        name: "REVIEW",
        text: "审核人员"
      }, {
        name: "UPLOAD",
        text: "路测人员"
      }, {
        name: "NORMAL",
        text: "普通用户"
      }, {
        name: "SUPER",
        text: "高级用户"
      }];
    }

    function initData() {
      userService.getUserList(pageIndex, tabIndex)
        .then(function(result) {
          $scope.users = result.content;
          $scope.pageData = {
            totalPage: result.totalPages,
            pageIndex: result.number
          };
        });
    }

    $scope.canShow = function(user) {
      if(user.id === 1){
        return false;
      }
      
      if(Cookies.get("role") === 'ADMIN' && Cookies.get("username") === user.email){
        return false;
      }
      
      return true;
    };

    $scope.$on("onTabChanged", function(tabScope, item, index) {
      pageIndex = 0;
      tabIndex = index;
      initData();
    });

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.onViewButtonClick = function(user) {
      window.location.href = "users.detail.html?userId=" + user.id;
    };

    $scope.onPermissionButtonClick = function(user) {
      dialog.showCustom({
        templateUrl: 'users.role.html',
        controller: "userRoleDialogController",
        params: {
          user: user
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onAddBlackButtonClick = function(user) {
      userService.addBlackList(user.id)
        .then(function() {
          dialog.showInfo("拉入黑名单成功！")
            .then(function() {
              initData();
            });
        });
    };

  }
]);