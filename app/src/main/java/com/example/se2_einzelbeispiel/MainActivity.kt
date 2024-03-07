package com.example.se2_einzelbeispiel

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.se2_einzelbeispiel.ui.theme.SE2_EinzelbeispielTheme
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SE2_EinzelbeispielTheme {
                MainWindow()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWindow() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var matNr by rememberSaveable { mutableStateOf("") }
        var serverResult by rememberSaveable { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextField(
                value = matNr,
                onValueChange = { matNr = it },
                label = { Text("Gib deine Martrikelnummer ein") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text(text = "Antwort vom Server: $serverResult")

            Button(
                onClick = {
                          Thread {
                              val serverResponse = sendMatNrToServer(matNr)
                              serverResult = serverResponse
                          }.start()
            },
            ) {
                Text(text = "Abschicken")
            }
        }
    }
}

private fun sendMatNrToServer(matNr: String): String {
    var receivedData = ""

    try {
        val url = "se2-submission.aau.at"
        val port = 20080
        val socket = Socket(url, port)


        //send data to server
        val output = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
        output.println(matNr.toInt())

        // receive data from server
        val serverResponseReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val response = serverResponseReader.readLine()

        output.close()
        serverResponseReader.close()
        socket.close()

        if (response.isNotEmpty()) {
            receivedData = response
            Log.d("main_test","Received from server: $response")
        }
        else
            Log.d("main_test", "NO DATA RECEIVED!")
        }
    catch (e: Exception) {
        receivedData = "ERROR: Daten konnten nicht vom Server geladen werden!"
    }
    return receivedData
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SE2_EinzelbeispielTheme {
        MainWindow()
    }
}