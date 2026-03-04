package com.aa.carrepair.data.repository

import android.content.Context
import android.net.Uri
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.result.safeApiCall
import com.aa.carrepair.data.remote.api.InspectionApi
import com.aa.carrepair.domain.model.BoundingBox
import com.aa.carrepair.domain.model.FindingSeverity
import com.aa.carrepair.domain.model.InspectionFinding
import com.aa.carrepair.domain.model.InspectionMode
import com.aa.carrepair.domain.model.InspectionResult
import com.aa.carrepair.domain.repository.InspectionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class InspectionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val inspectionApi: InspectionApi
) : InspectionRepository {

    override suspend fun analyzeImage(imageUri: Uri, mode: InspectionMode): DataResult<InspectionResult> =
        safeApiCall {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw IllegalArgumentException("Cannot open image URI")
            val tempFile = File.createTempFile("inspection", ".jpg", context.cacheDir)
            tempFile.outputStream().use { out -> inputStream.copyTo(out) }

            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
            val modePart = mode.name.lowercase().toRequestBody("text/plain".toMediaType())

            val response = inspectionApi.analyzeImage(imagePart, modePart, null)

            InspectionResult(
                id = response.inspectionId,
                mode = mode,
                findings = response.findings.map { finding ->
                    InspectionFinding(
                        type = finding.type,
                        description = finding.description,
                        severity = runCatching {
                            FindingSeverity.valueOf(finding.severity.uppercase())
                        }.getOrDefault(FindingSeverity.INFO),
                        confidence = finding.confidence,
                        boundingBox = finding.boundingBox?.let {
                            BoundingBox(it.left, it.top, it.right, it.bottom)
                        }
                    )
                },
                severityScore = response.severityScore,
                summary = response.summary,
                recommendations = response.recommendations,
                createdAt = Instant.now()
            )
        }

    override fun getInspectionHistory(): Flow<List<InspectionResult>> = flowOf(emptyList())

    override suspend fun saveInspectionResult(result: InspectionResult): DataResult<Unit> =
        DataResult.Success(Unit)
}
