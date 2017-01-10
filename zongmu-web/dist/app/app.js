'use strict';

var zongmu = angular.module("zongmu", ["huoyun-ui", "ngDialog"]);
zongmu.constant("serviceUrl", "/service/");
zongmu.constant("mediaServiceUrl", "http://118.178.195.17:8083/");
//"aliyun"
zongmu.constant("appEnv", "aliyun");

zongmu.config(["applicationProvider", "navProvider", "pageProvider", "footbarProvider",
  "$httpProvider", "$logProvider", "httpErrorProvider", "serviceUrl",
  function(applicationProvider, navProvider, pageProvider, footbarProvider,
    $httpProvider, $logProvider, httpErrorProvider, serviceUrl) {
    setAppConfig();
    setPageConfig();
    setNavBarConfig();
    setFootbarConfig();
    setHttpConfig();
    setHttpErrorHanldeConfig();

    function setDebugConfig() {
      $logProvider.debugEnabled(true);
    }

    function setAppConfig() {
      applicationProvider.setName("纵目真值标注系统");
      applicationProvider.loginFunc = function() {
        var loginUrl = serviceUrl + "page/user/login.html";
        if(window.location.pathname !== loginUrl) {
          window.location.href = loginUrl;
        }
      };

      applicationProvider.registerFunc = function() {
        var registerUrl = serviceUrl + "page/user/register.html";
        if(window.location.pathname !== registerUrl) {
          window.location.href = registerUrl;
        }
      };

      applicationProvider.logoutFunc = function(userService) {
        this.logout().then(function() {
          applicationProvider.setLogin(false);
          window.location.href = serviceUrl + "page/user/login.html";
        }.bind(this));
      };
    }

    function setPageConfig() {
      // 设置页面宽度
      pageProvider.setPageWidth("1024px");
    }

    function setNavBarConfig() {
      navProvider.setItems([{
        name: "home",
        text: "任务大厅",
        href: serviceUrl + "page/home/index.html"
      }, {
        name: "mytask",
        text: "我的任务",
        href: serviceUrl + "page/task/index.html"
      }, {
        name: "train",
        text: "训练中心",
        href: serviceUrl + "page/train/index.html"
      }, {
        name: "setting",
        text: "设置",
        visibility: false,
        href: serviceUrl + "page/setting/asset.view.tag.html"
      }, {
        name: "help",
        text: "帮助中心",
        href: serviceUrl + "page/help/index.html"
      }]);
    }

    function setFootbarConfig() {
      footbarProvider.setUsefulLinks([{
        text: "关于系统",
        href: "/"
      }, {
        text: "联系我们",
        href: "/"
      }, {
        text: "帮助",
        href: "/"
      }]);
      footbarProvider.setCopyRight("2014-2016");
      footbarProvider.setCompanyName("纵目科技有限公司");
      footbarProvider.setRecordInfo("沪公安备09004260号");
    }

    function setHttpConfig() {
      $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
      $httpProvider.defaults.headers.common.Accept = "application/json";
      $httpProvider.defaults.headers.get = {
        "Content-Type": "application/json"
      };
      $httpProvider.defaults.headers.post = {
        "Content-Type": "application/json"
      };

      $httpProvider.interceptors.push("ajaxInterceptor");
    }

    function setHttpErrorHanldeConfig() {
      httpErrorProvider.setHandle(httpErrorHandle);
    }

    function httpErrorHandle(res) {
      var dialog = httpErrorProvider.getDialog();
      $(".widgets-loading-container").css("display", "none");
      if(res && res.status) {
        if([401,405].indexOf(res.status) !== -1) {
          applicationProvider.setLogin(false);
          window.location.href = serviceUrl + "page/user/login.html";
        } else {
          if(res.data && res.data.message) {
            dialog.showError(res.data.message);
          } else {
            dialog.showError("系统错误！");
          }
        }
      } else {
        alert("系统错误！")
      }
    }
  }
]);

zongmu.controller("appController", ["$scope", 'breadCrumb', "dialog", "httpError", "application", "userService", "nav",

  function($scope, breadCrumbProvider, dialog, httpErrorProvider, applicationProvider, userService, navProvider) {
    initView();

    httpErrorProvider.setDialog(dialog);
    applicationProvider.setUserService(userService);

    $scope.setTitle = function(title) {
      $scope.title = title;
    };

    $scope.showLoading = function() {
      $scope.loading = true;
    };

    $scope.hideLoading = function() {
      $scope.loading = false;
    };

    $scope.setLoadingText = function(val) {
      $scope.loadingText = val;
    };

    function initView() {
      var items = navProvider.getItems();
      var role = Cookies.get("role");
      if(role === "ADMIN") {
        items.forEach(function(it) {
          if(["setting"].indexOf(it.name) !== -1) {
            it.visibility = true;
          }
        });
      }
    }
  }
]);
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
'use strict';

