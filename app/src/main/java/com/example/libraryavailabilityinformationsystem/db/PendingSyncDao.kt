package com.example.libraryavailabilityinformationsystem.db

import androidx.room.*

@Dao
interface PendingSyncDao {
    @Query("SELECT * FROM pending_sync WHERE status = 'PENDING' ORDER BY timestamp ASC")
    fun getPendingSyncOperations(): List<PendingSyncEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPendingSync(syncItem: PendingSyncEntity)

    @Query("UPDATE pending_sync SET status = :status WHERE syncId = :syncId")
    fun updateSyncStatus(syncId: String, status: String)

    @Query("DELETE FROM pending_sync WHERE syncId = :syncId")
    fun deleteSyncOperation(syncId: String)
}
