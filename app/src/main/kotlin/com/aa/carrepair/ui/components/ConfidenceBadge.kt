package com.aa.carrepair.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.aa.carrepair.R

@Composable
fun ConfidenceBadge(
    confidence: Int,
    modifier: Modifier = Modifier
) {
    val color = when {
        confidence >= 80 -> Color(0xFF388E3C)
        confidence >= 60 -> Color(0xFFFBC02D)
        else -> Color(0xFFF57C00)
    }
    val icon = if (confidence >= 60) Icons.Default.CheckCircle else Icons.Default.Warning
    val text = stringResource(R.string.chat_confidence, confidence)
    val cdText = stringResource(R.string.cd_confidence_badge)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.semantics { contentDescription = "$cdText: $text" }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}