zongmu.factory('enumService', function() {
  return {
    getHours: function() {
      var hours = [];
      for(var index = 0; index <= 24; index++) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    switch(input) {
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
    if(input) {
      if(input < 60) {
        return input + " 分钟";
      } else {
        return(input / 60).toFixed(2) + "小时";
      }
    }

    return "--";
  };
});

zongmu.filter("minsToHour", function() {
  return function(input) {
    if(input) {
      return(input / 60).toFixed(2);
    } else {
      return 0;
    }
  };
});

zongmu.filter("timeString", function() {
  return function(val) {
    if(val === undefined || val == null) {
      return "";
    }
    if(val === 0) {
      return val;
    }

    if(val < 60) {
      return `${val.toFixed(0)}秒`;
    }

    var sec = val % 60;
    var min = null;
    if(sec === 0) {
      min = val / 60;
    } else {
      min = ((val - sec) / 60) % 60;
    }
    if(val < 60 * 60) {
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
    if(kSize < 1024) {
      return kSize.toFixed(2) + " KB";
    }

    var mSize = kSize / 1024;
    if(mSize < 1024) {
      return mSize.toFixed(2) + " MB";
    }

    var gSize = mSize / 1024;
    return gSize.toFixed(2) + " GB";
  };
});

zongmu.filter("joda", function() {

  return function(input) {
    if(typeof input === "number") {
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
    if(input) {
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
        if(it.groups && it.groups.length > 0) {
          res.push(it);
        }
      })
      return res;
    },

    convertToSingle: function(data,height,width) {
      data.forEach(function(it) {
        (it.groups || []).forEach(function(group) {
          (group.points || []).forEach(function(point) {
            point.x = ((point.x) * width) / (872);
            point.y = ((point.y) * height) / (450);
          });
        });
      });
    },

    convertBackSingle: function(data,height,width) {
      (data || []).forEach(function(shape) {
        Object.keys(shape.$$timeline.data).forEach(function(key) {
          (shape.$$timeline.data[key] || []).forEach(function(point) {
            point.x = (point.x * (872)) / width;
            point.y = (point.y * (450)) / height;
          });
        });
      });
    },

    convertToFour: function(data,height,width) {
      data.forEach(function(it) {
        (it.groups || []).forEach(function(group) {
          (group.points || []).forEach(function(point) {
            point.x = ((point.x) * width) / (420);
            point.y = ((point.y) * height) / (285);
          });
        });
      });
    },

    convertBackFour: function(shape,height,width) {
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
      if(shape.taskItem) {
        $shape.type = shape.taskItem.shapeType;
      }
      $shape.count = shape.sideCount;
      $shape.colorTag = shape.colorTag;
      shape.tags.forEach(function(it) {
        var tagItem = shape.tagsMap[it.tagItemId];
        if(tagItem) {
          if(tagItem.type === "DROPDOWNBOX") {
            $shape.tagDorpdownList[tagItem.tagId] = tagItem;
          } else if(tagItem.type === "CHECKBOX") {
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
      if(shape.tagCheckBoxList) {
        Object.keys(shape.tagCheckBoxList).forEach(function(key) {
          if(shape.tagCheckBoxList[key]) {
            tags.push({
              tagItemId: key
            });
          }
        });
      }

      if(shape.tagDorpdownList) {
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

        if(shape.taskItemFile) {
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

        if(shape.colorTag) {
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
'use strict';
/*
 * 首页 - 任务中心
 */
zongmu.controller("taskCenterController", ['$scope', 'taskService', 'dialog', 'enumService', 'assetService',
  'algorithmService', "assetViewTagService", "viewTagService", "markUtil",
  function($scope, taskService, dialog, enumService, assetService, algorithmService, assetViewTagService, viewTagService, markUtil) {
    var pageIndex = $.url().param("pageIndex");
    var filter = $.url().param("filter");
    var role = Cookies.get("role");
    $scope.isAdmin = role === "ADMIN";
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;

    function QueryParams() {
      this.taskName = null;
      this.taskItemNo = null;
      this.assetName = null;
      this.assetNo = null;

      this.point = {
        op: null,
        value: null
      };
      this.createDate = {
        from: null,
        to: null
      };
      this.uploadDate = {
        from: null,
        to: null
      };
      this.videoLength = {
        op: null,
        value: null
      };

      this.taskFinishDate = {
        from: null,
        to: null
      };

      this.videoRecordDate = {
        from: null,
        to: null
      };

      init();

      function init() {
        $scope.uploadTypes.forEach(function(it) {
          it.checked = false;
        });

        ($scope.algorithms || []).forEach(function(it) {
          it.checked = false;
        });

        $scope.taskStatus.forEach(function(it) {
          it.checked = false;
        });

        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });

        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });
      }

      this.getData = function() {

        this.assetTypes = [];
        $scope.uploadTypes.forEach(function(it) {
          if(it.checked) {
            this.assetTypes.push(it.name);
          }
        }.bind(this));

        this.algorithmIds = [];
        ($scope.algorithms || []).forEach(function(it) {
          if(it.checked) {
            this.algorithmIds.push(it.id);
          }
        }.bind(this));

        this.taskItemStatus = [];
        if($scope.isAdmin) {
          $scope.taskStatus.forEach(function(it) {
            if(it.checked) {
              this.taskItemStatus.push(it.name);
            }
          }.bind(this));
        } else {
          this.taskItemStatus.push('NEW');
        }

        this.assetViewTagItemIds = [];
        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.assetViewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        this.viewTagItemIds = [];
        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.viewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        return this;
      };
    }

    $scope.uploadTypes = enumService.getUploadTypes();
    $scope.taskStatus = enumService.getTaskStatus();
    $scope.ops = enumService.getOps();
    $scope.queryParams = new QueryParams();

    algorithmService.getAlgorithms()
      .then(function(data) {
        $scope.algorithms = data;
      });

    assetViewTagService.getAll()
      .then(function(tags) {
        $scope.viewTags = tags;
      });

    viewTagService.getAllViewTags()
      .then(function(viewTags) {
        $scope.taskViewTags = viewTags;
      });

    initView();
    initData();
    //getAssetTags();

    function initView() {
      $scope.setTitle("任务大厅");
      $scope.expandSearch = false;
      $scope.s = {};
      $scope.taskTypes = enumService.getTaskTypes();
      $scope.taskStatuses = enumService.getTaskStatus();
      $scope.ops = enumService.getOps();
      $scope.table = {
        columns: [{
          name: "op-first",
          text: "选择"
        }, {
          name: "src",
          text: "任务"
        }, {
          name: "taskItemNo",
          text: "任务编号"
        }, {
          name: "taskName",
          text: "任务名称"
        }, {
          name: "taskType",
          text: "任务类型"
        }, {
          name: "point",
          text: "奖励金币"
        }, {
          name: "createTime",
          text: "创建时间"
        }, {
          name: "status",
          text: "状态"
        }]
      };
    }

    $scope.$watch("table.selectedAll", function() {

      if($scope.table.dataset && $scope.table.dataset.length > 0) {
        $scope.table.dataset.forEach(function(it) {
          it.isSelected = $scope.selectedAll;
        });
      }
    });

    $scope.onSelectedAll = function() {
      setSelectedAll(true);
    };

    $scope.onUnSelectedAll = function() {
      setSelectedAll(false);
    };

    function setSelectedAll(isSelected) {
      if($scope.table.dataset && $scope.table.dataset.length > 0) {
        $scope.table.dataset.forEach(function(it) {
          if(it.status === 'NEW') {
            it.isSelected = isSelected;
          }
        });
      }
    }

    $scope.onBatchTaskButtonClick = function() {
      $scope.enableBatch = true;
    };

    $scope.onCancelBatchTaskButtonClick = function() {
      $scope.enableBatch = false;
    };

    $scope.onSaveBatchTaskButtonClick = function() {

      var taskItemNos = [];
      $scope.table.dataset.forEach(function(it) {
        if(it.isSelected) {
          taskItemNos.push(it.taskItemNo);
        }
      });

      if(taskItemNos.length === 0) {
        dialog.showError("请选择任务!");
        return;
      }

      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍后...");
      taskService.batchAcceptTasks(taskItemNos)
        .then(function(data) {
          $scope.enableBatch = false;
          $scope.hideLoading();
          dialog.showInfo("批量接受任务成功！")
            .then(function() {
              initData();
            });
        });
    };

    $scope.tagMap = {};

    function getAssetTags() {
      assetService.getAssetTags()
        .then(function(tags) {
          $scope.tagMap = {};
          $scope.tags = tags;
          $scope.weatherTags = [];
          $scope.roadTags = [];
          $scope.tags.forEach(function(it) {
            $scope.tagMap[it.id] = it;
            if(it.category === 'ROAD') {
              $scope.roadTags.push(it);
            } else if(it.category === 'WEATHER') {
              $scope.weatherTags.push(it);
            }
          });

        });
    }

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    $scope.$on("tableIndexChanged", function(paginationScope, pageIndex) {
      load(pageIndex);
    });

    $scope.onAcceptTaskClick = function(taskItemNo) {
      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍后...");
      taskService.acceptTask(taskItemNo)
        .then(function(taskItem) {
          $scope.hideLoading();
          dialog.showInfo("已接受接受该任务！")
            .then(function() {
              var pageName = markUtil.getMarkPageUrl(taskItem.assetType, taskItem.taskType, taskItem.taskRecordNo);
              window.location.href = `../mark/${pageName}`;
              //            if(taskItem.assetType === 'SINGLE') {
              //              window.location.href = "../mark/video.html?taskRecordNo=" + taskItem.taskRecordNo;
              //            } else if(taskItem.assetType === 'FOUR') {
              //              window.location.href = "../mark/video.four.html?taskRecordNo=" + taskItem.taskRecordNo;
              //            } else if(taskItem.assetType === 'PICTURE') {
              //              window.location.href = "../mark/pic.html?taskRecordNo=" + taskItem.taskRecordNo;
              //            }
            });
        });
    };

    function initData() {
      //    $scope.showLoading();
      //    $scope.setLoadingText("正在加载，请稍后...");
      //    taskService.getTasks(pageIndex, filter).then(function(result) {
      //      $scope.hideLoading();
      //      $scope.table.dataset = result.content;
      //      $scope.table.pageData = {
      //        totalPage: result.totalPages,
      //        pageIndex: result.number
      //      };
      //    });
      load();
    }

    function load(index) {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍后...");
      //    taskService.getTasks(index, filter).then(function(result) {
      //      $scope.hideLoading();
      //      $scope.table.dataset = result.content;
      //      $scope.table.pageData = {
      //        totalPage: result.totalPages,
      //        pageIndex: result.number
      //      };
      //    });

      taskService.queryTasks(index, $scope.queryParams.getData()).then(function(result) {
        $scope.hideLoading();
        $scope.table.dataset = result.content;
        $scope.table.pageData = {
          totalPage: result.totalPages,
          pageIndex: result.number
        };
      });
    }

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
      //    $scope.s = {
      //      name: null,
      //      taskType: null,
      //      pointOp: null,
      //      point: null,
      //      status: null,
      //      createTimeFrom: null,
      //      createTimeTo: null
      //    };
      //
      //    ($scope.tags || []).forEach(function(tag) {
      //      tag.isSelected = false;
      //    });
    };

    $scope.onSearchButtonClick = function() {
      load();
      //    filter = "";
      //    if($scope.s.name) {
      //      filter += "taskName like " + $scope.s.name + ";";
      //    }
      //
      //    if($scope.s.taskType) {
      //      filter += "taskType eq " + $scope.s.taskType + ";";
      //    }
      //
      //    if($scope.s.pointOp && $scope.s.point) {
      //      filter += "point " + $scope.s.pointOp + " " + $scope.s.point + ";";
      //    }
      //
      //    if($scope.s.status) {
      //      filter += "status eq " + $scope.s.status + ";";
      //    }
      //
      //    if($scope.s.createTimeFrom && $scope.s.createTimeTo && $scope.s.createTimeFrom > $scope.s.createTimeTo) {
      //      dialog.showError("开始时间应小于结束时间！");
      //      return;
      //    }
      //
      //    if($scope.s.createTimeFrom) {
      //      filter += "createTimeFrom eq " + $scope.s.createTimeFrom.toISOString() + ";";
      //    }
      //
      //    if($scope.s.createTimeTo) {
      //      filter += "createTimeTo eq " + $scope.s.createTimeTo.toISOString();
      //    }
      //
      //    //    if($scope.s.createTimeOp && $scope.s.createTime) {
      //    //      filter += "createTime " + $scope.s.createTimeOp + " " + $scope.s.createTime.toISOString() + ";";
      //    //    }
      //
      //    var roadTags = [];
      //    var weatherTags = [];
      //    ($scope.tags || []).forEach(function(it) {
      //      if(it.isSelected) {
      //        if(it.category === 'ROAD') {
      //          roadTags.push(it.id);
      //        } else if(it.category === 'WEATHER') {
      //          weatherTags.push(it.id);
      //        }
      //      }
      //    });
      //
      //    if(roadTags.length > 0) {
      //      filter += "roadTagIds in " + JSON.stringify(roadTags) + ";";
      //    }
      //
      //    if(weatherTags.length > 0) {
      //      filter += "weatherTagIds in " + JSON.stringify(weatherTags) + ";";
      //    }
      //
      //    $scope.showLoading();
      //    $scope.setLoadingText("正在搜索，请稍后...");
      //    taskService.getTasks(pageIndex, filter).then(function(result) {
      //      $scope.hideLoading();
      //      $scope.table.dataset = result.content;
      //      $scope.table.pageData = {
      //        totalPage: result.totalPages,
      //        pageIndex: result.number
      //      };
      //    });
    }
  }
]);
'use strict';

zongmu.controller("helpController", ["$scope", function($scope) {
  initView();

  function initView() {
    $scope.setTitle("帮助中心");
  }
}]);
'use strict';

zongmu.controller("attrsDialogController", ['$scope', 'dialog', 'tagService',
  function($scope, dialog, tagService) {
    var params = $scope.ngDialogData;
    initData();

    function initData() {
      $scope.data = {
        color: params.shapeObj.color,
        attrs: {}
      };

      tagService.getTags()
        .then(function(res) {
          $scope.tags = res.data;
          initAttrs();
        });
    }

    $scope.onOkClick = function() {
      $scope.closeThisDialog({
        key: 'ok',
        result: [params.shapeObj, $scope.data.color, getAttrs()]
      });
    };

    function getAttrs() {
      var attrs = [];
      angular.forEach($scope.tags, function(tag, index) {
        var select = $scope.data.attrs[tag.name];
        if (select) {
          attrs.push({
            id: tag.id,
            name: tag.name,
            valueId: select.id,
            value: select.name
          });
        }
      });

      return attrs;
    }

    function initAttrs() {
      angular.forEach(params.shapeObj.attrs || [], function(attr, index) {
        $scope.data.attrs[attr.name] = {
          id: attr.valueId,
          value: attr.name
        };
      })
    }
  }
]);
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
'use strict';

zongmu.controller("markFourPicController", ['$q', '$scope', 'dialog', 'taskRecordService',
  'tagService', '$log', 'formatService', 'reviewRecordService', 'mediaServiceUrl', 'colorTagService', "markUtil", "breadCrumb",

  function($q, $scope, dialog, taskRecordService, tagService,
    $log, formatService, reviewRecordService, mediaServiceUrl, colorTagService, markUtil, breadCrumbProvider) {
    var huoyun = angular.module("huoyun-ui");
    var taskRecordNo = $.url().param("taskRecordNo");
    var status = $.url().param("status") || 0;
    $scope.frameIndex = 1;
    $scope.currentSelected = null;
    $scope.tempShapes1 = [];
    $scope.tempShapes2 = [];
    $scope.tempShapes3 = [];
    $scope.tempShapes4 = [];
    $scope.names1 = [];
    $scope.names2 = [];
    $scope.names3 = [];
    $scope.names4 = [];
    $scope.shapes1 = [];
    $scope.shapes2 = [];
    $scope.shapes3 = [];
    $scope.shapes3 = [];

    initView() && initData();

    $scope.$watchCollection("tempShapes1", function() {
      onNamesChanged(1);
    });

    $scope.$watchCollection("tempShapes2", function() {
      onNamesChanged(2);
    });
    $scope.$watchCollection("tempShapes3", function() {
      onNamesChanged(3);
    });
    $scope.$watchCollection("tempShapes4", function() {
      onNamesChanged(4);
    });

    $scope.canContinueMark = function() {
      if(!$scope.taskRecord) {
        return false;
      }
      if(["WAITTING", "REVIEWING","ACCEPTED"].indexOf($scope.taskRecord.status) !== -1) {
        var role = Cookies.get("role");
        if(["NORMAL", "FINANCE","SUPER","UPLOAD"].indexOf(role) !== -1) {
          return false;
        }
      }
      return true;
    };

    function onNamesChanged(videoIndex) {
      var temp = [];
      [1, 2, 3, 4].forEach(function(it) {
        $scope["tempShapes" + it].forEach(function(shape) {
          if(temp.indexOf(shape.shapeId) === -1) {
            temp.push(shape.shapeId);
          }
        });
      });

      [1, 2, 3, 4].forEach(function(it) {
        $scope["names" + it] = temp;
      });
    }

    function setNames(videoIndex, shape) {
      [1, 2, 3, 4].forEach(function(it) {
        if(it !== videoIndex) {
          if($scope["names" + it].indexOf(shape.shapeId) === -1) {
            $scope["names" + it].push(shape.shapeId);
          }
        }
      });
    }

    function resetNames() {
      [1, 2, 3, 4].forEach(function(it) {
        $scope["names" + it] = [];
        onNamesChanged(it);
      });
    }

    $scope.onCursorChecked = function() {
      $scope.showCursor = true;
    };

    $scope.onCursorUnChecked = function() {
      $scope.showCursor = false;
    };

    $scope.onPreviousButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if(pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onNextButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if(pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onViewTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'view.tag.update.dialog.html',
        controller: "viewTagUpdateDialogController",
        params: {
          algorithm: $scope.algorithm,
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          $scope.onSaveClick();
        }
      });
    };

    function getReviewRecord(reviewRecordNo) {
      reviewRecordService.getReviewRecord(reviewRecordNo)
        .then(function(data) {
          $scope.reviewRecord = data;
        });
    }

    $scope.onAssetTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: '../task/asset.tag.dialog.html',
        controller: "assetTagUpdateController",
        params: {
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          if(res.roadTag && res.roadTag.name) {
            $scope.taskRecord.taskItem.roadTag = res.roadTag;
            $scope.taskRecord.taskItem.roadTagId = res.roadTag.id;
          }

          if(res.weatherTag && res.weatherTag.name) {
            $scope.taskRecord.taskItem.weatherTag = res.weatherTag;
            $scope.taskRecord.taskItem.weatherTagId = res.weatherTag.id;
          }
        }
      });
    };

    function initView() {
      $scope.setTitle("任务标注");
      breadCrumbProvider.setHistories([{
        text: "任务大厅",
        href: "../home/index.html"
      }, {
        text: "任务标注",
        href: "#"
      }]);

      $scope.nameSelected = true;
      if(!taskRecordNo) {
        dialog.showError("参数错误");
        return false;
      }

      $scope.showCursor = true;
      $scope.videoControlBar = huoyun.newObject("VideoControlBar");

      $scope.canReview = true;
      $scope.disableEdit = false;
      var role = Cookies.get("role");
      if(["NORMAL", "FINANCE"].indexOf(role) !== -1) {
        $scope.canReview = false;
      }

      return true;
    }

    function initMarkRecords() {
      $scope.taskRecord.taskMarkRecords.forEach(function(it, index) {
        initMarkGroup(it);
      });
    }

    function initMarkShapeMap(groups) {
      var taskItemFileMap = {};
      groups.forEach(function(it, index) {
        if(!taskItemFileMap[it.taskItemFileId]) {
          taskItemFileMap[it.taskItemFileId] = [];
        }

        taskItemFileMap[it.taskItemFileId].push(it);
      });

      return taskItemFileMap;
    }

    function initNewShape(shape) {
      var $shape = huoyun.newObject("Shape");
      $shape.shapeId = shape.name;
      $shape.id = shape.id;
      $shape.name = shape.name;
      $shape.color = shape.color;
      $shape.type = $scope.taskRecord.taskItem.shapeType;
      $shape.count = $scope.taskRecord.taskItem.sideCount;
      $shape.colorTag = shape.colorTag;

      return $shape;
    }

    function getFileIndex(fileId) {
      for(var index = 1; index <= 4; index++) {
        var key = `taskItemFile${index}`;
        if(fileId === $scope[key].id) {
          return index;
        }
      }
    }

    function initMarkGroup(shape) {
      var shapes = [];
      var shapeMap = initMarkShapeMap(shape.groups);
      var frameIndexInfoMap = {};
      shape.shapeFrameIndexInfos.forEach(function(it) {
        var key = `${it.shapeName}@${it.fileId}`;
        if(!frameIndexInfoMap[key]) {
          frameIndexInfoMap[key] = [];
        }
        frameIndexInfoMap[key].push(it);
      });

      Object.keys(shapeMap).forEach(function(key, index) {
        var fileIndex = getFileIndex(+key);
        if(fileIndex !== undefined) {
          var frameIndexInfo = frameIndexInfoMap[`${shape.name}@${key}`];
          var temp = initNewShape(shape);
          temp.$$timeline = initTimeline(shapeMap[key], frameIndexInfo);
          formatService.convertBackFour(temp);
          shapes.push(temp);
          $scope[`tempShapes${fileIndex}`].push(temp);
        }
      });

      var tagDorpdownList = {};
      var tagCheckBoxList = {};

      shape.tags.forEach(function(it) {
        var tagItem = $scope.tagsMap[it.tagItemId];
        if(tagItem) {
          tagDorpdownList[tagItem.tagId] = tagItem;
          tagCheckBoxList[tagItem.id] = true;
        }
      });

      angular.forEach(shapes, function(it, index) {
        it.tagDorpdownList = tagDorpdownList;
        it.tagCheckBoxList = tagCheckBoxList;
      });
    }

    function initTimeline(groups, frameIndexInfo) {
      var minIndex = null;
      var maxIndex = undefined;
      var endMap = {};
      frameIndexInfo.forEach(function(it) {
        if(minIndex === null) {
          minIndex = it.startIndex;
        } else {
          if(minIndex > it.startIndex) {
            minIndex = it.startIndex;
          }
        }

        if(maxIndex === undefined) {
          maxIndex = it.endIndex;
        } else if(maxIndex != null) {
          if(maxIndex < it.endIndex) {
            maxIndex = it.endIndex;
          }
        }

        if(it.endIndex) {
          endMap[it.endIndex] = {
            isEnd: true
          }
        }
      });

      var $timeline = huoyun.newObject("Timeline");
      $timeline.data = [];
      $timeline.startIndex = minIndex;
      $timeline.endIndex = frameIndexInfo.endIndex;

      groups.forEach(function(group, groupIndex) {
        $timeline.data[group.frameIndex] = [];
        group.points.forEach(function(point, index) {
          var $point = huoyun.newObject("point", [point.x, point.y]);
          $timeline.data[group.frameIndex].push($point);

          if(endMap[group.frameIndex] !== undefined) {
            $timeline.data[group.frameIndex].isEnd = true;
          }
        });
      });

      return $timeline;
    }

    function initData() {
      $q.all([taskRecordService.getTaskMarks(taskRecordNo, status),
          taskRecordService.getAlgorithm(taskRecordNo)
        ])
        .then(function(res) {
          $scope.taskRecord = res[0];
          if($scope.taskRecord.reviewRecordNo) {
            getReviewRecord($scope.taskRecord.reviewRecordNo);
          }

          if(onParamValid()) {
            initTaskItemFiles();
            initVideo();
            initTags(res[1]);
            if($scope.taskRecord.taskMarkRecords) {
              initMarkRecords();
            }
          }
        });
    }

    function onParamValid() {
      var result = true;
      if($scope.taskRecord.taskItem && $scope.taskRecord.taskItem.taskItemFiles.length === 4) {
        [1, 2, 3, 4].forEach(function(it, index) {
          if(!$scope.taskRecord.taskItem.taskItemFiles[index].path) {
            result = false;
          }
        });

        if(!result) {
          dialog.showError("视频加载失败");
        }

      } else {
        dialog.showError("参数错误");
        result = false;
      }

      return result;
    }

    function initTaskItemFiles() {
      [1, 2, 3, 4].forEach(function(it, index) {
        var file = $scope.taskRecord.taskItem.taskItemFiles[index];
        var path = file.path;
        if(path.indexOf("front.avi") !== -1) {
          $scope["taskItemFile1"] = file;
          $scope["taskItemFile1"].path = mediaServiceUrl + path;
        } else if(path.indexOf("left.avi") !== -1) {
          $scope["taskItemFile2"] = file;
          $scope["taskItemFile2"].path = mediaServiceUrl + path;
        } else if(path.indexOf("rear.avi") !== -1) {
          $scope["taskItemFile3"] = file;
          $scope["taskItemFile3"].path = mediaServiceUrl + path;
        } else {
          $scope["taskItemFile4"] = file;
          $scope["taskItemFile4"].path = mediaServiceUrl + path;
        }
        //$scope["taskItemFile" + it] = $scope.taskRecord.taskItem.taskItemFiles[index];
        //$scope["taskItemFile" + it].path = mediaServiceUrl + $scope["taskItemFile" + it].path;
      });
    }

    function initTags(algorithm) {
      $scope.algorithm = algorithm;
      $scope.tags = algorithm.tags;
      $scope.tagsMap = $scope.tags.reduce(function(map, it, index) {
        it.items.forEach(function(item, itemIndex) {
          map[item.id] = item;
          map[item.id].type = it.type;
        });
        return map;
      }, {});

      $scope.colorGroup = algorithm.colorGroup;
      initViewTags();
    }

    function initViewTags() {
      if($scope.taskRecord.taskItem.viewTags) {
        $scope.taskRecord.taskItem.viewTags.forEach(function(it) {
          it.viewTag.items.forEach(function(item) {
            if(item.id === it.viewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }
    }

    function initVideo() {
      [1, 2, 3, 4].forEach(function(it) {
        $scope["video" + it] = huoyun.newObject("Video");
        $scope["video" + it].fps = $scope["taskItemFile" + it].fps || 96;
        $scope["video" + it].setTimeUpdatedCallback(onTimeUpdated);
        $scope["video" + it].setPauseCallback(onVideoPaused);
        $scope.videoControlBar.push($scope["video" + it]);
      });

    }

    $scope.onShape1SelectedChanged = function(shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(1, shape);
    };

    $scope.onShape2SelectedChanged = function(shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(2, shape);
    };

    $scope.onShape3SelectedChanged = function(shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(3, shape);
    };

    $scope.onShape4SelectedChanged = function(shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(4, shape);
    };

    function onShapeSelectionChanged(videoIndex, shape) {
      [1, 2, 3, 4].forEach(function(it) {
        if(it !== videoIndex) {
          setSelectedShapeInList(it, shape);
        }
      });
    }

    $scope.onShape1ObjectCopy = function(shape) {
      onShapeObjectCopy(1, shape);
    };

    $scope.onShape2ObjectCopy = function(shape) {
      onShapeObjectCopy(2, shape);
    };

    $scope.onShape3ObjectCopy = function(shape) {
      onShapeObjectCopy(3, shape);
    };

    $scope.onShape4ObjectCopy = function(shape) {
      onShapeObjectCopy(4, shape);
    };

    $scope.onColorSelectedChanged = function(colorTag) {
      if($scope.currentSelected && colorTag) {
        [1, 2, 3, 4].forEach(function(it) {
          $scope["tempShapes" + it].forEach(function(itShape, itIndex) {
            if(itShape.shapeId === $scope.currentSelected.shapeId) {
              itShape.changeColor(colorTag.color);
            }
          });
        });
      }
    };

    function onShapeObjectCopy(videoIndex, shape) {
      var find = null;

      [1, 2, 3, 4].forEach(function(it, index) {
        if(it != videoIndex) {
          $scope["tempShapes" + it].forEach(function(itShape, itIndex) {
            if(itShape.shapeId === shape.shapeId) {
              if(find === null) {
                find = itShape;
              }
              itShape.setSelected();
            }
          });
        }
      });

      if(find) {
        resetNames();
        shape.tagCheckBoxList = find.tagCheckBoxList;
        shape.tagDorpdownList = find.tagDorpdownList;
        shape.colorTag = find.colorTag;
        shape.changeColor(find.color);
        //finds[0].setSelected();
      }
    }

    function setSelectedShapeInList(listIndex, shape) {
      $scope["tempShapes" + listIndex].forEach(function(it, index) {
        if(it.shapeId === shape.shapeId) {
          it.setSelected();
        } else {
          it.setUnSelected();
        }
      });
    }

    function getFinalSaveData() {
      var taskMarkRecords = [];

      var map = getMarkRecordMap();
      Object.keys(map).forEach(function(key) {
        taskMarkRecords.push(map[key]);
      });

      formatService.convertToFour(taskMarkRecords);
      return formatService.deleteUnMarkShape(taskMarkRecords);
    }

    function getMarkRecordMap() {
      var taskMarkRecordMap = {};
      [1, 2, 3, 4].forEach(function(it, index) {
        $scope["tempShapes" + it].forEach(function(shape) {
          var record = taskMarkRecordMap[shape.shapeId];
          if(!record) {
            record = getSaveTaskMarkRecord(shape);
            taskMarkRecordMap[shape.shapeId] = record;
          }

          if(!record.shapeFrameIndexInfos) {
            record.shapeFrameIndexInfos = [];
          }

          var frameKeys = Object.keys(shape.$$timeline.data);
          var startIndex = null;
          var endIndex = null;
          for(var fIndex = 0; fIndex < frameKeys.length; fIndex++) {
            if(startIndex == null) {
              startIndex = frameKeys[fIndex];
            }

            if(shape.$$timeline.data[frameKeys[fIndex]].isEnd) {
              endIndex = frameKeys[fIndex];
            }

            if(endIndex != null) {
              record.shapeFrameIndexInfos.push({
                startIndex: startIndex,
                endIndex: endIndex,
                fileId: $scope["taskItemFile" + it].id,
                shapeName: shape.shapeId
              });

              startIndex = null;
              endIndex = null;
            } else {
              if(fIndex === frameKeys.length - 1) {
                record.shapeFrameIndexInfos.push({
                  startIndex: startIndex,
                  endIndex: endIndex,
                  fileId: $scope["taskItemFile" + it].id,
                  shapeName: shape.shapeId
                });
              }
            }
          }

          //        record.shapeFrameIndexInfos.push({
          //          startIndex: shape.$$timeline.startIndex,
          //          endIndex: shape.$$timeline.endIndex,
          //          fileId: $scope["taskItemFile" + it].id,
          //          shapeName: shape.shapeId
          //        });

          record.groups = record.groups.concat(getSaveGroup(shape, $scope["taskItemFile" + it]));
        });
      });

      return taskMarkRecordMap;
    }

    function getSaveTaskMarkRecord(shape) {
      var record = {};

      record.name = shape.shapeId;
      record.color = shape.color;
      record.startIndex = shape.$$timeline.startIndex;
      record.endIndex = shape.$$timeline.endIndex;
      if(shape.colorTag) {
        record.colorTagId = shape.colorTag.id;
      }
      record.tags = getSaveTags(shape);
      record.groups = [];

      return record;
    }

    function getSaveGroup(shape, taskItemFile) {
      var groups = [];

      Object.keys(shape.$$timeline.data).forEach(function(key) {
        var group = {};
        group.taskItemFileId = taskItemFile.id;
        group.frameIndex = key;
        group.points = getSavePoints(shape.$$timeline.data[key]);
        groups.push(group);
      });

      return groups;
    }

    function getSavePoints(pointDatas) {
      var points = [];
      pointDatas.forEach(function(it, index) {
        points.push({
          x: it.x,
          y: it.y
        });
      });
      return points;
    }

    function getSaveTags(shape) {
      var tags = [];

      Object.keys(shape.tagDorpdownList).forEach(function(key) {
        tags.push({
          tagItemId: shape.tagDorpdownList[key].id
        });
      });

      Object.keys(shape.tagCheckBoxList).forEach(function(key) {
        tags.push({
          tagItemId: key
        });
      });

      return tags;
    }

    $(document).on("keydown", function(event) {
      switch(event.keyCode) {
        case 81:
          event.stopPropagation();
          $scope.onSubmitClick();
          break;
        case 87:
          event.stopPropagation();
          $scope.onSaveClick();
          break;
      }
    });

    $scope.onSaveClick = function() {
      var data = getFinalSaveData();
      $scope.showLoading();
      taskRecordService.saveTaskMarks(taskRecordNo, data)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("保存成功").then(function() {
            window.location.reload();
            $log.info("保存成功！");
          });
        });
    };

    $scope.onSubmitClick = function() {
      var data = getFinalSaveData();
      dialog.showConfirm({
        title: "提示",
        message: "确认要提交标记结果么，提交后标记结果将由管理员进行审核，在此期间将不能对改任务进行标记。确认要继续么？",
        onConfirm: function() {
          $scope.showLoading();
          taskRecordService.saveTaskMarks(taskRecordNo, data)
            .then(function() {
              sumbitTask();
            });
        }
      })
    };

    $scope.onStartReviewClick = function() {
      if(!$scope.taskRecord.reviewRecordNo) {
        dialog.showError("参数错误");
        return;
      }
      reviewRecordService.startReview($scope.taskRecord.reviewRecordNo)
        .then(function() {
          initData();
        });
    };

    $scope.onReviewPassClick = function() {
      taskRecordService.reviewPass(taskRecordNo)
        .then(function() {
          dialog.showInfo("保存成功").then(function() {
            initData();
          });
        });
    };

    $scope.onReviewFailClick = function() {
      dialog.showCustom({
        templateUrl: '../task/reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: {
            taskRecordNo: taskRecordNo
          }
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    function disableMark() {
      if($scope.video1 && $scope.video1.dom && $scope.video1.dom.pause) {
        $scope.svgBarDisabled = false;
      } else {
        $scope.svgBarDisabled = true;
      }
      //$scope.svgBarDisabled = true;
    }

    function enableMark() {
      if($scope.taskRecord.status === "INPROGRESS") {
        $scope.svgBarDisabled = false;
      } else {
        $scope.svgBarDisabled = true;
        $log.warn("Enabled mark failed, because current task record status not inprogress");
      }
    }

    function onTimeUpdated(currentTime, totalTime) {
      /*
       * 现将shapes对象置空，在进行赋值（原因是因为angular里的watchCollection只监听数组的变化，不监听数组里面值的变化。）
       */
      reset();

      disableMark();
      $scope.$apply();
    }

    function reset() {
      $scope.shapes1 = [];
      $scope.shapes2 = [];
      $scope.shapes3 = [];
      $scope.shapes4 = [];
      $scope.$apply();
    }

    function onVideoPaused() {
      enableMark();
      $scope.$apply();
    }

    function sumbitTask() {
      taskRecordService.finishMark(taskRecordNo)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("提交成功").then(function() {
            //initData();
            window.location.reload();
          });
        });
    }
  }
]);
'use strict';

zongmu.controller("markPicController", ['$q', '$scope', 'taskService', 'dialog', 'tagService',
  'taskRecordService', 'mediaServiceUrl', 'formatService', '$log', 'reviewRecordService',
  'colorTagService', 'breadCrumb', 'markUtil',
  function($q, $scope, taskService, dialog, tagService, taskRecordService, mediaServiceUrl,
    formatService, $log, reviewRecordService, colorTagService, breadCrumbProvider, markUtil) {
    var taskRecordNo = $.url().param("taskRecordNo");
    var status = $.url().param("status") || 0;
    var markData = null;
    initView() && initData();

    $scope.onSaveButtonClick = function() {
      $scope.setLoadingText("正在保存，请稍等...");
      $scope.showLoading();
      taskRecordService.saveTaskMarks(taskRecordNo, getDataBeforeSave())
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("保存成功").then(function() {
            window.location.reload();
          });
        });
    };

    $scope.canContinueMark = function() {
      if(!$scope.taskRecord) {
        return false;
      }
      if(["WAITTING", "REVIEWING","ACCEPTED"].indexOf($scope.taskRecord.status) !== -1) {
        var role = Cookies.get("role");
        if(["NORMAL", "FINANCE","SUPER","UPLOAD"].indexOf(role) !== -1) {
          return false;
        }
      }
      return true;
    };

    $scope.onViewTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'view.tag.update.dialog.html',
        controller: "viewTagUpdateDialogController",
        params: {
          algorithm: $scope.algorithm,
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          $scope.onSaveButtonClick();
          //window.location.reload();
        }
      });
    };

    $scope.onSubmitClick = function() {

      dialog.showConfirm({
        title: "提示",
        message: "确认要提交标记结果么，提交后标记结果将由管理员进行审核，在此期间将不能对改任务进行标记。确认要继续么？",
        onConfirm: function() {
          $scope.setLoadingText("正在保存，请稍等...");
          $scope.showLoading();
          taskRecordService.saveTaskMarks(taskRecordNo, getDataBeforeSave())
            .then(function() {
              sumbitTask();
            });
        }
      });
    };

    $scope.onStartReviewClick = function() {
      if(!$scope.taskRecord.reviewRecordNo) {
        dialog.showError("参数错误");
        return;
      }
      reviewRecordService.startReview($scope.taskRecord.reviewRecordNo)
        .then(function() {
          initData();
        });
    };

    $scope.onReviewPassClick = function() {
      taskRecordService.reviewPass(taskRecordNo)
        .then(function() {
          dialog.showInfo("保存成功").then(function() {
            initData();
          });
        });
    };

    $scope.onReviewFailClick = function() {
      dialog.showCustom({
        templateUrl: '../task/reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: {
            taskRecordNo: taskRecordNo
          }
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    function sumbitTask() {
      taskRecordService.finishMark(taskRecordNo)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("提交成功").then(function() {
            window.location.reload();
          });
        });
    }

    function getDataBeforeSave() {
      var tempData = formatService.toMarkDataModel($scope.tempShapes);
      return formatService.deleteUnMarkShape(tempData);;
    }

    $scope.onCursorChecked = function() {
      $scope.showCursor = true;
    };

    $scope.onCursorUnChecked = function() {
      $scope.showCursor = false;
    };

    $scope.onPreviousButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if(pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onNextButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if(pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    function initViewTags() {
      if($scope.taskRecord.taskItem.viewTags) {
        $scope.taskRecord.taskItem.viewTags.forEach(function(it) {
          it.viewTag.items.forEach(function(item) {
            if(item.id === it.viewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }
    }

    $scope.onAssetTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: '../task/asset.tag.dialog.html',
        controller: "assetTagUpdateController",
        params: {
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          console.log(res)
          if(res.viewTags) {
            console.log(res.viewTags)
          }
        }
      });
    };

    function initView() {
      $scope.setTitle("任务标注");

      breadCrumbProvider.setHistories([{
        text: "任务大厅",
        href: "../home/index.html"
      }, {
        text: "任务标注",
        href: "#"
      }]);

      if(!taskRecordNo) {
        dialog.showError("参数错误");
        return false;
      }

      $scope.showCursor = true;
      $scope.tempShapes = [];
      $scope.shapes = [];

      $scope.canReview = true;
      $scope.disableEdit = false;
      var role = Cookies.get("role");
      if(["NORMAL", "FINANCE","SUPER","UPLOAD"].indexOf(role) !== -1) {
        $scope.canReview = false;
      }

      return true;
    }

    function initTags(algorithm) {
      $scope.algorithm = algorithm;
      $scope.tags = algorithm.tags;
      $scope.tagsMap = $scope.tags.reduce(function(map, it, index) {
        it.items.forEach(function(item, itemIndex) {
          map[item.id] = item;
          map[item.id].type = it.type;
        });
        return map;
      }, {});

      $scope.colorGroup = algorithm.colorGroup;
      initViewTags();
    }

    function getReviewRecord(reviewRecordNo) {
      reviewRecordService.getReviewRecord(reviewRecordNo)
        .then(function(data) {
          $scope.reviewRecord = data;
        });
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      $q.all([taskRecordService.getTaskMarks(taskRecordNo, status),
          taskRecordService.getAlgorithm(taskRecordNo)
        ])
        .then(function(res) {
          $scope.hideLoading();
          $scope.taskRecord = res[0];
          if($scope.taskRecord.reviewRecordNo) {
            getReviewRecord($scope.taskRecord.reviewRecordNo);
          }
          initTags(res[1]);

          if($scope.taskRecord.taskItem && $scope.taskRecord.taskItem.taskItemFiles.length > 0) {
            $scope.taskItemFile = $scope.taskRecord.taskItem.taskItemFiles[0];
          } else {
            dialog.showError("参数错误");
            return false;
          }

          var tagsMap = $scope.tags.reduce(function(map, it, index) {
            it.items.forEach(function(item, itemIndex) {
              map[item.id] = item;
              map[item.id].type = it.type;
            });
            return map;
          }, {});

          if(!$scope.taskItemFile.path) {
            dialog.showError("参数错误,没有找到对应的图片文件。");
            return false;
          } else {
            $scope.taskItemFile.path = mediaServiceUrl + $scope.taskItemFile.path;
          }

          if($scope.taskRecord.taskMarkRecords) {
            $scope.taskRecord.taskMarkRecords.forEach(function(it, index) {
              it.tagsMap = tagsMap;
              it.sideCount = $scope.taskRecord.taskItem.sideCount;
              it.taskItem = $scope.taskRecord.taskItem;
            });

            var $shapes = formatService.toMarkViewModel($scope.taskRecord.taskMarkRecords);
            if($shapes.length > 0) {
              $scope.tempShapes = $shapes;
            }
          }

          $scope.frameIndex = 1;
        });
    }
  }
]);
'use strict';

zongmu.controller("markFourVideoController", ['$q', '$scope', 'dialog', 'taskRecordService',
  'tagService', '$log', 'formatService', 'reviewRecordService', 'mediaServiceUrl', 'colorTagService', "markUtil", "breadCrumb",

  function($q, $scope, dialog, taskRecordService, tagService,
    $log, formatService, reviewRecordService, mediaServiceUrl, colorTagService, markUtil, breadCrumbProvider) {
    var huoyun = angular.module("huoyun-ui");
    var taskRecordNo = $.url().param("taskRecordNo");
    var status = $.url().param("status") || 0;
    $scope.currentSelected = null;
    $scope.tempShapes1 = [];
    $scope.tempShapes2 = [];
    $scope.tempShapes3 = [];
    $scope.tempShapes4 = [];
    $scope.names1 = [];
    $scope.names2 = [];
    $scope.names3 = [];
    $scope.names4 = [];
    $scope.shapes1 = [];
    $scope.shapes2 = [];
    $scope.shapes3 = [];
    $scope.shapes3 = [];

    initView() && initData();

    $scope.$watchCollection("tempShapes1", function() {
      onNamesChanged(1);
    });

    $scope.$watchCollection("tempShapes2", function() {
      onNamesChanged(2);
    });
    $scope.$watchCollection("tempShapes3", function() {
      onNamesChanged(3);
    });
    $scope.$watchCollection("tempShapes4", function() {
      onNamesChanged(4);
    });

    $scope.canContinueMark = function() {
      if(!$scope.taskRecord) {
        return false;
      }
      if(["WAITTING", "REVIEWING","ACCEPTED"].indexOf($scope.taskRecord.status) !== -1) {
        var role = Cookies.get("role");
        if(["NORMAL", "FINANCE","SUPER","UPLOAD"].indexOf(role) !== -1) {
          return false;
        }
      }
      return true;
    };

    function onNamesChanged(videoIndex) {
      var temp = [];
      var nameMap = {
        1: [],
        2: [],
        3: [],
        4: []
      };
      [1, 2, 3, 4].forEach(function(it) {
        $scope["tempShapes" + it].forEach(function(shape) {
          //nameMap[it].push(shape.shapeId);
          if(temp.indexOf(shape.shapeId) === -1) {
            temp.push(shape.shapeId);
          }
        });
      });

      [1, 2, 3, 4].forEach(function(it) {
        $scope["names" + it] = temp;
      });
    }

    function setNames(videoIndex, shape) {
      [1, 2, 3, 4].forEach(function(it) {
        if(it !== videoIndex) {
          if($scope["names" + it].indexOf(shape.shapeId) === -1) {
            $scope["names" + it].push(shape.shapeId);
          }
        }
      });
    }

    function resetNames() {
      [1, 2, 3, 4].forEach(function(it) {
        $scope["names" + it] = [];
        onNamesChanged(it);
      });
    }

    $scope.onCursorChecked = function() {
      $scope.showCursor = true;
    };

    $scope.onCursorUnChecked = function() {
      $scope.showCursor = false;
    };

    $scope.onPreviousButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if(pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onNextButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if(pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onViewTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'view.tag.update.dialog.html',
        controller: "viewTagUpdateDialogController",
        params: {
          algorithm: $scope.algorithm,
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          $scope.onSaveClick();
          //window.location.reload();
        }
      });
    };

    function getReviewRecord(reviewRecordNo) {
      reviewRecordService.getReviewRecord(reviewRecordNo)
        .then(function(data) {
          $scope.reviewRecord = data;
        });
    }

    $scope.onAssetTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: '../task/asset.tag.dialog.html',
        controller: "assetTagUpdateController",
        params: {
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          if(res.roadTag && res.roadTag.name) {
            $scope.taskRecord.taskItem.roadTag = res.roadTag;
            $scope.taskRecord.taskItem.roadTagId = res.roadTag.id;
          }

          if(res.weatherTag && res.weatherTag.name) {
            $scope.taskRecord.taskItem.weatherTag = res.weatherTag;
            $scope.taskRecord.taskItem.weatherTagId = res.weatherTag.id;
          }
        }
      });
    };

    function initView() {
      $scope.setTitle("任务标注");
      breadCrumbProvider.setHistories([{
        text: "任务大厅",
        href: "../home/index.html"
      }, {
        text: "任务标注",
        href: "#"
      }]);

      $scope.nameSelected = true;
      if(!taskRecordNo) {
        dialog.showError("参数错误");
        return false;
      }

      $scope.showCursor = true;
      $scope.videoControlBar = huoyun.newObject("VideoControlBar");

      $scope.canReview = true;
      $scope.disableEdit = false;
      var role = Cookies.get("role");
      if(["NORMAL", "FINANCE"].indexOf(role) !== -1) {
        $scope.canReview = false;
      }

      return true;
    }

    function initMarkRecords() {
      $scope.taskRecord.taskMarkRecords.forEach(function(it, index) {
        initMarkGroup(it);
      });
    }

    function initMarkShapeMap(groups) {
      var taskItemFileMap = {};
      groups.forEach(function(it, index) {
        if(!taskItemFileMap[it.taskItemFileId]) {
          taskItemFileMap[it.taskItemFileId] = [];
        }

        taskItemFileMap[it.taskItemFileId].push(it);
      });

      return taskItemFileMap;
    }

    function initNewShape(shape) {
      var $shape = huoyun.newObject("Shape");
      $shape.shapeId = shape.name;
      $shape.id = shape.id;
      $shape.name = shape.name;
      $shape.color = shape.color;
      $shape.type = $scope.taskRecord.taskItem.shapeType;
      $shape.count = $scope.taskRecord.taskItem.sideCount;
      $shape.colorTag = shape.colorTag;

      return $shape;
    }

    function getFileIndex(fileId) {
      for(var index = 1; index <= 4; index++) {
        var key = `taskItemFile${index}`;
        if(fileId === $scope[key].id) {
          return index;
        }
      }
    }

    function initMarkGroup(shape) {
      var shapes = [];
      var shapeMap = initMarkShapeMap(shape.groups);
      var frameIndexInfoMap = {};
      shape.shapeFrameIndexInfos.forEach(function(it) {
        var key = `${it.shapeName}@${it.fileId}`;
        if(!frameIndexInfoMap[key]) {
          frameIndexInfoMap[key] = [];
        }
        frameIndexInfoMap[key].push(it);
      });

      Object.keys(shapeMap).forEach(function(key, index) {
        var fileIndex = getFileIndex(+key);
        if(fileIndex !== undefined) {
          var frameIndexInfo = frameIndexInfoMap[`${shape.name}@${key}`];
          var temp = initNewShape(shape);
          temp.$$timeline = initTimeline(shapeMap[key], frameIndexInfo);
          formatService.convertBackFour(temp,$scope.taskItemFile1.height,$scope.taskItemFile1.width);
          shapes.push(temp);
          $scope[`tempShapes${fileIndex}`].push(temp);
        }
      });

      var tagDorpdownList = {};
      var tagCheckBoxList = {};

      shape.tags.forEach(function(it) {
        var tagItem = $scope.tagsMap[it.tagItemId];
        if(tagItem) {
          tagDorpdownList[tagItem.tagId] = tagItem;
          tagCheckBoxList[tagItem.id] = true;
        }
      });

      angular.forEach(shapes, function(it, index) {
        it.tagDorpdownList = tagDorpdownList;
        it.tagCheckBoxList = tagCheckBoxList;
      });
    }

    function initTimeline(groups, frameIndexInfo) {
      var minIndex = null;
      var maxIndex = undefined;
      var endMap = {};
      frameIndexInfo.forEach(function(it) {
        if(minIndex === null) {
          minIndex = it.startIndex;
        } else {
          if(minIndex > it.startIndex) {
            minIndex = it.startIndex;
          }
        }

        if(maxIndex === undefined) {
          maxIndex = it.endIndex;
        } else if(maxIndex != null) {
          if(maxIndex < it.endIndex) {
            maxIndex = it.endIndex;
          }
        }

        if(it.endIndex) {
          endMap[it.endIndex] = {
            isEnd: true
          }
        }
      });

      var $timeline = huoyun.newObject("Timeline");
      $timeline.data = [];
      $timeline.startIndex = minIndex;
      $timeline.endIndex = frameIndexInfo.endIndex;

      groups.forEach(function(group, groupIndex) {
        $timeline.data[group.frameIndex] = [];
        group.points.forEach(function(point, index) {
          var $point = huoyun.newObject("point", [point.x, point.y]);
          $timeline.data[group.frameIndex].push($point);

          if(endMap[group.frameIndex] !== undefined) {
            $timeline.data[group.frameIndex].isEnd = true;
          }
        });
      });

      return $timeline;
    }

    function initData() {
      $q.all([taskRecordService.getTaskMarks(taskRecordNo, status),
          taskRecordService.getAlgorithm(taskRecordNo)
        ])
        .then(function(res) {
          $scope.taskRecord = res[0];
          if($scope.taskRecord.reviewRecordNo) {
            getReviewRecord($scope.taskRecord.reviewRecordNo);
          }

          if(onParamValid()) {
            initTaskItemFiles();
            initVideo();
            initTags(res[1]);
            if($scope.taskRecord.taskMarkRecords) {
              initMarkRecords();
            }
          }
        });
    }

    function onParamValid() {
      var result = true;
      if($scope.taskRecord.taskItem && $scope.taskRecord.taskItem.taskItemFiles.length === 4) {
        [1, 2, 3, 4].forEach(function(it, index) {
          if(!$scope.taskRecord.taskItem.taskItemFiles[index].path) {
            result = false;
          }
        });

        if(!result) {
          dialog.showError("视频加载失败");
        }

      } else {
        dialog.showError("参数错误");
        result = false;
      }

      return result;
    }

    function initTaskItemFiles() {
      [1, 2, 3, 4].forEach(function(it, index) {
        var file = $scope.taskRecord.taskItem.taskItemFiles[index];
        var path = file.path;
        if(path.indexOf("front.avi") !== -1) {
          $scope["taskItemFile1"] = file;
          $scope["taskItemFile1"].path = mediaServiceUrl + path;
        } else if(path.indexOf("left.avi") !== -1) {
          $scope["taskItemFile2"] = file;
          $scope["taskItemFile2"].path = mediaServiceUrl + path;
        } else if(path.indexOf("rear.avi") !== -1) {
          $scope["taskItemFile3"] = file;
          $scope["taskItemFile3"].path = mediaServiceUrl + path;
        } else {
          $scope["taskItemFile4"] = file;
          $scope["taskItemFile4"].path = mediaServiceUrl + path;
        }
        //$scope["taskItemFile" + it] = $scope.taskRecord.taskItem.taskItemFiles[index];
        //$scope["taskItemFile" + it].path = mediaServiceUrl + $scope["taskItemFile" + it].path;
      });
    }

    function initTags(algorithm) {
      $scope.algorithm = algorithm;
      $scope.tags = algorithm.tags;
      $scope.tagsMap = $scope.tags.reduce(function(map, it, index) {
        it.items.forEach(function(item, itemIndex) {
          map[item.id] = item;
          map[item.id].type = it.type;
        });
        return map;
      }, {});

      $scope.colorGroup = algorithm.colorGroup;
      initViewTags();
    }

    function initViewTags() {
      if($scope.taskRecord.taskItem.viewTags) {
        $scope.taskRecord.taskItem.viewTags.forEach(function(it) {
          it.viewTag.items.forEach(function(item) {
            if(item.id === it.viewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }
    }

    function initVideo() {
      [1, 2, 3, 4].forEach(function(it) {
        $scope["video" + it] = huoyun.newObject("Video");
        $scope["video" + it].fps = $scope["taskItemFile" + it].fps || 96;
        $scope["video" + it].setTimeUpdatedCallback(onTimeUpdated);
        $scope["video" + it].setPauseCallback(onVideoPaused);
        $scope.videoControlBar.push($scope["video" + it]);
      });

    }

    $scope.onShape1SelectedChanged = function(shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(1, shape);
    };

    $scope.onShape2SelectedChanged = function(shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(2, shape);
    };

    $scope.onShape3SelectedChanged = function(shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(3, shape);
    };

    $scope.onShape4SelectedChanged = function(shape) {
      $scope.currentSelected = shape;
      onShapeSelectionChanged(4, shape);
    };

    function onShapeSelectionChanged(videoIndex, shape) {
      [1, 2, 3, 4].forEach(function(it) {
        if(it !== videoIndex) {
          setSelectedShapeInList(it, shape);
        }
      });
    }

    $scope.onShape1ObjectCopy = function(shape) {
      onShapeObjectCopy(1, shape);
    };

    $scope.onShape2ObjectCopy = function(shape) {
      onShapeObjectCopy(2, shape);
    };

    $scope.onShape3ObjectCopy = function(shape) {
      onShapeObjectCopy(3, shape);
    };

    $scope.onShape4ObjectCopy = function(shape) {
      onShapeObjectCopy(4, shape);
    };

    $scope.onColorSelectedChanged = function(colorTag) {
      if($scope.currentSelected && colorTag) {
        [1, 2, 3, 4].forEach(function(it) {
          $scope["tempShapes" + it].forEach(function(itShape, itIndex) {
            if(itShape.shapeId === $scope.currentSelected.shapeId) {
              itShape.changeColor(colorTag.color);
            }
          });
        });
      }
    };

    function onShapeObjectCopy(videoIndex, shape) {
      var find = null;

      [1, 2, 3, 4].forEach(function(it, index) {
        if(it != videoIndex) {
          $scope["tempShapes" + it].forEach(function(itShape, itIndex) {
            if(itShape.shapeId === shape.shapeId) {
              if(find === null) {
                find = itShape;
              }
              itShape.setSelected();
            }
          });
        }
      });

      if(find) {
        resetNames();
        shape.tagCheckBoxList = find.tagCheckBoxList;
        shape.tagDorpdownList = find.tagDorpdownList;
        shape.colorTag = find.colorTag;
        shape.changeColor(find.color);
        //finds[0].setSelected();
      }
    }

    function setSelectedShapeInList(listIndex, shape) {
      $scope["tempShapes" + listIndex].forEach(function(it, index) {
        if(it.shapeId === shape.shapeId) {
          it.setSelected();
        } else {
          it.setUnSelected();
        }
      });
    }

    function getFinalSaveData() {
      var taskMarkRecords = [];

      var map = getMarkRecordMap();
      Object.keys(map).forEach(function(key) {
        taskMarkRecords.push(map[key]);
      });

      formatService.convertToFour(taskMarkRecords,$scope.taskItemFile1.height,$scope.taskItemFile1.width);
      return formatService.deleteUnMarkShape(taskMarkRecords);
    }

    function getMarkRecordMap() {
      var taskMarkRecordMap = {};
      [1, 2, 3, 4].forEach(function(it, index) {
        $scope["tempShapes" + it].forEach(function(shape) {
          var record = taskMarkRecordMap[shape.shapeId];
          if(!record) {
            record = getSaveTaskMarkRecord(shape);
            taskMarkRecordMap[shape.shapeId] = record;
          }

          if(!record.shapeFrameIndexInfos) {
            record.shapeFrameIndexInfos = [];
          }

          var frameKeys = Object.keys(shape.$$timeline.data);
          var startIndex = null;
          var endIndex = null;
          for(var fIndex = 0; fIndex < frameKeys.length; fIndex++) {
            if(startIndex == null) {
              startIndex = frameKeys[fIndex];
            }

            if(shape.$$timeline.data[frameKeys[fIndex]].isEnd) {
              endIndex = frameKeys[fIndex];
            }

            if(endIndex != null) {
              record.shapeFrameIndexInfos.push({
                startIndex: startIndex,
                endIndex: endIndex,
                fileId: $scope["taskItemFile" + it].id,
                shapeName: shape.shapeId
              });

              startIndex = null;
              endIndex = null;
            } else {
              if(fIndex === frameKeys.length - 1) {
                record.shapeFrameIndexInfos.push({
                  startIndex: startIndex,
                  endIndex: endIndex,
                  fileId: $scope["taskItemFile" + it].id,
                  shapeName: shape.shapeId
                });
              }
            }
          }

          //        record.shapeFrameIndexInfos.push({
          //          startIndex: shape.$$timeline.startIndex,
          //          endIndex: shape.$$timeline.endIndex,
          //          fileId: $scope["taskItemFile" + it].id,
          //          shapeName: shape.shapeId
          //        });

          record.groups = record.groups.concat(getSaveGroup(shape, $scope["taskItemFile" + it]));
        });
      });

      return taskMarkRecordMap;
    }

    function getSaveTaskMarkRecord(shape) {
      var record = {};

      record.name = shape.shapeId;
      record.color = shape.color;
      record.startIndex = shape.$$timeline.startIndex;
      record.endIndex = shape.$$timeline.endIndex;
      if(shape.colorTag) {
        record.colorTagId = shape.colorTag.id;
      }
      record.tags = getSaveTags(shape);
      record.groups = [];

      return record;
    }

    function getSaveGroup(shape, taskItemFile) {
      var groups = [];

      Object.keys(shape.$$timeline.data).forEach(function(key) {
        var group = {};
        group.taskItemFileId = taskItemFile.id;
        group.frameIndex = key;
        group.points = getSavePoints(shape.$$timeline.data[key]);
        groups.push(group);
      });

      return groups;
    }

    function getSavePoints(pointDatas) {
      var points = [];
      pointDatas.forEach(function(it, index) {
        points.push({
          x: it.x,
          y: it.y
        });
      });
      return points;
    }

    function getSaveTags(shape) {
      var tags = [];

      Object.keys(shape.tagDorpdownList).forEach(function(key) {
        tags.push({
          tagItemId: shape.tagDorpdownList[key].id
        });
      });

      Object.keys(shape.tagCheckBoxList).forEach(function(key) {
        tags.push({
          tagItemId: key
        });
      });

      return tags;
    }

    $(document).on("keydown", function(event) {
      switch(event.keyCode) {
        case 81:
          event.stopPropagation();
          $scope.onSubmitClick();
          break;
        case 87:
          event.stopPropagation();
          $scope.onSaveClick();
          break;
      }
    });

    $scope.onSaveClick = function() {
      var data = getFinalSaveData();
      $scope.showLoading();
      taskRecordService.saveTaskMarks(taskRecordNo, data)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("保存成功").then(function() {
            window.location.reload();
            $log.info("保存成功！");
          });
        });
    };

    $scope.onSubmitClick = function() {
      var data = getFinalSaveData();
      dialog.showConfirm({
        title: "提示",
        message: "确认要提交标记结果么，提交后标记结果将由管理员进行审核，在此期间将不能对改任务进行标记。确认要继续么？",
        onConfirm: function() {
          $scope.showLoading();
          taskRecordService.saveTaskMarks(taskRecordNo, data)
            .then(function() {
              sumbitTask();
            });
        }
      })
    };

    $scope.onStartReviewClick = function() {
      if(!$scope.taskRecord.reviewRecordNo) {
        dialog.showError("参数错误");
        return;
      }
      reviewRecordService.startReview($scope.taskRecord.reviewRecordNo)
        .then(function() {
          initData();
        });
    };

    $scope.onReviewPassClick = function() {
      taskRecordService.reviewPass(taskRecordNo)
        .then(function() {
          dialog.showInfo("保存成功").then(function() {
            initData();
          });
        });
    };

    $scope.onReviewFailClick = function() {
      dialog.showCustom({
        templateUrl: '../task/reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: {
            taskRecordNo: taskRecordNo
          }
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    function disableMark() {
      if($scope.video1 && $scope.video1.dom && $scope.video1.dom.pause) {
        $scope.svgBarDisabled = false;
      } else {
        $scope.svgBarDisabled = true;
      }
      //$scope.svgBarDisabled = true;
    }

    function enableMark() {
      if($scope.taskRecord.status === "INPROGRESS") {
        $scope.svgBarDisabled = false;
      } else {
        $scope.svgBarDisabled = true;
        $log.warn("Enabled mark failed, because current task record status not inprogress");
      }
    }

    function onTimeUpdated(currentTime, totalTime) {
      /*
       * 现将shapes对象置空，在进行赋值（原因是因为angular里的watchCollection只监听数组的变化，不监听数组里面值的变化。）
       */
      reset();

      disableMark();
      $scope.$apply();
    }

    function reset() {
      $scope.shapes1 = [];
      $scope.shapes2 = [];
      $scope.shapes3 = [];
      $scope.shapes4 = [];
      $scope.$apply();
    }

    function onVideoPaused() {
      enableMark();
      $scope.$apply();
    }

    function sumbitTask() {
      taskRecordService.finishMark(taskRecordNo)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("提交成功").then(function() {
            //initData();
            window.location.reload();
          });
        });
    }
  }
]);
'use strict';

zongmu.controller("markVideoController", ['$q', '$scope', 'dialog', 'taskRecordService',
  '$log', 'formatService', 'reviewRecordService', 'mediaServiceUrl', 'breadCrumb', "markUtil", "taskService",

  function($q, $scope, dialog, taskRecordService, $log,
    formatService, reviewRecordService, mediaServiceUrl, breadCrumbProvider, markUtil, taskService) {
    var huoyun = angular.module("huoyun-ui");
    var taskRecordNo = $.url().param("taskRecordNo");
    var status = $.url().param("status") || 0;
    $scope.tempShapes = [];

    initView() && initData();

    function initView() {
      $scope.setTitle("任务标注");
      breadCrumbProvider.setHistories([{
        text: "任务大厅",
        href: "../home/index.html"
      }, {
        text: "任务标注",
        href: "#"
      }]);

      if(!taskRecordNo) {
        dialog.showError("参数错误");
        return false;
      }

      $scope.showCursor = true;
      $scope.videoControlBar = huoyun.newObject("VideoControlBar");
      $scope.shapes = [];

      $scope.canReview = true;
      $scope.disableEdit = false;
      var role = Cookies.get("role");
      if(["NORMAL", "FINANCE", "SUPER", "UPLOAD"].indexOf(role) !== -1) {
        $scope.canReview = false;
      }
      return true;
    }

    function getReviewRecord(reviewRecordNo) {
      reviewRecordService.getReviewRecord(reviewRecordNo)
        .then(function(data) {
          $scope.reviewRecord = data;
        });
    }

    $scope.onCursorChecked = function() {
      $scope.showCursor = true;
    };

    $scope.onCursorUnChecked = function() {
      $scope.showCursor = false;
    };

    $scope.canContinueMark = function() {
      if(!$scope.taskRecord) {
        return false;
      }
      if(["WAITTING", "REVIEWING", "ACCEPTED"].indexOf($scope.taskRecord.status) !== -1) {
        var role = Cookies.get("role");
        if(["NORMAL", "FINANCE", "SUPER", "UPLOAD"].indexOf(role) !== -1) {
          return false;
        }
      }
      return true;
    };

    $scope.onPreviousButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if(pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onNextButtonClick = function(taskRecord) {
      var pageName = markUtil.getMarkPage(taskRecord);
      if(pageName) {
        window.location.href = pageName + "?status=" + status + "&taskRecordNo=" + taskRecord.taskRecordNo + "#mark";
      }
    };

    $scope.onAssetTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: '../task/asset.tag.dialog.html',
        controller: "assetTagUpdateController",
        params: {
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          if(res.roadTag && res.roadTag.name) {
            $scope.taskRecord.taskItem.roadTag = res.roadTag;
            $scope.taskRecord.taskItem.roadTagId = res.roadTag.id;
          }

          if(res.weatherTag && res.weatherTag.name) {
            $scope.taskRecord.taskItem.weatherTag = res.weatherTag;
            $scope.taskRecord.taskItem.weatherTagId = res.weatherTag.id;
          }
        }
      });
    };

    $scope.onViewTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'view.tag.update.dialog.html',
        controller: "viewTagUpdateDialogController",
        params: {
          algorithm: $scope.algorithm,
          taskItem: $scope.taskRecord.taskItem
        },
        onConfirm: function(res) {
          $scope.onSaveClick(true);
          //window.location.reload();
        }
      });
    };

    function initViewTags() {
      if($scope.taskRecord.taskItem.viewTags) {
        $scope.taskRecord.taskItem.viewTags.forEach(function(it) {
          it.viewTag.items.forEach(function(item) {
            if(item.id === it.viewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }
    }

    function initTags(algorithm) {
      $scope.algorithm = algorithm;
      $scope.tags = algorithm.tags;
      $scope.tagsMap = $scope.tags.reduce(function(map, it, index) {
        it.items.forEach(function(item, itemIndex) {
          map[item.id] = item;
          map[item.id].type = it.type;
        });
        return map;
      }, {});

      $scope.colorGroup = algorithm.colorGroup;
      initViewTags();
    }

    function initData() {
      $q.all([taskRecordService.getTaskMarks(taskRecordNo, status),
          taskRecordService.getAlgorithm(taskRecordNo)
        ])
        .then(function(res) {
          $scope.taskRecord = res[0];

          if($scope.taskRecord.reviewRecordNo) {
            getReviewRecord($scope.taskRecord.reviewRecordNo);
          }
          if($scope.taskRecord.taskItem && $scope.taskRecord.taskItem.taskItemFiles.length > 0) {
            $scope.taskItemFile = $scope.taskRecord.taskItem.taskItemFiles[0];
          } else {
            dialog.showError("参数错误");
            return false;
          }

          if(!$scope.taskItemFile.path) {
            dialog.showError("参数错误,没有找到对应的视频文件。");
            return false;
          } else {
            $scope.taskItemFile.path = mediaServiceUrl + $scope.taskItemFile.path;
          }

          initTags(res[1]);
          initVideo();

          if($scope.taskRecord.taskMarkRecords) {
            $scope.taskRecord.taskMarkRecords.forEach(function(it, index) {
              it.tagsMap = $scope.tagsMap;
              it.taskItem = $scope.taskRecord.taskItem;
            });

            var $shapes = formatService.toMarkViewModel($scope.taskRecord.taskMarkRecords);
            formatService.convertBackSingle($shapes,$scope.taskItemFile.height,$scope.taskItemFile.width);
            if($shapes.length > 0) {
              $scope.tempShapes = $shapes;
            }
          }
        });
    }

    function initVideo() {
      $scope.video = huoyun.newObject("Video");
      $scope.video.fps = $scope.taskItemFile.fps;
      $scope.video.setTimeUpdatedCallback(onTimeUpdated);
      $scope.video.setPauseCallback(onVideoPaused);
      $scope.videoControlBar.push($scope.video);
    }

    function getDataBeforeSave() {
      
      var tempData = formatService.toMarkDataModel($scope.tempShapes);
      formatService.convertToSingle(tempData,$scope.taskItemFile.height,$scope.taskItemFile.width);
      return formatService.deleteUnMarkShape(tempData);;
    }

    $(document).on("keydown", function(event) {
      switch(event.keyCode) {
        case 81:
          event.stopPropagation();
          $scope.onSubmitClick();
          break;
        case 87:
          event.stopPropagation();
          $scope.onSaveClick();
          break;
      }
    });

    $scope.onSaveClick = function(needRefresh) {
      $scope.showLoading();
      var data = [];
      data.push({
        taskItemFile: $scope.taskItemFile,
        markShapes: $scope.tempShapes
      });

      var tempData = getDataBeforeSave();
      taskRecordService.saveTaskMarks(taskRecordNo, tempData)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("保存成功").then(function() {
            window.location.reload();
            $log.info("保存成功！");
          });
        });
    };

    $scope.onSubmitClick = function() {
      var data = [];
      data.push({
        taskItemFile: $scope.taskItemFile,
        markShapes: $scope.tempShapes
      });

      dialog.showConfirm({
        title: "提示",
        message: "确认要提交标记结果么，提交后标记结果将由管理员进行审核，在此期间将不能对改任务进行标记。确认要继续么？",
        onConfirm: function() {
          taskRecordService.saveTaskMarks(taskRecordNo, getDataBeforeSave())
            .then(function() {
              sumbitTask();
            });
        }
      })
    };

    $scope.onStartReviewClick = function() {
      if(!$scope.taskRecord.reviewRecordNo) {
        dialog.showError("参数错误");
        return;
      }
      reviewRecordService.startReview($scope.taskRecord.reviewRecordNo)
        .then(function() {
          initData();
        });
    };

    $scope.onReviewPassClick = function() {
      taskRecordService.reviewPass(taskRecordNo)
        .then(function() {
          dialog.showInfo("保存成功").then(function() {
            initData();
          });
        });
    };

    $scope.onReviewFailClick = function() {
      dialog.showCustom({
        templateUrl: '../task/reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: {
            taskRecordNo: taskRecordNo
          }
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    function disableMark() {
      if($scope.video && $scope.video.dom && $scope.video.dom.pause) {
        $scope.svgBarDisabled = false;
      } else {
        $scope.svgBarDisabled = true;
      }
    }

    function enableMark() {
      if($scope.taskRecord.status === "INPROGRESS") {
        $scope.svgBarDisabled = false;
      } else {
        $scope.svgBarDisabled = true;
        $log.warn("Enabled mark failed, because current task record status not inprogress");
      }
    }

    function onTimeUpdated(currentTime, totalTime) {
      disableMark();
      $scope.$apply();
    }

    function onVideoPaused() {
      enableMark();
      $scope.$apply();
    }

    function sumbitTask() {
      taskRecordService.finishMark(taskRecordNo)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("提交成功").then(function() {
            window.location.reload();
            //window.location.href = "../task/index.html";
          });
        });
    }
  }
]);
'use strict';

zongmu.controller("viewTagUpdateDialogController", ['$scope', 'dialog', 'assetService', 'taskService',
  function($scope, dialog, assetService, taskService) {
    var params = $scope.ngDialogData;

    init();

    function init() {
      $scope.viewTags = params.algorithm.viewTags;
      $scope.viewTagsMap = {};
      params.taskItem.viewTags.forEach(function(it) {
        $scope.viewTagsMap[it.viewTagId] = it.viewTagItemId;
      });
    }

    function getSelected() {
      var viewTags = [];

      Object.keys($scope.viewTagsMap).forEach(function(key) {
        if($scope.viewTagsMap[key] != null || $scope.viewTagsMap[key] != undefined) {
          viewTags.push({
            viewTagId: key,
            viewTagItemId: $scope.viewTagsMap[key]
          });
        }
      });

      return viewTags;
    }

    $scope.onSaveButtonClick = function() {
      var viewTags = getSelected();
      taskService.updateViewTags(params.taskItem.taskItemNo, {
          "items": viewTags
        })
        .then(function() {
          $scope.closeThisDialog({
            key: 'ok',
            result: {
              viewTags: viewTags
            }
          });
        });
    };
  }
]);
'use strict';

zongmu.controller("assetDetailController", ["$q", "$scope", "assetService", "taskService", "dialog", "$timeout", "breadCrumb", "exportService",
  function($q, $scope, assetService, taskService, dialog, $timeout, breadCrumbProvider, exportService) {
    var assetNo = $.url().param("assetNo");

    initView() && initData();

    $scope.onSearchButtonClick = function() {
      $scope.expandButton = !$scope.expandButton;
    };

    function initView() {
      $scope.expandButton = true;
      breadCrumbProvider.setHistories([{
        text: "上传记录",
        href: "assets.html"
      }, {
        text: "记录详情",
        href: "#"
      }]);

      $scope.taskColumns = [{
        name: "taskName",
        text: "任务名称"
      }, {
        name: "taskType",
        text: "类型"
      }, {
        name: "priority",
        text: "优先级"
      }, {
        name: "showHome",
        text: "是否在任务大厅显示"
      }, {
        name: "op",
        text: "操作"
      }];

      $scope.fileColumns = [{
        name: "assetFileNo",
        text: "No."
      }, {
        name: "fileName",
        text: "文件名"
      }, {
        name: "fileSize",
        text: "文件大小"
      }, {
        name: "assetFileStatus",
        text: "状态"
      }];

      if(!assetNo) {
        dialog.showError("参数错误");
        return false;
      }
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      $q.all([assetService.getAsset(assetNo), taskService.getTasksByAssetNo(assetNo)])
        .then(function(res) {
          $scope.asset = res[0];
          if($scope.asset.viewTags) {
            $scope.asset.viewTags.forEach(function(it) {
              it.viewTag.items.forEach(function(item) {
                if(item.id === it.assetViewTagItemId) {
                  it.viewTagItem = item;
                }
              })
            });
          }
          $scope.tasks = res[1];
          $scope.hideLoading();
          refresh();
        });
    }

    function refresh() {
      var items = ($scope.asset.assetFiles || []).filter(function(it) {
        return it.assetFileStatus === "FTPUPLOADING" || it.assetFileStatus === "COMPRESSING" || it.assetFileStatus === "UPLOADSUCCESS";
      });

      if(items.length > 0) {
        $timeout(function() {
          getAssetData();
        }, 2000);
      }
    }

    function getAssetData() {
      assetService.getAsset(assetNo)
        .then(function(asset) {
          $scope.asset = asset;
          if($scope.asset.viewTags) {
            $scope.asset.viewTags.forEach(function(it) {
              it.viewTag.items.forEach(function(item) {
                if(item.id === it.assetViewTagItemId) {
                  it.viewTagItem = item;
                }
              })
            });
          }
          refresh();
        });
    }

    $scope.onCompressButtonClick = function() {
      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍等...");
      assetService.compress(assetNo)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("开始压缩")
            .then(function() {
              initData();
            });
        });
    }

    $scope.onNewTaskButtonClick = function() {
      if(assetNo) {
        window.location.href = "asset.new.html?assetNo=" + assetNo;
      } else {
        dialog.showError("参数错误");
      }
    };

    $scope.onSetTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'asset.tag.dialog.html',
        controller: "assetTagUpdateController",
        params: {
          asset: $scope.asset
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onUpdateAssetViewTagButtonClick = function() {

      dialog.showCustom({
        templateUrl: 'asset.view.tag.update.dialog.html',
        controller: "assetViewTagUpdateDialogController",
        params: {
          asset: $scope.asset
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(rowData) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条记录吗？",
        onConfirm: function() {
          $scope.showLoading();
          $scope.setLoadingText("正在删除任务和任务相关数据，请稍后...");
          taskService.deleteTask(rowData.taskNo)
            .then(function() {
              $scope.hideLoading();
              initData();
            });
        }
      });
    };

    $scope.onExportButtonClick = function(rowData) {
      $scope.showLoading();
      $scope.setLoadingText("正在导出数据，请稍后...");
      exportService.exportTask(assetNo, rowData.taskNo)
        .then(function(data) {
          $scope.hideLoading();
          if(data.taskItems.length > 0) {
            dialog.showCustom({
              templateUrl: 'export.failed.dialog.html',
              controller: "exportFailedDialogController",
              params: {
                taskItems: data.taskItems
              },
              onConfirm: function() {
              }
            });
          } else {
            dialog.showInfo("导出成功");
          }
        });
    };
  }
]);
'use strict';

zongmu.controller("newTaskController", ["$scope", "assetService", "dialog", "enumService", "taskService",
  "breadCrumb", "$q", "algorithmService", "viewTagService",
  function($scope, assetService, dialog, enumService, taskService, breadCrumbProvider, $q, algorithmService, viewTagService) {
    var assetNo = $.url().param("assetNo");
    var asset = null;
    initView() && initData();

    $scope.onCancelButtonClick = function() {
      dialog.showConfirm({
        title: "提示",
        message: "确定要放弃修改？",
        onConfirm: function() {
          history.back();
        }
      });
    };

    $scope.onSaveButtonClick = function() {
      var data = $scope.data;

      if(!data.taskName) {
        dialog.showInfo("请填写任务名称");
        return;
      }

      if(!data.taskType) {
        dialog.showInfo("请选择任务类型");
        return;
      }

      if(!data.algorithmId) {
        dialog.showInfo("请选择算法类型");
        return;
      }

      if(data.taskType === 'VIDEO') {

        if(!data.timeInterval || data.timeInterval < 30) {
          dialog.showInfo("请设置视频切割的时间间隔，间隔必须大于等于30秒");
          return;
        }

      } else {
        if(!data.shapeType) {
          dialog.showInfo("请选择标注形状");
          return;
        }

        if(data.shapeType === 'POLYLINE') {
          if(!data.sideCount || data.sideCount < 3 || data.sideCount >= 10) {
            dialog.showInfo("请填写多边形边数，边数必须大于等于3且小于等于10.");
            return;
          }
        }
        if($scope.asset.assetType !== "PICTURE") {
          if(!data.timeInterval || data.timeInterval < 3) {
            dialog.showInfo("请设置图片提取的时间间隔，间隔必须大于等于3秒");
            return;
          }
        }

      }

      if(!data.point) {
        dialog.showInfo("请设置奖励金币，并且金币不能为0。");
        return;
      }

      if(data.point <= 0) {
        dialog.showInfo("金币必须大于0。");
        return;
      }

      data.viewTags = getSelectedViewTagItems();

      $scope.showLoading();
      taskService.createTask(data)
        .then(function(res) {
          $scope.hideLoading();
          dialog.showInfo("保存成功！").then(function() {
            window.location.href = "asset.detail.html?assetNo=" + assetNo;
          });
        })
    };

    function initView() {
      if(!assetNo) {
        dialog.showError("参数不正确");
        return false;
      }

      breadCrumbProvider.setHistories([{
        text: "上传记录",
        href: "assets.html"
      }, {
        text: "记录详情",
        href: "asset.detail.html?assetNo=" + assetNo
      }, {
        text: "新建任务",
        href: "#"
      }]);

      $scope.taskTypes = enumService.getTaskTypes();
      $scope.shapeTypes = enumService.getShapeTypes();
      $scope.videoShapeTypes = enumService.getVideoShapeTypes();
      $scope.taskPriorities = enumService.getTaskPriorities();
      $scope.selection = {};
      return true;
    }

    function initData() {
      $scope.data = {
        assetNo: assetNo,
        taskType: "VIDEO",
        shapeType: "RECT",
        assetTags: [],
        priority: 2
      };

      $scope.viewTags = [];

      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      $q.all([assetService.getAsset(assetNo), algorithmService.getAlgorithms()])
        .then(function(res) {
          $scope.hideLoading();
          $scope.asset = res[0];
          $scope.algorithms = res[1];
          if($scope.algorithms.length > 0) {
            $scope.data.algorithmId = $scope.algorithms[0].id;
          }
          if($scope.asset.assetType === "PICTURE") {
            $scope.data.taskType = "PICTURE";
          }

          if($scope.asset.viewTags) {
            $scope.asset.viewTags.forEach(function(it) {
              it.viewTag.items.forEach(function(item) {
                if(item.id === it.assetViewTagItemId) {
                  it.viewTagItem = item;
                }
              })
            });
          }
        });
    }

    function initViewTags() {
      $scope.viewTagsMap = {};
      $scope.viewTags.forEach(function(it) {
        it.items.forEach(function(item) {
          if(item.default) {
            $scope.viewTagsMap[it.id] = item.id;
          }
        });

        if(!$scope.viewTagsMap[it.id] && it.items.length > 0) {
          $scope.viewTagsMap[it.id] = it.items[0].id;
        }
      });
    }

    function getSelectedViewTagItems() {

      var viewTags = [];

      Object.keys($scope.viewTagsMap).forEach(function(key) {
        if($scope.viewTagsMap[key] != null || $scope.viewTagsMap[key] != undefined) {
          viewTags.push({
            viewTagId: key,
            viewTagItemId: $scope.viewTagsMap[key]
          });
        }
      });

      return viewTags;
    }

    $scope.$watch("data.algorithmId", function() {
      if($scope.data.algorithmId) {
        $scope.showLoading();
        $scope.setLoadingText("正在加载数据，请稍后...");
        viewTagService.getViewTags($scope.data.algorithmId)
          .then(function(res) {
            $scope.hideLoading();
            $scope.viewTags = res;
            initViewTags();
          });
      }
    });
  }
]);
'use strict';

zongmu.controller("assetTagUpdateController", ['$scope', 'dialog', 'assetService', 'taskService',
  function($scope, dialog, assetService, taskService) {
    var params = $scope.ngDialogData;
    var weatherTagId = null;
    var roadTagId = null;
    init();

    function init() {
      if(params.asset) {
        weatherTagId = params.asset.weatherTagId;
        roadTagId = params.asset.roadTagId;
      }

      if(params.taskItem) {
        weatherTagId = params.taskItem.weatherTagId;
        roadTagId = params.taskItem.roadTagId;
      }

      assetService.getAssetTags()
        .then(function(tags) {
          initTags(tags);
        });
    }

    function initTags(tags) {
      $scope.weatherTags = [];
      $scope.roadTags = [];
      tags.forEach(function(it) {
        if(it.category === 'ROAD') {
          if(roadTagId === it.id) {
            $scope.roadTag = it;
          }
          $scope.roadTags.push(it);
        } else if(it.category === 'WEATHER') {
          if(weatherTagId === it.id) {
            $scope.weatherTag = it;
          }
          $scope.weatherTags.push(it);
        }
      });
    }

    $scope.onSaveButtonClick = function() {
      if(params.asset) {
        assetService.updateAssetTags(params.asset.assetNo, {
            weatherTagId: $scope.weatherTag.id,
            roadTagId: $scope.roadTag.id
          })
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else if(params.taskItem) {
        taskService.updateAssetTaskTags(params.taskItem.taskItemNo, {
            weatherTagId: $scope.weatherTag.id,
            roadTagId: $scope.roadTag.id
          })
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok',
              result: {
                roadTag: $scope.roadTag,
                weatherTag: $scope.weatherTag
              }
            });
          });
      }

    };
  }
]);
'use strict';

