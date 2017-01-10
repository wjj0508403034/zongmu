'use strict';
var huoyunUI = angular.module('huoyun-ui', ["ngDialog","ngFileUpload","ui.bootstrap.datetimepicker"]);
huoyunUI.objects = {};
huoyunUI.definedObject = function(name , fn){
  if(huoyunUI.objects[name]){
    console.error("Object " + name + " has been defined.");
  }else{
    huoyunUI.objects[name] = fn;
  }
};
huoyunUI.newObject = function(name,args){
  var obj = huoyunUI.objects[name];
  if(obj && typeof obj === "function"){
    return obj.apply(this,args);
  }else{
    console.error("Create new object " + name + " failed.");
  }
}