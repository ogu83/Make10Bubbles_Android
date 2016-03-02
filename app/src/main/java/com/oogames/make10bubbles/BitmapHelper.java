package com.oogames.make10bubbles;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by oguzkoroglu on 28/02/16.
 */
public class BitmapHelper {
    public static Bitmap CombineImages(Bitmap c, Bitmap s)
    {
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
    }
}
