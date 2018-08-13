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

    private ButtonStyle selectedStyle = ButtonStyle.ORIGINAL;
    private ButtonLanguage selectedLanguage;

    private enum ButtonStyle{
        ORIGINAL, LIGHT, DARK
    }

    private enum ButtonLanguage{
        ENGLISH, SPANISH
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
        int  defaultLanguage = (getResources().getString(R.string.default_phone_language_code).
                equalsIgnoreCase(SPANISH_LANGUAGE_CODE)) ? ButtonLanguage.SPANISH.ordinal():
                ButtonLanguage.ENGLISH.ordinal();

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PayButton);

        selectedStyle = ButtonStyle.values()[typedArray.getInt(R.styleable.PayButton_style,
                selectedStyle.ordinal())];

        selectedLanguage = ButtonLanguage.values()[typedArray.getInt(R.styleable.PayButton_language,
                defaultLanguage)];

        typedArray.recycle();
    }
    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        switch (selectedStyle) {
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
            case ENGLISH:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_white));
                break;
            case SPANISH:
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
            case ENGLISH:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_black));
                break;
            case SPANISH:
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
            case ENGLISH:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_white));
                break;
            case SPANISH:
                setImageDrawable(getResources().getDrawable(R.drawable.athm_white_es));
                break;
        }
        ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(getResources().
                getColor(R.color.dark_button)));
    }
}