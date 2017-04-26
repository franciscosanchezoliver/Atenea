package spuzi.atenea.Client.Classes;

import android.util.Base64;

/**
 * Created by spuzi on 23/03/2017.
 */

public class Imagen {
    private byte[] content;
    private int anchura;
    private int altura;
    private int length;
    private FormatoImagen formato;
    private int calidad;

    /** CONSTRUCTORES **/
    public Imagen (){}

    public Imagen ( byte[] content ){
        this.content = content;
    }

    public Imagen ( byte[] content, int anchura, int altura, int length ) {
        this.content = content;
        this.anchura = anchura;
        this.altura = altura;
        this.length = length;
    }

    /** GETTERS Y SETTERS **/
    public byte[] getContent () {
        return content;
    }

    public String getBase64 (){
        return Base64.encodeToString( this.content, Base64.NO_WRAP );
    }

    public void setAnchura ( int anchura ) {
        this.anchura = anchura;
    }

    public void setContent ( byte[] content ) {
        this.content = content;
    }

    public int getAnchura () {
        return anchura;
    }

    public int getAltura () {
        return altura;
    }

    public void setAltura ( int altura ) {
        this.altura = altura;
    }

    public int getLength () {
        return length;
    }

    public void setLength ( int length ) {
        this.length = length;
    }


    public FormatoImagen getFormato () {
        return formato;
    }

    public void setFormato ( FormatoImagen formato ) {
        this.formato = formato;
    }

    public int getCalidad () {
        return calidad;
    }

    public void setCalidad ( int calidad ) {
        this.calidad = calidad;
    }
}
