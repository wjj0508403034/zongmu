'use strict';

zongmu.controller("algorithmDetailController", ['$q', '$scope', 'algorithmService', 'dialog',
  'breadCrumb', 'colorGroupService', 'colorTagService', 'tagService', "viewTagService",
  function($q, $scope, algorithmService, dialog, breadCrumbProvider, colorGroupService,
    colorTagService, tagService, viewTagService) {
    var algorithmId = $.url().param("algorithmId");
    initView() && initData();

    function initView() {
      $scope.setTitle("算法详细信息");
      if(!algorithmId) {
        dialog.showError("参数错误！");
        return;
      }

      breadCrumbProvider.setHistories([{
        text: "算法设置",
        href: "algorithm.html"
      }, {
        text: "算法详细信息",
        href: "#"
      }]);

      $scope.colorColumns = [{
        name: "name",
        text: "颜色名称"
      }, {
        name: "color",
        text: "颜色"
      }, {
        name: "op",
        text: "操作"
      }];

      $scope.tagColumns = [{
        name: "name",
        text: "属性名称"
      }, {
        name: "type",
        text: "类型"
      }, {
        name: "op",
        text: "操作"
      }];

      $scope.viewTagColumns = [{
        name: "name",
        text: "属性名称"
      }, {
        name: "op",
        text: "操作"
      }];

      return true;
    }

    function initData() {
      algorithmService.getAlgorithm(algorithmId)
        .then(function(algorithm) {
          $scope.algorithm = algorithm;
        });
    }

    $scope.onSetTagsButtonClick = function() {
      $scope.isEdit = true;
    };

    $scope.onSaveButtonClick = function() {
      var tagIds = [];
      $scope.tags.forEach(function(it) {
        if(it.isSelected) {
          tagIds.push(it.id);
        }
      });

      algorithmService.setTags(algorithmId, {
        hasColor: $scope.hasColor,
        tagIds: tagIds.join(";")
      }).then(function() {
        dialog.showInfo("关联成功").then(function() {
          initData();
        })
      });
    };

    $scope.onCancelButtonClick = function() {
      $scope.isEdit = false;
    };

    $scope.onCreateTagsButtonClick = function() {
      dialog.showCustom({
        templateUrl: "tag.create.html",
        controller: "tagCreateController",
        params: {
          algorithmId: algorithmId
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onCreateViewTagsButtonClick = function() {
      dialog.showCustom({
        templateUrl: "view.tag.create.dialog.html",
        controller: "viewTagCreateController",
        params: {
          algorithmId: algorithmId
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onViewViewTagButtonClicked = function(viewTag) {
      window.location.href = `viewTag.detail.html?viewTagId=${viewTag.id}&algorithmId=${algorithmId}`;
    };

    $scope.onDeleteViewTagButtonClicked = function(viewTag) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条场景属性吗？",
        onConfirm: function() {
          viewTagService.delete(viewTag.id)
            .then(function() {
              initData();
            });
        }
      });
    };

    $scope.onEditViewTagButtonClicked = function(viewTag) {
      dialog.showCustom({
        templateUrl: "view.tag.update.dialog.html",
        controller: "viewTagUpdateController",
        params: {
          viewTag: viewTag
        },
        onConfirm: function() {
          initData();
        }
      });
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
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          tagService.deleteTag(tag.id)
            .then(function() {
              initData();
            });
        }
      });
    };

    $scope.onViewTagButtonClicked = function(tag) {
      window.location.href = "tag.detail.html?tagId=" + tag.id + "&algorithmId=" + $scope.algorithm.id;
    };

    $scope.onCreateColorButtonClick = function() {
      dialog.showCustom({
        templateUrl: "color.group.dialog.html",
        controller: "colorGroupDialogController",
        params: {
          algorithm: $scope.algorithm
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onCreateColorTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: "color.create.html",
        controller: "colorCreateController",
        params: {
          group: $scope.algorithm.colorGroup
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onUpdateButtonClick = function(color) {
      dialog.showCustom({
        templateUrl: "color.create.html",
        controller: "colorCreateController",
        params: {
          color: color,
          group: $scope.algorithm.colorGroup
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(color) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          deleteColor(color.id);
        }
      })
    };

    function deleteColor(colorId) {
      colorTagService.deleteColorTag(colorId)
        .then(function() {
          initData();
          /* dialog.showInfo("删除成功!").then(function() {
             initData();
           });*/
        });
    };
  }
]);