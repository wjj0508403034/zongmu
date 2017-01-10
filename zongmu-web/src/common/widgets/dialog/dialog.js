'use strict';

angular.module('huoyun-ui').config(["ngDialogProvider", function(ngDialogProvider) {
  ngDialogProvider.setDefaults({
    className: 'ngdialog-theme-default huoyun-dialog',
    showClose: false,
    closeByDocument: false,
    closeByEscape: false
  });
}]);

angular.module('huoyun-ui').factory("dialog", ['$q', "ngDialog", "$log", function($q, ngDialog, $log) {

  function openDialog(templateUrl, options) {
    var dtd = $q.defer();
    ngDialog.open({
      template: templateUrl,
      controller: "dialogController",
      data: options
    }).closePromise.then(function(data) {
      $log.debug("Closed dialog " + data.id);
      if (angular.isFunction(options.confirm)) {
        dtd.resolve(options.confirm.call(this));
      }else{
        dtd.resolve();
      }
    });
    return dtd.promise;
  };

  return {

    showInfo: function(message, callback) {
      return openDialog("widgets/dialog/show.html", {
        "level": "info",
        "title": "信息",
        "message": message,
        "confirm": callback
      });
    },

    showWarn: function(message, callback) {
      openDialog("widgets/dialog/show.html", {
        "level": "warn",
        "title": "提醒",
        "message": message,
        "confirm": callback
      });
    },

    showError: function(message, callback) {
      openDialog("widgets/dialog/show.html", {
        "level": "error",
        "title": "错误",
        "message": message,
        "confirm": callback
      });
    },

    /*
     * options:
     * --title
     * --message
     * --onConfirm
     * --onCancel
     */
    showConfirm: function(options) {
      ngDialog.open({
        template: "widgets/dialog/confirm.html",
        controller: "dialogController",
        data: options
      }).closePromise.then(function(data) {
        $log.debug("Closed dialog " + data.id);
        if (data.value === "ok" && angular.isFunction(options.onConfirm)) {
          options.onConfirm.call(this,[data.value]);
          return;
        }

        if (data.value === "cancel" && angular.isFunction(options.onCancel)) {
          options.onCancel.call(this);
        }
      });
    },

    showCustom: function(options) {
      ngDialog.open({
        template: options.templateUrl,
        controller: options.controller,
        appendClassName: options.appendClassName,
        data: options.params
      }).closePromise.then(function(data) {
        $log.debug("Closed dialog " + data.id);
        if (data.value.key === "ok" && angular.isFunction(options.onConfirm)) {
          options.onConfirm.apply(this, [data.value.result]);
          return;
        }

        if (data.value.key === "cancel" && angular.isFunction(options.onCancel)) {
          options.onCancel.apply(this, data.value.result);
        }
      });
    }

  };
}]);

angular.module('huoyun-ui').controller("dialogController", ["$scope", "ngDialog", function($scope, ngDialog) {

}]);