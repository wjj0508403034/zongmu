'use strict';

zongmu.controller("assetController", ["$scope", "assetService", "breadCrumb", "enumService", "dialog",
  "algorithmService", "assetViewTagService", "viewTagService",
  function($scope, assetService, breadCrumbProvider, enumService, dialog, algorithmService, assetViewTagService, viewTagService) {
    var pageIndex = $.url().param("pageIndex");
    $scope.taskStatus = enumService.getTaskStatus();
    $scope.assetTypes = enumService.getUploadTypes();
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;

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

        this.taskItemStatus = [];
        $scope.taskStatus.forEach(function(it) {
          if(it.checked) {
            this.taskItemStatus.push(it.name);
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

    initView() && initData();
    getAssetTags();

    function initView() {

      $scope.s = {};
      breadCrumbProvider.setHistories([{
        text: "上传记录",
        href: "assets.html"
      }]);
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "assetType",
        text: "类型"
      }, {
        name: "recordTime",
        text: "录制时间"
      }, {
        name: "recordLength",
        text: "录制时长"
      }, {
        name: "pictureCount",
        text: "图片张数"
      }, {
        name: "op",
        text: "操作"
      }];
      $scope.ops = enumService.getOps();

      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍后...");
      assetService.queryAssets(pageIndex, $scope.queryParams.getData())
        .then(function(pageData) {
          $scope.hideLoading();
          $scope.dataset = pageData.content;
          $scope.pageData = {
            totalPage: pageData.totalPages,
            pageIndex: pageData.number
          };
        });
    }

    function getAssetTags() {
      assetService.getAssetTags()
        .then(function(tags) {
          $scope.tags = tags;
        })
    }

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    $scope.onSearchButtonClick = function() {
      initData()
        //    var filter = "";
        //
        //    if($scope.s.assetType) {
        //      filter += "assetType eq " + $scope.s.assetType + ";";
        //    }
        //
        //    if($scope.s.uploadTimeOp && $scope.s.uploadTime) {
        //      filter += "createTime " + $scope.s.uploadTimeOp + " " + $scope.s.uploadTime.toISOString() + ";";
        //    }
        //
        //    if($scope.s.recordTimeOp && $scope.s.recordTime) {
        //      filter += "uploadTime " + $scope.s.recordTimeOp + " " + $scope.s.recordTime.toISOString() + ";";
        //    }
        //
        //    if($scope.s.recordLengthOp && $scope.s.recordLength) {
        //      filter += "recordLength " + $scope.s.recordLengthOp + " " + $scope.s.recordLength + ";";
        //    }
        //
        //    var tags = [];
        //    ($scope.tags || []).forEach(function(it) {
        //      if(it.isSelected) {
        //        tags.push(it.id);
        //      }
        //    });
        //
        //    if(tags.length > 0) {
        //      filter += "tags in " + JSON.stringify(tags) + ";";
        //    }
        //
        //    $scope.showLoading();
        //    $scope.setLoadingText("正在搜索，请稍后...");
        //    assetService.getAssets(pageIndex, filter)
        //      .then(function(pageData) {
        //        $scope.hideLoading();
        //        $scope.dataset = pageData.content;
        //        $scope.pageData = {
        //          totalPage: pageData.totalPages,
        //          pageIndex: pageData.number
        //        };
        //      });
    };

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
      //window.location.href = "assets.html?pageIndex=" + pageIndex;
    });

    $scope.onUploadButtonClick = function() {
      window.location.href = "asset.upload.html";
    };

    $scope.onNewTaskButtonClick = function(rowData) {
      window.location.href = "asset.new.html?assetNo=" + rowData.assetNo;
    };

    $scope.onDeleteButtonClick = function(rowData) {

      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条记录吗？",
        onConfirm: function() {
          $scope.showLoading();
          $scope.setLoadingText("正在删除视频和视频相关数据，请稍后...");
          assetService.deleteAsset(rowData.assetNo)
            .then(function() {
              $scope.hideLoading();
              initData();
            });
        }
      });
    };
  }
]);