package spuzi.atenea.Server.Screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import spuzi.atenea.Common.NetworkActivity;
import spuzi.atenea.Common.NetworkStatusEnum;
import spuzi.atenea.Common.StatusNetwork;
import spuzi.atenea.R;
import spuzi.atenea.Server.Classes.InputPermission;

import spuzi.atenea.Server.Interfaces.OnEventListener;

/**
 * Created by spuzi on 09/03/2017.
 */


public class InputPassword extends NetworkActivity implements View.OnClickListener {
    private EditText editTextPassword; //Password used to access the camera
    private Button nextButton; //Button to start recording
    private ProgressBar loadingProgressbar; //Loading image

    private int puerto = 9999; //Port to open
    private SharedPreferences prefs; //Used to save the last data written (password of the camera)
    private SharedPreferences.Editor editor;//Used to write the password wrote by the user

    static InputPermission inputPermission;//ask for permission to use camera and microphone

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        initUIElements();
        loadPreviousPasswordInserted();//Load last password inserted by the user
    }

    /**
     * Load last password inserted by the user
     */
    private void loadPreviousPasswordInserted () {
        prefs = getSharedPreferences( "Preferencias", Context.MODE_PRIVATE );
        editTextPassword.setText( prefs.getString("password", "") ) ;
    }

    /**
     * Check if the device has permission to use the camera / microphone, if don't then ask the user for it
     */
    private void checkPermissions () {
        inputPermission = new InputPermission( this );
        inputPermission.askCameraPermission();
        inputPermission.askMicrophonePermission();
    }

    /**
     * Set the layout and the elements of the interface
     */
    private void initUIElements () {
        setContentView( R.layout.server_input_password );
        loadingProgressbar = (ProgressBar) findViewById( R.id.progressBarLoading );
        editTextPassword = (EditText) findViewById( R.id.editTextPassword );
        nextButton = (Button) findViewById( R.id.buttonNext );
        nextButton.setOnClickListener( this );

        hideLoadingElements();
    }

    /**
     * Shows the progress bar and unable the input fields.
     */
    @Override
    public void showLoadingElements(){
        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                loadingProgressbar.setVisibility( View.VISIBLE );
                editTextPassword.setVisibility( View.INVISIBLE );
                nextButton.setVisibility( View.INVISIBLE );
            }
        } );
    }

    /**
     * Hides the progress bar and enable the input fields.
     */
    @Override
    public void hideLoadingElements () {
        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                loadingProgressbar.setVisibility( View.INVISIBLE );
                editTextPassword.setVisibility( View.VISIBLE );
                nextButton.setVisibility( View.VISIBLE );
            }
        } );
    }

    /**
     * When button is clicked then call the next screen and pass the network parameters
     */
    @Override
    public void onClick ( View v ) {
        checkPermissions();
        if(inputPermission.hasCameraAndMicrophonePermission()) {
            //pass network parameters to next screen
            Intent intent = new Intent( getApplicationContext(), SetPreviewCamera.class );
            intent.putExtra( "PASSWORD", editTextPassword.getText().toString() );
            intent.putExtra( "IP_PUBLICA", this.statusNetwork.getPublicIP() );
            intent.putExtra( "IP_PRIVADA", statusNetwork.getPrivateIP() );
            intent.putExtra( "PUERTO", puerto );
            intent.putExtra( "MAC", statusNetwork.getMac() );

            //Write the password in a xml file to load it next time the user use the app
            editor = prefs.edit();
            editor.putString( "password", editTextPassword.getText().toString() );
            editor.commit();

            startActivity( intent );
        }
    }

    @Override
    public boolean onKeyDown ( int keyCode, KeyEvent event ) {

        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            System.out.println("Le di a atrás");
            return true;
        }

        return super.onKeyDown( keyCode, event );
    }
}