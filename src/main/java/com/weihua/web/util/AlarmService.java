package com.weihua.web.util;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.weihua.assistant.constant.OriginType;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.ui.userinterface.AssistantInterface;
import com.weihua.ui.userinterface.UserInterface;
import com.weihua.util.DBUtil;
import com.weihua.util.GsonUtil;
import com.weihua.util.TemplateUtil;

public class AlarmService implements ServletContextListener {
	private static final Logger LOGGER = Logger.getLogger(AlarmService.class);
	private static Timer timer = null;
	private static final int DELAY = 5000;
	private static final int PERIOD = 1000;

	private static final UserInterface USER_INTERFACE = new AssistantInterface();
	private static final String ALARM_REQUEST_CONTENT;

	static {
		BaseRequest.RequestData request = new BaseRequest.RequestData();
		request.isLocationPath = true;
		request.requestContent = "callAlarmService";
		request.originType = OriginType.WEB.getCode();
		ALARM_REQUEST_CONTENT = GsonUtil.toJson(request);
	}

	public AlarmService() {
		super();
	}

	private void init(ServletContextEvent servletContextEvent) {
		if (timer == null) {
			timer = new Timer();
			String webRootPath = servletContextEvent.getServletContext().getRealPath("/");
			LOGGER.info("webRootPath:" + webRootPath);
			TemplateUtil.initTemplateReader(new WebTemplateReader());
			DBUtil.initDBHelper(new WebDBHelper(webRootPath));
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().log("AlarmService is starting.");
		init(servletContextEvent);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					String responseContent = USER_INTERFACE.getResponse(ALARM_REQUEST_CONTENT);
					Map<String, Object> alarm = GsonUtil.getEntityFromJson(responseContent,
							new TypeToken<Map<String, Object>>() {
							});
					if (alarm != null && alarm.get("msgList") != null) {
						List<Map<String, String>> alarmList = GsonUtil.getEntityFromJson(
								alarm.get("msgList").toString(), new TypeToken<List<Map<String, String>>>() {
								});
						for (Map<String, String> item : alarmList) {
							showMsg(item);
						}
					}
				} catch (Exception e) {
					LOGGER.error("AlarmService run error:", e);
				}
			}
		}, DELAY, PERIOD);
		servletContextEvent.getServletContext().log("AlarmService was started.");
	}

	private void showMsg(Map<String, String> msg) {
		if (msg != null && msg.get("title") != null) {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("cmd /c mshta vbscript:msgbox(\"" + msg.get("content") + "\",64,\"" + msg.get("title")
						+ "\")(window.close)");
			} catch (Exception e) {
				LOGGER.error("Show msg error.", e);
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		timer.cancel();
		servletContextEvent.getServletContext().log("AlarmService was stopped.");
	}

}
