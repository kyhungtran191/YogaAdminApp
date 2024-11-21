package com.example.yogaadminapp.models

 data class Course(
    val id: String,
    val name:String,
    val dayOfWeek: String,
    val startTime: String,
    val duration: Int,
    val price: Double,
    val type: String,
    val capacity: Int,
    val description: String?
)