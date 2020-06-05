package kr.ac.kpu.game.andgp.bugjl0995.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class GameObject {
    private static final int TILE_WIDTH = 100;
    private static final int TILE_HEIGHT = 150;
    private static final String TAG = GameObject.class.getSimpleName();

    private Bitmap bitmap;
    private final Rect borderRect;
    private Paint paint;
    private int paintStrokeWidth;

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
        this.paintStrokeWidth = 10;
        paint.setStrokeWidth(paintStrokeWidth);
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

    private boolean touchesImage(int x, int y){
        if((this.x + TILE_WIDTH < x) || (x < this.x))
            return false;
        if((this.y + TILE_HEIGHT < y) || (y < this.y))
            return false;
//        Log.d(TAG, "It's True!");
        return true;
    }

    public void selectImage(){
        this.paint.setColor(Color.RED);
        paintStrokeWidth = 20;
        this.paint.setStrokeWidth(paintStrokeWidth);
    }
    public void unselectImage(){
        this.paint.setColor(Color.BLACK);
        paintStrokeWidth = 10;
        this.paint.setStrokeWidth(paintStrokeWidth);
    }

    public void onTouchEvent(MotionEvent event, GameView currentView){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                int downX = (int)event.getX();
                int downY = (int)event.getY();
//                Log.d(TAG, "MouseButtonDown! : (" + downX + ", " + downY + ") ");
                if(touchesImage(downX, downY))
                    currentView.setCurrentTile(this);
            default:
                break;
        }
    }
}