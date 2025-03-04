package cn.aicamera.frontend.ui


import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cn.aicamera.frontend.R
import cn.aicamera.frontend.ui.camera.CameraActivity
import cn.aicamera.frontend.ui.copywriting.CopywritingActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val mContext = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("相机名称") },
                actions = {
                    // 设置按钮
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
//        bottomBar = {
//            // 底部照相按钮
//            BottomAppBar(
//                modifier = Modifier.height(80.dp),
//                contentPadding = PaddingValues(16.dp),
//                containerColor = MaterialTheme.colorScheme.primary
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Button(
//                        onClick = {
//                            // 跳转到相机页
//                            val intent=Intent(mContext,CameraActivity::class.java)
//                            mContext.startActivity(intent)
//                        },
//                        shape = RoundedCornerShape(50),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.White,
//                            contentColor = MaterialTheme.colorScheme.primary
//                        ),
//                        modifier = Modifier.size(60.dp)
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_camera),
//                            contentDescription = "Camera",
//                            modifier = Modifier.size(50.dp)
//                        )
//                    }
//                }
//            }
//        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // =照相页面
            RoundedButton(
                text = "智能拍照引导",
                onClick = {
                    val intent=Intent(mContext,CameraActivity::class.java)
                    mContext.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 相册页面
            RoundedButton(
                text = "图片自动生成文案",
                onClick = {
                    val intent=Intent(mContext,CopywritingActivity::class.java)
                    mContext.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 设置页面
            RoundedButton(
                text = "设置",
                onClick = { navController.navigate("settings") }
            )
        }
    }
}

@Composable
fun RoundedButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}