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
        setContentView(R.layout.activity_main);
        GameView gameView = findViewById(R.id.gameScreenView);

        resources = getResources();

        int tileWidth = gameView.windowWidth / gameView.MAX_ROW;
        int tileHeight = gameView.windowHeight / gameView.MAX_COLUMN;
        gameView.addTile(new GameObject(resources, R.mipmap.crocodile, 1, 1, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.crocodile, 2, 1, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.cameleon, 2, 0, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.cameleon, 2, 2, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.deer, 1, 2, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.deer, 3, 2, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.deer, 0, 2, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.deer, 0, 1, tileWidth, tileHeight));

        gameView.addTile(new GameObject(resources, R.mipmap.duck, 4, 4, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.duck, 5, 7, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.lion, 7, 6, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.lion, 6, 6, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.cameleon, 5, 6, tileWidth, tileHeight));
        gameView.addTile(new GameObject(resources, R.mipmap.cameleon, 5, 1, tileWidth, tileHeight));

        gameView.getDestroyableTiles();
//        GameObject go = new GameObject(getResources(), R.mipmap.crocodile, 0, 0);
//        go.draw(canvas);
    }
}
