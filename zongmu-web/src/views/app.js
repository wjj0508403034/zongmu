'use strict';

var zongmu = angular.module("zongmu", ["huoyun-ui", "ngDialog"]);
zongmu.constant("serviceUrl", "/service/");
zongmu.constant("mediaServiceUrl", "http://localhost:8083/");
//"aliyun"
zongmu.constant("appEnv", "");

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
        href: "http://www.zongmutech.com/"
      }, {
        text: "联系我们(QQ:123456;Tel:12345678)",
        href: "#foot"
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