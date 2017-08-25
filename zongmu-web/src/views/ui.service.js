'use strict';

zongmu.factory('enumService', function() {
  return {
    getHours: function() {
      var hours = [];
      for (var index = 0; index <= 24; index++) {
        hours.push({
          name: index,
          text: index
        });
      }

      return hours;
    },

    getSexes: function() {
      return [{
        name: "UNKOWN",
        text: "--"
      }, {
        name: "MAN",
        text: "男"
      }, {
        name: "FEMAN",
        text: "女"
      }];
    },

    getTaskTypes: function() {
      return [{
        name: "VIDEO",
        text: "视频"
      }, {
        name: "PICTURE",
        text: "图片"
      }];
    },

    getTaskPriorities: function() {
      return [{
        name: 1,
        text: "高"
      }, {
        name: 2,
        text: "中"
      }, {
        name: 3,
        text: "低"
      }];
    },

    getTagControlTypes: function() {
      return [{
        name: "DROPDOWNBOX",
        text: "下拉框"
      }, {
        name: "CHECKBOX",
        text: "复选框"
      }];
    },

    getTagCategories: function() {
      return [{
        name: "ROAD",
        text: "路况"
      }, {
        name: "WEATHER",
        text: "天气"
      }];
    },

    getShapeTypes: function() {
      return [{
        name: "RECT",
        text: "矩形"
      }, {
        name: "POLYLINE",
        text: "多边形"
      }, {
        name: "ANY",
        text: "任意多边形"
      }];
    },

    getVideoShapeTypes: function() {
      return [{
        name: "RECT",
        text: "矩形"
      }, {
        name: "POLYLINE",
        text: "多边形"
      }];
    },

    getUploadTypes: function() {
      return [{
        name: "SINGLE",
        text: "一路视频"
      }, {
        name: "FOUR",
        text: "四路视频"
      }, {
        name: "PICTURE",
        text: "图片"
      }];
    },

    getTaskStatus: function() {
      return [{
        name: "NEW",
        text: "新任务"
      }, {
        name: "INPROGRESS",
        text: "进行中"
      }, {
        name: "FINISHED",
        text: "已结束"
      }];
    },

    getReviewTaskStatus: function() {
      return [{
        name: "WAITTING",
        text: "待审核"
      }, {
        name: "INPROGRESS",
        text: "审核中"
      }, {
        name: "PASS",
        text: "审核成功"
      }, {
        name: "FAILED",
        text: "审核失败"
      }];
    },

    getOps: function() {
      return [{
        name: ">",
        text: "大于",
        value: "GreatThan"
      }, {
        name: "=",
        text: "等于",
        value: "Equal"
      }, {
        name: "<",
        text: "小于",
        value: "LessThan"
      }];
    },

    getBusinessRoles: function() {
      return [{
        name: "ADMIN",
        text: "管理员"
      }, {
        name: "FINANCE",
        text: "财务人员"
      }, {
        name: "REVIEW",
        text: "审核人员"
      }, {
        name: "NORMAL",
        text: "普通用户"
      }, {
        name: "UPLOAD",
        text: "路测人员"
      }, {
        name: "SUPER",
        text: "高级用户"
      }];
    }
  };
});

zongmu.filter("businessRole", function() {

  return function(input) {
    switch (input) {
      case "ADMIN":
        return "管理员";
      case "FINANCE":
        return "财务人员";
      case "REVIEW":
        return "审核人员";
      case "NORMAL":
        return "普通用户";
      case "UPLOAD":
        return "路测人员";
      case "SUPER":
        return "高级用户";
      default:
        return input;
    }
  };
});

zongmu.filter("sex", function() {
  return function(input) {
    switch (input) {
      case "MAN":
        return "男";
      case "FEMAN":
        return "女";
      default:
        return "--";
    }
  };
});

zongmu.filter("boolText", function() {
  return function(input) {
    return input ? "是" : "否";
  };
});

zongmu.filter("tagCategory", function() {
  return function(input) {
    switch (input) {
      case "ROAD":
        return "路况";
      case "WEATHER":
        return "天气";
      default:
        return input;
    }
  };
});

zongmu.filter("tagControlType", function() {
  return function(input) {
    switch (input) {
      case "DROPDOWNBOX":
        return "下拉框";
      case "CHECKBOX":
        return "复选框";
      case "RADIOBUTTON":
        return "单选";
      default:
        return input;
    }
  };
});

zongmu.filter("uploadType", function() {
  return function(input) {
    switch (input) {
      case "SINGLE":
        return "一路视频";
      case "FOUR":
        return "四路视频";
      case "PICTURE":
        return "图片";
      default:
        return input;
    }
  };
});

