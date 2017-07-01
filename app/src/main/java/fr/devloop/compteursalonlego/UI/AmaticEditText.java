package fr.devloop.compteursalonlego.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by jerom on 01/07/2017.
 */

public class AmaticEditText extends android.support.v7.widget.AppCompatEditText {

    public AmaticEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AmaticEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AmaticEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Amatic-Bold.ttf");
            setTypeface(tf);
        }
    }

}