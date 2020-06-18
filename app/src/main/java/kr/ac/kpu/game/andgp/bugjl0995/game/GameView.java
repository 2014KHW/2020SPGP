package kr.ac.kpu.game.andgp.bugjl0995.game;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
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
    public int windowWidth;
    public int windowHeight;

    private int destroyCombo = 0;
    private long comboTimeNanos;
    private boolean updateTime;
    private int mouseX, mouseY;
    private Paint comboTextPaint;
    private boolean destroyTile = false;

    static private HashMap<Integer, HashMap<Integer, GameObject>>  tileObjectMap = new HashMap<>();
    static private ArrayList<Pair<GameObject, GameObject>> tileDestroyable = new ArrayList<>();
    static private GameObject selectedTile;
    private long timeLimit; // 제한시간
    private long currentTimeNanos;

    public GameView(Context context) {
        super(context);
        postFrameCallback();

        initResources();
    }

    private void initResources() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
        Point windowSize = new Point();
        wm.getDefaultDisplay().getSize(windowSize);
//        GameView windowSize = findViewById(R.id.gameScreenView);
        windowWidth = windowSize.x;
        windowHeight = windowSize.y * 5 / 6;
        Log.d(TAG, "windowWidth , windowHeight : " + windowWidth + ", " + windowHeight);

        selectedTile = null;
        updateTime = true;
        comboTextPaint = new Paint();
        timeLimit = (long)1000000000 * 100;

        postFrameCallback();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initResources();
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
                currentObject.draw(canvas, this);
            }
        }

        if(destroyCombo > 0 && destroyTile){
            comboTextPaint.setColor(Color.RED);
            comboTextPaint.setTextSize(30);
            canvas.drawText("Combo : " + destroyCombo, mouseX, mouseY, comboTextPaint);
            Log.d(TAG, "Combo : " + destroyCombo);
        }

        if(timeLimit >= 0){
            Bitmap timeLimitBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.cat1);

            Rect timeSrcRect = new Rect(0, 0,
                    timeLimitBitmap.getWidth(),
                    timeLimitBitmap.getHeight());

            int result = (int) (windowWidth * ((double)timeLimit / ((double)1000000000 * 100)));

            Rect timeDstRect = new Rect(0, 0,
                    result,
                    timeLimitBitmap.getHeight());

            canvas.drawBitmap(timeLimitBitmap, timeSrcRect, timeDstRect, null);
        }
    }

    private void postFrameCallback() {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                update(frameTimeNanos);
                getDestroyableTiles();
                invalidate();
                postFrameCallback();
            }
        });
    }

    private void update(long frameTimeNanos) {

        for(int y = 0; y < MAX_COLUMN; y++){
            for(int x = 0; x < MAX_ROW; x++){
                final GameObject currentTile = tileFind(x, y);
                if(currentTile == null)
                    continue;
                if(currentTile.getStatus() == GameObject.Status.dead){
                    removePair(currentTile);
                    tileObjectMap.get(currentTile.getX()).remove(currentTile.getY());
                    continue;
                }
                currentTile.update(frameTimeNanos);
            }
        }

        if(updateTime){
            Log.d(TAG, "UpdateTime");
            comboTimeNanos = frameTimeNanos;
            currentTimeNanos = frameTimeNanos;
            updateTime = false;
        }

        timeLimit -= frameTimeNanos - currentTimeNanos;
        if(timeLimit < 0)
            Log.d(TAG, "GameOver!!");
        currentTimeNanos = frameTimeNanos;

        if(frameTimeNanos - comboTimeNanos > (long)1000000000 * 5){
            Log.d(TAG, "frameTimeNanos - FrameNanosCombo = " + (frameTimeNanos - comboTimeNanos) + " Combo destroyed");
            destroyCombo = 0;
            comboTimeNanos = frameTimeNanos;
        }

        if(frameTimeNanos - comboTimeNanos > (long)1500000000){
            destroyTile = false;
        }
    }

    private void removePair(GameObject currentTile) {
        boolean done = false;
        while(done == false){
            int loopTimes = 0;
            for(Pair<GameObject, GameObject> p : tileDestroyable){
                if(p.first == currentTile){
                    tileDestroyable.remove(p);
                    break;
                }
                if(p.second == currentTile){
                    tileDestroyable.remove(p);
                    break;
                }
                loopTimes++;
                if(tileDestroyable.size() == loopTimes){
                    done = true;
                    break;
                }
            }
            if(tileDestroyable.size() == 0)
                return;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        for(GameObject o : tiles){
//            o.onTouchEvent(event, this);
//        }

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                Log.d(TAG, "my Destroyable Tile List : " + tileDestroyable.size() / 2);
//                for(Pair<GameObject, GameObject> pair : tileDestroyable){
//                    Log.d(TAG, "src X, Y : (" + pair.first.getX() + ", " + pair.first.getY() + " )"
//                    + "\n dst X, Y : (" + pair.second.getX() + ", " + pair.second.getY() + ") ");
//                }

                int downX = (int)event.getX();
                int downY = (int)event.getY();

//                Log.d(TAG, "MouseButtonDown! : (" + downX + ", " + downY + ") ");
                int downIndexX = downX / (windowWidth / MAX_ROW);
                int downIndexY = downY / (windowHeight / MAX_COLUMN);
//                Log.d(TAG, "MouseButtonDown! Index : (" + downIndexX + ", " + downIndexY + ") ");

                GameObject currentTile = tileFind(downIndexX, downIndexY);

                if(currentTile == null){
                    if(selectedTile != null)
                        selectedTile.unselectImage();
                    selectedTile = null;
                }
                else{
                    boolean forceDestroy = false;
                    if(destroyCombo > 0 && destroyCombo % 5 == 0){
                        Log.d(TAG, " " + destroyCombo % 5);
                        for(int y = 0; y < MAX_COLUMN; y++){
                            for(int x = 0; x < MAX_ROW; x++){
                                GameObject sameWithCurrentTile = tileFind(y, x);
                                if(sameWithCurrentTile == null)continue;
                                if(sameWithCurrentTile.getResourceId() != currentTile.getResourceId())continue;
                                if(sameWithCurrentTile.getX() == currentTile.getX() && sameWithCurrentTile.getY() == currentTile.getY())continue;
                                currentTile.setStatus(GameObject.Status.destroyed);
                                sameWithCurrentTile.setStatus(GameObject.Status.destroyed);
                                destroyCombo++;
                                updateTime = true;
                                forceDestroy = true;
                                break;
                            }
                        }
                        if(forceDestroy)
                            break;
                    }

                    if(tileDestroyable.contains(new Pair<GameObject, GameObject>(currentTile, selectedTile))){
                        currentTile.setStatus(GameObject.Status.destroyed);
                        selectedTile.setStatus(GameObject.Status.destroyed);
                        selectedTile = null;
                        mouseX = downX; mouseY = downY;
                        destroyCombo++;
                        destroyTile = true;
                        updateTime = true;
                        break;
                    }
                    else{
                        if(selectedTile != null)
                            selectedTile.unselectImage();
                    }
                    currentTile.selectImage();
                    if(currentTile.getStatus() == GameObject.Status.normal)
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

    enum Direction {UP, LEFT, DOWN, RIGHT, END};
    public ArrayList<Point> Destroyable(int srcX, int srcY, int dstX, int dstY, Direction curDir, Direction prevDir , int turnCount){
        if(curDir == null && prevDir == null)
            if(srcX == dstX && srcY == dstY)
                return null;

        if(curDir != null && prevDir != null)
            if(curDir != prevDir)
                turnCount++;

        if(turnCount >= 3)
            return null;

        if(prevDir != null && prevDir != curDir){
            if((prevDir.ordinal() + curDir.ordinal()) % 2 == 0)
                return null;
        }
        if(curDir != null){
//            Log.d(TAG, "current direction : " + curDir.ordinal());
            switch (curDir.ordinal()){
                case 0://UP
                    srcY--;
                    break;
                case 1://LEFT
                    srcX--;
                    break;
                case 2://DOWN
                    srcY++;
                    break;
                case 3://RIGHT
                    srcX++;
                    break;
            }
        }

        if(srcX < 0 || srcX >= MAX_ROW)
            return null;
        if(srcY < 0 || srcY >= MAX_COLUMN)
            return null;

        GameObject currentTile = tileFind(srcX, srcY);
        if(currentTile != null)
        {
            if(srcX == dstX && srcY == dstY){
//                Log.d(TAG, "Found Path!");
                ArrayList<Point> result = new ArrayList<>();
                result.add(new Point(srcX, srcY));
                return result;
            }
            else if(curDir != null)
                return null;
        }

        if(turnCount >= 2){
            ArrayList<Point> currentPath;
            currentPath = Destroyable(srcX, srcY, dstX, dstY, curDir, curDir, turnCount);
            if(currentPath == null)
                return null;
            else{
                currentPath.add(new Point(srcX, srcY));
                return currentPath;
            }
        }
        else {

            ArrayList<Point> selectedPath = null;
            ArrayList<Point> comparePath = null;

            selectedPath = Destroyable(srcX, srcY, dstX, dstY, Direction.UP, curDir, turnCount);
            comparePath = Destroyable(srcX, srcY, dstX, dstY, Direction.LEFT, curDir, turnCount);
            if(comparePath != null){
                if(selectedPath != null)
                    selectedPath = (selectedPath.size() < comparePath.size()) ? selectedPath : comparePath;
                else
                    selectedPath = comparePath;
            }

            comparePath = Destroyable(srcX, srcY, dstX, dstY, Direction.DOWN, curDir, turnCount);
            if(comparePath != null){
                if(selectedPath != null)
                    selectedPath = (selectedPath.size() < comparePath.size()) ? selectedPath : comparePath;
                else
                    selectedPath = comparePath;
            }


            comparePath = Destroyable(srcX, srcY, dstX, dstY, Direction.RIGHT, curDir, turnCount);
            if(comparePath != null){
                if(selectedPath != null)
                    selectedPath = (selectedPath.size() < comparePath.size()) ? selectedPath : comparePath;
                else
                    selectedPath = comparePath;
            }

            if(selectedPath != null)
                selectedPath.add(new Point(srcX, srcY));
            return selectedPath;
        }
    }

    private GameObject tileFind(int x, int y){
        HashMap<Integer, GameObject> columnTile;
        columnTile = tileObjectMap.get(x);
        if(columnTile == null)
            return null;

        GameObject result;
        result = columnTile.get(y);

        return result;
    }

    public void getDestroyableTiles(){
        for(int srcY = 0; srcY < MAX_COLUMN; srcY++){
            for(int srcX = 0; srcX < MAX_ROW; srcX++){
                GameObject srcTile = tileFind(srcX, srcY);
                if(srcTile == null)
                    continue;
                if(srcTile.getStatus() != GameObject.Status.normal)
                    continue;

                for(int dstY = 0; dstY < MAX_COLUMN; dstY++){
                    for(int dstX = 0; dstX < MAX_ROW; dstX++) {
                        GameObject dstTile = tileFind(dstX, dstY);
                        if(dstTile == null)
                            continue;
                        if(dstTile.getStatus() != GameObject.Status.normal)
                            continue;
                        if(srcTile.getResourceId() != dstTile.getResourceId())
                            continue;

                        ArrayList<Point> findResult = Destroyable(srcX, srcY, dstX, dstY, null, null, 0);
                        if(findResult != null){
                            Pair pairTile = new Pair(srcTile, dstTile);
                            if(tileDestroyable.size() > 0)
                                if(tileDestroyable.contains(pairTile))
                                    continue;
                            tileDestroyable.add(new Pair(srcTile, dstTile));
                            tileDestroyable.add(new Pair(dstTile, srcTile));
                        }
                    }
                }
            }
        }
    }
}
