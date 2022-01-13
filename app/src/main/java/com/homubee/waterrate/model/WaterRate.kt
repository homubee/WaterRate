package com.homubee.waterrate.model

import java.io.Serializable

/*
 수도 요금 데이터 클래스
 공용(0)/개인(1) 타입, 이름과 전월지침, 공용 또는 개인 수도 이름 리스트를 멤버변수로 가짐
 */
data class WaterRate(val type: Int, val name: String, val lastMonthCount: Int, val waterRateList: MutableList<String>) : Serializable
