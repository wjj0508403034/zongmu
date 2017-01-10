'use strict';

angular.module('huoyun-ui').directive("widgetsSvgDraw", ["huoyunUtil", "$log", function(huoyunUtil, $log) {
  return {
    restrict: "A",
    replace: true,
    scope: {
      "type": "@",
      "obj": "=",
    },
    controller: "svgDrawController",
    link: function($scope, elem, attrs) {
      var storyboard = null;
      var shapeObj = null;
      var huoyun = angular.module('huoyun-ui');

      if (svgCheck()) {
        initStoryboard();
        initData();
      }

      function svgCheck() {
        if (SVG) {
          if (SVG.supported) {
            return true;
          } else {
            $log.warn("Your brower isn't support SVG.");
          }
        } else {
          $log.error("You lost svg.js!!!");
        }

        return false;
      }

      function initData() {
        angular.forEach($scope.obj.getShapeObjs(), function(shapeObj, index) {
          shapeObj.setStoryBoard(storyboard);
          shapeObj.setScope($scope);
          shapeObj.setLogger($log);
          shapeObj.draw();
        });
      }

      $scope.$watch("obj.readonly", function(newValue) {
        if (newValue) {
          $scope.obj.disableDrawing();
        } else {
          $scope.obj.enableDrawing();
        }
      });

      function onDrawingEndCallback(obj) {
        if (obj) {
          $scope.obj.addShapeObj(obj);
          $scope.$apply();
        }
        shapeObj = null;
      }

      /*
       * 初始化画板和画笔
       */
      function initStoryboard() {
        var svgId = randomSvgId();
        var drawPaint = angular.element("<div class='svg-draw-paint'></div>").attr("id", svgId);
        drawPaint.css("height", "100%").css("width", "100%");
        elem.append(drawPaint);
        storyboard = SVG(svgId);
        storyboard.size("100%", "100%")

        storyboard.mousedown(function(event) {
          if (!$scope.obj.readonly) {
            var point = huoyun.newObject("point", [event.offsetX, event.offsetY]);
            if (shapeObj === null) {
              shapeObj = huoyun.newObject("shapeObj");
              shapeObj.type = $scope.type;
              shapeObj.setStoryBoard(storyboard);
              shapeObj.setStartPoint(point);
              shapeObj.setDrawingEndCallback(onDrawingEndCallback);
            } else {
              shapeObj.setEndPoint(point);
            }
          }
        });

        storyboard.mousemove(function(event) {
          if (!$scope.obj.readonly) {
            if (shapeObj && shapeObj.points.length > 0) {
              var point = huoyun.newObject("point", [event.offsetX, event.offsetY]);
              shapeObj.moveTo(point);
            }
          }
        });
      }

      /*
       * 生成随机SVG的ID
       */
      function randomSvgId() {
        var random = parseInt(Math.random() * 100000);
        return "svg" + random;
      }
    }
  };
}]);

angular.module('huoyun-ui').controller("svgDrawController", ["$scope", "$log", function($scope, $log) {

  if (!$scope.type) {
    $scope.type = "rect";
  }

  if (!$scope.obj) {
    $scope.obj = angular.module('huoyun-ui').newObject("svgDrawObj");
  }
  $scope.obj.setScope($scope);
  $scope.obj.setLogger($log);

}]);

angular.module('huoyun-ui').definedObject("point", function(x, y) {
  var xValue = x;
  var yValue = y;
  return {
    x: xValue,
    y: yValue,
    getValue: function() {
      return [this.x, this.y];
    }
  }
});

