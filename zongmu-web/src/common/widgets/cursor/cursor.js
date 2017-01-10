'use strict';

angular.module('huoyun-ui').directive("widgetsCursor", ["huoyunUtil", function(huoyunUtil) {
  return {
    restrict: "A",
    templateUrl: "widgets/cursor/cursor.html",
    replace: true,
    scope: {
      "show": "=",
      "classes": "@"
    },
    controller: "cursorController",
    link: function($scope, elem, attrs) {
      $scope.rects = [];

      $scope.$watch("classes", function() {
        $scope.rects = [];
        ($scope.classes || "").split(";").forEach(function(it) {
          if(it) {
            $scope.rects.push(new rect(it));
          }
        });
      });

      $(window).mousemove(function(event) {
        event.stopPropagation();
        var res = false;
        if($scope.show === true) {
          $scope.rects.forEach(function(it) {
            if(it.inRange(event.pageX, event.pageY)) {
              res = true;
            }
          });

          if(res) {
            elem.css("display", "block");
            var top = event.screenY > event.clientY ? event.clientY : event.screenY;
            var left = event.screenX > event.clientX ? event.clientX : event.screenX;
            elem.css("left", left + "px");
            elem.css("top", top + "px");
          } else {
            elem.css("display", "none");
          }
        } else {
          elem.css("display", "none");
        }
      });

      function rect(classes) {
        var xxClass = classes;
        

        return {
          inRange: function(x, y) {
            var elem = $(xxClass);
            if(elem.length === 0) {
              return false;
            }
            var position = elem.offset();
            var width = elem.width();
            var height = elem.height();
            if(x >= position.left && y >= position.top && x <= position.left + width && y <= position.top + height) {
              return true;
            }
            return false;
          }
        };
      }
    }
  };
}]);

angular.module('huoyun-ui').controller("cursorController", ["$scope", function($scope) {

}]);