zongmu.filter("taskPriority", function() {
  return function(input) {
    switch (input) {
      case 1:
        return "高";
      case 2:
        return "中";
      case 3:
        return "低";
      default:
        return input;
    }
  };
});

zongmu.filter("taskStatus", function() {
  return function(input) {
    switch (input) {
      case "CUTTING":
        return "正在创建...";
      case "CUTTINGFAILURE":
        return "创建失败";
      case "CUTFINISHED":
        return "创建成功";
      default:
        return input;
    }
  };
});

zongmu.filter("payStatus", function() {
  return function(input) {
    switch (input) {
      case "PENDING":
        return "待支付";
      case "PAYED":
        return "已支付";
      default:
        return input;
    }
  };
});

zongmu.filter("taskItemStatus", function() {
  return function(input) {
    switch (input) {
      case "NEW":
        return "新任务";
      case "INPROGRESS":
        return "进行中";
      case "FINISHED":
        return "已结束";
      case "CREATEFAILED":
        return "视频处理失败";
      case "CUTTING":
        return "视频正在处理中...";
      case "REVIEWFAILED":
        return "审核失败";
      default:
        return input;
    }
  };
});

zongmu.filter("taskRecordStatus", function() {
  return function(input) {
    switch (input) {
      case "INPROGRESS":
        return "标注进行中";
      case "WAITTING":
        return "等待审核";
      case "REVIEWING":
        return "正在进行审核";
      case "ACCEPTED":
        return "审核通过";
      case "REJECTED":
        return "审核未通过";
      default:
        return input;
    }
  };
});

zongmu.filter("reviewRecordStatus", function() {
  return function(input) {
    switch (input) {
      case "WAITTING":
        return "待审核";
      case "INPROGRESS":
        return "正在进行审核";
      case "PASS":
        return "审核通过";
      case "FAILED":
        return "审核未通过";
      default:
        return input;
    }
  };
});

zongmu.filter("uploadStatus", function() {
  return function(input) {
    switch (input) {
      case "UPLOADING":
        return "上传中...";
      case "UPLOADFAILED":
        return "上传失败";
      case "UPLOADSUCCESS":
        return "上传成功";
      case "FTPUPLOADING":
        return "正在上传到FTP...";
      case "FTPUPLOADSUCCESS":
        return "上传到FTP成功";
      case "FTPUPLOADFAILED":
        return "上传到FTP失败";
      case "COMPRESSING":
        return "压缩中...";
      case "COMPRESSFAILED":
        return "压缩失败";
      case "COMPRESSSUCCESS":
        return "压缩成功";
      default:
        return input;
    }
  };
});

zongmu.filter("taskType", function() {
  return function(input) {
    switch (input) {
      case "PICTURE":
        return "图片";
      case "VIDEO":
        return "视频";
      default:
        return input;
    }
  };
});

zongmu.filter("shapeType", function() {
  return function(input) {
    switch (input) {
      case "RECT":
        return "矩形";
      case "POLYLINE":
        return "多边形";
      case "ANY":
        return "任意多边形";
      default:
        return input;
    }
  };
});

zongmu.filter("timeText", function() {

  return function(input) {
    if (input) {
      if (input < 60) {
        return input + " 分钟";
      } else {
        return (input / 60).toFixed(2) + "小时";
      }
    }

    return "--";
  };
});

zongmu.filter("minsToHour", function() {
  return function(input) {
    if (input) {
      return (input / 60).toFixed(2);
    } else {
      return 0;
    }
  };
});

zongmu.filter("timeString", function() {
  return function(val) {
    if (val === undefined || val == null) {
      return "";
    }
    if (val === 0) {
      return val;
    }

    if (val < 60) {
      return `${val.toFixed(0)}秒`;
    }

    var sec = val % 60;
    var min = null;
    if (sec === 0) {
      min = val / 60;
    } else {
      min = ((val - sec) / 60) % 60;
    }
    if (val < 60 * 60) {
      return `${min.toFixed(0)}分${sec.toFixed(0)}秒`;
    }

    var hour = val / 3600;
    return `${hour.toFixed(0)}时${min.toFixed(0)}分${sec.toFixed(0)}秒`;

    //  if(!val) {
    //    return val;
    //  }
    //
    //  val = val.toFixed(0);
    //
    //  if(val < 60) {
    //    return `${val}秒`;
    //  }
    //
    //  var sec = val % 60;
    //  var min = null;
    //  if(sec === 0) {
    //    min = val / 60;
    //  } else {
    //    min = (val - sec) / 60;
    //  }
    //  if(val < 60 * 60) {
    //    return `${min}分 ${sec}秒`;
    //  }
    //
    //  var hour = (val - min * 60 - sec) / 3600;
    //  return `${hour} 时${min}分 ${sec}秒`;
  };
});

