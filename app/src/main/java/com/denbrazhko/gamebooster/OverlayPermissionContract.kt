package com.denbrazhko.gamebooster

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

class OverlayPermissionContract : ActivityResultContract<Unit, Unit>() {
    override fun createIntent(context: Context, input: Unit?): Intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    )

    override fun parseResult(resultCode: Int, intent: Intent?) {

    }
}