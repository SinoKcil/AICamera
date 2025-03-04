package cn.aicamera.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import cn.aicamera.frontend.ui.MainScreen
import cn.aicamera.frontend.ui.theme.CameraAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CameraAppTheme {
                //初始化导航控制器
                val navController = rememberNavController()
                //主界面
                MainScreen(navController = navController)
            }
        }
    }
}

@Preview
@Composable
fun TestView(){
    CameraAppTheme {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}
