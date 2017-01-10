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