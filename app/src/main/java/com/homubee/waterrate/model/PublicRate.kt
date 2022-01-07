package com.homubee.waterrate.model

/*
 공용 수도 요금 데이터 클래스
 이름과 전월지침을 멤버변수로 가짐
 */
data class PublicRate(val name: String, val lastMonthCount: Int) {}
