package com.weihua.web.service;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.weihua.web.entity.alarm.AlarmInfo;
import com.weihua.web.entity.request.BaseRequest;
import com.weihua.web.entity.request.Request;
import com.weihua.web.entity.response.Response;
import com.weihua.web.service.annotation.BackServiceLocation;
import com.weihua.web.service.base.BaseAssistant;

/**
 * @author chengwei2
 * @category Health advice;Common diseases;Food therapy;Mental health;Physical
 *           exercise;
 */
public class HealthyAssistant extends BaseAssistant {
	private static Logger LOGGER = Logger.getLogger(HealthyAssistant.class);

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getRestSuggestion((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		return response;
	}

	@BackServiceLocation(value = "getRestSuggestion")
	public Response getRestSuggestion(BaseRequest request) {
		if (isTriggerServiceReminding(request)) {
			AlarmInfo alarmInfo = new AlarmInfo();
			alarmInfo.setTitle("please have a rest");
			alarmInfo.setContent("you can get down floors");
			alarmInfo.setImage("http://icon.nipic.com/BannerPic/20170815/original/20170815181219_1.jpg");
			return responseJson(alarmInfo);
		}
		return null;
	}

}
