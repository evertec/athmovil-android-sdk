package com.evertecinc.athmovil.sdk.checkout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.widget.LinearLayout;
/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 */
public class PayButton extends AppCompatImageButton {

    final static String SPANISH_LANGUAGE_CODE = "es";
    final static float BUTTON_ELEVATION = 2;
    final static float BUTTON_HEIGHT = 60;

    private ButtonTheme selectedTheme = ButtonTheme.ORIGINAL;
    private ButtonLanguage selectedLanguage;
    private int  defaultLanguage;

    public enum ButtonTheme {
        ORIGINAL, LIGHT, DARK
    }

    public enum ButtonLanguage {
        EN, ES, DEFAULT
    }

    public PayButton(Context context) {
        super(context);
    }

    public PayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Validating initial values for the buttons
     * @param context - button context
     * @param attrs - button selected attributes
     */
    private void init(final Context context, final AttributeSet attrs) {
         defaultLanguage = (getResources().getString(R.string.default_phone_language_code).
                equalsIgnoreCase(SPANISH_LANGUAGE_CODE)) ? ButtonLanguage.ES.ordinal():
                ButtonLanguage.EN.ordinal();

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PayButton);

        selectedTheme = ButtonTheme.values()[typedArray.getInt(R.styleable.PayButton_buttonTheme,
                selectedTheme.ordinal())];

        selectedLanguage = ButtonLanguage.values()[typedArray.getInt(R.styleable.PayButton_lang,
                defaultLanguage)];

        typedArray.recycle();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        switch (selectedTheme) {
            case ORIGINAL:
                setOriginalButton();
                break;
            case LIGHT:
                setLightButton();
                break;
            case DARK:
                setDarkButton();
                break;
            default:
                setOriginalButton();
                break;
        }

        ViewCompat.setElevation(this, spToPx(getContext(), BUTTON_ELEVATION));
        setScaleType(ScaleType.CENTER_INSIDE);
        getLayoutParams().height = spToPx(getContext(), BUTTON_HEIGHT);
        getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        requestLayout();
    }
    /**
     * Helper method that converts sp value to pixels.
     */
    private int spToPx(final Context context, final float sp) {
        return Math.round(sp * context.getResources().getDisplayMetrics().scaledDensity);
    }
    /**
     * Helper method to set the attributes to the original button.
     */
    private void setOriginalButton() {
        switch (selectedLanguage) {
            case EN:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_white));
                break;
            case ES:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_white_es));
                break;
        }
        ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(getResources().
                getColor(R.color.orange_button)));
    }
    /**
     * Helper method to set the attributes to the light button.
     */
    private void setLightButton() {
        switch (selectedLanguage) {
            case EN:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_black));
                break;
            case ES:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_black_es));
                break;
        }
        ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(getResources().
                getColor(R.color.light_button)));
    }
    /**
     * Helper method to set the attributes to the dark button.
     */
    private void setDarkButton() {
        switch (selectedLanguage) {
            case EN:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_white));
                break;
            case ES:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_white_es));
                break;
        }
        ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(getResources().
                getColor(R.color.dark_button)));
    }

    public void setTheme(ButtonTheme buttonTheme){
        selectedTheme = buttonTheme;
    }

    public  void setLanguage(ButtonLanguage language){
        if(language.equals(ButtonLanguage.DEFAULT)){
            selectedLanguage = defaultLanguage == 0 ? ButtonLanguage.EN : ButtonLanguage.ES;
        } else {
            selectedLanguage = language;
        }
    }
}