'use strict';

zongmu.factory("nullUtils", [
  function() {
    return {
      isNull: function(value) {
        return value === undefined || value == null;
      }
    };
  }
]);