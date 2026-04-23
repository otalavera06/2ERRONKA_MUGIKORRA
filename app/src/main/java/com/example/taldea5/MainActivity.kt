package com.example.taldea5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taldea5.ui.theme.Taldea5Theme
import com.example.taldea5.ui.theme.BrandBlack
import com.example.taldea5.ui.theme.BrandGold
import com.example.taldea5.ui.theme.BrandIvory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Taldea5Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppRoot()
                }
            }
        }
    }
}

private enum class Screen { Login, Menu, Chat }

@Composable
private fun AppRoot() {
    var screen by remember { mutableStateOf(Screen.Login) }
    var loggedMahaia by remember { mutableStateOf<MahaiaLoginResponse?>(null) }
    var serviceNumber by remember { mutableIntStateOf(1) }
    var serviceActive by remember { mutableStateOf(false) }
    var chatSessionVersion by remember { mutableIntStateOf(1) }
    val sharedCart = remember { mutableStateMapOf<Int, Int>() }
    val sharedAccumulated = remember { mutableStateListOf<EskaeraLineRequest>() }

    when (screen) {
        Screen.Login -> LoginScreen(
            onLoginSuccess = { mahaia ->
                loggedMahaia = mahaia
                serviceNumber = 1
                serviceActive = false
                chatSessionVersion = 1
                sharedCart.clear()
                sharedAccumulated.clear()
                screen = Screen.Menu
            }
        )
        Screen.Menu -> MenuScreen(
            workerName = loggedMahaia?.displayName,
            workerMahaiId = loggedMahaia?.id,
            chatBaimena = loggedMahaia?.chatBaimena ?: false,
            serviceNumber = serviceNumber,
            serviceActive = serviceActive,
            onServiceStarted = {
                serviceActive = true
            },
            onServiceFinished = {
                serviceActive = false
                serviceNumber += 1
                chatSessionVersion += 1
            },
            onLogout = {
                loggedMahaia = null
                serviceNumber = 1
                serviceActive = false
                chatSessionVersion = 1
                sharedCart.clear()
                sharedAccumulated.clear()
                screen = Screen.Login
            },
            onOpenChat = {
                screen = Screen.Chat
            },
            sharedCart = sharedCart,
            sharedAccumulated = sharedAccumulated
        )
        Screen.Chat -> ChatScreen(
            host = NetworkConfig.chatHost,
            mesaId = loggedMahaia?.id,
            serviceNumber = serviceNumber,
            chatSessionVersion = chatSessionVersion,
            onBack = {
                screen = Screen.Menu
            }
        )
    }
}

@Composable
private fun LoginScreen(onLoginSuccess: (MahaiaLoginResponse) -> Unit) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(BrandBlack),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo2erronkabien),
                contentDescription = "Logoa",
                modifier = Modifier.size(240.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(BrandBlack)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text("Erabiltzailea:", fontSize = 18.sp, color = BrandIvory)
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandGold,
                    unfocusedBorderColor = BrandIvory.copy(alpha = 0.5f),
                    focusedTextColor = BrandIvory,
                    unfocusedTextColor = BrandIvory,
                    cursorColor = BrandGold
                )
            )

            Text("Pasahitza:", fontSize = 18.sp, color = BrandIvory)
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 28.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandGold,
                    unfocusedBorderColor = BrandIvory.copy(alpha = 0.5f),
                    focusedTextColor = BrandIvory,
                    unfocusedTextColor = BrandIvory,
                    cursorColor = BrandGold
                )
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        msg = ""
                        try {
                            val res = RetrofitClient.api.login(LoginRequest(user, pass))
                            val mahaia = res.body()
                            if (res.isSuccessful && mahaia != null) {
                                onLoginSuccess(mahaia)
                            } else if (res.code() == 401) {
                                msg = "Erabiltzailea edo pasahitza okerra da"
                            } else {
                                msg = "Errorea: ${res.code()}"
                            }
                        } catch (e: Exception) {
                            msg = "Errorea: ${e.message}"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGold, contentColor = BrandBlack)
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BrandBlack, strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                }
                Text("Saioa hasi")
            }

            if (msg.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(msg, color = BrandIvory)
            }
        }
    }
}
