'use strict';

angular.module('huoyun-ui').directive("widgetsPlayerBar", ["huoyunUtil", "$sce", "$log", function (huoyunUtil, $sce, $log) {
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

angular.module('huoyun-ui').controller("playerBarController", ["$scope", "$log", function ($scope, $log) {
  if (!$scope.controlBar) {
    $scope.controlBar = angular.module("huoyun-ui").newObject("VideoControlBar");
  }
  $scope.controlBar.setScope($scope);

  $(document).on("keydown", function (event) {
    console.log(event.keyCode)
    if ($scope.controlBar) {
      event.stopPropagation();
      switch (event.keyCode) {
        case 67: //C
          if ($scope.controlBar.status === "play") {
            $scope.controlBar.pause();
          } else {
            $scope.controlBar.play();
          }
          break;
        case 87: // W
          $scope.controlBar.pause();
          $scope.controlBar.updatePrecent(0);
          break;
        case 39: // -->
          $scope.controlBar.forward();
          break;
        case 37: // <--
          $scope.controlBar.back();
          break;
        case 88: // X
          $scope.controlBar.smallforward();
          break;
        case 90: // Z
          $scope.controlBar.smallback();
          break;
        case 86: // V
          $scope.controlBar.setPlayRate(0.5);
          $scope.$apply();
          break;
        case 66: // B
          $scope.controlBar.setPlayRate(1);
          $scope.$apply();
          break;
        case 78:
          $scope.controlBar.setPlayRate(2);
          $scope.$apply();
          break;
      }
    }
  });

  $scope.$on("onPrecentChanged", function (event, precent) {
    console.log(precent);
    $scope.controlBar.updatePrecent(precent);
  });
}]);

angular.module("huoyun-ui").definedObject("VideoControlBar", function () {
  var $scope = null;
  var step = 5; // 快进快退时间间隔，单位是秒
  var self = this;
  self.playbackRate = 1; //正常速率播放

  function setVideosPlayRate(rate) {
    obj.playRate = rate;
    obj.videos.forEach(function (video) {
      video.setPlayRate(rate);
    });
  }

  function playAll() {
    var result = true;
    angular.forEach(obj.videos, function (video, index) {
      result = result && video.play();
    });
    if (result) {
      obj.status = "play";
    }
  }

  function pauseAll() {
    angular.forEach(obj.videos, function (video, index) {
      video.pause();
    });
    obj.status = "pause";
  }

  function forwardAll() {
    angular.forEach(obj.videos, function (video, index) {
      video.forward();
    });
  }

  function backAll() {
    angular.forEach(obj.videos, function (video, index) {
      video.back();
    });
  }

  function smallBackAll() {
    angular.forEach(obj.videos, function (video, index) {
      video.smallback();
    });
  }

  function smallForwardAll() {
    angular.forEach(obj.videos, function (video, index) {
      video.smallforward();
    });
  }

  function updatePrecentAll(val) {
    angular.forEach(obj.videos, function (video, index) {
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
    push: function (video) {
      this.videos.push(video);
      if (this.videos.length === 1) {
        this.videos[0].setTimeUpdatedCallback(onTimeUpdated);
        this.videos[0].setMetadataLoadCallback(onMetadataLoaded);
        this.videos[0].setPlayEndedCallback(onVideoEnded);
      }
    },
    play: function () {
      playAll();
    },
    pause: function () {
      pauseAll();
    },
    setScope: function (scope) {
      $scope = scope;
    },
    setPlayRate: function (rate) {
      setVideosPlayRate(rate);
    },
    forward: function () {
      forwardAll();
    },
    back: function () {
      backAll();
    },
    smallback: function () {
      smallBackAll();
    },
    smallforward: function () {
      smallForwardAll();
    },
    updatePrecent: function (val) {
      updatePrecentAll(val);
    }

  };

  return obj;

});

angular.module('huoyun-ui').definedObject("playBarObj", function () {
  var $scope = null;
  var markBars = [];
  var videoObjs = [];

  function playAll() {
    angular.forEach(videoObjs, function (videoObj, index) {
      videoObj.play();
    });
  }

  function pauseAll() {
    angular.forEach(videoObjs, function (videoObj, index) {
      videoObj.pause();
    });
  }

  function disableAllMark() {
    angular.forEach(markBars, function (bar, index) {
      bar.setDisabled(true);
    });
  }

  function enableAllMark() {
    angular.forEach(markBars, function (bar, index) {
      bar.setDisabled(false);
    });
  }

  var onVideoTimeUpdateCallback = function (currentTime, duration) {
    console.log(currentTime + "," + duration)
    obj.currentTime = currentTime;
    obj.duration = duration;
    obj.percent = (currentTime / duration) * 100;
    $scope && $scope.$apply();
  };

  var onVideoEndedCallback = function () {
    obj.status = "pause";
    $scope && $scope.$apply();
  };

  var onVideoWaitingCallback = function () {
    obj.pause();
  };

  var obj = {
    status: "pause",
    disabled: false,
    percent: 0,
    currentTime: 0,
    duration: 0,
    setScope: function (val) {
      $scope = val;
    },
    addMarkBar: function (bar) {
      markBars.push(bar);
    },
    addVideoObj: function (obj) {
      if (videoObjs.length === 0) {
        obj.setTimeUpdatedCallback(onVideoTimeUpdateCallback);
        obj.setEndedCallback(onVideoEndedCallback);
      }
      obj.setWaitingCallback(onVideoWaitingCallback);
      videoObjs.push(obj);
    },
    play: function () {
      if (!this.disabled) {
        this.status = "play";
        disableAllMark();
        playAll();
      }
    },
    pause: function () {
      if (!this.disabled) {
        this.status = "pause";
        enableAllMark();
        pauseAll();
      }
    }
  };

  return obj;
});

angular.module('huoyun-ui').filter("hour", ["huoyunUtil", function (huoyunUtil) {
  return function (input) {
    if (typeof input === "number") {
      var hour = huoyunUtil.pad(parseInt(input / (60 * 60)), 2);
      var minu = huoyunUtil.pad(parseInt((input - (hour * 60 * 60)) / 60), 2);
      var second = huoyunUtil.pad(parseInt(input - (hour * 60 * 60) - (minu * 60)), 2);

      return hour + ":" + minu + ":" + second;
    }

    return "00:00:00";
  };
}]);