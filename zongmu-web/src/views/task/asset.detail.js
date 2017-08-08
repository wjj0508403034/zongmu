'use strict';

zongmu.controller("assetDetailController", ["$q", "$scope", "assetService", "taskService", "dialog", "$timeout", "breadCrumb", "exportService", "appEnv",
  function($q, $scope, assetService, taskService, dialog, $timeout, breadCrumbProvider, exportService, appEnv) {
    var assetNo = $.url().param("assetNo");
    $scope.appEnv = appEnv;
    initView() && initData();

    $scope.onSearchButtonClick = function() {
      $scope.expandButton = !$scope.expandButton;
    };

    function initView() {
      $scope.expandButton = true;
      breadCrumbProvider.setHistories([{
        text: "上传记录",
        href: "assets.html"
      }, {
        text: "记录详情",
        href: "#"
      }]);

      $scope.taskColumns = [{
        name: "taskName",
        text: "任务名称"
      }, {
        name: "taskType",
        text: "类型"
      }, {
        name: "priority",
        text: "优先级"
      }, {
        name: "showHome",
        text: "是否在任务大厅显示"
      }, {
        name: "op",
        text: "操作"
      }];

      $scope.fileColumns = [{
        name: "assetFileNo",
        text: "No."
      }, {
        name: "fileName",
        text: "文件名"
      }, {
        name: "fileSize",
        text: "文件大小"
      }, {
        name: "assetFileStatus",
        text: "状态"
      }];

      if (!assetNo) {
        dialog.showError("参数错误");
        return false;
      }
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      $q.all([assetService.getAsset(assetNo), taskService.getTasksByAssetNo(assetNo)])
        .then(function(res) {
          $scope.asset = res[0];
          if ($scope.asset.viewTags) {
            $scope.asset.viewTags.forEach(function(it) {
              it.viewTag.items.forEach(function(item) {
                if (item.id === it.assetViewTagItemId) {
                  it.viewTagItem = item;
                }
              })
            });
          }
          $scope.tasks = res[1];
          $scope.hideLoading();
          refresh();
        });
    }

    function refresh() {
      var items = ($scope.asset.assetFiles || []).filter(function(it) {
        return it.assetFileStatus === "FTPUPLOADING" || it.assetFileStatus === "COMPRESSING" || it.assetFileStatus === "UPLOADSUCCESS";
      });

      if (items.length > 0) {
        $timeout(function() {
          getAssetData();
        }, 2000);
      }
    }

    function getAssetData() {
      assetService.getAsset(assetNo)
        .then(function(asset) {
          $scope.asset = asset;
          if ($scope.asset.viewTags) {
            $scope.asset.viewTags.forEach(function(it) {
              it.viewTag.items.forEach(function(item) {
                if (item.id === it.assetViewTagItemId) {
                  it.viewTagItem = item;
                }
              })
            });
          }
          refresh();
        });
    }

    $scope.onCompressButtonClick = function() {
      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍等...");
      assetService.compress(assetNo)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("开始压缩")
            .then(function() {
              initData();
            });
        });
    }

    $scope.onNewTaskButtonClick = function() {
      if (assetNo) {
        window.location.href = "asset.new.html?assetNo=" + assetNo;
      } else {
        dialog.showError("参数错误");
      }
    };

    $scope.onSetTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'asset.tag.dialog.html',
        controller: "assetTagUpdateController",
        params: {
          asset: $scope.asset
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onUpdateAssetViewTagButtonClick = function() {

      dialog.showCustom({
        templateUrl: 'asset.view.tag.update.dialog.html',
        controller: "assetViewTagUpdateDialogController",
        params: {
          asset: $scope.asset
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(rowData) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条记录吗？",
        onConfirm: function() {
          $scope.showLoading();
          $scope.setLoadingText("正在删除任务和任务相关数据，请稍后...");
          taskService.deleteTask(rowData.taskNo)
            .then(function() {
              $scope.hideLoading();
              initData();
            });
        }
      });
    };

    $scope.onExportButtonClick = function(rowData) {
      $scope.showLoading();
      $scope.setLoadingText("正在导出数据，请稍后...");
      var exportServiceProxy = rowData.taskType === 'VIDEO' ?
        exportService.exportTask(assetNo, rowData.taskNo) :
        exportService.exportPicTask(assetNo, rowData.taskNo);
      exportServiceProxy.then(function(data) {
        $scope.hideLoading();
        if (data && data.taskItems.length > 0) {
          dialog.showCustom({
            templateUrl: 'export.failed.dialog.html',
            controller: "exportFailedDialogController",
            params: {
              taskItems: data.taskItems
            },
            onConfirm: function() {}
          });
        } else {
          dialog.showInfo("导出成功");
        }
      });
    };
  }
]);