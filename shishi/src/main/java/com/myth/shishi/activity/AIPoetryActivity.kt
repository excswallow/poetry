package com.myth.shishi.activity

import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.myth.poetrycommon.BaseApplication
import com.myth.shishi.R
import com.myth.shishi.ai.AiPoet
import com.myth.shishi.ai.PoetryStyle
import com.myth.shishi.ai.UnmappedWordException
import kotlinx.android.synthetic.main.activity_ai_poetry.*
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException

class AIPoetryActivity : AppCompatActivity() {

    companion object {
        const val SP_CONFIG_NAME = "config"
        const val SP_CONFIG_KEY_TEXT_COLOR = "textColor"

        const val DEFAULT_TEXT_COLOR = 0xFF444444
    }

    private var acrostic = false
    private var aiPoet: AiPoet? = null

    private lateinit var backgroundFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_poetry)
        initAiPoet()

        initClickListeners()
        setAcrosticStatus(true)
        et_style.setText(PoetryStyle.getRandomStyle())
        et_style.setTypeface(BaseApplication.instance.getTypeface())
        et_acrostic.setText("人工智能")
        et_acrostic.setTypeface(BaseApplication.instance.getTypeface())

        et_style.setHintTextColor(getColor(R.color.black_hint))
        et_acrostic.setHintTextColor(getColor(R.color.black_hint))

        song.performClick()
        Handler().post {
            ViewCompat.setTransitionName(iv_yz, "");
        }
        ll_option.post {
            val llHeight = ll_option.height
            ll_option.translationY = llHeight.toFloat()

            val animator = ll_option.animate().translationY(0.0f)
            animator.duration = 800
            animator.start()
        }
        backgroundFile = File(getExternalFilesDir(null), "background.jpg")
        loadBackground()
        loadTextColor()
    }

    private fun loadTextColor() {
        text.textColor = getSharedPreferences(SP_CONFIG_NAME, Context.MODE_PRIVATE)
            .getInt(SP_CONFIG_KEY_TEXT_COLOR, DEFAULT_TEXT_COLOR.toInt())

    }

    private fun loadBackground() {
        if (backgroundFile.isFile) {
            val bitmap = BitmapFactory.decodeFile(backgroundFile.absolutePath)
            bitmap?.let{
                rl_card.background = BitmapDrawable(resources, bitmap)
            }
        }
    }

    private fun initClickListeners() {
        cl_root.setOnClickListener {
            clearEditFocus()
        }
        song.setOnClickListener {
            val acrosticStr = et_acrostic.text.toString()
            val styleStr = et_style.text.toString()
            if (acrostic && acrosticStr.isBlank()) {
                toast("请输入藏头词")
                return@setOnClickListener
            }
            if (styleStr.isBlank()) {
                toast("请输入作诗风格")
                return@setOnClickListener
            }
            try {
                text.text = "${aiPoet?.song(if (acrostic) acrosticStr else "", styleStr, acrostic)}"
            } catch (e: UnmappedWordException) {
                toast("遇到了无法映射的文字：${e.word}")
            }
        }
        tv_random.setOnClickListener {
            et_style.setText(PoetryStyle.getRandomStyle())
            val animator = tv_random.animate().rotationBy(180.0f)
            animator.duration = 500
            animator.start()
        }
        tv_normal.setOnClickListener {
            setAcrosticStatus(false)
        }
        tv_acrostic.setOnClickListener {
            setAcrosticStatus(true)
        }
    }

    private fun clearEditFocus() {
        et_style.clearFocus()
        et_acrostic.clearFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_style.windowToken, 0)
    }

    private fun setAcrosticStatus(acrostic: Boolean) {
        this.acrostic = acrostic
        if (acrostic) {
            til_acrostic.visibility = View.VISIBLE
            setActiveTextView(tv_acrostic, tv_normal)
        } else {
            til_acrostic.visibility = View.GONE
            setActiveTextView(tv_normal, tv_acrostic)
        }
        clearEditFocus()
    }

    private fun setActiveTextView(tvPositive: TextView?, tvNegative: TextView?) {
        tvPositive?.setCompoundDrawablesWithIntrinsicBounds(
            resources.getDrawable(R.drawable.point_red, theme),
            null, null, null
        )
        tvPositive?.setTextColor(Color.parseColor("#ffffff"))
        tvNegative?.setCompoundDrawablesWithIntrinsicBounds(
            resources.getDrawable(R.drawable.point_gray, theme),
            null, null, null
        )
        tvNegative?.setTextColor(getColor(R.color.black_hint))
    }

    private fun initAiPoet() {
        aiPoet = try {
            AiPoet(this);
        } catch (e: IOException) {
            null
        }
    }


}
