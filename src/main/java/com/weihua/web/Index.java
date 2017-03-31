package com.weihua.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weihua.common.constant.CommonConstant;
import com.weihua.ui.userinterface.AssistantInterface;
import com.weihua.ui.userinterface.UserInterface;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.TemplateUtil;
import com.weihua.util.TemplateUtil.TemplateReader;

public class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static UserInterface userInterface = new AssistantInterface();

	private static String webRootPath = null;

	private static final String REQUEST_PARAM_NAME = "requestContent";

	public void init() throws ServletException {
		webRootPath = this.getServletContext().getRealPath("/");
		WebTemplateReader templateReader = new WebTemplateReader();
		TemplateUtil.initTemplateReader(templateReader);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=" + CommonConstant.CHARSET_UTF8);
		String requestContent = request.getParameter(REQUEST_PARAM_NAME);
		String content = null;
		if (requestContent == null) {
			content = TemplateUtil.renderByTemplateFile(webRootPath, "index.htm", null);
		} else {
			content = userInterface.getResponse(requestContent);
		}

		PrintWriter out = response.getWriter();
		out.println(content);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

	public static class WebTemplateReader implements TemplateReader {

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

}