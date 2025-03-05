package cn.aicamera.frontend.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

// 来源：https://blog.csdn.net/Tobey_r1/article/details/131414236
/**
 * 选择一张照片
 */
class SelectPicture : ActivityResultContract<Unit?, Uri?>() {

    private var context: Context? = null

    override fun createIntent(context: Context, input: Unit?): Intent {
        this.context = context
        return Intent(Intent.ACTION_PICK).setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri?{
        return intent?.data
    }
}
