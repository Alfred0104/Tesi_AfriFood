package com.example.afrifoodadmin.model

data class AddFoodModel(
    val foodName: String?="",
    val foodDescription: String?="",
    val foodCoverImg: String?="",
    val foodCategory: String?="",
    val foodId: String?="",
    val foodSp: String?="",
    val foodImage: ArrayList<String>
)
