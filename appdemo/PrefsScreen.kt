package auto.prefs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    initialUsername: String,
    initialLoginCount: Int,
    initialIsFirstRun: Boolean,
    initialUserProfile: UserProfile,
    initialLastLoginTime: String,
    onSavePreferences: (
        username: String,
        loginCount: Int,
        isFirstRun: Boolean,
        userProfile: UserProfile,
        lastLoginTime: String
    ) -> Unit
) {
    var userNameState by remember { mutableStateOf(initialUsername) }
    var loginCountState by remember { mutableIntStateOf(initialLoginCount) }
    var isFirstRunState by remember { mutableStateOf(initialIsFirstRun) }
    var profileNameState by remember { mutableStateOf(initialUserProfile.name) }
    var profileAgeState by remember { mutableStateOf(initialUserProfile.age.toString()) }
    var profileIsPremiumState by remember { mutableStateOf(initialUserProfile.isPremium) }
    var lastLoginState by remember { mutableStateOf(formatInstant(initialLastLoginTime)) }
    var showSavedMessage by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(showSavedMessage) {
        if (showSavedMessage) {
            delay(2000)
            showSavedMessage = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AutoPrefs Demo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize().verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "SharedPreferences Demo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Changes are automatically saved to SharedPreferences. " +
                                "Close and reopen the app to see persistence in action!",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (showSavedMessage) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "✓ Preferences saved!",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            SectionTitle("Username")
            OutlinedTextField(
                value = userNameState,
                onValueChange = { userNameState = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            SectionTitle("Login Count")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = {
                    if (loginCountState > 0) loginCountState--
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Decrease")
                }

                Text(
                    text = loginCountState.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                OutlinedButton(onClick = {
                    loginCountState++
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }

            SectionTitle("First Run Flag")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Is First Run",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = isFirstRunState,
                    onCheckedChange = { isFirstRunState = it }
                )
            }

            SectionTitle("User Profile")

            OutlinedTextField(
                value = profileNameState,
                onValueChange = { profileNameState = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Profile Name") },
                leadingIcon = { Icon(Icons.Default.Face, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = profileAgeState,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        profileAgeState = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Age") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Premium Account",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = profileIsPremiumState,
                    onCheckedChange = { profileIsPremiumState = it }
                )
            }

            SectionTitle("Last Login")
            Text(
                text = lastLoginState,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        // Updates all preferences
                        val now = Instant.now().toString()
                        onSavePreferences(
                            userNameState,
                            loginCountState,
                            isFirstRunState,
                            UserProfile(
                                profileNameState,
                                profileAgeState.toIntOrNull() ?: 0,
                                profileIsPremiumState
                            ),
                            now
                        )
                        lastLoginState = formatInstant(now)
                        showSavedMessage = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Changes")
                }

                OutlinedButton(
                    onClick = {
                        // Reset to defaults
                        userNameState = "Guest"
                        loginCountState = 0
                        isFirstRunState = true
                        profileNameState = "Guest"
                        profileAgeState = "18"
                        profileIsPremiumState = false
                        val now = Instant.now().toString()
                        lastLoginState = formatInstant(now)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

private fun formatInstant(timestamp: String): String {
    return try {
        val instant = Instant.parse(timestamp)
        DateTimeFormatter
            .ofPattern("MMM dd, yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(instant)
    } catch (e: Exception) {
        timestamp
    }
}