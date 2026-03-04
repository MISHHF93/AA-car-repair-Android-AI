package com.aa.carrepair.domain.usecase.safety

import com.aa.carrepair.domain.model.SafetyClassification
import com.aa.carrepair.domain.safety.SafetyClassifier
import javax.inject.Inject

class ClassifySafetyUseCase @Inject constructor(
    private val safetyClassifier: SafetyClassifier
) {
    operator fun invoke(content: String, dtcCodes: List<String> = emptyList()): SafetyClassification =
        safetyClassifier.classify(content, dtcCodes)
}
