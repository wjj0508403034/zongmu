'use strict';

zongmu.controller("assetViewTagDetailController", ["$scope", "assetViewTagService", "dialog", "breadCrumb",
  function($scope, assetViewTagService, dialog, breadCrumbProvider) {
    var tagId = $.url().param("tagId");
    initView();
    initData();

    $scope.createTag = function() {
      dialog.showCustom({
        templateUrl: "asset.view.tag.item.create.html",
        controller: "assetViewTagItemCreateController",
        params: {
          assetViewTagId: +tagId
        },
        onConfirm: function() {
          refresh();
        }
      });
    };

    $scope.onDeleteButtonClick = function(item) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          assetViewTagService.deleteTagItem(item.id)
            .then(function() {
              refresh();
            });
        }
      });

    };

    $scope.onSetDefaultTagButtonClicked = function(tagItem) {
      assetViewTagService.setItemDefault(tagItem.id)
        .then(function() {
          refresh();
        });
    };

    function initView() {
      $scope.title = "视频属性设置";
      breadCrumbProvider.setHistories([{
        text: "视频属性设置",
        href: "asset.view.tag.html"
      }, {
        text: "详细设置",
        href: "#"
      }]);
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "isDefault",
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
      assetViewTagService.getViewTag(tagId)
        .then(function(data) {
          $scope.tag = data;
        });
    }
  }
]);