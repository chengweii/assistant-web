package com.weihua.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.common.constant.CommonConstant;
import com.weihua.ui.userinterface.AssistantInterface;
import com.weihua.ui.userinterface.UserInterface;
import com.weihua.util.TemplateUtil;

public class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final UserInterface USER_INTERFACE = new AssistantInterface();

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=" + CommonConstant.CHARSET_UTF8);
		String requestContent = request.getParameter(BaseRequest.REQUEST_PARAM_KEY);
		String content = null;
		if (requestContent == null) {
			content = TemplateUtil.renderByTemplateFile(this.getServletContext().getRealPath("/"), "index.htm", null);
		} else {
			content = USER_INTERFACE.getResponse(requestContent);
		}

		PrintWriter out = response.getWriter();
		out.println(content);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}