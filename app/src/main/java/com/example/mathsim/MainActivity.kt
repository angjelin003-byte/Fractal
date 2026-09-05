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
import kotlin.math.abs
import kotlin.system.exitProcess

data class Vec3(val x: Float, val y: Float, val z: Float) {
    fun dot(o: Vec3) = x * o.x + y * o.y + z * o.z
    fun cross(o: Vec3) = Vec3(y * o.z - z * o.y, z * o.x - x * o.z, x * o.y - y * o.x)
    fun norm() = sqrt(x * x + y * y + z * z)
    fun normalize(): Vec3 {
        val n = norm()
        return if (n > 0) Vec3(x / n, y / n, z / n) else Vec3(0f, 0f, 0f)
    }
    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
}

fun getOrthogonalBasis(v: Vec3): Pair<Vec3, Vec3> {
    val vn = v.normalize()
    var up = Vec3(0f, 1f, 0f)
    if (abs(vn.dot(up)) > 0.99f) {
        up = Vec3(1f, 0f, 0f)
    }
    val u1 = vn.cross(up).normalize()
    val u2 = vn.cross(u1)
    return Pair(u1, u2)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MathSim() }
    }
}

@Composable
fun MathSim() {
    var showOptions by remember { mutableStateOf(true) }
    var expansion by remember { mutableStateOf(5f) }
    var hue by remember { mutableStateOf(120f) }
    var lines by remember { mutableStateOf(50f) }
    var spin by remember { mutableStateOf(0f) }
    val context = LocalContext.current as Activity

    val hsvColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 0.2f))
    
    Box(modifier = Modifier.fillMaxSize().background(Color(hsvColor))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            val numIterations = (lines * 20).toInt()
            
            val path = Path()
            var currentX = cx
            var currentY = cy
            path.moveTo(currentX, currentY)
            
            var vn = Vec3(0f, -1f, 0f)
            
            val psi = (Math.PI * (3.0 - sqrt(5.0))).toFloat()
            val theta = 0.55f + spin
            val cosTheta = cos(theta)
            val sinTheta = sin(theta)
            
            for (i in 0 until numIterations) {
                val (u1, u2) = getOrthogonalBasis(vn)
                
                val currentPsi = i * psi
                val uPsi = (u1 * cos(currentPsi)) + (u2 * sin(currentPsi))
                
                val vNext = (vn * cosTheta) + (uPsi * sinTheta)
                vn = vNext.normalize()
                
                currentX += vn.x * expansion
                currentY += vn.y * expansion
                
                path.lineTo(currentX, currentY)
            }
            
            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(width = 1.5f)
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
                    modifier = Modifier.padding(16.dp).clickable {
                        context.finishAffinity()
                        exitProcess(0)
                    }
                )
            }
            if (showOptions) {
                Column(modifier = Modifier.background(Color(0xAA000000)).padding(16.dp)) {
                    Text("Angle/Expansion", color = Color.White)
                    Slider(value = expansion, onValueChange = { expansion = it }, valueRange = 1f..50f)
                    Text("Hue", color = Color.White)
                    Slider(value = hue, onValueChange = { hue = it }, valueRange = 0f..360f)
                    Text("Lines Multiplier", color = Color.White)
                    Slider(value = lines, onValueChange = { lines = it }, valueRange = 1f..300f)
                    Text("Spin (θ)", color = Color.White)
                    Slider(value = spin, onValueChange = { spin = it }, valueRange = -1.5f..1.5f)
                }
            }
        }
    }
}