zongmu.controller("assetUploadController", ["$scope", "$log", "assetService", "serviceUrl",
  "$timeout", 'Upload', "dialog", "enumService", "$q", "breadCrumb", "assetViewTagService",
  function($scope, $log, assetService, serviceUrl, $timeout, $upload, dialog, enumService, $q, breadCrumbProvider, assetViewTagService) {

    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "上传记录",
        href: "assets.html"
      }, {
        text: "新建上传",
        href: "#"
      }]);
      $scope.uploading = false;
      $scope.assetType = "SINGLE";
      $scope.videoTypes = enumService.getUploadTypes();
      return true;
    }

    function initData() {
      $scope.selection = {};
      assetViewTagService.getAll()
        .then(function(tags) {
          $scope.viewTags = tags;
          $scope.viewTagsMap = {};
          $scope.viewTags.forEach(function(it) {
            it.items.forEach(function(item) {
              if(item.default) {
                $scope.viewTagsMap[it.id] = item.id;
              }
            });

            if(!$scope.viewTagsMap[it.id] && it.items.length > 0) {
              $scope.viewTagsMap[it.id] = it.items[0].id;
            }
          });
          //initTags(tags);
        });
    }

    function initTags(tags) {
      $scope.weatherTags = [];
      $scope.roadTags = [];
      $scope.weatherTag = null;
      $scope.roadTag = null;
      tags.forEach(function(it) {
        if(it.category === 'ROAD') {
          $scope.roadTags.push(it);
          if(it.default) {
            $scope.roadTag = it.id;
          }
        } else if(it.category === 'WEATHER') {
          $scope.weatherTags.push(it);
          if(it.default) {
            $scope.weatherTag = it.id;
          }
        }
      });

      if($scope.weatherTag === null && $scope.weatherTags.length > 0) {
        $scope.weatherTag = $scope.weatherTags[0].id;
      }

      if($scope.roadTag === null && $scope.roadTags.length > 0) {
        $scope.roadTag = $scope.roadTags[0].id;
      }
    }

    function getFileExt(fileName) {
      var parts = fileName.split(".");
      if(parts.length > 1) {
        return parts[parts.length - 1].toLowerCase();
      }
    }

    $scope.onUploadButtonClicked = function(files, file1, file2, file3, file4) {
      if(!$scope.name) {
        dialog.showError("请输入名称！");
        return;
      }
      var assetType = $scope.assetType;
      if(assetType === "PICTURE") {
        if(!files || files.length === 0) {
          dialog.showError("请选择上传的图片！");
          return;
        }

        var checkResult = true;
        files.forEach(function(it) {
          if(["image/jpeg", "image/png", "image/png", "image/bmp"].indexOf(it.type) === -1) {
            dialog.showError(`${it.name}不是正确的图片,请上传正确的图片格式的图片!`);
            checkResult = false;
            return;
          }
        })

        if(!checkResult) {
          return;
        }

        if(!$scope.recordTime) {
          dialog.showError("请设置视频录制时间！");
          return;
        }

        if($scope.recordTime > new Date()) {
          dialog.showError("视频录制时间不应该晚于当前时间！");
          return;
        }

        uploadPics(files);
        return;
      }

      if(!file1) {
        dialog.showError("请选择上传的文件1！");
        return;
      }

      if(getFileExt(file1.name) !== "avi") {
        dialog.showError("视频格式必须是AVI格式！");
        return;
      }

      if(assetType === 'FOUR') {
        if(!file2) {
          dialog.showError("请选择上传的文件2！");
          return;
        }
        if(!file3) {
          dialog.showError("请选择上传的文件3！");
          return;
        }
        if(!file4) {
          dialog.showError("请选择上传的文件4！");
          return;
        }

        var fourVideoNames = ["front.avi", "left.avi", "rear.avi", "right.avi"];
        var videoNameIndex = fourVideoNames.indexOf(file1.name.toLowerCase());
        if(videoNameIndex === -1) {
          dialog.showError("四路视频名称必须为front.avi，left.avi，rear.avi，right.avi。");
          return;
        }

        fourVideoNames.splice(videoNameIndex, 1);

        videoNameIndex = fourVideoNames.indexOf(file2.name.toLowerCase());
        if(videoNameIndex === -1) {
          dialog.showError("四路视频名称必须为front.avi，left.avi，rear.avi，right.avi。");
          return;
        }

        fourVideoNames.splice(videoNameIndex, 1);

        videoNameIndex = fourVideoNames.indexOf(file3.name.toLowerCase());
        if(videoNameIndex === -1) {
          dialog.showError("四路视频名称必须为front.avi，left.avi，rear.avi，right.avi。");
          return;
        }

        fourVideoNames.splice(videoNameIndex, 1);

        videoNameIndex = fourVideoNames.indexOf(file4.name.toLowerCase());
        if(videoNameIndex === -1) {
          dialog.showError("四路视频名称必须为front.avi，left.avi，rear.avi，right.avi。");
          return;
        }
      }

      if(!$scope.recordTime) {
        dialog.showError("请设置视频录制时间！");
        return;
      }

      if($scope.recordTime > new Date()) {
        dialog.showError("视频录制时间不应该晚于当前时间！");
        return;
      }

      var viewTags = [];

      Object.keys($scope.viewTagsMap).forEach(function(key) {
        if($scope.viewTagsMap[key] != null || $scope.viewTagsMap[key] != undefined) {
          viewTags.push({
            assetViewTagId: key,
            assetViewTagItemId: $scope.viewTagsMap[key]
          });
        }
      });

      $scope.uploading = true;
      $scope.showLoading();
      assetService.createAsset({
        name: $scope.name,
        assetType: assetType,
        recordTime: $scope.recordTime,
        memo: $scope.memo,
        weatherTagId: $scope.weatherTag,
        roadTagId: $scope.roadTag,
        viewTags: viewTags
      }).then(function(data) {

        if($scope.assetType === 'FOUR') {
          $q.all([uploadFile(data.assetNo, file1), uploadFile(data.assetNo, file2), uploadFile(data.assetNo, file3), uploadFile(data.assetNo, file4)])
            .then(function(results) {
              var res = results.filter(function(it) {
                return it;
              });

              if(res.length === 4) {
                uploadFinish(true);
              } else {
                uploadFinish(false);
              }

            });
        } else {
          uploadFile(data.assetNo, file1).then(function(res) {
            uploadFinish(res);
          });
        }
      });
    };

    function uploadPics(files) {
      var viewTags = [];
      Object.keys($scope.viewTagsMap).forEach(function(key) {
        if($scope.viewTagsMap[key] != null || $scope.viewTagsMap[key] != undefined) {
          viewTags.push({
            assetViewTagId: key,
            assetViewTagItemId: $scope.viewTagsMap[key]
          });
        }
      });
      $scope.showLoading();
      assetService.createAsset({
        name: $scope.name,
        assetType: $scope.assetType,
        recordTime: $scope.recordTime,
        weatherTagId: $scope.weatherTag,
        roadTagId: $scope.roadTag,
        memo: $scope.memo,
        viewTags: viewTags
      }).then(function(data) {
        $q.all(files.map(function(it) {
          return uploadFile(data.assetNo, it);
        })).then(function() {
          uploadFinish(true);
        });
      });
    }

    function uploadFile1(assetNo, file1, file2, file3, file4) {
      uploadFile(assetNo, file1)
        .then(function(res) {
          if($scope.assetType === 'SINGLE') {
            uploadFinish(res);
          } else {
            if(res) {
              $scope.message = "第一个文件上传成功，正在上传第二个文件...";
              uploadFile2(assetNo, file2, file3, file4)
            }
          }
        });
    }

    function uploadFile2(assetNo, file2, file3, file4) {
      uploadFile(assetNo, file2)
        .then(function(res) {
          if(res) {
            uploadFile3(assetNo, file3, file4);
          }
        });
    }

    function uploadFile3(assetNo, file3, file4) {
      uploadFile(assetNo, file3)
        .then(function(res) {
          if(res) {
            uploadFile4(assetNo, file4);
          }
        });
    }

    function uploadFile4(assetNo, file4) {
      uploadFile(assetNo, file4)
        .then(function(res) {
          uploadFinish(res);
        });
    }

    function uploadFinish(res) {
      $scope.hideLoading();
      if(res) {
        dialog.showInfo("上传成功！")
          .then(function() {
            window.location.href = "assets.html";
          });
      } else {
        dialog.showInfo("上传失败！")
          .then(function() {
            window.location.href = "assets.html";
          });
      }

    }

    function uploadFile(assetNo, file) {
      var dtd = $q.defer();
      var url = serviceUrl + 'assets/' + assetNo + '/upload';
      $log.info("Start to uploading file " + file.name + " ...");
      file.upload = $upload.upload({
        url: url,
        data: {
          file: file
        },
      });

      file.upload.then(function(response) {
        $log.info("Upload file " + file.name + " success.");
        dtd.resolve(true);
      }, function(response) {
        $log.info("Upload file " + file.name + " failed.");
        //dialog.showInfo("上传失败！")
        dtd.resolve(false);
        /*if (response.status > 0)
          $scope.errorMsg = response.status + ': ' + response.data;*/
      }, function(evt) {
        // Math.min is to fix IE which reports 200% sometimes
        file.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
        console.log(file.progress);
        $log.info("Upload file " + file.name + " progress " + file.progress);
        //$scope.$apply();
      });

      return dtd.promise;
    }

    $scope.onCancelClicked = function() {
      window.history.back();
    }

  }
]);
'use strict';

