package com.example.libraryavailabilityinformationsystem.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_sync")
data class PendingSyncEntity(
    @PrimaryKey
    val syncId: String,
    val entityType: String, // "BOOK", "USER"
    val entityId: String,
    val operation: String,  // "INSERT", "UPDATE", "DELETE"
    val payloadJson: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING"
)
