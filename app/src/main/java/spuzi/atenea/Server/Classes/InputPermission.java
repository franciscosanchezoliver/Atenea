package spuzi.atenea.Server.Classes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by spuzi on 19/03/2017.
 */

public class InputPermission {

    private Context context;

    public InputPermission ( Context context ) {
        this.context = context;
    }

    public boolean hasCameraAndMicrophonePermission (){
        return hasCameraPermission() && hasMicrophonePermission();
    }

    public boolean hasCameraPermission(){
        int permissionCamera = ContextCompat.checkSelfPermission( context, Manifest.permission.CAMERA );
        return ( permissionCamera == 0  ) ? true : false;
    }

    public boolean hasMicrophonePermission(){
        int permissionMicrophone = ContextCompat.checkSelfPermission( context, Manifest.permission.RECORD_AUDIO );
        return ( permissionMicrophone == 0  ) ? true : false;
    }

    public void askCameraPermission(){
        if(!hasCameraPermission()){
            String[] PERMISSIONS = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions( (Activity) context, PERMISSIONS , 1 );
        }
    }

    public void askMicrophonePermission(){
        if(!hasMicrophonePermission()){
            String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};
            ActivityCompat.requestPermissions( (Activity) context, PERMISSIONS , 1 );
        }
    }


}
