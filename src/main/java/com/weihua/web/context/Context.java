package com.weihua.web.context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.weihua.web.constant.AssistantType;
import com.weihua.web.constant.ServiceType;
import com.weihua.web.entity.request.BaseRequest;
import com.weihua.web.entity.response.BaseResponse;
import com.weihua.web.dao.MainDao;
import com.weihua.web.dao.impl.MainDaoImpl;
import com.weihua.common.util.DateUtil;
import com.weihua.common.util.DateUtil.DateFormatType;

public class Context {

	private static MainDao mainDao = new MainDaoImpl();

	public static boolean recordHistory(AssistantType assistantType, ServiceType serviceType, BaseRequest request,
			BaseResponse response) {
		if (assistantType == null)
			return false;

		HistoryRecord historyRecord = new HistoryRecord();
		historyRecord.setRequest(request);
		historyRecord.setAssistantType(assistantType);
		historyRecord.setServiceType(serviceType);
		historyRecord.setCreateTime(new Date());

		persistenceHistory(historyRecord);

		return true;
	}

	public static List<HistoryRecord> findHistoryByAssistantId(AssistantType assistantType) {
		if (assistantType == null)
			return null;

		List<HistoryRecord> historyRecordList = new ArrayList<HistoryRecord>();

		List<Map<String, Object>> mapList = mainDao.findAssistantHistoryByAssistantId(assistantType.getCode());

		if (mapList != null && mapList.size() > 0) {
			for (Map<String, Object> item : mapList) {
				HistoryRecord historyRecord = new HistoryRecord();
				historyRecord.setRequest(new BaseRequest(String.valueOf(item.get("request_content"))));
				historyRecord.setAssistantType(assistantType);
				historyRecord.setServiceType(ServiceType.fromCode(String.valueOf(item.get("service_type"))));
				historyRecord.setCreateTime(DateUtil.getDateFormat(String.valueOf(item.get("create_time")),
						DateFormatType.YYYY_MM_DD_HH_MM_SS));
				historyRecordList.add(historyRecord);
			}
		}

		return historyRecordList;
	}

	public static HistoryRecord findLastBackAssistantHistory(AssistantType assistantType, String requestContent) {
		if (assistantType == null || requestContent == null)
			return null;

		Map<String, Object> map = mainDao.findLastBackAssistantHistory(assistantType.getCode(), requestContent);

		if (map != null) {
			HistoryRecord historyRecord = new HistoryRecord();
			historyRecord.setRequest(new BaseRequest(String.valueOf(map.get("request_content"))));
			historyRecord.setAssistantType(assistantType);
			historyRecord.setServiceType(ServiceType.fromCode(String.valueOf(map.get("service_type"))));
			historyRecord.setCreateTime(
					DateUtil.getDateFormat(String.valueOf(map.get("create_time")), DateFormatType.YYYY_MM_DD_HH_MM_SS));
			return historyRecord;
		}

		return null;
	}

	private static void persistenceHistory(HistoryRecord historyRecord) {
		mainDao.recordAssistantHistory(historyRecord.getAssistantType().getCode(),
				historyRecord.getRequest().getOriginRequest(), historyRecord.getServiceType().getCode(),
				DateUtil.getDateFormat(historyRecord.getCreateTime(), DateFormatType.YYYY_MM_DD_HH_MM_SS));
	}

	public static class HistoryRecord {
		private BaseRequest request;
		private AssistantType assistantType;
		private ServiceType serviceType;
		private Date createTime;

		public BaseRequest getRequest() {
			return request;
		}

		public void setRequest(BaseRequest request) {
			this.request = request;
		}

		public AssistantType getAssistantType() {
			return assistantType;
		}

		public void setAssistantType(AssistantType assistantType) {
			this.assistantType = assistantType;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public ServiceType getServiceType() {
			return serviceType;
		}

		public void setServiceType(ServiceType serviceType) {
			this.serviceType = serviceType;
		}

	}
}
