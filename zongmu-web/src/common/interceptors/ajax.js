'use strict';

angular.module('huoyun-ui').factory("ajaxInterceptor", ["$q", "$log", "httpError", function($q, $log, httpError) {
  return {
    "requestError": function(rejection) {
      $log.warn("http request has error ...");
      $log.warn(rejection);
      var handleFunc = httpError.getHandle();
      if(angular.isFunction(handleFunc)) {
        handleFunc(rejection);
      } else {
        return $q.reject(rejection);
      }
    },

    "responseError": function(rejection) {
      $log.warn("http response has error ...");
      $log.warn(rejection);
      var handleFunc = httpError.getHandle();
      if(angular.isFunction(handleFunc)) {
        handleFunc(rejection);
        return $q.reject(rejection);
      } else {
        return $q.reject(rejection);
      }
    }
  };
}]);

angular.module('huoyun-ui').provider("httpError", function() {
  var handleFunc = null;
  var $dialog = null;

  this.setHandle = function(handle) {
    handleFunc = handle;
  };

  this.getHandle = function() {
    return handleFunc;
  };
  
  this.setDialog = function(dialog){
    $dialog = dialog;
  };
  
  this.getDialog = function(){
    return $dialog;
  }

  this.$get = function() {
    return this;
  };
});