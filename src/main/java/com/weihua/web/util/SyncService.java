package com.weihua.web.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.weihua.message.MessageService;

public class SyncService implements ServletContextListener {
	private static final Logger LOGGER = Logger.getLogger(SyncService.class);
	private static Timer timer = null;
	private static final int DELAY = 5000;
	private static final int PERIOD = 15 * 60 * 1000;

	static {
		initUtilConfig();
	}

	private static void initUtilConfig() {
		ResourceBundle bundle = ResourceBundle.getBundle("assets/config", Locale.getDefault());
		Map<String, String> map = new HashMap<String, String>();
		for (String key : bundle.keySet()) {
			map.put(key, bundle.getString(key));
		}
		ConfigUtil.init(map);
	}

	public SyncService() {
		super();
	}

	private void init(ServletContextEvent servletContextEvent) {
		if (timer == null) {
			timer = new Timer();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().log("SyncService is starting.");
		init(servletContextEvent);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					MessageService.notifyConsumers();
				} catch (Exception e) {
					LOGGER.error("SyncService run error:", e);
				}
			}
		}, DELAY, PERIOD);
		servletContextEvent.getServletContext().log("SyncService was started.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		timer.cancel();
		servletContextEvent.getServletContext().log("SyncService was stopped.");
	}

}
