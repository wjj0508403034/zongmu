'use strict';

zongmu.controller("reviewRecordsController", ["$scope", "reviewRecordService",
  "dialog", "breadCrumb", "taskRecordService", "markUtil", "algorithmService",
  "assetViewTagService", "viewTagService", "enumService", "rejectReasonService",
  function($scope, reviewRecordService, dialog, breadCrumbProvider, taskRecordService,
    markUtil, algorithmService, assetViewTagService, viewTagService, enumService, rejectReasonService) {
    var pageIndex = $.url().param("pageIndex") || 0;
    var tabIndex = 0;
    $scope.assetTypes = enumService.getUploadTypes();
    $scope.taskStatus = enumService.getReviewTaskStatus();
    $scope.ops = enumService.getOps();
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;

    function QueryParams() {
      this.taskName = null;
      this.taskItemNo = null;
      this.assetName = null;
      this.assetNo = null;
      this.userName = null;
      this.uploadDate = {
        from: null,
        to: null
      };
      this.videoLength = {
        op: null,
        value: null
      };

      this.taskFinishDate = {
        from: null,
        to: null
      };

      this.videoRecordDate = {
        from: null,
        to: null
      };

      init();

      function init() {
        $scope.assetTypes.forEach(function(it) {
          it.checked = false;
        });

        ($scope.algorithms || []).forEach(function(it) {
          it.checked = false;
        });

        $scope.taskStatus.forEach(function(it) {
          it.checked = false;
        });

        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });

        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });

        ($scope.reasons || []).forEach(function(it) {
          it.checked = false;
        }.bind(this));
      }

      this.getData = function() {

        this.assetTypes = [];
        $scope.assetTypes.forEach(function(it) {
          if(it.checked) {
            this.assetTypes.push(it.name);
          }
        }.bind(this));

        this.algorithmIds = [];
        ($scope.algorithms || []).forEach(function(it) {
          if(it.checked) {
            this.algorithmIds.push(it.id);
          }
        }.bind(this));

        this.status = [];
        $scope.taskStatus.forEach(function(it) {
          if(it.checked) {
            this.status.push(it.name);
          }
        }.bind(this));

        this.assetViewTagItemIds = [];
        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.assetViewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        this.viewTagItemIds = [];
        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.viewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        this.reasonIds = [];
        ($scope.reasons || []).forEach(function(it) {
          if(it.checked) {
            this.reasonIds.push(it.id);
          }
        }.bind(this));

        return this;
      };
    }

    $scope.queryParams = new QueryParams();

    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "审核记录",
        href: "reviews.html"
      }]);
      $scope.tabs = [{
        name: "all",
        text: "全部",
        active: true
      }, {
        name: "WAITTING",
        text: "待审核"
      }, {
        name: "INPROGRESS",
        text: "审核中"
      }, {
        name: "PASS",
        text: "审核通过"
      }, {
        name: "FAILED",
        text: "审核失败"
      }];

      $scope.columns = [{
        name: "op-first",
        text: ""
      }, {
        name: "reviewRecordNo",
        text: "No."
      }, {
        name: "taskRecordNo",
        text: "任务编号"
      }, {
        name: "subtotal",
        text: "审核失败次数"
      }, {
        name: "status",
        text: "状态"
      }, {
        name: "op",
        text: "操作"
      }];
      return true;
    }

    rejectReasonService.getReasons()
      .then(function(data) {
        $scope.reasons = data;
      });

    algorithmService.getAlgorithms()
      .then(function(data) {
        $scope.algorithms = data;
      });

    assetViewTagService.getAll()
      .then(function(tags) {
        $scope.viewTags = tags;
      });

    viewTagService.getAllViewTags()
      .then(function(viewTags) {
        $scope.taskViewTags = viewTags;
      });

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍后...");
      reviewRecordService.queryReviewRecords(pageIndex, $scope.queryParams.getData())
        .then(function(result) {
          $scope.hideLoading();
          $scope.tasks = result.content;
          $scope.pageData = {
            totalPage: result.totalPages,
            pageIndex: result.number
          };
        });
    }

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    $scope.onSearchButtonClick = function() {
      initData();
    };

    $scope.$on("onTabChanged", function(tabScope, item, index) {
      pageIndex = 0;
      tabIndex = index;
      initData();
    });

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.onStartReviewButtonClick = function(reviewRecord) {
      reviewRecordService.startReview(reviewRecord.reviewRecordNo)
        .then(function() {
          var pageName = markUtil.getMarkPageUrl(reviewRecord.taskRecord.assetType, reviewRecord.taskRecord.taskType, reviewRecord.taskRecord.taskRecordNo);;
          window.location.href = `../mark/${pageName}`;
          //
          //        var pageName = null;
          //        if(reviewRecord.taskRecord.assetType === "FOUR") {
          //          pageName = "video.four.html";
          //        } else if(reviewRecord.taskRecord.assetType === "SINGLE") {
          //          pageName = reviewRecord.taskRecord.taskType === 'PICTURE' ? "pic.html" : "video.html";
          //        } else if(reviewRecord.taskRecord.assetType === "PICTURE") {
          //          pageName = "pic.html";
          //        }
          //
          //        if(pageName) {
          //          window.location.href = "../mark/" + pageName + "?taskRecordNo=" + reviewRecord.taskRecord.taskRecordNo;
          //        }
        });
    };

    $scope.onViewButtonClick = function(reviewRecord) {
      var pageName = markUtil.getMarkPage(reviewRecord.taskRecord);
      if(pageName) {
        window.location.href = "../mark/" + pageName + "?status=1&taskRecordNo=" + reviewRecord.taskRecord.taskRecordNo;
      }
    };

    $scope.onNewTaskButtonClick = function(reviewRecord) {
      reviewRecordService.newTask(reviewRecord.reviewRecordNo)
        .then(function() {
          dialog.showInfo("任务发布成功！")
            .then(function() {
              initData();
            });
        });
    };

    $scope.onBatchReviewButtonClick = function() {
      $scope.enableBatch = true;
    };

    $scope.onSubmitBatchReviewPassButtonClick = function() {
      var reviewRecordNos = getBatchItems();

      if(reviewRecordNos.length === 0) {
        dialog.showError("请选择审核记录!");
        return;
      }

      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍后...");
      reviewRecordService.batchReviewPass(reviewRecordNos)
        .then(function(data) {
          $scope.enableBatch = false;
          $scope.hideLoading();
          dialog.showInfo("批量操作成功！")
            .then(function() {
              initData();
            });
        });
    };

    $scope.onSubmitBatchReviewFailedButtonClick = function() {
      var reviewRecordNos = getBatchItems();

      if(reviewRecordNos.length === 0) {
        dialog.showError("请选择审核记录!");
        return;
      }

      dialog.showCustom({
        templateUrl: 'reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecordNos: reviewRecordNos,
          batch: true
        },
        onConfirm: function() {
          $scope.enableBatch = false;
          dialog.showInfo("批量操作成功！")
            .then(function() {
              initData();
            });
        }
      });
    };

    function getBatchItems() {
      var reviewRecordNos = [];
      $scope.tasks.forEach(function(it) {
        if(it.isSelected) {
          reviewRecordNos.push(it.reviewRecordNo);
        }
      });

      return reviewRecordNos;
    }

    $scope.onCancelBatchReviewButtonClick = function() {
      $scope.enableBatch = false;
    };

    $scope.onSelectedAll = function() {
      setSelectedAll(true);
    };

    $scope.onUnSelectedAll = function() {
      setSelectedAll(false);
    };

    $scope.onReviewFailedButtonClick = function(reviewRecord) {
      dialog.showCustom({
        templateUrl: 'reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: reviewRecord
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onReviewPassButtonClick = function(reviewRecord) {
      taskRecordService.reviewPass(reviewRecord.taskRecordNo)
        .then(function() {
          initData();
        });
    };

    function setSelectedAll(isSelected) {
      if($scope.tasks && $scope.tasks.length > 0) {
        $scope.tasks.forEach(function(it) {
          it.isSelected = isSelected;
        });
      }
    }

    //  $scope.onSearchButtonClick = function() {
    //    $scope.expandSearch = !$scope.expandSearch;
    //  };

  }
]);