zongmu.filter("fileSize", function() {

  return function(input) {
    var kSize = input / 1024;
    if (kSize < 1024) {
      return kSize.toFixed(2) + " KB";
    }

    var mSize = kSize / 1024;
    if (mSize < 1024) {
      return mSize.toFixed(2) + " MB";
    }

    var gSize = mSize / 1024;
    return gSize.toFixed(2) + " GB";
  };
});

zongmu.filter("joda", function() {

  return function(input) {
    if (typeof input === "number") {
      return new Date(input);
    }

    return input;
  };
});

zongmu.filter("video", ["mediaServiceUrl", function(mediaServiceUrl) {

  return function(input) {
    return mediaServiceUrl + input;
  };
}]);

zongmu.filter("taskFullName", function() {
  return function(task) {
    return task && task.taskName + "(第" + (task.orderNo + 1) + "个任务)";
  };
});

zongmu.filter("suffix", function() {
  return function(input, suffix) {
    if (input) {
      return input + suffix;
    }

    return input;
  }
});

zongmu.constant("webcontext", "/zongmu/dist/app");

zongmu.factory('formatService', function() {
  var huoyun = angular.module("huoyun-ui");

  return {

    deleteUnMarkShape: function(data) {
      var res = [];
      data.forEach(function(it) {
        if (it.groups && it.groups.length > 0) {
          res.push(it);
        }
      })
      return res;
    },

    convertToSingle: function(data, height, width) {
      data.forEach(function(it) {
        (it.groups || []).forEach(function(group) {
          (group.points || []).forEach(function(point) {
            point.x = ((point.x) * width) / (872);
            point.y = ((point.y) * height) / (450);
          });
        });
      });
    },

    convertToPicSingle: function(data, height, width) {
      data.forEach(function(it) {
        (it.groups || []).forEach(function(group) {
          (group.points || []).forEach(function(point) {
            point.x = ((point.x) * width) / (872);
            point.y = ((point.y) * height) / (500);
          });
        });
      });
    },

    convertBackPicSingle: function(data, height, width) {
      (data || []).forEach(function(shape) {
        Object.keys(shape.$$timeline.data).forEach(function(key) {
          (shape.$$timeline.data[key] || []).forEach(function(point) {
            point.x = (point.x * (872)) / width;
            point.y = (point.y * (500)) / height;
          });
        });
      });
    },

    convertBackSingle: function(data, height, width) {
      (data || []).forEach(function(shape) {
        Object.keys(shape.$$timeline.data).forEach(function(key) {
          (shape.$$timeline.data[key] || []).forEach(function(point) {
            point.x = (point.x * (872)) / width;
            point.y = (point.y * (450)) / height;
          });
        });
      });
    },

    convertToFour: function(data, height, width) {
      data.forEach(function(it) {
        (it.groups || []).forEach(function(group) {
          (group.points || []).forEach(function(point) {
            point.x = ((point.x) * width) / (420);
            point.y = ((point.y) * height) / (285);
          });
        });
      });
    },

    convertBackFour: function(shape, height, width) {
      Object.keys(shape.$$timeline.data).forEach(function(key) {
        (shape.$$timeline.data[key] || []).forEach(function(point) {
          point.x = (point.x * (420)) / width;
          point.y = (point.y * (285)) / height;
        });
      });
    },

    toTimelineDataModel: function(points, frameIndex) {
      var model = {
        frameIndex: frameIndex,
        points: []
      };

      angular.forEach(points || [], function(point, index) {
        model.points.push({
          x: point.x,
          y: point.y
        });
      });

      return model;
    },

    toTimelineViewModel: function(shape) {
      var $timeline = huoyun.newObject("Timeline");
      $timeline.startIndex = shape.startIndex;
      $timeline.endIndex = shape.endIndex;
      $timeline.data = [];

      angular.forEach(shape.groups || [], function(group, index) {
        $timeline.data[group.frameIndex] = [];
        angular.forEach(group.points || [], function(point, pointIndex) {
          var $point = huoyun.newObject("point", [point.x, point.y]);
          $timeline.data[group.frameIndex].push($point);
        });
      });

      return $timeline;
    },

    toShapeDataModel: function(shape) {
      var model = {
        shapeId: shape.shapeId,
        name: shape.name,
        color: shape.color,
        type: shape.type,
        sideCount: shape.count,
        startIndex: shape.$$timeline.startIndex,
        endIndex: shape.$$timeline.endIndex,
        timelines: [],
        tags: []
      };

      angular.forEach(shape.$$timeline.data || [], function(points, index) {
        var $timeline = this.toTimelineDataModel(points, index)
        model.timelines.push($timeline);
      }.bind(this));

      angular.forEach(shape.tags, function(tagItem, tagId) {
        model.tags.push({
          tagId: tagId,
          tagItemId: tagItem.id,
          tagItemValue: tagItem.value
        });
      });

      return model;
    },

    toShapeViewModel: function(shape) {
      var $shape = huoyun.newObject("Shape");
      $shape.id = shape.id;
      $shape.shapeId = shape.name;
      $shape.name = shape.name;
      $shape.count = shape.sideCount;
      $shape.color = shape.color;
      $shape.$$timeline = this.toTimelineViewModel(shape);
      if (shape.taskItem) {
        $shape.type = shape.taskItem.shapeType;
      }
      $shape.count = shape.sideCount;
      $shape.colorTag = shape.colorTag;
      shape.tags.forEach(function(it) {
        var tagItem = shape.tagsMap[it.tagItemId];
        if (tagItem) {
          if (tagItem.type === "DROPDOWNBOX") {
            $shape.tagDorpdownList[tagItem.tagId] = tagItem;
          } else if (tagItem.type === "CHECKBOX") {
            $shape.tagCheckBoxList[tagItem.id] = true;
          }
        }
      });
      return $shape;
    },

    toMarkDataModel: function(data) {
      //    var model = {
      //      taskItemFileMarks: []
      //    };
      //
      //    angular.forEach(data || [], function(taskItemFileMark, index) {
      //      var $taskItemFileMark = {
      //        taskItemFileNo: taskItemFileMark.taskItemFile.taskItemFileNo,
      //        markShapes: this.toMarkShapesDataModel(taskItemFileMark.markShapes)
      //      };
      //
      //      model.taskItemFileMarks.push($taskItemFileMark);
      //    }.bind(this));

      /*angular.forEach(shapes || [], function(shape, index) {
        var $shape = this.toShapeDataModel(shape);
        model.markShapes.push($shape);
      }.bind(this));*/

      return this.toMarkShapesDataModel(data);
    },

    getTags: function(shape) {
      var tags = [];
      if (shape.tagCheckBoxList) {
        Object.keys(shape.tagCheckBoxList).forEach(function(key) {
          if (shape.tagCheckBoxList[key]) {
            tags.push({
              tagItemId: key
            });
          }
        });
      }

      if (shape.tagDorpdownList) {
        Object.keys(shape.tagDorpdownList).map(function(it) {
          return {
            tagItemId: shape.tagDorpdownList[it].id
          };
        }).forEach(function(it) {
          tags.push(it);
        });
      }

      return tags;
    },

    getGroups: function(shape) {
      var that = this;

      var groups = [];
      Object.keys(shape.$$timeline.data).map(function(it) {
        var group = {
          frameIndex: it,
          points: shape.$$timeline.data[it]
        };

        if (shape.taskItemFile) {
          group.taskItemFileId = shape.taskItemFile.id;
        }

        return group;
      }).forEach(function(it) {
        groups.push(it);
      });
      return groups;
    },

    toMarkShapesDataModel: function(shapes) {
      var $shapes = [];
      angular.forEach(shapes || [], function(shape, index) {
        var $shape = {
          name: shape.shapeId,
          color: shape.color,
          startIndex: shape.$$timeline.startIndex,
          endIndex: shape.$$timeline.endIndex,
          groups: []
        };

        if (shape.colorTag) {
          $shape.colorTagId = shape.colorTag.id;
        }

        $shape.tags = this.getTags(shape);
        $shape.groups = this.getGroups(shape);

        $shapes.push($shape);
      }.bind(this));

      return $shapes;
    },

    toMarkViewModel: function(taskMarkRecords) {
      var that = this;
      var $shapes = [];
      taskMarkRecords.forEach(function(it, index) {
        $shapes.push(that.toShapeViewModel(it));
      });

      return $shapes;
    },

    toMarkShapesViewModel: function(shapes) {
      var $shapes = [];
      angular.forEach(shapes, function(shape, index) {
        var $shape = this.toShapeViewModel(shape);
        $shapes.push($shape);
      }.bind(this));

      return $shapes;
    }
  };
});