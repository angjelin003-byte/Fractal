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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MathSim() }
    }
}

@Composable
fun MathSim() {
    var showOptions by remember { mutableStateOf(true) }
    var expansion by remember { mutableStateOf(20f) }
    var hue by remember { mutableStateOf(120f) }
    var lines by remember { mutableStateOf(50f) }
    var spin by remember { mutableStateOf(0f) }
    val context = LocalContext.current as Activity

    val hsvColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 0.2f))
    
    Box(modifier = Modifier.fillMaxSize().background(Color(hsvColor))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            val numIterations = (lines * 100).toInt()
            
            val path = Path()
            var currentX = cx
            var currentY = cy
            path.moveTo(currentX, currentY)
            
            // Initial vector (V0)
            var vx = 0f
            var vy = -1f
            
            // ψ = π(3 - √5) ≈ 137.5°
            val psi = Math.PI * (3.0 - Math.sqrt(5.0))
            
            // θ = 0.55 + spin (0 in the middle of the slider)
            val baseTheta = 0.55 + spin
            val cosTheta = cos(baseTheta).toFloat()
            val sinTheta = sin(baseTheta).toFloat()
            
            for (i in 0 until numIterations) {
                // u(n*ψ)
                val angle = i * psi
                val ux = cos(angle).toFloat()
                val uy = sin(angle).toFloat()
                
                // Vn+1 = cos(θ)*Vn + sin(θ)*u(n*ψ)
                var nvx = cosTheta * vx + sinTheta * ux
                var nvy = cosTheta * vy + sinTheta * uy
                
                // norm
                val len = sqrt(nvx * nvx + nvy * nvy)
                if (len > 0) {
                    nvx /= len
                    nvy /= len
                }
                
                vx = nvx
                vy = nvy
                
                // Calculate next point
                val segLength = expansion * 0.2f
                currentX += vx * segLength
                currentY += vy * segLength
                
                path.lineTo(currentX, currentY)
            }
            
            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(width = 2f)
            )
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
                    Text("Angle/Expansion", color = Color.White)
                    Slider(value = expansion, onValueChange = { expansion = it }, valueRange = 1f..100f)
                    Text("Hue", color = Color.White)
                    Slider(value = hue, onValueChange = { hue = it }, valueRange = 0f..360f)
                    Text("Lines Multiplier", color = Color.White)
                    Slider(value = lines, onValueChange = { lines = it }, valueRange = 10f..200f)
                    Text("Spin (θ)", color = Color.White)
                    Slider(value = spin, onValueChange = { spin = it }, valueRange = -1f..1f)
                }
            }
        }
    }
}
