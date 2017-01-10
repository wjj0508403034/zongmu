'use strict';

zongmu.controller("trainCreateController", ["$scope", "trainService", "dialog", "breadCrumb",
  function($scope, trainService, dialog, breadCrumbProvider) {
    var trainId = $.url().param("trainId");
    initView() && initData();

    function initView() {
      var text = trainId ? "修改训练设置文档" : "新建训练设置文档";
      $scope.title = text;
      breadCrumbProvider.setHistories([{
        text: "训练设置",
        href: "train.html"
      }, {
        text: text,
        href: "#"
      }]);
      $scope.train = {};
      return true;
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.train.subject) {
        dialog.showError("文档名称不能为空。");
        return;
      }

      if(!$scope.train.body) {
        dialog.showError("文档内容不能为空。");
        return;
      }

      $scope.setLoadingText("正在保存，请稍后...");
      $scope.showLoading();
      if(trainId) {
        trainService.updateTrain(trainId, $scope.train)
          .then(function() {
            $scope.hideLoading();
            window.location.href = "train.html";
          });
      } else {
        trainService.createTrain($scope.train)
          .then(function() {
            $scope.hideLoading();
            window.location.href = "train.html";
          });
      }
    }

    $scope.onCancelButtonClick = function() {
      history.back();
    };

    function initData() {
      if(trainId) {
        $scope.setLoadingText("正在加载，请稍后...");
        $scope.showLoading();
        trainService.getTrain(trainId)
          .then(function(train) {
            $scope.hideLoading();
            $scope.train = train;
          });
      }
    }
  }
]);