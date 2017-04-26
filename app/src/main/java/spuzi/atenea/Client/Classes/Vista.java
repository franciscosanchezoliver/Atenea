package spuzi.atenea.Client.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;

/**
 * Created by spuzi on 23/03/2017.
 */


public class Vista extends SurfaceView implements  Runnable{
    SurfaceHolder holder;
    private boolean run ;
    private Thread thread;
    private YuvImage yuv;//Android coge las imagenes en formato yuv
    Bitmap bitmap;
    ByteArrayOutputStream out;
    byte[] bytes;
    Bitmap.Config configuracion;
    private int width;
    private int height;
    private double relacion;
    final double RELACION_ANCHURA_ALTURA = 1.2 ;



    public void setRun(boolean r){
        this.run = r;
    }

    public Vista ( Context context , int width , int height ) {
        super( context );

        holder = getHolder();
        holder.addCallback( new MyCallback() );
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //La imagen original se toma en 176x144, pero nosotros la reescalaremos para que se vea en grande
        //pero al reescalarla queremos mantener la relación que haya entre la altura y la anchura, si
        //dividimos anchura/altura nos da 1,2 . Por tanto vamos a hacer que siempre se mantenga esa proporcion
        this.width = width;
        this.height = height;
        this.relacion =(double) width / height;
        if(relacion < 1.2){//reducir altura
            this.height = (int) (width/RELACION_ANCHURA_ALTURA);
        }else{//reducir anchura
            this.width = (int)  (height * RELACION_ANCHURA_ALTURA);
        }

    }

    @Override
    public void run () {
        while(run) { //para que se ejecute de forma continua
            if(holder.getSurface().isValid()){ //si el surface esta bien para editarlo
                //Se desbloquea el canvas para dibujar
                Canvas canvas = holder.lockCanvas();
                //dibujamos
                painting( canvas );
                //volvemos a bloquear el canvas
                holder.unlockCanvasAndPost( canvas );
            }
        }
    }

    public void startThread(){
        //iniciamos el hilo
        this.thread = new Thread(this);
        setRun(true);
        thread.start();
    }

    public void stopThread(){
        //paramos el hilo
        boolean stop = true;
        setRun( false );
        while(stop) {
            try {
                this.thread.join();
                stop = false;
            } catch ( InterruptedException e ) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void painting ( Canvas canvas ) {
        //comprobamos que haya alguna imagen disponible, si no es así no podemos enseñar nada
        if ( Conector.imagen != null ) {

            //Recibimos la imagen en formato RAW, esto en Android es formato yuv, el formato yuv
            //tenemos que convertirlo a un formato bitmap para mostrarlo en la pantalla
            try {
                yuv = new YuvImage( Conector.imagen.getContent(), 17, 176, 144, null );
                out = new ByteArrayOutputStream();
                yuv.compressToJpeg( new Rect( 0, 0, 176, 144 ), 100, out );
                bytes = out.toByteArray();
                bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length );
                configuracion = bitmap.getConfig();

                //para poder mostrar el bitmap en el surfaceview tenemos que indicarle que es un bitmap mutable
                bitmap = bitmap.copy( configuracion, true );

                //escalamos el bitmap para que no se vea tan pequeño y se adapte a la pantalla
                bitmap = Bitmap.createScaledBitmap( bitmap, width, height, false );

                //dibujamos el bitmap en el canvas, cuando trabajamos en un surface view, el surface view
                //tiene un canvas y tinemos que dibujar en el, lo que queremos que salga en la pantalla
                canvas.drawBitmap( bitmap, 0, 0, null );
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        else{
            Paint paint = new Paint();
            //pintar el fondo del canvas del mismo color del background
            paint.setColor( Color.parseColor( "#303030" ) );

            paint.setStyle( Paint.Style.FILL );
            canvas.drawPaint( paint );

            //pintar las letras
            paint.setColor( Color.WHITE );
            paint.setColor( Color.parseColor( "#FFFFFF" ) );
            paint.setTextSize( 70 );

            String text = "Loading...";

            Rect r = new Rect();

            canvas.getClipBounds(r);
            int cHeight = r.height();
            int cWidth = r.width();
            paint.setTextAlign(Paint.Align.LEFT);
            paint.getTextBounds(text, 0, text.length(), r);
            float x = cWidth / 2f - r.width() / 2f - r.left;
            float y = cHeight / 2f + r.height() / 2f - r.bottom;
            canvas.drawText(text, x, y, paint);


        }
    }



}
