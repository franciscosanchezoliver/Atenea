package spuzi.atenea.Client.Classes;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import static spuzi.atenea.Client.Classes.Conector.bufferSounds;

/**
 * Created by spuzi on 23/03/2017.
 */

public class Speaker implements Runnable{

    private AudioTrack audioTrack;
    private int bufferSize;
    private int sampleRate = 16000; // 44100 for music
    private int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    static boolean  seccionCritica = false;

    Thread thread;
    private boolean run;


    public Speaker ( ) {
        bufferSize =  25600;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    }

    @Override
    public void run () {

        audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, bufferSize , AudioTrack.MODE_STREAM );
        audioTrack.play();
        seccionCritica = true;

        while(run) {
            if(bufferSounds != null && bufferSounds.size() > 0) {
                try {
                    playSound( (Sonido) bufferSounds.pollFirst() );
                }catch ( Exception e ){
                    Log.e( "ERROR:", "playing sound" );
                    Log.e("ERROR:", e.getMessage());
                }
            }
        }


    }

    private void playSound(Sonido sonido ){
        audioTrack.write( sonido.getContent(), 0, sonido.getContent().length );
    }


    public void startThread(){
        //iniciamos el hilo
        this.thread = new Thread(this);
        setRun(true);
        thread.start();
    }

    public void stopThread(){
        //paramos el hilo
        boolean stop = true;
        setRun( false );

        audioTrack.release();

        while(stop) {
            try {
                this.thread.join();
                stop = false;
            } catch ( InterruptedException e ) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void setRun ( boolean run ) {
        this.run = run;
    }

    public boolean isRunning () {
        return run;
    }


}
