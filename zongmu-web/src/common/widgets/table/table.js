'use strict';

angular.module('huoyun-ui').directive("widgetsTable", ["huoyunUtil", "$compile", function(huoyunUtil, $compile) {
  return {
    restrict: "A",
    templateUrl: "widgets/table/table.html",
    replace: true,
    transclude: "element",
    scope: {
      "columns": "=",
      "dataset": "=",
      "emptyText": "@",
      "pageData": "="
    },
    controller: "tableController",
    compile: function(elem, attrs, transFunc) {

      return function($scope, element, attrs, controller) {
        var tbody = element.find("tbody");
        var thead = element.find("thead tr");
        var headCache = [];
        // 这个cache用来存储创建了多少元素，在销毁这些元素时有效
        var cache = [];
        $scope.$watchCollection("dataset", function() {
          destory();

          if($scope.dataset && $scope.dataset.length > 0) {
            angular.forEach($scope.dataset, function(row, rowIndex) {
              var $rowScope = $scope.$new();
              $rowScope.rowData = row;
              $rowScope.rowIndex = rowIndex;
              tbody.append(createRowElem($rowScope, rowIndex));
            });
          }
        });

        $scope.$watchCollection("columns", function() {
          destoryHeadCache();
          if($scope.columns && $scope.columns.length > 0) {
            angular.forEach($scope.columns, function(column, columnIndex) {
              var $columnScope = $scope.$new();
              $columnScope.columnData = column;
              $columnScope.columnIndex = columnIndex;
              thead.append(createColumnElem($columnScope, columnIndex));
            });
          }
        });

        function createColumnElem($columnScope, headColumnIndex) {
          var columnElement = angular.element("<th></th>");
          columnElement.attr("name", $columnScope.columnData.name);
          var cellTemplate = null;

          //获取用户自定义模板
          transFunc($columnScope, function(clone, columnScope) {
            angular.forEach(clone.children(), function(childElem, index) {
              var columnIndex = childElem.getAttribute("column-index");
              if(childElem.hasAttribute("head-template") && Number(columnIndex) === headColumnIndex) {
                cellTemplate = childElem;
              }
            });
          });

          // 创建默认模板
          if(!cellTemplate) {
            columnElement.text($columnScope.columnData.text);
          } else {
            columnElement.append(cellTemplate);
          }

          $compile(columnElement)($columnScope);
          headCache.push(columnElement);
          return columnElement;
        }

        function destoryHeadCache() {
          angular.forEach(headCache, function(item, index) {
            var scope = item.data()["$scope"];
            if(scope) {
              scope.$destroy();
            }
            item.remove();
          });
          headCache = [];
        }

        function destory() {
          angular.forEach(cache, function(item, index) {
            var scope = item.data()["$scope"];
            if(scope) {
              scope.$destroy();
            }
            item.remove();
          });
          cache = [];
        }

        function createRowElem($rowScope, rowIndex) {
          var rowElement = angular.element("<tr></tr>");
          angular.forEach($scope.columns, function(column, cellIndex) {
            var $cellScope = $rowScope.$new();
            $cellScope.cellData = $rowScope.rowData[column.name];
            $cellScope.rowData = $rowScope.rowData;
            $cellScope.cellIndex = cellIndex;
            // table 上一级的scope
            $cellScope.$superScope = $rowScope.$parent.$parent;
            rowElement.append(createCellElem($cellScope, cellIndex, column.name));
          });

          $compile(rowElement)($rowScope)
          cache.push(rowElement);
          return rowElement;
        }

        function createCellElem($cellScope, cellIndex, columnName) {
          var cellElement = angular.element("<td></td>");
          cellElement.attr("name", columnName);
          var cellTemplate = null;

          //获取用户自定义模板
          transFunc($cellScope, function(clone, cellScope) {
            angular.forEach(clone.children(), function(childElem, index) {
              var columnIndex = childElem.getAttribute("column-index");
              if(childElem.hasAttribute("cell-template") && Number(columnIndex) === cellIndex) {
                cellTemplate = childElem;
              }
            });
          });

          // 创建默认模板
          if(!cellTemplate) {
            cellTemplate = angular.element("<div cell-template>{{cellData}}</div>");
          }

          cellElement.append(cellTemplate);
          $compile(cellElement)($cellScope);
          cache.push(cellElement);
          return cellElement;
        }
      };
    }
  };
}]);

