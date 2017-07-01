package fr.devloop.compteursalonlego.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by jerom on 01/07/2017.
 */

public class AmaticBoldTextView extends android.support.v7.widget.AppCompatTextView {

    public AmaticBoldTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public AmaticBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public AmaticBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/Amatic-Bold.ttf", context);
        setTypeface(customFont);
    }
}