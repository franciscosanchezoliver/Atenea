package spuzi.atenea.Client.Classes;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import spuzi.atenea.Common.Sound;
import spuzi.atenea.Common.Worker;

import static spuzi.atenea.Client.Classes.Connector.bufferSounds;

/**
 * Created by spuzi on 23/03/2017.
 */

public class Speaker extends Worker{

    private AudioTrack audioTrack;
    private int bufferSize;
    private int sampleRate = 16000; // 44100 for music
    private int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    static boolean criticalSection = false;


    public Speaker ( ) {
        bufferSize =  25600;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    }

    @Override
    public void run () {
        audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, bufferSize , AudioTrack.MODE_STREAM );
        audioTrack.play();
        criticalSection = true;

        while(super.isRunning()) {
            if(bufferSounds != null && bufferSounds.size() > 0) {
                try {
                    playSound( (Sound) bufferSounds.pollFirst() );
                }catch ( Exception e ){
                    Log.e( "ERROR:", "playing sound" );
                    Log.e("ERROR:", e.getMessage());
                }
            }
        }
    }

    private void playSound (Sound sound ){
        audioTrack.write( sound.getContent(), 0, sound.getContent().length );
    }


    @Override
    public void stopWorker () {
        super.stopWorker();
        audioTrack.release();
    }


}