angular.module('huoyun-ui').controller("tableController", ["$scope", function($scope) {

}]);

angular.module('huoyun-ui').directive("widgetsPagination", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/table/pagination.html",
    replace: true,
    scope: {
      totalPage: "@",
      pageIndex: "@"
    },
    controller: "paginationController",
    link: function($scope, elem, attrs) {

    }
  };
});

angular.module('huoyun-ui').controller("paginationController", ["$scope", "huoyunUtil", "$log", "dialog", function($scope, huoyunUtil, $log, dialog) {

  $scope.$watch("totalPage", function() {
    refresh();
  });

  $scope.$watch("pageIndex", function() {
    refresh();
  });

  $scope.onFirstButtonClicked = function() {
    var pageIndex = Number($scope.pageIndex);
    if(pageIndex != 0) {
      $scope.pageIndex = 0;
      $scope.$emit("tableIndexChanged", $scope.pageIndex);
    } else {
      dialog.showInfo("已经是到第一页了！");
    }
  };

  $scope.onLastButtonClicked = function() {
    var pageIndex = Number($scope.pageIndex);
    var totalPage = Number($scope.totalPage);
    if(pageIndex + 1 !== totalPage) {
      $scope.pageIndex = totalPage - 1;
      $scope.$emit("tableIndexChanged", $scope.pageIndex);
    } else {
      dialog.showInfo("已经是最后一页了！");
    }
  };
  
  $scope.disableLast = function(){
    var pageIndex = Number($scope.pageIndex);
    var totalPage = Number($scope.totalPage);
    return totalPage === 0 || pageIndex + 1 === totalPage;
  };

  $scope.onPreviousButtonClicked = function() {
    var pageIndex = Number($scope.pageIndex);
    if(pageIndex >= 1) {
      $scope.pageIndex = pageIndex - 1;
      $scope.$emit("tableIndexChanged", $scope.pageIndex);
    } else {
      dialog.showInfo("已经是到第一页了！");
    }
  };

  $scope.onNextButtonClicked = function() {
    var totalPage = Number($scope.totalPage);
    var pageIndex = Number($scope.pageIndex);
    if(pageIndex + 1 < totalPage) {
      $scope.pageIndex = pageIndex + 1;
      $scope.$emit("tableIndexChanged", $scope.pageIndex);
    } else {
      dialog.showInfo("已经是最后一页了！");
    }
  };

  $scope.onIndexButtonClicked = function(index) {
    var pageIndex = Number($scope.pageIndex);
    if(pageIndex !== index) {
      $scope.pageIndex = index;
      $scope.$emit("tableIndexChanged", $scope.pageIndex);
    }
  };

  function refresh() {
    if(Number($scope.totalPage) === NaN || Number($scope.pageIndex) === NaN) {
      return;
    }

    var totalPage = 0;
    var pageIndex = 0;

    if($scope.totalPage && huoyunUtil.asNumber($scope.totalPage)) {
      totalPage = Number($scope.totalPage);
    }

    if($scope.pageIndex && huoyunUtil.asNumber($scope.pageIndex)) {
      pageIndex = Number($scope.pageIndex);
    }

    if(pageIndex > totalPage) {
      $log.warn("Current page index " + pageIndex + " is large than total page count " + totalPage);
    } else {
      //$scope.showPrevious = pageIndex >= 5;
      //$scope.showNext = true;
      var start = parseInt(pageIndex / 5) * 5;
      var end = (parseInt(pageIndex / 5) + 1) * 5;
      if(end > totalPage) {
        end = totalPage;
        //$scope.showNext = false;
      }
      $scope.items = [];
      for(var index = start; index < end; index++) {
        $scope.items.push({
          index: index + 1,
          current: index === pageIndex
        });
      }

      //    $scope.showPrevious = pageIndex !== 0;
      //    $scope.showNext = (pageIndex + 1) !== end;
      //    if(totalPage <= 5) {
      //      $scope.showFirst = false;
      //      $scope.showNext = false;
      //    } else {
      //      $scope.showFirst = pageIndex !== 0;
      //      $scope.showLast = (pageIndex + 1) !== end;
      //    }
    }
  }
}]);