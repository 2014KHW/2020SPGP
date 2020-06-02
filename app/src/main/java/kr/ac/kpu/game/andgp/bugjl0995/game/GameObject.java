package kr.ac.kpu.game.andgp.bugjl0995.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameObject {
    private static final int TILE_WIDTH = 100;
    private static final int TILE_HEIGHT = 150;

    private Bitmap bitmap;
    private final Rect borderRect;
    private Paint paint;

    private int x;
    private int y;

    GameObject(Resources resources, int resId, int x, int y){
        this.bitmap = BitmapFactory.decodeResource(resources, resId);
        bitmap = Bitmap.createScaledBitmap(bitmap, TILE_WIDTH, TILE_HEIGHT, true);

        this.x = x;
        this.y = y;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        this.paint = paint;

        Bitmap borderBitmap = Bitmap.createBitmap(
                bitmap.getWidth() + 10,
                bitmap.getHeight() + 10,
                Bitmap.Config.ARGB_8888
        );

        this.borderRect = new Rect(x + 5, y + 5,
                x + borderBitmap.getWidth() - 5,
                y + borderBitmap.getHeight() - 5);
    }

    void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);
        canvas.drawRect(borderRect, paint);
    }
}
