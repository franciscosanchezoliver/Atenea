package spuzi.atenea.Server.Screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import spuzi.atenea.R;
import spuzi.atenea.Server.Interfaces.OnEventListener;

/**
 * Created by spuzi on 20/03/2017.
 */

public class DialogScreen {

    Context context;
    AlertDialog.Builder builder;

    public DialogScreen(Context context , String title, String message){
        this.context = context;
        builder = new AlertDialog.Builder( context );
        builder.setCancelable( false );
        builder.setTitle( title );
        builder.setMessage( message );
    }


    public void setPossitiveButton ( String buttonText ,final OnEventListener onEventListener ){
        builder.setPositiveButton( buttonText , new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialog, int which ) {
                onEventListener.onEvent();
            }
        } );
    }

    public void setNegativeButton ( String buttonText ,final OnEventListener onEventListener ){
        builder.setNegativeButton( buttonText , new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialog, int which ) {
                onEventListener.onEvent();
            }
        } );
    }

    public void show(){
        builder.create();
        builder.show();
    }

}
