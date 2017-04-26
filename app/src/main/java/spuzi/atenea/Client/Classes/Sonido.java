package spuzi.atenea.Client.Classes;

import android.util.Base64;

/**
 * Created by spuzi on 23/03/2017.
 */


public class Sonido {
    private byte[] content;
    private int length;
    private FormatoSonido formato;

    public Sonido () {
    }

    public Sonido ( byte[] content ){
        this.content = content;
    }

    public byte[] getContent () {
        return content;
    }
    public void setContent ( byte[] content ) {
        this.content = content;
    }

    public int getLength () {
        return content.length;
    }

    public void setLength ( int length ) {
        this.length = length;
    }

    public FormatoSonido getFormato () {
        return formato;
    }

    public void setFormato ( FormatoSonido formato ) {
        this.formato = formato;
    }

    public String getBase64 (){
        return Base64.encodeToString( this.content, Base64.NO_WRAP );
    }
}
