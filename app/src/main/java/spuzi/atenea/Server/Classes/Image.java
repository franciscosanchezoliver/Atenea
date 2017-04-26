package spuzi.atenea.Server.Classes;

import android.util.Base64;

/**
 * Created by spuzi on 09/03/2017.
 */


public class Image {
    private byte[] content;
    private int width;
    private int height;


    /** CONSTRUCTORES **/
    public Image (){
        content = new byte[0];
    }

    public Image ( byte[] content ){
        this.content = content;
    }

    public Image ( byte[] content, int width, int height) {
        this.content = content;
        this.width = width;
        this.height = height;
    }


    /** GETTERS Y SETTERS **/
    public byte[] getContent () {
        return content;
    }

    public void setContent ( byte[] content ) {
        this.content = content;
    }

    public void setWidth ( int width ) {
        this.width = width;
    }

    public int getWidth () {
        return width;
    }

    public int getHeight () {
        return height;
    }

    public void setHeight ( int height ) {
        this.height = height;
    }

    public int getLength () {
        return content.length;
    }


}

