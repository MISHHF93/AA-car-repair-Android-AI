package com.aa.carrepair.domain.usecase.inspection

import android.net.Uri
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.InspectionMode
import com.aa.carrepair.domain.model.InspectionResult
import com.aa.carrepair.domain.repository.InspectionRepository
import javax.inject.Inject

class AnalyzeImageUseCase @Inject constructor(
    private val inspectionRepository: InspectionRepository
) {
    suspend operator fun invoke(
        imageUri: Uri,
        mode: InspectionMode
    ): DataResult<InspectionResult> =
        inspectionRepository.analyzeImage(imageUri, mode)
}
