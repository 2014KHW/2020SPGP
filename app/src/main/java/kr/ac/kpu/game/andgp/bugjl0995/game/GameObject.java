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
    private static final String TAG = GameObject.class.getSimpleName();

    private Bitmap bitmap;
    private final Rect borderRect;
    private Paint paint;
    private int paintStrokeWidth;

    private int x;
    private int y;
    private int rectX;
    private int rectY;
    private int resId;

    GameObject(Resources resources, int resId, int x, int y, int width, int height){
        this.bitmap = BitmapFactory.decodeResource(resources, resId);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        this.x = x;
        this.y = y;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        this.paintStrokeWidth = 10;
        paint.setStrokeWidth(paintStrokeWidth);
        paint.setAntiAlias(true);
        this.paint = paint;
        this.resId = resId;

        Bitmap borderBitmap = Bitmap.createBitmap(
                bitmap.getWidth() + 10,
                bitmap.getHeight() + 10,
                Bitmap.Config.ARGB_8888
        );

        this.rectX = x * width;
        this.rectY = y * height;
        this.borderRect = new Rect(rectX + 5, rectY + 5,
                rectX + borderBitmap.getWidth() - 5,
                rectY + borderBitmap.getHeight() - 5);
    }

    void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, rectX, rectY, null);
        canvas.drawRect(borderRect, paint);
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

//    public void onTouchEvent(MotionEvent event, GameView currentView){
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN :
//                int downX = (int)event.getX();
//                int downY = (int)event.getY();
//                Log.d(TAG, "MouseButtonDown! : (" + downX + ", " + downY + ") ");
//                if(touchesImage(downX, downY))
//                    currentView.setCurrentTile(this);
//            default:
//                break;
//        }
//    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getResourceId() {return resId;}
}
