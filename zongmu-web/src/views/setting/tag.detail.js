'use strict';

zongmu.controller("tagDetailController", ['$q', '$scope', 'tagService', 'dialog', 'breadCrumb',
  function($q, $scope, tagService, dialog, breadCrumbProvider) {
    var tagId = $.url().param("tagId");
    var algorithmId = $.url().param("algorithmId");

    initView() && initData();
    var tagsMap = {};

    function initView() {
      $scope.setTitle("属性详细信息");
      if(!tagId) {
        dialog.showError("参数错误！");
        return;
      }

      breadCrumbProvider.setHistories([{
        text: "算法设置",
        href: "algorithm.html"
      }, {
        text: "算法详细信息",
        href: "algorithm.detail.html?algorithmId=" + algorithmId
      }, {
        text: "属性详细信息",
        href: "#"
      }]);

      $scope.columns = [{
        name: "value",
        text: "值"
      }, {
        name: "default",
        text: "默认值"
      }, {
        name: "op",
        text: "操作"
      }];

      return true;
    }

    function initData() {
      tagService.getTag(tagId)
        .then(function(tag) {
          $scope.tag = tag;
          $scope.defaultValues = [];
          tag.items.forEach(function(it) {
            if(it.default) {
              $scope.defaultValues.push(it);
            }
          });
        });
    }

    $scope.onCreateItemsButtonClicked = function() {
      dialog.showCustom({
        templateUrl: "tag.value.create.html",
        controller: "tagValueCreateController",
        params: {
          tag: $scope.tag
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onSetDefaultValueClicked = function() {
      dialog.showCustom({
        templateUrl: "tag.defaultvalue.html",
        controller: "tagDefaultValueController",
        params: {
          tag: $scope.tag
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onSetDefaultButtonClick = function(tagItem) {
      $scope.showLoading();
      $scope.setLoadingText("正在保存数据，请稍后...");
      tagService.setTagDefaultValue(tagItem.tagId, tagItem)
        .then(function(res) {
          initData();
        });
    };

    $scope.onDeleteTagItemButtonClicked = function(tagItem) {
      dialog.showConfirm({
        title: "提示",
        message: "确定要删除么？",
        onConfirm: function() {
          tagService.deleteTagItem(tagItem.id)
            .then(function() {
              initData();
            });
        }
      });
    };
  }
]);