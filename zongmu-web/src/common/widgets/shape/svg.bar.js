'use strict';

angular.module('huoyun-ui').directive("widgetsSvgBar", ["huoyunUtil", "$log", "dialog", function(huoyunUtil, $log, dialog) {
  return {
    restrict: "A",
    replace: true,
    templateUrl: "widgets/shape/svg.bar.html",
    scope: {
      tags: "=",
      shapes: "=",
      type: "=",
      disabled: "=",
      sideCount: "=",
      frameIndex: "=",
      shapesBox: "=",
      current: "=",
      taskItemFile: "=",
      onSelectedShapeChanged: "&",
      names: "=",
      nameSelected: "=",
      onShapeObjectCopyed: "&",
      isSingle: "@"
    },
    controller: "svgBarController",
    link: function($scope, elem, attrs) {
      var huoyun = angular.module('huoyun-ui');

      $scope.$watchCollection("shapes", function() {
        angular.forEach($scope.shapes, function(shape, index) {
          setSelectCallback(shape);
        });
      });

      function setSelectCallback(shape) {
        if(!shape.getSelectCallback()) {
          shape.setSelectCallback(onSelectCallback);
        }

        if(!shape.getUnSelectCallback()) {
          shape.setUnSelectCallback(onUnSelectCallback);
        }
      }

      $scope.isShapeHide = function(shape) {
        if(shape) {
          var isHide = !shape.$$timeline.inRange($scope.frameIndex);
          console.log("@@@@" + isHide);
          if(isHide){
            shape.unselect();
          }
          return isHide;
        }
        return true;
      };

      $scope.getShapeNames = function(shape) {
        var res = [];
        var shapeNames = [];
        $scope.shapes.forEach(function(xShape) {
          if(xShape.shapeId !== shape.shapeId) {
            shapeNames.push(xShape.shapeId);
          }
        });
        // 同一摄像头的Id号也要显示
        ($scope.names || []).forEach(function(name) {
          if(shapeNames.indexOf(name) === -1) {
            res.push(name);
          }
          // if(res.indexOf(name) === -1){
          //   res.push(name);
          // }
        });
        return res;
      };

      $scope.$watch("frameIndex", function() {
        if($scope.shapesBox && $scope.frameIndex !== undefined && $scope.frameIndex !== null) {
          var shapes = [];
          angular.forEach($scope.shapesBox, function(shape, index) {
            shape.setDrawEndCallback(onShapeDrawEnd);
            if(shape.$$timeline.inRange($scope.frameIndex)) {
              shape.points = shape.$$timeline.getPoints($scope.frameIndex);
              shapes.push(shape);
            } else {
              shape.points = [];
              shapes.push(shape);
            }
          });
          $scope.shapes = shapes;
          $scope.shapes.forEach(function(shapex) {
            shapex.draw();
          })
        }
      });

      $scope.onShapeSectionSelect = function(index, shape) {
        if(!shape.isSelected()) {
          onSelectChange(index, shape);
        }
      };

      $scope.onNewButtonClick = function() {
        var shape = huoyun.newObject("Shape");
        shape.count = $scope.sideCount || 5;
        shape.type = $scope.type || "RECT";
        shape.tagsData = $scope.tags;
        shape.color = huoyunUtil.randomColor();
        shape.shapeId = huoyunUtil.generateNo();
        shape.setDefaultTags();
        shape.setDrawEndCallback(onShapeDrawEnd);
        $log.info("New shape object:" + shape.shapeId);
        if(!$scope.shapes) {
          $scope.shapes = [];
        }
        $scope.shapes.push(shape);
        shape.$$timeline.setStart($scope.frameIndex);
        if(!$scope.shapesBox) {
          $scope.shapesBox = [];
        }
        $scope.shapesBox.push(shape);
        setSelectCallback(shape);
        shape.select(true);
        $scope.$emit("onShapeCreated", shape);
      };

      if($scope.isSingle === "true") {
        $(document).on("keydown", function(event) {
          console.log(event.keyCode)
          event.stopPropagation();
          switch(event.keyCode) {
            case 65: // A
              $scope.onNewButtonClick();
              $scope.$apply();
              break;
            case 68: // D
              if($scope.current) {
                $scope.onDeleteShapeButtonClick($scope.current);
              }
              break;
            case 70: //F
              if($scope.current) {
                $scope.onStopMarkButtonClick($scope.current);
              }
              break;
            case 69: // E
             if (event.ctrlKey) {
               event.preventDefault();
               $scope.onDeleteButtonClick();
             }
          }
        });
      }

      function onSelectedChangedNotification(shape) {
        $scope.current = shape;
        $scope.onSelectedShapeChanged({
          shape: shape
        });
      }

      function onDeleteShapeObj() {
        $scope.current = null;
        var index = elem.find(".shape-section[active='true']").index();
        if(index >= 0) {
          var deletedShape = $scope.shapes[index];
          deletedShape.destory();
          $scope.shapes.splice(index, 1);
          deleteShapeFromBox(deletedShape);
          $scope.$emit("onShapeDeleted", deletedShape);
        } else {
          $log.warn("Delete failed, because no shape is selected.")
        }
      }

      $scope.onDeleteButtonClick = function() {
        if($scope.current) {
          dialog.showConfirm({
            title: "提示",
            message: "确定要删除该对象么？",
            onConfirm: function() {
              onDeleteShapeObj();
            }
          });
        } else {
          dialog.showInfo("请选择要删除对象！");
        }
      };

      $scope.onShapeCopySelected = function(shapeId) {
        var shape = null;
        if($scope.current && $scope.current.shapeId === shapeId) {
          shape = $scope.current;
        } else {
          var finds = $scope.shapesBox.filter(function(it) {
            return it.shapeId === shapeId;
          });

          if(finds.length > 0) {
            shape = finds[0];
          }
        }

        if(shape) {
          $scope.onShapeObjectCopyed({
            shape: shape
          });
        }
      };

      $scope.onDeleteShapeButtonClick = function(shape) {
        shape.remove();
        delete shape.$$timeline.data[$scope.frameIndex];
      };

      $scope.onStopMarkButtonClick = function(shape) {
        $scope.$emit("onStopMarked", shape);
        shape.$$timeline.setEnd($scope.frameIndex);
      };

      function deleteShapeFromBox(shape) {
        if($scope.shapesBox && $scope.shapesBox.length > 0) {
          var deleteIndex = null;
          angular.forEach($scope.shapesBox, function($shape, $index) {
            if($shape.shapeId === shape.shapeId) {
              deleteIndex = $index;
              return;
            }
          });

          if(deleteIndex !== null) {
            $scope.shapesBox.splice(deleteIndex, 1);
          }
        }
      }

      function onShapeDrawEnd(shape) {
        shape.$$timeline.data[$scope.frameIndex] = angular.copy(shape.points);
        if(shape.$$timeline.endIndex !== null) {
          if($scope.frameIndex > shape.$$timeline.endIndex) {
            shape.$$timeline.endIndex = null;
          }
        }
      }

      function onSelectCallback(shapeObj) {
        onSelectedChangedNotification(shapeObj);
        $scope.shapes.forEach(function(shape, shapeIndex) {
          if(shapeObj.shapeId !== shape.shapeId) {
            shape.unselect();
          }
        });
      }

      function onUnSelectCallback(shape) {

      }

      function onSelectChange(index, shapeObj) {
        $scope.shapes.forEach(function(shape, shapeIndex) {
          if(index !== shapeIndex) {
            shape.unselect();
          } else {
            shape.select();
            onSelectedChangedNotification(shape);
          }
        });
      }

      function indexOf(shape) {
        var index = null;
        angular.forEach($scope.shapes, function(item, itemIndex) {
          if(shape.shapeId === item.shapeId) {
            index = itemIndex;
          }
        });

        return index;
      }
    }
  };
}]);

angular.module('huoyun-ui').controller("svgBarController", ["$scope", function($scope) {

}]);