package com.example.readwritecalls.data

import java.util.Date

data class CallLogItem(
    val name: String,
    val number: String,
    val type: Int,
    val date: Date,
    val duration: String
)