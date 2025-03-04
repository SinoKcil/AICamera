package cn.aicamera.frontend.ui.camera.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArrowAnimation(
    arrowShow: Offset?,
    start: Offset,
    end: Offset,
    text: String,
    duration: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()

    // 整体动画进度（0f到1f循环）
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val textMeasurer = rememberTextMeasurer()

    val wingMax =
        Math.floor(
            Math.sqrt(
                Math.pow((start.x - end.x).toDouble(), 2.0)
                        + Math.pow((start.y - end.y).toDouble(), 2.0)
            ) / 40
        )
    // 当前显示侧翼数量，使用乘法实现区间计算
    val wingCount = Math.ceil(progress * wingMax).toInt()-1

    // 当前箭头位置
    val currentPos = Offset(
        x = lerp(start.x, end.x, progress),
        y = lerp(start.y, end.y, progress)
    )

    // 方向计算
    val angle = remember(start, end) {
        val dx = end.x - start.x
        val dy = end.y - start.y
        atan2(dy, dx) * (180f / PI.toFloat())
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            var position = Offset(0f,0f)
            // 绘制动态侧翼
            repeat(wingCount) { index ->
                position = calculateOffsetOnLine(start,end,1/wingMax.toFloat()*index)
                drawPath(
                    path = createWingPath(position, angle),
                    color = Color.Yellow.copy(alpha = 0.9f - index * 0.05f),
                    style = Stroke(width = (3 - index*0.3f).dp.toPx())
                )
            }

            // 文字提示
            val measuredText = textMeasurer.measure(
                AnnotatedString(text),
//                AnnotatedString("(${currentPos.x},${currentPos.y}),angle=${angle}," +
//                        "progress=${progress},wingMax=${wingMax},wingCount=${wingCount}," +
//                        "nowPos=(${position.x},${position.y})"),
                constraints = Constraints.fixed(
                    width = (size.width / 3f).toInt(),
                    height = (size.height / 3f).toInt()
                ),
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
            // 绘制文字
            drawText(
                textLayoutResult = measuredText,
                color = Color.White,
                // 文字位置不变
                topLeft = Offset(start.x - 80.dp.toPx(), start.y + 40.dp.toPx()),
            )
        }
    }
}

/**
 * 绘制一个顶点位于position，线段角度为angle的左右侧翼
 */
private fun createWingPath(position: Offset, angle: Float): Path {
    return Path().apply {
        val wingLength = 60f  // 侧翼长度
        val wingAngle = 25f

        // 左侧翼
        val leftOffset = calculateRotatedEndpoint(position, angle, wingLength, wingAngle)
        moveTo(position.x, position.y)
        lineTo(leftOffset.x, leftOffset.y)

        // 右侧翼
        val rightOffset = calculateRotatedEndpoint(position, angle, wingLength, -wingAngle)
        moveTo(position.x, position.y)
        lineTo(rightOffset.x, rightOffset.y)
    }
}

private fun calculateRotatedEndpoint(
    position: Offset,
    angle: Float, // 初始角度，x轴正方向顺时针，角度值
    length: Float,
    originAngle: Float // 顺时针旋转角度
): Offset {
    val newAngle = angle + 180
    val originAngleInRadians = originAngle.toFloat()
    val totalAngle = Math.toRadians((newAngle + originAngleInRadians).toDouble()).toFloat()

    val dx = length * cos(totalAngle)
    val dy = length * sin(totalAngle)

    val endX = position.x + dx
    val endY = position.y + dy

    return Offset(endX, endY)
}

// 角度转换扩展
val Float.degrees: Double get() = this * PI / 180

/**
 * 计算从start到end的线段上指定比例位置的 Offset
 */
fun calculateOffsetOnLine(start: Offset, end: Offset, ratio: Float): Offset {
    val clampedRatio = ratio.coerceIn(0f, 1f)
    val x = start.x + (end.x - start.x) * clampedRatio
    val y = start.y + (end.y - start.y) * clampedRatio
    return Offset(x, y)
}

// 使用示例
@Preview
@Composable
fun AnimationPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        ArrowAnimation(
            arrowShow = Offset(500f, 500f),
            start = Offset(700f, 700f),
            end = Offset(300f, 300f),
            text = "跟随箭头移动手机",
            duration = 2000 // 毫秒
        )
    }
}
