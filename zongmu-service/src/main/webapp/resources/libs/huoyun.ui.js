'use strict';
var huoyunUI = angular.module('huoyun-ui', ["ngDialog","ngFileUpload","ui.bootstrap.datetimepicker"]);
huoyunUI.objects = {};
huoyunUI.definedObject = function(name , fn){
  if(huoyunUI.objects[name]){
    console.error("Object " + name + " has been defined.");
  }else{
    huoyunUI.objects[name] = fn;
  }
};
huoyunUI.newObject = function(name,args){
  var obj = huoyunUI.objects[name];
  if(obj && typeof obj === "function"){
    return obj.apply(this,args);
  }else{
    console.error("Create new object " + name + " failed.");
  }
}
'use strict';

angular.module('huoyun-ui').directive("huoyunAppendClass", function() {
  return {
    restrict: "A",
    controller: "huoyunAppendClassController",
    link: function($scope, elem, attrs) {
      if (attrs.huoyunAppendClass) {
        elem.addClass(attrs.huoyunAppendClass);
      }
    }
  };
});

angular.module('huoyun-ui').controller("huoyunAppendClassController", ["$scope", function($scope) {

}]);
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
'use strict';

angular.module('huoyun-ui').factory("huoyunUtil", function() {

  return {
    boolParse: function(val) {
      if (typeof val === "boolean") {
        return val;
      }

      return val && val.toLowerCase() === "true";
    },

    /*
     * 如果value=true，则添加class，或者删除class
     */
    toggleClass: function(elem, bool, className) {
      if (bool) {
        elem.addClass(className);
      } else {
        elem.removeClass(className);
      }
    },

    isEmpty: function(val) {
      return !(val && typeof val === "string" && val.trim() !== "");
    },

    isTrue: function(val) {
      if (typeof val === "boolean") {
        return val;
      }

      if (typeof val === "string") {
        return val.toLowerCase() === "true";
      }

      return false;
    },

    pad: function(val, num) {
      var str = '' + val;
      while (str.length < num) {
        str = '0' + str;
      }

      return str;
    },

    generateNo: function() {
      var now = new Date();
      return "" + this.pad(now.getHours(), 2) + this.pad(now.getMinutes(), 2) + this.pad(now.getSeconds(), 2) + this.pad(now.getMilliseconds(), 3);
      //return "" + now.getFullYear() + this.pad(now.getMonth(), 2) + this.pad(now.getDay(), 2) + this.pad(now.getHours(), 2) + this.pad(now.getMinutes(), 2) + this.pad(now.getSeconds(), 2) + this.pad(now.getMilliseconds(), 3);
    },

    randomColor: function() {
      //rgb(29, 140, 115)
      var red = (Math.random() * 250).toFixed(0);
      var green = (Math.random() * 250).toFixed(0);
      var blue = (Math.random() * 250).toFixed(0);

      return "rgb(" + red + "," + green + "," + blue + ")";
    },

    /*
     * "1" -> true
     * 1 -> true
     */
    asNumber: function(val) {
      return Number(val) !== NaN;
    },

    /*
     * 1 -> "1"
     */
    numberToString: function(val) {
      return val + "";
    },

    drawShape: function(context, points, borderColor, close) {
      context.beginPath();
      angular.forEach(points, function(point, index) {
        if (index === 0) {
          context.moveTo(point.x, point.y);
        } else {
          context.lineTo(point.x, point.y);
        }
      });

      if (close) {
        context.closePath();
      }

      context.strokeStyle = borderColor;
      context.stroke();
      context.save();
    },

    getLineSlope: function(startPoint, endPoint) {
      return (endPoint.y - startPoint.y) / (endPoint.x - startPoint.x);
    },

    /*
     * y2-y1    y3-y2
     * -----  = ------
     * x2-x1    x3-x2
     */
    isLine: function(firstPoint, secondPoint, thirdPoint) {
      return (secondPoint.y - firstPoint.y) * (thirdPoint.x - secondPoint.x) === (thirdPoint.y - secondPoint.y) * (secondPoint.x - firstPoint.x);
    },

    /*
     * 视频帧的计算，当前时间乘以帧率
     */
    getFrameIndex: function(time, fps) {
      return Math.ceil(time * fps);
    }
  };

});
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
'use strict';

/*
 * size: small, normal, large, huge
 */
angular.module('huoyun-ui').directive("widgetsButton", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/button/button.html",
    replace: true,
    scope: {
      text: "@"
    },
    controller: "buttonController",
    link: function($scope, elem, attrs) {
      $scope.size = attrs.size;
    }
  };
});

angular.module('huoyun-ui').controller("buttonController", ["$scope", function($scope) {

}]);
'use strict';

angular.module('huoyun-ui').directive("widgetsCheckbox", ["huoyunUtil", function(huoyunUtil) {
  return {
    restrict: "A",
    templateUrl: "widgets/checkbox/checkbox.html",
    replace: true,
    scope: {
      "ngModel": "=",
      "content": "@",
      "onChecked": "&",
      "onUnchecked": "&"
    },
    controller: "checkboxController",
    link: function($scope, elem, attrs) {

      $scope.$watch("ngModel", function(newValue) {
        huoyunUtil.toggleClass(elem, newValue, "checked");
      });

      $scope.onCheckboxClicked = function() {
        var disabled = elem.attr("disabled") === "disabled";
        if(!disabled) {
          $scope.ngModel = !$scope.ngModel;
          if($scope.ngModel) {
            $scope.onChecked();
          } else {
            $scope.onUnchecked();
          }

          $scope.$emit("changed");
        }
      };

    }
  };
}]);

angular.module('huoyun-ui').controller("checkboxController", ["$scope", function($scope) {

}]);
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
'use strict';

angular.module('huoyun-ui').directive("widgetsFootBar", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/footbar/footbar.html",
    replace: true,
    controller: "footBarController",
    link: function($scope, elem, attrs) {

    }
  };
});

angular.module('huoyun-ui').controller("footBarController", ["$scope", "footbar", function($scope, footbarProvider) {
  $scope.links = footbarProvider.getUsefulLinks();
  $scope.copyright = footbarProvider.getCopyRight();
  $scope.companyName = footbarProvider.getCompanyName();
  $scope.record = footbarProvider.getRecordInfo();
}]);

