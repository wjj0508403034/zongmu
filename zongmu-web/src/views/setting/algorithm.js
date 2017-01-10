'use strict';

zongmu.controller("algorithmSettingController", ["$scope", "algorithmService", "dialog", "breadCrumb",
  function($scope, algorithmService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.onUpdateButtonClick = function(algorithm) {
      dialog.showCustom({
        templateUrl: "algorithm.create.html",
        controller: "algorithmCreateController",
        params: {
          algorithm: algorithm
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onCreateButtonClick = function() {
      dialog.showCustom({
        templateUrl: "algorithm.create.html",
        controller: "algorithmCreateController",
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onViewButtonClick = function(algorithm) {
      window.location.href = "algorithm.detail.html?algorithmId=" + algorithm.id;
    };

    $scope.onDeleteButtonClick = function(algorithm) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          deleteAlgorithm(algorithm.id);
        }
      })
    };

    function deleteAlgorithm(algorithmId) {
      algorithmService.deleteAlgorithm(algorithmId)
        .then(function() {
          initData();
          //        dialog.showInfo("删除成功!").then(function() {
          //          initData();
          //        });
        });
    }

    function initView() {
      $scope.title = "算法设置";
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "算法设置",
        href: "#"
      }]);
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      algorithmService.getAlgorithms()
        .then(function(data) {
          $scope.hideLoading();
          $scope.dataset = data;
        });
    }

  }
]);