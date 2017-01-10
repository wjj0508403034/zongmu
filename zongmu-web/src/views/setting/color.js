'use strict';

zongmu.controller("colorSettingController", ["$scope", "colorTagService", "dialog", "breadCrumb",
  function($scope, colorTagService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.onUpdateButtonClick = function(color) {
      dialog.showCustom({
        templateUrl: "color.create.html",
        controller: "colorCreateController",
        params: {
          color: color
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onCreateButtonClick = function() {
      dialog.showCustom({
        templateUrl: "color.create.html",
        controller: "colorCreateController",
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(color) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认要删除此条记录么？",
        onConfirm: function() {
          deleteColor(color.id);
        }
      })
    };

    function deleteColor(colorId) {
      colorTagService.deleteColorTag(colorId)
        .then(function() {
          dialog.showInfo("删除成功!").then(function() {
            initData();
          });
        });
    }

    function initView() {
      $scope.title = "颜色属性设置";
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "color",
        text: "颜色"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "颜色属性设置",
        href: "#"
      }]);
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      colorTagService.getColorTags()
        .then(function(data) {
          $scope.hideLoading();
          $scope.colorTags = data;
        });
    }

  }
]);