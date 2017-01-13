'use strict';

zongmu.controller("markVideoController", ['$q', '$scope', 'dialog', 'taskRecordService',
  '$log', 'formatService', 'reviewRecordService', 'mediaServiceUrl', 'breadCrumb', "markUtil", "taskService",

  function ($q, $scope, dialog, taskRecordService, $log,
    formatService, reviewRecordService, mediaServiceUrl, breadCrumbProvider, markUtil, taskService) {
    var huoyun = angular.module("huoyun-ui");
    var taskRecordNo = $.url().param("taskRecordNo");
    var status = $.url().param("status") || 0;
    $scope.tempShapes = [];

    initView() && initData();

    function initView() {
      $scope.setTitle("任务标注");
      breadCrumbProvider.setHistories([{
        text: "任务大厅",
        href: "../home/index.html"
      }, {
        text: "任务标注",
        href: "#"
      }]);

      if (!taskRecordNo) {
        dialog.showError("参数错误");
        return false;
      }

      $scope.showCursor = true;
      $scope.videoControlBar = huoyun.newObject("VideoControlBar");
      $scope.shapes = [];

      $scope.canReview = true;
      $scope.disableEdit = false;
      var role = Cookies.get("role");
      if (["NORMAL", "FINANCE", "SUPER", "UPLOAD"].indexOf(role) !== -1) {
        $scope.canReview = false;
      }
      return true;
    }

    function getReviewRecord(reviewRecordNo) {
      reviewRecordService.getReviewRecord(reviewRecordNo)
        .then(function (data) {
          $scope.reviewRecord = data;
        });
    }

    $scope.onCursorChecked = function () {
      $scope.showCursor = true;
    };

    $scope.onCursorUnChecked = function () {
      $scope.showCursor = false;
    };

    $scope.canContinueMark = function () {
      if (!$scope.taskRecord) {
        return false;
      }
      if (["WAITTING", "REVIEWING", "ACCEPTED"].indexOf($scope.taskRecord.status) !== -1) {
        var role = Cookies.get("role");
        if (["NORMAL", "FINANCE", "SUPER", "UPLOAD"].indexOf(role) !== -1) {
          return false;
        }
      }
      return true;
    };

    $scope.onPreviousButtonClick = function (taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if (pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onNextButtonClick = function (taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if (pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onAssetTagButtonClick = function () {
      dialog.showCustom({
        templateUrl: '../task/asset.tag.dialog.html',
        controller: "assetTagUpdateController",
        params: {
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function (res) {
          if (res.roadTag && res.roadTag.name) {
            $scope.taskRecord.taskItem.roadTag = res.roadTag;
            $scope.taskRecord.taskItem.roadTagId = res.roadTag.id;
          }

          if (res.weatherTag && res.weatherTag.name) {
            $scope.taskRecord.taskItem.weatherTag = res.weatherTag;
            $scope.taskRecord.taskItem.weatherTagId = res.weatherTag.id;
          }
        }
      });
    };

    $scope.onViewTagButtonClick = function () {
      dialog.showCustom({
        templateUrl: 'view.tag.update.dialog.html',
        controller: "viewTagUpdateDialogController",
        params: {
          algorithm: $scope.algorithm,
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function (res) {
          $scope.onSaveClick(true);
          //window.location.reload();
        }
      });
    };

    function initViewTags() {
      if ($scope.taskRecord.taskItem.viewTags) {
        $scope.taskRecord.taskItem.viewTags.forEach(function (it) {
          it.viewTag.items.forEach(function (item) {
            if (item.id === it.viewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }
    }

    function initTags(algorithm) {
      $scope.algorithm = algorithm;
      $scope.tags = algorithm.tags;
      $scope.tagsMap = $scope.tags.reduce(function (map, it, index) {
        it.items.forEach(function (item, itemIndex) {
          map[item.id] = item;
          map[item.id].type = it.type;
        });
        return map;
      }, {});

      $scope.colorGroup = algorithm.colorGroup;
      initViewTags();
    }

    function initData() {
      $q.all([taskRecordService.getTaskMarks(taskRecordNo, status),
      taskRecordService.getAlgorithm(taskRecordNo)
      ])
        .then(function (res) {
          $scope.taskRecord = res[0];

          if ($scope.taskRecord.reviewRecordNo) {
            getReviewRecord($scope.taskRecord.reviewRecordNo);
          }
          if ($scope.taskRecord.taskItem && $scope.taskRecord.taskItem.taskItemFiles.length > 0) {
            $scope.taskItemFile = $scope.taskRecord.taskItem.taskItemFiles[0];
          } else {
            dialog.showError("参数错误");
            return false;
          }

          if (!$scope.taskItemFile.path) {
            dialog.showError("参数错误,没有找到对应的视频文件。");
            return false;
          } else {
            $scope.taskItemFile.path = mediaServiceUrl + $scope.taskItemFile.path;
          }

          initTags(res[1]);
          initVideo();

          if ($scope.taskRecord.taskMarkRecords) {
            $scope.taskRecord.taskMarkRecords.forEach(function (it, index) {
              it.tagsMap = $scope.tagsMap;
              it.sideCount = $scope.taskRecord.taskItem.sideCount;
              it.taskItem = $scope.taskRecord.taskItem;
            });

            var $shapes = formatService.toMarkViewModel($scope.taskRecord.taskMarkRecords);
            formatService.convertBackSingle($shapes, $scope.taskItemFile.height, $scope.taskItemFile.width);
            if ($shapes.length > 0) {
              $scope.tempShapes = $shapes;
            }
          }
        });
    }

    function initVideo() {
      $scope.video = huoyun.newObject("Video");
      $scope.video.fps = $scope.taskItemFile.fps;
      $scope.video.setTimeUpdatedCallback(onTimeUpdated);
      $scope.video.setPauseCallback(onVideoPaused);
      $scope.videoControlBar.push($scope.video);
    }

    function getDataBeforeSave() {

      var tempData = formatService.toMarkDataModel($scope.tempShapes);
      formatService.convertToSingle(tempData, $scope.taskItemFile.height, $scope.taskItemFile.width);
      return formatService.deleteUnMarkShape(tempData);;
    }

    $(document).on("keydown", function (event) {
      if (event.ctrlKey) {
        switch (event.keyCode) {
          case 81:
            event.stopPropagation();
            event.preventDefault();
            $scope.onSubmitClick();
            break;
          case 83:
            event.stopPropagation();
            event.preventDefault();
            $scope.onSaveClick();
            break;
        }
      }
    });

    $scope.onSaveClick = function (needRefresh) {
      $scope.showLoading();
      var data = [];
      data.push({
        taskItemFile: $scope.taskItemFile,
        markShapes: $scope.tempShapes
      });

      var tempData = getDataBeforeSave();
      taskRecordService.saveTaskMarks(taskRecordNo, tempData)
        .then(function () {
          $scope.hideLoading();
          dialog.showInfo("保存成功").then(function () {
            window.location.reload();
            $log.info("保存成功！");
          });
        });
    };

    $scope.onSubmitClick = function () {
      if($scope.taskRecord.status === 'ACCEPTED'){
        console.log("Task status is ACCEPTED, can't submit");
        return;
      }
      
      var data = [];
      data.push({
        taskItemFile: $scope.taskItemFile,
        markShapes: $scope.tempShapes
      });

      dialog.showConfirm({
        title: "提示",
        message: "确认要提交标记结果么，提交后标记结果将由管理员进行审核，在此期间将不能对改任务进行标记。确认要继续么？",
        onConfirm: function () {
          taskRecordService.saveTaskMarks(taskRecordNo, getDataBeforeSave())
            .then(function () {
              sumbitTask();
            });
        }
      })
    };

    $scope.onStartReviewClick = function () {
      if (!$scope.taskRecord.reviewRecordNo) {
        dialog.showError("参数错误");
        return;
      }
      reviewRecordService.startReview($scope.taskRecord.reviewRecordNo)
        .then(function () {
          window.location.reload();
        });
    };

    $scope.onReviewPassClick = function () {
      taskRecordService.reviewPass(taskRecordNo)
        .then(function () {
          dialog.showInfo("保存成功").then(function () {
            window.location.reload();
            //initData();
          });
        });
    };

    $scope.onReviewFailClick = function () {
      dialog.showCustom({
        templateUrl: '../task/reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: {
            taskRecordNo: taskRecordNo
          }
        },
        onConfirm: function () {
          window.location.reload();
          //initData();
        }
      });
    };

    function disableMark() {
      if ($scope.video && $scope.video.dom && $scope.video.dom.pause) {
        $scope.svgBarDisabled = false;
      } else {
        $scope.svgBarDisabled = true;
      }
    }

    function enableMark() {
      if ($scope.taskRecord.status === "INPROGRESS") {
        $scope.svgBarDisabled = false;
      } else {
        $scope.svgBarDisabled = true;
        $log.warn("Enabled mark failed, because current task record status not inprogress");
      }
    }

    function onTimeUpdated(currentTime, totalTime) {
      disableMark();
      $scope.$apply();
    }

    function onVideoPaused() {
      enableMark();
      $scope.$apply();
    }

    function sumbitTask() {
      taskRecordService.finishMark(taskRecordNo)
        .then(function () {
          $scope.hideLoading();
          dialog.showInfo("提交成功").then(function () {
            window.location.reload();
            //window.location.href = "../task/index.html";
          });
        });
    }
  }
]);