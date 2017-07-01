package fr.devloop.compteursalonlego.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jerom on 01/07/2017.
 */

public class AmaticTextView extends android.support.v7.widget.AppCompatTextView {

    public AmaticTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public AmaticTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public AmaticTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/AmaticSC-Regular.ttf", context);
        setTypeface(customFont);
    }
}