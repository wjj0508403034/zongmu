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