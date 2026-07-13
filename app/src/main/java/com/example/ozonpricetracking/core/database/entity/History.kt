package com.example.ozonpricetracking.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["productId"])]
)
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val price: Int,
    val createdAt: Long = System.currentTimeMillis()
)