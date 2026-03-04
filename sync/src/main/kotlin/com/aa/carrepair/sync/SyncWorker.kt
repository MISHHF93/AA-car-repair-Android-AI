package com.aa.carrepair.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("SyncWorker starting background sync")
        return try {
            performSync()
            Timber.d("SyncWorker completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker failed")
            if (runAttemptCount < MAX_RETRY_COUNT) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun performSync() {
        // Sync operations: refresh cached data, upload pending changes, etc.
        Timber.d("Performing data sync operations")
    }

    companion object {
        const val MAX_RETRY_COUNT = 3
    }
}
