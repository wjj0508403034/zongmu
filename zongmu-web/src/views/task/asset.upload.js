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