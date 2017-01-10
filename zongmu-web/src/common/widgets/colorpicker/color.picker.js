'use strict';

angular.module('huoyun-ui').directive("widgetsColorPicker", ["$log", function($log) {
  return {
    restrict: "A",
    templateUrl: "widgets/colorpicker/color.picker.html",
    replace: true,
    scope: {
      ngModel: "="
    },
    controller: "colorPickerController",
    link: function($scope, elem, attrs) {
      var button = elem.find(".color-button");
      button.colorpicker({
        hideButton: true
      });

      button.on("change.color", function(event, color) {
        $log.debug("selected color is:" + color);
        $scope.ngModel = color;
        $scope.$apply();
      });
      
      $scope.$watch("ngModel",function(){
        button.css("background-color", $scope.ngModel);
      });
    }
  };
}]);

angular.module('huoyun-ui').controller("colorPickerController", ["$scope", function($scope) {

}]);