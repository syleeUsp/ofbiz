// ==================================================
//
// jquery-input-mask-phone-number v1.0
//
// Licensed (The MIT License)
//
// Copyright © Raja Rama Mohan Thavalam <rajaram.tavalam@gmail.com>
//
// ==================================================

(function ($) {
    $.fn.usPhoneFormat = function (options) {
        var params = $.extend({
            format: 'xxx-xxx-xxxx',
            international: false,

        }, options);

        if (params.format === 'xxx-xxx-xxxx') {
            $(this).bind('paste', function (e) {
                e.preventDefault();
                var inputValue = e.originalEvent.clipboardData.getData('Text');
                inputValue = inputValue.replace(/-\s/gi, "");
                if(inputValue.indexOf(",") >= 0) {
                	inputValue = $.trim(inputValue);
                	var strArry = inputValue.split(",");
                	if(strArry.length > 0) {
                		var resultStr = "";
                		var i = 0;
                		for(var str of strArry) {
                			if (!$.isNumeric(str)) {
                                return "";
                            }
                			str = String(str.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3"));
                			str = str.substring(0, 12);
                			strArry[i] = str;
                			i++;
                		}
                		$(this).val(strArry.join(", "));
                	}
                } else {
                	if (!$.isNumeric(inputValue)) {
                		return "";
                	}

            		inputValue = String(inputValue.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3"));
            		inputValue = inputValue.substring(0, 12);
            		$(this).val(inputValue);
                }
            });
            $(this).bind('change', function (e) {
                var inputValue = $(this).val();
                inputValue = inputValue.replace(/-\s/gi, "");
                if(inputValue.indexOf(",") >= 0) {
                	inputValue = $.trim(inputValue);
                	var strArry = inputValue.split(",");
                	if(strArry.length > 0) {
                		var resultStr = "";
                		var i = 0;
                		for(var str of strArry) {
                			if (!$.isNumeric(str)) {
                                return "";
                            }
                			str = String(str.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3"));
                			str = str.substring(0, 12);
                			strArry[i] = str;
                			i++;
                		}
                		$(this).val(strArry.join(", "));
                	}
                } else {
            		inputValue = String(inputValue.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3"));
            		inputValue = inputValue.substring(0, 12);
            		$(this).val(inputValue);
                }
            });
        } else if (params.format === '(xxx) xxx-xxxx') {
            $(this).bind('change', function (e) {
            	var inputValue = $(this).val();
            	inputValue = inputValue.replace(/-\(\)\s/gi, "");
                if(inputValue.indexOf(",") >= 0) {
                	inputValue = $.trim(inputValue);
                	var strArry = inputValue.split(",");
                	if(strArry.length > 0) {
                		var resultStr = "";
                		var i = 0;
                		for(var str of strArry) {
                			if (!$.isNumeric(str)) {
                                return "";
                            }
                			str = String(str.replace(/(\d{3})(\d{3})(\d{4})/, "($1) $2-$3"));
                			str = str.substring(0, 14);
                			strArry[i] = str;
                			i++;
                		}
                		$(this).val(strArry.join(", "));
                	}
                } else {
            		inputValue = String(inputValue.replace(/(\d{3})(\d{3})(\d{4})/, "($1) $2-$3"));
            		inputValue = inputValue.substring(0, 14);
            		$(this).val(inputValue);
                }
            });
            $(this).bind('paste', function (e) {
            	e.preventDefault();
                var inputValue = e.originalEvent.clipboardData.getData('Text');
                inputValue = inputValue.replace(/-\(\)\s/gi, "");
                if(inputValue.indexOf(",") >= 0) {
                	inputValue = $.trim(inputValue);
                	var strArry = inputValue.split(",");
                	if(strArry.length > 0) {
                		var resultStr = "";
                		var i = 0;
                		for(var str of strArry) {
                			if (!$.isNumeric(str)) {
                                return "";
                            }
                			str = String(str.replace(/(\d{3})(\d{3})(\d{4})/, "($1) $2-$3"));
                			str = str.substring(0, 14);
                			strArry[i] = str;
                			i++;
                		}
                		$(this).val(strArry.join(", "));
                	}
                } else {
                	if (!$.isNumeric(inputValue)) {
                		return "";
                	}

            		inputValue = String(inputValue.replace(/(\d{3})(\d{3})(\d{4})/, "($1) $2-$3"));
            		inputValue = inputValue.substring(0, 14);
            		$(this).val(inputValue);
                }
            });
        }
    }
}(jQuery));




//$(this).on('keyup', function (e) {
//if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
//  return "";
//}
//var curchr = this.value.length;
//var curval = $(this).val();
//if (curchr == 3 && e.which != 8 && e.which != 0) {
//  $(this).val('(' + curval + ')' + " ");
//} else if (curchr == 9 && e.which != 8 && e.which != 0) {
//  $(this).val(curval + "-");
//}
//$(this).attr('maxlength', '14');
//});
//$(this).on('keyup', function (e) {
//if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57) && e.which != 88) {
//  return "";
//}
//
//inputValue = this.value;
//if(inputValue.indexOf(",") >= 0) {
//	inputValue = $.trim(inputValue);
//	var strArry = inputValue.split(",");
//	if(strArry.length > 0) {
//		var resultStr = "";
//		var i = 0;
//		for(var str of strArry) {
//			if (!$.isNumeric(str)) {
//              return "";
//          }
//			str = String(str.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3"));
//			str = str.substring(0, 12);
//			strArry[i] = str;
//			i++;
//		}
//		$(this).val(strArry.join(", "));
//	}
//} else {
//	console.log("ddddddddd");
//	var curchr = this.value.length;
//  var curval = $(this).val();
//  if (curchr == 3 && e.which != 8 && e.which != 0) {
//      $(this).val(curval + "-");
//  } else if (curchr == 7 && e.which != 8 && e.which != 0) {
//      $(this).val(curval + "-");
//  }
//}
//});