'use strict';
zongmu.factory('markUtil', function() {

  var urlMap = {
    "FOUR_VIDEO": "video.four.html",
    "FOUR_PICTURE": "pic.four.html",
    "SINGLE_VIDEO": "video.html",
    "SINGLE_PICTURE": "pic.html",
    "PICTURE_PICTURE": "pic.html"
  };

  return {
    getMarkPage: function(taskRecord) {
      var pageName = null;
      if(taskRecord.assetType === "FOUR") {
        return "video.four.html";
      } else if(taskRecord.assetType === "SINGLE") {
        return taskRecord.taskType === 'PICTURE' ? "pic.html" : "video.html";
      } else if(taskRecord.assetType === "PICTURE") {
        return "pic.html";
      }
    },

    getMarkPageUrl: function(assetType, taskType, taskRecordNo) {
      var pageName = urlMap[`${assetType}_${taskType}`];
      return `${pageName}?taskRecordNo=${taskRecordNo}`;
    }
  };
});