angular.module('huoyun-ui').provider("footbar", function() {
  var usefulLinks = [];
  var copyright = null;
  var companyName = null;
  var record = null; // 备案信息

  this.setUsefulLinks = function(links) {
    if (angular.isArray(links)) {
      usefulLinks = links;
    }
  };

  this.getUsefulLinks = function() {
    return usefulLinks;
  };

  this.setCopyRight = function(value) {
    copyright = value;
  };

  this.getCopyRight = function() {
    return copyright;
  };

  this.setRecordInfo = function(value) {
    record = value;
  };

  this.getRecordInfo = function() {
    return record;
  };

  this.setCompanyName = function(value) {
    companyName = value;
  };

  this.getCompanyName = function() {
    return companyName;
  };

  this.$get = function() {
    return this;
  };
});
'use strict';

/*
 * fields:所有字段的meta，例如
 * fields: [{
 *   name: "firstName",
 *   label: "姓",
 *   type: "text",
 *   required: true,
 *   
 * }]
 */
angular.module('huoyun-ui').directive("widgetsForm", ["huoyunUtil", function(huoyunUtil) {
  return {
    restrict: "A",
    templateUrl: "widgets/form/form.html",
    replace: true,
    scope: {
      "title": "@",
      "fields": "=",
      "data": "="
    },
    controller: "formController",
    link: function($scope, elem, attrs) {

    }
  };
}]);

angular.module('huoyun-ui').controller("formController", ["$scope", function($scope) {

}]);
'use strict';

angular.module('huoyun-ui').directive("widgetsInputGroup", ["huoyunUtil", function(huoyunUtil) {
  return {
    restrict: "A",
    templateUrl: "widgets/inputGroup/inputGroup.html",
    replace: true,
    scope: {
      "label": "@",
      "ngModel": "=",
      "type": "@",
      "readonly": "@",
      "requried": "@",
      "hasError": "@",
      "disabled": "@"
    },
    controller: "inputGroupController",
    controllerAs: "inputGroup",
    link: function($scope, elem, attrs) {

      $scope.$watch("requried", function(newValue) {
        huoyunUtil.toggleClass(elem, huoyunUtil.boolParse(newValue), "requried");
      });

      $scope.$watch("readonly", function(newValue) {
        huoyunUtil.toggleClass(elem, huoyunUtil.boolParse(newValue), "readonly");
      });

      $scope.$watch("error", function(newValue) {
        huoyunUtil.toggleClass(elem, huoyunUtil.boolParse(newValue), "error");
      });
    }
  };
}]);

angular.module('huoyun-ui').controller("inputGroupController", ["$scope", "huoyunUtil", function($scope, huoyunUtil) {

}]);
'use strict';

angular.module('huoyun-ui').directive("widgetsLinkButton", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/linkButton/linkButton.html",
    replace: true,
    scope: {
      text: "@"
    },
    controller: "linkButtonController",
    link: function($scope, elem, attrs) {

    }
  };
});

angular.module('huoyun-ui').controller("linkButtonController", ["$scope", function($scope) {

}]);
'use strict';

angular.module('huoyun-ui').directive("widgetsLoading", ["$log", function($log) {
  return {
    restrict: "A",
    templateUrl: "widgets/loading/loading.html",
    replace: true,
    scope: {
      text: "@",
      show: "="
    },
    controller: "loadingController",
    link: function($scope, elem, attrs) {
      $scope.$watch("show",function(){
        if($scope.show !== true){
          elem.css("display","none");
        }else{
          elem.css("display","block");
        }
      });
    }
  }
}]);

angular.module('huoyun-ui').controller("loadingController", ["$scope", "$log", function($scope, $log) {
 
}]);
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
'use strict';

angular.module('huoyun-ui').directive("widgetsPicMarker", ["huoyunUtil", "$sce", "$log", function(huoyunUtil, $sce, $log) {
  return {
    restrict: "A",
    templateUrl: "widgets/marker/pic.marker.html",
    replace: true,
    scope: {
      "src": "=",
      "shapes": "=",
      "taskItemFile": "="
    },
    controller: "picMarkerController",
    link: function($scope, elem, attrs) {
      $scope.$watch("src", function() {
        if ($scope.src) {
          $log.debug("Picture Source URL:" + $scope.src);
          elem.find("img").attr("src", $sce.trustAsResourceUrl($scope.src));
        }
      });
    }
  }
}]);

angular.module('huoyun-ui').controller("picMarkerController", ["$scope", function($scope) {
  $scope.frameIndex = 1;
}]);
'use strict';

angular.module('huoyun-ui').directive("widgetsPlayerBar", ["huoyunUtil", "$sce", "$log", function(huoyunUtil, $sce, $log) {
  return {
    restrict: "A",
    templateUrl: "widgets/marker/player.bar.html",
    replace: true,
    scope: {
      controlBar: "="
    },
    controller: "playerBarController"
  };
}]);

angular.module('huoyun-ui').controller("playerBarController", ["$scope", "$log", function($scope, $log) {
  if(!$scope.controlBar) {
    $scope.controlBar = angular.module("huoyun-ui").newObject("VideoControlBar");
  }
  $scope.controlBar.setScope($scope);

  $(document).on("keydown", function(event) {
    console.log(event.keyCode)
    if($scope.controlBar) {
      event.stopPropagation();
      switch(event.keyCode) {
        case 67:
          if($scope.controlBar.status === "play") {
            $scope.controlBar.pause();
          } else {
            $scope.controlBar.play();
          }
          break;
        case 73:
          $scope.controlBar.pause();
          $scope.controlBar.updatePrecent(0);
          break;
        case 77:
          $scope.controlBar.forward();
          break;
        case 78:
          $scope.controlBar.back();
          break;
        case 88:
          $scope.controlBar.smallforward();
          break;
        case 90:
          $scope.controlBar.smallback();
          break;
      }
    }
  });

  $scope.$on("onPrecentChanged", function(event, precent) {
    console.log(precent);
    $scope.controlBar.updatePrecent(precent);
  });
}]);

