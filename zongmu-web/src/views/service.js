'use strict';

zongmu.factory("assetService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {

  var baseUrl = serviceUrl + "assets";

  return {
    createAsset: function(data) {
      var dtd = $q.defer();
      $http.post(baseUrl, data).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    deleteAsset: function(assetNo) {
      var dtd = $q.defer();
      $http.delete(`${baseUrl}/${assetNo}`).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getAssets: function(pageIndex, filter) {
      var url = baseUrl;
      if(pageIndex) {
        url = url + "?pageIndex=" + pageIndex;
      }

      if(filter) {
        if(pageIndex) {
          url += "&filter=" + filter;

        } else {
          url += "?filter=" + filter;
        }
      }

      var dtd = $q.defer();
      $http.get(url).success(function(pageData) {
        dtd.resolve(pageData);
      });
      return dtd.promise;
    },

    queryAssets: function(pageIndex, params) {
      var dtd = $q.defer();
      $http.post(`${baseUrl}/queryAssets?pageIndex=${pageIndex || 0}`, params)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    getAsset: function(assetNo) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + assetNo).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    upload: function(data) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/upload", data).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    compress: function(assetNo) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + assetNo + "/compress").success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getAssetTags: function() {
      var dtd = $q.defer();
      $http.get(serviceUrl + "/assetTags").success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    deleteAssetTag: function(assetTagId) {
      var dtd = $q.defer();
      $http.delete(serviceUrl + "/assetTags/" + assetTagId).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    addAssetTag: function(assetName) {
      var dtd = $q.defer();
      $http.post(serviceUrl + "/assetTags", {
        name: assetName
      }).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    setDefaultAssetTag: function(tagId) {
      var dtd = $q.defer();
      $http.post(serviceUrl + "/assetTags/" + tagId + "")
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    updateAssetTags: function(assetNo, tagParam) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + assetNo + "/updateTags", tagParam)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    updateAssetViewTags: function(assetNo, tagParam) {
      var dtd = $q.defer();
      $http.post(`${baseUrl}/${assetNo}/updateAssetViewTags`, tagParam)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    batchCreateAssetTags: function(data) {
      var dtd = $q.defer();
      $http.post(serviceUrl + "/assetTags/batchCreate", data)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    }

  };
}]);

zongmu.factory("reviewRecordService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {

  var baseUrl = serviceUrl + "reviewRecords";

  return {
    queryReviewRecords: function(pageIndex, params) {
      var url = baseUrl + "/queryReviewRecords";
      if(pageIndex) {
        url = url + "?pageIndex=" + pageIndex;
      }
      var dtd = $q.defer();
      $http.post(url, params).success(function(pageData) {
        dtd.resolve(pageData);
      });
      return dtd.promise;
    },

    getMyReviewRecords: function(pageIndex, status) {
      var url = baseUrl + "/my?status=" + status;
      if(pageIndex) {
        url = url + "&pageIndex=" + pageIndex;
      }
      var dtd = $q.defer();
      $http.get(url).success(function(pageData) {
        dtd.resolve(pageData);
      });
      return dtd.promise;
    },

    getReviewRecord: function(reviewRecordNo) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + reviewRecordNo).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    startReview: function(reviewRecordNo) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + reviewRecordNo + "/startReview").success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    newTask: function(reviewRecordNo) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + reviewRecordNo + "/new").success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    batchReviewPass: function(reviewRecordNos) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/batchReviewPass", reviewRecordNos)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    batchReviewFailed: function(data) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/batchReviewFailed", data)
        .success(function(ret) {
          dtd.resolve(ret);
        });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("taskRecordService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "taskRecords";
  return {
    getTaskRecord: function(taskRecordNo) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + taskRecordNo).success(function(data) {
        dtd.resolve(data);
      });

      return dtd.promise;
    },

    search: function(pageIndex, param) {
      var dtd = $q.defer();
      var url = `${baseUrl}/search`;
      if(pageIndex !== undefined && pageIndex !== null) {
        url += "?pageIndex=" + pageIndex;
      }
      $http.post(url, param)
        .success(function(data) {
          dtd.resolve(data);
        });

      return dtd.promise;
    },

    getMyTaskRecords: function(pageIndex, status) {
      var dtd = $q.defer();
      var url = baseUrl + "/my?status=" + status;
      if(pageIndex) {
        url += "&pageIndex=" + pageIndex;
      }
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    cancelTask: function(taskRecordNo) {
      var dtd = $q.defer();
      var url = baseUrl + "/" + taskRecordNo + "/cancel";
      $http.post(url).success(function(data) {
        dtd.resolve(data);
      });

      return dtd.promise;
    },

    saveTaskMarks: function(taskRecordNo, data) {
      var dtd = $q.defer();
      var url = baseUrl + "/" + taskRecordNo + "/taskMarks";
      $http.post(url, {
        "taskMarkRecords": data
      }).success(function(data) {
        dtd.resolve(data);
      });

      return dtd.promise;
    },

    getAlgorithm: function(taskRecordNo) {
      var dtd = $q.defer();
      var url = baseUrl + "/" + taskRecordNo + "/algorithm";
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });

      return dtd.promise;
    },

    getTaskMarks: function(taskRecordNo, status) {
      var dtd = $q.defer();
      var url = baseUrl + "/" + taskRecordNo + "/taskMarks";
      if(status !== undefined) {
        url += "?status=" + status;
      }
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });

      return dtd.promise;
    },

    finishMark: function(taskRecordNo) {
      var dtd = $q.defer();
      var url = baseUrl + "/" + taskRecordNo + "/finish";
      $http.post(url).success(function(data) {
        dtd.resolve(data);
      });

      return dtd.promise;
    },

    reviewPass: function(taskRecordNo) {
      var dtd = $q.defer();
      var url = baseUrl + "/" + taskRecordNo + "/accept";
      $http.post(url, "test").success(function(data) {
        dtd.resolve(data);
      });

      return dtd.promise;
    },

    reviewFail: function(taskRecordNo, rejectReason) {
      var dtd = $q.defer();
      var url = baseUrl + "/" + taskRecordNo + "/reject";
      $http.post(url, rejectReason).success(function(data) {
        dtd.resolve(data);
      });

      return dtd.promise;
    }
  };
}]);

