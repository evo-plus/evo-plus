package ru.dargen.evoplus.service.user.model

data class UserStatisticModel(
    var level: Int,
    var blocks: Int,
    var balance: Double,
    var shards: Double,
    var location: String,

    var statistic: String,
)