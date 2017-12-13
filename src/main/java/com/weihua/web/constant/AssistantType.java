package com.weihua.web.constant;

import com.weihua.web.service.BingeWatchingAssistant;
import com.weihua.web.service.ConsultationAssistant;
import com.weihua.web.service.DietAssistant;
import com.weihua.web.service.FamilyAssistant;
import com.weihua.web.service.FinancingAssistant;
import com.weihua.web.service.GoalAssistant;
import com.weihua.web.service.HealthyAssistant;
import com.weihua.web.service.InterviewAssistant;
import com.weihua.web.service.KnowledgeBaseAssistant;
import com.weihua.web.service.MainAssistant;
import com.weihua.web.service.ParentingAssistant;
import com.weihua.web.service.ScheduleAssistant;
import com.weihua.web.service.TravelAssistant;
import com.weihua.web.service.WeatherAssistant;
import com.weihua.web.service.WorkAssistant;

public enum AssistantType {

	MAIN_ASSISTANT("111", MainAssistant.class.getName()),
	GOAL_ASSISTANT("112", GoalAssistant.class.getName()),
	FAMILY_ASSISTANT("113", FamilyAssistant.class.getName()),
	WEATHER_ASSISTANT("114", WeatherAssistant.class.getName()),
	TRAVEL_ASSISTANT("115", TravelAssistant.class.getName()),
	PARENTING_ASSISTANT("116", ParentingAssistant.class.getName()),
	CONSULTATION_ASSISTANT("117", ConsultationAssistant.class.getName()),
	FINANCING_ASSISTANT("118", FinancingAssistant.class.getName()),
	INTERVIEW_ASSISTANT("119", InterviewAssistant.class.getName()),
	BINGEWATCHING_ASSISTANT("120", BingeWatchingAssistant.class.getName()),
	HEALTHY_ASSISTANT("121", HealthyAssistant.class.getName()),
	DIET_ASSISTANT("122", DietAssistant.class.getName()),
	SCHEDULE_ASSISTANT("123", ScheduleAssistant.class.getName()),
	WORK_ASSISTANT("124", WorkAssistant.class.getName()),
	KNOWLEDGEBASE_ASSISTANT("125", KnowledgeBaseAssistant.class.getName());

	private AssistantType(String code, String value) {
		this.code = code;
		this.value = value;
	}

	private String code;
	private String value;

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public static AssistantType fromCode(String code) {
		for (AssistantType entity : AssistantType.values()) {
			if (entity.getCode().equals(code)) {
				return entity;
			}
		}
		return MAIN_ASSISTANT;
	}
	
	public static AssistantType fromValue(String value) {
		for (AssistantType entity : AssistantType.values()) {
			if (entity.getValue().equals(value)) {
				return entity;
			}
		}
		return MAIN_ASSISTANT;
	}
}
