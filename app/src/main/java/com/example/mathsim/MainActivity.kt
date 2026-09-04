package com.example.mathsim

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathSim()
        }
    }
}

@Composable
fun MathSim() {
    var showOptions by remember { mutableStateOf(true) }
    var expansion by remember { mutableStateOf(100f) }
    var hue by remember { mutableStateOf(0f) }
    var lines by remember { mutableStateOf(20f) }
    var spin by remember { mutableStateOf(0f) }
    var rotation by remember { mutableStateOf(0f) }
    val context = LocalContext.current as Activity

    LaunchedEffect(spin) {
        while (true) {
            rotation += spin
            delay(16)
        }
    }

    val hsvColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 0.2f))
    
    Box(modifier = Modifier.fillMaxSize().background(Color(hsvColor))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            val numLines = lines.toInt()
            
            for (i in 0 until numLines) {
                val angle = Math.toRadians((i * 360.0 / numLines) + rotation)
                val r = 100f + expansion
                val x1 = cx + (r * cos(angle)).toFloat()
                val y1 = cy + (r * sin(angle) * 0.4f).toFloat()
                val x2 = cx + (r * cos(angle + Math.PI)).toFloat()
                val y2 = cy + (r * sin(angle + Math.PI) * 0.4f).toFloat()
                
                drawLine(
                    color = Color.White,
                    start = androidx.compose.ui.geometry.Offset(x1, y1),
                    end = androidx.compose.ui.geometry.Offset(x2, y2),
                    strokeWidth = 2f
                )
            }
        }

        Column(modifier = Modifier.align(Alignment.TopEnd).width(250.dp).padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "—",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp).clickable { showOptions = !showOptions }
                )
                Text(
                    text = "✕",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp).clickable { context.finish() }
                )
            }
            if (showOptions) {
                Column(modifier = Modifier.background(Color(0xAA000000)).padding(16.dp)) {
                    Text("Angle / Expansion", color = Color.White)
                    Slider(value = expansion, onValueChange = { expansion = it }, valueRange = 0f..500f)
                    Text("Hue", color = Color.White)
                    Slider(value = hue, onValueChange = { hue = it }, valueRange = 0f..360f)
                    Text("Lines Multiplier", color = Color.White)
                    Slider(value = lines, onValueChange = { lines = it }, valueRange = 2f..100f)
                    Text("Spin", color = Color.White)
                    Slider(value = spin, onValueChange = { spin = it }, valueRange = -20f..20f)
                }
            }
        }
    }
}
