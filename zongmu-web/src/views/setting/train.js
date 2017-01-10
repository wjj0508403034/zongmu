'use strict';

zongmu.controller("trainSettingController", ["$scope", "trainService", "dialog", "breadCrumb",
  function($scope, trainService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.createTrain = function() {
      window.location.href = "train.create.html";
    };

    $scope.onUpdateButtonClicked = function(train) {
      window.location.href = "train.create.html?trainId=" + train.id;
    };

    $scope.onDeleteButtonClicked = function(train) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认要删除此条记录么？",
        onConfirm: function() {
          deleteTrain(train.id);
        }
      });
    };

    function deleteTrain(trainId) {
      $scope.setLoadingText("正在删除，请稍后...");
      $scope.showLoading();
      trainService.deleteTrain(trainId)
        .then(function() {
          refresh();
        });
    }

    function initView() {
      $scope.title = "训练设置";
      breadCrumbProvider.setHistories([{
        text: "训练设置",
        href: "#"
      }]);
      $scope.columns = [{
        name: "subject",
        text: "标题"
      }, {
        name: "op",
        text: "操作"
      }];
    }

    function initData() {
      refresh();
    }

    function refresh() {
      $scope.setLoadingText("正在加载，请稍后...");
      $scope.showLoading();
      trainService.getTrains()
        .then(function(data) {
          $scope.hideLoading();
          $scope.dataSource = data;
        });
    }
  }
]);