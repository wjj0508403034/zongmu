'use strict';
/*
 * 首页 - 任务中心
 */
zongmu.controller("taskCenterController", ['$scope', 'taskService', 'dialog', 'enumService', 'assetService',
  'algorithmService', "assetViewTagService", "viewTagService", "markUtil",
  function($scope, taskService, dialog, enumService, assetService, algorithmService, assetViewTagService, viewTagService, markUtil) {
    var pageIndex = $.url().param("pageIndex");
    var filter = $.url().param("filter");
    var role = Cookies.get("role");
    $scope.isAdmin = role === "ADMIN";
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;

    function QueryParams() {
      this.taskName = null;
      this.taskItemNo = null;
      this.assetName = null;
      this.assetNo = null;

      this.point = {
        op: null,
        value: null
      };
      this.createDate = {
        from: null,
        to: null
      };
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
        $scope.uploadTypes.forEach(function(it) {
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
        $scope.uploadTypes.forEach(function(it) {
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

        this.taskItemStatus = [];
        if($scope.isAdmin) {
          $scope.taskStatus.forEach(function(it) {
            if(it.checked) {
              this.taskItemStatus.push(it.name);
            }
          }.bind(this));
        } else {
          this.taskItemStatus.push('NEW');
        }

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

    $scope.uploadTypes = enumService.getUploadTypes();
    $scope.taskStatus = enumService.getTaskStatus();
    $scope.ops = enumService.getOps();
    $scope.queryParams = new QueryParams();

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

    initView();
    initData();
    //getAssetTags();

    function initView() {
      $scope.setTitle("任务大厅");
      $scope.expandSearch = false;
      $scope.s = {};
      $scope.taskTypes = enumService.getTaskTypes();
      $scope.taskStatuses = enumService.getTaskStatus();
      $scope.ops = enumService.getOps();
      $scope.table = {
        columns: [{
          name: "op-first",
          text: "选择"
        }, {
          name: "src",
          text: "任务"
        }, {
          name: "taskItemNo",
          text: "任务编号"
        }, {
          name: "taskName",
          text: "任务名称"
        }, {
          name: "taskType",
          text: "任务类型"
        }, {
          name: "point",
          text: "奖励金币"
        }, {
          name: "createTime",
          text: "创建时间"
        }, {
          name: "status",
          text: "状态"
        }]
      };
    }

    $scope.$watch("table.selectedAll", function() {

      if($scope.table.dataset && $scope.table.dataset.length > 0) {
        $scope.table.dataset.forEach(function(it) {
          it.isSelected = $scope.selectedAll;
        });
      }
    });

    $scope.onSelectedAll = function() {
      setSelectedAll(true);
    };

    $scope.onUnSelectedAll = function() {
      setSelectedAll(false);
    };

    function setSelectedAll(isSelected) {
      if($scope.table.dataset && $scope.table.dataset.length > 0) {
        $scope.table.dataset.forEach(function(it) {
          if(it.status === 'NEW') {
            it.isSelected = isSelected;
          }
        });
      }
    }

    $scope.onBatchTaskButtonClick = function() {
      $scope.enableBatch = true;
    };

    $scope.onCancelBatchTaskButtonClick = function() {
      $scope.enableBatch = false;
    };

    $scope.onSaveBatchTaskButtonClick = function() {

      var taskItemNos = [];
      $scope.table.dataset.forEach(function(it) {
        if(it.isSelected) {
          taskItemNos.push(it.taskItemNo);
        }
      });

      if(taskItemNos.length === 0) {
        dialog.showError("请选择任务!");
        return;
      }

      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍后...");
      taskService.batchAcceptTasks(taskItemNos)
        .then(function(data) {
          $scope.enableBatch = false;
          $scope.hideLoading();
          dialog.showInfo("批量接受任务成功！")
            .then(function() {
              initData();
            });
        });
    };

    $scope.tagMap = {};

    function getAssetTags() {
      assetService.getAssetTags()
        .then(function(tags) {
          $scope.tagMap = {};
          $scope.tags = tags;
          $scope.weatherTags = [];
          $scope.roadTags = [];
          $scope.tags.forEach(function(it) {
            $scope.tagMap[it.id] = it;
            if(it.category === 'ROAD') {
              $scope.roadTags.push(it);
            } else if(it.category === 'WEATHER') {
              $scope.weatherTags.push(it);
            }
          });

        });
    }

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    $scope.$on("tableIndexChanged", function(paginationScope, pageIndex) {
      load(pageIndex);
    });

    $scope.onAcceptTaskClick = function(taskItemNo) {
      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍后...");
      taskService.acceptTask(taskItemNo)
        .then(function(taskItem) {
          $scope.hideLoading();
          dialog.showInfo("已接受接受该任务！")
            .then(function() {
              var pageName = markUtil.getMarkPageUrl(taskItem.assetType, taskItem.taskType, taskItem.taskRecordNo);
              window.location.href = `../mark/${pageName}`;
              //            if(taskItem.assetType === 'SINGLE') {
              //              window.location.href = "../mark/video.html?taskRecordNo=" + taskItem.taskRecordNo;
              //            } else if(taskItem.assetType === 'FOUR') {
              //              window.location.href = "../mark/video.four.html?taskRecordNo=" + taskItem.taskRecordNo;
              //            } else if(taskItem.assetType === 'PICTURE') {
              //              window.location.href = "../mark/pic.html?taskRecordNo=" + taskItem.taskRecordNo;
              //            }
            });
        });
    };

    function initData() {
      //    $scope.showLoading();
      //    $scope.setLoadingText("正在加载，请稍后...");
      //    taskService.getTasks(pageIndex, filter).then(function(result) {
      //      $scope.hideLoading();
      //      $scope.table.dataset = result.content;
      //      $scope.table.pageData = {
      //        totalPage: result.totalPages,
      //        pageIndex: result.number
      //      };
      //    });
      load();
    }

    function load(index) {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍后...");
      //    taskService.getTasks(index, filter).then(function(result) {
      //      $scope.hideLoading();
      //      $scope.table.dataset = result.content;
      //      $scope.table.pageData = {
      //        totalPage: result.totalPages,
      //        pageIndex: result.number
      //      };
      //    });

      taskService.queryTasks(index, $scope.queryParams.getData()).then(function(result) {
        $scope.hideLoading();
        $scope.table.dataset = result.content;
        $scope.table.pageData = {
          totalPage: result.totalPages,
          pageIndex: result.number
        };
      });
    }

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
      //    $scope.s = {
      //      name: null,
      //      taskType: null,
      //      pointOp: null,
      //      point: null,
      //      status: null,
      //      createTimeFrom: null,
      //      createTimeTo: null
      //    };
      //
      //    ($scope.tags || []).forEach(function(tag) {
      //      tag.isSelected = false;
      //    });
    };

    $scope.onSearchButtonClick = function() {
      load();
      //    filter = "";
      //    if($scope.s.name) {
      //      filter += "taskName like " + $scope.s.name + ";";
      //    }
      //
      //    if($scope.s.taskType) {
      //      filter += "taskType eq " + $scope.s.taskType + ";";
      //    }
      //
      //    if($scope.s.pointOp && $scope.s.point) {
      //      filter += "point " + $scope.s.pointOp + " " + $scope.s.point + ";";
      //    }
      //
      //    if($scope.s.status) {
      //      filter += "status eq " + $scope.s.status + ";";
      //    }
      //
      //    if($scope.s.createTimeFrom && $scope.s.createTimeTo && $scope.s.createTimeFrom > $scope.s.createTimeTo) {
      //      dialog.showError("开始时间应小于结束时间！");
      //      return;
      //    }
      //
      //    if($scope.s.createTimeFrom) {
      //      filter += "createTimeFrom eq " + $scope.s.createTimeFrom.toISOString() + ";";
      //    }
      //
      //    if($scope.s.createTimeTo) {
      //      filter += "createTimeTo eq " + $scope.s.createTimeTo.toISOString();
      //    }
      //
      //    //    if($scope.s.createTimeOp && $scope.s.createTime) {
      //    //      filter += "createTime " + $scope.s.createTimeOp + " " + $scope.s.createTime.toISOString() + ";";
      //    //    }
      //
      //    var roadTags = [];
      //    var weatherTags = [];
      //    ($scope.tags || []).forEach(function(it) {
      //      if(it.isSelected) {
      //        if(it.category === 'ROAD') {
      //          roadTags.push(it.id);
      //        } else if(it.category === 'WEATHER') {
      //          weatherTags.push(it.id);
      //        }
      //      }
      //    });
      //
      //    if(roadTags.length > 0) {
      //      filter += "roadTagIds in " + JSON.stringify(roadTags) + ";";
      //    }
      //
      //    if(weatherTags.length > 0) {
      //      filter += "weatherTagIds in " + JSON.stringify(weatherTags) + ";";
      //    }
      //
      //    $scope.showLoading();
      //    $scope.setLoadingText("正在搜索，请稍后...");
      //    taskService.getTasks(pageIndex, filter).then(function(result) {
      //      $scope.hideLoading();
      //      $scope.table.dataset = result.content;
      //      $scope.table.pageData = {
      //        totalPage: result.totalPages,
      //        pageIndex: result.number
      //      };
      //    });
    }
  }
]);