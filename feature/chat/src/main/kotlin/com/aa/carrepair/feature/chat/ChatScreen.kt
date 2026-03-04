package com.aa.carrepair.feature.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aa.carrepair.domain.model.MessageRole
import com.aa.carrepair.feature.chat.R

@Composable
fun ChatScreen(
    sessionId: String,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(sessionId) {
        viewModel.initSession(sessionId)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = stringResource(R.string.chat_title),
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(uiState.messages) { message ->
                    com.aa.carrepair.ui.components.ChatBubble(
                        content = message.content,
                        role = message.role,
                        timestamp = message.timestamp,
                        confidence = message.confidence
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (uiState.isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            uiState.error?.let { error ->
                com.aa.carrepair.ui.components.ErrorCard(
                    message = error,
                    onRetry = { viewModel.clearError() },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            ChatInputBar(
                inputText = uiState.inputText,
                onInputChanged = viewModel::onInputChanged,
                onSend = { viewModel.sendMessage() },
                enabled = !uiState.isTyping,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val sendDesc = stringResource(R.string.cd_send_button)
    val hintText = stringResource(R.string.chat_input_hint)

    Box(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChanged,
            placeholder = { Text(hintText) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = onSend,
                    enabled = enabled && inputText.isNotBlank(),
                    modifier = Modifier.semantics { contentDescription = sendDesc }
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                }
            },
            maxLines = 4
        )
    }
}

@Composable
private fun TypingIndicator() {
    Text(
        text = stringResource(R.string.chat_typing_indicator),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}