zongmu.controller("assetViewTagUpdateDialogController", ['$scope', 'dialog', 'assetService', 'assetViewTagService',
  function($scope, dialog, assetService, assetViewTagService) {
    var params = $scope.ngDialogData;

    init();

    function init() {

      assetViewTagService.getAll()
        .then(function(tags) {
          $scope.viewTags = tags;
          $scope.viewTagsMap = {};
          params.asset.viewTags.forEach(function(it) {
            $scope.viewTagsMap[it.assetViewTagId] = it.assetViewTagItemId;
          });
        });
    }

    function getSelected() {
      var viewTags = [];

      Object.keys($scope.viewTagsMap).forEach(function(key) {
        if($scope.viewTagsMap[key] != null || $scope.viewTagsMap[key] != undefined) {
          viewTags.push({
            assetViewTagId: key,
            assetViewTagItemId: $scope.viewTagsMap[key]
          });
        }
      });

      return viewTags;
    }

    $scope.onSaveButtonClick = function() {
      assetService.updateAssetViewTags(params.asset.assetNo, {
          "items": getSelected()
        })
        .then(function() {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);
'use strict';

zongmu.controller("assetController", ["$scope", "assetService", "breadCrumb", "enumService", "dialog",
  "algorithmService", "assetViewTagService", "viewTagService",
  function($scope, assetService, breadCrumbProvider, enumService, dialog, algorithmService, assetViewTagService, viewTagService) {
    var pageIndex = $.url().param("pageIndex");
    $scope.taskStatus = enumService.getTaskStatus();
    $scope.assetTypes = enumService.getUploadTypes();
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;

    function QueryParams() {
      this.taskName = null;
      this.taskItemNo = null;
      this.assetName = null;
      this.assetNo = null;
      this.uploadDate = {
        from: null,
        to: null
      };
      this.videoLength = {
        op: null,
        value: null
      };

      this.taskFinishDate = {
        from: null,
        to: null
      };

      this.videoRecordDate = {
        from: null,
        to: null
      };

      init();

      function init() {
        $scope.assetTypes.forEach(function(it) {
          it.checked = false;
        });

        ($scope.algorithms || []).forEach(function(it) {
          it.checked = false;
        });

        $scope.taskStatus.forEach(function(it) {
          it.checked = false;
        });

        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });

        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });
      }

      this.getData = function() {

        this.assetTypes = [];
        $scope.assetTypes.forEach(function(it) {
          if(it.checked) {
            this.assetTypes.push(it.name);
          }
        }.bind(this));

        this.algorithmIds = [];
        ($scope.algorithms || []).forEach(function(it) {
          if(it.checked) {
            this.algorithmIds.push(it.id);
          }
        }.bind(this));

        this.taskItemStatus = [];
        $scope.taskStatus.forEach(function(it) {
          if(it.checked) {
            this.taskItemStatus.push(it.name);
          }
        }.bind(this));

        this.assetViewTagItemIds = [];
        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.assetViewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        this.viewTagItemIds = [];
        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.viewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        return this;
      };
    }

    $scope.queryParams = new QueryParams();

    algorithmService.getAlgorithms()
      .then(function(data) {
        $scope.algorithms = data;
      });

    assetViewTagService.getAll()
      .then(function(tags) {
        $scope.viewTags = tags;
      });

    viewTagService.getAllViewTags()
      .then(function(viewTags) {
        $scope.taskViewTags = viewTags;
      });

    initView() && initData();
    getAssetTags();

    function initView() {

      $scope.s = {};
      breadCrumbProvider.setHistories([{
        text: "上传记录",
        href: "assets.html"
      }]);
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "assetType",
        text: "类型"
      }, {
        name: "recordTime",
        text: "录制时间"
      }, {
        name: "recordLength",
        text: "录制时长"
      }, {
        name: "pictureCount",
        text: "图片张数"
      }, {
        name: "op",
        text: "操作"
      }];
      $scope.ops = enumService.getOps();

      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍后...");
      assetService.queryAssets(pageIndex, $scope.queryParams.getData())
        .then(function(pageData) {
          $scope.hideLoading();
          $scope.dataset = pageData.content;
          $scope.pageData = {
            totalPage: pageData.totalPages,
            pageIndex: pageData.number
          };
        });
    }

    function getAssetTags() {
      assetService.getAssetTags()
        .then(function(tags) {
          $scope.tags = tags;
        })
    }

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    $scope.onSearchButtonClick = function() {
      initData()
        //    var filter = "";
        //
        //    if($scope.s.assetType) {
        //      filter += "assetType eq " + $scope.s.assetType + ";";
        //    }
        //
        //    if($scope.s.uploadTimeOp && $scope.s.uploadTime) {
        //      filter += "createTime " + $scope.s.uploadTimeOp + " " + $scope.s.uploadTime.toISOString() + ";";
        //    }
        //
        //    if($scope.s.recordTimeOp && $scope.s.recordTime) {
        //      filter += "uploadTime " + $scope.s.recordTimeOp + " " + $scope.s.recordTime.toISOString() + ";";
        //    }
        //
        //    if($scope.s.recordLengthOp && $scope.s.recordLength) {
        //      filter += "recordLength " + $scope.s.recordLengthOp + " " + $scope.s.recordLength + ";";
        //    }
        //
        //    var tags = [];
        //    ($scope.tags || []).forEach(function(it) {
        //      if(it.isSelected) {
        //        tags.push(it.id);
        //      }
        //    });
        //
        //    if(tags.length > 0) {
        //      filter += "tags in " + JSON.stringify(tags) + ";";
        //    }
        //
        //    $scope.showLoading();
        //    $scope.setLoadingText("正在搜索，请稍后...");
        //    assetService.getAssets(pageIndex, filter)
        //      .then(function(pageData) {
        //        $scope.hideLoading();
        //        $scope.dataset = pageData.content;
        //        $scope.pageData = {
        //          totalPage: pageData.totalPages,
        //          pageIndex: pageData.number
        //        };
        //      });
    };

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
      //window.location.href = "assets.html?pageIndex=" + pageIndex;
    });

    $scope.onUploadButtonClick = function() {
      window.location.href = "asset.upload.html";
    };

    $scope.onNewTaskButtonClick = function(rowData) {
      window.location.href = "asset.new.html?assetNo=" + rowData.assetNo;
    };

    $scope.onDeleteButtonClick = function(rowData) {

      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条记录吗？",
        onConfirm: function() {
          $scope.showLoading();
          $scope.setLoadingText("正在删除视频和视频相关数据，请稍后...");
          assetService.deleteAsset(rowData.assetNo)
            .then(function() {
              $scope.hideLoading();
              initData();
            });
        }
      });
    };
  }
]);
'use strict';

