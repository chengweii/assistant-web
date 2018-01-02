package com.weihua.web.service.base;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.weihua.web.constant.OriginType;
import com.weihua.web.context.Context;
import com.weihua.web.context.Context.HistoryRecord;
import com.weihua.web.entity.request.BaseRequest;
import com.weihua.web.service.Assistant;
import com.weihua.common.util.DateUtil;
import com.weihua.common.util.GsonUtil;

public abstract class ServiceAssistant implements Assistant {

	private static Logger LOGGER = Logger.getLogger(ServiceAssistant.class);

	protected static boolean isTriggerServiceReminding(BaseRequest request) {
		ServiceRemindTimeConfig serviceRemindTimeConfig = getServiceRemindTimeConfig(request);
		if (serviceRemindTimeConfig == null) {
			return false;
		}
		if (ServiceTriggerPeriod.DAY == serviceRemindTimeConfig.getServiceTriggerPeriod()) {
			Date currentDate = new Date();
			String currentTime = DateUtil.getDateFormat(currentDate, "HH:mm");
			if (serviceRemindTimeConfig.remindTimes.contains(currentTime)) {
				currentTime = DateUtil.getDateFormat(currentDate, "yyyy-MM-dd ") + currentTime;
				HistoryRecord lastHistoryRecord = Context.findLastBackAssistantHistory(request.getAssistantType(),
						request.getOriginRequest());

				String remindTime = "";
				if (lastHistoryRecord != null) {
					remindTime = DateUtil.getDateFormat(lastHistoryRecord.getCreateTime(), "yyyy-MM-dd HH:mm");
				}

				boolean result = lastHistoryRecord == null || !currentTime.equals(remindTime);
				if (result) {
					LOGGER.info("ServiceAssistant currentTime:" + currentTime + "remindTime:" + remindTime);
				}
				return result;
			}
		} else if (ServiceTriggerPeriod.SECOND == serviceRemindTimeConfig.getServiceTriggerPeriod()) {
			HistoryRecord lastHistoryRecord = Context.findLastBackAssistantHistory(request.getAssistantType(),
					request.getOriginRequest());
			return lastHistoryRecord == null || DateUtil.getDateDiff(DateUtil.getNowDateTime(),
					lastHistoryRecord.getCreateTime()) > Long.valueOf(serviceRemindTimeConfig.remindTimes.get(0));
		}

		return false;

	}

	private enum ServiceTriggerPeriod {
		SECOND, DAY, WEEK, MONTH, SEASON, YEAR;
		public static ServiceTriggerPeriod fromCode(String code) {
			for (ServiceTriggerPeriod entity : ServiceTriggerPeriod.values()) {
				if (String.valueOf(entity.ordinal()).equals(code)) {
					return entity;
				}
			}
			return null;
		}
	}

	private static ServiceRemindTimeConfig getServiceRemindTimeConfig(BaseRequest request) {
		if (request.getOriginType() != OriginType.WEB)
			return null;
		ServiceRemindTimeConfig serviceRemindTimeConfig = GsonUtil.getEntityFromJson(request.getExtraInfo(),
				new TypeToken<ServiceRemindTimeConfig>() {
				});
		return serviceRemindTimeConfig;
	}

	private static class ServiceRemindTimeConfig {
		public ServiceTriggerPeriod getServiceTriggerPeriod() {
			return ServiceTriggerPeriod.fromCode(triggerPeriod);
		}

		public String triggerPeriod;
		public List<String> remindTimes;

		@Override
		public String toString() {
			return "{serviceTriggerPeriod:" + getServiceTriggerPeriod().name() + ",remindTimes:" + remindTimes + "}";
		}
	}
}
