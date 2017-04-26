package spuzi.atenea.Client.Classes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import spuzi.atenea.Client.Screens.CameraViewer;

/**
 * Created by spuzi on 23/03/2017.
 */

public class URLCaller extends Thread{

    URL url ;
    Thread thread;
    private boolean run;
    CameraViewer cameraViewer;

    public URLCaller ( String url, CameraViewer cameraViewer ){
        try {
            this.url = new URL( url);
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        }

        this.cameraViewer = cameraViewer;
    }


    @Override
    public void run () {
        if(run) {
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch ( IOException e ) {
                System.out.println( "Error al hacer la peticion a la web" );
                e.printStackTrace();
            }

            try {
                InputStream in = null;
                try {
                    in = new BufferedInputStream( urlConnection.getInputStream() );
                    byte[] array = new byte[ 50 ];
                    int size = in.read( array, 0, 50 );
                    array = Arrays.copyOfRange( array, 0, size );
                    String respuesta = new String( array, "UTF-8" );
                    cameraViewer.setIpPublicaCliente( respuesta );

                } catch ( IOException e ) {
                    e.printStackTrace();
                    cameraViewer.setIpPublicaCliente( "SIN IP PUBLICA" );

                }

            } finally {
                urlConnection.disconnect();
            }
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

    public void setRun ( boolean run ) {
        this.run = run;
    }
}
