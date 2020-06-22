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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Random;

class Descending implements Comparator<Integer>{
    public int compare(Integer a, Integer b){
        return b.compareTo(a);
    }
}

public class GameView extends View {
    private static final String TAG = GameView.class.getSimpleName();
    //    static private ArrayList<GameObject> tiles = new ArrayList<>();
    public final int MAX_ROW = 10;
    public final int MAX_COLUMN = 9;
    public TextView hInterface = null;
    public int windowWidth;
    public int windowHeight;

    public static int destroyCombo = 0;
    private long comboTimeNanos;
    private boolean updateTime;
    private int mouseX, mouseY;
    private Paint comboTextPaint;
    public static boolean destroyTile = false;
    private boolean allDestroyedTiles = false;

    static private HashMap<Integer, HashMap<Integer, GameObject>>  tileObjectMap = new HashMap<>();
    static private ArrayList<Pair<GameObject, GameObject>> tileDestroyable = new ArrayList<>();
    static private GameObject selectedTile;
    private long timeLimit; // 제한시간
    private long currentTimeNanos;

    private int score;
    private ArrayList<Integer> highscores = new ArrayList<>();

    private GameState gameState;
    static private ArrayList<Bitmap> gameStateBitmap = new ArrayList<>();

    private ArrayList<ImageButton> itemList = new ArrayList<>();

    enum GameState{menu, gaming, gameover, gameclear}

    private void initResources() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
        Point windowSize = new Point();
        wm.getDefaultDisplay().getSize(windowSize);
//        GameView windowSize = findViewById(R.id.gameScreenView);
        windowWidth = windowSize.x;
        windowHeight = windowSize.y * 5 / 6;
//        Log.d(TAG, "windowWidth , windowHeight : " + windowWidth + ", " + windowHeight);

        selectedTile = null;
        updateTime = true;
        comboTextPaint = new Paint();
        timeLimit = (long)1000000000 * 100;

        score = 0;
        gameState = GameState.menu;
        if(gameStateBitmap.size() == 0){
            gameStateBitmap.add(BitmapFactory.decodeResource(getResources(), R.mipmap.start_message));
            gameStateBitmap.add(BitmapFactory.decodeResource(getResources(), R.mipmap.gameover_message));
            gameStateBitmap.add(BitmapFactory.decodeResource(getResources(), R.mipmap.gameclear_message));
        }

