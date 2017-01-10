'use strict';

angular.module('huoyun-ui')
  .directive('widgetsMarkdown', function() {
    return {
      restrict: 'A',
      scope: {
        content: "@"
      },
      link: function($scope, element, attrs) {
        if(showdown) {
          var converter = new showdown.Converter();
          $scope.$watch("content", function() {
            if($scope.content) {
              var htmlContent = converter.makeHtml($scope.content);
              element.html(htmlContent);
            }
          });

        }
      }
    };
  });