package com.evertecinc.athmovil.sdk.checkout

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import kotlin.math.roundToInt

class PayButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.imageButtonStyle
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    private var selectedTheme = ButtonTheme.ORIGINAL
    private var selectedLanguage: ButtonLanguage? = null
    private var defaultLanguage: Int = 0

    enum class ButtonTheme {
        ORIGINAL, LIGHT, DARK
    }

    enum class ButtonLanguage {
        EN, ES, DEFAULT
    }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        defaultLanguage = if (resources.getString(R.string.default_phone_language_code)
                .equals(SPANISH_LANGUAGE_CODE, ignoreCase = true)
        ) {
            ButtonLanguage.ES.ordinal
        } else {
            ButtonLanguage.EN.ordinal
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PayButton)
        selectedTheme = ButtonTheme.entries[typedArray.getInt(
            R.styleable.PayButton_buttonTheme,
            selectedTheme.ordinal
        )]
        selectedLanguage = ButtonLanguage.entries[typedArray.getInt(
            R.styleable.PayButton_lang,
            defaultLanguage
        )]
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (selectedTheme) {
            ButtonTheme.ORIGINAL -> setOriginalButton()
            ButtonTheme.LIGHT -> setLightButton()
            ButtonTheme.DARK -> setDarkButton()
        }

        ViewCompat.setElevation(this, spToPx(context, BUTTON_ELEVATION).toFloat())
        scaleType = ImageView.ScaleType.CENTER_INSIDE
        layoutParams?.height = spToPx(context, BUTTON_HEIGHT)
        layoutParams?.width = LinearLayout.LayoutParams.MATCH_PARENT
        requestLayout()
    }

    private fun spToPx(context: Context, sp: Float): Int {
        return (sp * context.resources.displayMetrics.scaledDensity).roundToInt()
    }

    private fun setOriginalButton() {
        when (selectedLanguage) {
            ButtonLanguage.EN -> setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.athm_white, context.theme)
            )
            ButtonLanguage.ES -> setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.athm_white_es, context.theme)
            )
            else -> {}
        }
        ViewCompat.setBackgroundTintList(
            this,
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.orange_button, context.theme))
        )
    }

    private fun setLightButton() {
        when (selectedLanguage) {
            ButtonLanguage.EN -> setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.athm_black, context.theme)
            )
            ButtonLanguage.ES -> setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.athm_black_es, context.theme)
            )
            else -> {}
        }
        ViewCompat.setBackgroundTintList(
            this,
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.light_button, context.theme))
        )
    }

    private fun setDarkButton() {
        when (selectedLanguage) {
            ButtonLanguage.EN -> setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.athm_white, context.theme)
            )
            ButtonLanguage.ES -> setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.athm_white_es, context.theme)
            )
            else -> {}
        }
        ViewCompat.setBackgroundTintList(
            this,
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.dark_button, context.theme))
        )
    }

    fun setTheme(buttonTheme: ButtonTheme) {
        selectedTheme = buttonTheme
    }

    fun setLanguage(language: ButtonLanguage) {
        selectedLanguage = if (language == ButtonLanguage.DEFAULT) {
            if (defaultLanguage == 0) ButtonLanguage.EN else ButtonLanguage.ES
        } else {
            language
        }
    }

    companion object {
        const val SPANISH_LANGUAGE_CODE = "es"
        const val BUTTON_ELEVATION = 2f
        const val BUTTON_HEIGHT = 60f
    }
}
