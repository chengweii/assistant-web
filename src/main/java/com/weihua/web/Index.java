package com.weihua.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weihua.common.util.TemplateUtil;
import com.weihua.web.constant.AssistantConstant;
import com.weihua.web.entity.request.BaseRequest;
import com.weihua.web.service.MainAssistant;
import com.weihua.web.util.AlarmService;

public class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=" + AssistantConstant.CHARSET_UTF8);
		String requestContent = request.getParameter(BaseRequest.REQUEST_PARAM_KEY);
		String content = null;
		if (requestContent == null) {
			content = TemplateUtil.renderByTemplateFile(this.getServletContext().getRealPath("/"), "index.htm", null);
		} else {
			if (requestContent.equals(AlarmService.GET_MSG_FROM_LOCAL_QUEUE)) {
				content = AlarmService.getMsgFromLocalQueue();
			} else {
				content = MainAssistant.getInstance().execute(requestContent);
			}
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