'use strict';

zongmu.controller("reviewDetailController", ["$q", "$scope", "reviewRecordService", "dialog", "breadCrumb", "rejectReasonService", "taskRecordService",
  function($q, $scope, reviewRecordService, dialog, breadCrumbProvider, rejectReasonService, taskRecordService) {
    var reviewRecordNo = $.url().param("reviewRecordNo");

    initView() && initData();

    function initView() {
      var role = Cookies.get("role");
      $scope.role = role;
      breadCrumbProvider.setHistories([{
        text: "审核记录",
        href: "reviews.html"
      }, {
        text: "记录详情",
        href: "#"
      }]);

      if(!reviewRecordNo) {
        dialog.showError("参数错误");
        return false;
      }
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      reviewRecordService.getReviewRecord(reviewRecordNo)
        .then(function(review) {
          $scope.review = review;
          if(review.status === "FAILED" && review.reasonId) {
            getReason(review.reasonId)
          } else {
            $scope.hideLoading();
          }
        });
    }

    function getReason(reasonId) {
      rejectReasonService.getReason(reasonId)
        .then(function(data) {
          $scope.hideLoading();
          $scope.reason = data;
        });
    }

    $scope.onReviewPassButtonClick = function() {
      taskRecordService.reviewPass($scope.review.taskRecordNo)
        .then(function() {
          initData();
        });
    };

    $scope.onReviewFailedButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: $scope.review
        },
        onConfirm: function() {
          initData();
        }
      });
    };

  }
]);