zongmu.controller("baseTaskController", ["$scope", "breadCrumb", "serviceUrl", "algorithmService", "appEnv",

  function($scope, breadCrumbProvider, serviceUrl, algorithmService, appEnv) {

    $scope.setTitle("我的任务");

    $scope.sidebar = [{
      name: "task-management",
      text: "任务管理",
      icon: "tasks",
      items: [{
        name: "task-record",
        text: "任务记录",
        href: serviceUrl + "page/task/index.html"
      }, {
        name: "upload-task-record",
        text: "上传记录",
        href: serviceUrl + "page/task/assets.html"
      }, {
        name: "review-task-record",
        text: "审核记录",
        href: serviceUrl + "page/task/reviews.html"
      }]
    }, {
      name: "report",
      text: "信息统计",
      icon: "dashboard",
      items: []
    }, {
      name: "user-profile",
      text: "个人中心",
      icon: "user",
      items: [{
        name: "user-profile",
        text: "个人信息",
        href: serviceUrl + "page/user/profile.html"
      }, {
        name: "user-point",
        text: "会员积分",
        href: serviceUrl + "page/user/point.html"
      }, {
        name: "user-secret",
        text: "安全中心",
        href: serviceUrl + "page/user/resetpassword.html"
      }, {
        name: "point-approve-list",
        text: "提现申请记录",
        href: serviceUrl + "page/user/point.request.list.html"
      }]
    }];

    initView();

    function initData() {
      algorithmService.getAlgorithms()
        .then(function(data) {
          data.forEach(function(it) {
            $scope.sidebar[1].items.push({
              name: it.id + "",
              text: it.name + "路测数据统计",
              href: serviceUrl + "page/task/bsd.html?reportId=" + it.id
            });
          })
        });
    }

    function initView() {
      var role = Cookies.get("role");
      if(role === "NORMAL") {
        hideGroups(["report"]);
        hideGroupItems(["upload-task-record", "review-task-record", "point-approve-list"]);
      } else if(role === "REVIEW") {
        hideGroups(["report"]);
        hideGroupItems(["upload-task-record", "point-approve-list"]);
      } else if(role === "FINANCE") {
        hideGroups(["report"]);
        hideGroupItems(["upload-task-record", "review-task-record"]);
      } else if(role === "UPLOAD") {
        hideGroups(["report"]);
        var arrs = ["point-approve-list", "review-task-record"];
        if(appEnv === "aliyun"){
          arrs.push("upload-task-record")
        }
        hideGroupItems(arrs);
      } else if(role === "ADMIN") {
        if(appEnv === "aliyun") {
          hideGroupItems(["upload-task-record"]);
        }
        initData();
      } else if(role === "SUPER") {
        hideGroupItems(["upload-task-record", "point-approve-list", "review-task-record"]);
        initData();
      }
    }

    $scope.hideSideItems = function(names) {
      hideGroupItems(names);
    }

    $scope.hideSideGroups = function(groups) {
      hideGroups(groups);
    };

    function hideGroups(groups) {
      $scope.sidebar.forEach(function(group) {
        if(groups.indexOf(group.name) !== -1) {
          group.visibility = false;
        } else {
          delete group.visibility;
        }
      });
    }

    function hideGroupItems(names) {
      $scope.sidebar.forEach(function(group) {
        group.items.forEach(function(it) {
          if(names.indexOf(it.name) !== -1) {
            it.visibility = false;
          } else {
            delete it.visibility;
          }
        });
      });
    }
  }
]);
'use strict';

zongmu.controller("bsdReportController", ["$scope", "breadCrumb", "reportService", "dialog", "enumService", "assetViewTagService", "nullUtils", "viewTagService",
  function($scope, breadCrumbProvider, reportService, dialog, enumService, assetViewTagService, nullUtils, viewTagService) {
    var reportId = $.url().param("reportId");
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;
    $scope.taskStatus = [{
      name: "Accept",
      text: "接受"
    }, {
      name: "New",
      text: "未接受"
    }, {
      name: "Pass",
      text: "审核通过"
    }, {
      name: "Reject",
      text: "未审核通过"
    }];
    $scope.queryParams = new QueryParams();
    initView() && initData();
    $scope.onButtonClick = function() {
      if($scope.from === undefined || $scope.from === null) {
        dialog.showError("开始时间段不能为空！");
        return;
      }

      if(!$scope.to === undefined || $scope.to === null) {
        dialog.showError("结束时间段不能为空！");
        return;
      }

      if($scope.from >= $scope.to) {
        dialog.showError("开始时间不能大于结束时间！");
        return;
      }

      initData();
    };

    function QueryParams() {
      this.taskName = null;
      this.taskItemNo = null;
      this.assetName = null;
      this.assetNo = null;
      this.uploadDate = {
        from: null,
        to: null
      };
      this.taskDate = {
        from: null,
        to: null
      };
      this.recordLength = {
        op: null,
        value: null
      };

      this.taskFinishDate = {
        from: null,
        to: null
      };

      this.assetRecordDate = {
        from: null,
        to: null
      };

      init();

      function init() {
        $scope.taskStatus.forEach(function(it) {
          it.checked = false;
        });

        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });

        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });
      }

      this.getData = function() {
        this.taskItemStatus = [];
        $scope.taskStatus.forEach(function(it) {
          if(it.checked) {
            this.taskItemStatus.push(it.name);
          }
        }.bind(this));

        this.assetViewItemIds = [];
        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.assetViewItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        this.viewTagItemMap = getTaskViewItemIds();

        return this;
      };
    }

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    function searching() {
      $scope.showLoading();
      $scope.setLoadingText("正在查询，请稍等...");
      reportService.search(reportId, $scope.queryParams.getData())
        .then(function(data) {
          $scope.hideLoading();
          $scope.tables = [];

          data.tables.forEach(function(it) {
            $scope.tables.push(initTable(it));
          });
          var sumTab = maxTable(data.tables);
          if(sumTab) {
            $scope.tables.push(sumTab);
          }

          console.log(data.tables);

        });
    }

    function initView() {
      if(!reportId) {
        dialog.showError("参数错误！");
        return false;
      }

      $scope.reportId = reportId + "";
      breadCrumbProvider.setHistories([{
        text: "路测视频统计",
        href: "bsd.html"
      }]);
      $scope.hours = enumService.getHours();
      $scope.from = 7;
      $scope.to = 11;
      $scope.viewTags = [];
      $scope.taskViewTags = [];
      $scope.expandSearch = false;
      $scope.ops = enumService.getOps();

      assetViewTagService.getAll()
        .then(function(tags) {
          $scope.viewTags = tags;
        });

      viewTagService.getViewTags(reportId)
        .then(function(viewTags) {
          $scope.taskViewTags = viewTags;
        });
      return true;
    }

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    function getTaskViewItemIds() {
      var map = {};
      $scope.taskViewTags.forEach(function(it) {
        map[it.id] = [];
        it.items.forEach(function(item) {
          if(item.checked) {
            map[it.id].push(item.id);
          }
        });

        if(map[it.id].length === 0) {
          delete map[it.id];
        }
      });

      return map;
    };

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在查询，请稍等...");
      var ids = [];
      $scope.viewTags.forEach(function(it) {
        it.items.forEach(function(item) {
          if(item.checked) {
            ids.push(item.id);
          }
        });
      });

      searching();

      //    reportService.getNewBsdReport(reportId, ids.join(","))
      //      .then(function(data) {
      //        $scope.hideLoading();
      //        $scope.tables = [];
      //
      //        data.tables.forEach(function(it) {
      //          $scope.tables.push(initTable(it));
      //        });
      //        var sumTab = sumTable(data.tables);
      //        if(sumTab) {
      //          $scope.tables.push(sumTab);
      //        }
      //
      //        console.log(data.tables);
      //
      //      });
      //    reportService.getBsdReport(reportId, $scope.from, $scope.to)
      //      .then(function(data) {
      //        $scope.hideLoading();
      //        data.videoTable.rows.forEach(function(row) {
      //          Object.keys(row.data).forEach(function(key) {
      //            if(row.data[key]) {
      //              row.data[key] = (row.data[key] / 60).toFixed(2);
      //            } else {
      //              row.data[key] = 0;
      //            }
      //          });
      //        });
      //        $scope.videoTable = initTable(data.videoTable);
      //        $scope.picTable = initTable(data.pictureTable);
      //      });
    }

    function sumTable(tables) {
      var table = {
        name: "总计",
        headers: [{
          "name": "pic",
          "text": "图片"
        }, {
          "name": "video",
          "text": "视频"
        }],
        rows: []
      };

      var table = {
        columns: [{
          name: "text",
          text: "总计\\类型"
        }, {
          "name": "pic",
          "text": "图片"
        }, {
          "name": "video",
          "text": "视频"
        }],
        dataset: [],
        name: "总计"
      };

      var sumPic = 0;
      var sumVideo = 0;
      tables.forEach(function(it, index) {
        it.rows.forEach(function(row, rowIndex) {
          Object.keys(row.data).forEach(function(key) {
            if(rowIndex === 0) {
              sumVideo += row.data[key];
            }
            if(rowIndex === 1) {
              sumPic += row.data[key];
            }
          });
        });

        //      if(index === 0) {
        //        table = $.extend(true, {}, it);
        //      } else {
        //        it.rows.forEach(function(row, rowIndex) {
        //          var sumRow = table.rows[rowIndex];
        //          Object.keys(row.data).forEach(function(key) {
        //            sumRow.data[key] = sumRow.data[key] + row.data[key];
        //          });
        //
        //          table.rows[rowIndex] = sumRow;
        //        });
        //      }
      });
      table.dataset.push({
        "text": "总计",
        "pic": sumPic,
        "video": calc(sumVideo)
      });

      return table;
    }

    function initTable(tableData) {

      var columns = [{
        name: "text",
        text: "类型\\场景属性"
      }].concat(tableData.headers);

      var dataset = tableData.rows.map(function(it, index) {
        var row = {
          text: it.text
        };
        if(index === 0) {
          it.data1 = {};
          Object.keys(it.data).forEach(function(key) {
            it.data1[key] = calc(it.data[key]);
          });
          $.extend(true, row, it.data1);
        } else {
          $.extend(true, row, it.data);
        }

        return row;
      });

      return {
        columns: columns,
        dataset: dataset,
        name: tableData.name
      };
    }

    function calc(val) {
      if(val === 0) {
        return val;
      }

      if(val < 60) {
        return `${val}秒`;
      }

      var sec = val % 60;
      var min = null;
      if(sec === 0) {
        min = val / 60;
      } else {
        min = ((val - sec) / 60) % 60;
      }
      if(val < 60 * 60) {
        return `${min}分 ${sec}秒`;
      }

      var hour = (val - min * 60 - sec) / 3600;
      return `${hour}时${min}分 ${sec}秒`;
    }

    function maxTable(tables) {
      var table = {
        columns: [{
          name: "text",
          text: "总计\\类型"
        }, {
          "name": "pic",
          "text": "图片"
        }, {
          "name": "video",
          "text": "视频"
        }],
        dataset: [],
        name: "总计"
      };

      var maxVideo = 0;
      var maxPicture = 0;

      tables.forEach(function(it, index) {
        it.rows.forEach(function(row, rowIndex) {
          Object.keys(row.data).forEach(function(key) {
            if(rowIndex === 0) {
              if(maxVideo < row.data[key]) {
                maxVideo = row.data[key];
              }
            }
            if(rowIndex === 1) {
              if(maxPicture < row.data[key]) {
                maxPicture = row.data[key];
              }
            }
          });
        });
      });

      table.dataset.push({
        "text": "总计",
        "pic": maxPicture,
        "video": calc(maxVideo)
      });

      return table;
    }

  }
]);
'use strict';

zongmu.controller("chooseTagsController", ['$scope', 'dialog', 'assetService', 'taskService',
  function($scope, dialog, assetService, taskService) {
    var params = $scope.ngDialogData;
    initData();

    function initData() {
      var map = {};
      angular.forEach(params.selectedTags || [], function(it, index) {
        map[it.id] = it;
      });

      assetService.getAssetTags()
        .then(function(tags) {
          $scope.tags = tags;
          angular.forEach($scope.tags, function(it, index) {
            it.isSelected = map[it.id] !== undefined;
          });
        });
    }

    $scope.$watch("allSelected", function() {
      angular.forEach($scope.tags, function(it, index) {
        it.isSelected = $scope.allSelected;
      });
    });

    $scope.onOkClick = function() {
      var selectedTags = [];
      angular.forEach($scope.tags, function(it, index) {
        if(it.isSelected) {
          selectedTags.push(it.id);
        }
      });

      if(params.type === "task") {
        taskService.updateAssetTags(params.taskNo, selectedTags)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        assetService.updateAssetTags(params.assetNo, selectedTags)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }
    };

  }
]);
'use strict';

zongmu.controller("taskItemDetailController", ['$q', '$scope', 'taskService', 'taskRecordService', 'reviewRecordService', 'dialog', "markUtil",
  function($q, $scope, taskService, taskRecordService, reviewRecordService, dialog, markUtil) {
    var taskItemNo = $.url().param("taskItemNo");

    initView() && initData();

    function initView() {
      $scope.setTitle("任务详细信息");

      $scope.taskRecordColumns = [{
        name: "taskRecordNo",
        text: "标注记录号"
      }, {
        name: "userName",
        text: "标注者"
      }, {
        name: "startTime",
        text: "开始时间"
      }, {
        name: "endTime",
        text: "结束时间"
      }, {
        name: "status",
        text: "状态"
      }, {
        name: "point",
        text: "奖励"
      }];

      $scope.reviewRecordColumns = [{
        name: "reviewRecordNo",
        text: "审核记录号"
      }, {
        name: "userName",
        text: "审核者"
      }, {
        name: "startTime",
        text: "开始时间"
      }, {
        name: "endTime",
        text: "结束时间"
      }, {
        name: "status",
        text: "状态"
      }];

      var role = Cookies.get("role");
      $scope.role = role;
      $scope.canReview = ["NORMAL", "FINANCE"].indexOf(role) === -1;
      return true;
    }

    $scope.onContinueMarkButtonClick = function() {
      gotoNextPage();
    };

    function gotoNextPage() {
      if($scope.task.taskRecordNo) {
        var pageName = markUtil.getMarkPageUrl($scope.task.assetType, $scope.task.taskType, $scope.task.taskRecordNo);
        window.location.href = `../mark/${pageName}`;
        //      if($scope.task.taskType === "VIDEO") {
        //        if($scope.task.taskItemFiles.length === 4) {
        //          window.location.href = "../mark/video.four.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //        } else {
        //          if($scope.task.assetType === "PICTURE") {
        //            window.location.href = "../mark/pic.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //          } else {
        //            window.location.href = "../mark/video.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //          }
        //        }
        //      } else {
        //        if($scope.task.taskItemFiles.length === 4) {
        //          window.location.href = "../mark/pic.four.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //        } else {
        //          window.location.href = "../mark/pic.html?taskRecordNo=" + $scope.task.taskRecordNo;
        //        }
        //
        //      }
      } else {
        dialog.showError("参数错误");
      }
    }

    $scope.onAcceptTaskClick = function() {
      taskService.acceptTask(taskItemNo)
        .then(function(data) {
          var pageName = markUtil.getMarkPageUrl(data.assetType, data.taskType, data.taskRecordNo);
          window.location.href = `../mark/${pageName}`;
          //var pageName = markUtil.getMarkPage(data);
          //window.location.href = "../mark/" + pageName + "?taskRecordNo=" + data.taskRecordNo;
        });
    };

    function initData() {
      if(!taskItemNo) {
        dialog.showError("参数错误");
      } else {
        taskService.getTaskDetail(taskItemNo)
          .then(function(task) {
            $scope.task = task;
          });
      }
    }
  }
]);
'use strict';

