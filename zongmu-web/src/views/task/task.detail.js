'use strict';

zongmu.controller("taskDetailController", ["$q", "$scope", "taskService", "dialog", "breadCrumb", "$timeout",
  function($q, $scope, taskService, dialog, breadCrumbProvider, $timeout) {
    var taskNo = $.url().param("taskNo");
    var pageIndex = $.url().param("pageIndex");

    initView() && initData();

    function initView() {
      $scope.taskColumns = [{
        name: "taskItemNo",
        text: "No."
      }, {
        name: "taskType",
        text: "类型"
      }, {
        name: "status",
        text: "状态"
      }];

      if(!taskNo) {
        dialog.showError("参数错误");
        return false;
      }
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...")
      taskService.getTask(taskNo, pageIndex)
        .then(function(data) {
          $scope.hideLoading();
          $scope.task = data;
          $scope.pageData = {
            totalPage: data.taskItems.totalPages,
            pageIndex: data.taskItems.number
          };
          initTags();
          initNavPath();
          refresh();
        });
    }

    function refresh() {
      var items = ($scope.task.taskItems.content || []).filter(function(it) {
        return it.status === "CUTTING";
      });

      if(items.length > 0) {
        $timeout(function() {
          getTask();
        }, 2000);
      }
    }

    function getTask() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...")
      taskService.getTask(taskNo, pageIndex)
        .then(function(data) {
          $scope.hideLoading();
          $scope.task = data;
          $scope.pageData = {
            totalPage: data.taskItems.totalPages,
            pageIndex: data.taskItems.number
          };

          initTags();
          refresh();
        });
    }

    function initTags() {
      if($scope.task.assetViewTags) {
        $scope.task.assetViewTags.forEach(function(it) {
          it.viewTag.items.forEach(function(item) {
            if(item.id === it.assetViewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }

      if($scope.task.viewTags) {
        $scope.task.viewTags.forEach(function(it) {
          it.viewTag.items.forEach(function(item) {
            if(item.id === it.viewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }
    }

    function initNavPath() {
      breadCrumbProvider.setHistories([{
        text: "上传记录",
        href: "assets.html"
      }, {
        text: "记录详情",
        href: "asset.detail.html?assetNo=" + $scope.task.assetNo
      }, {
        text: "任务详情",
        href: "#"
      }]);
    }

    $scope.$on("tableIndexChanged", function(paginationScope, pageIndex) {
      window.location.href = "task.detail.html?taskNo=" + taskNo + "&pageIndex=" + pageIndex;
    });

    $scope.onSetTopButtonClick = function(top) {
      taskService.setTop(taskNo, top).then(function() {
        initData();
      });
    }

    $scope.onSetShowButtonClick = function(show) {
      taskService.setShow(taskNo, show).then(function() {
        initData();
      });
    }

    $scope.onSetPriorityButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'task.priority.dialog.html',
        controller: "chooseTaskPriorityController",
        params: {
          task: $scope.task
        },
        onConfirm: function() {
          initData();
        }
      });
    };
  }
]);