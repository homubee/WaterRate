package com.homubee.waterrate.model

import java.io.Serializable

/**
 * 수도 요금 데이터 클래스
 *
 * 수도 요금 및 계산 데이터를 나타냄.
 *
 * @property type 공용(0)/개인(1)/개인-공용만 쓰는 경우(2) 타입
 * @property name 이름(고유 ID)
 * @property lastMonthCount 전월지침(0.5 단위)
 * @property waterRateList 공용 또는 개인 수도 이름 리스트
 */
data class WaterRate(val type: Int, val name: String, val lastMonthCount: Double, val waterRateList: MutableList<String>) : Serializable