zongmu.controller("exportFailedDialogController", ['$scope','dialog',
  function($scope, dialog) {    

    $scope.taskItems = $scope.ngDialogData.taskItems;

  }
]);
'use strict';

zongmu.controller("myTasksController", ["$scope", "taskRecordService", "breadCrumb", "dialog", "enumService",
  "algorithmService", "assetViewTagService", "viewTagService", "markUtil",
  function($scope, taskRecordService, breadCrumbProvider, dialog, enumService,
    algorithmService, assetViewTagService, viewTagService, markUtil) {
    var pageIndex = $.url().param("pageIndex") || 0;
    var tabIndex = 0;

    $scope.assetTypes = enumService.getUploadTypes();
    $scope.ops = enumService.getOps();
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;
    $scope.taskStatus = [{
      name: "INPROGRESS",
      text: "标注进行中"
    }, {
      name: "WAITTING",
      text: "等待审核"
    }, {
      name: "REVIEWING",
      text: "正在进行审核"
    }, {
      name: "ACCEPTED",
      text: "审核通过"
    }, {
      name: "REJECTED",
      text: "审核失败"
    }];

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    $scope.canView = function(rowData) {
      var role = Cookies.get("role");
      if(role === "ADMIN") {
        return true;
      } else {
        if(["WAITTING", "ACCEPTED"].indexOf(rowData.status) !== -1) {
          return false;
        }

        return true;
      }
    };

    function QueryParams() {
      this.taskName = null;
      this.taskItemNo = null;
      this.assetName = null;
      this.assetNo = null;
      this.uploadDate = {
        from: null,
        to: null
      };
      this.videoLength = {
        op: null,
        value: null
      };

      this.taskFinishDate = {
        from: null,
        to: null
      };

      this.videoRecordDate = {
        from: null,
        to: null
      };

      init();

      function init() {
        $scope.assetTypes.forEach(function(it) {
          it.checked = false;
        });

        ($scope.algorithms || []).forEach(function(it) {
          it.checked = false;
        });

        $scope.taskStatus.forEach(function(it) {
          it.checked = false;
        });

        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });

        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });
      }

      this.getData = function() {

        this.assetTypes = [];
        $scope.assetTypes.forEach(function(it) {
          if(it.checked) {
            this.assetTypes.push(it.name);
          }
        }.bind(this));

        this.algorithmIds = [];
        ($scope.algorithms || []).forEach(function(it) {
          if(it.checked) {
            this.algorithmIds.push(it.id);
          }
        }.bind(this));

        this.taskRecordStatus = [];
        $scope.taskStatus.forEach(function(it) {
          if(it.checked) {
            this.taskRecordStatus.push(it.name);
          }
        }.bind(this));

        this.assetViewTagItemIds = [];
        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.assetViewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        this.viewTagItemIds = [];
        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.viewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        return this;
      };
    }

    $scope.queryParams = new QueryParams();
    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "任务记录",
        href: "index.html"
      }]);

      $scope.tabs = [{
        name: "all",
        text: "全部",
        active: true
      }, {
        name: "INPROGRESS",
        text: "标注进行中"
      }, {
        name: "WAITTING",
        text: "等待审核"
      }, {
        name: "REVIEWING",
        text: "正在进行审核"
      }, {
        name: "ACCEPTED",
        text: "审核通过"
      }, {
        name: "REJECTED",
        text: "审核失败"
      }];

      $scope.columns = [{
        name: "taskItemNo",
        text: "任务名称"
      }, {
        name: "taskType",
        text: "任务类型"
      }, {
        name: "point",
        text: "金币"
      }, {
        name: "status",
        text: "状态"
      }, {
        name: "op",
        text: "操作"
      }];
      return true;
    }

    algorithmService.getAlgorithms()
      .then(function(data) {
        $scope.algorithms = data;
      });

    assetViewTagService.getAll()
      .then(function(tags) {
        $scope.viewTags = tags;
      });

    viewTagService.getAllViewTags()
      .then(function(viewTags) {
        $scope.taskViewTags = viewTags;
      });

    $scope.$on("onTabChanged", function(tabScope, item, index) {
      pageIndex = 0;
      tabIndex = index;
      initData();
    });

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    $scope.onSearchButtonClick = function() {
      initData();
    };

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      taskRecordService.search(pageIndex, $scope.queryParams.getData())
        .then(function(result) {
          $scope.hideLoading();
          $scope.tasks = result.content;
          $scope.pageData = {
            totalPage: result.totalPages,
            pageIndex: result.number
          };
        });
      //    taskRecordService.getMyTaskRecords(pageIndex, tabIndex)
      //      .then(function(result) {
      //        $scope.hideLoading();
      //        $scope.tasks = result.content;
      //        $scope.pageData = {
      //          totalPage: result.totalPages,
      //          pageIndex: result.number
      //        };
      //      });
    }

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
      //window.location.href = "index.html?pageIndex=" + pageIndex;
    });

    $scope.onCancelButtonClicked = function(taskRecord) {
      dialog.showConfirm({
        title: "提示",
        message: "确定要放弃任务么？",
        onConfirm: function() {
          $scope.showLoading();
          $scope.setLoadingText("正在取消任务，请稍后...");
          taskRecordService.cancelTask(taskRecord.taskRecordNo)
            .then(function() {
              $scope.hideLoading();
              window.location.href = "../home/index.html";
            });
        }
      });

    };

    $scope.onViewButtonClicked = function(taskRecord) {
      var pageName = markUtil.getMarkPageUrl(taskRecord.assetType, taskRecord.taskType, taskRecord.taskRecordNo);;
      window.location.href = `../mark/${pageName}`;
      //    if(taskRecord.assetType === "FOUR") {
      //      pageName = "video.four.html";
      //    } else if(taskRecord.assetType === "SINGLE") {
      //      pageName = taskRecord.taskType === 'PICTURE' ? "pic.html" : "video.html";
      //    } else if(taskRecord.assetType === "PICTURE") {
      //      pageName = "pic.html";
      //    }
      //
      //    if(pageName) {
      //      window.location.href = "../mark/" + pageName + "?taskRecordNo=" + taskRecord.taskRecordNo;
      //    }
    }

  }
]);
'use strict';

zongmu.controller("reviewDetailController", ["$q", "$scope", "reviewRecordService", "dialog", "breadCrumb", "rejectReasonService", "taskRecordService",
  function($q, $scope, reviewRecordService, dialog, breadCrumbProvider, rejectReasonService, taskRecordService) {
    var reviewRecordNo = $.url().param("reviewRecordNo");

    initView() && initData();

    function initView() {
      var role = Cookies.get("role");
      $scope.role = role;
      breadCrumbProvider.setHistories([{
        text: "审核记录",
        href: "reviews.html"
      }, {
        text: "记录详情",
        href: "#"
      }]);

      if(!reviewRecordNo) {
        dialog.showError("参数错误");
        return false;
      }
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      reviewRecordService.getReviewRecord(reviewRecordNo)
        .then(function(review) {
          $scope.review = review;
          if(review.status === "FAILED" && review.reasonId) {
            getReason(review.reasonId)
          } else {
            $scope.hideLoading();
          }
        });
    }

    function getReason(reasonId) {
      rejectReasonService.getReason(reasonId)
        .then(function(data) {
          $scope.hideLoading();
          $scope.reason = data;
        });
    }

    $scope.onReviewPassButtonClick = function() {
      taskRecordService.reviewPass($scope.review.taskRecordNo)
        .then(function() {
          initData();
        });
    };

    $scope.onReviewFailedButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: $scope.review
        },
        onConfirm: function() {
          initData();
        }
      });
    };

  }
]);
'use strict';

