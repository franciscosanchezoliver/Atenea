package spuzi.atenea.Client.Screens;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import spuzi.atenea.Client.Classes.Camera;
import spuzi.atenea.Client.Classes.Conector;
import spuzi.atenea.Client.Classes.Speaker;
import spuzi.atenea.Client.Classes.URLCaller;
import spuzi.atenea.Client.Classes.Vista;
import spuzi.atenea.Client.Screens.ConnectTo;
import spuzi.atenea.R;

/**
 * Created by spuzi on 23/03/2017.
 */


public class CameraViewer extends AppCompatActivity implements View.OnClickListener{

    Camera camera;
    String ipPublicaCliente;//la ip publica del cliente

    Button botonStop;
    Vista vista;
    FrameLayout camView;
    int height = -1;
    int width =-1;
    Conector conector;

    TextView textViewCargando;
    ProgressBar progressBar;

    private Speaker speaker;

    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        initUIElements();
        adaptCameraSizeToScreenSize();

        speaker = new Speaker();


        //recogemos los datos de la pantalla "StartScreen.java"
        String mac = (String) getIntent().getExtras().get("MAC");
        String ipPublica = (String) getIntent().getExtras().get( "IP_PUBLICA" );
        String ipPrivada = (String) getIntent().getExtras().get("IP_PRIVADA");
        int puerto = (int) getIntent().getExtras().get( "PUERTO" );
        String nombre = (String) getIntent().getExtras().get( "PASSWORD_CORRECT" );
        String password = (String) getIntent().getExtras().get( "PASSWORD" );

        camera = new Camera( mac , ipPrivada, ipPublica , puerto, nombre , password);

        URLCaller urlCaller = new URLCaller( "http://spuzi.esy.es/checkPublicIP.php", this );
        urlCaller.startThread();

        while(ipPublicaCliente == null){//espera activa
        }


        String IP;

        //si cliente y servidor estan en la misma red, entonces usaremos la ip local para conectarse
        //si estan en diferentes redes, entonces usare la ip publica
        //si cliente y servidor se encuentra debajos del mismo NAT (router) entonces compartiran la misma
        //IP publica
        if(ipPublicaCliente.equals( camera.getPublicIP() )){
            IP = camera.getPrivateIP();
        }else{
            IP = camera.getPublicIP();
        }

        if(conector == null){
            if(hayConexionALaRed()){

                progressBar.setVisibility( View.INVISIBLE );
                textViewCargando.setVisibility( View.INVISIBLE );
                //Mostramos solamente el texto y la barra de cargando carguemos los parametros
                camView.setVisibility( View.VISIBLE );
                botonStop.setVisibility( View.VISIBLE );

                conector = new Conector( IP, puerto, password );

                //view that draws the images sent by the server
                vista = new Vista( this , width ,  height);
                camView.addView( vista );

                botonStop = (Button) findViewById( R.id.botonStop );
                botonStop.setOnClickListener( this );

                textViewCargando = (TextView) findViewById( R.id.textViewCargando );
                progressBar = (ProgressBar) findViewById( R.id.progressBar );
            }
            else
                Log.e( "Error:" , "Sin red, comprobar que la pantalla este encendida." );
        }
    }


    private void initUIElements(){
        setContentView( R.layout.client_camera_viewer );
        camView = (FrameLayout) findViewById( R.id.cameraView );
        //Mostramos solamente el texto y la barra de cargando carguemos los parametros
        camView.setVisibility( View.INVISIBLE );
        botonStop.setVisibility( View.INVISIBLE );
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
        vista.stopThread();
        conector.stopThread();
        speaker.stopThread();
    }

    @Override
    protected void onResume () {
        vista.startThread();
        speaker.startThread();
        conector.startThread();
        super.onResume();
    }

    private boolean hayConexionALaRed (){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo                 = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable())
            return true;
        else return false;
    }

    @Override
    public void onClick ( View v ) {
        switch (v.getId())
        {
            //handle multiple view click events
            case R.id.botonStop:
                //display in long period of time
                //Toast.makeText( getApplicationContext(), "Cerrando conexion...", Toast.LENGTH_LONG ).show();
                conector.cerrarComunicacion();
                vista.stopThread();

                Intent intent = new Intent( getApplicationContext(), ConnectTo.class );
                startActivity( intent );
                break;
        }
    }

    public void setIpPublicaCliente ( String ipPublicaCliente ) {
        this.ipPublicaCliente = ipPublicaCliente;
    }
}
