package spuzi.atenea.Common;

/**
 * Created by spuzi on 23/03/2017.
 */

public class LaImagen extends Data {

    private int width;
    private int height;

    public LaImagen(){
        super();
        this.width = 0;
        this.height = 0;
    }

    public LaImagen(byte[] content , int width , int height){
        super(content);
        this.width = width;
        this.height = height;
    }

    /** GETTERS Y SETTERS **/
    public int getWidth () {
        return width;
    }

    public void setWidth ( int width ) {
        this.width = width;
    }

    public int getHeight () {
        return height;
    }

    public void setHeight ( int height ) {
        this.height = height;
    }
}