angular.module("huoyun-ui").definedObject("VideoControlBar", function() {
  var $scope = null;
  var step = 5; // 快进快退时间间隔，单位是秒
  var self = this;
  self.playbackRate = 1; //正常速率播放

  function setVideosPlayRate(rate) {
    obj.playRate = rate;
    obj.videos.forEach(function(video) {
      video.setPlayRate(rate);
    });
  }

  function playAll() {
    var result = true;
    angular.forEach(obj.videos, function(video, index) {
      result = result && video.play();
    });
    if(result) {
      obj.status = "play";
    }
  }

  function pauseAll() {
    angular.forEach(obj.videos, function(video, index) {
      video.pause();
    });
    obj.status = "pause";
  }

  function forwardAll() {
    angular.forEach(obj.videos, function(video, index) {
      video.forward();
    });
  }

  function backAll() {
    angular.forEach(obj.videos, function(video, index) {
      video.back();
    });
  }

  function smallBackAll() {
    angular.forEach(obj.videos, function(video, index) {
      video.smallback();
    });
  }

  function smallForwardAll() {
    angular.forEach(obj.videos, function(video, index) {
      video.smallforward();
    });
  }

  function updatePrecentAll(val) {
    angular.forEach(obj.videos, function(video, index) {
      video.updatePrecent(val);
    });
  }

  function onTimeUpdated() {
    obj.currentTime = obj.videos[0].currentTime;
    obj.duration = obj.videos[0].duration;
    obj.percent = obj.videos[0].percent;
    $scope && $scope.$apply();
  }

  function onMetadataLoaded() {
    obj.duration = obj.videos[0].duration;
    $scope && $scope.$apply();
  }

  function onVideoEnded() {
    obj.status = "end";
    $scope && $scope.$apply();
  }

  var obj = {
    videos: [],
    status: "waiting",
    percent: 0,
    currentTime: 0,
    duration: 0,
    playRate: 1,
    push: function(video) {
      this.videos.push(video);
      if(this.videos.length === 1) {
        this.videos[0].setTimeUpdatedCallback(onTimeUpdated);
        this.videos[0].setMetadataLoadCallback(onMetadataLoaded);
        this.videos[0].setPlayEndedCallback(onVideoEnded);
      }
    },
    play: function() {
      playAll();
    },
    pause: function() {
      pauseAll();
    },
    setScope: function(scope) {
      $scope = scope;
    },
    setPlayRate: function(rate) {
      setVideosPlayRate(rate);
    },
    forward: function() {
      forwardAll();
    },
    back: function() {
      backAll();
    },
    smallback: function() {
      smallBackAll();
    },
    smallforward: function() {
      smallForwardAll();
    },
    updatePrecent: function(val) {
      updatePrecentAll(val);
    }

  };

  return obj;

});

angular.module('huoyun-ui').definedObject("playBarObj", function() {
  var $scope = null;
  var markBars = [];
  var videoObjs = [];

  function playAll() {
    angular.forEach(videoObjs, function(videoObj, index) {
      videoObj.play();
    });
  }

  function pauseAll() {
    angular.forEach(videoObjs, function(videoObj, index) {
      videoObj.pause();
    });
  }

  function disableAllMark() {
    angular.forEach(markBars, function(bar, index) {
      bar.setDisabled(true);
    });
  }

  function enableAllMark() {
    angular.forEach(markBars, function(bar, index) {
      bar.setDisabled(false);
    });
  }

  var onVideoTimeUpdateCallback = function(currentTime, duration) {
    console.log(currentTime + "," + duration)
    obj.currentTime = currentTime;
    obj.duration = duration;
    obj.percent = (currentTime / duration) * 100;
    $scope && $scope.$apply();
  };

  var onVideoEndedCallback = function() {
    obj.status = "pause";
    $scope && $scope.$apply();
  };

  var onVideoWaitingCallback = function() {
    obj.pause();
  };

  var obj = {
    status: "pause",
    disabled: false,
    percent: 0,
    currentTime: 0,
    duration: 0,
    setScope: function(val) {
      $scope = val;
    },
    addMarkBar: function(bar) {
      markBars.push(bar);
    },
    addVideoObj: function(obj) {
      if(videoObjs.length === 0) {
        obj.setTimeUpdatedCallback(onVideoTimeUpdateCallback);
        obj.setEndedCallback(onVideoEndedCallback);
      }
      obj.setWaitingCallback(onVideoWaitingCallback);
      videoObjs.push(obj);
    },
    play: function() {
      if(!this.disabled) {
        this.status = "play";
        disableAllMark();
        playAll();
      }
    },
    pause: function() {
      if(!this.disabled) {
        this.status = "pause";
        enableAllMark();
        pauseAll();
      }
    }
  };

  return obj;
});

angular.module('huoyun-ui').filter("hour", ["huoyunUtil", function(huoyunUtil) {
  return function(input) {
    if(typeof input === "number") {
      var hour = huoyunUtil.pad(parseInt(input / (60 * 60)), 2);
      var minu = huoyunUtil.pad(parseInt((input - (hour * 60 * 60)) / 60), 2);
      var second = huoyunUtil.pad(parseInt(input - (hour * 60 * 60) - (minu * 60)), 2);

      return hour + ":" + minu + ":" + second;
    }

    return "00:00:00";
  };
}]);
'use strict';

angular.module('huoyun-ui').directive("widgetsVideoPlayer", ["huoyunUtil", "$sce", "$log", function(huoyunUtil, $sce, $log) {
  return {
    restrict: "A",
    templateUrl: "widgets/marker/video.marker.html",
    replace: true,
    scope: {
      "shapes": "=",
      "src": "=",
      "video": "=",
      "frameIndex": "="
    },
    controller: "videoPlayerController",
    link: function($scope, elem, attrs) {
      var video = elem.find("video");
      if(video) {

        $scope.$watch("video", function() {
          if($scope.video && $scope.video.inited === false) {
            $scope.video.setLogger($log);
            $scope.video.init(video[0]);
          }
        });

        $scope.$watch("src", function() {
          if($scope.src) {
            $log.debug("Video Source URL:" + $scope.src);
            elem.find("source").attr("src", $sce.trustAsResourceUrl($scope.src));
            video[0].load();
          }
        });
      }
    }
  };
}]);

angular.module('huoyun-ui').controller("videoPlayerController", ["$scope", function($scope) {

}]);

