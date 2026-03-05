package com.aa.carrepair.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aa.carrepair.BuildConfig
import com.aa.carrepair.R

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPersonaSelection: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onSignOut: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }

    // Sign-out confirmation dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text(stringResource(R.string.profile_sign_out)) },
            text = { Text(stringResource(R.string.profile_sign_out_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    viewModel.signOut { onSignOut() }
                }) {
                    Text(stringResource(R.string.profile_sign_out), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Delete all data confirmation dialog
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirmation() },
            title = { Text(stringResource(R.string.settings_data_delete)) },
            text = { Text(stringResource(R.string.settings_data_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAllData { onSignOut() }
                }) {
                    Text(stringResource(R.string.action_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteConfirmation() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = stringResource(R.string.settings_title),
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Account / Profile ────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_persona)) {
                uiState.userDisplayName?.let { name ->
                    SettingsInfoRow(
                        title = stringResource(R.string.profile_display_name),
                        value = name
                    )
                }
                uiState.userEmail?.let { email ->
                    SettingsInfoRow(
                        title = stringResource(R.string.profile_email),
                        value = email
                    )
                }
                uiState.selectedPersonaDisplayName?.let { displayName ->
                    SettingsInfoRow(
                        title = stringResource(R.string.settings_current_profile),
                        value = displayName
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(text = stringResource(R.string.profile_edit))
                    }
                    Button(
                        onClick = {
                            viewModel.resetPersona(onComplete = onNavigateToPersonaSelection)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(text = stringResource(R.string.settings_change_profile))
                    }
                }
            }

            // ── Appearance ───────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_theme)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeChip(
                        label = stringResource(R.string.settings_theme_light),
                        icon = Icons.Default.LightMode,
                        selected = uiState.theme == "light",
                        onClick = { viewModel.setTheme("light") },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeChip(
                        label = stringResource(R.string.settings_theme_dark),
                        icon = Icons.Default.DarkMode,
                        selected = uiState.theme == "dark",
                        onClick = { viewModel.setTheme("dark") },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeChip(
                        label = stringResource(R.string.settings_theme_system),
                        icon = Icons.Default.PhoneAndroid,
                        selected = uiState.theme == "system",
                        onClick = { viewModel.setTheme("system") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Units ────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_units)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.units == "imperial",
                        onClick = { viewModel.setUnits("imperial") },
                        label = { Text(stringResource(R.string.settings_units_imperial)) },
                        leadingIcon = if (uiState.units == "imperial") {
                            { Icon(Icons.Default.Straighten, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = uiState.units == "metric",
                        onClick = { viewModel.setUnits("metric") },
                        label = { Text(stringResource(R.string.settings_units_metric)) },
                        leadingIcon = if (uiState.units == "metric") {
                            { Icon(Icons.Default.Straighten, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Notifications ────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_notifications)) {
                SettingsToggleRow(
                    title = stringResource(R.string.settings_maintenance_alerts),
                    subtitle = "Get notified when maintenance is due",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = viewModel::setNotificationsEnabled
                )
            }

            // ── Privacy ──────────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_privacy)) {
                SettingsToggleRow(
                    title = stringResource(R.string.settings_privacy_mode),
                    subtitle = stringResource(R.string.settings_privacy_mode_desc),
                    checked = uiState.privacyModeEnabled,
                    onCheckedChange = viewModel::setPrivacyMode
                )
            }

            // ── Data Management ──────────────────────────────
            SettingsSection(title = "Data Management") {
                SettingsActionRow(
                    title = stringResource(R.string.settings_data_export),
                    subtitle = "Download a copy of your data",
                    onClick = { /* Export flow placeholder */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SettingsActionRow(
                    title = stringResource(R.string.settings_data_delete),
                    subtitle = "Permanently delete all app data",
                    isDestructive = true,
                    onClick = { viewModel.showDeleteConfirmation() }
                )
            }

            // ── About ────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_about)) {
                SettingsInfoRow(
                    title = stringResource(R.string.settings_version),
                    value = BuildConfig.VERSION_NAME
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Sign Out button ──────────────────────────────
            Button(
                onClick = { showSignOutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.profile_sign_out),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ThemeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        leadingIcon = {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        },
        modifier = modifier
    )
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    content()
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsInfoRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isDestructive) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
