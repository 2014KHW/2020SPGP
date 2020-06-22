package kr.ac.kpu.game.andgp.bugjl0995.game;

        import android.content.Context;
        import android.media.AudioAttributes;
        import android.media.AudioManager;
        import android.media.SoundPool;
        import java.util.HashMap;

public class SoundEffects {
    private Context context;
    private SoundPool soundPool;

    public static SoundEffects get(){
        if(singleton == null) {
            singleton = new SoundEffects();
        }
        return singleton;
    }
    private static SoundEffects singleton;

    public void init(Context context){
        this.context = context;
        AudioAttributes audioAttributes = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            this.soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(3)
                    .build();
        } else {
            this.soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }
    }

    private static final int[] SOUND_IDS = {
            R.raw.broken_toy, R.raw.ontouch, R.raw.combo, R.raw.crush,
    };
    private HashMap<Integer, Integer> soundIdMap = new HashMap<>();
    public void loadAll(){
        for(int resId: SOUND_IDS){
            int soundId = soundPool.load(context, resId, 1);
            soundIdMap.put(resId, soundId);
        }
    }
    public int play(int resId){
        int soundId = soundIdMap.get(resId);
        int streamId = soundPool.play(soundId, 1f, 1f, 1, 0, 1f);
        return streamId;
    }
}