zongmu.controller("reviewFailedDialogController", ['$scope', 'dialog', 'taskRecordService', 'rejectReasonService', 'reviewRecordService',
  function($scope, dialog, taskRecordService, rejectReasonService, reviewRecordService) {
    var params = $scope.ngDialogData;

    init();

    $scope.onOkClick = function() {
      if(!$scope.data.reason) {
        dialog.showError("请选择原因！");
        return;
      }
      var data = {
        reasonId: $scope.data.reason.id,
        memo: $scope.data.memo
      };

      if(params.batch) {
        data.reviewRecordNos = params.reviewRecordNos;
        reviewRecordService.batchReviewFailed(data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        taskRecordService.reviewFail(params.reviewRecord.taskRecordNo, data)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }

    };

    function init() {
      $scope.data = {};
      rejectReasonService.getReasons()
        .then(function(data) {
          $scope.reasons = data;
          $scope.reasons.forEach(function(it) {
            if(it.default) {
              $scope.data.reason = it;
            }
          });
          if(!$scope.data.reason && $scope.reasons.length > 0) {
            $scope.data.reason = $scope.reasons[0];
          }
        });
    }

  }
]);
'use strict';

zongmu.controller("reviewRecordsController", ["$scope", "reviewRecordService",
  "dialog", "breadCrumb", "taskRecordService", "markUtil", "algorithmService",
  "assetViewTagService", "viewTagService", "enumService", "rejectReasonService",
  function($scope, reviewRecordService, dialog, breadCrumbProvider, taskRecordService,
    markUtil, algorithmService, assetViewTagService, viewTagService, enumService, rejectReasonService) {
    var pageIndex = $.url().param("pageIndex") || 0;
    var tabIndex = 0;
    $scope.assetTypes = enumService.getUploadTypes();
    $scope.taskStatus = enumService.getReviewTaskStatus();
    $scope.ops = enumService.getOps();
    $scope.assetButtonExpand = false;
    $scope.taskViewButtonExpand = false;

    function QueryParams() {
      this.taskName = null;
      this.taskItemNo = null;
      this.assetName = null;
      this.assetNo = null;
      this.userName = null;
      this.uploadDate = {
        from: null,
        to: null
      };
      this.videoLength = {
        op: null,
        value: null
      };

      this.taskFinishDate = {
        from: null,
        to: null
      };

      this.videoRecordDate = {
        from: null,
        to: null
      };

      init();

      function init() {
        $scope.assetTypes.forEach(function(it) {
          it.checked = false;
        });

        ($scope.algorithms || []).forEach(function(it) {
          it.checked = false;
        });

        $scope.taskStatus.forEach(function(it) {
          it.checked = false;
        });

        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });

        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            item.checked = false;
          });
        });

        ($scope.reasons || []).forEach(function(it) {
          it.checked = false;
        }.bind(this));
      }

      this.getData = function() {

        this.assetTypes = [];
        $scope.assetTypes.forEach(function(it) {
          if(it.checked) {
            this.assetTypes.push(it.name);
          }
        }.bind(this));

        this.algorithmIds = [];
        ($scope.algorithms || []).forEach(function(it) {
          if(it.checked) {
            this.algorithmIds.push(it.id);
          }
        }.bind(this));

        this.status = [];
        $scope.taskStatus.forEach(function(it) {
          if(it.checked) {
            this.status.push(it.name);
          }
        }.bind(this));

        this.assetViewTagItemIds = [];
        ($scope.viewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.assetViewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        this.viewTagItemIds = [];
        ($scope.taskViewTags || []).forEach(function(it) {
          it.items.forEach(function(item) {
            if(item.checked) {
              this.viewTagItemIds.push(item.id);
            }
          }.bind(this));
        }.bind(this));

        this.reasonIds = [];
        ($scope.reasons || []).forEach(function(it) {
          if(it.checked) {
            this.reasonIds.push(it.id);
          }
        }.bind(this));

        return this;
      };
    }

    $scope.queryParams = new QueryParams();

    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "审核记录",
        href: "reviews.html"
      }]);
      $scope.tabs = [{
        name: "all",
        text: "全部",
        active: true
      }, {
        name: "WAITTING",
        text: "待审核"
      }, {
        name: "INPROGRESS",
        text: "审核中"
      }, {
        name: "PASS",
        text: "审核通过"
      }, {
        name: "FAILED",
        text: "审核失败"
      }];

      $scope.columns = [{
        name: "op-first",
        text: ""
      }, {
        name: "reviewRecordNo",
        text: "No."
      }, {
        name: "taskRecordNo",
        text: "任务编号"
      }, {
        name: "subtotal",
        text: "审核失败次数"
      }, {
        name: "status",
        text: "状态"
      }, {
        name: "op",
        text: "操作"
      }];
      return true;
    }

    rejectReasonService.getReasons()
      .then(function(data) {
        $scope.reasons = data;
      });

    algorithmService.getAlgorithms()
      .then(function(data) {
        $scope.algorithms = data;
      });

    assetViewTagService.getAll()
      .then(function(tags) {
        $scope.viewTags = tags;
      });

    viewTagService.getAllViewTags()
      .then(function(viewTags) {
        $scope.taskViewTags = viewTags;
      });

    $scope.onExpandButtonClick = function() {
      $scope.expandSearch = !$scope.expandSearch;
    };

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍后...");
      reviewRecordService.queryReviewRecords(pageIndex, $scope.queryParams.getData())
        .then(function(result) {
          $scope.hideLoading();
          $scope.tasks = result.content;
          $scope.pageData = {
            totalPage: result.totalPages,
            pageIndex: result.number
          };
        });
    }

    $scope.onClearSearchButtonClick = function() {
      $scope.queryParams = new QueryParams();
    };

    $scope.onSearchButtonClick = function() {
      initData();
    };

    $scope.$on("onTabChanged", function(tabScope, item, index) {
      pageIndex = 0;
      tabIndex = index;
      initData();
    });

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.onStartReviewButtonClick = function(reviewRecord) {
      reviewRecordService.startReview(reviewRecord.reviewRecordNo)
        .then(function() {
          var pageName = markUtil.getMarkPageUrl(reviewRecord.taskRecord.assetType, reviewRecord.taskRecord.taskType, reviewRecord.taskRecord.taskRecordNo);;
          window.location.href = `../mark/${pageName}`;
          //
          //        var pageName = null;
          //        if(reviewRecord.taskRecord.assetType === "FOUR") {
          //          pageName = "video.four.html";
          //        } else if(reviewRecord.taskRecord.assetType === "SINGLE") {
          //          pageName = reviewRecord.taskRecord.taskType === 'PICTURE' ? "pic.html" : "video.html";
          //        } else if(reviewRecord.taskRecord.assetType === "PICTURE") {
          //          pageName = "pic.html";
          //        }
          //
          //        if(pageName) {
          //          window.location.href = "../mark/" + pageName + "?taskRecordNo=" + reviewRecord.taskRecord.taskRecordNo;
          //        }
        });
    };

    $scope.onViewButtonClick = function(reviewRecord) {
      var pageName = markUtil.getMarkPage(reviewRecord.taskRecord);
      if(pageName) {
        window.location.href = "../mark/" + pageName + "?status=1&taskRecordNo=" + reviewRecord.taskRecord.taskRecordNo;
      }
    };

    $scope.onNewTaskButtonClick = function(reviewRecord) {
      reviewRecordService.newTask(reviewRecord.reviewRecordNo)
        .then(function() {
          dialog.showInfo("任务发布成功！")
            .then(function() {
              initData();
            });
        });
    };

    $scope.onBatchReviewButtonClick = function() {
      $scope.enableBatch = true;
    };

    $scope.onSubmitBatchReviewPassButtonClick = function() {
      var reviewRecordNos = getBatchItems();

      if(reviewRecordNos.length === 0) {
        dialog.showError("请选择审核记录!");
        return;
      }

      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍后...");
      reviewRecordService.batchReviewPass(reviewRecordNos)
        .then(function(data) {
          $scope.enableBatch = false;
          $scope.hideLoading();
          dialog.showInfo("批量操作成功！")
            .then(function() {
              initData();
            });
        });
    };

    $scope.onSubmitBatchReviewFailedButtonClick = function() {
      var reviewRecordNos = getBatchItems();

      if(reviewRecordNos.length === 0) {
        dialog.showError("请选择审核记录!");
        return;
      }

      dialog.showCustom({
        templateUrl: 'reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecordNos: reviewRecordNos,
          batch: true
        },
        onConfirm: function() {
          $scope.enableBatch = false;
          dialog.showInfo("批量操作成功！")
            .then(function() {
              initData();
            });
        }
      });
    };

    function getBatchItems() {
      var reviewRecordNos = [];
      $scope.tasks.forEach(function(it) {
        if(it.isSelected) {
          reviewRecordNos.push(it.reviewRecordNo);
        }
      });

      return reviewRecordNos;
    }

    $scope.onCancelBatchReviewButtonClick = function() {
      $scope.enableBatch = false;
    };

    $scope.onSelectedAll = function() {
      setSelectedAll(true);
    };

    $scope.onUnSelectedAll = function() {
      setSelectedAll(false);
    };

    $scope.onReviewFailedButtonClick = function(reviewRecord) {
      dialog.showCustom({
        templateUrl: 'reviews.failed.dialog.html',
        controller: "reviewFailedDialogController",
        params: {
          reviewRecord: reviewRecord
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onReviewPassButtonClick = function(reviewRecord) {
      taskRecordService.reviewPass(reviewRecord.taskRecordNo)
        .then(function() {
          initData();
        });
    };

    function setSelectedAll(isSelected) {
      if($scope.tasks && $scope.tasks.length > 0) {
        $scope.tasks.forEach(function(it) {
          it.isSelected = isSelected;
        });
      }
    }

    //  $scope.onSearchButtonClick = function() {
    //    $scope.expandSearch = !$scope.expandSearch;
    //  };

  }
]);
'use strict';

zongmu.controller("taskDetailController", ["$q", "$scope", "taskService", "dialog", "breadCrumb", "$timeout",
  function($q, $scope, taskService, dialog, breadCrumbProvider, $timeout) {
    var taskNo = $.url().param("taskNo");
    var pageIndex = $.url().param("pageIndex");

    initView() && initData();

    function initView() {
      $scope.taskColumns = [{
        name: "taskItemNo",
        text: "No."
      }, {
        name: "taskType",
        text: "类型"
      }, {
        name: "status",
        text: "状态"
      }];

      if(!taskNo) {
        dialog.showError("参数错误");
        return false;
      }
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...")
      taskService.getTask(taskNo, pageIndex)
        .then(function(data) {
          $scope.hideLoading();
          $scope.task = data;
          $scope.pageData = {
            totalPage: data.taskItems.totalPages,
            pageIndex: data.taskItems.number
          };
          initTags();
          initNavPath();
          refresh();
        });
    }

    function refresh() {
      var items = ($scope.task.taskItems.content || []).filter(function(it) {
        return it.status === "CUTTING";
      });

      if(items.length > 0) {
        $timeout(function() {
          getTask();
        }, 2000);
      }
    }

    function getTask() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...")
      taskService.getTask(taskNo, pageIndex)
        .then(function(data) {
          $scope.hideLoading();
          $scope.task = data;
          $scope.pageData = {
            totalPage: data.taskItems.totalPages,
            pageIndex: data.taskItems.number
          };

          initTags();
          refresh();
        });
    }

    function initTags() {
      if($scope.task.assetViewTags) {
        $scope.task.assetViewTags.forEach(function(it) {
          it.viewTag.items.forEach(function(item) {
            if(item.id === it.assetViewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }

      if($scope.task.viewTags) {
        $scope.task.viewTags.forEach(function(it) {
          it.viewTag.items.forEach(function(item) {
            if(item.id === it.viewTagItemId) {
              it.viewTagItem = item;
            }
          })
        });
      }
    }

    function initNavPath() {
      breadCrumbProvider.setHistories([{
        text: "上传记录",
        href: "assets.html"
      }, {
        text: "记录详情",
        href: "asset.detail.html?assetNo=" + $scope.task.assetNo
      }, {
        text: "任务详情",
        href: "#"
      }]);
    }

    $scope.$on("tableIndexChanged", function(paginationScope, pageIndex) {
      window.location.href = "task.detail.html?taskNo=" + taskNo + "&pageIndex=" + pageIndex;
    });

    $scope.onSetTopButtonClick = function(top) {
      taskService.setTop(taskNo, top).then(function() {
        initData();
      });
    }

    $scope.onSetShowButtonClick = function(show) {
      taskService.setShow(taskNo, show).then(function() {
        initData();
      });
    }

    $scope.onSetPriorityButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'task.priority.dialog.html',
        controller: "chooseTaskPriorityController",
        params: {
          task: $scope.task
        },
        onConfirm: function() {
          initData();
        }
      });
    };
  }
]);
'use strict';

zongmu.controller("chooseTaskPriorityController", ['$scope', 'dialog', 'taskService', 'enumService',
  function($scope, dialog, taskService, enumService) {
    var params = $scope.ngDialogData;
    $scope.task = params.task;
    $scope.taskPriority = $scope.task.priority;
    $scope.taskPriorities = enumService.getTaskPriorities();
    $scope.onOkClick = function() {
      taskService.setPriority($scope.task.taskNo, $scope.taskPriority)
        .then(function() {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };

  }
]);
'use strict';

zongmu.controller("algorithmCreateController", ["$scope", "algorithmService", "dialog", "passwordUtils",
  function($scope, algorithmService, dialog, passwordUtils) {
    var params = $scope.ngDialogData;
    $scope.isNew = true;

    if(params && params.algorithm) {
      $scope.name = params.algorithm.name;
      $scope.isNew = false;
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.name) {
        dialog.showError("请填写算法名称");
        return;
      }

      if(!passwordUtils.checkTag($scope.name)) {
        dialog.showError("名称只能有中划线下划线英文字母中文字母或者数字组成!");
        return;
      }

      if(!$scope.isNew) {
        algorithmService.updateAlgorithm(params.algorithm.id, {
          name: $scope.name
        }).then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
      } else {
        algorithmService.createAlgorithm({
          name: $scope.name
        }).then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
      }

    };
  }
]);
'use strict';

zongmu.controller("algorithmDetailController", ['$q', '$scope', 'algorithmService', 'dialog',
  'breadCrumb', 'colorGroupService', 'colorTagService', 'tagService', "viewTagService",
  function($q, $scope, algorithmService, dialog, breadCrumbProvider, colorGroupService,
    colorTagService, tagService, viewTagService) {
    var algorithmId = $.url().param("algorithmId");
    initView() && initData();

    function initView() {
      $scope.setTitle("算法详细信息");
      if(!algorithmId) {
        dialog.showError("参数错误！");
        return;
      }

      breadCrumbProvider.setHistories([{
        text: "算法设置",
        href: "algorithm.html"
      }, {
        text: "算法详细信息",
        href: "#"
      }]);

      $scope.colorColumns = [{
        name: "name",
        text: "颜色名称"
      }, {
        name: "color",
        text: "颜色"
      }, {
        name: "op",
        text: "操作"
      }];

      $scope.tagColumns = [{
        name: "name",
        text: "属性名称"
      }, {
        name: "type",
        text: "类型"
      }, {
        name: "op",
        text: "操作"
      }];

      $scope.viewTagColumns = [{
        name: "name",
        text: "属性名称"
      }, {
        name: "op",
        text: "操作"
      }];

      return true;
    }

    function initData() {
      algorithmService.getAlgorithm(algorithmId)
        .then(function(algorithm) {
          $scope.algorithm = algorithm;
        });
    }

    $scope.onSetTagsButtonClick = function() {
      $scope.isEdit = true;
    };

    $scope.onSaveButtonClick = function() {
      var tagIds = [];
      $scope.tags.forEach(function(it) {
        if(it.isSelected) {
          tagIds.push(it.id);
        }
      });

      algorithmService.setTags(algorithmId, {
        hasColor: $scope.hasColor,
        tagIds: tagIds.join(";")
      }).then(function() {
        dialog.showInfo("关联成功").then(function() {
          initData();
        })
      });
    };

    $scope.onCancelButtonClick = function() {
      $scope.isEdit = false;
    };

    $scope.onCreateTagsButtonClick = function() {
      dialog.showCustom({
        templateUrl: "tag.create.html",
        controller: "tagCreateController",
        params: {
          algorithmId: algorithmId
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onCreateViewTagsButtonClick = function() {
      dialog.showCustom({
        templateUrl: "view.tag.create.dialog.html",
        controller: "viewTagCreateController",
        params: {
          algorithmId: algorithmId
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onViewViewTagButtonClicked = function(viewTag) {
      window.location.href = `viewTag.detail.html?viewTagId=${viewTag.id}&algorithmId=${algorithmId}`;
    };

    $scope.onDeleteViewTagButtonClicked = function(viewTag) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条场景属性吗？",
        onConfirm: function() {
          viewTagService.delete(viewTag.id)
            .then(function() {
              initData();
            });
        }
      });
    };

    $scope.onEditViewTagButtonClicked = function(viewTag) {
      dialog.showCustom({
        templateUrl: "view.tag.update.dialog.html",
        controller: "viewTagUpdateController",
        params: {
          viewTag: viewTag
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onEditTagButtonClicked = function(tag) {
      dialog.showCustom({
        templateUrl: "tag.create.html",
        controller: "tagCreateController",
        params: {
          tag: tag
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteTagButtonClicked = function(tag) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          tagService.deleteTag(tag.id)
            .then(function() {
              initData();
            });
        }
      });
    };

    $scope.onViewTagButtonClicked = function(tag) {
      window.location.href = "tag.detail.html?tagId=" + tag.id + "&algorithmId=" + $scope.algorithm.id;
    };

    $scope.onCreateColorButtonClick = function() {
      dialog.showCustom({
        templateUrl: "color.group.dialog.html",
        controller: "colorGroupDialogController",
        params: {
          algorithm: $scope.algorithm
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onCreateColorTagButtonClick = function() {
      dialog.showCustom({
        templateUrl: "color.create.html",
        controller: "colorCreateController",
        params: {
          group: $scope.algorithm.colorGroup
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onUpdateButtonClick = function(color) {
      dialog.showCustom({
        templateUrl: "color.create.html",
        controller: "colorCreateController",
        params: {
          color: color,
          group: $scope.algorithm.colorGroup
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(color) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          deleteColor(color.id);
        }
      })
    };

    function deleteColor(colorId) {
      colorTagService.deleteColorTag(colorId)
        .then(function() {
          initData();
          /* dialog.showInfo("删除成功!").then(function() {
             initData();
           });*/
        });
    };
  }
]);
'use strict';

zongmu.controller("algorithmSettingController", ["$scope", "algorithmService", "dialog", "breadCrumb",
  function($scope, algorithmService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.onUpdateButtonClick = function(algorithm) {
      dialog.showCustom({
        templateUrl: "algorithm.create.html",
        controller: "algorithmCreateController",
        params: {
          algorithm: algorithm
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onCreateButtonClick = function() {
      dialog.showCustom({
        templateUrl: "algorithm.create.html",
        controller: "algorithmCreateController",
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onViewButtonClick = function(algorithm) {
      window.location.href = "algorithm.detail.html?algorithmId=" + algorithm.id;
    };

    $scope.onDeleteButtonClick = function(algorithm) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          deleteAlgorithm(algorithm.id);
        }
      })
    };

    function deleteAlgorithm(algorithmId) {
      algorithmService.deleteAlgorithm(algorithmId)
        .then(function() {
          initData();
          //        dialog.showInfo("删除成功!").then(function() {
          //          initData();
          //        });
        });
    }

    function initView() {
      $scope.title = "算法设置";
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "算法设置",
        href: "#"
      }]);
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      algorithmService.getAlgorithms()
        .then(function(data) {
          $scope.hideLoading();
          $scope.dataset = data;
        });
    }

  }
]);
'use strict';

zongmu.controller("assetTagCreateController", ["$scope", "assetService", "enumService", "dialog",
  function($scope, assetService, enumService, dialog) {

    init();

    function init() {
      $scope.category = "ROAD";
      $scope.categories = enumService.getTagCategories();
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

      var data = {
        category: $scope.category,
        value: $scope.value
      };
      assetService.batchCreateAssetTags(data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);
'use strict';

zongmu.controller("assetTagController", ["$scope", "assetService", "dialog", "breadCrumb",
  function($scope, assetService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.createTag = function() {
      dialog.showCustom({
        templateUrl: "asset.tag.create.html",
        controller: "assetTagCreateController",
        onConfirm: function() {
          refresh();
        }
      });
    };

    $scope.onDeleteButtonClick = function(tagItem) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          assetService.deleteAssetTag(tagItem.id)
            .then(function() {
              refresh();
              //            dialog.showInfo("删除成功!").then(function() {
              //              refresh();
              //            });
            });
        }
      });

    };

    $scope.onSetDefaultTagButtonClicked = function(tagItem) {
      assetService.setDefaultAssetTag(tagItem.id)
        .then(function() {
          refresh();
        });
    };

    function initView() {
      $scope.title = "视频属性设置";
      breadCrumbProvider.setHistories([{
        text: "视频属性设置",
        href: "#"
      }]);
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "default",
        text: "默认值"
      }, {
        name: "op",
        text: "操作"
      }];
    }

    function initData() {
      refresh();
    }

    function refresh() {
      $scope.roadTags = [];
      $scope.weatherTags = [];
      assetService.getAssetTags()
        .then(function(data) {
          data.forEach(function(it) {
            if(it.category === "ROAD") {
              $scope.roadTags.push(it);
            } else if(it.category === "WEATHER") {
              $scope.weatherTags.push(it);
            }
          });
        });
    }
  }
]);
'use strict';

zongmu.controller("assetViewTagCreateController", ["$scope", "assetViewTagService", "dialog","passwordUtils",
  function($scope, assetViewTagService, dialog,passwordUtils) {

    var params = $scope.ngDialogData || {};
    if(params.tag) {
      $scope.value = params.tag.name;
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }
      
      if(!passwordUtils.checkTag($scope.value)){
        dialog.showError("属性只能有中划线下划线英文字母中文字母或者数字组成!");
        return;
      }

      var data = {
        name: $scope.value
      };

      if(params.tag) {
        assetViewTagService.update(params.tag.id, data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        assetViewTagService.create(data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }

    };
  }
]);
'use strict';

zongmu.controller("assetViewTagDetailController", ["$scope", "assetViewTagService", "dialog", "breadCrumb",
  function($scope, assetViewTagService, dialog, breadCrumbProvider) {
    var tagId = $.url().param("tagId");
    initView();
    initData();

    $scope.createTag = function() {
      dialog.showCustom({
        templateUrl: "asset.view.tag.item.create.html",
        controller: "assetViewTagItemCreateController",
        params: {
          assetViewTagId: +tagId
        },
        onConfirm: function() {
          refresh();
        }
      });
    };

    $scope.onDeleteButtonClick = function(item) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          assetViewTagService.deleteTagItem(item.id)
            .then(function() {
              refresh();
            });
        }
      });

    };

    $scope.onSetDefaultTagButtonClicked = function(tagItem) {
      assetViewTagService.setItemDefault(tagItem.id)
        .then(function() {
          refresh();
        });
    };

    function initView() {
      $scope.title = "视频属性设置";
      breadCrumbProvider.setHistories([{
        text: "视频属性设置",
        href: "asset.view.tag.html"
      }, {
        text: "详细设置",
        href: "#"
      }]);
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "isDefault",
        text: "默认值"
      }, {
        name: "op",
        text: "操作"
      }];
    }

    function initData() {
      refresh();
    }

    function refresh() {
      assetViewTagService.getViewTag(tagId)
        .then(function(data) {
          $scope.tag = data;
        });
    }
  }
]);
'use strict';

zongmu.controller("assetViewTagItemCreateController", ["$scope", "assetViewTagService", "dialog",
  function($scope, assetViewTagService, dialog) {
    var params = $scope.ngDialogData;

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

//    if(!passwordUtils.batchCheckTag($scope.value)) {
//      dialog.showError("属性只能有中划线下划线英文字母中文字母或者数字组成!");
//      return;
//    }

      var data = {
        assetViewTagId: params.assetViewTagId,
        items: $scope.value.split(";").filter(function(it) {
          return it;
        })
      };
      assetViewTagService.batchCreateItems(data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);
'use strict';

zongmu.controller("assetViewTagController", ["$scope", "assetViewTagService", "dialog", "breadCrumb",
  function($scope, assetViewTagService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.createTag = function() {
      dialog.showCustom({
        templateUrl: "asset.view.tag.create.html",
        controller: "assetViewTagCreateController",
        onConfirm: function() {
          refresh();
        }
      });
    };

    $scope.onDeleteButtonClick = function(item) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          assetViewTagService.deleteAssetViewTag(item.id)
            .then(function() {
              refresh();
            });
        }
      });

    };

    $scope.onViewTagButtonClicked = function(item) {
      window.location.href=`asset.view.tag.detail.html?tagId=${item.id}`;
    };
    
    $scope.onUpdateViewTagButtonClicked = function(item){
      dialog.showCustom({
        templateUrl: "asset.view.tag.create.html",
        controller: "assetViewTagCreateController",
        params: {
          tag: item
        },
        onConfirm: function() {
          refresh();
        }
      });
    };

    function initView() {
      $scope.title = "视频属性设置";
      breadCrumbProvider.setHistories([{
        text: "视频属性设置",
        href: "#"
      }]);
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "op",
        text: "操作"
      }];
    }

    function initData() {
      refresh();
    }

    function refresh() {
      assetViewTagService.getAll()
        .then(function(data) {
          $scope.items = data;
        });
    }
  }
]);
'use strict';

zongmu.controller("colorCreateController", ["$scope", "colorTagService", "dialog", function($scope, colorTagService, dialog) {
  var params = $scope.ngDialogData;

  initView();

  function initView() {
    if(params && params.color) {
      $scope.name = params.color.name;
      $scope.color = params.color.color;
    }
  }

  $scope.onSaveButtonClick = function() {
    if(!$scope.name) {
      dialog.showError("请填写属性名称");
      return;
    }

    if(!$scope.color) {
      dialog.showError("请选择颜色");
      return;
    }

    var colorTag = {
      name: $scope.name,
      color: $scope.color,
      colorGroupId: params.group.id
    };

    if(params && params.color) {
      colorTagService.updateColorTag(params.color.id, colorTag)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    } else {
      colorTagService.createColorTag(colorTag)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    }

  };
}]);
'use strict';

zongmu.controller("colorGroupDialogController", ["$scope", "colorGroupService", "dialog",
  function($scope, colorGroupService, dialog) {
    var params = $scope.ngDialogData;

    initView();

    function initView() {
      if(params.algorithm.colorGroup) {
        $scope.name = params.algorithm.colorGroup.name;
      }
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.name) {
        dialog.showError("名称不能为空！");
        return;
      }

      var group = {
        name: $scope.name,
        algorithmId: params.algorithm.id
      };

      if(params.algorithm.colorGroup) {
        colorGroupService.update(params.algorithm.id, group)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        colorGroupService.create(group)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }
    };
  }
]);
'use strict';

zongmu.controller("colorSettingController", ["$scope", "colorTagService", "dialog", "breadCrumb",
  function($scope, colorTagService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.onUpdateButtonClick = function(color) {
      dialog.showCustom({
        templateUrl: "color.create.html",
        controller: "colorCreateController",
        params: {
          color: color
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onCreateButtonClick = function() {
      dialog.showCustom({
        templateUrl: "color.create.html",
        controller: "colorCreateController",
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(color) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认要删除此条记录么？",
        onConfirm: function() {
          deleteColor(color.id);
        }
      })
    };

    function deleteColor(colorId) {
      colorTagService.deleteColorTag(colorId)
        .then(function() {
          dialog.showInfo("删除成功!").then(function() {
            initData();
          });
        });
    }

    function initView() {
      $scope.title = "颜色属性设置";
      $scope.columns = [{
        name: "name",
        text: "名称"
      }, {
        name: "color",
        text: "颜色"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "颜色属性设置",
        href: "#"
      }]);
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      colorTagService.getColorTags()
        .then(function(data) {
          $scope.hideLoading();
          $scope.colorTags = data;
        });
    }

  }
]);
'use strict';

zongmu.controller("reasonCreateController", ["$scope", "rejectReasonService", "dialog", function($scope, rejectReasonService, dialog) {
  var params = $scope.ngDialogData;

  $scope.onSaveButtonClick = function() {
    if (!$scope.value) {
      dialog.showError("请填写原因描述");
      return;
    }

    rejectReasonService.createReason($scope.value)
      .then(function(res) {
        $scope.closeThisDialog({
          key: 'ok'
        });
      });
  };
}]);
'use strict';

zongmu.controller("reasonSettingController", ["$scope", "rejectReasonService", "dialog", "breadCrumb",
  function($scope, rejectReasonService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.onCreateButtonClick = function() {
      dialog.showCustom({
        templateUrl: "reason.create.html",
        controller: "reasonCreateController",
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(reason) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          deleteReason(reason.id);
        }
      })
    };

    function deleteReason(reasonId) {
      rejectReasonService.deleteReason(reasonId)
        .then(function() {
          initData();
          //        dialog.showInfo("删除成功!").then(function() {
          //          initData();
          //        });
        });
    }

    function initView() {
      $scope.title = "审批拒绝原因设置";
      $scope.columns = [{
        name: "description",
        text: "描述"
      }, {
        name: "default",
        text: "默认值"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "原因设置",
        href: "#"
      }]);
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载数据，请稍后...");
      rejectReasonService.getReasons()
        .then(function(data) {
          $scope.hideLoading();
          $scope.reasons = data;
        });
    }

    $scope.onSetDefaultButtonClick = function(reason) {
      $scope.showLoading();
      $scope.setLoadingText("正在保存数据，请稍后...");
      rejectReasonService.setDefault(reason.id)
        .then(function(data) {
          initData();
        });
    };
  }
]);
'use strict';

zongmu.controller("settingController", ["$scope", function($scope) {
  $scope.setTitle("设置");
  
  $scope.sidebar = [{
    name: "setting-general",
    text: "基本设置",
    icon: "tasks",
    items: [{
      name: "asset-tag-settings",
      text: "视频属性设置",
      href: "asset.view.tag.html"
    }, {
      name: "reject-reason-settings",
      text: "原因设置",
      href: "reason.html"
    }, {
      name: "algorithm-settings",
      text: "算法设置",
      href: "algorithm.html"
    }, {
      name: "train-settings",
      text: "训练设置",
      href: "train.html"
    }]
  }, {
    name: "user-profile",
    text: "权限设置",
    icon: "user",
    items: [{
      name: "user-list",
      text: "用户列表",
      href: "users.html"
    }, {
      name: "black-user-list",
      text: "黑名单",
      href: "user.black.list.html"
    }]
  }];

  initData();

  function initData() {
    var role = Cookies.get("role");
    if(role === "REVIEW") {
      hideGroups(["user-profile"]);
    }
  }

  $scope.hideSideItems = function(names) {
    hideGroupItems(names);
  }

  $scope.hideSideGroups = function(groups) {
    hideGroups(groups);
  };

  function hideGroups(groups) {
    $scope.sidebar.forEach(function(group) {
      if(groups.indexOf(group.name) !== -1) {
        group.visibility = false;
      } else {
        delete group.visibility;
      }
    });
  }

  function hideGroupItems(names) {
    $scope.sidebar.forEach(function(group) {
      group.items.forEach(function(it) {
        if(names.indexOf(it.name) !== -1) {
          it.visibility = false;
        } else {
          delete it.visibility;
        }
      });
    });
  }
}]);
'use strict';

zongmu.controller("tagCreateController", ["$scope", "tagService", "enumService",
  function($scope, tagService, enumService) {

    var params = $scope.ngDialogData;

    initView();

    function initView() {
      $scope.controlTypes = enumService.getTagControlTypes();
      if(params && params.tag) {
        $scope.isEdit = true;
        $scope.type = params.tag.type;
        $scope.name = params.tag.name;
        $scope.algorithmId = params.tag.algorithmId;
      } else {
        $scope.type = "DROPDOWNBOX";
        $scope.algorithmId = params.algorithmId;
      }
    }

    $scope.onSaveButtonClick = function() {
      var data = {
        name: $scope.name,
        type: $scope.type,
        algorithmId: $scope.algorithmId
      };
      if(params && params.tag) {
        tagService.updateTag(params.tag.id, data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        tagService.createTag(data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }

    };
  }
]);
'use strict';

zongmu.controller("tagDefaultValueController", ["$scope", "tagService", "dialog",
  function($scope, tagService, dialog) {
    var params = $scope.ngDialogData;
    initView();

    function initView() {
      $scope.tag = angular.copy(params.tag);
      $scope.selectTagItem = null;
      if ($scope.tag.type === 'DROPDOWNBOX' || $scope.tag.type === 'RADIOBUTTON') {
        var results = $scope.tag.items.filter(function(it) {
          return it.default;
        });

        if (results.length > 0) {
          $scope.selectTagItem = results[0];
        }
      }
    }

    $scope.onSaveButtonClick = function() {
      var tagItems = [];
      if ($scope.tag.type === 'DROPDOWNBOX' || $scope.tag.type === 'RADIOBUTTON') {
        if (!$scope.selectTagItem) {
          dialog.showError("请选择一个默认值。");
          return;
        } else {
          tagItems.push($scope.selectTagItem);
        }
      } else if ($scope.tag.type === 'CHECKBOX') {
        angular.forEach($scope.tag.items, function(item, index) {
          if (item.default) {
            tagItems.push(item);
          }
        });
      }

      if (tagItems.length > 0) {
        tagService.setMultiDefaultValues($scope.tag.id, tagItems)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }
    };
  }
]);
'use strict';

zongmu.controller("tagDetailController", ['$q', '$scope', 'tagService', 'dialog', 'breadCrumb',
  function($q, $scope, tagService, dialog, breadCrumbProvider) {
    var tagId = $.url().param("tagId");
    var algorithmId = $.url().param("algorithmId");

    initView() && initData();
    var tagsMap = {};

    function initView() {
      $scope.setTitle("属性详细信息");
      if(!tagId) {
        dialog.showError("参数错误！");
        return;
      }

      breadCrumbProvider.setHistories([{
        text: "算法设置",
        href: "algorithm.html"
      }, {
        text: "算法详细信息",
        href: "algorithm.detail.html?algorithmId=" + algorithmId
      }, {
        text: "属性详细信息",
        href: "#"
      }]);

      $scope.columns = [{
        name: "value",
        text: "值"
      }, {
        name: "default",
        text: "默认值"
      }, {
        name: "op",
        text: "操作"
      }];

      return true;
    }

    function initData() {
      tagService.getTag(tagId)
        .then(function(tag) {
          $scope.tag = tag;
          $scope.defaultValues = [];
          tag.items.forEach(function(it) {
            if(it.default) {
              $scope.defaultValues.push(it);
            }
          });
        });
    }

    $scope.onCreateItemsButtonClicked = function() {
      dialog.showCustom({
        templateUrl: "tag.value.create.html",
        controller: "tagValueCreateController",
        params: {
          tag: $scope.tag
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onSetDefaultValueClicked = function() {
      dialog.showCustom({
        templateUrl: "tag.defaultvalue.html",
        controller: "tagDefaultValueController",
        params: {
          tag: $scope.tag
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onSetDefaultButtonClick = function(tagItem) {
      $scope.showLoading();
      $scope.setLoadingText("正在保存数据，请稍后...");
      tagService.setTagDefaultValue(tagItem.tagId, tagItem)
        .then(function(res) {
          initData();
        });
    };

    $scope.onDeleteTagItemButtonClicked = function(tagItem) {
      dialog.showConfirm({
        title: "提示",
        message: "确定要删除么？",
        onConfirm: function() {
          tagService.deleteTagItem(tagItem.id)
            .then(function() {
              initData();
            });
        }
      });
    };
  }
]);
'use strict';

zongmu.controller("tagController", ["$scope", "tagService", "dialog", "breadCrumb",
  function($scope, tagService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.createTag = function() {
      dialog.showCustom({
        templateUrl: "tag.create.html",
        controller: "tagCreateController",
        onConfirm: function() {
          initData();
        }
      });
    };

    function initView() {
      $scope.title = "属性设置";
      breadCrumbProvider.setHistories([{
        text: "标注属性设置",
        href: "#"
      }]);
      $scope.columns = [{
        name: "name",
        text: "属性名称"
      }, {
        name: "type",
        text: "类型"
      }, {
        name: "op",
        text: "操作"
      }];
    }

    function initData() {
      tagService.getTags()
        .then(function(data) {
          $scope.dataSource = data;
        });
    }

    $scope.onViewTagButtonClicked = function(tag) {
      window.location.href = "tag.detail.html?tagId=" + tag.id;
    };

    $scope.onEditTagButtonClicked = function(tag) {
      dialog.showCustom({
        templateUrl: "tag.create.html",
        controller: "tagCreateController",
        params: {
          tag: tag
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteTagButtonClicked = function(tag) {
      dialog.showConfirm({
        title: "提示",
        message: "确定要删除么？",
        onConfirm: function() {
          tagService.deleteTag(tag.id)
            .then(function() {
              dialog.showInfo("删除成功。").then(function() {
                initData();
              });
            });
        }
      });
    }
  }
]);
'use strict';

zongmu.controller("tagValueCreateController", ["$scope", "tagService", "dialog", function($scope, tagService, dialog) {
  var params = $scope.ngDialogData;

  $scope.onSaveButtonClick = function() {
    if(!$scope.value){
      dialog.showError("请填写属性值");
      return;
    }
    
    var tagItems = $scope.value.split(";");
    if (params && params.tag && params.tag.id) {
      tagService.batchAddTagItem(params.tag.id, tagItems)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    } else {
      dialog.showError("参数错误");
    }
  };
}]);
'use strict';

zongmu.controller("trainCreateController", ["$scope", "trainService", "dialog", "breadCrumb",
  function($scope, trainService, dialog, breadCrumbProvider) {
    var trainId = $.url().param("trainId");
    initView() && initData();

    function initView() {
      var text = trainId ? "修改训练设置文档" : "新建训练设置文档";
      $scope.title = text;
      breadCrumbProvider.setHistories([{
        text: "训练设置",
        href: "train.html"
      }, {
        text: text,
        href: "#"
      }]);
      $scope.train = {};
      return true;
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.train.subject) {
        dialog.showError("文档名称不能为空。");
        return;
      }

      if(!$scope.train.body) {
        dialog.showError("文档内容不能为空。");
        return;
      }

      $scope.setLoadingText("正在保存，请稍后...");
      $scope.showLoading();
      if(trainId) {
        trainService.updateTrain(trainId, $scope.train)
          .then(function() {
            $scope.hideLoading();
            window.location.href = "train.html";
          });
      } else {
        trainService.createTrain($scope.train)
          .then(function() {
            $scope.hideLoading();
            window.location.href = "train.html";
          });
      }
    }

    $scope.onCancelButtonClick = function() {
      history.back();
    };

    function initData() {
      if(trainId) {
        $scope.setLoadingText("正在加载，请稍后...");
        $scope.showLoading();
        trainService.getTrain(trainId)
          .then(function(train) {
            $scope.hideLoading();
            $scope.train = train;
          });
      }
    }
  }
]);
'use strict';

zongmu.controller("trainSettingController", ["$scope", "trainService", "dialog", "breadCrumb",
  function($scope, trainService, dialog, breadCrumbProvider) {
    initView();
    initData();

    $scope.createTrain = function() {
      window.location.href = "train.create.html";
    };

    $scope.onUpdateButtonClicked = function(train) {
      window.location.href = "train.create.html?trainId=" + train.id;
    };

    $scope.onDeleteButtonClicked = function(train) {
      dialog.showConfirm({
        title: "删除提示",
        message: "确认要删除此条记录么？",
        onConfirm: function() {
          deleteTrain(train.id);
        }
      });
    };

    function deleteTrain(trainId) {
      $scope.setLoadingText("正在删除，请稍后...");
      $scope.showLoading();
      trainService.deleteTrain(trainId)
        .then(function() {
          refresh();
        });
    }

    function initView() {
      $scope.title = "训练设置";
      breadCrumbProvider.setHistories([{
        text: "训练设置",
        href: "#"
      }]);
      $scope.columns = [{
        name: "subject",
        text: "标题"
      }, {
        name: "op",
        text: "操作"
      }];
    }

    function initData() {
      refresh();
    }

    function refresh() {
      $scope.setLoadingText("正在加载，请稍后...");
      $scope.showLoading();
      trainService.getTrains()
        .then(function(data) {
          $scope.hideLoading();
          $scope.dataSource = data;
        });
    }
  }
]);
'use strict';

zongmu.controller("blackUserListController", ["$scope", "userService", "dialog", "breadCrumb",
  function($scope, userService, dialog, breadCrumbProvider) {
    var pageIndex = 0;
    initView();
    initData();

    function initView() {
      $scope.title = "黑名单列表";
      $scope.columns = [{
        name: "email",
        text: "账户"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "黑名单列表",
        href: "#"
      }]);
    }

    function initData() {
      userService.getBlackUserList(pageIndex)
        .then(function(result) {
          $scope.users = result.content;
          $scope.pageData = {
            totalPage: result.totalPages,
            pageIndex: result.number
          };
        });
    }

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.onViewButtonClick = function(user) {
      window.location.href = "users.detail.html?userId=" + user.id;
    };

    $scope.onRemoveBlackButtonClick = function(user) {
      userService.removeBlackList(user.id)
        .then(function() {
          dialog.showInfo("移除黑名单成功！")
            .then(function() {
              initData();
            });
        });
    };

  }
]);
'use strict';

zongmu.controller("userDetailController", ["$scope", "userService", "dialog", "breadCrumb",
  function($scope, userService, dialog, breadCrumbProvider) {
    var userId = $.url().param("userId");
    initView();
    initData();

    function initView() {
      $scope.title = "用户详细信息";
      breadCrumbProvider.setHistories([{
        text: "用户列表",
        href: "users.html"
      }, {
        text: "详细信息",
        href: "#"
      }]);
    }

    function initData() {
      userService.getUser(userId)
        .then(function(data) {
          $scope.user = data;
        });
    }

    $scope.onRoleButtonClick = function() {
      dialog.showCustom({
        templateUrl: 'users.role.html',
        controller: "userRoleDialogController",
        params: {
          user: $scope.user
        },
        onConfirm: function() {
          initData();
        }
      });
    }

  }
]);
'use strict';

zongmu.controller("userListController", ["$scope", "userService", "dialog", "breadCrumb",
  function($scope, userService, dialog, breadCrumbProvider) {
    var pageIndex = 0;
    var tabIndex = 0;
    initView();
    initData();

    function initView() {
      $scope.title = "用户列表";
      $scope.columns = [{
        name: "email",
        text: "账户"
      }, {
        name: "businessRole",
        text: "角色"
      }, {
        name: "op",
        text: "操作"
      }];

      breadCrumbProvider.setHistories([{
        text: "用户列表",
        href: "#"
      }]);

      $scope.tabs = [{
        name: "all",
        text: "全部",
        active: true
      }, {
        name: "ADMIN",
        text: "管理员"
      }, {
        name: "FINANCE",
        text: "财务人员"
      }, {
        name: "REVIEW",
        text: "审核人员"
      }, {
        name: "UPLOAD",
        text: "路测人员"
      }, {
        name: "NORMAL",
        text: "普通用户"
      }, {
        name: "SUPER",
        text: "高级用户"
      }];
    }

    function initData() {
      userService.getUserList(pageIndex, tabIndex)
        .then(function(result) {
          $scope.users = result.content;
          $scope.pageData = {
            totalPage: result.totalPages,
            pageIndex: result.number
          };
        });
    }

    $scope.canShow = function(user) {
      if(user.id === 1){
        return false;
      }
      
      if(Cookies.get("role") === 'ADMIN' && Cookies.get("username") === user.email){
        return false;
      }
      
      return true;
    };

    $scope.$on("onTabChanged", function(tabScope, item, index) {
      pageIndex = 0;
      tabIndex = index;
      initData();
    });

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.onViewButtonClick = function(user) {
      window.location.href = "users.detail.html?userId=" + user.id;
    };

    $scope.onPermissionButtonClick = function(user) {
      dialog.showCustom({
        templateUrl: 'users.role.html',
        controller: "userRoleDialogController",
        params: {
          user: user
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onAddBlackButtonClick = function(user) {
      userService.addBlackList(user.id)
        .then(function() {
          dialog.showInfo("拉入黑名单成功！")
            .then(function() {
              initData();
            });
        });
    };

  }
]);
'use strict';

zongmu.controller("userRoleDialogController", ["$scope", "userService", "dialog", "enumService",
  function($scope, userService, dialog, enumService) {
    var params = $scope.ngDialogData;

    initView();

    function initView() {
      $scope.roles = enumService.getBusinessRoles();
      $scope.role = params.user.businessRole;
    }

    $scope.onSaveButtonClick = function() {
      userService.setUserRole(params.user.id, $scope.role)
        .then(function() {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);
'use strict';

zongmu.controller("viewTagCreateController", ["$scope", "viewTagService", "dialog",
  function($scope, viewTagService, dialog) {
    var params = $scope.ngDialogData;
    init();

    function init() {}

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

      var data = {
        algorithmId: +params.algorithmId,
        name: $scope.value
      };
      viewTagService.create(data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);
'use strict';

zongmu.controller("viewTagUpdateController", ["$scope", "viewTagService", "dialog",
  function($scope, viewTagService, dialog) {
    var params = $scope.ngDialogData;
    init();

    function init() {
      $scope.value = params.viewTag.name;
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

      var data = {
        name: $scope.value
      };
      viewTagService.update(params.viewTag.id, data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);
'use strict';

zongmu.controller("viewTagDetailController", ["$scope", "viewTagService", "dialog", "breadCrumb",
  function($scope, viewTagService, dialog, breadCrumbProvider) {
    var viewTagId = $.url().param("viewTagId");
    var algorithmId = $.url().param("algorithmId");
    initView() && initData();

    function initView() {
      $scope.title = "用户详细信息";
      breadCrumbProvider.setHistories([{
        text: "算法设置",
        href: "algorithm.html"
      }, {
        text: "算法详细信息",
        href: "algorithm.detail.html?algorithmId=" + algorithmId
      }, {
        text: "场景属性详细信息",
        href: "#"
      }]);

      $scope.columns = [{
        name: "name",
        text: "值"
      }, {
        name: "op",
        text: "操作"
      }];

      return true;
    }

    function initData() {
      viewTagService.getViewTag(viewTagId)
        .then(function(data) {
          $scope.viewTag = data;
        });
    }

    $scope.onCreateViewTagsButtonClick = function() {
      dialog.showCustom({
        templateUrl: "viewTag.item.create.html",
        controller: "NewViewTagItemCreateController",
        params: {
          viewTagId: +viewTagId
        },
        onConfirm: function() {
          initData();
        }
      });
    };

    $scope.onDeleteButtonClick = function(item) {
      dialog.showConfirm({
        title: "提示",
        message: "确认删除此条属性吗？",
        onConfirm: function() {
          viewTagService.deleteTagItem(item.id)
            .then(function() {
              initData();
            });
        }
      });
    };

    $scope.onUpdateButtonClick = function() {

    };
  }
]);
'use strict';

zongmu.controller("NewViewTagItemCreateController", ["$scope", "viewTagService", "dialog",
  function($scope, viewTagService, dialog) {
    var params = $scope.ngDialogData;

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

      var data = {
        names: $scope.value.split(";").filter(function(it) {
          return it;
        })
      };
      viewTagService.batchCreateItems(params.viewTagId, data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);
'use strict';

zongmu.controller("trainController", ["$scope", "trainService", function($scope, trainService) {
  var trainId = $.url().param("trainId");
  initView();
  initData();

  function initView() {
    $scope.setTitle("任务训练");
    $scope.sidebar = [{
      name: "train-general",
      text: "训练教程",
      icon: "tasks",
      items: []
    }];
  }

  function initData() {
    $scope.showLoading();
    trainService.getTrains()
      .then(function(data) {
        if(data.length === 0) {
          $scope.hideLoading();
        } else {
          data.forEach(function(it, index) {
            $scope.sidebar[0].items.push({
              name: it.id + "",
              text: it.subject,
              href: "index.html?trainId=" + it.id
            });

            if(!trainId && index === 0) {
              trainId = it.id;
            }

            $scope.currentItem = trainId;

            if(+trainId === it.id) {
              getTrain(it.id);
            }
          })
        }
      });
  }

  function getTrain(trainId) {
    trainService.getTrain(trainId)
      .then(function(train) {
        $scope.hideLoading();
        $scope.content = train.body;
      });
  }

}]);
'use strict';

zongmu.controller("activeUserController", ["$scope", "userService", "dialog", "$timeout",
  function($scope, userService, dialog, $timeout) {
    var email = $.url().param("mail");
    var code = $.url().param("code");
    initView() && initData();

    function initView() {
      $scope.setTitle("用户激活");
      $scope.counter = 5;
      return true;
    }

    function initData() {
      if(email) {
        email = email.replace("____", "@");
      }
      userService.active({
          code: code,
          email: email
        })
        .then(function(res) {
          $scope.activeResult = res;
          if(res.result) {
            startCounter();
          }
        });
    }

    $scope.onSubmit = function() {
      userService.reactive({
          email: email
        })
        .then(function(res) {
          window.location.href = "register.info.html?mail=" + $.url().param("mail");
        });
    };

    function startCounter() {
      $timeout(function() {
        $scope.counter = $scope.counter - 1;
        if($scope.counter > 0) {
          startCounter();
          $scope.$apply();
        } else {
          window.location.href = "login.html";
        }
      }, 1000);
    }

  }
]);
'use strict';

zongmu.controller("changeMoneyCreateController", ["$scope", "breadCrumb", "userPointService", "dialog", "payService",
  function($scope, breadCrumbProvider, userPointService, dialog, payService) {
    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "会员积分",
        href: "point.html"
      }, {
        text: "积分提现",
        href: "#"
      }]);

      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");
      userPointService.getAvailablePoint()
        .then(function(data) {
          $scope.availablePoint = data;
          $scope.hideLoading();
        });
    }

    $scope.onSubmitClicked = function() {
      if($scope.money === null || $scope.money === undefined || $scope.money <= 0) {
        dialog.showError("提现积分不能为空,并且不能为负数。");
        return;
      }

      if($scope.money > $scope.availablePoint) {
        dialog.showError("提现积分不能大于可用积分。");
        return;
      }

      if($scope.money % 100 !== 0) {
        dialog.showError("提现积分必须是100的整数倍。");
        return;
      }

      payService.requestPay({
        point: $scope.money
      }).then(function() {
        dialog.showInfo("提交提现请求成功")
          .then(function() {
            window.location.href = "point.html";
          });
      });
    };

    $scope.onCancelClicked = function() {
      window.location.href = "point.html";
    }

  }
]);
'use strict';

zongmu.controller("forgetPasswordInfoController", ["$scope", "userService", "dialog",
  function($scope, userService, dialog) {
    var email = $.url().param("mail");
    initView() && initData();

    function initView() {
      $scope.setTitle("重置密码");
      return true;
    }

    function initData() {
      if(email) {
        $scope.email = email.replace("____", "@");
      }
    }
  }
]);
'use strict';

zongmu.controller("forgetPasswordController", ["$scope", "userService", "dialog",
  function($scope, userService, dialog) {
    initView() && initData();

    function initView() {
      $scope.setTitle("忘记密码");
      return true;
    }

    function initData() {

    }

    $scope.onSubmit = function() {
      if(!$scope.email) {
        dialog.showError("邮箱地址不能为空。")
        return;
      }

      var param = {};
      param.email = $scope.email;
      param.password = $scope.password;
      param.repeatPassword = $scope.repeatPassword;

      $scope.showLoading();
      userService.forgetPassword(param)
        .then(function() {
          $scope.hideLoading();
          window.location.href = "forget.password.info.html?mail=" + $scope.email.replace("@", "____");
        });
    };

  }
]);
'use strict';

zongmu.controller("forgetResetPasswordController", ["$scope", "userService", "dialog", "passwordUtils",
  function($scope, userService, dialog, passwordUtils) {
    var email = $.url().param("mail");
    var code = $.url().param("code");
    initView() && initData();

    function initView() {
      $scope.setTitle("重置密码");
      return true;
    }

    function initData() {

    }

    $scope.onSubmit = function() {
      if(!$scope.password) {
        dialog.showError("密码不能为空。")
        return;
      }

      if(!$scope.repeatPassword) {
        dialog.showError("重复密码不能为空。")
        return;
      }

      if($scope.password !== $scope.repeatPassword) {
        dialog.showError("两次密码不一致。")
        return;
      }

      if(!passwordUtils.checkPassword($scope.repeatPassword)) {
        dialog.showError(passwordUtils.getMsg());
        return;
      }

      var param = {};
      param.email = (email || "").replace("____", "@");
      param.code = code;
      param.password = $scope.password;
      param.repeatPassword = $scope.repeatPassword;
      $scope.showLoading();
      userService.forgetResetPassword(param)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("重置密码成功！")
            .then(function() {
              window.location.href = "login.html";
            });

        });
    };

  }
]);
'use strict';

zongmu.controller("loginController", ["$scope", "userService", "dialog", "serviceUrl", "application",
  function($scope, userService, dialog, serviceUrl, applicationProvider) {
    initView() && initData();

    function initView() {
      $scope.setTitle("用户登录");
      return true;
    }

    function initData() {

    }

    $scope.onRegisterButtonClick = function() {
      window.location.href = "register.html";
    };

    $scope.onForgetPasswordButtonClick = function() {
      window.location.href = "forget.password.html";
    };

    $(document).on("keydown", function(event) {
      event.stopPropagation();
      if($(".ngdialog.huoyun-dialog").length === 0) {
        if(event.keyCode === 13) {
          $scope.onSubmit();
        }
      }
    });

    $scope.onSubmit = function() {
      if(!$scope.email) {
        dialog.showError("邮箱不能为空。")
        return;
      }

      if(!$scope.password) {
        dialog.showError("密码不能为空。")
        return;
      }

      var param = {};
      param.email = $scope.email;
      param.password = $scope.password;

      $scope.showLoading();
      userService.login(param)
        .then(function(user) {
          $scope.hideLoading();
          applicationProvider.setLogin(true);
          applicationProvider.setUserName(user.email, user.businessRole);
          window.location.href = serviceUrl + "page/home/index.html"
        });
    };

  }
]);
'use strict';

zongmu.controller("payDetailController", ["$scope", "breadCrumb", "payService", "dialog",
  function($scope, breadCrumbProvider, payService, dialog) {
    var payId = $.url().param("payId");
    initView() && initData();

    function initView() {
      $scope.setTitle("提现申请记录");
      breadCrumbProvider.setHistories([{
        text: "提现申请记录",
        href: "point.request.list.html"
      }, {
        text: "详细信息",
        href: "#"
      }]);

      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");
      payService.getPay(payId)
        .then(function(data) {
          $scope.hideLoading();
          $scope.pay = data;
        });
    }

    $scope.onPayButtonClick = function() {
      window.location.href = "payed.html?payId=" + payId;
    };

  }
]);
'use strict';

zongmu.controller("payedController", ["$scope", "breadCrumb", "payService", "dialog",
  function($scope, breadCrumbProvider, payService, dialog) {
    var payId = $.url().param("payId");
    initView() && initData();

    function initView() {
      $scope.setTitle("提现申请记录");
      breadCrumbProvider.setHistories([{
        text: "提现申请记录",
        href: "point.request.list.html"
      }, {
        text: "详细信息",
        href: "pay.detail.html?payId=" + payId
      }, {
        text: "支付",
        href: "#"
      }]);

      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");
      payService.getPay(payId)
        .then(function(data) {
          $scope.hideLoading();
          $scope.pay = data;
        });
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.transcationNo) {
        dialog.showError("支付交易号不能为空。");
        return;
      }

      payService.pay(payId, {
          transcationNo: $scope.transcationNo,
          memo: $scope.memo
        })
        .then(function() {
          dialog.showInfo("保存成功！")
            .then(function() {
              window.location.href = "pay.detail.html?payId=" + payId;
            });
        });
    };

    $scope.onCancelButtonClick = function() {
      window.history.back();
    };

  }
]);
'use strict';

zongmu.controller("userPointController", ["$scope", "breadCrumb", "userPointService", "dialog", "enumService",
  function($scope, breadCrumbProvider, userPointService, dialog, enumService) {
    var pageIndex = 0;
    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "会员积分",
        href: "#"
      }]);

      $scope.columns = [{
        name: "createTime",
        text: "时间"
      }, {
        name: "point",
        text: "收入/支出"
      }, {
        name: "memo",
        text: "详细说明"
      }];
      return true;
    }

    function initData() {
      $scope.sexes = enumService.getSexes();
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");

      userPointService.getMyPoints(pageIndex)
        .then(function(data) {
          $scope.hideLoading();
          $scope.user = data.user;
          $scope.myPoint = data;
          $scope.pageData = {
            totalPage: data.points.totalPages,
            pageIndex: data.points.number
          };
        });
    }

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.onChangedMoneyButtonClicked = function() {
      if(!$scope.user.alipayAccount) {
        dialog.showInfo("请先设置支付宝账号，然后提现！")
          .then(function() {
            window.location.href = "profile.html";
          });
        return;
      }

      if($scope.myPoint.point < 100) {
        dialog.showError("积分余额不足100，不能提现！");
        return;
      }

      window.location.href = "change.money.create.html";
    };

  }
]);
'use strict';

zongmu.controller("pointApproveListController", ["$scope", "breadCrumb", "payService", "dialog",
  function($scope, breadCrumbProvider, payService, dialog) {
    var pageIndex = 0;
    var tabIndex = 0;
    initView() && initData();

    function initView() {
      $scope.setTitle("提现申请记录");
      breadCrumbProvider.setHistories([{
        text: "提现申请记录",
        href: "#"
      }]);

      $scope.tabs = [{
        name: "all",
        text: "全部",
        active: true
      }, {
        name: "PENDING",
        text: "待支付"
      }, {
        name: "PAYED",
        text: "已支付"
      }];

      $scope.columns = [{
        name: "userName",
        text: "申请人"
      }, {
        name: "point",
        text: "提现金额"
      }, {
        name: "createTime",
        text: "申请时间"
      }, {
        name: "status",
        text: "状态"
      }, {
        name: "op",
        text: "操作"
      }];
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");
      payService.getList(pageIndex, tabIndex)
        .then(function(data) {
          $scope.hideLoading();
          $scope.paylist = data.content;
          $scope.pageData = {
            totalPage: data.totalPages,
            pageIndex: data.number
          };
        });
    }

    $scope.$on("tableIndexChanged", function(paginationScope, index) {
      pageIndex = index;
      initData();
    });

    $scope.$on("onTabChanged", function(tabScope, item, index) {
      pageIndex = 0;
      tabIndex = index;
      initData();
    });

    $scope.onPayButtonClick = function(rowData) {
      window.location.href = "payed.html?payId=" + rowData.id;
    };

    $scope.onViewButtonClick = function(rowData) {
      window.location.href = "pay.detail.html?payId=" + rowData.id;
    };

  }
]);
'use strict';

zongmu.controller("profileController", ["$scope", "breadCrumb", "userService", "dialog", "enumService",
  function($scope, breadCrumbProvider, userService, dialog, enumService) {
    initView() && initData();

    function initView() {
      $scope.sexes = enumService.getSexes();
      breadCrumbProvider.setHistories([{
        text: "个人信息",
        href: "#"
      }]);
      return true;
    }

    function initData() {
      $scope.showLoading();
      $scope.setLoadingText("正在加载，请稍等...");
      userService.getProfile()
        .then(function(data) {
          $scope.hideLoading();
          $scope.user = data;
        });
    }

    $scope.onCancelClicked = function() {
      initData();
    };

    $scope.onSaveButtonClicked = function() {
      $scope.showLoading();
      $scope.setLoadingText("正在保存，请稍等...");
      userService.updateProfile($scope.user)
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("保存成功！")
            .then(function() {
              initData();
            });

        });
    };

  }
]);
'use strict';

zongmu.controller("registerInfoController", ["$scope", "userService", "dialog",
  function($scope, userService, dialog) {
    var email = $.url().param("mail");
    initView() && initData();

    function initView() {
      $scope.setTitle("用户注册");
      return true;
    }

    function initData() {
      if(email) {
        $scope.email = email.replace("____", "@");
      }
    }
  }
]);
'use strict';

zongmu.controller("registerController", ["$scope", "userService", "dialog", "passwordUtils",
  function($scope, userService, dialog, passwordUtils) {
    initView() && initData();

    function initView() {
      $scope.setTitle("用户注册");
      return true;
    }

    function initData() {

    }

    $scope.onLoginButtonClick = function() {
      window.location.href = "login.html";
    };

    $scope.onSubmit = function() {
      if(!$scope.email) {
        dialog.showError("邮箱地址不能为空。")
        return;
      }

      if(!$scope.password) {
        dialog.showError("密码不能为空。")
        return;
      }

      if(!$scope.repeatPassword) {
        dialog.showError("重复密码不能为空。")
        return;
      }

      if($scope.password !== $scope.repeatPassword) {
        dialog.showError("两次密码不一致。")
        return;
      }

      if($scope.password.length < 6) {
        dialog.showError("密码必须大于6位数。")
        return;
      }

      if(!passwordUtils.checkPassword($scope.repeatPassword)) {
        dialog.showError(passwordUtils.getMsg());
        return;
      }

      var param = {};
      param.email = $scope.email;
      param.password = $scope.password;
      param.repeatPassword = $scope.repeatPassword;
      $scope.showLoading();
      userService.register(param)
        .then(function() {
          $scope.hideLoading();
          window.location.href = "register.info.html?mail=" + $scope.email.replace("@", "____");
        });
    };

  }
]);
'use strict';

zongmu.controller("resetPasswordController", ["$scope", "breadCrumb", "userService", "dialog", "enumService", "passwordUtils",
  function($scope, breadCrumbProvider, userService, dialog, enumService, passwordUtils) {
    initView() && initData();

    function initView() {
      breadCrumbProvider.setHistories([{
        text: "安全中心",
        href: "#"
      }]);
      return true;
    }

    function initData() {
      $scope.oldPassword = null;
      $scope.password = null;
      $scope.repeatPassword = null;
    }

    $scope.onSaveButtonClicked = function() {
      if(!$scope.oldPassword) {
        dialog.showError("旧密码不能为空！");
        return;
      }

      if(!$scope.password) {
        dialog.showError("新密码不能为空！");
        return;
      }

      if(!$scope.repeatPassword) {
        dialog.showError("重复新密码不能为空！");
        return;
      }

      if($scope.password !== $scope.repeatPassword) {
        dialog.showError("两次密码不一致！");
        return;
      }

      if(!passwordUtils.checkPassword($scope.repeatPassword)) {
        dialog.showError(passwordUtils.getMsg());
        return;
      }

      $scope.showLoading();
      $scope.setLoadingText("正在重置，请稍等...");
      userService.resetPassword({
          oldPassword: $scope.oldPassword,
          password: $scope.password,
          repeatPassword: $scope.repeatPassword
        })
        .then(function() {
          $scope.hideLoading();
          dialog.showInfo("密码重置成功！")
            .then(function() {
              initData();
            })
        });
    };

    $scope.onCancelClicked = function() {
      initData();
    }
  }
]);
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