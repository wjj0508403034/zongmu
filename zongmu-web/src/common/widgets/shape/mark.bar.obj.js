'use strict';

angular.module('huoyun-ui').definedObject("markBarObj", function() {
  var svgDrawObj = null;

  function enableDrawing() {
    if (svgDrawObj) {
      svgDrawObj.readonly = false;
    }
  }

  function disableDrawing() {
    if (svgDrawObj) {
      svgDrawObj.readonly = true;
    }
  }

  return {
    disabled: false,
    visibility: false,
    canMark: false,
    setDisabled: function(val) {
      this.disabled = val;
      this.setCanMark(false);
    },
    setVisibility: function(val) {
      this.visibility = val;
      this.setCanMark(false);
    },
    setCanMark: function(val) {
      if (this.canMark !== val) {
        this.canMark = val;
        if (val) {
          enableDrawing();
        } else {
          disableDrawing();
        }
      }
    },
    deleteShape: function() {
      if (!this.disabled) {
        deleteSelected();
      }
    },
    setAttrs: function() {
      if (!this.disabled) {
        if (currentSelectedShape) {
          showAttrDialog();
        } else {
          dialog.showWarn("请先选择要标记的图形！");
        }
      }
    },
    select: function() {
      if (!this.disabled) {
        this.setCanMark(false);
      }
    },
    draw: function() {
      if (!this.disabled) {
        this.setCanMark(true);
      }
    },
    setSvgDrawObj: function(val) {
      svgDrawObj = val;
    }
  };
});