angular.module("huoyun-ui").definedObject("Video", function() {
  var $log = null;
  //var step = 5; // 快进快退时间间隔，单位是秒
  var onTimeUpdatedCallbacks = [];
  var onVideoPauseCallbacks = [];
  var onMetadataLoadedCallbacks = [];
  var onVideoEndedCallbacks = [];
  var frameIndex = 0;
  var self = this;
  self.playbackRate = 1;

  var timer = null;

  function init(videoDom) {
    obj.inited = true;
    obj.dom = videoDom;
    obj.dom.ontimeupdate = function(e) {
      e.preventDefault();
      obj.currentTime = this.currentTime;
      obj.duration = this.duration;
      obj.percent = (this.currentTime / this.duration) * 100;
      $log && $log.debug("Video is playing at " + this.currentTime + "s, total time length is " + this.duration + "s.");
      startTimer();
      calcVideoFrame();
      angular.forEach(onTimeUpdatedCallbacks || [], function(callback, index) {
        callback.apply(this, [this.currentTime, this.duration]);
      }.bind(this));
    };

    obj.dom.onpause = function(e) {
      e.preventDefault();
      angular.forEach(onVideoPauseCallbacks || [], function(callback, index) {
        callback.apply(this, []);
      });
    };

    obj.dom.onwaiting = function(e) {
      stopTimer();
      $log && $log.debug("Video is on waiting");
    };

    obj.dom.onloadedmetadata = function(e) {
      $log && $log.debug("Video metadata is loaded");
      console.log(arguments);
      obj.duration = this.duration;
      angular.forEach(onMetadataLoadedCallbacks || [], function(callback, index) {
        callback.apply(this, [this.duration]);
      }.bind(this));
    };

    obj.dom.onended = function(e) {
      $log && $log.debug("Video play ended.");
      stopTimer();
      obj.duration = this.duration;
      angular.forEach(onVideoEndedCallbacks || [], function(callback, index) {
        callback.apply(this, [this.duration]);
      }.bind(this));
    };

    obj.dom.onerror = function(e) {
      stopTimer();
      $log && $log.debug("Video occur error.");
    };

    obj.dom.oncanplaythrough = function(e) {
      $log && $log.debug("Video can play through.");
    };
  }

  function setVideoPlayRate(rate) {
    self.playbackRate = rate;
    if(obj.dom) {
      obj.dom.playbackRate = rate;
    }
  }

  function onFrameChanged() {
    if(obj.fps !== undefined && obj.fps !== null) {
      calcVideoFrame();
      angular.forEach(onTimeUpdatedCallbacks || [], function(callback, index) {
        callback.apply(this, [this.currentTime, this.duration]);
      }.bind(this));
    } else {
      $log && $log.warn("can't calc current video frame, because not set video fps");
    }
  }

  function calcVideoFrame() {
    var videoTime = obj.dom.currentTime;
    frameIndex = Math.ceil(obj.fps * videoTime);
    console.log("Current video time: " + videoTime);
    console.log("Current video frame: " + frameIndex);
  }

  function startTimer() {
    if(!timer) {
      if(obj.dom && !obj.dom.paused) {
        var dur = 8;
        if(self.playbackRate === 0.5) {
          dur = 16;
        } else if(self.playbackRate === 2) {
          dur = 4;
        }
        timer = setInterval(onFrameChanged, dur);
      }
    }
  }

  function stopTimer() {
    clearInterval(timer);
    timer = null;
  }

  function playVideo() {
    if(obj.dom) {
      $log && $log.debug("Video is playing ...");
      obj.dom.playbackRate = self.playbackRate;
      obj.dom.play();
      startTimer();
      return true;
    } else {
      $log && $log.warn("Video play failed, because video not set.");
      return false;
    }
  }

  function updateVideoPrecent(val) {
    if(typeof val === "number" && obj.dom) {
      obj.dom.currentTime = obj.dom.duration * val;
    }
  }

  function pauseVideo() {
    if(obj.dom) {
      $log && $log.debug("Video is pause.");
      obj.dom.pause();
    } else {
      $log && $log.warn("Video pause failed, because video not set.");
    }
    stopTimer();
  }

  function forwardVideo(step) {
    if(obj.dom) {
      if(obj.dom.currentTime + step >= obj.dom.duration) {
        obj.dom.currentTime = obj.dom.duration;
      } else {
        obj.dom.currentTime += step;
      }
    } else {
      $log && $log.warn("Video forward failed, because video not set.");
    }
  }

  function backVideo(step) {
    if(obj.dom) {
      if(obj.dom.currentTime - step <= 0) {
        obj.dom.currentTime = 0;
      } else {
        obj.dom.currentTime -= step;
      }
    } else {
      $log && $log.warn("Video back failed, because video not set.");
    }
  }

  var obj = {
    inited: false,
    dom: null,
    currentTime: 0,
    duration: 0,
    percent: 0,
    fps: null,
    setPlayRate: function(rate) {
      setVideoPlayRate(rate);
    },
    forward: function() {
      forwardVideo(3);
    },
    back: function() {
      backVideo(3);
    },
    smallforward: function() {
      forwardVideo(0.5);
    },
    smallback: function() {
      backVideo(0.5);
    },
    updatePrecent: function(val) {
      updateVideoPrecent(val);
    },
    currentFrame: function() {
      return frameIndex;
      //    if(this.fps !== undefined && this.fps !== null) {
      //      return Math.ceil(this.fps * this.currentTime);
      //    } else {
      //      $log && $log.warn("can't calc current video frame, because not set video fps");
      //    }
    },
    init: function(videoDom) {
      init(videoDom);
    },
    play: function() {
      return playVideo();
    },
    pause: function() {
      pauseVideo();
    },
    setLogger: function(logger) {
      $log = logger;
    },
    setTimeUpdatedCallback(callback) {
      onTimeUpdatedCallbacks.push(callback);
    },
    setPauseCallback(callback) {
      onVideoPauseCallbacks.push(callback);
    },
    setMetadataLoadCallback(callback) {
      onMetadataLoadedCallbacks.push(callback);
    },
    setPlayEndedCallback(callback) {
      onVideoEndedCallbacks.push(callback);
    }
  };

  return obj;
});

angular.module('huoyun-ui').definedObject("videoObj", function(elem) {
  var videoElem = elem;
  var $scope = null;
  var onTimeUpdatedCallback = null;
  var onEndedCallback = null;
  var onWaitingCallback = null;

  addEventListeners();

  function addEventListeners() {
    if(videoElem) {
      videoElem.ontimeupdate = function(e) {
        onTimeUpdatedCallback && onTimeUpdatedCallback.apply(this, [this.currentTime, this.duration]);
        $scope && $scope.$emit("onTimeUpdated", this.currentTime, this.duration);
      };

      videoElem.onended = function(e) {
        onEndedCallback && onEndedCallback.apply(this);
        $scope && $scope.$emit("onEnded");
      };

      videoElem.onwaiting = function(e) {
        onWaitingCallback && onWaitingCallback.apply(this);
        $scope && $scope.$emit("onWaiting");
      };
    }
  }

  function removeEventListeners() {
    if(videoElem) {
      videoElem.ontimeupdate = null;
      videoElem.onended = null;
      videoElem.onwaiting = null;
    }
  }

  var obj = {
    src: "",
    getVideoElement: function() {
      return videoElem;
    },
    setVideoElement: function(val) {
      removeEventListeners();
      videoElem = val;
      addEventListeners();
    },
    setScope: function(val) {
      $scope = val;
    },
    play: function() {
      if(videoElem) {
        videoElem.play();
      } else {
        console.warn("Play failed, because hasn't set video element.")
      }
    },
    pause: function() {
      if(videoElem) {
        videoElem.pause();
      } else {
        console.warn("Play failed, because hasn't set video element.")
      }
    },
    setTimeUpdatedCallback: function(callback) {
      if(typeof callback === "function") {
        onTimeUpdatedCallback = callback;
      } else {
        console.warn("set timeUpdatedCallback failed, because the set value is not function.");
      }
    },
    setEndedCallback: function(callback) {
      if(typeof callback === "function") {
        onEndedCallback = callback;
      } else {
        console.warn("set endedCallback failed, because the set value is not function.");
      }
    },
    setWaitingCallback: function(callback) {
      if(typeof callback === "function") {
        onWaitingCallback = callback;
      } else {
        console.warn("set waitingCallback failed, because the set value is not function.");
      }
    }
  };

  return obj;
});
'use strict';

