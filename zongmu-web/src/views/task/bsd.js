'use strict';

zongmu.controller("bsdReportController", ["$scope", "breadCrumb", "reportService", "dialog", "enumService", "assetViewTagService", "nullUtils", "viewTagService",
  function($scope, breadCrumbProvider, reportService, dialog, enumService, assetViewTagService, nullUtils, viewTagService) {
    var reportId = $.url().param("reportId");
    $scope.algorithmMap.callback = function(map){
      var name = map[reportId].name + "路测视频统计";
      breadCrumbProvider.setHistories([{
        text: name,
        href: "bsd.html"
      }]);
    };
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;
    $scope.taskStatus = [{
      name: "Accept",
      text: "接受"
    }, {
      name: "New",
      text: "未接受"
    }, {
      name: "Pass",
      text: "审核通过"
    }, {
      name: "Reject",
      text: "未审核通过"
    }];
    $scope.queryParams = new QueryParams();
    initView() && initData();
    $scope.onButtonClick = function() {
      if($scope.from === undefined || $scope.from === null) {
        dialog.showError("开始时间段不能为空！");
        return;
      }

      if(!$scope.to === undefined || $scope.to === null) {
        dialog.showError("结束时间段不能为空！");
        return;
      }

      if($scope.from >= $scope.to) {
        dialog.showError("开始时间不能大于结束时间！");
        return;
      }

      initData();
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
      this.taskDate = {
        from: null,
        to: null
      };
      this.recordLength = {
        op: null,
        value: null
      };

      this.taskFinishDate = {
        from: null,
        to: null
      };

      this.assetRecordDate = {
        from: null,
        to: null
      };

      init();

      function init() {
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
        this.taskItemStatus = [];
        $scope.taskStatus.forEach(function(it) {
          if(it.checked) {
            this.taskItemStatus.push(it.name);
          }
        }.bind(this));

        this.assetViewItemIds = [];
        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.assetViewItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        this.viewTagItemMap = getTaskViewItemIds();

        return this;
      };
    }

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    function searching() {
      $scope.showLoading();
      $scope.setLoadingText("正在查询，请稍等...");
      reportService.search(reportId, $scope.queryParams.getData())
        .then(function(data) {
          $scope.hideLoading();
          $scope.tables = [];

          data.tables.forEach(function(it) {
            $scope.tables.push(initTable(it));
          });
          var sumTab = maxTable(data.tables);
          if(sumTab) {
            $scope.tables.push(sumTab);
          }

          console.log(data.tables);

        });
    }

    function initView() {
      if(!reportId) {
        dialog.showError("参数错误！");
        return false;
      }
     
      $scope.reportId = reportId + "";
      breadCrumbProvider.setHistories([{
        text: "路测视频统计",
        href: "bsd.html"
      }]);
      $scope.hours = enumService.getHours();
      $scope.from = 7;
      $scope.to = 11;
      $scope.viewTags = [];
      $scope.taskViewTags = [];
      $scope.expandSearch = false;
      $scope.ops = enumService.getOps();

      assetViewTagService.getAll()
        .then(function(tags) {
          $scope.viewTags = tags;
        });

      viewTagService.getViewTags(reportId)
        .then(function(viewTags) {
          $scope.taskViewTags = viewTags;
        });
      return true;
    }

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    function getTaskViewItemIds() {
      var map = {};
      $scope.taskViewTags.forEach(function(it) {
        map[it.id] = [];
        it.items.forEach(function(item) {
          if(item.checked) {
            map[it.id].push(item.id);
          }
        });

        if(map[it.id].length === 0) {
          delete map[it.id];
        }
      });

      return map;
    };

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在查询，请稍等...");
      var ids = [];
      $scope.viewTags.forEach(function(it) {
        it.items.forEach(function(item) {
          if(item.checked) {
            ids.push(item.id);
          }
        });
      });

      searching();

      //    reportService.getNewBsdReport(reportId, ids.join(","))
      //      .then(function(data) {
      //        $scope.hideLoading();
      //        $scope.tables = [];
      //
      //        data.tables.forEach(function(it) {
      //          $scope.tables.push(initTable(it));
      //        });
      //        var sumTab = sumTable(data.tables);
      //        if(sumTab) {
      //          $scope.tables.push(sumTab);
      //        }
      //
      //        console.log(data.tables);
      //
      //      });
      //    reportService.getBsdReport(reportId, $scope.from, $scope.to)
      //      .then(function(data) {
      //        $scope.hideLoading();
      //        data.videoTable.rows.forEach(function(row) {
      //          Object.keys(row.data).forEach(function(key) {
      //            if(row.data[key]) {
      //              row.data[key] = (row.data[key] / 60).toFixed(2);
      //            } else {
      //              row.data[key] = 0;
      //            }
      //          });
      //        });
      //        $scope.videoTable = initTable(data.videoTable);
      //        $scope.picTable = initTable(data.pictureTable);
      //      });
    }

    function sumTable(tables) {
      var table = {
        name: "总计",
        headers: [{
          "name": "pic",
          "text": "图片"
        }, {
          "name": "video",
          "text": "视频"
        }],
        rows: []
      };

      var table = {
        columns: [{
          name: "text",
          text: "总计\\类型"
        }, {
          "name": "pic",
          "text": "图片"
        }, {
          "name": "video",
          "text": "视频"
        }],
        dataset: [],
        name: "总计"
      };

      var sumPic = 0;
      var sumVideo = 0;
      tables.forEach(function(it, index) {
        it.rows.forEach(function(row, rowIndex) {
          Object.keys(row.data).forEach(function(key) {
            if(rowIndex === 0) {
              sumVideo += row.data[key];
            }
            if(rowIndex === 1) {
              sumPic += row.data[key];
            }
          });
        });

        //      if(index === 0) {
        //        table = $.extend(true, {}, it);
        //      } else {
        //        it.rows.forEach(function(row, rowIndex) {
        //          var sumRow = table.rows[rowIndex];
        //          Object.keys(row.data).forEach(function(key) {
        //            sumRow.data[key] = sumRow.data[key] + row.data[key];
        //          });
        //
        //          table.rows[rowIndex] = sumRow;
        //        });
        //      }
      });
      table.dataset.push({
        "text": "总计",
        "pic": sumPic,
        "video": calc(sumVideo)
      });

      return table;
    }

    function initTable(tableData) {

      var columns = [{
        name: "text",
        text: "类型\\场景属性"
      }].concat(tableData.headers);

      var dataset = tableData.rows.map(function(it, index) {
        var row = {
          text: it.text
        };
        if(index === 0) {
          it.data1 = {};
          Object.keys(it.data).forEach(function(key) {
            it.data1[key] = calc(it.data[key]);
          });
          $.extend(true, row, it.data1);
        } else {
          $.extend(true, row, it.data);
        }

        return row;
      });

      return {
        columns: columns,
        dataset: dataset,
        name: tableData.name
      };
    }

    function calc(val) {
      if(val === 0) {
        return val;
      }

      if(val < 60) {
        return `${val}秒`;
      }

      var sec = val % 60;
      var min = null;
      if(sec === 0) {
        min = val / 60;
      } else {
        min = ((val - sec) / 60) % 60;
      }
      if(val < 60 * 60) {
        return `${min}分 ${sec}秒`;
      }

      var hour = (val - min * 60 - sec) / 3600;
      return `${hour}时${min}分 ${sec}秒`;
    }

    function maxTable(tables) {
      var table = {
        columns: [{
          name: "text",
          text: "总计\\类型"
        }, {
          "name": "pic",
          "text": "图片"
        }, {
          "name": "video",
          "text": "视频"
        }],
        dataset: [],
        name: "总计"
      };

      var maxVideo = 0;
      var maxPicture = 0;

      tables.forEach(function(it, index) {
        it.rows.forEach(function(row, rowIndex) {
          Object.keys(row.data).forEach(function(key) {
            if(rowIndex === 0) {
              if(maxVideo < row.data[key]) {
                maxVideo = row.data[key];
              }
            }
            if(rowIndex === 1) {
              if(maxPicture < row.data[key]) {
                maxPicture = row.data[key];
              }
            }
          });
        });
      });

      table.dataset.push({
        "text": "总计",
        "pic": maxPicture,
        "video": calc(maxVideo)
      });

      return table;
    }

  }
]);