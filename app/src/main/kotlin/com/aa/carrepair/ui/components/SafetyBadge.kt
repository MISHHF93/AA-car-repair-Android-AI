package com.aa.carrepair.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.aa.carrepair.R
import com.aa.carrepair.domain.model.SafetyLevel

@Composable
fun SafetyBadge(
    level: SafetyLevel,
    modifier: Modifier = Modifier
) {
    val (color, textResId) = when (level) {
        SafetyLevel.CRITICAL -> Color(0xFFD32F2F) to R.string.safety_critical
        SafetyLevel.HIGH -> Color(0xFFF57C00) to R.string.safety_high
        SafetyLevel.MEDIUM -> Color(0xFFFBC02D) to R.string.safety_medium
        SafetyLevel.LOW -> Color(0xFF388E3C) to R.string.safety_low
    }
    val text = stringResource(textResId)
    val cdText = stringResource(R.string.cd_safety_badge)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .semantics { contentDescription = "$cdText: $text" }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
