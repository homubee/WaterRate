package com.homubee.waterrate.model

import java.io.Serializable

/*
 개인 수도 요금 데이터 클래스
 이름과 전월지침, 공용 수도 이름 리스트를 멤버변수로 가짐
 */
data class PrivateRate(val name: String, val lastMonthCount: Int, val publicList: MutableList<String>) : Serializable
