package com.weihua.web.dao.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.web.constant.FoodType;
import com.weihua.web.dao.FoodListDao;

public class FoodListDaoImpl extends BaseDao implements FoodListDao {

	private static Logger LOGGER = Logger.getLogger(FoodListDaoImpl.class);

	@Override
	public Map<String, Object> findRandomRecord(FoodType foodType) {
		String sql = "select * from food_list where food_type = ? order by random() limit 1";
		Map<String, Object> result = dBHelper.queryMap(LOGGER, sql, foodType.getCode());
		return result;
	}

}
