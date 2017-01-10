'use strict';

zongmu.factory("passwordUtils", [
  function() {
    return {
      checkPassword: function(password) {
        if(password.length < 8) {
          return false;
        }

        if(!/[0-9]+/.test(password)) {
          return false;
        }

        if(!/[a-z]+/.test(password)) {
          return false;
        }

        if(!/[A-Z]+/.test(password)) {
          return false;
        }

        return true;
      },

      getMsg: function() {
        return "密码必须包含数字，小写字母，大写字母,并且长度必须大于8位字符。";;
      },

      checkTag: function(tag) {
        return /[_\-0-9a-zA-Z\u4e00-\u9fa5]+/.test(tag);
      },

      batchCheckTag: function() {
        return /[;_\-0-9a-zA-Z\u4e00-\u9fa5]+/.test(tag);
      }
    };
  }
]);