package com.weihua.web.service;

import com.weihua.web.entity.request.Request;
import com.weihua.web.entity.response.Response;

public interface Assistant {
	Response getResponse(Request request);
}
