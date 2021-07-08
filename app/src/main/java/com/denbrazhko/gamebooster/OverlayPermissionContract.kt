package com.denbrazhko.gamebooster

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract

class OverlayPermissionContract : ActivityResultContract<Unit, Unit>() {
    override fun createIntent(context: Context, input: Unit?): Intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    )

    override fun parseResult(resultCode: Int, intent: Intent?) = Unit

    companion object {
        private const val TAG = "OVERLAY_CONTRACT_TAG"
    }
}