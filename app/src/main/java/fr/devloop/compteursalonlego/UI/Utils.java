package fr.devloop.compteursalonlego.UI;

import android.content.res.Resources;

/**
 * Created by jeromedemonchaux on 16/06/2017.
 */

public final class Utils {

    private Utils() {
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
