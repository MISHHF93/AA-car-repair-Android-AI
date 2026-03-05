package com.aa.carrepair.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.aa.carrepair.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AATopAppBar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    branded: Boolean = false,
    actions: @Composable () -> Unit = {}
) {
    val backDesc = stringResource(R.string.cd_back_button)
    val colors = if (branded) {
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        TopAppBarDefaults.topAppBarColors()
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = if (branded) FontWeight.Bold else FontWeight.Normal
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.semantics { contentDescription = backDesc }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = { actions() },
        colors = colors
    )
}
