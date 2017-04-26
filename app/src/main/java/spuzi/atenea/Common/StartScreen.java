package spuzi.atenea.Common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import spuzi.atenea.Client.Screens.ConnectTo;
import spuzi.atenea.R;
import spuzi.atenea.Server.Screens.InputPassword;

/**
 * Created by spuzi on 09/03/2017.
 */


public class StartScreen extends Activity implements View.OnClickListener{

    Button buttonUseAsCamera;
    Button buttonConnectToCamera;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        initUIElements();
    }

    /**
     * Set the layout and the elements of the interface
     */
    private void initUIElements () {
        setContentView( R.layout.start_screen );
        buttonUseAsCamera = (Button) findViewById( R.id.buttonUseAsCamera );
        buttonUseAsCamera.setOnClickListener( this );

        buttonConnectToCamera = (Button) findViewById( R.id.buttonConnectToCamera );
        buttonConnectToCamera.setOnClickListener( this );
    }


    @Override
    public void onClick ( View v ) {
        switch ( v.getId() ){

            case R.id.buttonUseAsCamera:
                Intent screenUserAsACamera = new Intent( getApplicationContext(), InputPassword.class );
                startActivity( screenUserAsACamera );
                break;

            case R.id.buttonConnectToCamera:
                Intent screenConnectToCamera = new Intent( getApplicationContext(), ConnectTo.class );
                startActivity( screenConnectToCamera );
                break;

        }
    }


}
