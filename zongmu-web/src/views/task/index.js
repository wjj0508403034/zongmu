'use strict';

zongmu.controller("myTasksController", ["$scope", "taskRecordService", "breadCrumb", "dialog", "enumService",
  "algorithmService", "assetViewTagService", "viewTagService", "markUtil",
  function($scope, taskRecordService, breadCrumbProvider, dialog, enumService,
    algorithmService, assetViewTagService, viewTagService, markUtil) {
    var pageIndex = $.url().param("pageIndex") || 0;
    var tabIndex = 0;

    $scope.assetTypes = enumService.getUploadTypes();
    $scope.ops = enumService.getOps();
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;
    $scope.taskStatus = [{
      name: "INPROGRESS",
      text: "标注进行中"
    }, {
      name: "WAITTING",
      text: "等待审核"
    }, {
      name: "REVIEWING",
      text: "正在进行审核"
    }, {
      name: "ACCEPTED",
      text: "审核通过"
    }, {
      name: "REJECTED",
      text: "审核失败"
    }];

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    $scope.canView = function(rowData) {
      var role = Cookies.get("role");
      if(role === "ADMIN") {
        return true;
      } else {
        if(["WAITTING", "ACCEPTED"].indexOf(rowData.status) !== -1) {
          return false;
        }

        return true;
      }
    };

    function QueryParams() {
      this.taskName = null;
      this.taskItemNo = null;
      this.assetName = null;
      this.assetNo = null;
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

        this.taskRecordStatus = [];
        $scope.taskStatus.forEach(function(it) {
          if(it.checked) {
            this.taskRecordStatus.push(it.name);
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

        return this;
      };
    }

    $scope.queryParams = new QueryParams();
    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "任务记录",
        href: "index.html"
      }]);

      $scope.tabs = [{
        name: "all",
        text: "全部",
        active: true
      }, {
        name: "INPROGRESS",
        text: "标注进行中"
      }, {
        name: "WAITTING",
        text: "等待审核"
      }, {
        name: "REVIEWING",
        text: "正在进行审核"
      }, {
        name: "ACCEPTED",
        text: "审核通过"
      }, {
        name: "REJECTED",
        text: "审核失败"
      }];

      $scope.columns = [{
        name: "taskItemNo",
        text: "任务名称"
      }, {
        name: "taskType",
        text: "任务类型"
      }, {
        name: "point",
        text: "金币"
      }, {
        name: "status",
        text: "状态"
      }, {
        name: "op",
        text: "操作"
      }];
      return true;
    }

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

    $scope.$on("onTabChanged", function(tabScope, item, index) {
      pageIndex = 0;
      tabIndex = index;
      initData();
    });

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    $scope.onSearchButtonClick = function() {
      initData();
    };

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      taskRecordService.search(pageIndex, $scope.queryParams.getData())
        .then(function(result) {
          $scope.hideLoading();
          $scope.tasks = result.content;
          $scope.pageData = {
            totalPage: result.totalPages,
            pageIndex: result.number
          };
        });
      //    taskRecordService.getMyTaskRecords(pageIndex, tabIndex)
      //      .then(function(result) {
      //        $scope.hideLoading();
      //        $scope.tasks = result.content;
      //        $scope.pageData = {
      //          totalPage: result.totalPages,
      //          pageIndex: result.number
      //        };
      //      });
    }

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
      //window.location.href = "index.html?pageIndex=" + pageIndex;
    });

    $scope.onCancelButtonClicked = function(taskRecord) {
      dialog.showConfirm({
        title: "提示",
        message: "确定要放弃任务么？",
        onConfirm: function() {
          $scope.showLoading();
          $scope.setLoadingText("正在取消任务，请稍后...");
          taskRecordService.cancelTask(taskRecord.taskRecordNo)
            .then(function() {
              $scope.hideLoading();
              window.location.href = "../home/index.html";
            });
        }
      });

    };

    $scope.onViewButtonClicked = function(taskRecord) {
      var pageName = markUtil.getMarkPageUrl(taskRecord.assetType, taskRecord.taskType, taskRecord.taskRecordNo);;
      window.location.href = `../mark/${pageName}`;
      //    if(taskRecord.assetType === "FOUR") {
      //      pageName = "video.four.html";
      //    } else if(taskRecord.assetType === "SINGLE") {
      //      pageName = taskRecord.taskType === 'PICTURE' ? "pic.html" : "video.html";
      //    } else if(taskRecord.assetType === "PICTURE") {
      //      pageName = "pic.html";
      //    }
      //
      //    if(pageName) {
      //      window.location.href = "../mark/" + pageName + "?taskRecordNo=" + taskRecord.taskRecordNo;
      //    }
    }

  }
]);