package com.weihua.web.dao;

import java.util.Map;

public interface LifeMotoDao {
	Map<String, Object> findRandomRecord();

	int[] syncAllRecord(Object[][] params);
}
