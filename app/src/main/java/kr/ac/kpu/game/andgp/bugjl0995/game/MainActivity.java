package kr.ac.kpu.game.andgp.bugjl0995.game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Resources resources;

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameView gameView = new GameView(this );
        setContentView(gameView);

        resources = getResources();

        int tileWidth = gameView.windowWidth / gameView.MAX_ROW;
        int tileHeight = gameView.windowHeight / gameView.MAX_COLUMN;
        gameView.addTile(new GameObject(resources, R.mipmap.crocodile, 1, 1, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.crocodile, 2, 1, tileWidth, tileHeight));
//        GameObject go = new GameObject(getResources(), R.mipmap.crocodile, 0, 0);
//        go.draw(canvas);
    }
}
