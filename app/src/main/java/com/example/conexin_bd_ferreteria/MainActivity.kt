package com.example.conexin_bd_ferreteria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.conexin_bd_ferreteria.ui.FerreteriaAppPreview
import com.example.conexin_bd_ferreteria.ui.FerreteriaAppScreen
import com.example.conexin_bd_ferreteria.ui.theme.Conexión_BD_FerreteriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Conexión_BD_FerreteriaTheme {
                FerreteriaAppScreen()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    FerreteriaAppPreview()
}
