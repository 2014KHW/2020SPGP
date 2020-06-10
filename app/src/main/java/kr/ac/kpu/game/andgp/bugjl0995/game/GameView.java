package kr.ac.kpu.game.andgp.bugjl0995.game;

import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.util.Pair;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

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
    static private ArrayList<Pair<GameObject, GameObject>> tileDestroyable = new ArrayList<>();
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
                getDestroyableTiles();
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
                Log.d(TAG, "my Destroyable Tile List : " + tileDestroyable.size() / 2);

                int downX = (int)event.getX();
                int downY = (int)event.getY();
//                Log.d(TAG, "MouseButtonDown! : (" + downX + ", " + downY + ") ");
                int downIndexX = downX / (windowWidth / MAX_ROW);
                int downIndexY = downY / (windowHeight / MAX_COLUMN);
//                Log.d(TAG, "MouseButtonDown! Index : (" + downIndexX + ", " + downIndexY + ") ");
                HashMap<Integer, GameObject> columnTileMap = tileObjectMap.get(downIndexX);

                if(columnTileMap == null){
                    if(selectedTile != null)
                        selectedTile.unselectImage();
                    selectedTile = null;
                    break;
                }
                GameObject currentTile = columnTileMap.get(downIndexY);

                if(selectedTile != null){
                    ArrayList<Point> findResult = Destroyable(currentTile.getX(), currentTile.getY(),
                                                                   selectedTile.getX(), selectedTile.getY(),
                                                                   null, null, 0);
                    Log.d(TAG, "findResult : " + findResult);
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

                for(int dstY = 0; dstY < MAX_COLUMN; dstY++){
                    for(int dstX = 0; dstX < MAX_ROW; dstX++) {
                        GameObject dstTile = tileFind(dstX, dstY);
                        if(dstTile == null)
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

    public void selectDestroy(GameObject currentObject, GameObject prevObject){

    }
}
