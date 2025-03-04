package cn.aicamera.frontend.ui.copywriting

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cn.aicamera.frontend.R
import cn.aicamera.frontend.ui.theme.CameraAppTheme
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

class CopywritingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagePath : Uri? = intent.getParcelableExtra("image_path")
        // 对话框架部分
        val apiKey = "hz2zpsvk7e75"
        val userToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidGVzdFVzZXIifQ.LTIaVYRIcreLgqrl1ZuCxoTOuzvuRZyRxrP5Njscijk"
        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
        val statePluginFactory = StreamStatePluginFactory(config = StatePluginConfig(), appContext = this)
        val client = ChatClient.Builder(apiKey, this)
            .withPlugins(offlinePluginFactory,statePluginFactory) // 离线存储和缓存
            .build()
        client.connectUser(
            user = User(
                id = "testUser",
                name = "Administrator",
            ),
            token = userToken
        ).enqueue()

        val channelClient = client.channel(
            channelType = "messaging",
            channelId = "general"
        )

        // 创建频道（如果不存在）
        channelClient.create(
            memberIds = listOf("testUser"),
            extraData = mapOf("name" to "General Channel")
        ).enqueue { result ->
            if (result.isSuccess) {
                // 发送模拟消息
                val message1 = Message(
                    text = "欢迎来到聊天室！🎉",
                    user = User(
                        id = "system",
                        name = "系统通知",
                        image = "https://example.com/system.png"
                    )
                )
                channelClient.sendMessage(message1).enqueue()

                val message2 = Message(
                    text = "你好，今天天气怎么样？",
                    user = User(
                        id = "user-456",
                        name = "Bob",
                        image = "https://example.com/bob.jpg"
                    )
                )
                channelClient.sendMessage(message2).enqueue()
            }
        }

        setContent {
            CameraAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CommunicationScreen(imagePath)
                }
            }
        }
    }
}