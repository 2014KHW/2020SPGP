package kr.ac.kpu.game.andgp.bugjl0995.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GameView extends View {
    private static final String TAG = GameView.class.getSimpleName();
    static private ArrayList<GameObject> tiles = new ArrayList<>();

    public GameView(Context context) {
        super(context);
        postFrameCallback();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (tiles.size() <= 0)
            return;

        for(GameObject o : tiles){
            o.draw(canvas);
        }
//        Bitmap testBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.crocodile);
//        canvas.drawBitmap(testBitmap, 0, 0, null);
//        super.onDraw(canvas);
    }

    private void postFrameCallback() {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                invalidate();
                postFrameCallback();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for(GameObject o : tiles){
            o.onTouchEvent(event);
        }
//        return super.onTouchEvent(event);
        return false;
    }

    public void addTile(GameObject gameObject){
        tiles.add(gameObject);
    }
}
