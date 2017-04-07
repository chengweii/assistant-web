package com.weihua.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.common.constant.CommonConstant;
import com.weihua.ui.userinterface.AssistantInterface;
import com.weihua.ui.userinterface.UserInterface;
import com.weihua.util.DBUtil;
import com.weihua.util.TemplateUtil;
import com.weihua.web.util.WebDBHelper;
import com.weihua.web.util.WebTemplateReader;

public class Index extends HttpServlet {

	private static Logger LOGGER = Logger.getLogger(Index.class);

	private static final long serialVersionUID = 1L;

	private static UserInterface userInterface = new AssistantInterface();

	private static String webRootPath = null;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=" + CommonConstant.CHARSET_UTF8);
		String requestContent = request.getParameter(BaseRequest.REQUEST_PARAM_KEY);
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

	public void init() throws ServletException {
		webRootPath = this.getServletContext().getRealPath("/");
		LOGGER.info("webRootPath:" + webRootPath);
		TemplateUtil.initTemplateReader(new WebTemplateReader());
		DBUtil.initDBHelper(new WebDBHelper(webRootPath));
	}

}