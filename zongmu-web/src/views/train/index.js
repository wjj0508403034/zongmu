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