angular.module('huoyun-ui').definedObject("svgDrawObj", function() {
  var $scope = null;
  var $log = null;

  function onSelectedChanged(selectedShapeObj) {
    angular.forEach(obj.shapeObjs, function(shapeObj, index) {
      if (!selectedShapeObj.equal(shapeObj)) {
        shapeObj.unselect();
      }
    });
  }

  function selectedShapeObjIndex() {
    angular.forEach(obj.shapeObjs, function(shapeObj, index) {
      if (shapeObj.isSelected()) {
        return index;
      }
    });
  }

  function unselectAll() {
    angular.forEach(obj.shapeObjs, function(shapeObj, index) {
      shapeObj.unselect();
    });
  }

  function initShapes(data) {
    var huoyun = angular.module('huoyun-ui');
    angular.forEach(data || [], function(item, index) {
      var shapeObj = huoyun.newObject("shapeObj");
      shapeObj.type = item.type;
      shapeObj.color = item.color;
      angular.forEach(item.points, function(point, pointIndex) {
        shapeObj.points.push(huoyun.newObject("point", [point.x, point.y]));
      });
      obj.addShapeObj(shapeObj);
    });
  }

  var obj = {
    type: "rect",
    readonly: true,
    shapeObjs: [],
    setScope: function(val) {
      $scope = val;
    },
    setLogger: function(val) {
      $log = val;
    },
    addShapeObj: function(shapeObj) {
      if (shapeObj) {
        this.shapeObjs.push(shapeObj);
        shapeObj.setSelectedCallback(onSelectedChanged);
      }
    },
    getSelectedShapeObj: function() {
      var selectedShapeObj = null;
      angular.forEach(this.shapeObjs, function(shapeObj, index) {
        if (shapeObj.isSelected()) {
          selectedShapeObj = shapeObj;
        }
      });

      return selectedShapeObj;
    },
    removeSelected: function() {
      var selectedIndex = null;
      var selectedShapeObj = null;
      angular.forEach(shapeObjs, function(shapeObj, index) {
        if (shapeObj.isSelected()) {
          selectedIndex = index;
          selectedShapeObj = shapeObj;
        }
      });
      shapeObjs.splice(selectedIndex, 1);
      selectedShapeObj.remove();
      unselectAll();
    },
    disableDrawing: function() {
      this.readonly = true;
      angular.forEach(this.shapeObjs, function(shapeObj, index) {
        shapeObj.enableSelected();
      });
    },
    enableDrawing: function() {
      this.readonly = false;
      angular.forEach(this.shapeObjs, function(shapeObj, index) {
        shapeObj.disableSelected();
      });
      unselectAll();
    },
    setData: function(data) {
      initShapes(data);
    },
    getShapeObjs: function() {
      return this.shapeObjs;
    }
  };

  return obj;
});

