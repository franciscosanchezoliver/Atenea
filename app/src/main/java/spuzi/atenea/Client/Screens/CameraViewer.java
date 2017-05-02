package spuzi.atenea.Client.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import spuzi.atenea.Client.Classes.Camera;
import spuzi.atenea.Client.Classes.Connector;
import spuzi.atenea.Client.Classes.Speaker;
import spuzi.atenea.Client.Classes.VideoViewer;
import spuzi.atenea.Common.NetworkActivity;
import spuzi.atenea.R;

/**
 * Created by spuzi on 23/03/2017.
 */


public class CameraViewer extends NetworkActivity implements View.OnClickListener{

    private Camera remoteCamera;
    private Button buttonStop;
    private VideoViewer videoViewer;
    private FrameLayout camView;
    private int height = -1;
    private int width =-1;
    private Connector connector;

    private TextView textViewLoading;
    private ProgressBar progressBar;

    private Speaker speaker;

    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        initUIElements();
        adaptCameraSizeToScreenSize();
        getDataFromPreviousInterface();

        speaker = new Speaker();
    }

    /**
     * Get the data sent from the previous Screen
     */
    private void getDataFromPreviousInterface () {
        String mac = (String) getIntent().getExtras().get("MAC");
        String ipPublica = (String) getIntent().getExtras().get( "IP_PUBLICA" );
        String ipPrivada = (String) getIntent().getExtras().get("IP_PRIVADA");
        int puerto = (int) getIntent().getExtras().get( "PUERTO" );
        String nombre = (String) getIntent().getExtras().get( "PASSWORD_CORRECT" );
        String password = (String) getIntent().getExtras().get( "PASSWORD" );

        remoteCamera = new Camera( mac , ipPrivada, ipPublica , puerto, nombre , password);
    }


    private void initUIElements(){
        setContentView( R.layout.client_camera_viewer );
        buttonStop = (Button) findViewById( R.id.botonStop );
        buttonStop.setOnClickListener( this );
        textViewLoading = (TextView) findViewById( R.id.textViewLoading );
        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        camView = (FrameLayout) findViewById( R.id.cameraView );
        //Mostramos solamente el texto y la barra de cargando carguemos los parametros
        camView.setVisibility( View.INVISIBLE );
        buttonStop.setVisibility( View.INVISIBLE );
    }

    /**
     * The image recieve from the server has an specific proportion, so we have to keep that proportion in our screen
     */
    private void adaptCameraSizeToScreenSize () {
        height =getWindowManager().getDefaultDisplay().getHeight();
        width =getWindowManager().getDefaultDisplay().getWidth();
        double proportion =(double) width / height;
        if(proportion < 1.2){//reducir altura
            height = (int) (width/1.2);
        }else{//reducir anchura
            width = (int)  (height * 1.2);
        }

        camView.getLayoutParams().height =height;
        camView.getLayoutParams().width = width;
        camView.requestLayout();
    }

    @Override
    protected void onPause () {
        super.onPause();
        videoViewer.stopThread();
        connector.stopThread();
        speaker.stopWorker();
    }

    @Override
    public void onResume () {
        videoViewer.startThread();
        speaker.startWorker();
        connector.startThread();
        super.onResume();
    }

    /**
     * Hides the progress bar and a txt that says "loading"
     * and shows the frame where you can see the camera and a stop button
     */
    @Override
    protected void hideLoadingElements () {
        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                progressBar.setVisibility( View.INVISIBLE );
                textViewLoading.setVisibility( View.INVISIBLE );
                camView.setVisibility( View.VISIBLE );
                buttonStop.setVisibility( View.VISIBLE );
            }
        } );

        String remoteIP;

        /**
         * If the client and the server are in the same Local Network then we use the private IP to connect them
         * Else we use the public IP to connect them through internet
         */
        if(super.getPublicIP().equals( remoteCamera.getPublicIP() )){
            remoteIP = remoteCamera.getPrivateIP();
        }else{
            remoteIP = remoteCamera.getPublicIP();
        }

        if( connector == null){
            connector = new Connector( remoteIP, remoteCamera.getPort(), remoteCamera.getPassword() );
            //view that draws the images sent by the server
            videoViewer = new VideoViewer( this , width , height);
            camView.addView( videoViewer );
        }
    }

    /**
     * shows a progress bar and a txt that says "loading"
     * and hides the frame where you can see the camera and a stop button
     */
    @Override
    protected void showLoadingElements () {
        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                progressBar.setVisibility( View.VISIBLE );
                textViewLoading.setVisibility( View.VISIBLE );
                camView.setVisibility( View.INVISIBLE );
                buttonStop.setVisibility( View.INVISIBLE );
            }
        } );
    }


    @Override
    public void onClick ( View v ) {
        switch (v.getId())
        {
            //handle multiple view click events
            case R.id.buttonStop:
                connector.cerrarComunicacion();
                videoViewer.stopThread();

                Intent intent = new Intent( getApplicationContext(), ConnectTo.class );
                startActivity( intent );
                break;
        }
    }


}
