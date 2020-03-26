package kr.ac.kpu.game.andgp.bugjl0995.game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Camel Case

        // UpperCamelCase : 클래스 이름
        // LowerCamelCase : 함수, 변수 이름
        // 모두 대문자 : 상수
        // 약어를 쓰는 경우 : 내용이 몇 줄 안될 정도로 간단할 경우
        // Git Project

        Log.d(TAG, "Message from onCreate()");
        TextView tv = findViewById(R.id.textView3);
        tv.setText("Launched");
    }

    public void onBtnFirst(View v) {
        Log.d(TAG, "onBtnFirst()");
        TextView tv = findViewById(R.id.textViewMessage);
        tv.setText("First Button Pressed");

        ImageView iv = findViewById(R.id.catImageView);
        iv.setImageResource(R.mipmap.cat1);
    }

    public void onBtnSecond(View view) {
        ImageView iv = findViewById(R.id.catImageView);
        iv.setImageResource(R.mipmap.cat2);

        Random random = new Random();
        final int value = random.nextInt(100) + 1;

        final TextView tv = findViewById(R.id.textViewMessage);
        tv.setText("Random number: " + value);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setText("Timer has changed: " + (value + 100));
            }
        }, 1000);
    }

    public void onBtnThird(View view) {
        TextView tv = findViewById(R.id.textView);
        int count = 0;
        try{
            count = Integer.parseInt((String) tv.getText());
        }catch(Exception e){
        }
        count++;
        tv.setText(String.valueOf(count));

        new AlertDialog.Builder(this)
                .setTitle("Hello")
                .setMessage("World")
                .setPositiveButton("Hahaha", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView tv = findViewById(R.id.textViewMessage);
                        tv.setText("Hahaha Dialog Button Pressed");
                    }
                })
                .setNegativeButton("Nooooo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView tv = findViewById(R.id.textViewMessage);
                        tv.setText("Nooooo Dialog Button Pressed");
                    }
                })
                .create()
                .show();
    }
}
