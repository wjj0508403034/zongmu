package com.zongmu.service.util;

public class ZStringUtil {

	public static String longToStr(Long val) {
		if (val == null) {
			return "";
		}

		return String.valueOf(val);
	}
	
	public static String intToStr(Integer val) {
		if (val == null) {
			return "";
		}

		return String.valueOf(val);
	}
	
	public static String floatToStr(Float val) {
		if (val == null) {
			return "";
		}

		return String.valueOf(val);
	}
}
