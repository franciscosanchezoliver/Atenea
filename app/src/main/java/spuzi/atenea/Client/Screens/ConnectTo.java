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
import spuzi.atenea.Client.Interfaces.OnCheckRemoteCameraStatus;
import spuzi.atenea.R;
import spuzi.atenea.Server.Interfaces.OnEventListener;
import spuzi.atenea.Server.Screens.DialogScreen;

/**
 * Created by spuzi on 22/03/2017.
 *
 * A screen that shows 2 EditText to enter the name of the remoteCamera you want to connect to and the its password
 *
 */

public class ConnectTo extends NetworkActivity implements View.OnClickListener , OnCheckRemoteCameraStatus{


    private TextView textViewConnectToCamera;
    private EditText editTextName;
    private EditText editTextPassword;
    private Button buttonConnect;
    private ProgressBar progressBarLoading;
    private SharedPreferences prefs;//to save the name and password written by the user
    private Camera remoteCamera; //The data of the remote remoteCamera (ip private and public and port)
    private String name;//the name of the remote remoteCamera entered by the user
    private String password;//the password of the remote remoteCamera entered by the user
    private RemoteCameraStatusChecker remoteCameraStatusChecker; //Check if the remote remoteCamera is online


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
        textViewConnectToCamera = (TextView) findViewById( R.id.textViewConectarCamara );
        editTextName = (EditText) findViewById( R.id.editTextNombre );
        editTextPassword = (EditText) findViewById( R.id.editTextPassword );
        buttonConnect = (Button) findViewById( R.id.botonConnect );
        buttonConnect.setOnClickListener( this );
        progressBarLoading = (ProgressBar) findViewById( R.id.progressBarCargando );
        progressBarLoading.setVisibility( View.INVISIBLE );
    }

    /**
     * Check if the user has entered a name and password before, if so then fill the EditText with
     * those values
     */
    private void recoverLastNameAndPassword(){
        prefs = getSharedPreferences( "Preferencias", Context.MODE_PRIVATE );
        name = prefs.getString( "nombre", "" );
        //Fill the EditText
        password = prefs.getString("password", "");
        editTextName.setText( name );
        editTextPassword.setText( password );
    }

    /**
     *  Save the name and password entered by the user
     */
    private void saveNameAndPassword(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( "nombre", editTextName.getText().toString() );
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
                textViewConnectToCamera.setText( getString( R.string.loading ) );
                progressBarLoading.setVisibility( View.VISIBLE );
                buttonConnect.setVisibility( View.INVISIBLE );
                editTextName.setVisibility( View.INVISIBLE );
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
                textViewConnectToCamera.setText( getString( R.string.connect ) );
                progressBarLoading.setVisibility( View.INVISIBLE );
                buttonConnect.setVisibility( View.VISIBLE );
                editTextName.setVisibility( View.VISIBLE );
                editTextPassword.setVisibility( View.VISIBLE );
            }
        } );
    }


    /**
     * When button is clicked the client tries to connect to a remote remoteCamera
     */
    @Override
    public void onClick ( View v ) {
        switch (v.getId())
        {
            case R.id.botonConnect:
                saveNameAndPassword();
                showLoadingElements();
                //Check if the remoteCamera we want to connect to exist
                remoteCameraStatusChecker = new RemoteCameraStatusChecker( editTextName.getText().toString(), this  );
                remoteCameraStatusChecker.startWorker();
        }
    }

    /**
     * This event gets the state of the remoteCamera we want to connect to
     */
    @Override
    public void onRemoteCameraStatusRecieved ( RemoteCameraStatus cameraStatus, Camera camera ) {
        this.remoteCamera = camera;
        switch ( cameraStatus ){
            case NOT_FOUND: remoteCameraNotFound();break;
            case AVAILABLE: remoteCameraFound();break;
        }
    }

    /**
     * Shows a dialog that tells the user that the remoteCamera he wants to connect doesn't exist
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
     * Shows a dialog that tells the user that the remoteCamera he wants to connect doesn't exist
     */
    public void remoteCameraFound () {
        remoteCamera.printInfo();

        //if the given MAC and password are found in the web database then we have permission to access the remoteCamera
        //so we can go to the next screen if all data is correct (name and password of the remote remoteCamera)
        if( remoteCamera.getPassword().equals( editTextPassword.getText().toString() ) ) {

            //Go to next screen
            Intent intent = new Intent( getApplicationContext(), CameraViewer.class );
            intent.putExtra( "MAC" , remoteCamera.getMac() );
            intent.putExtra( "IP_PUBLICA" , remoteCamera.getPublicIP() );
            intent.putExtra( "IP_PRIVADA" , remoteCamera.getPrivateIP() );
            intent.putExtra( "PUERTO" , remoteCamera.getPort() );
            intent.putExtra( "NOMBRE", remoteCamera.getName() );
            intent.putExtra( "PASSWORD" , remoteCamera.getPassword() );

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
