package com.runanywhere.runanywhereai.presentation.disaster.translation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 🗣 Offline Communication Assistant
 * Translate speech/text between local languages for disaster response
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationScreen(
    viewModel: TranslationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val translations by viewModel.translations.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new translations appear
    LaunchedEffect(translations.size) {
        if (translations.isNotEmpty()) {
            listState.animateScrollToItem(translations.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("🗣 Translation Assistant")
                        Text(
                            "Offline Multilingual Communication",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearTranslations() }) {
                        Icon(Icons.Default.Delete, "Clear history")
                    }
                }
            )
        },
        snackbarHost = {
            // Show error messages
            uiState.error?.let { errorMessage ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.updateInputText(uiState.inputText) }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(errorMessage)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Language Selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Select Languages",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Source Language
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("From:", modifier = Modifier.width(60.dp))
                        LanguageDropdown(
                            selectedLanguage = uiState.sourceLanguage,
                            onLanguageSelected = { viewModel.setSourceLanguage(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Swap Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { viewModel.swapLanguages() }) {
                            Icon(Icons.Default.SwapVert, "Swap languages")
                        }
                    }

                    // Target Language
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("To:", modifier = Modifier.width(60.dp))
                        LanguageDropdown(
                            selectedLanguage = uiState.targetLanguage,
                            onLanguageSelected = { viewModel.setTargetLanguage(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Translation History
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (translations.isEmpty()) {
                    item {
                        EmptyStateMessage()
                    }
                } else {
                    items(translations) { translation ->
                        TranslationCard(translation)
                    }
                }
            }

            // Input Section
            InputSection(
                inputText = uiState.inputText,
                onInputChanged = { viewModel.updateInputText(it) },
                isLoading = uiState.isTranslating,
                onTranslate = { viewModel.translate() },
                onVoiceInput = { viewModel.startVoiceInput() }
            )
        }
    }
}

@Composable
fun LanguageDropdown(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedLanguage.displayName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Language.commonLanguages.forEach { language ->
                DropdownMenuItem(
                    text = { Text("${language.flag} ${language.displayName}") },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TranslationCard(translation: Translation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Original Text
            Column {
                Text(
                    "${translation.sourceLanguage.flag} ${translation.sourceLanguage.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    translation.originalText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Divider()

            // Translated Text
            Column {
                Text(
                    "${translation.targetLanguage.flag} ${translation.targetLanguage.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    translation.translatedText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Timestamp
            Text(
                translation.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InputSection(
    inputText: String,
    onInputChanged: (String) -> Unit,
    isLoading: Boolean,
    onTranslate: () -> Unit,
    onVoiceInput: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter text to translate...") },
                minLines = 2,
                maxLines = 4,
                enabled = !isLoading
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Voice Input Button
                OutlinedButton(
                    onClick = onVoiceInput,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Mic, "Voice input", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Voice")
                }

                // Translate Button
                Button(
                    onClick = onTranslate,
                    modifier = Modifier.weight(2f),
                    enabled = !isLoading && inputText.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Translate, null, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Translate")
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.Translate,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "No translations yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Enter text or use voice input to translate between languages offline",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Data classes
data class Language(
    val code: String,
    val displayName: String,
    val flag: String
) {
    companion object {
        val commonLanguages = listOf(
            // Most Widely Spoken Languages (Top 25)
            Language("en", "English", "🇬🇧"),
            Language("zh", "Chinese (Mandarin)", "🇨🇳"),
            Language("hi", "Hindi", "🇮🇳"),
            Language("es", "Spanish", "🇪🇸"),
            Language("fr", "French", "🇫🇷"),
            Language("ar", "Arabic", "🇸🇦"),
            Language("bn", "Bengali", "🇧🇩"),
            Language("pt", "Portuguese", "🇵🇹"),
            Language("ru", "Russian", "🇷🇺"),
            Language("ja", "Japanese", "🇯🇵"),
            Language("pa", "Punjabi", "🇮🇳"),
            Language("de", "German", "🇩🇪"),
            Language("jv", "Javanese", "🇮🇩"),
            Language("ko", "Korean", "🇰🇷"),
            Language("vi", "Vietnamese", "🇻🇳"),
            Language("te", "Telugu", "🇮🇳"),
            Language("mr", "Marathi", "🇮🇳"),
            Language("ta", "Tamil", "🇮🇳"),
            Language("tr", "Turkish", "🇹🇷"),
            Language("ur", "Urdu", "🇵🇰"),
            Language("gu", "Gujarati", "🇮🇳"),
            Language("pl", "Polish", "🇵🇱"),
            Language("uk", "Ukrainian", "🇺🇦"),
            Language("ml", "Malayalam", "🇮🇳"),
            Language("kn", "Kannada", "🇮🇳"),
            
            // European Languages
            Language("it", "Italian", "🇮🇹"),
            Language("ro", "Romanian", "🇷🇴"),
            Language("nl", "Dutch", "🇳🇱"),
            Language("el", "Greek", "🇬🇷"),
            Language("cs", "Czech", "🇨🇿"),
            Language("sv", "Swedish", "🇸🇪"),
            Language("hu", "Hungarian", "🇭🇺"),
            Language("bg", "Bulgarian", "🇧🇬"),
            Language("da", "Danish", "🇩🇰"),
            Language("fi", "Finnish", "🇫🇮"),
            Language("no", "Norwegian", "🇳🇴"),
            Language("sk", "Slovak", "🇸🇰"),
            Language("hr", "Croatian", "🇭🇷"),
            Language("lt", "Lithuanian", "🇱🇹"),
            Language("sl", "Slovenian", "🇸🇮"),
            Language("et", "Estonian", "🇪🇪"),
            Language("lv", "Latvian", "🇱🇻"),
            
            // Asian Languages
            Language("th", "Thai", "🇹🇭"),
            Language("my", "Burmese", "🇲🇲"),
            Language("km", "Khmer", "🇰🇭"),
            Language("lo", "Lao", "🇱🇦"),
            Language("si", "Sinhala", "🇱🇰"),
            Language("ne", "Nepali", "🇳🇵"),
            Language("id", "Indonesian", "🇮🇩"),
            Language("ms", "Malay", "🇲🇾"),
            Language("tl", "Tagalog (Filipino)", "🇵🇭"),
            Language("mn", "Mongolian", "🇲🇳"),
            
            // Middle Eastern Languages
            Language("fa", "Persian (Farsi)", "🇮🇷"),
            Language("he", "Hebrew", "🇮🇱"),
            Language("ku", "Kurdish", "🇮🇶"),
            Language("az", "Azerbaijani", "🇦🇿"),
            Language("kk", "Kazakh", "🇰🇿"),
            Language("uz", "Uzbek", "🇺🇿"),
            
            // African Languages
            Language("sw", "Swahili", "🇰🇪"),
            Language("am", "Amharic", "🇪🇹"),
            Language("ha", "Hausa", "🇳🇬"),
            Language("yo", "Yoruba", "🇳🇬"),
            Language("ig", "Igbo", "🇳🇬"),
            Language("zu", "Zulu", "🇿🇦"),
            Language("af", "Afrikaans", "🇿🇦"),
            Language("so", "Somali", "🇸🇴"),
            
            // Latin American Languages
            Language("pt-BR", "Portuguese (Brazil)", "🇧🇷"),
            Language("es-MX", "Spanish (Mexico)", "🇲🇽"),
            Language("es-AR", "Spanish (Argentina)", "🇦🇷"),
            Language("qu", "Quechua", "🇵🇪"),
            Language("gn", "Guaraní", "🇵🇾"),
            
            // Other Regional Languages
            Language("sq", "Albanian", "🇦🇱"),
            Language("hy", "Armenian", "🇦🇲"),
            Language("ka", "Georgian", "🇬🇪"),
            Language("mk", "Macedonian", "🇲🇰"),
            Language("sr", "Serbian", "🇷🇸"),
            Language("bs", "Bosnian", "🇧🇦"),
            Language("is", "Icelandic", "🇮🇸"),
            Language("mt", "Maltese", "🇲🇹"),
            
            // Southeast Asian Languages
            Language("ceb", "Cebuano", "🇵🇭"),
            Language("hmn", "Hmong", "🇱🇦"),
            Language("tg", "Tajik", "🇹🇯"),
            Language("tk", "Turkmen", "🇹🇲"),
            Language("ky", "Kyrgyz", "🇰🇬"),
            
            // Pacific Languages
            Language("mi", "Maori", "🇳🇿"),
            Language("sm", "Samoan", "🇼🇸"),
            Language("to", "Tongan", "🇹🇴"),
            Language("haw", "Hawaiian", "🇺🇸"),
            
            // Caribbean/Creole Languages
            Language("ht", "Haitian Creole", "🇭🇹"),
            
            // Additional Asian Scripts
            Language("zh-TW", "Chinese (Traditional)", "🇹🇼"),
            Language("zh-CN", "Chinese (Simplified)", "🇨🇳"),
            Language("ja-JP", "Japanese (Japan)", "🇯🇵"),
            Language("ko-KR", "Korean (Korea)", "🇰🇷")
        ).sortedBy { it.displayName }
    }
}

data class Translation(
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val timestamp: String
)