        postFrameCallback();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initResources();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        switch (gameState){
            case menu:
                onDrawMenu(canvas);
                break;
            case gaming:
                onDrawGaming(canvas);
                break;
            case gameclear:
                onDrawGameclear(canvas);
                break;
            case gameover:
                onDrawGameover(canvas);
                break;
        }
    }

    private void onDrawGameclear(Canvas canvas) {
        Bitmap gameclearBitmap = gameStateBitmap.get(2);
        Rect srcRect = new Rect(0, 0,
                gameclearBitmap.getWidth(), gameclearBitmap.getHeight());
        Rect dstRect = new Rect(0, 0, windowWidth, windowHeight);
        canvas.drawBitmap(gameclearBitmap, srcRect, dstRect, null);
    }

    private void onDrawGameover(Canvas canvas) {
        Bitmap gameoverBitamp = gameStateBitmap.get(1);
        Rect srcRect = new Rect(0, 0,
                gameoverBitamp.getWidth(), gameoverBitamp.getHeight());
        Rect dstRect = new Rect(0, 0, windowWidth, windowHeight);
        canvas.drawBitmap(gameoverBitamp, srcRect, dstRect, null);
    }

    private void onDrawMenu(Canvas canvas) {
        Bitmap startBitamp = gameStateBitmap.get(0);
        Rect srcRect = new Rect(0, 0,
                startBitamp.getWidth(), startBitamp.getHeight());
        Rect dstRect = new Rect(0, 0, windowWidth, windowHeight);
        canvas.drawBitmap(startBitamp, srcRect, dstRect, null);
    }

    private void onDrawGaming(Canvas canvas) {
        if (tileObjectMap.size() <= 0)
            return;

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

        if(selectedTile != null)
            selectedTile.draw(canvas, this);

        if(destroyCombo > 0 && destroyTile){
            comboTextPaint.setColor(Color.RED);
            comboTextPaint.setTextSize(30);
            canvas.drawText("Combo : " + destroyCombo, mouseX, mouseY, comboTextPaint);
            Log.d(TAG, "Combo : " + destroyCombo);
        }

        if(timeLimit >= 0){
//            Bitmap timeLimitBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timelimit_bar);
            BitmapDrawable bd = (BitmapDrawable)getResources().getDrawable(R.drawable.timelimit_bar);
            Bitmap timeLimitBitmap = bd.getBitmap();

            int resultsrcRect = (int) (timeLimitBitmap.getWidth() * ((double)timeLimit / ((double)1000000000 * 100)));
            int resultdstRect = (int) (windowWidth * ((double)timeLimit / ((double)1000000000 * 100)));

            Rect timeSrcRect = new Rect(0, 0,
                    resultsrcRect,
                    timeLimitBitmap.getHeight());

            Rect timeDstRect = new Rect(0, 0,
                    resultdstRect,
                    20);
//            Log.d(TAG, "bitmap, src, dst : " + timeLimitBitmap + ", " + timeSrcRect + ", " + timeDstRect);

            canvas.drawBitmap(timeLimitBitmap, timeSrcRect, timeDstRect, null);
        }

        if(hInterface != null){
            hInterface.setTextColor(Color.YELLOW);
            hInterface.setText("Score : " + this.score);
//            Log.d(TAG, "Score : " + this.score);
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

        if(gameState == GameState.gaming){
            updateGaming(frameTimeNanos);
        }
        else{
            timeLimit = (long)1000000000 * 100;
        }
    }

    private void updateGaming(long frameTimeNanos) {
        allDestroyedTiles = true;
        for(int y = 0; y < MAX_COLUMN; y++){
            for(int x = 0; x < MAX_ROW; x++){
                final GameObject currentTile = tileFind(x, y);
                if(currentTile == null)
                    continue;
                allDestroyedTiles = false;
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
        if(timeLimit < 0){
//            Log.d(TAG, "GameOver!!");
            gameState = GameState.gameover;
            return;
        }
        currentTimeNanos = frameTimeNanos;

        if(frameTimeNanos - comboTimeNanos > (long)1000000000 * 5){
            Log.d(TAG, "frameTimeNanos - FrameNanosCombo = " + (frameTimeNanos - comboTimeNanos) + " Combo destroyed");
            destroyCombo = 0;
            comboTimeNanos = frameTimeNanos;
        }

        if(frameTimeNanos - comboTimeNanos > (long)1500000000){
            destroyTile = false;
        }

        if(tileDestroyable.size() == 0 && allDestroyedTiles){
            gameState = GameState.gameclear;
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
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                switch (gameState){
                    case menu:
                        onTouchMenu(event);
                        break;
                    case gaming:
                        onTouchGaming(event);
                        for(ImageButton ib : itemList){
                            ib.onTouchEvent(event);
                        }
                        break;
                    case gameclear:
                        onTouchGameclear(event);
                        break;
                    case gameover:
                        onTouchGameover(event);
                        break;
                }
                break;
            default:
                break;
        }

        return false;
    }

    private void onTouchGameclear(MotionEvent event) {
        gameState = GameState.menu;
        addHighscore(score);
        tileObjectMap = new HashMap<>();
        tileDestroyable = new ArrayList<>();
        setTiles();
        getDestroyableTiles();
    }

    private void addHighscore(int score) {
        if(highscores.size() < 10){
            highscores.add(score);
        }
        else{
            highscores.remove(9);
            highscores.add(score);
        }
        Collections.sort(highscores, new Descending());
    }


    private void onTouchGameover(MotionEvent event) {
        gameState = GameState.menu;
        tileObjectMap = new HashMap<>();
        tileDestroyable = new ArrayList<>();
        setTiles();
        getDestroyableTiles();
    }

    private void onTouchMenu(MotionEvent event) {
        gameState = GameState.gaming;
    }

    private void onTouchGaming(MotionEvent event) {
        Log.d(TAG, "my Destroyable Tile List : " + tileDestroyable.size() / 2);

        int downX = (int)event.getX();
        int downY = (int)event.getY();
        int downIndexX = downX / (windowWidth / MAX_ROW);
        int downIndexY = downY / (windowHeight / (MAX_COLUMN + 1));
        Log.d(TAG, "MouseButtonDown! Index : (" + downIndexX + ", " + downIndexY + ") ");

        GameObject currentTile = tileFind(downIndexX, downIndexY);

        if(currentTile == null){
            if(selectedTile != null){
                selectedTile.unselectImage();
                timeLimit -= (long)1000000000 * 5;
            }
            selectedTile = null;
        }
        else{
            boolean forceDestroy = false;
            if(destroyCombo > 0 && destroyCombo % 5 == 0){
//                        Log.d(TAG, " " + destroyCombo % 5);
                for(int y = 0; y < MAX_COLUMN; y++){
                    for(int x = 0; x < MAX_ROW; x++){
                        GameObject sameWithCurrentTile = tileFind(y, x);
                        if(sameWithCurrentTile == null)continue;
                        if(sameWithCurrentTile.getResourceId() != currentTile.getResourceId())continue;
                        if(sameWithCurrentTile.getX() == currentTile.getX() && sameWithCurrentTile.getY() == currentTile.getY())continue;
                        currentTile.setStatus(GameObject.Status.destroyed);
                        sameWithCurrentTile.setStatus(GameObject.Status.destroyed);
                        destroyCombo++;
                        this.score += 100 * destroyCombo;
                        updateTime = true;
                        forceDestroy = true;
                        break;
                    }
                    if(forceDestroy)
                        break;
                }
                if(forceDestroy)
                    return;
            }

            if(tileDestroyable.contains(new Pair<GameObject, GameObject>(currentTile, selectedTile))){
                currentTile.setStatus(GameObject.Status.destroyed);
                selectedTile.setStatus(GameObject.Status.destroyed);
                selectedTile = null;
                mouseX = downX; mouseY = downY;
                destroyCombo++;
                this.score += 100 * destroyCombo;
                destroyTile = true;
                updateTime = true;
                return;
            }
            else{
                if(selectedTile != null){
                    selectedTile.unselectImage();
                    timeLimit -= (long)1000000000 * 5;
                }
            }
            currentTile.selectImage();
            if(currentTile.getStatus() != GameObject.Status.dead &&
               currentTile.getStatus() != GameObject.Status.destroyed)
                currentTile.setStatus(GameObject.Status.normal);
            if(currentTile.getStatus() == GameObject.Status.normal)
                selectedTile = currentTile;
        }
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

    public void addItem(final ImageButton item){

        itemList.add(item);

        item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                useItem(itemList.indexOf(item));
            }
        });
    }

    private void useItem(int index) {
        switch (index){
            case 0:{
                GameObject randomTile = null;
                while(randomTile == null){
                    Random randNum = new Random(System.currentTimeMillis());
                    int x = randNum.nextInt(MAX_COLUMN);
                    int y = randNum.nextInt(MAX_ROW);
                    randomTile = tileFind(x, y);
                }
                ArrayList<GameObject> sameTiles = findSamePictureTile(randomTile);
                Random randNum = new Random(System.currentTimeMillis());
                int one = randNum.nextInt(sameTiles.size());
                int two = one;
                while(one == two){
                    two = randNum.nextInt(sameTiles.size());
                }
                sameTiles.get(one).setStatus(GameObject.Status.found);
                sameTiles.get(one).unselectImage();
                sameTiles.get(two).setStatus(GameObject.Status.found);
                sameTiles.get(one).unselectImage();
                if(selectedTile == sameTiles.get(one) || selectedTile == sameTiles.get(two))
                    selectedTile = null;
                break;}
            case 1:{
                GameObject randomTile = null;
                while(randomTile == null){
                    Random randNum = new Random(System.currentTimeMillis());
                    int x = randNum.nextInt(MAX_COLUMN);
                    int y = randNum.nextInt(MAX_ROW);
                    randomTile = tileFind(x, y);
                }
                ArrayList<GameObject> sameTiles = findSamePictureTile(randomTile);
                Random randNum = new Random(System.currentTimeMillis());
                int one = randNum.nextInt(sameTiles.size());
                int two = one;
                while(one == two){
                    two = randNum.nextInt(sameTiles.size());
                }
                sameTiles.get(one).setStatus(GameObject.Status.destroyed);
                sameTiles.get(two).setStatus(GameObject.Status.destroyed);
                break;}
            case 2:{
                ArrayList<GameObject> allTiles = getAllTiles();
                int i = 0;
                Random randNum = new Random(System.currentTimeMillis());
                while( i++ <= 100 ){
                    int srcIndex = randNum.nextInt(allTiles.size());
                    int dstIndex = randNum.nextInt(allTiles.size());
                    int srcRes = allTiles.get(srcIndex).getResId();
                    int dstRes = allTiles.get(dstIndex).getResId();
                    allTiles.get(srcIndex).setResId(dstRes);
                    allTiles.get(dstIndex).setResId(srcRes);
                }
                tileDestroyable = new ArrayList<>();
                getDestroyableTiles();
                break;}
        }

        destroyCombo = 0;
    }

    private ArrayList<GameObject> getAllTiles() {
        ArrayList<GameObject> allTiles = new ArrayList<>();
        for(int y = 0; y < MAX_COLUMN; y++){
            for(int x = 0; x < MAX_ROW; x++){
                GameObject currentTile = tileFind(x, y);
                if(currentTile != null)
                    allTiles.add(currentTile);
            }
        }
        return allTiles;
    }

    private ArrayList<GameObject> findSamePictureTile(GameObject randomTile) {
        ArrayList<GameObject> samePictures  = new ArrayList<>();
        samePictures.add(randomTile);
        for(int y = 0; y < MAX_COLUMN; y++){
            for(int x = 0; x < MAX_COLUMN; x++){
                GameObject currentTile = tileFind(x, y);
                if(currentTile == null)continue;
                if(currentTile.getX() == randomTile.getX() &&
                currentTile.getY() == randomTile.getY())continue;
                if(currentTile.getResourceId() == randomTile.getResourceId())
                    samePictures.add(currentTile);
            }
        }
        return samePictures;
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
                if(srcTile.getStatus() == GameObject.Status.destroyed
                        || srcTile.getStatus() == GameObject.Status.dead)
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

    public void setTiles(){
        Random random = new Random(System.currentTimeMillis());
        ArrayList<Integer> initPositions = new ArrayList<>();
        int maxSize = MAX_ROW * MAX_COLUMN;
        while(initPositions.size() < maxSize * 2 / 3){
            int nextPosition = random.nextInt(maxSize);
            while(initPositions.contains(nextPosition)){
                nextPosition = random.nextInt(maxSize);
            }
            int anotherNextPosition = random.nextInt(maxSize);
            while(initPositions.contains(anotherNextPosition) || anotherNextPosition == nextPosition){
                anotherNextPosition = random.nextInt(maxSize);
            }

            initPositions.add(nextPosition);
            initPositions.add(anotherNextPosition);
        }

        int mipmapIndex = random.nextInt(MainActivity.mipmaps.size());
        int even = 0;
        for(int i : initPositions){
            if(even == 0){
                mipmapIndex = random.nextInt(MainActivity.mipmaps.size());
            }
            int tileWidth = windowWidth / MAX_ROW;
            int tileHeight = windowHeight / (MAX_COLUMN + 1);
            Log.d(TAG, "new gameObject position is : " + i % MAX_ROW + ", " + i / MAX_ROW);
            addTile(new GameObject(getResources(), MainActivity.mipmaps.get(mipmapIndex),
                    i % MAX_ROW, i / MAX_ROW, tileWidth, tileHeight));
            even = (even + 1) % 2;
        }
    }
}
