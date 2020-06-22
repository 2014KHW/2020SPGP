package kr.ac.kpu.game.andgp.bugjl0995.game;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.res.Resources;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Resources resources;
    public static HashMap<Integer, Integer> mipmaps;

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GameView gameView = findViewById(R.id.gameScreenView);
        TextView hInterface = findViewById(R.id.highInterface);
        gameView.hInterface = hInterface;
        ImageButton itemFindglass = findViewById(R.id.lowInterface).findViewById(R.id.findglass);
        gameView.addItem(itemFindglass);
        ImageButton itemBomb = findViewById(R.id.lowInterface).findViewById(R.id.bomb);
        gameView.addItem(itemBomb);
        ImageButton itemShuffle = findViewById(R.id.lowInterface).findViewById(R.id.shuffle);
        gameView.addItem(itemShuffle);
//        findViewById(R.id.lowInterface);

        resources = getResources();
        mipmaps = initMipmapOfMap();
        gameView.setTiles();
        gameView.getDestroyableTiles();



    }

    public HashMap<Integer, Integer> initMipmapOfMap(){

        //crocodile, cameleon, deer, duck, lion, elephant, hipo, panda, racoon, zibra

        HashMap<Integer, Integer> mipmaps = new HashMap<>();

        mipmaps.put(0, R.mipmap.crocodile);
        mipmaps.put(1, R.mipmap.cameleon);
        mipmaps.put(2, R.mipmap.deer);
        mipmaps.put(3, R.mipmap.duck);
        mipmaps.put(4, R.mipmap.lion);

        mipmaps.put(5, R.mipmap.elephant);
        mipmaps.put(6, R.mipmap.hipo);
        mipmaps.put(7, R.mipmap.panda);
        mipmaps.put(8, R.mipmap.racoon);
        mipmaps.put(9, R.mipmap.zibra);

        return mipmaps;
    }
}
