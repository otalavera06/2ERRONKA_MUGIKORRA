package com.example.taldea5

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

data class ChatMessage(
    val fromMe: Boolean,
    val text: String,
    val tipoMezua: String = "TEXT",
    val timestampMs: Long = System.currentTimeMillis(),
    val fileName: String? = null,
    val fileDataBase64: String? = null
)

class TcpChatClient(
    private val host: String,
    private val port: Int,
    private val mesaId: Int?,
    private val mesaName: String
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null
    private var readJob: Job? = null

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun connect() {
        if (_connected.value) return
        if (mesaId == null) {
            _error.value = "Mahaia ez da identifikatu."
            return
        }

        _error.value = null
        scope.launch {
            try {
                val s = Socket(host, port)
                socket = s
                reader = BufferedReader(InputStreamReader(s.getInputStream()))
                writer = BufferedWriter(OutputStreamWriter(s.getOutputStream()))
                sendRaw(buildRegisterMessage(mesaId, mesaName))
                _connected.value = true

                readJob?.cancel()
                readJob = scope.launch {
                    try {
                        while (isActive && socket?.isConnected == true) {
                            val line = reader?.readLine() ?: break
                            handleServerLine(line)
                        }
                    } catch (e: Exception) {
                        _error.value = "Txata irakurtzean errorea: ${e.message}"
                    } finally {
                        disconnect()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ezin konektatu: ${e.message}"
                disconnect()
            }
        }
    }

    fun disconnect() {
        scope.launch {
            try { readJob?.cancel() } catch (_: Exception) {}
            try { reader?.close() } catch (_: Exception) {}
            try { writer?.close() } catch (_: Exception) {}
            try { socket?.close() } catch (_: Exception) {}

            readJob = null
            reader = null
            writer = null
            socket = null
            _connected.value = false
        }
    }

    fun send(text: String) {
        val msg = text.trim()
        if (msg.isEmpty()) return
        val currentMesaId = mesaId ?: run {
            _error.value = "Mahaia ez da identifikatu."
            return
        }

        addMessage(fromMe = true, text = msg, tipoMezua = "TEXT")

        scope.launch {
            try {
                if (writer == null) {
                    _error.value = "Ez dago konexiorik."
                    return@launch
                }

                sendRaw(buildMesaChatMessage(currentMesaId, mesaName, msg, "TEXT"))
            } catch (e: Exception) {
                _error.value = "Bidaltze errorea: ${e.message}"
            }
        }
    }

    fun sendEmoji(emoji: String) {
        if (emoji.isEmpty()) return
        val currentMesaId = mesaId ?: run {
            _error.value = "Mahaia ez da identifikatu."
            return
        }

        addMessage(fromMe = true, text = emoji, tipoMezua = "EMOJI")

        scope.launch {
            try {
                if (writer == null) {
                    _error.value = "Ez dago konexiorik."
                    return@launch
                }

                sendRaw(buildMesaChatMessage(currentMesaId, mesaName, emoji, "EMOJI"))
            } catch (e: Exception) {
                _error.value = "Bidaltze errorea: ${e.message}"
            }
        }
    }

    fun sendFile(filePath: String) {
        val currentMesaId = mesaId ?: run {
            _error.value = "Mahaia ez da identifikatu."
            return
        }

        scope.launch {
            try {
                val file = java.io.File(filePath)
                if (!file.exists()) {
                    _error.value = "Fitxategia ez da existitzen."
                    return@launch
                }

                val fileData = file.readBytes()
                val fileName = file.name
                val fileDataBase64 = android.util.Base64.encodeToString(fileData, android.util.Base64.NO_WRAP)
                val encryptedFileData = encode(fileDataBase64)

                addMessage(
                    fromMe = true,
                    text = "[Fitxategia: $fileName]",
                    tipoMezua = "FILE",
                    fileName = fileName,
                    fileDataBase64 = fileDataBase64
                )

                if (writer == null) {
                    _error.value = "Ez dago konexiorik."
                    return@launch
                }

                sendRaw(buildMesaFileMessage(currentMesaId, fileName, encryptedFileData))
            } catch (e: Exception) {
                _error.value = "Fitxategia bidaltze errorea: ${e.message}"
            }
        }
    }

    private fun handleServerLine(line: String) {
        val parts = line.split("|")
        if (parts.size < 2) return

        when (parts[0]) {
            "CHAT" -> {
                if (parts.size < 5) return
                if (parts[1] != "TPV") return

                val tipoMezua = if (parts.size >= 7) parts[6] else "TEXT"

                when (tipoMezua) {
                    "FILE" -> {
                        if (parts.size >= 7) {
                            val fileName = parts[4]
                            val encryptedData = parts[5]
                            val fileDataBase64 = decode(encryptedData)
                            addMessage(
                                fromMe = false,
                                text = "[Fitxategia: $fileName]",
                                tipoMezua = "FILE",
                                fileName = fileName,
                                fileDataBase64 = fileDataBase64
                            )
                        }
                    }
                    "EMOJI" -> {
                        val emoji = decode(parts[4])
                        addMessage(fromMe = false, text = emoji, tipoMezua = "EMOJI")
                    }
                    else -> {
                        val messageText = decode(parts[4])
                        addMessage(fromMe = false, text = messageText, tipoMezua = "TEXT")
                    }
                }
            }
        }
    }

    private fun buildRegisterMessage(mesaId: Int, mesaName: String): String {
        return "REGISTER|MESA|$mesaId|${encode(mesaName)}"
    }

    private fun buildMesaChatMessage(mesaId: Int, mesaName: String, text: String, tipoMezua: String = "TEXT"): String {
        return "CHAT|MESA|$mesaId|${encode(mesaName)}|${encode(text)}|$tipoMezua"
    }

    private fun buildMesaFileMessage(mesaId: Int, fileName: String, encryptedFileData: String): String {
        return "CHAT|MESA|$mesaId|${encode(mesaName)}|$fileName|$encryptedFileData|FILE"
    }

    private fun sendRaw(text: String) {
        writer?.apply {
            write(text)
            newLine()
            flush()
        } ?: run {
            _error.value = "Ez dago konexiorik."
        }
    }

    private fun encode(value: String): String {
        return try {
            CryptoHelper.cifrar(value)
        } catch (e: Exception) {
            _error.value = "Errorea zifratzean: ${e.message}"
            ""
        }
    }

    private fun decode(value: String): String {
        return try {
            CryptoHelper.descifrar(value)
        } catch (e: Exception) {
            _error.value = "Errorea deszifratzean: ${e.message}"
            ""
        }
    }

    private fun addMessage(
        fromMe: Boolean,
        text: String,
        tipoMezua: String = "TEXT",
        fileName: String? = null,
        fileDataBase64: String? = null
    ) {
        val current = _messages.value
        _messages.value = current + ChatMessage(
            fromMe = fromMe,
            text = text,
            tipoMezua = tipoMezua,
            fileName = fileName,
            fileDataBase64 = fileDataBase64
        )
    }
}

