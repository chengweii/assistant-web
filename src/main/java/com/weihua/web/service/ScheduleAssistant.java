package com.weihua.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.weihua.common.util.CollectionUtil;
import com.weihua.common.util.DateUtil;
import com.weihua.common.util.DateUtil.DateFormatType;
import com.weihua.common.util.DateUtil.TimePeriod;
import com.weihua.common.util.DidaListUtil;
import com.weihua.common.util.DidaListUtil.Task;
import com.weihua.common.util.DidaListUtil.TaskStatus;
import com.weihua.common.util.DidaListUtil.TaskType;
import com.weihua.common.util.DidaListUtil.Task.Item;
import com.weihua.common.util.EmailUtil;
import com.weihua.common.util.EmailUtil.SendInfo;
import com.weihua.common.util.GsonUtil;
import com.weihua.web.constant.AssistantConstant;
import com.weihua.web.constant.OriginType;
import com.weihua.web.dao.FamilyDao;
import com.weihua.web.dao.HolidayArrangementDao;
import com.weihua.web.dao.LifeMotoDao;
import com.weihua.web.dao.impl.FamilyDaoImpl;
import com.weihua.web.dao.impl.HolidayArrangementDaoImpl;
import com.weihua.web.dao.impl.LifeMotoDaoImpl;
import com.weihua.web.entity.request.BaseRequest;
import com.weihua.web.entity.request.Request;
import com.weihua.web.entity.response.Response;
import com.weihua.web.service.annotation.BackServiceLocation;
import com.weihua.web.service.annotation.ServiceLocation;
import com.weihua.web.service.base.BaseAssistant;
import com.weihua.web.util.ConfigUtil;

/**
 * @author chengwei2
 * @category Schedule Service
 */
