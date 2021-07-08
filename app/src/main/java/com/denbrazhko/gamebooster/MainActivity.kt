package com.denbrazhko.gamebooster

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.parseColor
import android.graphics.PixelFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import com.denbrazhko.gamebooster.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val wm by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    private lateinit var aimView: ImageView

    private val overlayPermissionContract =
        registerForActivityResult(OverlayPermissionContract()) {}

    private val aimSettings by lazy { AimSettings(size = binding.ivAim.width) }

    private val layoutParamsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else WindowManager.LayoutParams.TYPE_PHONE

    private val aimViewLayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        layoutParamsType,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        initListeners()
    }

    private fun init() {
        aimView = ImageView(this).apply {
            setImageDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.ic_aim
                )
            )
            setColorFilter(aimSettings.color)
        }
    }


    private fun initListeners() {
        with(binding) {
            switchToggle.setOnCheckedChangeListener { _, isChecked ->
                toggleAimVisibility(
                    isChecked
                )
            }
            seekSize.setOnSeekBarChangeListener(this@MainActivity)
            vColor0.setOnClickListener { changePreviewAimColor(0) }
            vColor1.setOnClickListener { changePreviewAimColor(1) }
            vColor2.setOnClickListener { changePreviewAimColor(2) }
            vColor3.setOnClickListener { changePreviewAimColor(3) }
            vColor4.setOnClickListener { changePreviewAimColor(4) }
        }
    }


    private fun changePreviewAimColor(arrayItemPosition: Int) {
        binding.ivAim.setColorFilter(colors[arrayItemPosition])
        aimView.setColorFilter(colors[arrayItemPosition])
    }

    private fun toggleAimVisibility(isVisible: Boolean) {
        if (isVisible) {
            if (shouldRequestOverlayPermission())
                overlayPermissionContract.launch(Unit)
        }
    }


    private fun showAim() {
        if (binding.switchToggle.isChecked && !shouldRequestOverlayPermission()) {
            if(aimSettings.size == 0) aimSettings.size = binding.ivAim.width
            aimViewLayoutParams.height = aimSettings.size
            aimViewLayoutParams.width = aimSettings.size
            aimView.colorFilter = binding.ivAim.colorFilter
            wm.addView(aimView, aimViewLayoutParams)
        }
    }


    override fun onPause() {
        super.onPause()
        showAim()
    }

    override fun onResume() {
        super.onResume()
        removeAimView()
    }


    override fun onDestroy() {
        super.onDestroy()
        removeAimView()
    }

    private fun removeAimView() {
        if (aimView.windowToken != null) {
            wm.removeView(aimView)
        }
    }

    private fun shouldRequestOverlayPermission(): Boolean = !Settings.canDrawOverlays(this)

    override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

    override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val coefficient = progress.toFloat() / 50 //0 - min, 100 - max, 50 - default
        aimSettings.size = (binding.ivAim.width * coefficient).toInt()
        binding.ivAim.scaleX = coefficient
        binding.ivAim.scaleY = coefficient
    }

    companion object {
        private const val TAG = "MAIN_ACTIVITY_TAG"
        private val colors = arrayOf(
            parseColor("#605D58"),
            parseColor("#642C6B"),
            parseColor("#151412"),
            parseColor("#D2383A"),
            parseColor("#1CA754")
        )

    }
}