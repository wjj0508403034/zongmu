'use strict';

zongmu.controller("assetViewTagController", ["$scope", "assetViewTagService", "dialog", "breadCrumb",
  function($scope, assetViewTagService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.createTag = function() {
      dialog.showCustom({
        templateUrl: "asset.view.tag.create.html",
        controller: "assetViewTagCreateController",
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
          assetViewTagService.deleteAssetViewTag(item.id)
            .then(function() {
              refresh();
            });
        }
      });

    };

    $scope.onViewTagButtonClicked = function(item) {
      window.location.href=`asset.view.tag.detail.html?tagId=${item.id}`;
    };
    
    $scope.onUpdateViewTagButtonClicked = function(item){
      dialog.showCustom({
        templateUrl: "asset.view.tag.create.html",
        controller: "assetViewTagCreateController",
        params: {
          tag: item
        },
        onConfirm: function() {
          refresh();
        }
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
        name: "op",
        text: "操作"
      }];
    }

    function initData() {
      refresh();
    }

    function refresh() {
      assetViewTagService.getAll()
        .then(function(data) {
          $scope.items = data;
        });
    }
  }
]);