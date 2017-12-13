package com.weihua.web.util;

import java.io.InputStream;

import com.google.common.base.Throwables;
import com.weihua.common.util.TemplateUtil.TemplateReader;
import com.weihua.web.constant.AssistantConstant;

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
			content = new String(buffer, AssistantConstant.CHARSET_UTF8);
		} catch (Exception e) {
			Throwables.propagate(e);
		}

		return content;
	}

}