public class ScheduleAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(ScheduleAssistant.class);
	private static LifeMotoDao lifeMotoDao = new LifeMotoDaoImpl();
	private static HolidayArrangementDao holidayArrangementDao = new HolidayArrangementDaoImpl();
	private static FamilyDao familyDao = new FamilyDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				analysisRequest(baseRequest);
				response = getScheduleList((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		return response;
	}

	private void analysisRequest(BaseRequest baseRequest) {
		String content = baseRequest.getContent();
		Map<String, String> extraInfo = new HashMap<String, String>();
		TaskType taskType = TaskType.fromValue(content);
		if (taskType != null) {
			extraInfo.put("taskType", taskType.getCode());
			baseRequest.setExtraInfo(GsonUtil.toJson(extraInfo));
		}
	}

	@ServiceLocation(value = "getScheduleList")
	public Response getScheduleList(BaseRequest request) throws Exception {
		if (request.getOriginType() != OriginType.WEB)
			return null;
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);

		TaskType taskType = TaskType.fromCode(String.valueOf(extraInfoMap.get("taskType")));
		List<Task> taskList = DidaListUtil.getTaskListFromDida365(taskType, TaskStatus.UNFINISH);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("taskCode", taskType.getCode());
		model.put("taskValue", taskType.getValue());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if (CollectionUtil.isNotEmpty(taskList)) {
			for (Task item : taskList) {
				Map<String, Object> entity = new HashMap<String, Object>();
				entity.put("id", item.id);
				entity.put("title", item.title);
				entity.put("status", item.status);
				list.add(entity);
			}
		}

		model.put("taskList", list);

		return response(model, "schedule/tasklist");
	}

	@BackServiceLocation(value = "getScheduleByTime")
	public Response getScheduleByTime(BaseRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		if (isTriggerServiceReminding(request)) {
			bindScheduleModel(model);

			SendInfo info = new SendInfo();
			info.setReceiveUser(ConfigUtil.get(AssistantConstant.FAMILY_ASSISTANT_EMAIL_SCHEDULE_REMINDEMAILUSER));
			info.setHeadName(AssistantConstant.FAMILY_ASSISTANT_STRING_12);
			Response response = response(model, "schedule/schedule");
			info.setSendHtml(response.getContent());
			EmailUtil.send(info);

			return responseJson(null);
		}

		return null;
	}

	private void bindScheduleModel(Map<String, Object> model) throws Exception {
		Map<String, Object> lifeMoto = lifeMotoDao.findRandomRecord();
		model.put("lifeMoto", lifeMoto.get("moto"));
		model.put("lifeMotoImage", lifeMoto.get("collocation_picture"));

		List<Map<String, Object>> scheduleList = new ArrayList<Map<String, Object>>();
		List<Task> taskList = DidaListUtil.getTaskListFromDida365(TaskType.CURRENT_SCHEDULE, TaskStatus.UNFINISH);
		if (!CollectionUtil.isEmpty(taskList)) {
			for (Task task : taskList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("schedule_time", task.startDate);
				map.put("schedule_title", task.title);
				map.put("schedule_content", task.content);
				scheduleList.add(map);
			}
		}

		boolean isHoliday = holidayArrangementDao
				.findIsHoliday(DateUtil.getDateFormat(new Date(), DateFormatType.YYYY_MM_DD));
		List<Task> triflesTaskList = DidaListUtil.getTaskListFromDida365(TaskType.MY_DAILY, TaskStatus.UNFINISH);
		if (!CollectionUtil.isEmpty(triflesTaskList)) {
			String prefix = isHoliday ? AssistantConstant.FAMILY_ASSISTANT_STRING_6
					: AssistantConstant.FAMILY_ASSISTANT_STRING_7;
			List<Item> items = null;
			for (Task task : triflesTaskList) {
				if (task.title.contains(prefix)) {
					items = task.items;
				}
			}

			for (Item item : items) {
				Map<String, Object> map = new HashMap<String, Object>();
				String[] schedule = item.title.split("##");
				map.put("schedule_time", schedule[0]);
				map.put("schedule_title", schedule[1]);
				map.put("schedule_content", schedule[2]);
				scheduleList.add(map);
			}
		}

		/*
		 * List<Map<String, Object>> triflesList =
		 * familyDao.findRecordListByTime("00:00", "23:59", isHoliday ?
		 * AssistantConstant.FAMILY_ASSISTANT_STRING_6 :
		 * AssistantConstant.FAMILY_ASSISTANT_STRING_7); if
		 * (!CollectionUtil.isEmpty(triflesList)) { for (Map<String, Object>
		 * item : triflesList) { Map<String, Object> map = new HashMap<String,
		 * Object>(); map.put("schedule_time", item.get("record_time"));
		 * map.put("schedule_title", item.get("record_title"));
		 * map.put("schedule_content", item.get("record_content"));
		 * scheduleList.add(map); } }
		 */

		Date tempDate;
		TimePeriod timePeriod;
		for (Map<String, Object> item : scheduleList) {
			if (item.get("schedule_time") == null) {
				item.put("schedule_time", "00:00");
			}
			tempDate = DateUtil.getDateFormat(String.valueOf(item.get("schedule_time")), DateFormatType.HH_MM);
			timePeriod = DateUtil.getTimePeriodByDate(tempDate);
			item.put("time_period_color", TimePeriodColor.fromCode(timePeriod.getCode()).getValue());
		}

		model.put("scheduleList", scheduleList);
	}

	public static enum TimePeriodColor {
		MORNING("MORNING", "rgba(46,204,113,0.6)"), BEFORENOON("BEFORENOON", "rgba(26,188,156,0.6)"), NOON("NOON",
				"rgba(231,76,60,0.6)"), AFTERNOON("AFTERNOON", "rgba(230,126,34,0.6)"), NIGHT("NIGHT",
						"rgba(243,156,18,0.6)"), DEEPNIGHT("DEEPNIGHT", "rgba(192,57,43,0.6)");

		private TimePeriodColor(String code, String value) {
			this.code = code;
			this.value = value;
		}

		private String code;
		private String value;

		public String getCode() {
			return code;
		}

		public String getValue() {
			return value;
		}

		public static TimePeriodColor fromCode(String code) {
			for (TimePeriodColor entity : TimePeriodColor.values()) {
				if (entity.getCode().equals(code)) {
					return entity;
				}
			}
			return TimePeriodColor.MORNING;
		}
	}

}
