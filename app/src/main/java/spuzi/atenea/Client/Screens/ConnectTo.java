package spuzi.atenea.Client.Screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import spuzi.atenea.Client.Classes.Camera;
import spuzi.atenea.Client.Classes.RemoteCameraStatusChecker;
import spuzi.atenea.Client.Classes.RemoteCameraStatus;
import spuzi.atenea.Common.NetworkActivity;
import spuzi.atenea.Common.StatusNetwork;
import spuzi.atenea.Client.Interfaces.OnCheckRemoteCameraStatus;
import spuzi.atenea.Common.NetworkStatusEnum;
import spuzi.atenea.R;
import spuzi.atenea.Server.Interfaces.OnEventListener;
import spuzi.atenea.Server.Screens.DialogScreen;

/**
 * Created by spuzi on 22/03/2017.
 *
 * A screen that shows 2 EditText to enter the name of the camera you want to connect to and the its password
 *
 */

public class ConnectTo extends NetworkActivity implements View.OnClickListener , OnCheckRemoteCameraStatus{


    private TextView textViewConectarCamara;
    private EditText editTextNombre;
    private EditText editTextPassword;
    private Button botonConnect;
    private ProgressBar progressBarCargando;
    private SharedPreferences prefs;//to save the name and password written by the user

    private Camera camera; //The data of the remote camera (ip private and public and port)
    private String nombre;//the name of the remote camera
    private String password;//the name of the remote camera

    private RemoteCameraStatusChecker remoteCameraStatusChecker; //Check if the remote camera is online


    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        initUIElements();
        recoverLastNameAndPassword();
    }

    /**
     * Set the layout and the elements of the interface
     */
    private void initUIElements () {
        setContentView( R.layout.client_connect_to );
        textViewConectarCamara = (TextView) findViewById( R.id.textViewConectarCamara );
        editTextNombre = (EditText) findViewById( R.id.editTextNombre );
        editTextPassword = (EditText) findViewById( R.id.editTextPassword );
        botonConnect = (Button) findViewById( R.id.botonConnect );
        botonConnect.setOnClickListener( this );
        progressBarCargando = (ProgressBar) findViewById( R.id.progressBarCargando );
        progressBarCargando.setVisibility( View.INVISIBLE );
    }

    /**
     * Check if the user has entered a name and password before, if so then fill the EditText with
     * those values
     */
    private void recoverLastNameAndPassword(){
        prefs = getSharedPreferences( "Preferencias", Context.MODE_PRIVATE );
        nombre = prefs.getString( "nombre", "" );
        //Fill the EditText
        password = prefs.getString("password", "");
        editTextNombre.setText( nombre );
        editTextPassword.setText( password );
    }

    /**
     *  Save the name and password entered by the user
     */
    private void saveNameAndPassword(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( "nombre", editTextNombre.getText().toString() );
        editor.putString( "password", editTextPassword.getText().toString() );
        editor.commit();
    }


    /**
     *  Show the loadings elements and hide the EditText elements
     */
    @Override
    public void showLoadingElements(){

        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                textViewConectarCamara.setText( getString( R.string.loading ) );
                progressBarCargando.setVisibility( View.VISIBLE );
                botonConnect.setVisibility( View.INVISIBLE );
                editTextNombre.setVisibility( View.INVISIBLE );
                editTextPassword.setVisibility( View.INVISIBLE );
            }
        } );
    }

    /**
     * Hide the loading elements and show the EditText elements
     */
    @Override
    public void hideLoadingElements(){

        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                textViewConectarCamara.setText( getString( R.string.connect ) );
                progressBarCargando.setVisibility( View.INVISIBLE );
                botonConnect.setVisibility( View.VISIBLE );
                editTextNombre.setVisibility( View.VISIBLE );
                editTextPassword.setVisibility( View.VISIBLE );
            }
        } );
    }


    /**
     * When button is clicked the client tries to connect to a remote camera
     */
    @Override
    public void onClick ( View v ) {
        switch (v.getId())
        {
            case R.id.botonConnect:
                saveNameAndPassword();
                showLoadingElements();
                //Check if the camera we want to connect to exist
                remoteCameraStatusChecker = new RemoteCameraStatusChecker( editTextNombre.getText().toString(), this  );
        }
    }

    /**
     * This event gets the state of the camera we want to connect to
     */
    @Override
    public void onRemoteCameraStatusRecieved ( RemoteCameraStatus cameraStatus, Camera camera ) {
        this.camera = camera;
        switch ( cameraStatus ){
            case NOT_FOUND: remoteCameraNotFound();break;
            case AVAILABLE: remoteCameraFound();break;
        }
    }

    /**
     * Shows a dialog that tells the user that the camera he wants to connect doesn't exist
     */
    public void remoteCameraNotFound () {
        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                DialogScreen dialogScreen = new DialogScreen( ConnectTo.this ,
                                                              getString( R.string.not_found_title ),
                                                              getString( R.string.not_found ) );
                dialogScreen.setPossitiveButton( getString( R.string.ok ), new OnEventListener() {
                    @Override
                    public void onEvent () {
                        System.out.println("The given name was not found.");
                        hideLoadingElements();
                    }
                } );

                dialogScreen.show();
            }
        } );
    }

    /**
     * Shows a dialog that tells the user that the camera he wants to connect doesn't exist
     */
    public void remoteCameraFound () {
        camera.printInfo();

        //if the given MAC and password are found in the web database then we have permission to access the camera
        //so we can go to the next screen if all data is correct (name and password of the remote camera)
        if( camera.getPassword().equals( editTextPassword.getText().toString() ) ) {

            //Go to next screen
            Intent intent = new Intent( getApplicationContext(), CameraViewer.class );
            intent.putExtra( "MAC" , camera.getMac() );
            intent.putExtra( "IP_PUBLICA" , camera.getPublicIP() );
            intent.putExtra( "IP_PRIVADA" , camera.getPrivateIP() );
            intent.putExtra( "PUERTO" , camera.getPort() );
            intent.putExtra( "NOMBRE", camera.getName() );
            intent.putExtra( "PASSWORD" , camera.getPassword() );

            startActivity( intent );
        }else{
            //password written is incorrect
            System.out.println("Password written is incorrect");

            runOnUiThread( new Runnable() {
                @Override
                public void run () {
                    DialogScreen dialogScreen = new DialogScreen( ConnectTo.this ,
                                                                  getString( R.string.password_incorrect_title ),
                                                                  getString( R.string.password_incorrect ) );
                    dialogScreen.setPossitiveButton( getString( R.string.ok ), new OnEventListener() {
                        @Override
                        public void onEvent () {
                            System.out.println("The given password is incorrect.");
                            hideLoadingElements();
                        }
                    } );

                    dialogScreen.show();
                }
            } );
        }
    }



}