angular.module('huoyun-ui').directive("widgetsNav", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/nav/nav.html",
    replace: true,
    scope: {
      "current": "@"
    },
    controller: "navController",
    link: function($scope, elem, attrs) {
      $scope.$watch("contentWidth", function() {
        elem.find("ul").css("width", $scope.contentWidth);
      });
    }
  };
});

angular.module('huoyun-ui').controller("navController", ["$scope", "nav", "page", function($scope, navProvider, pageProvider) {
  $scope.items = navProvider.getItems() || [];
  angular.forEach($scope.items, function(item, index) {
    item.className = item.name === $scope.current ? "active" : "";
  });

  $scope.contentWidth = pageProvider.getPageWidth();
}]);

angular.module('huoyun-ui').provider("nav", function() {
  this.items = [];

  this.setItems = function(items) {
    if (angular.isArray(items)) {
      this.items = items;
    }
  };

  this.getItems = function() {
    return this.items;
  };

  this.$get = function() {
    return this;
  };
});
'use strict';

angular.module('huoyun-ui').directive("widgetsPage", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/page/page.html",
    replace: true,
    scope: true,
    transclude: true,
    controller: "pageController",
    link: function($scope, elem, attrs) {

      $scope.$watch("width", function() {
        elem.css("width", $scope.width);
      });
    }
  };
});

angular.module('huoyun-ui').controller("pageController", ["$scope", "page", function($scope, pageProvider) {
  $scope.width = pageProvider.getPageWidth();
}]);

angular.module('huoyun-ui').provider("page", function() {

  var pageWidth = "960px";

  this.setPageWidth = function(width) {
    pageWidth = width;
  };

  this.getPageWidth = function() {
    return pageWidth;
  }

  this.$get = function() {
    return this;
  };
});
'use strict';

angular.module('huoyun-ui').directive("widgetsProgress", ["huoyunUtil", "$sce", "$log", function(huoyunUtil, $sce, $log) {
  return {
    restrict: "A",
    templateUrl: "widgets/progress/progress.html",
    replace: true,
    scope: {
      percent: "@"
    },
    controller: "progressController",
    link: function($scope, elem, attrs) {
      $scope.$watch("percent", function() {
        var progressBar = elem.find(".huoyun-progress-bar");
        var positionBtn = elem.find(".position-btn");
        if (progressBar && $scope.percent) {
          progressBar.css("width", $scope.percent + "%");
          positionBtn.css("left", $scope.percent + "%");
        }
      });
      
      elem.on("mousedown",function(event){
        var precent = event.offsetX / elem.width();
        $scope.$emit("onPrecentChanged",precent);
      });
    }
  };
}]);

angular.module('huoyun-ui').controller("progressController", ["$scope", function($scope) {

}]);
'use strict';

angular.module('huoyun-ui').directive("widgetsRadioButton", ["huoyunUtil", function(huoyunUtil) {
  return {
    restrict: "A",
    templateUrl: "widgets/radiobutton/radiobutton.html",
    replace: true,
    scope: {
      "ngModel": "=",
      "content": "@"
    },
    controller: "radioButtonController",
    link: function($scope, elem, attrs) {

      $scope.$watch("ngModel", function(newValue) {
        huoyunUtil.toggleClass(elem, newValue, "checked");
      });

    }
  };
}]);

angular.module('huoyun-ui').controller("radioButtonController", ["$scope", function($scope) {

  $scope.onRadioButtonClicked = function() {
    $scope.ngModel = !$scope.ngModel;
    $scope.$emit("changed");
  };
}]);
'use strict';

angular.module('huoyun-ui').definedObject("markBarObj", function() {
  var svgDrawObj = null;

  function enableDrawing() {
    if (svgDrawObj) {
      svgDrawObj.readonly = false;
    }
  }

  function disableDrawing() {
    if (svgDrawObj) {
      svgDrawObj.readonly = true;
    }
  }

  return {
    disabled: false,
    visibility: false,
    canMark: false,
    setDisabled: function(val) {
      this.disabled = val;
      this.setCanMark(false);
    },
    setVisibility: function(val) {
      this.visibility = val;
      this.setCanMark(false);
    },
    setCanMark: function(val) {
      if (this.canMark !== val) {
        this.canMark = val;
        if (val) {
          enableDrawing();
        } else {
          disableDrawing();
        }
      }
    },
    deleteShape: function() {
      if (!this.disabled) {
        deleteSelected();
      }
    },
    setAttrs: function() {
      if (!this.disabled) {
        if (currentSelectedShape) {
          showAttrDialog();
        } else {
          dialog.showWarn("请先选择要标记的图形！");
        }
      }
    },
    select: function() {
      if (!this.disabled) {
        this.setCanMark(false);
      }
    },
    draw: function() {
      if (!this.disabled) {
        this.setCanMark(true);
      }
    },
    setSvgDrawObj: function(val) {
      svgDrawObj = val;
    }
  };
});
'use strict';

angular.module('huoyun-ui').directive("widgetsMarkInfoBar", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/shape/mark.info.bar.html",
    replace: true,
    scope: {
      markInfo: "=",
      colorGroup: "=",
      tags: "=",
      onColorChanged: "&"
    },
    controller: "markInfoBarController",
    link: function($scope, elem, attrs) {

    }
  };
});

