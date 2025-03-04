package cn.aicamera.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cn.aicamera.frontend.ui.*
import cn.aicamera.frontend.ui.camera.screen.CameraScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "main") {
        // 主界面
        composable("main") {
            MainScreen(navController = navController)
        }

        // 相机页面
        composable("camera") {
            CameraScreen()
        }
//        // 相册页面
//        composable("gallery") {
//            GalleryScreen()
//        }
//
//        // 设置页面
//        composable("settings") {
//            SettingsScreen()
//        }
    }
}