'use strict';

zongmu.controller("newTaskController", ["$scope", "assetService", "dialog", "enumService", "taskService",
  "breadCrumb", "$q", "algorithmService", "viewTagService",
  function($scope, assetService, dialog, enumService, taskService, breadCrumbProvider, $q, algorithmService, viewTagService) {
    var assetNo = $.url().param("assetNo");
    var asset = null;
    initView() && initData();

    $scope.onCancelButtonClick = function() {
      dialog.showConfirm({
        title: "提示",
        message: "确定要放弃修改？",
        onConfirm: function() {
          history.back();
        }
      });
    };

    $scope.onSaveButtonClick = function() {
      var data = $scope.data;

      if(!data.taskName) {
        dialog.showInfo("请填写任务名称");
        return;
      }

      if(!data.taskType) {
        dialog.showInfo("请选择任务类型");
        return;
      }

      if(!data.algorithmId) {
        dialog.showInfo("请选择算法类型");
        return;
      }

      if(data.taskType === 'VIDEO') {

        if(!data.timeInterval || data.timeInterval < 30) {
          dialog.showInfo("请设置视频切割的时间间隔，间隔必须大于等于30秒");
          return;
        }

      } else {
        if(!data.shapeType) {
          dialog.showInfo("请选择标注形状");
          return;
        }

        if(data.shapeType === 'POLYLINE') {
          if(!data.sideCount || data.sideCount < 3 || data.sideCount >= 10) {
            dialog.showInfo("请填写多边形边数，边数必须大于等于3且小于等于10.");
            return;
          }
        }
        if($scope.asset.assetType !== "PICTURE") {
          if(!data.timeInterval || data.timeInterval < 3) {
            dialog.showInfo("请设置图片提取的时间间隔，间隔必须大于等于3秒");
            return;
          }
        }

      }

      if(!data.point) {
        dialog.showInfo("请设置奖励金币，并且金币不能为0。");
        return;
      }

      if(data.point <= 0) {
        dialog.showInfo("金币必须大于0。");
        return;
      }

      data.viewTags = getSelectedViewTagItems();

      $scope.showLoading();
      taskService.createTask(data)
        .then(function(res) {
          $scope.hideLoading();
          dialog.showInfo("保存成功！").then(function() {
            window.location.href = "asset.detail.html?assetNo=" + assetNo;
          });
        })
    };

    function initView() {
      if(!assetNo) {
        dialog.showError("参数不正确");
        return false;
      }

      breadCrumbProvider.setHistories([{
        text: "上传记录",
        href: "assets.html"
      }, {
        text: "记录详情",
        href: "asset.detail.html?assetNo=" + assetNo
      }, {
        text: "新建任务",
        href: "#"
      }]);

      $scope.taskTypes = enumService.getTaskTypes();
      $scope.shapeTypes = enumService.getShapeTypes();
      $scope.videoShapeTypes = enumService.getVideoShapeTypes();
      $scope.taskPriorities = enumService.getTaskPriorities();
      $scope.selection = {};
      return true;
    }

    function initData() {
      $scope.data = {
        assetNo: assetNo,
        taskType: "VIDEO",
        shapeType: "RECT",
        assetTags: [],
        priority: 2
      };

      $scope.viewTags = [];

      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      $q.all([assetService.getAsset(assetNo), algorithmService.getAlgorithms()])
        .then(function(res) {
          $scope.hideLoading();
          $scope.asset = res[0];
          $scope.algorithms = res[1];
          if($scope.algorithms.length > 0) {
            $scope.data.algorithmId = $scope.algorithms[0].id;
          }
          if($scope.asset.assetType === "PICTURE") {
            $scope.data.taskType = "PICTURE";
          }

          if($scope.asset.viewTags) {
            $scope.asset.viewTags.forEach(function(it) {
              it.viewTag.items.forEach(function(item) {
                if(item.id === it.assetViewTagItemId) {
                  it.viewTagItem = item;
                }
              })
            });
          }
        });
    }

    function initViewTags() {
      $scope.viewTagsMap = {};
      $scope.viewTags.forEach(function(it) {
        it.items.forEach(function(item) {
          if(item.default) {
            $scope.viewTagsMap[it.id] = item.id;
          }
        });

        if(!$scope.viewTagsMap[it.id] && it.items.length > 0) {
          $scope.viewTagsMap[it.id] = it.items[0].id;
        }
      });
    }

    function getSelectedViewTagItems() {

      var viewTags = [];

      Object.keys($scope.viewTagsMap).forEach(function(key) {
        if($scope.viewTagsMap[key] != null || $scope.viewTagsMap[key] != undefined) {
          viewTags.push({
            viewTagId: key,
            viewTagItemId: $scope.viewTagsMap[key]
          });
        }
      });

      return viewTags;
    }

    $scope.$watch("data.algorithmId", function() {
      if($scope.data.algorithmId) {
        $scope.showLoading();
        $scope.setLoadingText("正在加载数据，请稍后...");
        viewTagService.getViewTags($scope.data.algorithmId)
          .then(function(res) {
            $scope.hideLoading();
            $scope.viewTags = res;
            initViewTags();
          });
      }
    });
  }
]);