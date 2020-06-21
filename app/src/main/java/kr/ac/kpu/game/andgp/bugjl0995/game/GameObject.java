package kr.ac.kpu.game.andgp.bugjl0995.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

public class GameObject {
    private static final String TAG = GameObject.class.getSimpleName();
    public static final int PADDING_FOR_SINK = 15;
    private final Resources resources;

    private Bitmap bitmap;
    private final Rect borderRect;
    private Paint paint;
    private int paintStrokeWidth;

    private int x;
    private int y;
    private int w;
    private int h;
    private int rectX;
    private int rectY;
    private int resId;
    private long twinkleFrame;

    private int frame = -1;
    static private ArrayList<Bitmap> destroyAnimSheet = null;
    static private int animResId;

    private long frameNanos;
    private boolean firstUpdate = true;

    private Status status;

    public void update(long frameTimeNanos) {
        if(firstUpdate){
            frameNanos = frameTimeNanos;
            twinkleFrame = frameTimeNanos;
            firstUpdate = false;
        }

        if(this.status == Status.destroyed){
//            Log.d(TAG, "frameTime : " + (frameTimeNanos - frameNanos));
            if(frameTimeNanos - frameNanos > 50000000){
                if(frame == 5)
                    this.status = Status.dead;
                frame = (frame + 1) % 6;
                frameNanos = frameTimeNanos;
            }
        }

        if(this.status == Status.found){
            if(frameTimeNanos - twinkleFrame > 50000000){
                if(this.paint.getColor() == Color.CYAN){
                    this.paint.setColor(Color.DKGRAY);
                }
                else if(this.paint.getColor() == Color.RED){
                    status = Status.normal;
                }
                else{
                    this.paint.setColor(Color.CYAN);
                }
                twinkleFrame = frameTimeNanos;
            }
        }
    }

    public void setResId(int resId){
        this.resId = resId;
        this.bitmap = BitmapFactory.decodeResource(resources, resId);
        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
    }
    public int getResId(){
        return this.resId;
    }

    public Status getStatus() {
        return this.status;
    }

    public enum Status{normal, found, destroyed, dead, END}
    GameObject(Resources resources, int resId, int x, int y, int width, int height){
        this.resources = resources;
        this.bitmap = BitmapFactory.decodeResource(resources, resId);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;

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
        this.borderRect = new Rect(rectX + 5, rectY + 5 - PADDING_FOR_SINK,
                rectX + borderBitmap.getWidth() - 5,
                rectY + borderBitmap.getHeight() - 5 - PADDING_FOR_SINK);

        this.status = Status.normal;
        initAnimation(resources);
    }

    void initAnimation(Resources resources){
        this.frame = 6;
        if(destroyAnimSheet == null){
            animResId = R.mipmap.firework;
            Bitmap destroySprite = BitmapFactory.decodeResource(resources, animResId);
            destroyAnimSheet = new ArrayList<>();
            for(int i = 0; i < 6; i++){
                Bitmap frameBitmap = Bitmap.createBitmap(destroySprite, i * 50, 0, 50, 50);
                frameBitmap = Bitmap.createScaledBitmap(frameBitmap, 150, 150, false);
                destroyAnimSheet.add(frameBitmap);
            }
        }
    }

    void draw(Canvas canvas, GameView gameView){
        canvas.drawBitmap(bitmap, rectX, rectY - PADDING_FOR_SINK, null);
        canvas.drawRect(borderRect, paint);

        int tileWidth = gameView.windowWidth / gameView.MAX_ROW;
        int tileHeight = gameView.windowHeight / gameView.MAX_COLUMN;
        if(frame >= 0 && frame < 6){
            Bitmap currentBitmap = destroyAnimSheet.get(frame);
            canvas.drawBitmap(currentBitmap,
                    rectX + tileWidth / 2 - currentBitmap.getWidth() / 2,
                    rectY + tileHeight / 2 - currentBitmap.getHeight() / 2,
                    null);
        }
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

    public void setStatus(Status status){
        this.status = status;
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
