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