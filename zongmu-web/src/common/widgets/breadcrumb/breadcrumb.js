'use strict';

angular.module('huoyun-ui').directive("widgetsBreadCrumb", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/breadcrumb/breadcrumb.html",
    replace: true,
    scope: {
      items: "="
    },
    controller: "breadCrumbController",
    link: function($scope, elem, attrs) {}
  };
});

angular.module('huoyun-ui').controller("breadCrumbController", ["$scope","breadCrumb", function($scope,breadCrumbProvider) {
   $scope.items = breadCrumbProvider.getHistories();
   
   breadCrumbProvider.setUpdateCallback(onUpdateChanged);
   
   function onUpdateChanged(){
      $scope.items = breadCrumbProvider.getHistories();
   }
}]);


angular.module('huoyun-ui').provider("breadCrumb", function(){
  
  var histories = [];
  var callback = null;
  
  this.setUpdateCallback = function(callbackFn){
    callback = callbackFn;
  };
  
  this.setHistories = function(items){
    histories = items;
    if(callback){
      callback.apply(this,[]);
    }
  };
  
  this.getHistories = function(){
    return histories;
  };
  
  this.push = function(item){
    histories.push(item)
  };
  
  this.$get = function(){
    return this;
  };
  
});