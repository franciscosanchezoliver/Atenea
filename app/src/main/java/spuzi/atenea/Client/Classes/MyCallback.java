package spuzi.atenea.Client.Classes;

import android.view.SurfaceHolder;

/**
 * Created by spuzi on 23/03/2017.
 */

public class MyCallback implements SurfaceHolder.Callback {
    @Override
    public void surfaceCreated ( SurfaceHolder holder ) {
        System.out.println("Surface creado");
    }

    @Override
    public void surfaceChanged ( SurfaceHolder holder, int format, int width, int height ) {
        System.out.println("Surface cambiado");
    }

    @Override
    public void surfaceDestroyed ( SurfaceHolder holder ) {
        System.out.println("Surface destruido");
    }
}