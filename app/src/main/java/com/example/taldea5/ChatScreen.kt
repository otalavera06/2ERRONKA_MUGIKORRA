package com.example.taldea5

import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taldea5.ui.theme.BrandBlack
import com.example.taldea5.ui.theme.BrandGold
import com.example.taldea5.ui.theme.BrandIvory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    host: String,
    port: Int = 5555,
    mesaId: Int?,
    serviceNumber: Int,
    chatSessionVersion: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val mesaName = remember(mesaId) { mesaId?.let { "Mahaia $it" } ?: "Mahaia" }
    val client = remember(host, port, mesaId, chatSessionVersion) {
        TcpChatClient(host, port, mesaId, mesaName)
    }

    val connected by client.connected.collectAsState()
    val messages by client.messages.collectAsState()
    val error by client.error.collectAsState()

    var input by remember(chatSessionVersion) { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val fileName = queryFileName(context, uri) ?: "fitxategia"
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val bytes = inputStream.readBytes()
                    inputStream.close()
                    val tempFile = java.io.File(context.cacheDir, "chat_${System.currentTimeMillis()}_$fileName")
                    tempFile.outputStream().use { it.write(bytes) }
                    client.sendFile(tempFile.absolutePath)
                }
            } catch (_: Exception) {
            }
        }
    }

    DisposableEffect(Unit) {
        client.connect()
        onDispose { client.disconnect() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (mesaId != null) "Txata · $mesaName · Zerbitzua $serviceNumber" else "Txata",
                    color = BrandGold,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = BrandGold)
                }
            },
            actions = {
                Text(
                    text = if (connected) "Konektatuta" else "Konektatu gabe",
                    color = if (connected) BrandGold else Color.Gray,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBlack)
        )

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { m ->
                ChatBubble(
                    fromMe = m.fromMe,
                    text = m.text,
                    tipoMezua = m.tipoMezua,
                    onClick = {
                        if (m.tipoMezua == "FILE" && m.fileName != null && m.fileDataBase64 != null) {
                            saveChatFile(context, m.fileName, m.fileDataBase64)
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Idatzi mezua…") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    cursorColor = Color.Black
                )
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = { input += "😊" },
                enabled = connected
            ) {
                Text(text = "😊", fontSize = 22.sp)
            }

            Spacer(Modifier.width(4.dp))

            IconButton(
                onClick = { filePickerLauncher.launch("*/*") },
                enabled = connected
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = null, tint = BrandGold)
            }

            Spacer(Modifier.width(4.dp))

            IconButton(
                onClick = {
                    client.send(input)
                    input = ""
                },
                enabled = connected && input.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = BrandGold)
            }
        }
    }
}

private fun queryFileName(context: android.content.Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && it.moveToFirst()) {
            return it.getString(nameIndex)
        }
    }
    return null
}

private fun saveChatFile(context: android.content.Context, fileName: String, base64Data: String) {
    try {
        val bytes = Base64.decode(base64Data, Base64.NO_WRAP)
        val dir = context.getExternalFilesDir(null)
        if (dir != null) {
            val file = java.io.File(dir, fileName)
            file.outputStream().use { it.write(bytes) }
            Toast.makeText(context, "Fitxategia gordeta: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Ezin izan da direktorioa lortu", Toast.LENGTH_LONG).show()
        }
    } catch (_: Exception) {
        Toast.makeText(context, "Errorea fitxategia gordetzean", Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun ChatBubble(
    fromMe: Boolean,
    text: String,
    tipoMezua: String,
    onClick: () -> Unit = {}
) {
    val bg = if (fromMe) BrandGold.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.06f)
    val align = if (fromMe) Alignment.End else Alignment.Start

    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (fromMe) Arrangement.End else Arrangement.Start) {
        Box(
            modifier = Modifier
                .clickable(enabled = tipoMezua == "FILE", onClick = onClick)
                .clip(RoundedCornerShape(14.dp))
                .background(bg)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .widthIn(max = 300.dp),

        ) {
            Text(text = text, color = Color.Black)
        }
    }
}
