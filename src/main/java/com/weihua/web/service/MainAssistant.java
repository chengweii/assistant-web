package com.weihua.web.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.weihua.common.util.GsonUtil;
import com.weihua.common.util.HttpUtil;
import com.weihua.web.constant.AssistantConstant;
import com.weihua.web.constant.AssistantType;
import com.weihua.web.constant.ServiceType;
import com.weihua.web.context.Context;
import com.weihua.web.dao.MainDao;
import com.weihua.web.dao.impl.MainDaoImpl;
import com.weihua.web.entity.request.BaseRequest;
import com.weihua.web.entity.request.Request;
import com.weihua.web.entity.response.BaseResponse;
import com.weihua.web.entity.response.Response;
import com.weihua.web.service.annotation.ServiceLocation;
import com.weihua.web.service.base.BaseAssistant;

/**
 * @author chengwei2
 * @category Base Service for all
 */
public class MainAssistant extends BaseAssistant {
	private static Logger LOGGER = Logger.getLogger(MainAssistant.class);

	private static MainDao mainDao = new MainDaoImpl();

	static {
		setAssistantMap(mainDao.findAssistantList());
	}

	public static MainAssistant getInstance() {
		return (MainAssistant) getAssistantByAssistantType(AssistantType.MAIN_ASSISTANT.getValue());
	}

	public String execute(String request) {
		BaseRequest baseRequest = new BaseRequest(request);
		Assistant assistant = assignAssistant(baseRequest);
		Response response = assistant.getResponse(baseRequest);
		if (response == null) {
			response = this.getResponse(baseRequest);
		}

		if (baseRequest.isLocationPath() == null) {
			Context.recordHistory(baseRequest.getAssistantType(), ServiceType.FRONT_SERVICE, baseRequest,
					(BaseResponse) response);
		}

		return response.getContent();
	}

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = toHome((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		return response;
	}

	@ServiceLocation(value = "toHome")
	public Response toHome(BaseRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (request.getContent() == null || request.getContent().equals("")
				|| request.getContent().equals("MyAssistant")) {
			model.put("welcomeMsg", AssistantConstant.MAIN_ASSISTANT_STRING_1);
			List<Map<String, Object>> commonServiceList = mainDao.findAssistantHistory(3);
			if (commonServiceList != null && commonServiceList.size() > 0) {
				List<Map<String, String>> array = new ArrayList<Map<String, String>>();
				for (int i = 0; i < commonServiceList.size(); i++) {
					Map<String, String> map = GsonUtil
							.getMapFromJson(String.valueOf(commonServiceList.get(i).get("request_content")));
					array.add(map);
				}
				model.put("commonServiceList", GsonUtil.toJson(array));
			}
		} else {
			model.put("welcomeMsg",
					MessageFormat.format(AssistantConstant.MAIN_ASSISTANT_STRING_2, request.getContent()));
			model.put("assistantList", getAssistantMap());
		}
		return response(model, "main");
	}

	@ServiceLocation(value = "crossDomainAccess")
	public Response crossDomainAccess(BaseRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (!Strings.isNullOrEmpty(request.getExtraInfo())) {
			Map<String, String> requestParams = GsonUtil.getMapFromJson(request.getExtraInfo());
			if (requestParams != null) {
				String content = "";
				Map<String, String> params = GsonUtil.getMapFromJson(requestParams.get("params"));
				Map<String, String> headers = GsonUtil.getMapFromJson(requestParams.get("headers"));
				if ("GET".equalsIgnoreCase(requestParams.get("method"))) {
					content = HttpUtil.get(requestParams.get("url"), params, headers);
				} else if ("POST".equalsIgnoreCase(requestParams.get("method"))) {
					content = HttpUtil.post(requestParams.get("url"), params, headers);
				}
				model.put("content", content);
			}
		}
		return responseJson(model);
	}

	@ServiceLocation(value = "callAlarmService")
	public Response callAlarmService(BaseRequest request) {
		List<Map<String, Object>> result = mainDao.findAssistantServiceList();

		Map<String, Object> model = new HashMap<String, Object>();
		List<String> msgList = new ArrayList<String>();

		Assistant assistant;
		Response response;
		BaseRequest baseRequest;
		try {
			for (Map<String, Object> item : result) {
				Class<?> assistantType;
				assistantType = Class.forName(AssistantType.fromCode(item.get("assistant_id").toString()).getValue());
				assistant = (Assistant) assistantType.newInstance();
				baseRequest = new BaseRequest(item.get("request_content").toString());
				baseRequest.setOriginType(request.getOriginType());
				baseRequest.setExtraInfo(item.get("time_config").toString());
				baseRequest.setAssistantType(baseRequest.getOriginAssistantType());
				response = assistant.getResponse(baseRequest);
				if (response != null) {
					if (response.getContent() != null) {
						msgList.add(response.getContent());
					}
					Context.recordHistory(baseRequest.getAssistantType(), ServiceType.BACK_SERVICE, baseRequest,
							(BaseResponse) response);
				}
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}

		model.put("msgList", msgList);
		return responseJson(model);
	}

	private Assistant assignAssistant(BaseRequest baseRequest) {
		Assistant assistant = this;
		try {
			if (baseRequest.getOriginAssistantType() != AssistantType.MAIN_ASSISTANT) {
				baseRequest.setAssistantType(baseRequest.getOriginAssistantType());
				assistant = getAssistantByAssistantType(baseRequest.getOriginAssistantType().getValue());
			} else {
				List<Map<String, Object>> assistantByRelatedWordList = mainDao.findAssistantByRelatedWordList();
				if (assistantByRelatedWordList != null && assistantByRelatedWordList.size() > 0) {
					for (Map<String, Object> entity : assistantByRelatedWordList) {
						if (entity.get("associated_word").equals(baseRequest.getContent())) {
							AssistantType assistantType = AssistantType.fromCode(entity.get("assistant_id").toString());
							baseRequest.setAssistantType(assistantType);
							assistant = getAssistantByAssistantType(assistantType.getValue());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		return assistant;
	}
}
