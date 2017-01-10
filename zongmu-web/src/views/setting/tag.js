'use strict';

zongmu.controller("tagController", ["$scope", "tagService", "dialog", "breadCrumb",
  function($scope, tagService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.createTag = function() {
      dialog.showCustom({
        templateUrl: "tag.create.html",
        controller: "tagCreateController",
        onConfirm: function() {
          initData();
        }
      });
    };

    function initView() {
      $scope.title = "属性设置";
      breadCrumbProvider.setHistories([{
        text: "标注属性设置",
        href: "#"
      }]);
      $scope.columns = [{
        name: "name",
        text: "属性名称"
      }, {
        name: "type",
        text: "类型"
      }, {
        name: "op",
        text: "操作"
      }];
    }

    function initData() {
      tagService.getTags()
        .then(function(data) {
          $scope.dataSource = data;
        });
    }

    $scope.onViewTagButtonClicked = function(tag) {
      window.location.href = "tag.detail.html?tagId=" + tag.id;
    };

    $scope.onEditTagButtonClicked = function(tag) {
      dialog.showCustom({
        templateUrl: "tag.create.html",
        controller: "tagCreateController",
        params: {
          tag: tag
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteTagButtonClicked = function(tag) {
      dialog.showConfirm({
        title: "提示",
        message: "确定要删除么？",
        onConfirm: function() {
          tagService.deleteTag(tag.id)
            .then(function() {
              dialog.showInfo("删除成功。").then(function() {
                initData();
              });
            });
        }
      });
    }
  }
]);