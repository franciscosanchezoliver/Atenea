package spuzi.atenea.Server.Classes;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import spuzi.atenea.Common.Buffer;

/**
 * Created by spuzi on 09/03/2017.
 */


public class Microphone implements Runnable{

    AudioRecord recorded;
    static Buffer bufferSonidos;

    public byte[] buffer;
    private int sampleRate = 16000; // 44100 for music
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //cuanto mas grande sea el número por el que se multiplica, mayor será la longitud de cada trozo de sonido
    int minBufSize = AudioRecord.getMinBufferSize( sampleRate,channelConfig,audioFormat ) ;


    Thread thread;
    private boolean run;


    public Microphone(){
        bufferSonidos = new Buffer();
        minBufSize =  AudioRecord.getMinBufferSize( sampleRate,channelConfig,audioFormat )*5;
        buffer = new byte[ minBufSize ];//buffer used to save every sound capture by the microphone
    }

    @Override
    public void run () {
        //iniciamos el micro del aparato
        recorded = new AudioRecord( MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize );
        recorded.startRecording();

        try {
            //bucle que va cogiendo cada sonido y metiendolo en el buffer de sonidos
            while(run) {
                //escucha un sonido del microfono y guarda
                recorded.read( buffer, 0, minBufSize );
                bufferSonidos.add( new Sound( buffer ) );
                buffer = new byte[minBufSize];
            }

        }catch ( Exception e ){
            Log.e( "ERROR:" , "recording from microphone" );
        }
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

    public boolean isRunning () {
        return run;
    }

    public void setRun ( boolean run ) {
        this.run = run;
    }


}
