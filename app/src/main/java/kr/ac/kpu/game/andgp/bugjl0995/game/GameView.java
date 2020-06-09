package kr.ac.kpu.game.andgp.bugjl0995.game;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class GameView extends View {
    private static final String TAG = GameView.class.getSimpleName();
    static private ArrayList<GameObject> tiles = new ArrayList<>();
    private final int MAX_ROW = 10;
    private final int MAX_COLUMN = 9;
    private int windowWidth;
    private int windowHeight;

//    static private HashMap<Integer, HashMap<Integer, GameObject>>  tileObjectMap = new HashMap<>();
    static private int selectedTileIndex;

    public GameView(Context context) {
        super(context);
        postFrameCallback();

        WindowManager wm = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
        Point windowSize = new Point();
        wm.getDefaultDisplay().getSize(windowSize);
        windowWidth = windowSize.x;
        windowHeight = windowSize.y;

        selectedTileIndex = -1;
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
            o.onTouchEvent(event, this);
        }
//        return super.onTouchEvent(event);
        return false;
    }

    public void addTile(GameObject gameObject){
        tiles.add(gameObject);
    }
    public void setCurrentTile(GameObject gameObject){
        if(selectedTileIndex != -1)
            tiles.get(selectedTileIndex).unselectImage();
        selectedTileIndex = tiles.indexOf(gameObject);
//        Log.d(TAG, "selected Image Index : " + selectedTileIndex);
        if(selectedTileIndex != -1)
            gameObject.selectImage();
    }

    public void selectDestroy(){

    }
}
