package spuzi.atenea.Server.Screens;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import spuzi.atenea.Common.NetworkActivity;
import spuzi.atenea.Common.NetworkStatusEnum;
import spuzi.atenea.R;
import spuzi.atenea.Server.Classes.CameraPreview;
import spuzi.atenea.Server.Classes.Microphone;
import spuzi.atenea.Server.Classes.PortOpenerService;
import spuzi.atenea.Server.Interfaces.OnPortsOpenListener;
import spuzi.atenea.Server.Interfaces.PreviewCamInterface;
import spuzi.atenea.Server.Classes.TCPServer;
import spuzi.atenea.Server.Classes.PortOpenerService.LocalBinder;
import spuzi.atenea.Server.Interfaces.OnPortsClosedListener;

/**
 * Created by spuzi on 09/03/2017.
 */


public class CameraOnline extends NetworkActivity implements View.OnClickListener , PreviewCamInterface {

    private CameraPreview cameraPreview;
    private Button buttonStop;
    private TextView cameraName;
    private TextView cameraOnline;
    private FrameLayout camView;//shows the image that the camera is capturing
    public static TCPServer tcpServer;
    private static Microphone microphone;
    private String privateIp;
    private String mac;
    private String publicIp;
    private String PASSWORD = "";
    private int port;

    private ProgressDialog progressDialog;

    private ProgressBar progressBar;

    PortOpenerService portOpenerService;

    OnPortsClosedListener onPortsClosed = new OnPortsClosedListener() {
        @Override
        public void onPortsClosed () {
            try{
                //paramos el servicio
                unbindService( serviceConnection );
            }catch ( Exception e){
                e.printStackTrace();
            }
        }
    };


    /**
     *  Interface ServiceConnection that permit the connection with the service (NAT port opener)
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        //When Android creates the connection between the client and the service, this method is called.
        //This method delivers the IBinder that the client can use to communicate with the service
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            portOpenerService = binder.getService();
            forwardPort();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            System.out.println("Servicio desconectado");
        }
    };


    private void showProgressDialog(){

        progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "Cargando....." );
        progressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
        progressDialog.setIndeterminate( true );
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        getDataFromPreviousInterface();
        initUIElements();
    }


    //Check if the service that opens the ports is already running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService( Context.ACTIVITY_SERVICE );
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE )) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void getDataFromPreviousInterface () {
        PASSWORD = (String) getIntent().getExtras().get( "PASSWORD" );
        privateIp = (String) getIntent().getExtras().get( "IP_PRIVADA" );
        publicIp = (String) getIntent().getExtras().get( "IP_PUBLICA" );
        port = (int) getIntent().getExtras().get( "PUERTO" );
        mac = (String) getIntent().getExtras().get( "MAC" );
    }

    /**
     * Set the layout and the elements of the interface
     */
    private void initUIElements () {
        setContentView( R.layout.server_camera_online);
        buttonStop = (Button) findViewById( R.id.buttonStop );
        buttonStop.setOnClickListener( this );

        cameraName = (TextView) findViewById( R.id.cameraName );
        cameraName.setText( "Name: " + mac.replaceAll( ":" , "" ) );//the device's name is his MAC

        cameraOnline = (TextView) findViewById( R.id.cameraOnline );

        camView = (FrameLayout) findViewById(R.id.cameraView);
        cameraPreview = new CameraPreview( this );

        camView.getLayoutParams().height =1;// "hide" the camView so the telephone doesn't overheat
        camView.getLayoutParams().width = 1;
        camView.requestLayout();

        progressBar = (ProgressBar) findViewById( R.id.progressBarStop );
        progressBar.setVisibility( View.INVISIBLE );
    }

    //Shows a progress bar and hide the other elements
    public void showLoadingElements (){
        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                buttonStop.setVisibility( View.INVISIBLE );
                cameraOnline.setVisibility( View.INVISIBLE );
                cameraName.setVisibility( View.INVISIBLE );
                progressBar.setVisibility( View.VISIBLE );
            }
        } );
    }

    //Starts the TCP server, microphone and open the ports of the NAT
    @Override
    public void onResume(){
        super.onResume();
        startServices();
    }

    //Stops the TCP server, microphone and close the opened ports
    @Override
    protected void onPause() {
        super.onPause();
        stopRunningServices();
    }

    //hide the loading bar and shows the buttons and texts
    @Override
    protected void hideLoadingElements () {
        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                buttonStop.setVisibility( View.VISIBLE );
                cameraOnline.setVisibility( View.VISIBLE );
                cameraName.setVisibility( View.VISIBLE );
                progressBar.setVisibility( View.INVISIBLE );
            }
        } );
    }

    /**
     * Button stop
     */
    @Override
    public void onClick ( View v ) {
        Intent intent = new Intent( getApplicationContext(), InputPassword.class );
        startActivity( intent );
    }

    //Stops the TCP
    private void stopRunningServices(){
        showLoadingElements();
        stopTCPServer();
        stopRecordingMicrophone();
        if (portOpenerService != null)
            portOpenerService.closeForwardedPorts(this.onPortsClosed );
    }

    private void startServices(){
        startTCPServer();
        startRecordingMicrophone();
        startOpenPortsService();
    }

    private void startOpenPortsService(){
        boolean isRunning = isMyServiceRunning( PortOpenerService.class );
        if(!isRunning ){
            //open the service that forward the NAT's ports
            // Bind to LocalService
            Intent intent = new Intent(this, PortOpenerService.class);
            bindService( intent, serviceConnection, Context.BIND_AUTO_CREATE );
        }
    }

    public void showCamView(){
        camView.addView( cameraPreview );
    }

    public void hideCamView(){
        camView.removeView( cameraPreview );
    }

    private void startTCPServer(){
        //Start a new TCP Server to listen to new clients
        if(tcpServer == null)
            tcpServer = new TCPServer( mac, privateIp, publicIp, PASSWORD , port, cameraPreview);
        if( tcpServer != null && !tcpServer.isRunning() ){
            tcpServer.startWorker();
        }
    }
    private void startRecordingMicrophone(){
        //Microphone to start listening to sounds
        if(microphone == null)
            microphone = new Microphone();
        if( microphone != null && !microphone.isRunning())
            microphone.startThread();
    }
    private void stopTCPServer(){
        if( tcpServer != null && tcpServer.isRunning() )
            tcpServer.stopWorker();
    }
    private void stopRecordingMicrophone(){
        if( microphone != null && microphone.isRunning())
            microphone.stopThread();
    }
    public void forwardPort(){
        portOpenerService.forwardNATPorts( privateIp , port );
    }

    /**
     * The Back button of android
     */
    @Override
    public boolean onKeyDown ( int keyCode, KeyEvent event ) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();//finish this activity
            Intent intent = new Intent( getApplicationContext(), InputPassword.class );
            startActivity( intent );
            return true;
        }
        return super.onKeyDown( keyCode, event );
    }

}












