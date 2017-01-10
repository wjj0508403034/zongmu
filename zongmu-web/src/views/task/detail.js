'use strict';

zongmu.controller("taskItemDetailController", ['$q', '$scope', 'taskService', 'taskRecordService', 'reviewRecordService', 'dialog', "markUtil",
  function($q, $scope, taskService, taskRecordService, reviewRecordService, dialog, markUtil) {
    var taskItemNo = $.url().param("taskItemNo");

    initView() && initData();

    function initView() {
      $scope.setTitle("任务详细信息");

      $scope.taskRecordColumns = [{
        name: "taskRecordNo",
        text: "标注记录号"
      }, {
        name: "userName",
        text: "标注者"
      }, {
        name: "startTime",
        text: "开始时间"
      }, {
        name: "endTime",
        text: "结束时间"
      }, {
        name: "status",
        text: "状态"
      }, {
        name: "point",
        text: "奖励"
      }];

      $scope.reviewRecordColumns = [{
        name: "reviewRecordNo",
        text: "审核记录号"
      }, {
        name: "userName",
        text: "审核者"
      }, {
        name: "startTime",
        text: "开始时间"
      }, {
        name: "endTime",
        text: "结束时间"
      }, {
        name: "status",
        text: "状态"
      }];

      var role = Cookies.get("role");
      $scope.role = role;
      $scope.canReview = ["NORMAL", "FINANCE"].indexOf(role) === -1;
      return true;
    }

    $scope.onContinueMarkButtonClick = function() {
      gotoNextPage();
    };

    function gotoNextPage() {
      if($scope.task.taskRecordNo) {
        var pageName = markUtil.getMarkPageUrl($scope.task.assetType, $scope.task.taskType, $scope.task.taskRecordNo);
        window.location.href = `../mark/${pageName}`;
        //      if($scope.task.taskType === "VIDEO") {
        //        if($scope.task.taskItemFiles.length === 4) {
        //          window.location.href = "../mark/video.four.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //        } else {
        //          if($scope.task.assetType === "PICTURE") {
        //            window.location.href = "../mark/pic.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //          } else {
        //            window.location.href = "../mark/video.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //          }
        //        }
        //      } else {
        //        if($scope.task.taskItemFiles.length === 4) {
        //          window.location.href = "../mark/pic.four.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //        } else {
        //          window.location.href = "../mark/pic.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //        }
        //
        //      }
      } else {
        dialog.showError("参数错误");
      }
    }

    $scope.onAcceptTaskClick = function() {
      taskService.acceptTask(taskItemNo)
        .then(function(data) {
          var pageName = markUtil.getMarkPageUrl(data.assetType, data.taskType, data.taskRecordNo);
          window.location.href = `../mark/${pageName}`;
          //var pageName = markUtil.getMarkPage(data);
          //window.location.href = "../mark/" + pageName + "?taskRecordNo=" + data.taskRecordNo;
        });
    };

    function initData() {
      if(!taskItemNo) {
        dialog.showError("参数错误");
      } else {
        taskService.getTaskDetail(taskItemNo)
          .then(function(task) {
            $scope.task = task;
          });
      }
    }
  }
]);