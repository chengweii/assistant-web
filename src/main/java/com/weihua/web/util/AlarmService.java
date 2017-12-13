package com.weihua.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.weihua.common.util.GsonUtil;
import com.weihua.common.util.TemplateUtil;
import com.weihua.web.constant.OriginType;
import com.weihua.web.dao.impl.BaseDao;
import com.weihua.web.entity.alarm.AlarmInfo;
import com.weihua.web.entity.request.BaseRequest;
import com.weihua.web.service.MainAssistant;

public class AlarmService implements ServletContextListener {
	private static final Logger LOGGER = Logger.getLogger(AlarmService.class);
	private static Timer timer = null;
	private static final int DELAY = 5000;
	private static final int PERIOD = 1000;

	private static final String ALARM_REQUEST_CONTENT;
	private static MainAssistant mainAssistant;

	private static Queue<AlarmInfo> msgQueue = new ConcurrentLinkedQueue<AlarmInfo>();

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
			BaseDao.init(new WebDBHelper(webRootPath));
			mainAssistant = MainAssistant.getInstance();
		}
	}

	public static final String GET_MSG_FROM_LOCAL_QUEUE = "getMsgFromLocalQueue";

	public static String getMsgFromLocalQueue() {
		List<AlarmInfo> msgList = new ArrayList<AlarmInfo>();
		while (true) {
			AlarmInfo msg = msgQueue.poll();
			if (msg != null) {
				msgList.add(msg);
			} else {
				break;
			}
		}
		return GsonUtil.toJson(msgList);
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().log("AlarmService is starting.");
		init(servletContextEvent);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					String responseContent = mainAssistant.execute(ALARM_REQUEST_CONTENT);
					Map<String, Object> alarm = GsonUtil.getEntityFromJson(responseContent,
							new TypeToken<Map<String, Object>>() {
							});
					if (alarm != null && alarm.get("msgList") != null) {
						List<AlarmInfo> alarmList = GsonUtil.getEntityFromJson(alarm.get("msgList").toString(),
								new TypeToken<List<AlarmInfo>>() {
								});
						for (AlarmInfo item : alarmList) {
							msgQueue.add(item);
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
