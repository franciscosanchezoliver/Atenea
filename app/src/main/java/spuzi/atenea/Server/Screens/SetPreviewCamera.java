package spuzi.atenea.Server.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import spuzi.atenea.R;
import spuzi.atenea.Server.Classes.CameraPreview;
import spuzi.atenea.Server.Interfaces.PreviewCamInterface;

/**
 * Created by spuzi on 09/03/2017.
 */


public class SetPreviewCamera extends AppCompatActivity implements View.OnClickListener , PreviewCamInterface {

    private CameraPreview cameraPreview;
    private Button startButton;
    private Button buttonStop;
    private FrameLayout camView;

    private String privateIP;
    private String mac;
    private String publicIP;

    private String password = "";
    private int port;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        getDataFromPreviousInterface();
        initUIElements();
        adaptCameraSizeToScreenSize();
    }

    /**
     * Get data from the input password screen
     */
    private void getDataFromPreviousInterface () {
        password = (String) getIntent().getExtras().get( "PASSWORD" );
        privateIP = (String) getIntent().getExtras().get( "IP_PRIVADA" );
        publicIP = (String) getIntent().getExtras().get( "IP_PUBLICA" );
        port = (int) getIntent().getExtras().get( "PUERTO" );
        mac = (String) getIntent().getExtras().get( "MAC" );
    }

    /**
     * Set the layout and the elements of the interface
     */
    private void initUIElements () {
        setContentView(R.layout.server_preview_camera);
        startButton = (Button) findViewById( R.id.buttonStart );
        startButton.setOnClickListener(this);

        buttonStop = (Button) findViewById( R.id.buttonStopPreviewCamera );
        buttonStop.setOnClickListener( this );

        camView = (FrameLayout) findViewById(R.id.cameraView);
        cameraPreview = new CameraPreview( this );
    }

    /**
     * The image recieve from the server has an specific proportion, so we have to keep that proportion in our screen
     */
    private void adaptCameraSizeToScreenSize () {
        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        double relacion =(double) width / height;
        if(relacion < 1.2){//reducir altura
            height = (int) (width/1.2);
        }else{//reducir anchura
            width = (int)  (height * 1.2);
        }

        camView.getLayoutParams().height =height;
        camView.getLayoutParams().width = width;
        camView.requestLayout();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(cameraPreview != null)
            cameraPreview.openCamera();
    }


    // remove preview callback before stopping the camera
    @Override
    protected void onPause() {
        super.onPause();
        if(cameraPreview != null)
            cameraPreview.pause();
    }


    @Override
    public void onClick ( View v ) {
        Intent intent = null;
        switch (v.getId())
        {
            case R.id.buttonStart:
                //call next screen in which it starts a TCP server and the microphone
                intent = new Intent( getApplicationContext(), CameraOnline.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent.putExtra( "PASSWORD", password );
                intent.putExtra( "IP_PUBLICA", publicIP );
                intent.putExtra( "IP_PRIVADA", privateIP );
                intent.putExtra( "PUERTO" , port );
                intent.putExtra( "MAC", mac );
                startActivity( intent );
                finish();
                break;

            case R.id.buttonStopPreviewCamera:
                cameraPreview.pause();
                intent = new Intent( getApplicationContext(), InputPassword.class );
                startActivity( intent );
                break;
        }
    }


    public void showCamView(){
        camView.addView( cameraPreview );
    }

    public void hideCamView(){
        camView.removeView( cameraPreview );
    }
}

