'use strict';

angular.module('huoyun-ui').directive("widgetsSvgStoryBoard", ["huoyunUtil", "$log", function(huoyunUtil, $log) {
  return {
    restrict: "A",
    replace: true,
    scope: {
      "shapes": "=",
      "frameIndex": "=",
      "taskItemFile": "="
    },
    controller: "svgStoryBoardController",
    link: function($scope, elem, attrs) {
      var huoyun = angular.module('huoyun-ui');
      var storyboard = null;
      svgCheck() && init();

      $scope.$watchCollection("shapes", function() {
        //elem.find("svg").empty();
        if(storyboard) {
          storyboard.clear();
          angular.forEach($scope.shapes, function(shape, index) {
            if(!shape.getStoryBoard()) {
              shape.setStoryBoard(storyboard);
            }
            if(!shape.getScope()) {
              shape.setScope($scope.$new())
            }

            if(!shape.getLogger()) {
              shape.setLogger($log);
            }

            shape.init();
          });
        }
      });

      $scope.$watch("frameIndex", function() {
        if(storyboard) {
          storyboard.clear();
          angular.forEach($scope.shapes, function(shape, index) {
            if(!shape.getStoryBoard()) {
              shape.setStoryBoard(storyboard);
            }
            if(!shape.getScope()) {
              shape.setScope($scope.$new())
            }

            if(!shape.getLogger()) {
              shape.setLogger($log);
            }

            shape.init();
          });
        }
      });

      function svgCheck() {
        if(SVG) {
          if(SVG.supported) {
            return true;
          } else {
            $log.warn("Your brower isn't support SVG.");
          }
        } else {
          $log.error("You lost svg.js!!!");
        }

        return false;
      }

      function init() {
        var svgId = huoyunUtil.generateNo();
        var drawPaint = angular.element("<div class='svg-draw-paint'></div>").attr("id", svgId);
        drawPaint.css("height", "100%").css("width", "100%");
        elem.append(drawPaint);
        storyboard = SVG(svgId);
        storyboard.size("100%", "100%");

        storyboard.mousedown(function(event) {
          var shape = getSelectedShape();
          if(shape && shape.canDraw()) {
            var point = huoyun.newObject("point", [event.offsetX, event.offsetY]);
            shape.push(point);
            if(!shape.taskItemFile) {
              shape.taskItemFile = $scope.taskItemFile;
            }
          } else {
            hitSelectedShape(event.offsetX, event.offsetY);
          }
        });

        storyboard.mousemove(function(event) {
          var shape = getSelectedShape();
          if(shape && shape.canDraw() && shape.points.length > 0) {
            var point = huoyun.newObject("point", [event.offsetX, event.offsetY]);
            shape.moveTo(point);
          }
        });

        $(document).on("keydown", function(event) {
          if(event.keyCode === 46) {
            var shape = getSelectedShape();
            if(shape && !shape.canDraw()) {
              shape.remove();
            }
          }
        });
      }

      function hitSelectedShape(x, y) {
        var hitShapes = [];

        angular.forEach($scope.shapes || [], function(shape, index) {
          if(shape.inside(x, y)) {
            hitShapes.push(shape);
          }
        });

        //当框被其他框包含时选择较小的框。
        if(hitShapes.length > 0) {
          var smallShape = hitShapes[0];
          var box = smallShape.getPolyline().rbox();
          for(var index = 1; index < hitShapes.length; index++) {
            var tempBox = hitShapes[index].getPolyline().rbox();
            if(box.height >= tempBox.height && box.width >= tempBox.width) {
              smallShape = hitShapes[index];
              box = tempBox;
            }
          }

          smallShape.select(true);
          $scope.$apply();
        }
      }

      function getSelectedShape() {
        var find = ($scope.shapes || []).filter(function(shape) {
          return shape.isSelected();
        });

        if(find.length > 0) {
          return find[0];
        }

        return null;
      }

    }
  };
}]);

angular.module('huoyun-ui').controller("svgStoryBoardController", ["$scope", "$log", function($scope, $log) {

}]);

