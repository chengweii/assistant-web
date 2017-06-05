package com.weihua.web.util;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.weihua.message.MessageDispenser;
import com.weihua.util.EmailUtil;

public class SyncService implements ServletContextListener {
	private static final Logger LOGGER = Logger.getLogger(SyncService.class);
	private static Timer timer = null;
	private static final int DELAY = 5000;
	private static final int PERIOD = 15 * 60 * 1000;

	static {
		ResourceBundle emailBundle = ResourceBundle.getBundle("assets/email", Locale.getDefault());
		EmailUtil.initDefaultEmailAccountInfo(emailBundle.getString("dataEmailUser"),
				emailBundle.getString("dataEmailUserPwd"), emailBundle.getString("remindEmailUser"),
				emailBundle.getString("notifyEmailUser"));
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
					MessageDispenser.notifyConsumers();
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