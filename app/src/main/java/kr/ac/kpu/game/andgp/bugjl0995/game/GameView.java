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
//    static private ArrayList<GameObject> tiles = new ArrayList<>();
    public final int MAX_ROW = 10;
    public final int MAX_COLUMN = 9;
    public final int windowWidth;
    public final int windowHeight;

    static private HashMap<Integer, HashMap<Integer, GameObject>>  tileObjectMap = new HashMap<>();
    static private GameObject selectedTile;

    public GameView(Context context) {
        super(context);
        postFrameCallback();

        WindowManager wm = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
        Point windowSize = new Point();
        wm.getDefaultDisplay().getSize(windowSize);
        windowWidth = windowSize.x;
        windowHeight = windowSize.y;

        selectedTile = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (tileObjectMap.size() <= 0)
            return;

//        for(GameObject o : tiles){
//            o.draw(canvas);
//        }

        for(int y = 0; y < MAX_COLUMN; y++){
            for(int x = 0; x < MAX_ROW; x++){
                HashMap<Integer, GameObject> columnMap = tileObjectMap.get(x);
                if(columnMap == null)
                    continue;
                GameObject currentObject = columnMap.get(y);
                if(currentObject == null)
                    continue;
                currentObject.draw(canvas);
            }
        }
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
//        for(GameObject o : tiles){
//            o.onTouchEvent(event, this);
//        }

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                int downX = (int)event.getX();
                int downY = (int)event.getY();
//                Log.d(TAG, "MouseButtonDown! : (" + downX + ", " + downY + ") ");
                int downIndexX = downX / (windowWidth / MAX_ROW);
                int downIndexY = downY / (windowHeight / MAX_COLUMN);
                Log.d(TAG, "MouseButtonDown! Index : (" + downIndexX + ", " + downIndexY + ") ");
                HashMap<Integer, GameObject> columnTileMap = tileObjectMap.get(downIndexX);

                if(columnTileMap == null){
                    if(selectedTile != null)
                        selectedTile.unselectImage();
                    selectedTile = null;
                    break;
                }
                GameObject currentTile = columnTileMap.get(downIndexY);

                if(selectedTile != null){
                    selectedTile.unselectImage();
                    selectedTile = null;
                }
                if(currentTile != null){
                    currentTile.selectImage();
                    selectedTile = currentTile;
                }
            default:
                break;
        }

        return false;
    }

    public void addTile(GameObject gameObject){
//        tiles.add(gameObject);
        HashMap<Integer, GameObject> columnTileMap = tileObjectMap.get(gameObject.getX());

        if(columnTileMap == null){
            tileObjectMap.put(gameObject.getX(), new HashMap<Integer, GameObject>());
            columnTileMap = tileObjectMap.get(gameObject.getX());
        }

        GameObject oldObject = columnTileMap.get(gameObject.getY());
        if(oldObject == null)
            columnTileMap.put(gameObject.getY(), gameObject);
    }
//    public void setCurrentTile(GameObject gameObject){
//        if(selectedTileIndex != -1)
//            tiles.get(selectedTileIndex).unselectImage();
//        selectedTileIndex = tiles.indexOf(gameObject);
//        Log.d(TAG, "selected Image Index : " + selectedTileIndex);
//        if(selectedTileIndex != -1)
//            gameObject.selectImage();
//    }

    public void selectDestroy(){

    }
}
