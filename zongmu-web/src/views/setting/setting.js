'use strict';

zongmu.controller("settingController", ["$scope", function($scope) {
  $scope.setTitle("设置");
  
  $scope.sidebar = [{
    name: "setting-general",
    text: "基本设置",
    icon: "tasks",
    items: [{
      name: "asset-tag-settings",
      text: "视频属性设置",
      href: "asset.view.tag.html"
    }, {
      name: "reject-reason-settings",
      text: "原因设置",
      href: "reason.html"
    }, {
      name: "algorithm-settings",
      text: "算法设置",
      href: "algorithm.html"
    }, {
      name: "train-settings",
      text: "训练设置",
      href: "train.html"
    }]
  }, {
    name: "user-profile",
    text: "权限设置",
    icon: "user",
    items: [{
      name: "user-list",
      text: "用户列表",
      href: "users.html"
    }, {
      name: "black-user-list",
      text: "黑名单",
      href: "user.black.list.html"
    }]
  }];

  initData();

  function initData() {
    var role = Cookies.get("role");
    if(role === "REVIEW") {
      hideGroups(["user-profile"]);
    }
  }

  $scope.hideSideItems = function(names) {
    hideGroupItems(names);
  }

  $scope.hideSideGroups = function(groups) {
    hideGroups(groups);
  };

  function hideGroups(groups) {
    $scope.sidebar.forEach(function(group) {
      if(groups.indexOf(group.name) !== -1) {
        group.visibility = false;
      } else {
        delete group.visibility;
      }
    });
  }

  function hideGroupItems(names) {
    $scope.sidebar.forEach(function(group) {
      group.items.forEach(function(it) {
        if(names.indexOf(it.name) !== -1) {
          it.visibility = false;
        } else {
          delete it.visibility;
        }
      });
    });
  }
}]);