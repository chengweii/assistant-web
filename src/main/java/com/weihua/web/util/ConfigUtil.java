package com.weihua.web.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.weihua.common.util.DidaListUtil;
import com.weihua.common.util.EmailUtil;

public class ConfigUtil {
	private static Logger LOGGER = Logger.getLogger(ConfigUtil.class);
	private static Map<String, String> properties = new HashMap<String, String>();

	public static String get(String key) {
		if (!inited) {
			Throwables.propagate(new RuntimeException("please init ConfigUtil firstly"));
		}
		return properties.get(key);
	}

	private static boolean inited = false;

	public static void init(Map<String, String> configs) {
		if (configs != null) {
			for (String key : configs.keySet()) {
				properties.put(key, configs.get(key));
			}
			inited = true;

			EmailUtil.init(properties.get("email.dataEmailUser"), properties.get("email.dataEmailUserPwd"));
			DidaListUtil.init(properties.get("didalist.username"), properties.get("didalist.password"));
		}
	}
}