angular.module('huoyun-ui').definedObject("Shape", function() {
  var huoyun = angular.module('huoyun-ui');
  var polyline = null;
  var storyboard = null;
  var isSelected = false;
  var $scope = null;
  var backgroundColor = "rgba(109, 33, 33, 0.25)";
  var onSelectedCallback = null;
  var onUnSelectedCallback = null;
  var onDrawEndCallback = null;
  var $timeline = huoyun.newObject("Timeline");
  var $logger = null;

  function init() {
    obj.readonly = false;
    polyline = storyboard.polyline()
    polyline.fill('none');
    polyline.stroke({
      width: 2,
      color: obj.color || "black"
    });

    if(obj.type !== "RECT") {
      polyline.marker('start', 5, 5, function(add) {
        add.circle(5).fill('red');
      });
    }

    initShape();
  }

  function initShape() {
    $logger && $logger.debug("start init shape ...");
    if(obj.points && obj.points.length > 0) {
      obj.readonly = true;
      $logger && $logger.debug("init shape ->start draw polyline ...");
      if(obj.type && obj.type === "RECT" && obj.points.length > 1) {
        drawingRect(obj.points[0], obj.points[1]);
      } else {
        polyline.plot(toArray(obj.points.concat(obj.points[0])));
      }
      $logger && $logger.debug("init shape ->start draw polyline end.");
      //attachEvents();
    }
    $logger && $logger.debug("end init shape .");
  }

  function initScope() {
    $scope.obj = obj;
    $scope.$watch("obj.color", function() {
      if(!obj.canDraw() && polyline) {
        /*
         * 这个地方会抛异常，但不影响使用
         */
        try {
          polyline.stroke(obj.color);
        } catch(e) {
          $logger && $logger.debug(e);
        }
      }
    });
  }

  function drawing(point) {
    if(obj.type && obj.type === "RECT") {
      if(obj.points.length === 1) {
        drawingRect(obj.points[0], point);
      } else if(obj.points.length > 1) {
        drawingRect(obj.points[0], obj.points[1]);
        endDrawing();
      }
    } else if(obj.type && obj.type === "POLYLINE") {
      polyline.plot(toArray(obj.points.concat(point)));
      if(obj.points.length >= obj.count) {
        polyline.plot(toArray(obj.points.concat(point, obj.points[0])));
        endDrawing();
      }
    } else {
      polyline.plot(toArray(obj.points.concat(point)));
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

  function endDrawing() {
    obj.readonly = true;
    polyline.fill(backgroundColor);
    //attachEvents();
    onDrawEndCallback && onDrawEndCallback.apply(this, [obj]);
    $scope && $scope.$emit("onShapeDrawEnd", obj);
  }

  function onSelected(needCallback) {
    isSelected = true;
    if(!obj.canDraw() && polyline) {
      polyline.fill(backgroundColor);
    }

    if(needCallback && onSelectedCallback) {
      onSelectedCallback.apply(this, [obj]);
    }
  }

  function onUnSelected(needCallback) {
    isSelected = false;
    if(polyline) {
      polyline.fill("none");
    }

    if(needCallback && onUnSelectedCallback) {
      onUnSelectedCallback.apply(this, [obj]);
    }
  }

  function attachEvents() {
    polyline.on("click", function() {
      if(obj.isSelected()) {
        onUnSelected();
        //      obj.unselect();
        //      if (onUnSelectedCallback) {
        //        onUnSelectedCallback.apply(this, [obj]);
        //      }
      } else {
        onSelected();
        //      obj.select();
        //      if (onSelectedCallback) {
        //        onSelectedCallback.apply(this, [obj]);
        //      }
      }
    });
  }

  function isClosed(point1, point2) {
    return Math.abs(point1.x - point2.x) < 5 && Math.abs(point1.y - point2.y) < 5;
  }

  function save(point) {
    if(obj.type && obj.type === "ANY" && obj.points.length > 2) {
      // 如果该点的位置与第一个点的位置差在5个像素内，则认为该图像闭合
      if(isClosed(obj.points[0], point)) {
        polyline.plot(toArray(obj.points.concat(obj.points[0])));
        endDrawing();
      } else {
        obj.points.push(point);
      }

    } else {
      obj.points.push(point);
    }
  }

  function toArray(points) {
    return points.map(function(it) {
      return it.getValue();
    });
  }

  var obj = {
    id: null,
    name: null,
    color: "black",
    points: [],
    count: 5,
    type: "RECT",
    readonly: true,
    startIndex: null,
    endIndex: null,
    tagDorpdownList: {},
    tagCheckBoxList: {},
    tagsData: [],
    setDefaultTags: function() {

      this.tagsData.forEach(function(tag) {
        if(tag.type === "DROPDOWNBOX") {
          var defaultItems = tag.items.filter(function(item) {
            return item.default;
          });

          if(defaultItems.length > 0) {
            this.tagDorpdownList[defaultItems[0].tagId] = defaultItems[0];
          }
        } else {
          tag.items.forEach(function(item) {
            if(item.default) {
              this.tagCheckBoxList[item.id] = true;
            }
          }.bind(this));
        }
      }.bind(this));
    },
    getPolyline: function() {
      return polyline;
    },
    changeColor: function(val) {
      obj.color = val;
      polyline.stroke(val);
    },
    init: function() {
      init();
    },
    tags: {

    },
    frameIndexes: [],
    startTime: null,
    endTime: null,
    $$timelines: [],
    $$timeline: $timeline,
    isSelected: function() {
      return isSelected;
    },

    setSelected: function() {
      isSelected = true;
    },
    setUnSelected: function() {
      isSelected = false;
    },

    select: function(needCallback) {
      //    isSelected = true;
      //    if (!this.canDraw() && polyline) {
      //      polyline.fill(backgroundColor);
      //    }
      onSelected(needCallback);
    },

    unselect: function(needCallback) {
      onUnSelected(needCallback);
      //    isSelected = false;
      //    polyline.fill("none");
    },

    inside: function(x, y) {
      return polyline.inside(x, y);
    },

    push: function(point) {
      save(point);
    },

    moveTo: function(point) {
      drawing(point);
    },
    setStoryBoard: function(val) {
      storyboard = val;
    },

    getStoryBoard: function() {
      return storyboard;
    },

    canDraw: function() {
      if( this.getStoryBoard() !== null && !this.readonly ){
        return true;
      }

      return false;
    },

    setScope: function(val) {
      $scope = val;
      initScope();
    },

    getScope: function() {
      return $scope;
    },

    setLogger: function(logger) {
      $logger = logger;
      this.$$timeline && this.$$timeline.setLogger(logger);
    },

    getLogger: function() {
      return $logger;
    },

    remove: function() {

      //    if (polyline) {
      //      var marker = polyline.reference("marker-start");
      //      if (marker) {
      //        marker.remove();
      //      }
      //      polyline.remove();
      //    }

      this.destory();

      this.points = [];
      init();
    },

    destory: function() {
      // 移除起始点的marker
      if(polyline) {
        if(this.type !== "RECT") {
          var marker = polyline.reference("marker-start");
          if(marker) {
            marker.remove();
          }
        }

        polyline.remove();
      }

    },

    setSelectCallback: function(callback) {
      onSelectedCallback = callback;
    },

    getSelectCallback: function() {
      return onSelectedCallback;
    },

    setUnSelectCallback: function(callback) {
      onUnSelectedCallback = callback;
    },

    getUnSelectCallback: function() {
      return onUnSelectedCallback;
    },

    setDrawEndCallback: function(callback) {
      onDrawEndCallback = callback;
    },
    draw: function() {
      initShape();
    }
  };

  return obj;
});

angular.module('huoyun-ui').definedObject("Timeline", function() {
  var $logger = null;
  var huoyun = angular.module("huoyun-ui");

  function calcNewPoint(startPoint, endPoint, rate) {
    var deltaX = endPoint.x - startPoint.x;
    var deltaY = endPoint.y - startPoint.y;
    var newPointX = startPoint.x + rate * deltaX;
    var newPointY = startPoint.y + rate * deltaY;
    return huoyun.newObject("point", [newPointX, newPointY]);
  }

  /*
   * 差值计算当前的图形，
   * 如果结束点没有设置，则一直保持开始点图形
   */
  function calcRealTimePoints(index) {
    var startData = getStartData(index);
    if(startData.isEnd) {
      return [];
    }

    var endData = getEndData(index);
    if(startData && !endData) {
      return angular.copy(startData.points);
    }

    var points = [];
    if(startData && endData) {
      /*
       * 开始点与结束点重合，则不进行差值运算
       */
      if(startData.index === endData.index) {
        return angular.copy(startData.points);
      }

      var totalIndexes = endData.index - startData.index;
      var deltaIndexes = index - startData.index;
      var rate = deltaIndexes / totalIndexes;
      for(var pIndex = 0; pIndex < startData.points.length; pIndex++) {
        points.push(calcNewPoint(startData.points[pIndex], endData.points[pIndex], rate));
      }
    }

    return points;
  }

  function getStartData(index) {
    var points = obj.data[index];
    if(points) {
      return {
        index: index,
        points: points,
        isEnd: points.isEnd
      };
    }

    var lastIndex = null;
    var keys = Object.keys(obj.data);
    console.log(keys);
    for(var i = 0; i < keys.length; i++) {
      if(+keys[i] > index) {
        break;
      } else {
        lastIndex = +keys[i];;
      }
    }

    if(lastIndex !== null) {
      console.log("start index: " + lastIndex);
      return {
        index: lastIndex,
        points: obj.data[lastIndex],
        isEnd: obj.data[lastIndex].isEnd
      };
    } else {
      $logger && $logger.warn("get start points failed");
    }
  }

  function getEndData(index) {
    /*
     * 当前帧数大于已记录，则一直画最后一个图形
     */
    if(index > obj.data.length) {
      return null;
    }

    var points = obj.data[index];
    if(points) {
      return {
        index: index,
        points: points
      };
    }

    var currentIndex = null;
    var keys = Object.keys(obj.data);
    console.log(keys);
    for(var i = 0; i < keys.length; i++) {
      if(+keys[i] >= index) {
        currentIndex = +keys[i];
        break;
      }
    }

    if(currentIndex !== null) {
      console.log("end index: " + currentIndex);
      return {
        index: currentIndex,
        points: obj.data[currentIndex]
      };
    } else {
      $logger && $logger.warn("get end points failed");
    }
  }

  function setTimelineStart(frameIndex, shape) {
    obj.startIndex = frameIndex;
    obj.data = [];
    //obj.data[frameIndex] = angular.copy(shape.points);
  }

  function setTimelineEnd(frameIndex) {
    if(frameIndex > obj.startIndex) {
//    if(obj.endIndex != undefined && obj.endIndex != null) {
//      if(frameIndex > obj.endIndex) {
//        obj.endIndex = frameIndex;
//      }
//    } else {
//      obj.endIndex = frameIndex;
//    }
      obj.endIndex = frameIndex;

      var needBeDeletedKeys = [];
      for(var key in obj.data) {
        if(+key > frameIndex) {
          needBeDeletedKeys.push(key);
        }
      }

      if(!obj.data[frameIndex]) {
        obj.data[frameIndex] = calcRealTimePoints(frameIndex);
      }
      obj.data[frameIndex].isEnd = true;

      angular.forEach(needBeDeletedKeys, function(key, keyIndex) {
        delete obj.data[key];
      });

    } else {
      $logger && $logger.warn("Set timeline end failed, because the end index is less than start index");
    }
  }

  /*
   * 计算当前帧在不在时间线范围内，如果在的话，就差值画图形，如果不在，则删除该图形
   */
  function frameIndexInRange(index) {
    if(typeof index === "number" && typeof obj.startIndex === "number" && index >= obj.startIndex) {
      if(obj.endIndex) {
        return index <= obj.endIndex;
      }

      return true;
    }

    return false;
  }

  /*
   * [100,200]
   * [230,300]
   * [300]
   */
  function test() {

  }

  var obj = {
    startIndex: null,
    endIndex: null,
    data: [],
    taskItemFileId: null,
    getPoints: function(index) {
      if(this.inRange(index)) {
        return calcRealTimePoints(index);
      } else {
        $logger && $logger.info("Current frame index not in this range");
      }
    },
    inRange: function(index) {
      return frameIndexInRange(index);
    },
    setLogger: function(log) {
      $logger = log;
    },
    setEnd: function(frameIndex) {
      setTimelineEnd(frameIndex);
    },
    setStart: function(frameIndex) {
      setTimelineStart(frameIndex);
    },
    canEnd: function(frameIndex) {
      if(this.startIndex === null || this.startIndex >= frameIndex) {
        return false;
      }

      return true;
    }
  };

  return obj;
});