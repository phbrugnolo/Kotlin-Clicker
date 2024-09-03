package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Jogo()
            }
        }
    }
}

@Composable
fun Jogo() {
    var jogoIniciado by rememberSaveable { mutableStateOf(false) }
    var numClicks by rememberSaveable { mutableStateOf(0) }
    var targetClicks by rememberSaveable { mutableStateOf((1..50).random()) }
    var imagemAtual by rememberSaveable { mutableStateOf(R.drawable.inicial) }
    var jogoAtivo by rememberSaveable { mutableStateOf(true) }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    var mostrarDialogoDesistencia by rememberSaveable { mutableStateOf(false) }
    var iniciarAtraso by rememberSaveable { mutableStateOf(false) }
    var iniciarAtrasoDesistencia by rememberSaveable { mutableStateOf(false) }
    var mensagemMotivacional by rememberSaveable { mutableStateOf("") }
    var mensagemDesistencia by rememberSaveable { mutableStateOf("") }

    val mensagensMotivacionais = listOf(
        "Cada passo é um avanço em direção ao seu objetivo!",
        "Mantenha o foco e siga em frente!",
        "A corrida é longa, mas a vitória é certa!",
        "Você está mais perto do que nunca. Continue!",
        "O esforço de agora é o sucesso de amanhã!",
        "A linha de chegada está logo à frente. Não desista agora!"
    )

    fun atualizarImagem() {
        val progresso = numClicks.toFloat() / targetClicks
        imagemAtual = when {
            progresso >= 1.0f -> {
                mensagensMotivacionais[5]
                R.drawable.conquista
            }
            progresso >= 0.66f -> {
                mensagemMotivacional = mensagensMotivacionais[4]
                R.drawable.img_final
            }
            progresso >= 0.33f -> {
                mensagemMotivacional = mensagensMotivacionais[2]
                R.drawable.mediana
            }
            else -> {
                mensagemMotivacional = mensagensMotivacionais[0]
                R.drawable.inicial
            }
        }
    }

    fun reiniciarJogo() {
        numClicks = 0
        targetClicks = (1..50).random()
        imagemAtual = R.drawable.inicial
        jogoAtivo = true
        mostrarDialogo = false
        mostrarDialogoDesistencia = false
        iniciarAtraso = false
        iniciarAtrasoDesistencia = false
        mensagemMotivacional = mensagensMotivacionais[0]
        mensagemDesistencia = ""
    }

    fun resetarParaMenuInicial() {
        jogoIniciado = false
        reiniciarJogo()
    }

    if (!jogoIniciado) {
        MenuInicial(onStartGame = {
            resetarParaMenuInicial()
            jogoIniciado = true
        })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!iniciarAtrasoDesistencia) {
                Image(
                    painter = painterResource(id = imagemAtual),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .clickable(enabled = jogoAtivo) {
                            if (numClicks < targetClicks) {
                                numClicks++
                                atualizarImagem()
                                if (numClicks == targetClicks) {
                                    jogoAtivo = false
                                    iniciarAtraso = true
                                }
                            }
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(mensagemMotivacional)

                Spacer(modifier = Modifier.height(16.dp))

                // Adicionando o contador de cliques
                Text("Cliques: $numClicks")

            } else {
                Image(
                    painter = painterResource(id = R.drawable.desistencia),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )

                Text("Você desistiu!")
            }

            if (jogoAtivo) {
                Button(
                    onClick = {
                        iniciarAtrasoDesistencia = true
                        jogoAtivo = false
                        mensagemDesistencia = "Você desistiu!"
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Desistir")
                }
            }

            if (iniciarAtraso) {
                LaunchedEffect(Unit) {
                    delay(5000L)
                    mostrarDialogo = true
                    iniciarAtraso = false
                }
            }

            if (iniciarAtrasoDesistencia) {
                LaunchedEffect(Unit) {
                    delay(5000L)
                    mostrarDialogoDesistencia = true
                    iniciarAtrasoDesistencia = false
                }
            }

            if (mostrarDialogo) {
                AlertDialog(
                    onDismissRequest = { /* Nada acontece ao clicar fora */ },
                    title = { Text(text = "Parabéns!") },
                    text = { Text("Você atingiu a conquista! Deseja jogar novamente?") },
                    confirmButton = {
                        Button(onClick = {
                            reiniciarJogo()
                        }) {
                            Text("Sim")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            resetarParaMenuInicial()
                        }) {
                            Text("Não")
                        }
                    }
                )
            }

            if (mostrarDialogoDesistencia && !iniciarAtrasoDesistencia) {
                AlertDialog(
                    onDismissRequest = { /* Nada acontece ao clicar fora */ },
                    title = { Text(text = "Você desistiu!") },
                    text = { Text("Deseja iniciar um novo jogo?") },
                    confirmButton = {
                        Button(onClick = {
                            reiniciarJogo()
                        }) {
                            Text("Sim")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            resetarParaMenuInicial()
                        }) {
                            Text("Não")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MenuInicial(onStartGame: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bem-vindo ao Jogo!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onStartGame) {
            Text("Jogar")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Jogo()
    }
}