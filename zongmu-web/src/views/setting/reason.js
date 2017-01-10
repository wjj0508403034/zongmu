'use strict';

zongmu.controller("reasonSettingController", ["$scope", "rejectReasonService", "dialog", "breadCrumb",
  function($scope, rejectReasonService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.onCreateButtonClick = function() {
      dialog.showCustom({
        templateUrl: "reason.create.html",
        controller: "reasonCreateController",
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(reason) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          deleteReason(reason.id);
        }
      })
    };

    function deleteReason(reasonId) {
      rejectReasonService.deleteReason(reasonId)
        .then(function() {
          initData();
          //        dialog.showInfo("删除成功!").then(function() {
          //          initData();
          //        });
        });
    }

    function initView() {
      $scope.title = "审批拒绝原因设置";
      $scope.columns = [{
        name: "description",
        text: "描述"
      }, {
        name: "default",
        text: "默认值"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "原因设置",
        href: "#"
      }]);
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      rejectReasonService.getReasons()
        .then(function(data) {
          $scope.hideLoading();
          $scope.reasons = data;
        });
    }

    $scope.onSetDefaultButtonClick = function(reason) {
      $scope.showLoading();
      $scope.setLoadingText("正在保存数据，请稍后...");
      rejectReasonService.setDefault(reason.id)
        .then(function(data) {
          initData();
        });
    };
  }
]);