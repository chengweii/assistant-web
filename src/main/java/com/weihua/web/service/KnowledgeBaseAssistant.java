package com.weihua.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.weihua.web.dao.KnowledgeListDao;
import com.weihua.web.dao.impl.KnowledgeListDaoImpl;
import com.weihua.web.entity.request.BaseRequest;
import com.weihua.web.entity.request.Request;
import com.weihua.web.entity.response.Response;
import com.weihua.web.service.annotation.ServiceLocation;
import com.weihua.web.service.base.BaseAssistant;

/**
 * @author chengwei2
 * @category Knowledge base service
 */
public class KnowledgeBaseAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(KnowledgeBaseAssistant.class);

	private static KnowledgeListDao knowledgeListDao = new KnowledgeListDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getKnowledgeBase((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		return response;
	}

	@ServiceLocation(value = "getKnowledgeBase")
	public Response getKnowledgeBase(BaseRequest request) {
		
		Map<String, Object> model = new HashMap<String, Object>();
		List<Map<String, Object>> knowledgeList = knowledgeListDao.findRecordList();
		model.put("knowledgeList", knowledgeList);
		return response(model, "knowledgebase/index");
	}
}
