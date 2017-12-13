package com.weihua.web.util;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public interface DBHelper {
	Map<String, Object> queryMap(Logger logger, String sql, Object... params);

	List<Map<String, Object>> queryMapList(Logger logger, String sql, Object... params);

	int queryUpdate(Logger logger, String sql, Object... params);

	int[] queryBatch(Logger logger, String sql, Object[][] params);
}