angular.module('huoyun-ui').controller("markInfoBarController", ["$scope", function($scope) {

  $scope.onColorSelectedChanged = function(colorTag) {
    $scope.onColorChanged({
      colorTag: colorTag
    });
    if($scope.markInfo && $scope.markInfo.color !== colorTag.color) {
      $scope.markInfo.changeColor(colorTag.color);
    }
  }
}]);
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
          return !shape.$$timeline.inRange($scope.frameIndex);
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
        ($scope.names || []).forEach(function(name) {
          if(shapeNames.indexOf(name) === -1) {
            res.push(name);
          }
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
            case 65:
              $scope.onNewButtonClick();
              $scope.$apply();
              break;
            case 70:
              if($scope.current) {
                $scope.onDeleteShapeButtonClick($scope.current);
              }
              break;
            case 71:
              if($scope.current) {
                $scope.onStopMarkButtonClick($scope.current);
              }
              break;
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
      $logger && $logger.debug("init shape ->start draw polyline ...");
      obj.readonly = true;
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
      return this.getStoryBoard() !== null && !this.readonly;
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
'use strict';

angular.module('huoyun-ui').directive("widgetsSideBar", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/sidebar/sidebar.html",
    replace: true,
    scope: {
      groups: "=",
      currentGroup: "@",
      currentItem: "@"
    },
    controller: "sideBarController",
    link: function($scope, elem, attrs) {}
  };
});

angular.module('huoyun-ui').controller("sideBarController", ["$scope", function($scope) {

}]);
'use strict';

angular.module('huoyun-ui').directive("widgetsTab", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/tab/tab.html",
    replace: true,
    scope: {
      "items": "="
    },
    controller: "tabController",
    link: function($scope, elem, attrs) {

      $scope.$watchCollection("items", function() {
        if($scope.items) {
          elem.find("li").css("width", (100 / $scope.items.length) + "%");
        }
      });
    }
  };
});

angular.module('huoyun-ui').controller("tabController", ["$scope", function($scope) {

  $scope.onTabItemClicked = function(item, index) {
    ($scope.items || []).forEach(function(it, itIndex) {
      it.active = index === itIndex;
    });

    $scope.$emit("onTabChanged", item, index);
  };

}]);
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
'use strict';

angular.module('huoyun-ui').directive("widgetsTopBar", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/topbar/topbar.html",
    replace: true,
    controller: "topBarController",
    link: function($scope, elem, attrs) {

    }
  };
});

angular.module('huoyun-ui').controller("topBarController", ["$scope", "application", function($scope, applicationProvider) {
  $scope.appName = applicationProvider.getName();
  $scope.hasLogin = applicationProvider.hasLogin();
  if($scope.hasLogin) {
    $scope.userName = applicationProvider.getUserName();
  }

  $scope.login = function() {
    if(typeof applicationProvider.loginFunc === "function") {
      applicationProvider.loginFunc();
    }
  };

  $scope.register = function() {
    if(typeof applicationProvider.registerFunc === "function") {
      applicationProvider.registerFunc();
    }
  };

  $scope.logout = function() {
    if(typeof applicationProvider.logoutFunc === "function") {
      applicationProvider.logoutFunc.bind(applicationProvider.getUserService())();
    }
  };
}]);

