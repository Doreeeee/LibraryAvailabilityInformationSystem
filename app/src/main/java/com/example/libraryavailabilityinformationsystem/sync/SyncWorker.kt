package com.example.libraryavailabilityinformationsystem.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.libraryavailabilityinformationsystem.db.LaisDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "SyncWorker started execution. Checking pending operations in Room database...")
        val database = LaisDatabase.getInstance(applicationContext)
        val pendingDao = database.pendingSyncDao()
        val bookDao = database.bookDao()
        val userDao = database.userDao()

        val pendingList = try {
            pendingDao.getPendingSyncOperations()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching pending sync operations from Room: ${e.message}")
            return Result.failure()
        }

        if (pendingList.isEmpty()) {
            Log.d(TAG, "No pending operations found. SyncWorker finished successfully.")
            return Result.success()
        }

        val firestore = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Cloud Firestore unavailable for sync: ${e.message}")
            return Result.retry()
        }

        for (item in pendingList) {
            try {
                when (item.entityType.uppercase()) {
                    "BOOK" -> {
                        val bookId = item.entityId.toIntOrNull() ?: continue
                        val bookEntity = bookDao.getBookById(bookId)
                        if (bookEntity != null) {
                            val bookMap = hashMapOf(
                                "id" to bookEntity.id,
                                "bookId" to bookEntity.id,
                                "title" to bookEntity.title,
                                "author" to bookEntity.author,
                                "year" to bookEntity.year,
                                "category" to bookEntity.category,
                                "status" to bookEntity.status,
                                "copies" to bookEntity.copies,
                                "availableCopies" to bookEntity.copies,
                                "total" to bookEntity.total,
                                "totalCopies" to bookEntity.total,
                                "numberOfCopies" to bookEntity.total,
                                "returnDate" to bookEntity.returnDate,
                                "expectedReturnDate" to bookEntity.returnDate,
                                "isOutdated" to bookEntity.isOutdated,
                                "description" to bookEntity.description,
                                "createdBy" to bookEntity.createdBy,
                                "createdAt" to bookEntity.createdAt,
                                "updatedAt" to bookEntity.updatedAt,
                                "isActive" to bookEntity.isActive
                            )
                            firestore.collection("books")
                                .document(bookEntity.id.toString())
                                .set(bookMap, SetOptions.merge())
                                .await()

                            bookDao.insertBook(bookEntity.copy(syncStatus = "SYNCED"))
                            pendingDao.deleteSyncOperation(item.syncId)
                            Log.d(TAG, "Successfully synced book #${bookEntity.id} '${bookEntity.title}' to Cloud Firestore.")
                        } else {
                            pendingDao.deleteSyncOperation(item.syncId)
                        }
                    }
                    "USER" -> {
                        val userId = item.entityId.toIntOrNull() ?: continue
                        val userEntity = userDao.getUserById(userId)
                        if (userEntity != null) {
                            val docId = if (!userEntity.uid.isNullOrEmpty()) userEntity.uid else userEntity.email.replace(".", "_")
                            val userMap = hashMapOf(
                                "uid" to docId,
                                "id" to userEntity.id,
                                "userId" to userEntity.id,
                                "name" to userEntity.name,
                                "fullName" to userEntity.name,
                                "email" to userEntity.email,
                                "username" to userEntity.username,
                                "role" to userEntity.role.lowercase(),
                                "studentId" to userEntity.studentId,
                                "department" to userEntity.department,
                                "phone" to userEntity.phone,
                                "isActive" to userEntity.isActive,
                                "createdAt" to userEntity.createdAt,
                                "updatedAt" to System.currentTimeMillis()
                            )
                            firestore.collection("users")
                                .document(docId)
                                .set(userMap, SetOptions.merge())
                                .await()

                            userDao.insertUser(userEntity.copy(syncStatus = "SYNCED"))
                            pendingDao.deleteSyncOperation(item.syncId)
                            Log.d(TAG, "Successfully synced user #${userEntity.id} '${userEntity.name}' to Cloud Firestore.")
                        } else {
                            pendingDao.deleteSyncOperation(item.syncId)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Sync attempt failed for operation ${item.syncId}: ${e.message}")
                pendingDao.updateSyncStatus(item.syncId, "FAILED")
                return Result.retry()
            }
        }

        return Result.success()
    }

    companion object {
        private const val TAG = "SyncWorker"
        private const val UNIQUE_WORK_NAME = "LaisBackgroundSyncWorker"

        fun scheduleNetworkSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                syncWorkRequest
            )
            Log.d(TAG, "Scheduled unique SyncWorker with NetworkType.CONNECTED constraint.")
        }
    }
}
