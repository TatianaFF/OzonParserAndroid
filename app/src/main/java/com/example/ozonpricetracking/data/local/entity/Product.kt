package com.example.ozonpricetracking.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product",
    indices = [Index(value = ["sku"], unique = true)]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val sku: String,
    val title: String,
    val image: String,
    val createdAt: Long = System.currentTimeMillis()
)