'use strict';

zongmu.controller("baseTaskController", ["$scope", "breadCrumb", "serviceUrl", "algorithmService", "appEnv",

  function($scope, breadCrumbProvider, serviceUrl, algorithmService, appEnv) {

    $scope.setTitle("我的任务");

    $scope.sidebar = [{
      name: "task-management",
      text: "任务管理",
      icon: "tasks",
      items: [{
        name: "task-record",
        text: "任务记录",
        href: serviceUrl + "page/task/index.html"
      }, {
        name: "upload-task-record",
        text: "上传记录",
        href: serviceUrl + "page/task/assets.html"
      }, {
        name: "review-task-record",
        text: "审核记录",
        href: serviceUrl + "page/task/reviews.html"
      }]
    }, {
      name: "report",
      text: "信息统计",
      icon: "dashboard",
      items: []
    }, {
      name: "user-profile",
      text: "个人中心",
      icon: "user",
      items: [{
        name: "user-profile",
        text: "个人信息",
        href: serviceUrl + "page/user/profile.html"
      }, {
        name: "user-point",
        text: "会员积分",
        href: serviceUrl + "page/user/point.html"
      }, {
        name: "user-secret",
        text: "安全中心",
        href: serviceUrl + "page/user/resetpassword.html"
      }, {
        name: "point-approve-list",
        text: "提现申请记录",
        href: serviceUrl + "page/user/point.request.list.html"
      }]
    }];

    initView();

    function initData() {
      $scope.algorithmMap = {};
      algorithmService.getAlgorithms()
        .then(function(data) {
          data.forEach(function(it) {
            $scope.algorithmMap[it.id] = it;
            $scope.sidebar[1].items.push({
              name: it.id + "",
              text: it.name,
              href: serviceUrl + "page/task/bsd.html?reportId=" + it.id
            });
          });
          if( $scope.algorithmMap.callback){
            $scope.algorithmMap.callback($scope.algorithmMap);
          }
        });
    }

    function initView() {
      var role = Cookies.get("role");
      if(role === "NORMAL") {
        hideGroups(["report"]);
        hideGroupItems(["upload-task-record", "review-task-record", "point-approve-list"]);
      } else if(role === "REVIEW") {
        hideGroups(["report"]);
        hideGroupItems(["upload-task-record", "point-approve-list"]);
      } else if(role === "FINANCE") {
        hideGroups(["report"]);
        hideGroupItems(["upload-task-record", "review-task-record"]);
      } else if(role === "UPLOAD") {
        hideGroups(["report"]);
        var arrs = ["point-approve-list", "review-task-record"];
        if(appEnv === "aliyun"){
          arrs.push("upload-task-record")
        }
        hideGroupItems(arrs);
      } else if(role === "ADMIN") {
        if(appEnv === "aliyun") {
          hideGroupItems(["upload-task-record"]);
        }
        initData();
      } else if(role === "SUPER") {
        hideGroupItems(["upload-task-record", "point-approve-list", "review-task-record"]);
        initData();
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
  }
]);