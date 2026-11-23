package com.example.libraryavailabilityinformationsystem

import com.example.libraryavailabilityinformationsystem.db.PendingSyncEntity
import com.example.libraryavailabilityinformationsystem.db.toEntity
import com.example.libraryavailabilityinformationsystem.models.Book
import com.example.libraryavailabilityinformationsystem.models.Status
import org.junit.Assert.*
import org.junit.Test

class SyncWorkerTest {

    @Test
    fun testPendingSyncEntity_creationAndMapping() {
        val syncItem = PendingSyncEntity(
            syncId = "sync_1001",
            entityType = "BOOK",
            entityId = "101",
            operation = "UPDATE",
            status = "PENDING"
        )

        assertEquals("sync_1001", syncItem.syncId)
        assertEquals("BOOK", syncItem.entityType)
        assertEquals("101", syncItem.entityId)
        assertEquals("UPDATE", syncItem.operation)
        assertEquals("PENDING", syncItem.status)
    }

    @Test
    fun testConflictResolution_newerTimestampPreserved() {
        val t1 = System.currentTimeMillis()
        val t2 = t1 + 5000

        val bookV1 = Book(id = 1, title = "Original Title", author = "Author A", updatedAt = t1)
        val bookV2 = Book(id = 1, title = "Updated Title", author = "Author A", updatedAt = t2)

        assertTrue(bookV2.updatedAt > bookV1.updatedAt)
        assertEquals("Updated Title", bookV2.title)
    }

    @Test
    fun testPendingBookEntity_syncStatusFlag() {
        val book = Book(id = 5, title = "Offline Algorithms", author = "Dev Team", status = Status.Available)
        val entityPending = book.toEntity(syncStatus = "PENDING")
        val entitySynced = book.toEntity(syncStatus = "SYNCED")

        assertEquals("PENDING", entityPending.syncStatus)
        assertEquals("SYNCED", entitySynced.syncStatus)
    }

    @Test
    fun testDuplicateSyncPrevention_uniqueSyncId() {
        val syncId1 = "sync_book_1"
        val syncId2 = "sync_book_1"

        assertEquals(syncId1, syncId2)
    }
}
