'use strict';

zongmu.controller("markFourVideoController", ['$q', '$scope', 'dialog', 'taskRecordService',
  'tagService', '$log', 'formatService', 'reviewRecordService', 'mediaServiceUrl', 'colorTagService', "markUtil", "breadCrumb",

  function ($q, $scope, dialog, taskRecordService, tagService,
    $log, formatService, reviewRecordService, mediaServiceUrl, colorTagService, markUtil, breadCrumbProvider) {
    var huoyun = angular.module("huoyun-ui");
    var taskRecordNo = $.url().param("taskRecordNo");
    var status = $.url().param("status") || 0;
    $scope.currentSelected = null;
    $scope.tempShapes1 = [];
    $scope.tempShapes2 = [];
    $scope.tempShapes3 = [];
    $scope.tempShapes4 = [];
    $scope.names1 = [];
    $scope.names2 = [];
    $scope.names3 = [];
    $scope.names4 = [];
    $scope.shapes1 = [];
    $scope.shapes2 = [];
    $scope.shapes3 = [];
    $scope.shapes3 = [];

    initView() && initData();

    $scope.$watchCollection("tempShapes1", function () {
      onNamesChanged(1);
    });

    $scope.$watchCollection("tempShapes2", function () {
      onNamesChanged(2);
    });
    $scope.$watchCollection("tempShapes3", function () {
      onNamesChanged(3);
    });
    $scope.$watchCollection("tempShapes4", function () {
      onNamesChanged(4);
    });

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

    function onNamesChanged(videoIndex) {
      var temp = [];
      var nameMap = {
        1: [],
        2: [],
        3: [],
        4: []
      };
      [1, 2, 3, 4].forEach(function (it) {
        $scope["tempShapes" + it].forEach(function (shape) {
          //nameMap[it].push(shape.shapeId);
          if (temp.indexOf(shape.shapeId) === -1) {
            temp.push(shape.shapeId);
          }
        });
      });

      [1, 2, 3, 4].forEach(function (it) {
        $scope["names" + it] = temp;
      });
    }

    function setNames(videoIndex, shape) {
      [1, 2, 3, 4].forEach(function (it) {
        if (it !== videoIndex) {
          if ($scope["names" + it].indexOf(shape.shapeId) === -1) {
            $scope["names" + it].push(shape.shapeId);
          }
        }
      });
    }

    function resetNames() {
      [1, 2, 3, 4].forEach(function (it) {
        $scope["names" + it] = [];
        onNamesChanged(it);
      });
    }

    $scope.onCursorChecked = function () {
      $scope.showCursor = true;
    };

    $scope.onCursorUnChecked = function () {
      $scope.showCursor = false;
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

    $scope.onViewTagButtonClick = function () {
      dialog.showCustom({
        templateUrl: 'view.tag.update.dialog.html',
        controller: "viewTagUpdateDialogController",
        params: {
          algorithm: $scope.algorithm,
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function (res) {
          $scope.onSaveClick();
          //window.location.reload();
        }
      });
    };

    function getReviewRecord(reviewRecordNo) {
      reviewRecordService.getReviewRecord(reviewRecordNo)
        .then(function (data) {
          $scope.reviewRecord = data;
        });
    }

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

    function initView() {
      $scope.setTitle("任务标注");
      breadCrumbProvider.setHistories([{
        text: "任务大厅",
        href: "../home/index.html"
      }, {
        text: "任务标注",
        href: "#"
      }]);

      $scope.nameSelected = true;
      if (!taskRecordNo) {
        dialog.showError("参数错误");
        return false;
      }

      $scope.showCursor = true;
      $scope.videoControlBar = huoyun.newObject("VideoControlBar");

      $scope.canReview = true;
      $scope.disableEdit = false;
      var role = Cookies.get("role");
      if (["NORMAL", "FINANCE"].indexOf(role) !== -1) {
        $scope.canReview = false;
      }

      return true;
    }

    function initMarkRecords() {
      $scope.taskRecord.taskMarkRecords.forEach(function (it, index) {
        initMarkGroup(it);
      });
    }

    function initMarkShapeMap(groups) {
      var taskItemFileMap = {};
      groups.forEach(function (it, index) {
        if (!taskItemFileMap[it.taskItemFileId]) {
          taskItemFileMap[it.taskItemFileId] = [];
        }

        taskItemFileMap[it.taskItemFileId].push(it);
      });

      return taskItemFileMap;
    }

    function initNewShape(shape) {
      var $shape = huoyun.newObject("Shape");
      $shape.shapeId = shape.name;
      $shape.id = shape.id;
      $shape.name = shape.name;
      $shape.color = shape.color;
      $shape.type = $scope.taskRecord.taskItem.shapeType;
      $shape.count = $scope.taskRecord.taskItem.sideCount;
      $shape.colorTag = shape.colorTag;

      return $shape;
    }

    function getFileIndex(fileId) {
      for (var index = 1; index <= 4; index++) {
        var key = `taskItemFile${index}`;
        if (fileId === $scope[key].id) {
          return index;
        }
      }
    }

    function initMarkGroup(shape) {
      var shapes = [];
      var shapeMap = initMarkShapeMap(shape.groups);
      var frameIndexInfoMap = {};
      shape.shapeFrameIndexInfos.forEach(function (it) {
        var key = `${it.shapeName}@${it.fileId}`;
        if (!frameIndexInfoMap[key]) {
          frameIndexInfoMap[key] = [];
        }
        frameIndexInfoMap[key].push(it);
      });

      Object.keys(shapeMap).forEach(function (key, index) {
        var fileIndex = getFileIndex(+key);
        if (fileIndex !== undefined) {
          var frameIndexInfo = frameIndexInfoMap[`${shape.name}@${key}`];
          var temp = initNewShape(shape);
          temp.$$timeline = initTimeline(shapeMap[key], frameIndexInfo);
          formatService.convertBackFour(temp, $scope.taskItemFile1.height, $scope.taskItemFile1.width);
          shapes.push(temp);
          $scope[`tempShapes${fileIndex}`].push(temp);
        }
      });

      var tagDorpdownList = {};
      var tagCheckBoxList = {};

      shape.tags.forEach(function (it) {
        var tagItem = $scope.tagsMap[it.tagItemId];
        if (tagItem) {
          tagDorpdownList[tagItem.tagId] = tagItem;
          tagCheckBoxList[tagItem.id] = true;
        }
      });

      angular.forEach(shapes, function (it, index) {
        it.tagDorpdownList = tagDorpdownList;
        it.tagCheckBoxList = tagCheckBoxList;
      });
    }

    function initTimeline(groups, frameIndexInfo) {
      var minIndex = null;
      var maxIndex = undefined;
      var endMap = {};
      frameIndexInfo.forEach(function (it) {
        if (minIndex === null) {
          minIndex = it.startIndex;
        } else {
          if (minIndex > it.startIndex) {
            minIndex = it.startIndex;
          }
        }

        if (maxIndex === undefined) {
          maxIndex = it.endIndex;
        } else if (maxIndex != null) {
          if (maxIndex < it.endIndex) {
            maxIndex = it.endIndex;
          }
        }

        if (it.endIndex) {
          endMap[it.endIndex] = {
            isEnd: true
          }
        }
      });

      var $timeline = huoyun.newObject("Timeline");
      $timeline.data = [];
      $timeline.startIndex = minIndex;
      $timeline.endIndex = maxIndex;

      groups.forEach(function (group, groupIndex) {
        $timeline.data[group.frameIndex] = [];
        group.points.forEach(function (point, index) {
          var $point = huoyun.newObject("point", [point.x, point.y]);
          $timeline.data[group.frameIndex].push($point);

          if (endMap[group.frameIndex] !== undefined) {
            $timeline.data[group.frameIndex].isEnd = true;
          }
        });
      });

      return $timeline;
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

          if (onParamValid()) {
            initTaskItemFiles();
            initVideo();
            initTags(res[1]);
            if ($scope.taskRecord.taskMarkRecords) {
              initMarkRecords();
            }
          }
        });
    }

    function onParamValid() {
      var result = true;
      if ($scope.taskRecord.taskItem && $scope.taskRecord.taskItem.taskItemFiles.length === 4) {
        [1, 2, 3, 4].forEach(function (it, index) {
          if (!$scope.taskRecord.taskItem.taskItemFiles[index].path) {
            result = false;
          }
        });

        if (!result) {
          dialog.showError("视频加载失败");
        }

      } else {
        dialog.showError("参数错误");
        result = false;
      }

      return result;
    }

    function initTaskItemFiles() {
      [1, 2, 3, 4].forEach(function (it, index) {
        var file = $scope.taskRecord.taskItem.taskItemFiles[index];
        var path = file.path;
        if (path.indexOf("front.avi") !== -1) {
          $scope["taskItemFile1"] = file;
          $scope["taskItemFile1"].path = mediaServiceUrl + path;
        } else if (path.indexOf("left.avi") !== -1) {
          $scope["taskItemFile2"] = file;
          $scope["taskItemFile2"].path = mediaServiceUrl + path;
        } else if (path.indexOf("rear.avi") !== -1) {
          $scope["taskItemFile3"] = file;
          $scope["taskItemFile3"].path = mediaServiceUrl + path;
        } else {
          $scope["taskItemFile4"] = file;
          $scope["taskItemFile4"].path = mediaServiceUrl + path;
        }
        //$scope["taskItemFile" + it] = $scope.taskRecord.taskItem.taskItemFiles[index];
        //$scope["taskItemFile" + it].path = mediaServiceUrl + $scope["taskItemFile" + it].path;
      });
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

    function initVideo() {
      [1, 2, 3, 4].forEach(function (it) {
        $scope["video" + it] = huoyun.newObject("Video");
        $scope["video" + it].fps = $scope["taskItemFile" + it].fps || 96;
        $scope["video" + it].setTimeUpdatedCallback(onTimeUpdated);
        $scope["video" + it].setPauseCallback(onVideoPaused);
        $scope.videoControlBar.push($scope["video" + it]);
      });

    }

    $scope.onShape1SelectedChanged = function (shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(1, shape);
    };

    $scope.onShape2SelectedChanged = function (shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(2, shape);
    };

    $scope.onShape3SelectedChanged = function (shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(3, shape);
    };

    $scope.onShape4SelectedChanged = function (shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(4, shape);
    };

    function onShapeSelectionChanged(videoIndex, shape) {
      [1, 2, 3, 4].forEach(function (it) {
        if (it !== videoIndex) {
          setSelectedShapeInList(it, shape);
        }
      });
    }

    $scope.onShape1ObjectCopy = function (shape) {
      onShapeObjectCopy(1, shape);
    };

    $scope.onShape2ObjectCopy = function (shape) {
      onShapeObjectCopy(2, shape);
    };

    $scope.onShape3ObjectCopy = function (shape) {
      onShapeObjectCopy(3, shape);
    };

    $scope.onShape4ObjectCopy = function (shape) {
      onShapeObjectCopy(4, shape);
    };

    $scope.onColorSelectedChanged = function (colorTag) {
      if ($scope.currentSelected && colorTag) {
        [1, 2, 3, 4].forEach(function (it) {
          $scope["tempShapes" + it].forEach(function (itShape, itIndex) {
            if (itShape.shapeId === $scope.currentSelected.shapeId) {
              itShape.changeColor(colorTag.color);
            }
          });
        });
      }
    };

    function onShapeObjectCopy(videoIndex, shape) {
      var find = null;

      [1, 2, 3, 4].forEach(function (it, index) {
        //if (it != videoIndex) {
          $scope["tempShapes" + it].forEach(function (itShape, itIndex) {
            if (itShape !== shape && itShape.shapeId === shape.shapeId) {
              if (find === null) {
                find = itShape;
              }
              itShape.setSelected();
            }
          });
        //}
      });

      if (find) {
        resetNames();
        shape.tagCheckBoxList = find.tagCheckBoxList;
        shape.tagDorpdownList = find.tagDorpdownList;
        shape.colorTag = find.colorTag;
        shape.changeColor(find.color);
        //finds[0].setSelected();
      }
    }

    function setSelectedShapeInList(listIndex, shape) {
      $scope["tempShapes" + listIndex].forEach(function (it, index) {
        if (it.shapeId === shape.shapeId) {
          it.setSelected();
        } else {
          it.setUnSelected();
        }
      });
    }

    function getFinalSaveData() {
      var taskMarkRecords = [];

      var map = getMarkRecordMap();
      Object.keys(map).forEach(function (key) {
        taskMarkRecords.push(map[key]);
      });

      formatService.convertToFour(taskMarkRecords, $scope.taskItemFile1.height, $scope.taskItemFile1.width);
      return formatService.deleteUnMarkShape(taskMarkRecords);
    }

    function getMarkRecordMap() {
      var taskMarkRecordMap = {};
      [1, 2, 3, 4].forEach(function (it, index) {
        $scope["tempShapes" + it].forEach(function (shape) {
          var record = taskMarkRecordMap[shape.shapeId];
          if (!record) {
            record = getSaveTaskMarkRecord(shape);
            taskMarkRecordMap[shape.shapeId] = record;
          }

          if (!record.shapeFrameIndexInfos) {
            record.shapeFrameIndexInfos = [];
          }

          var frameKeys = Object.keys(shape.$$timeline.data);
          var startIndex = null;
          var endIndex = null;
          for (var fIndex = 0; fIndex < frameKeys.length; fIndex++) {
            if (startIndex == null) {
              startIndex = frameKeys[fIndex];
            }

            if (shape.$$timeline.data[frameKeys[fIndex]].isEnd) {
              endIndex = frameKeys[fIndex];
            }

            if (endIndex != null) {
              record.shapeFrameIndexInfos.push({
                startIndex: startIndex,
                endIndex: endIndex,
                fileId: $scope["taskItemFile" + it].id,
                shapeName: shape.shapeId
              });

              startIndex = null;
              endIndex = null;
            } else {
              if (fIndex === frameKeys.length - 1) {
                record.shapeFrameIndexInfos.push({
                  startIndex: startIndex,
                  endIndex: endIndex,
                  fileId: $scope["taskItemFile" + it].id,
                  shapeName: shape.shapeId
                });
              }
            }
          }

          //        record.shapeFrameIndexInfos.push({
          //          startIndex: shape.$$timeline.startIndex,
          //          endIndex: shape.$$timeline.endIndex,
          //          fileId: $scope["taskItemFile" + it].id,
          //          shapeName: shape.shapeId
          //        });

          record.groups = record.groups.concat(getSaveGroup(shape, $scope["taskItemFile" + it]));
        });
      });

      return taskMarkRecordMap;
    }

    function getSaveTaskMarkRecord(shape) {
      var record = {};

      record.name = shape.shapeId;
      record.color = shape.color;
      record.startIndex = shape.$$timeline.startIndex;
      record.endIndex = shape.$$timeline.endIndex;
      if (shape.colorTag) {
        record.colorTagId = shape.colorTag.id;
      }
      record.tags = getSaveTags(shape);
      record.groups = [];

      return record;
    }

    function getSaveGroup(shape, taskItemFile) {
      var groups = [];

      Object.keys(shape.$$timeline.data).forEach(function (key) {
        var group = {};
        group.taskItemFileId = taskItemFile.id;
        group.frameIndex = key;
        group.points = getSavePoints(shape.$$timeline.data[key]);
        groups.push(group);
      });

      return groups;
    }

    function getSavePoints(pointDatas) {
      var points = [];
      pointDatas.forEach(function (it, index) {
        points.push({
          x: it.x,
          y: it.y
        });
      });
      return points;
    }

    function getSaveTags(shape) {
      var tags = [];

      Object.keys(shape.tagDorpdownList).forEach(function (key) {
        tags.push({
          tagItemId: shape.tagDorpdownList[key].id
        });
      });

      Object.keys(shape.tagCheckBoxList).forEach(function (key) {
        tags.push({
          tagItemId: key
        });
      });

      return tags;
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

    $scope.onSaveClick = function () {
      var data = getFinalSaveData();
      $scope.showLoading();
      taskRecordService.saveTaskMarks(taskRecordNo, data)
        .then(function () {
          $scope.hideLoading();
          dialog.showInfo("保存成功").then(function () {
            window.location.reload();
            $log.info("保存成功！");
          });
        });
    };

    $scope.onSubmitClick = function () {
      var data = getFinalSaveData();
      dialog.showConfirm({
        title: "提示",
        message: "确认要提交标记结果么，提交后标记结果将由管理员进行审核，在此期间将不能对改任务进行标记。确认要继续么？",
        onConfirm: function () {
          $scope.showLoading();
          taskRecordService.saveTaskMarks(taskRecordNo, data)
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
      if ($scope.video1 && $scope.video1.dom && $scope.video1.dom.pause) {
        $scope.svgBarDisabled = false;
      } else {
        $scope.svgBarDisabled = true;
      }
      //$scope.svgBarDisabled = true;
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
      /*
       * 现将shapes对象置空，在进行赋值（原因是因为angular里的watchCollection只监听数组的变化，不监听数组里面值的变化。）
       */
      reset();

      disableMark();
      $scope.$apply();
    }

    function reset() {
      $scope.shapes1 = [];
      $scope.shapes2 = [];
      $scope.shapes3 = [];
      $scope.shapes4 = [];
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
            //initData();
            window.location.reload();
          });
        });
    }
  }
]);