zongmu.factory("tagService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "tags";
  return {
    getTags: function() {
      var dtd = $q.defer();
      $http.get(baseUrl).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    createTag: function(data) {
      var dtd = $q.defer();
      $http.post(baseUrl, data).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    updateTag: function(tagId, data) {
      var dtd = $q.defer();
      $http.patch(baseUrl + "/" + tagId, data).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    deleteTag: function(tagId) {
      var dtd = $q.defer();
      $http.delete(baseUrl + "/" + tagId).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    addTagItem: function(tagId, value) {
      return $http.post(baseUrl + "/" + tagId, {
        value: value
      });
    },

    batchAddTagItem: function(tagId, value) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + tagId + "/batchAdd", value).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    setTagDefaultValue: function(tagId, tagItem) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + tagId + "/setDefaultValues", tagItem).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    setDefaultValue: function(tagId, tagItems) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + tagId + "/setDefaultValues", tagItems).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    setMultiDefaultValues: function(tagId, tagItems) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + tagId + "/setMultiDefaultValues", tagItems).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    deleteTagItem: function(tagItemId) {
      return $http.delete(baseUrl + "/deleteTagItem/" + tagItemId);
    },

    getTag: function(tagId) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + tagId).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("taskService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "tasks";

  return {
    createTask: function(task) {
      var dtd = $q.defer();
      return $http.post(baseUrl, task).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    queryTasks: function(pageIndex, param) {
      if(pageIndex == undefined || pageIndex == null) {
        pageIndex = 0;
      }
      var dtd = $q.defer();
      $http.post(`${baseUrl}/queryTasks?pageIndex=${pageIndex}`, param)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    deleteTask: function(taskNo) {
      var dtd = $q.defer();
      $http.delete(`${baseUrl}/${taskNo}`).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getTasksByAssetNo: function(assetNo) {
      var dtd = $q.defer();
      $http.get(serviceUrl + "/getTasksByAssetNo?assetNo=" + assetNo).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getTasks: function(pageIndex, filter) {
      var dtd = $q.defer();
      var url = baseUrl;
      if(pageIndex) {
        url += "?pageIndex=" + pageIndex;
      }

      if(filter) {
        if(pageIndex) {
          url += "&filter=" + filter;

        } else {
          url += "?filter=" + filter;
        }
      }

      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getTask: function(taskNo, pageIndex) {
      var dtd = $q.defer();
      var url = baseUrl + "/" + taskNo;
      if(pageIndex) {
        url += "?pageIndex=" + pageIndex;
      }
      $http.get(url)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    acceptTask: function(taskItemNo) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + taskItemNo + "/accept")
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    batchAcceptTasks: function(taskItemNos) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/batchAccept", taskItemNos)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    getTaskDetail: function(taskItemNo) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + taskItemNo + "/detail")
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },
    setTop: function(taskNo, top) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + taskNo + "/top", top)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    setShow: function(taskNo, show) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + taskNo + "/show", show)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    setPriority: function(taskNo, priority) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + taskNo + "/priority", priority)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    updateAssetTaskTags: function(taskItemNo, params) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + taskItemNo + "/updateTaskTags", params)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    updateAssetTags: function(taskNo, tagIds) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + taskNo + "/updateTags", {
        tagIds: tagIds
      }).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    updateViewTags: function(taskItemNo, data) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + taskItemNo + "/updateViewTags", data)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("rejectReasonService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "rejectReasons";

  return {
    getReasons: function() {
      var dtd = $q.defer();
      $http.get(baseUrl).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    createReason: function(description) {
      var dtd = $q.defer();
      $http.post(baseUrl, {
        description: description
      }).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    deleteReason: function(reasonId) {
      var dtd = $q.defer();
      $http.delete(baseUrl + "/" + reasonId).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getReason: function(reasonId) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + reasonId).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    setDefault: function(reasonId) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + reasonId + "/default")
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("colorTagService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "colorTags";

  return {
    getColorTags: function() {
      var dtd = $q.defer();
      $http.get(baseUrl).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    createColorTag: function(colorTag) {
      var dtd = $q.defer();
      $http.post(baseUrl, colorTag)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    updateColorTag: function(id, colorTag) {
      var dtd = $q.defer();
      $http.put(baseUrl + "/" + id, colorTag).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    deleteColorTag: function(id) {
      var dtd = $q.defer();
      $http.delete(baseUrl + "/" + id).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("trainService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "trains";

  return {
    getTrains: function() {
      var dtd = $q.defer();
      $http.get(baseUrl).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getTrain: function(id) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + id).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    createTrain: function(train) {
      var dtd = $q.defer();
      $http.post(baseUrl, train).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    updateTrain: function(id, train) {
      var dtd = $q.defer();
      $http.put(baseUrl + "/" + id, train).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    deleteTrain: function(id) {
      var dtd = $q.defer();
      $http.delete(baseUrl + "/" + id).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("reportService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "report";

  return {
    getBsdReport: function(algorithmId, from, to) {
      var dtd = $q.defer();
      var url = baseUrl + "/bsd?algorithmId=" + algorithmId + "&from=" + from + "&to=" + to;
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },
    getNewBsdReport: function(algorithmId, assetViewItemIds) {
      var dtd = $q.defer();
      var url = `${baseUrl}/newBSD?algorithmId=${algorithmId}`;
      if(assetViewItemIds) {
        url = `${url}&assetViewItemIds=${assetViewItemIds}`;
      }
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    search: function(algorithmId, searchParams) {
      var dtd = $q.defer();
      var url = `${baseUrl}/search/${algorithmId}`;
      $http.post(url, searchParams).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("exportService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "export";

  return {
    exportTask: function(assetNo, taskNo) {
      var dtd = $q.defer();
      var url = `${baseUrl}/new/assets/${assetNo}/${taskNo}`;
      $http.post(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("algorithmService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "algorithms";

  return {
    getAlgorithms: function() {
      var dtd = $q.defer();
      $http.get(baseUrl).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getAlgorithm: function(id) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + id).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    createAlgorithm: function(algorithm) {
      var dtd = $q.defer();
      $http.post(baseUrl, algorithm).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    updateAlgorithm: function(id, algorithm) {
      var dtd = $q.defer();
      $http.put(baseUrl + "/" + id, algorithm).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    setTags: function(id, algorithm) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + id + "/tags", algorithm).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    deleteAlgorithm: function(id) {
      var dtd = $q.defer();
      $http.delete(baseUrl + "/" + id).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("colorGroupService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "colorGroups";

  return {
    create: function(group) {
      var dtd = $q.defer();
      $http.post(baseUrl, group).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    update: function(id, group) {
      var dtd = $q.defer();
      $http.patch(baseUrl + "/" + id, group).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("userPointService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "points";

  return {
    getMyPoints: function(pageIndex) {
      var dtd = $q.defer();
      var url = baseUrl + "/my";
      if(pageIndex) {
        url = url + "?pageIndex=" + pageIndex;
      }
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getAvailablePoint: function() {
      var dtd = $q.defer();
      $http.get(baseUrl + "/my/avaiablepoint").success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    }
  }
}]);

zongmu.factory("assetViewTagService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "assetViewTags";

  return {
    create: function(data) {
      var dtd = $q.defer();
      $http.post(baseUrl, data)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    update: function(id, data) {
      var dtd = $q.defer();
      $http.put(`${baseUrl}/${id}`, data)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    getAll: function() {
      var dtd = $q.defer();
      $http.get(baseUrl)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    deleteAssetViewTag: function(id) {
      var dtd = $q.defer();
      $http.delete(baseUrl + "/" + id)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    getViewTag: function(id) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + id)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    batchCreateItems: function(data) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/batchCreateItems", data)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    setItemDefault: function(id) {
      var dtd = $q.defer();
      $http.post(`${baseUrl}/items/${id}/default`)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    deleteTagItem: function(id) {
      var dtd = $q.defer();
      $http.delete(`${baseUrl}/items/${id}`)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("viewTagService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "viewtags";

  return {
    create: function(data) {
      var dtd = $q.defer();
      $http.post(baseUrl, data)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    getViewTag: function(id) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + id)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    getAllViewTags: function() {
      var dtd = $q.defer();
      $http.get(baseUrl + "/listAll")
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    batchCreateItems: function(id, data) {
      var dtd = $q.defer();
      $http.post(`${baseUrl}/${id}/batchCreate`, data)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },
    update: function(id, data) {
      var dtd = $q.defer();
      $http.put(baseUrl + "/" + id, data)
        .success(function(res) {
          dtd.resolve(res);
        });
      return dtd.promise;
    },

    delete: function(id) {
      var dtd = $q.defer();
      $http.delete(baseUrl + "/" + id)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    getViewTags: function(algorithmId) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/list/" + algorithmId)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    deleteTagItem: function(id) {
      var dtd = $q.defer();
      $http.delete(`${baseUrl}/items/${id}`)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    }
  };
}]);

zongmu.factory("payService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl + "pays";

  return {
    getList: function(pageIndex, tabIndex) {
      var url = baseUrl + "?payStatus=" + tabIndex;
      if(pageIndex) {
        url = url + "&pageIndex=" + pageIndex;
      }
      var dtd = $q.defer();
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getPay: function(id) {
      var dtd = $q.defer();
      $http.get(baseUrl + "/" + id)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    requestPay: function(data) {
      var dtd = $q.defer();
      $http.post(baseUrl, data)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    pay: function(id, data) {
      var dtd = $q.defer();
      $http.post(baseUrl + "/" + id + "/pay", data)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    }
  }
}]);

zongmu.factory("userService", ["$q", "$http", "serviceUrl", function($q, $http, serviceUrl) {
  var baseUrl = serviceUrl;

  return {
    getUserList: function(pageIndex, role) {
      var url = baseUrl + "/users?role=" + role;
      if(pageIndex) {
        url = url + "&pageIndex=" + pageIndex;
      }
      var dtd = $q.defer();
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getBlackUserList: function(pageIndex) {
      var url = baseUrl + "/black/users";
      if(pageIndex) {
        url = url + "?pageIndex=" + pageIndex;
      }
      var dtd = $q.defer();
      $http.get(url)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    addBlackList: function(userId) {
      var url = baseUrl + "/black/" + userId + "/add";
      var dtd = $q.defer();
      $http.post(url)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    removeBlackList: function(userId) {
      var url = baseUrl + "/black/" + userId + "/remove";
      var dtd = $q.defer();
      $http.post(url)
        .success(function(data) {
          dtd.resolve(data);
        });
      return dtd.promise;
    },

    getUser: function(id) {
      var url = baseUrl + "/users/" + id;
      var dtd = $q.defer();
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    setUserRole: function(id, role) {
      var url = baseUrl + "/users/" + id + "/role";
      var dtd = $q.defer();
      $http.post(url, {
        role: role
      }).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    register: function(param) {
      var dtd = $q.defer();
      var url = baseUrl + "/register";
      $http.post(url, param).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    login: function(param) {
      var dtd = $q.defer();
      var url = baseUrl + "/login";
      $http.post(url, param).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    forgetPassword: function(param) {
      var dtd = $q.defer();
      var url = baseUrl + "/forgetPassword";
      $http.post(url, param).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    resetPassword: function(param) {
      var dtd = $q.defer();
      var url = baseUrl + "/resetPassword";
      $http.post(url, param).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    forgetResetPassword: function(param) {
      var dtd = $q.defer();
      var url = baseUrl + "/forgetResetPassword";
      $http.post(url, param).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    updateProfile: function(param) {
      var dtd = $q.defer();
      var url = baseUrl + "/profile";
      $http.post(url, param).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    getProfile: function() {
      var dtd = $q.defer();
      var url = baseUrl + "/profile";
      $http.get(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    logout: function() {
      var dtd = $q.defer();
      var url = baseUrl + "/logout";
      $http.post(url).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    active: function(param) {
      var dtd = $q.defer();
      var url = baseUrl + "/active";
      $http.post(url, param).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    },

    reactive: function(param) {
      var dtd = $q.defer();
      var url = baseUrl + "/reactive";
      $http.post(url, param).success(function(data) {
        dtd.resolve(data);
      });
      return dtd.promise;
    }
  };
}]);