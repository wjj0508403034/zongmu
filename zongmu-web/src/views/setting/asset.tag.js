'use strict';

zongmu.controller("assetTagController", ["$scope", "assetService", "dialog", "breadCrumb",
  function($scope, assetService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.createTag = function() {
      dialog.showCustom({
        templateUrl: "asset.tag.create.html",
        controller: "assetTagCreateController",
        onConfirm: function() {
          refresh();
        }
      });
    };

    $scope.onDeleteButtonClick = function(tagItem) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          assetService.deleteAssetTag(tagItem.id)
            .then(function() {
              refresh();
              //            dialog.showInfo("删除成功!").then(function() {
              //              refresh();
              //            });
            });
        }
      });

    };

    $scope.onSetDefaultTagButtonClicked = function(tagItem) {
      assetService.setDefaultAssetTag(tagItem.id)
        .then(function() {
          refresh();
        });
    };

    function initView() {
      $scope.title = "视频属性设置";
      breadCrumbProvider.setHistories([{
        text: "视频属性设置",
        href: "#"
      }]);
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "default",
        text: "默认值"
      }, {
        name: "op",
        text: "操作"
      }];
    }

    function initData() {
      refresh();
    }

    function refresh() {
      $scope.roadTags = [];
      $scope.weatherTags = [];
      assetService.getAssetTags()
        .then(function(data) {
          data.forEach(function(it) {
            if(it.category === "ROAD") {
              $scope.roadTags.push(it);
            } else if(it.category === "WEATHER") {
              $scope.weatherTags.push(it);
            }
          });
        });
    }
  }
]);