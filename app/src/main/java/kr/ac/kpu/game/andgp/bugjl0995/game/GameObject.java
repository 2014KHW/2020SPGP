package kr.ac.kpu.game.andgp.bugjl0995.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class GameObject {
    private int resourceId;
    private Bitmap bitmap;
    private float x;
    private float y;

    GameObject(Resources resources, int resId, float x, float y){
        this.resourceId = resId;
        this.bitmap = BitmapFactory.decodeResource(resources, resId);
        this.x = x;
        this.y = y;
    }

    void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);
    }
}
