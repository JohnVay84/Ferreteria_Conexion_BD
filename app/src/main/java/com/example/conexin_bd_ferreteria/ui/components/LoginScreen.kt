package com.example.conexin_bd_ferreteria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var usuario by remember { mutableStateOf("admin") }
    var clave by remember { mutableStateOf("1234") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showRecoverDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // LOGO E ICONO
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = "Aplicación Ferretería Don John",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Rodamientos y Fierros - Iniciar Sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // CAMPO USUARIO
                OutlinedTextField(
                    value = usuario,
                    onValueChange = {
                        usuario = it
                        errorMessage = null
                    },
                    label = { Text("Usuario / Correo") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // CAMPO CLAVE
                OutlinedTextField(
                    value = clave,
                    onValueChange = {
                        clave = it
                        errorMessage = null
                    },
                    label = { Text("Clave") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                // BOTÓN INICIAR SESIÓN
                Button(
                    onClick = {
                        if (usuario.isNotBlank() && clave.isNotBlank()) {
                            onLoginSuccess()
                        } else {
                            errorMessage = "Por favor ingrese usuario y clave."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "INICIAR SESIÓN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // ENLACE RECUPERAR CONTRASEÑA
                TextButton(onClick = { showRecoverDialog = true }) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    // DIÁLOGO RECUPERAR CONTRASEÑA
    if (showRecoverDialog) {
        var recoverEmail by remember { mutableStateOf(usuario) }
        var recoverSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showRecoverDialog = false },
            title = { Text("Recuperar Contraseña") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (recoverSuccess) {
                        Text(
                            text = "Se ha enviado un enlace de restablecimiento al correo: $recoverEmail.",
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text("Ingresa tu correo o usuario registrado para restablecer tu clave:")
                        OutlinedTextField(
                            value = recoverEmail,
                            onValueChange = { recoverEmail = it },
                            label = { Text("Correo o Usuario") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (recoverSuccess) {
                    Button(onClick = { showRecoverDialog = false }) {
                        Text("Aceptar")
                    }
                } else {
                    Button(
                        onClick = { recoverSuccess = true },
                        enabled = recoverEmail.isNotBlank()
                    ) {
                        Text("Enviar Enlace")
                    }
                }
            },
            dismissButton = {
                if (!recoverSuccess) {
                    TextButton(onClick = { showRecoverDialog = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }
}
