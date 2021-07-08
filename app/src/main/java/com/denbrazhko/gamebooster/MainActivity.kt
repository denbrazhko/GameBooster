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
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import com.denbrazhko.gamebooster.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val wm by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    private val aimView: ImageView by lazy {
        ImageView(this).apply {
            setImageDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.ic_aim
                )
            )
            setColorFilter(aimSettings.color)
        }
    }

    private val overlayPermissionContract =
        registerForActivityResult(OverlayPermissionContract()) {
            /* system doesn't returns result of this permission type,
             so we need to check it manually */
            if (binding.switchToggle.isChecked) {
                if (overlayPermissionGranted())
                    showAim()
                else binding.switchToggle.isChecked = false
            }
        }

    private val aimSettings = AimSettings()

    private val layoutParamsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else WindowManager.LayoutParams.TYPE_PHONE




    private val aimViewLayoutParams = WindowManager.LayoutParams(
        aimSettings.size,
        aimSettings.size,
        layoutParamsType,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    private val initX = aimViewLayoutParams.x
    private val initY = aimViewLayoutParams.y


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        initListeners()
    }

    private fun init() {
        binding.seekSize.incrementProgressBy(10)
        showAim()
        moveAim()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun moveAim() {
        var initTouchX = 0f
        var initTouchY = 0f
        aimView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initTouchX = event.rawX
                    initTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_UP -> true
                MotionEvent.ACTION_MOVE -> {
                    aimViewLayoutParams.x = initX + (event.rawX - initTouchX).toInt()
                    aimViewLayoutParams.y = initY + (event.rawY - initTouchY).toInt()
                    updateAimView()
                    true
                }
                else -> false
            }
        }
    }


    private fun initListeners() {
        with(binding) {
            switchToggle.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!overlayPermissionGranted())
                        overlayPermissionContract.launch(Unit)
                    else
                        showAim()
                } else
                    hideAim()

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
        aimView.setColorFilter(colors[arrayItemPosition])
    }


    private fun showAim() {
        if (binding.switchToggle.isChecked && overlayPermissionGranted()) {
            wm.addView(aimView, aimViewLayoutParams)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        hideAim()
    }

    private fun hideAim() {
        if (aimView.windowToken != null) {
            wm.removeView(aimView)
        }
    }

    private fun updateAimView() {
        wm.updateViewLayout(aimView, aimViewLayoutParams)
    }

    private fun overlayPermissionGranted(): Boolean = Settings.canDrawOverlays(this)

    override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

    override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val coefficient = progress.toFloat() / 50 //10 - min, 100 - max, 50 - default
        aimSettings.size = (aimSettings.defaultSize * coefficient).toInt()
        aimViewLayoutParams.width = aimSettings.size
        aimViewLayoutParams.height = aimSettings.size
        updateAimView()
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