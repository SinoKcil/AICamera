package cn.aicamera.frontend.ui.camera.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.aicamera.frontend.R

/**
 * 功能区域,包括拍照，调节缩放倍数等
 */
@SuppressLint("DefaultLocale")
@Composable
fun FunctionArea(
    updateScale: (Float) -> Unit,
    revertCamera: () -> Unit,
    takePhoto: () -> Unit,
    scale: Float
) {
    val scaleStep = 0.1f // 缩放按钮的步长
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 翻转摄像机按钮
        Column(
            modifier = Modifier.size(64.dp),
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    revertCamera()
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "翻转摄像头",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "翻转",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
            )
        }
        // 拍照按钮
        IconButton(
            onClick = {
                // 拍照逻辑
                takePhoto()
            },
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "拍摄",
                modifier = Modifier.fillMaxSize()
            )
        }

        // 缩放按钮,实现刻度尺比较麻烦，这里使用两个按钮进行最小若干距离的缩放
        Column(
            modifier = Modifier.width(64.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // 增加
            IconButton(
                onClick = {
                    updateScale(scaleStep)
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "减小缩放倍数",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (scale < 5f) {
                    String.format("%.2f", scale)
                } else "Max",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
            )
            // 减少
            IconButton(
                onClick = {
                    updateScale(-scaleStep)
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "增大缩放倍数",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "缩放",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
            )
        }
    }
}