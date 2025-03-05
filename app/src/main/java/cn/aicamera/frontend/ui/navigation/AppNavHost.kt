package cn.aicamera.frontend.ui.navigation

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cn.aicamera.frontend.ui.*
import cn.aicamera.frontend.ui.camera.screen.CameraScreen
import cn.aicamera.frontend.ui.copywriting.CommunicationScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "main") {
        // 主界面
        composable("home") {
            MainScreen(navController = navController)
        }
        // 个人中心页面
        composable("profile") {

        }
//        // 相机页面
//        composable("camera") {
//            CameraScreen()
//        }
//        // 文案页面
//        composable("chat") {
//            CommunicationScreen()
//        }
//
    }
}