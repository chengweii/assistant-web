package com.weihua.web.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;
import org.sqlite.SQLiteDataSource;

import com.google.common.base.Throwables;

public class WebDBHelper implements DBHelper {

	private static SQLiteDataSource dataSource;
	private static QueryRunner runner;

	public WebDBHelper(String webRootPath) {
		if (runner == null) {
			dataSource = new SQLiteDataSource();
			// dataSource.setUrl("jdbc:sqlite:" + webRootPath + "WEB-INF/classes/assistant.db");
			dataSource.setUrl("jdbc:sqlite:E:/assistant-server/database/assistant.db");
			runner = new QueryRunner(dataSource);
		}
	}

	@Override
	public Map<String, Object> queryMap(Logger logger, String sql, Object... params) {
		Map<String, Object> map = null;
		try {
			synchronized (runner) {
				map = runner.query(sql, new MapHandler(), params);
			}
		} catch (SQLException e) {
			Throwables.propagate(e);
		}
		return map;
	}

	@Override
	public List<Map<String, Object>> queryMapList(Logger logger, String sql, Object... params) {
		List<Map<String, Object>> mapList = null;
		try {
			synchronized (runner) {
				mapList = runner.query(sql, new MapListHandler(), params);
			}
		} catch (SQLException e) {
			Throwables.propagate(e);
		}
		return mapList;
	}

	@Override
	public int queryUpdate(Logger logger, String sql, Object... params) {
		int result = 0;
		try {
			synchronized (runner) {
				result = runner.update(sql, params);
			}
		} catch (SQLException e) {
			Throwables.propagate(e);
		}
		return result;
	}

	@Override
	public int[] queryBatch(Logger logger, String sql, Object[][] params) {
		int[] result = {};
		try {
			synchronized (runner) {
				result = runner.batch(sql, params);
			}
		} catch (SQLException e) {
			Throwables.propagate(e);
		}
		return result;
	}

}
