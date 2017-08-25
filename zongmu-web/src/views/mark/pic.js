'use strict';

zongmu.controller("markPicController", ['$q', '$scope', 'taskService', 'dialog', 'tagService',
  'taskRecordService', 'mediaServiceUrl', 'formatService', '$log', 'reviewRecordService',
  'colorTagService', 'breadCrumb', 'markUtil',
  function($q, $scope, taskService, dialog, tagService, taskRecordService, mediaServiceUrl,
    formatService, $log, reviewRecordService, colorTagService, breadCrumbProvider, markUtil) {
    var taskRecordNo = $.url().param("taskRecordNo");
    var status = $.url().param("status") || 0;
    var markData = null;
    initView() && initData();

    $scope.onSaveButtonClick = function() {
      $scope.setLoadingText("正在保存，请稍等...");
      $scope.showLoading();
      taskRecordService.saveTaskMarks(taskRecordNo, getDataBeforeSave())
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("保存成功").then(function() {
            window.location.reload();
          });
        });
    };

    $scope.canContinueMark = function() {
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

    $scope.onViewTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'view.tag.update.dialog.html',
        controller: "viewTagUpdateDialogController",
        params: {
          algorithm: $scope.algorithm,
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          $scope.onSaveButtonClick();
          //window.location.reload();
        }
      });
    };

    $scope.onSubmitClick = function() {

      dialog.showConfirm({
        title: "提示",
        message: "确认要提交标记结果么，提交后标记结果将由管理员进行审核，在此期间将不能对改任务进行标记。确认要继续么？",
        onConfirm: function() {
          $scope.setLoadingText("正在保存，请稍等...");
          $scope.showLoading();
          taskRecordService.saveTaskMarks(taskRecordNo, getDataBeforeSave())
            .then(function() {
              sumbitTask();
            });
        }
      });
    };

    $scope.onStartReviewClick = function() {
      if (!$scope.taskRecord.reviewRecordNo) {
        dialog.showError("参数错误");
        return;
      }
      reviewRecordService.startReview($scope.taskRecord.reviewRecordNo)
        .then(function() {
          window.location.reload();
        });
    };

    $scope.onReviewPassClick = function() {
      taskRecordService.reviewPass(taskRecordNo)
        .then(function() {
          dialog.showInfo("保存成功").then(function() {
            initData();
          });
        });
    };

    $scope.onReviewFailClick = function() {
      dialog.showCustom({
        templateUrl: '../task/reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: {
            taskRecordNo: taskRecordNo
          }
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    function sumbitTask() {
      taskRecordService.finishMark(taskRecordNo)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("提交成功").then(function() {
            window.location.reload();
          });
        });
    }

    function getDataBeforeSave() {
      var tempData = formatService.toMarkDataModel($scope.tempShapes);
      formatService.convertToPicSingle(tempData, $scope.taskItemFile.height, $scope.taskItemFile.width);
      return formatService.deleteUnMarkShape(tempData);;
    }

    $(document).on("keydown", function(event) {
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
            $scope.onSaveButtonClick();
            break;
        }
      }
    });

    $scope.onCursorChecked = function() {
      $scope.showCursor = true;
    };

    $scope.onCursorUnChecked = function() {
      $scope.showCursor = false;
    };

    $scope.onPreviousButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if (pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onNextButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if (pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    function initViewTags() {
      if ($scope.taskRecord.taskItem.viewTags) {
        $scope.taskRecord.taskItem.viewTags.forEach(function(it) {
          it.viewTag.items.forEach(function(item) {
            if (item.id === it.viewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }
    }

    $scope.onAssetTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: '../task/asset.tag.dialog.html',
        controller: "assetTagUpdateController",
        params: {
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          console.log(res)
          if (res.viewTags) {
            console.log(res.viewTags)
          }
        }
      });
    };

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
      $scope.tempShapes = [];
      $scope.shapes = [];

      $scope.canReview = true;
      $scope.disableEdit = false;
      var role = Cookies.get("role");
      if (["NORMAL", "FINANCE", "SUPER", "UPLOAD"].indexOf(role) !== -1) {
        $scope.canReview = false;
      }

      return true;
    }

    function initTags(algorithm) {
      $scope.algorithm = algorithm;
      $scope.tags = algorithm.tags;
      $scope.tagsMap = $scope.tags.reduce(function(map, it, index) {
        it.items.forEach(function(item, itemIndex) {
          map[item.id] = item;
          map[item.id].type = it.type;
        });
        return map;
      }, {});

      $scope.colorGroup = algorithm.colorGroup;
      initViewTags();
    }

    function getReviewRecord(reviewRecordNo) {
      reviewRecordService.getReviewRecord(reviewRecordNo)
        .then(function(data) {
          $scope.reviewRecord = data;
        });
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      $q.all([taskRecordService.getTaskMarks(taskRecordNo, status),
          taskRecordService.getAlgorithm(taskRecordNo)
        ])
        .then(function(res) {
          $scope.hideLoading();
          $scope.taskRecord = res[0];
          if ($scope.taskRecord.reviewRecordNo) {
            getReviewRecord($scope.taskRecord.reviewRecordNo);
          }
          initTags(res[1]);

          if ($scope.taskRecord.taskItem && $scope.taskRecord.taskItem.taskItemFiles.length > 0) {
            $scope.taskItemFile = $scope.taskRecord.taskItem.taskItemFiles[0];
          } else {
            dialog.showError("参数错误");
            return false;
          }

          var tagsMap = $scope.tags.reduce(function(map, it, index) {
            it.items.forEach(function(item, itemIndex) {
              map[item.id] = item;
              map[item.id].type = it.type;
            });
            return map;
          }, {});

          if (!$scope.taskItemFile.path) {
            dialog.showError("参数错误,没有找到对应的图片文件。");
            return false;
          } else {
            $scope.taskItemFile.path = mediaServiceUrl + $scope.taskItemFile.path;
          }

          if ($scope.taskRecord.taskMarkRecords) {
            $scope.taskRecord.taskMarkRecords.forEach(function(it, index) {
              it.tagsMap = tagsMap;
              it.sideCount = $scope.taskRecord.taskItem.sideCount;
              it.taskItem = $scope.taskRecord.taskItem;
            });

            var $shapes = formatService.toMarkViewModel($scope.taskRecord.taskMarkRecords);
            formatService.convertBackPicSingle($shapes, $scope.taskItemFile.height, $scope.taskItemFile.width);
            if ($shapes.length > 0) {
              $scope.tempShapes = $shapes;
            }
          }

          $scope.frameIndex = 1;
        });
    }
  }
]);