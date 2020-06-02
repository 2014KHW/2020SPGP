package kr.ac.kpu.game.andgp.bugjl0995.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GameView extends View {
    static private ArrayList<GameObject> tiles = new ArrayList<>();

    public GameView(Context context) {
        super(context);
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

    public void addTile(GameObject gameObject){
        tiles.add(gameObject);
    }
}
