package com.weihua.web.dao.impl;

import com.weihua.web.util.DBHelper;

public class BaseDao {
	protected static DBHelper dBHelper;

	public static void init(DBHelper dBHelper) {
		BaseDao.dBHelper = dBHelper;
	}
}
