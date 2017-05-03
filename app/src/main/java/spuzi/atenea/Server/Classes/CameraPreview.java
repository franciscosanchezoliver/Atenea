package spuzi.atenea.Server.Classes;

/**
 * Created by spuzi on 09/03/2017.
 */


import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import spuzi.atenea.Common.Buffer;
import spuzi.atenea.Common.Image;
import spuzi.atenea.Server.Interfaces.PreviewCamInterface;

import static android.content.ContentValues.TAG;
import static android.content.Context.WINDOW_SERVICE;

/**
 * Preview what the camera sees
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private int width;
    private int height;
    private Context context ;
    private Camera camera;
    Camera.Parameters params;
    private boolean isRunning = false;
    //static variable that can be shared between different threads
    public static Buffer BUFFER_IMAGES = new Buffer( 10);

    public void openCamera(){
        if( camera == null) {
            try {
                camera = Camera.open(); // attempt to get a Camera instance

                /* Seleccionamos el tamaño de la imagen óptimo que soporte la cámara y que podamos
                 * enviarlo por la red en un datagrama
                 */
                params = camera.getParameters();

                /* No podemos poner cualquier tamaño, cada movil soporta unos parámetros. Normalmente
                 * el tamaño de la imagen que toma el dispositivo es demasiado grande para poder
                 * enviarlo en un solo datagrama.
                 */
                List<Camera.Size> listaTamanios = params.getSupportedPreviewSizes();
                //order the list with the smallest element in the first position
                Collections.sort( listaTamanios , getComparatorBySize() );
                //get the smallest size
                Camera.Size cameraSize = listaTamanios.get( 0 );

                this.width = cameraSize.width;
                this.height = cameraSize.height;

                //ponemos un tamaño pequeño para la imagen para que se pueda enviar la imagen usando un
                //solo datagrama, recordemos que un datagrama/paquete puede enviar hasta 2^16
                params.setPreviewSize( width , height );

                System.out.println("Starting camera with parameters:");
                System.out.println("\tWidth:" + cameraSize.width);
                System.out.println("\tHeigth:" + cameraSize.height);

                camera.setParameters( params );

                //shows the preview of the camera
                new Handler( Looper.getMainLooper() ).post( new Runnable() {
                    @Override
                    public void run () {
                        ((PreviewCamInterface)context).showCamView();
                    }
                } );

                isRunning = true;

            }catch (Exception e){
                // Camera is not available (in use or does not exist)
                Log.e( "ERROR:" , "There was an error opening the camera, make sure your telephone has permission." );
                e.printStackTrace();
            }
        }
    }

    public void openCamera(int width, int height){
        if( camera == null) {
            try {
                camera = Camera.open(); // attempt to get a Camera instance

                /* Seleccionamos el tamaño de la imagen óptimo que soporte la cámara y que podamos
                 * enviarlo por la red en un datagrama
                 */
                params = camera.getParameters();

                params.setPreviewSize( width , height );

                camera.setParameters( params );

                //shows the preview of the camera
                new Handler( Looper.getMainLooper() ).post( new Runnable() {
                    @Override
                    public void run () {
                        ((PreviewCamInterface)context).showCamView();
                    }
                } );

                isRunning = true;

            }catch (Exception e){
                // Camera is not available (in use or does not exist)
                Log.e("ERROR:" , "There was an error opening the camera.");
                e.printStackTrace();
            }
        }
    }

    public CameraPreview( Context context ) {
        super(context);

        this.context = context;

        //openCamera();
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if( camera != null) {
                camera.setPreviewCallback( mPreviewCallback );
                camera.setPreviewDisplay( holder );
                camera.startPreview();
            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        //Do something when the surface is destroyed
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (isRunning) {
            camera.stopPreview();

            Camera.Parameters parameters = camera.getParameters();

            Display display = ( (WindowManager) context.getSystemService( WINDOW_SERVICE ) ).getDefaultDisplay();

            if ( display.getRotation() == Surface.ROTATION_0 ) {
                parameters.setPreviewSize( height, width );
                camera.setDisplayOrientation( 90 );
            }

            if ( display.getRotation() == Surface.ROTATION_90 ) {
                parameters.setPreviewSize( width, height );
            }

            if ( display.getRotation() == Surface.ROTATION_180 ) {
                parameters.setPreviewSize( height, width );
            }

            if ( display.getRotation() == Surface.ROTATION_270 ) {
                parameters.setPreviewSize( width, height );
                camera.setDisplayOrientation( 180 );
            }

            camera.setParameters( parameters );
            //resume();
            camera.setPreviewCallback( mPreviewCallback );
            try {
                camera.setPreviewDisplay( holder );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            camera.startPreview();
            isRunning = true;
        }
    }


    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        /**
         * This method capture the image of the camera
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            BUFFER_IMAGES.add(
                    new Image( data , params.getPreviewSize().width, params.getPreviewSize().height)
                             );
        }
    };

    /**
     * Comparator used to order the list of the sizes availables in the camera
     * @return
     */
    private Comparator<Camera.Size> getComparatorBySize (){
        Comparator comparator = new Comparator < Camera.Size >() {
            @Override
            public int compare ( Camera.Size o1, Camera.Size o2 ) {
                if(o1.height < o2.height)
                    return -1;
                else if(o1.height > o2.height)
                    return 1;
                else
                    return 0;
            }
        };
        return comparator;
    }

    public void pause(){
        if( camera != null){
            camera.stopPreview();
            camera.setPreviewCallback( null );
            camera.release();
            camera = null;
            //shows the preview of the camera
            new Handler( Looper.getMainLooper() ).post( new Runnable() {
                @Override
                public void run () {
                    ((PreviewCamInterface)context).hideCamView();
                }
            } );
            isRunning = false;
        }
        BUFFER_IMAGES.reset();
    }

    public void resume(){
        openCamera();
    }

    public boolean isRunning(){
        return this.isRunning;
    }

}
