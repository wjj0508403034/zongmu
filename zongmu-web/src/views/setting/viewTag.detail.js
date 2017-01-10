'use strict';

zongmu.controller("viewTagDetailController", ["$scope", "viewTagService", "dialog", "breadCrumb",
  function($scope, viewTagService, dialog, breadCrumbProvider) {
    var viewTagId = $.url().param("viewTagId");
    var algorithmId = $.url().param("algorithmId");
    initView() && initData();

    function initView() {
      $scope.title = "用户详细信息";
      breadCrumbProvider.setHistories([{
        text: "算法设置",
        href: "algorithm.html"
      }, {
        text: "算法详细信息",
        href: "algorithm.detail.html?algorithmId=" + algorithmId
      }, {
        text: "场景属性详细信息",
        href: "#"
      }]);

      $scope.columns = [{
        name: "name",
        text: "值"
      }, {
        name: "op",
        text: "操作"
      }];

      return true;
    }

    function initData() {
      viewTagService.getViewTag(viewTagId)
        .then(function(data) {
          $scope.viewTag = data;
        });
    }

    $scope.onCreateViewTagsButtonClick = function() {
      dialog.showCustom({
        templateUrl: "viewTag.item.create.html",
        controller: "NewViewTagItemCreateController",
        params: {
          viewTagId: +viewTagId
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(item) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          viewTagService.deleteTagItem(item.id)
            .then(function() {
              initData();
            });
        }
      });
    };

    $scope.onUpdateButtonClick = function() {

    };
  }
]);