angular.module('huoyun-ui').definedObject("shapeObj", function(storyboard, type) {
  var $scope = null;
  var $log = null;
  var storyboard = storyboard;
  var type = type;
  var color = "#000000";
  var selected = false;
  var onSelectedCallback = null;
  var onDrawingEndCallback = null;
  var polyline = null;
  storyboard && init();

  function init() {
    polyline = storyboard.polyline()
    polyline.fill('none');
    polyline.stroke({
      width: 5
    });
  }

  function draw() {
    if (polyline) {
      polyline.stroke(obj.color);
      if (obj.type === "rect") {
        if (obj.points.length === 2) {
          drawingRect(obj.points[0], obj.points[1]);
        } else {
          $log && $log.warn("Draw shape failed, because the points is invalid.")
        }
      } else {
        if (obj.points.length > 2) {
          drawingLine(obj.points[0]);
        } else {
          $log && $log.warn("Draw shape failed, because the points is invalid.")
        }
      }
    } else {
      $log && $log.warn("Draw shape failed, because the polyline is null.")
    }

  }

  function drawingRect(startPoint, endPoint) {
    var points = [];
    points.push([startPoint.x, startPoint.y]);
    points.push([startPoint.x, endPoint.y]);
    points.push([endPoint.x, endPoint.y]);
    points.push([endPoint.x, startPoint.y]);
    points.push([startPoint.x, startPoint.y]);
    polyline.plot(points);
  }

  function endDrawingRect(point) {
    obj.points.push(point);
    onDrawingEndCallback && onDrawingEndCallback.apply(this, [obj]);
    $scope && $scope.$emit("onDrawingEnd", obj);
  }

  function drawingLine(point) {
    polyline.plot(parsePointArray(obj.points.concat(point)));
  }

  function startOrEndLine(point) {
    if (obj.points.length > 1) {

      // 如果与开始点重合，则结束画图
      if (obj.points.length > 2) {
        if (isOverlap(obj.points[0], point)) {
          onDrawingEndCallback && onDrawingEndCallback.apply(this, [obj]);
          $scope && $scope.$emit("onDrawingEnd", obj);
          return;
        }
      }
      // 如果鼠标点击的点与最后一个点重合，则这个点抛弃
      if (isOverlap(obj.points[obj.points.length - 1], point)) {
        return;
      }
    }

    obj.points.push(point);
  }

  function parsePointArray(points) {
    return points.map(function(it) {
      return it.getValue();
    });
  }

  /*
   * 判断两点是否点重合，这里重合范围在正负5像素
   */
  function isOverlap(point1, point2) {
    return Math.abs(point1.x - point2.x) < 5 && Math.abs(point1.y - point2.y) < 5;
  }

  function onSelected() {
    if (!selected) {
      selected = true;
      polyline && polyline.fill("red");
      $scope && $scope.$emit("onSelectedChange", obj);
      onSelectedCallback && onSelectedCallback.apply(this, [obj]);
    }
  }

  function onUnSelected() {
    if (selected) {
      selected = false;
      polyline && polyline.fill("none");
      $scope && $scope.$emit("onSelectedChange", null);
    }
  }

  var obj = {
    type: type,
    color: color,
    points: [],
    getId: function() {
      return polyline && polyline.attr("id");
    },
    select: function() {
      onSelected();
    },
    unselect: function() {
      onUnSelected();
    },
    isSelected: function() {
      return selected;
    },
    enableSelected: function() {
      if (polyline) {
        polyline.on("click", onSelected);
      } else {
        $log && $log.warn("enable select failed, because polyline is null.")
      }
    },
    disableSelected: function() {
      if (polyline) {
        polyline.off("click");
      } else {
        $log && $log.warn("unenable unselect failed, because polyline is null.")
      }
    },
    setStoryBoard: function(val) {
      storyboard = val;
      init();
    },
    setScope: function(val) {
      $scope = val;
    },
    setLogger: function(val) {
      $log = val;
    },
    setColor: function(val) {
      this.color = val;
      if (polyline) {
        polyline.stroke(val);
      } else {
        $log && $log.warn("set color failed, because polyline is null.")
      }
    },
    remove: function() {
      polyline && polyline.remove();
    },
    setStartPoint: function(point) {
      if (this.type === "rect") {
        this.points.push(point);
      } else {
        startOrEndLine(point);
      }
    },
    moveTo: function(point) {
      if (this.type === "rect") {
        drawingRect(this.points[0], point);
      } else {
        drawingLine(point);
      }
    },
    setEndPoint: function(point) {
      if (this.type === "rect") {
        endDrawingRect(point);
      } else {
        startOrEndLine(point);
      }
    },
    equal: function(shapeObj) {
      return this.getId() === shapeObj.getId();
    },
    setSelectedCallback: function(callback) {
      if (typeof callback === "function") {
        onSelectedCallback = callback;
      } else {
        $log && $log.warn("set selectedCallback failed, because the set value is not function.");
      }
    },
    setDrawingEndCallback: function(callback) {
      if (typeof callback === "function") {
        onDrawingEndCallback = callback;
      } else {
        $log && $log.warn("set drawingEndCallback failed, because the set value is not function.");
      }
    },
    draw: function() {
      draw();
    }
  };

  return obj;
});