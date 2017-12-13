package com.weihua.web.dao;

import java.util.Map;

import com.weihua.web.constant.FoodType;

public interface FoodListDao {
	Map<String, Object> findRandomRecord(FoodType foodType);
}
