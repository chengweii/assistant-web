package com.weihua.web.util;

import java.io.InputStream;

import com.weihua.common.constant.CommonConstant;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.TemplateUtil.TemplateReader;

public class WebTemplateReader implements TemplateReader {

	private static final String BASE_PATH = "/" + TEMPLATE_ROOT + "/";

	@Override
	public String getTemplateContent(String templateName) {
		String content = "";
		try {
			InputStream input = this.getClass().getResourceAsStream(BASE_PATH + templateName);
			int size = input.available();

			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();
			content = new String(buffer, CommonConstant.CHARSET_UTF8);
		} catch (Exception e) {
			content = ExceptionUtil.getStackTrace(e);
		}

		return content;
	}

}
