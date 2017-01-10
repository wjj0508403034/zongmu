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