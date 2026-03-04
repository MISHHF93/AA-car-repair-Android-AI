package com.aa.carrepair.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aa.carrepair.R
import com.aa.carrepair.domain.model.UserPersona

private data class PersonaOption(
    val persona: UserPersona,
    val icon: ImageVector,
    val titleRes: Int,
    val descriptionRes: Int
)

private val PERSONA_OPTIONS = listOf(
    PersonaOption(
        persona = UserPersona.DIY_OWNER,
        icon = Icons.Default.Person,
        titleRes = R.string.persona_diy_owner,
        descriptionRes = R.string.persona_diy_owner_desc
    ),
    PersonaOption(
        persona = UserPersona.PROFESSIONAL_TECHNICIAN,
        icon = Icons.Default.Build,
        titleRes = R.string.persona_professional_tech,
        descriptionRes = R.string.persona_professional_tech_desc
    ),
    PersonaOption(
        persona = UserPersona.FLEET_MANAGER,
        icon = Icons.Default.DirectionsCar,
        titleRes = R.string.persona_fleet_manager,
        descriptionRes = R.string.persona_fleet_manager_desc
    )
)

/**
 * Persona-selection screen displayed after the Sign-In welcome screen. Users choose their role
 * (DIY Owner, Professional Technician, or Fleet Manager) to personalise the experience.
 * The selection is persisted and onboarding is marked complete before navigating to the dashboard.
 */
@Composable
fun PersonaSelectionScreen(
    onPersonaSelected: () -> Unit,
    viewModel: PersonaSelectionViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = stringResource(R.string.persona_selection_title)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.persona_selection_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(PERSONA_OPTIONS) { option ->
                PersonaCard(
                    option = option,
                    onClick = {
                        viewModel.selectPersona(option.persona, onComplete = onPersonaSelected)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonaCard(
    option: PersonaOption,
    onClick: () -> Unit
) {
    val title = stringResource(option.titleRes)
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "$title persona" },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(option.descriptionRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                )
            }
        }
    }
}
