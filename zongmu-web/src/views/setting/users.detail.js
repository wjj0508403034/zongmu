'use strict';

zongmu.controller("userDetailController", ["$scope", "userService", "dialog", "breadCrumb",
  function($scope, userService, dialog, breadCrumbProvider) {
    var userId = $.url().param("userId");
    initView();
    initData();

    function initView() {
      $scope.title = "用户详细信息";
      breadCrumbProvider.setHistories([{
        text: "用户列表",
        href: "users.html"
      }, {
        text: "详细信息",
        href: "#"
      }]);
    }

    function initData() {
      userService.getUser(userId)
        .then(function(data) {
          $scope.user = data;
        });
    }

    $scope.onRoleButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'users.role.html',
        controller: "userRoleDialogController",
        params: {
          user: $scope.user
        },
        onConfirm: function() {
          initData();
        }
      });
    }

  }
]);