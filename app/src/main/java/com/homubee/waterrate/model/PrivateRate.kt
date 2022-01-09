package com.homubee.waterrate.model

import java.io.Serializable

data class PrivateRate(val name: String, val lastMonthCount: Int, val PublicList: MutableList<PublicRate>) : Serializable