angular.module('huoyun-ui').provider("application", function() {
  var appName = null;
  var isLogin = false;
  var userName = null;
  var userService = null;

  this.loginFunc = null;
  this.registerFunc = null;
  this.logoutFunc = null;

  if(Cookies.get("login") === "true") {
    isLogin = true;
    userName = Cookies.get("username");
  }

  this.setName = function(name) {
    appName = name;
  };

  this.getName = function() {
    return appName;
  };

  this.setLogin = function(loginResult) {
    if(loginResult !== true) {
      Cookies.remove("username");
      Cookies.remove("login");
      Cookies.remove("role");
    } else {
      Cookies.set("login", loginResult);
    }

    isLogin = loginResult;
  };

  this.hasLogin = function() {
    return isLogin;
  };

  this.setUserName = function(user_name, role) {
    Cookies.set("username", user_name);
    Cookies.set("role", role);
    userName = user_name;
  };

  this.getUserName = function() {
    return userName;
  };

  this.setUserService = function(user_service) {
    userService = user_service;
  };

  this.getUserService = function() {
    return userService;
  };

  this.$get = function() {
    return this;
  };
});
angular.module("huoyun-ui").run(["$templateCache", function($templateCache) {$templateCache.put("widgets/breadcrumb/breadcrumb.html","<div class=\"widgets-bread-crumb\"><ul><li ng-repeat=\"item in items\"><a title=\"{{item.text}}\" ng-href=\"{{item.href}}\" ng-if=\"!$last\">{{item.text}}</a> <i class=\"fa fa-angle-right\" ng-if=\"!$last\"></i> <span title=\"{{item.text}}\" class=\"last-bread-crumb\" ng-if=\"$last\">{{item.text}}</span></li></ul></div>");
$templateCache.put("widgets/button/button.html","<div class=\"widgets-button\">{{text}}</div>");
$templateCache.put("widgets/checkbox/checkbox.html","<div class=\"widgets-checkbox\" ng-click=\"onCheckboxClicked()\" dis=\"dis\"><div class=\"checkbox-icon\"><i class=\"fa fa-square-o uncheck-font\" aria-hidden=\"true\"></i> <i class=\"fa fa-check-square check-font\" aria-hidden=\"true\"></i></div><div class=\"checkbox-content\">{{content}}</div></div>");
$templateCache.put("widgets/colorpicker/color.picker.html","<div class=\"widgets-color-picker\"><input class=\"color-button\"></div>");
$templateCache.put("widgets/cursor/cursor.html","<div class=\"widgets-cursor\"><div class=\"widgets-cursor-line-h\"></div><div class=\"widgets-cursor-line-v\"></div></div>");
$templateCache.put("widgets/dialog/confirm.html","<div class=\"widgets-dialog-confirm\"><div class=\"header-bar\"><div class=\"header-bar-container\"><i class=\"icon fa fa-info\"></i> <span>{{ngDialogData.title}}</span></div></div><div class=\"dialog-content\">{{ngDialogData.message}}</div><div class=\"dialog-tools-bar\"><div class=\"dialog-tools-bar-container\"><div widgets-button=\"\" text=\"取消\" huoyun-append-class=\"btn-gray\" ng-click=\"closeThisDialog(\'cancel\')\"></div><div widgets-button=\"\" text=\"确定\" ng-click=\"closeThisDialog(\'ok\')\"></div></div></div></div>");
$templateCache.put("widgets/dialog/show.html","<div class=\"widgets-dialog-show\" level=\"{{ngDialogData.level}}\"><div class=\"header-bar\"><div class=\"header-bar-container\"><i class=\"icon fa fa-info\" ng-if=\"ngDialogData.level === \'info\'\"></i> <i class=\"icon fa fa-exclamation-triangle\" ng-if=\"ngDialogData.level === \'warn\'\"></i> <i class=\"icon fa fa-exclamation-circle\" ng-if=\"ngDialogData.level === \'error\'\"></i> <span>{{ngDialogData.title}}</span></div></div><div class=\"dialog-content\">{{ngDialogData.message}}</div><div class=\"dialog-tools-bar\"><div class=\"dialog-tools-bar-container\"><div widgets-button=\"\" text=\"确定\" ng-click=\"closeThisDialog(\'ok\')\"></div></div></div></div>");
$templateCache.put("widgets/footbar/footbar.html","<div class=\"widgets-foot-bar\" id=\"foot\"><ul><li ng-repeat=\"link in links\"><a ng-href=\"{{link.href}}\">{{link.text}}</a></li></ul><div class=\"addition-info\"><span class=\"copyright\"><i class=\"fa fa-copyright\"></i> {{copyright}}</span> <span class=\"company-name\">{{companyName}}</span> <span class=\"record\">{{record}}</span></div></div>");
$templateCache.put("widgets/form/form.html","<div class=\"widgets-form\"><div class=\"form-header\">{{title}}</div><div class=\"form-body clearfix\"><div class=\"input-group-container\" ng-repeat=\"field in fields\"><div widgets-input-group=\"\" label=\"{{field.label}}\" ng-model=\"data[field.name]\" type=\"{{field.type}}\" requried=\"{{field.requried}}\"></div></div></div></div>");
$templateCache.put("widgets/inputGroup/inputGroup.html","<div class=\"widgets-input-group\"><label>{{label}}</label><div class=\"input-group-box input-mode\" ng-switch=\"type\"><div ng-switch-when=\"text\"><input type=\"text\" ng-model=\"$parent.ngModel\" ng-disabled=\"{{disbaled}}\"></div><div ng-switch-when=\"number\"><input type=\"number\" ng-model=\"$parent.ngModel\"></div><div ng-switch-when=\"tel\"><input type=\"tel\" ng-model=\"$parent.ngModel\"></div><div ng-switch-when=\"email\"><input type=\"email\" ng-model=\"$parent.ngModel\"></div><div ng-switch-when=\"checkbox\"><div widgets-checkbox=\"\" ng-model=\"$parent.ngModel\"></div></div><div ng-switch-when=\"radio\"><div widgets-radio-button=\"\" ng-model=\"$parent.ngModel\"></div></div><div ng-switch-when=\"textarea\" class=\"textarea-container\"><textarea ng-model=\"$parent.ngModel\"></textarea></div><div ng-switch-default=\"\">不支持该类型({{type}})控件</div></div><div class=\"input-group-box readonly-mode\" ng-switch=\"type\"><div ng-switch-when=\"link\"><a ng-href=\"{{ngModel}}\">{{ngModel}}</a></div><div ng-switch-default=\"\">{{ngModel}}</div></div></div>");
$templateCache.put("widgets/linkButton/linkButton.html","<div class=\"widgets-link-button\">{{text}}</div>");
$templateCache.put("widgets/loading/loading.html","<div class=\"widgets-loading-container\"><div class=\"widgets-loading-overlay\"></div><div class=\"widgets-loading-content\"><i class=\"fa fa-spinner fa-pulse fa-fw\"></i> <span>{{text}}</span></div></div>");
$templateCache.put("widgets/marker/pic.marker.html","<div class=\"widgets-pic-marker\"><img ng-src=\"{{src}}\"><div widgets-svg-story-board=\"\" shapes=\"shapes\" frame-index=\"frameIndex\" task-item-file=\"taskItemFile\"></div></div>");
$templateCache.put("widgets/marker/player.bar.html","<div class=\"widgets-player-bar\"><div widgets-progress=\"\" percent=\"{{controlBar.percent}}\"></div><div class=\"control-container\" play-rate=\"{{controlBar.playRate}}\"><div class=\"btn\" ng-click=\"controlBar.play()\" ng-show=\"controlBar.status !== \'play\'\"><i class=\"fa fa-play\"></i></div><div class=\"btn\" ng-click=\"controlBar.pause()\" ng-show=\"controlBar.status === \'play\'\"><i class=\"fa fa-pause\"></i></div><div class=\"btn\"><i class=\"fa fa-fast-backward\" ng-click=\"controlBar.back()\"></i></div><div class=\"btn\"><i class=\"fa fa-step-backward\" ng-click=\"controlBar.smallback()\"></i></div><div class=\"btn\"><i class=\"fa fa-step-forward\" ng-click=\"controlBar.smallforward()\"></i></div><div class=\"btn\"><i class=\"fa fa-fast-forward\" ng-click=\"controlBar.forward()\"></i></div><div class=\"btn rate\" rate=\"0.5\"><i class=\"fa\" ng-click=\"controlBar.setPlayRate(0.5)\">0.5倍</i></div><div class=\"btn rate\" rate=\"1\"><i class=\"fa\" ng-click=\"controlBar.setPlayRate(1)\">1倍</i></div><div class=\"btn rate\" rate=\"2\"><i class=\"fa\" ng-click=\"controlBar.setPlayRate(2)\">2倍</i></div><div class=\"time-info\"><span>{{controlBar.currentTime | hour}}</span> | <span>{{controlBar.duration | hour}}</span></div></div></div>");
$templateCache.put("widgets/marker/video.marker.html","<div class=\"widgets-video-player\"><video preload=\"metadata\"><source type=\"video/mp4\"></video><div widgets-svg-story-board=\"\" shapes=\"shapes\" frame-index=\"frameIndex\"></div></div>");
$templateCache.put("widgets/nav/nav.html","<nav class=\"widgets-nav\"><ul><li ng-repeat=\"item in items\" name=\"{{item.name}}\" huoyun-append-class=\"{{item.className}}\" ng-if=\"item.visibility !== false\"><a ng-href=\"{{item.href}}\">{{item.text}}</a></li></ul></nav>");
$templateCache.put("widgets/page/page.html","<div class=\"widgets-page\"><div ng-transclude=\"\"></div></div>");
$templateCache.put("widgets/progress/progress.html","<div class=\"widgets-progress\"><div class=\"background-container\"><div class=\"huoyun-progress-bar\"></div><div class=\"position-btn\"></div></div></div>");
$templateCache.put("widgets/radiobutton/radiobutton.html","<div class=\"widgets-radio-button\" ng-click=\"onRadioButtonClicked()\"><div class=\"radio-button-icon\"><i class=\"fa fa-dot-circle-o\" aria-hidden=\"true\"></i></div><div class=\"radio-button-content\">{{content}}</div></div>");
$templateCache.put("widgets/shape/mark.info.bar.html","<div class=\"widgets-mark-info-bar\"><div class=\"property-info-container\" ng-if=\"markInfo !== undefined && markInfo !== null\"><div class=\"property\" ng-show=\"colorGroup\"><label>{{colorGroup.name}}</label><div class=\"value\"><select ng-model=\"markInfo.colorTag\" ng-change=\"onColorSelectedChanged(markInfo.colorTag)\" ng-options=\"colorTag.name for colorTag in colorGroup.tags track by colorTag.id\"></select></div></div><div class=\"property\" ng-repeat=\"tag in tags\" ng-if=\"tag.type === \'DROPDOWNBOX\'\"><label>{{tag.name}}</label><div class=\"value\"><select ng-model=\"markInfo.tagDorpdownList[tag.id]\" ng-options=\"item.value for item in tag.items track by item.id\"></select></div></div><div class=\"property\" ng-repeat=\"tag in tags\" ng-if=\"tag.type === \'CHECKBOX\'\"><label>{{tag.name}}</label><div ng-repeat=\"tagItem in tag.items\"><div widgets-checkbox=\"\" ng-model=\"markInfo.tagCheckBoxList[tagItem.id]\" content=\"{{tagItem.value}}\"></div></div></div></div><div ng-if=\"markInfo === undefined || markInfo === null\">暂时没有信息</div></div>");
$templateCache.put("widgets/shape/svg.bar.html","<div class=\"widgets-svg-bar\"><div class=\"svg-bar-container\"><div class=\"tools-bar\"><div widgets-button=\"\" text=\"新建\" ng-click=\"onNewButtonClick()\"></div><div widgets-button=\"\" text=\"删除\" ng-click=\"onDeleteButtonClick()\"></div></div><ul><li class=\"shape-section\" hide=\"{{isShapeHide(shape)}}\" ng-repeat=\"shape in shapes\" ng-click=\"onShapeSectionSelect($index,shape)\" active=\"{{shape.isSelected()}}\"><div class=\"property-container\"><div class=\"property\" ng-hide=\"nameSelected === true\"><div class=\"shape-tools-bar\"><label>名称</label><div class=\"btn\" title=\"删除标记\" ng-click=\"onDeleteShapeButtonClick(shape)\"><i class=\"fa fa-trash-o\"></i></div><div class=\"btn finish-btn\" title=\"结束标记\" ng-click=\"onStopMarkButtonClick(shape)\"><i class=\"fa fa-stop\"></i></div></div><div class=\"value\"><input ng-model=\"shape.shapeId\" type=\"text\" disabled=\"disabled\"></div></div><div class=\"property\" ng-show=\"nameSelected === true\"><div class=\"shape-tools-bar\"><label>名称</label><div class=\"btn\" title=\"删除标记\" ng-click=\"onDeleteShapeButtonClick(shape)\"><i class=\"fa fa-trash-o\"></i></div><div class=\"btn finish-btn\" title=\"结束标记\" ng-click=\"onStopMarkButtonClick(shape)\"><i class=\"fa fa-stop\"></i></div></div><div class=\"value\"><select ng-model=\"shape.shapeId\" ng-options=\"name as name for name in getShapeNames(shape)\" ng-change=\"onShapeCopySelected(shape.shapeId)\"></select></div></div></div></li></ul></div><div class=\"mark-layer\" ng-show=\"disabled\"></div></div>");
$templateCache.put("widgets/sidebar/sidebar.html","<aside class=\"widgets-sidebar\"><div class=\"group\" ng-repeat=\"group in groups\" name=\"{{group.name}}\" ng-if=\"group.visibility !== false\"><div class=\"header\"><i class=\"fa\" huoyun-append-class=\"fa-{{group.icon}}\" ng-if=\"group.icon\"></i> <span>{{group.text}}</span></div><ul><li ng-repeat=\"item in group.items\" ng-class=\"group.name === currentGroup && item.name === currentItem ? \'active\':\'\'\" ng-if=\"item.visibility !== false\" name=\"{{item.name}}\" curr=\"{{currentItem}}\" groupname=\"{{group.name}}\"><a title=\"{{item.text}}\" ng-href=\"{{item.href}}\">{{item.text}}</a></li></ul></div></aside>");
$templateCache.put("widgets/tab/tab.html","<nav class=\"widgets-tab\"><ul><li ng-repeat=\"item in items\" name=\"{{item.name}}\" active=\"{{item.active}}\" ng-click=\"onTabItemClicked(item,$index)\">{{item.text}}</li></ul></nav>");
$templateCache.put("widgets/table/pagination.html","<div class=\"widgets-pagination\"><div class=\"widgets-pagination-container\" ng-show=\"totalPage !== \'0\'\"><div widgets-button=\"\" text=\"首页\" class=\"prev-button\" ng-disabled=\"pageIndex === \'0\'\" ng-click=\"onFirstButtonClicked()\"></div><div widgets-button=\"\" text=\"上一页\" class=\"prev-button\" ng-disabled=\"pageIndex === \'0\'\" ng-click=\"onPreviousButtonClicked()\"></div><div widgets-button=\"\" text=\"{{item.index}}\" ng-class=\"item.current ? \'index-button active\' : \'index-button\'\" ng-repeat=\"item in items\" ng-click=\"onIndexButtonClicked(item.index - 1)\"></div><div widgets-button=\"\" text=\"下一页\" class=\"next-button\" ng-disabled=\"disableLast()\" ng-click=\"onNextButtonClicked()\"></div><div widgets-button=\"\" text=\"最后一页\" class=\"prev-button\" ng-disabled=\"disableLast()\" ng-click=\"onLastButtonClicked()\"></div></div></div>");
$templateCache.put("widgets/table/table.html","<div class=\"widgets-table\"><table><thead><tr></tr></thead><tbody><tr class=\"empty-mask\" ng-if=\"!dataset || dataset.length === 0\"><td colspan=\"{{columns.length}}\"><div><i class=\"fa fa-inbox\"></i><span>{{emptyText}}</span></div></td></tr></tbody></table><div widgets-pagination=\"\" ng-show=\"pageData\" total-page=\"{{pageData.totalPage}}\" page-index=\"{{pageData.pageIndex}}\"></div></div>");
$templateCache.put("widgets/topbar/topbar.html","<div class=\"widgets-top-bar\"><div class=\"left\"><div class=\"title-container\">{{appName}}</div></div><div class=\"right\"><div class=\"sign-bar\" ng-if=\"!hasLogin\"><div class=\"login\" widgets-link-button=\"\" text=\"登录\" ng-click=\"login()\"></div><div class=\"register\" widgets-link-button=\"\" text=\"注册\" ng-click=\"register()\"></div></div><div class=\"info-bar\" ng-if=\"hasLogin\"><div class=\"user-info\">欢迎<span class=\"user-name\">{{userName}}</span></div><div class=\"logout\" widgets-link-button=\"\" text=\"退出\" ng-click=\"logout()\"></div></div></div></div>");}]);