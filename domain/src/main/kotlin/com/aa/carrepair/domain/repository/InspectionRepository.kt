package com.aa.carrepair.domain.repository

import android.net.Uri
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.InspectionMode
import com.aa.carrepair.domain.model.InspectionResult
import kotlinx.coroutines.flow.Flow

interface InspectionRepository {
    suspend fun analyzeImage(imageUri: Uri, mode: InspectionMode): DataResult<InspectionResult>
    fun getInspectionHistory(): Flow<List<InspectionResult>>
    suspend fun saveInspectionResult(result: InspectionResult): DataResult<Unit>
}
