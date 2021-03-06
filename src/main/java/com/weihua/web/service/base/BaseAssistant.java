package com.weihua.web.service.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.weihua.common.util.GsonUtil;
import com.weihua.common.util.TemplateUtil;
import com.weihua.web.constant.AssistantType;
import com.weihua.web.entity.request.BaseRequest;
import com.weihua.web.entity.response.BaseResponse;
import com.weihua.web.entity.response.Response;
import com.weihua.web.service.Assistant;
import com.weihua.web.service.annotation.BackServiceLocation;
import com.weihua.web.service.annotation.ServiceLocation;

public abstract class BaseAssistant extends ServiceAssistant {

	private static Logger LOGGER = Logger.getLogger(BaseAssistant.class);

	private static final String TEMPLATE_SUFFIX = ".htm";

	private static final String DEFAULT_TEMPLATE = "default";

	private static final String ASSISTANT_NAME = "assistantName";

	private static final String ASSISTANT_TYPE = "assistantType";

	private static final String ASSISTANT_TITLE = "assistantTitle";

	private static List<Map<String, Object>> assistantNameList = null;

	private static Map<String, Assistant> assistantCache = new HashMap<String, Assistant>();

	protected Response response(Map<String, Object> model, String templatePath) {
		bindCommonInfo(model);

		String content = TemplateUtil.renderByTemplateReader(templatePath + TEMPLATE_SUFFIX, model);
		BaseResponse response = new BaseResponse(content);
		if (model != null) {
			String modelJson = GsonUtil.toJson(model);
			response.setMetaData(modelJson);
		}

		return response;
	}

	protected Response response(Map<String, Object> model) {
		bindCommonInfo(model);

		String content = TemplateUtil.renderByTemplateReader(DEFAULT_TEMPLATE + TEMPLATE_SUFFIX, model);
		BaseResponse response = new BaseResponse(content);
		if (model != null) {
			String modelJson = GsonUtil.toJson(model);
			response.setMetaData(modelJson);
		}

		return response;
	}

	protected Response responseJson(Map<String, Object> model) {
		String modelJson = "{}";

		bindCommonInfo(model);

		if (model != null)
			modelJson = GsonUtil.toJson(model);

		BaseResponse response = new BaseResponse(modelJson);
		response.setMetaData(modelJson);

		return response;
	}

	protected Response responseJson(Object model) {
		String modelJson = "{}";

		if (model != null)
			modelJson = GsonUtil.toJson(model);

		BaseResponse response = new BaseResponse(modelJson);
		response.setMetaData(modelJson);

		return response;
	}

	private void bindCommonInfo(Map<String, Object> model) {
		if (model != null) {
			String code = AssistantType.fromValue(this.getClass().getName()).getCode();
			model.put(ASSISTANT_NAME, this.getClass().getSimpleName());
			model.put(ASSISTANT_TITLE, getAssistantNameFromAssistantMap(code));
			model.put(ASSISTANT_TYPE, code);
		}
	}

	private String getAssistantNameFromAssistantMap(String code) {
		if (assistantNameList != null) {
			for (Map<String, Object> item : assistantNameList) {
				if (code.equals(String.valueOf(item.get("id")))) {
					return String.valueOf(item.get("assistant_name"));
				}
			}
		}
		return "";
	}

	protected static Assistant getAssistantByAssistantType(String assistantTypeName) {
		try {
			if (assistantCache.containsKey(assistantTypeName)) {
				return assistantCache.get(assistantTypeName);
			} else {
				Class<?> assistantType = Class.forName(assistantTypeName);
				Assistant assistant = (Assistant) assistantType.newInstance();
				synchronized (assistantCache) {
					if (!assistantCache.containsKey(assistantTypeName)) {
						assistantCache.put(assistantTypeName, assistant);
					}
				}
				return assistant;
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		return null;
	}

	protected static void setAssistantMap(List<Map<String, Object>> assistantList) {
		BaseAssistant.assistantNameList = assistantList;
	}

	protected static List<Map<String, Object>> getAssistantMap() {
		return BaseAssistant.assistantNameList;
	}

	protected Response invokeLocationMethod(BaseRequest request)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method method : this.getClass().getDeclaredMethods()) {
			ServiceLocation serviceLocation = method.getAnnotation(ServiceLocation.class);
			if (serviceLocation != null && serviceLocation.value().equals(request.getContent())) {
				return (Response) method.invoke(this, request);
			}
			BackServiceLocation backServiceLocation = method.getAnnotation(BackServiceLocation.class);
			if (backServiceLocation != null && backServiceLocation.value().equals(request.getContent())) {
				return (Response) method.invoke(this, request);
			}
		}
		return null;
	}
	
}
