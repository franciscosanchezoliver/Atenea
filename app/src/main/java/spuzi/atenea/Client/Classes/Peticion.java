package spuzi.atenea.Client.Classes;

/**
 * Created by spuzi on 23/03/2017.
 * Es la peticion que le hace el cliente al servidor, aquí le indica que tipo de datos necesita, en que formato los necesita, etc...
 * Cualquier cosa que le queramos indicar al servidor lo indicaremos en la petición.
 */

public class Peticion {

    private String xml;
    private static final String CABECERA_XML  = "<?xml version='1.0' encoding='UTF-8'?>";
    private static final String CABECERA_PETICION  = "<request>";
    private static final String CABECERA_CIERRE_PETICION = "</request>";
    private static final String CABECERA_PASSWORD  = "<password>";
    private static final String CABECERA_CIERRE_PASSWORD = "</password>";
    private static final String CABECERA_FORMATO_IMAGEN = "<image-format>";
    private static final String CABECERA_CIERRE_FORMATO_IMAGEN = "</image-format>";
    private static final String CABECERA_CALIDAD_IMAGEN = "<image-quality>";
    private static final String CABECERA_CIERRE_CALIDAD_IMAGEN = "</image-quality>";
    private static final String CABECERA_FORMATO_SONIDO = "<sound-format>";
    private static final String CABECERA_CIERRE_FORMATO_SONIDO = "</sound-format>";


    public Peticion ( String password){
        xml = "";

        xml += CABECERA_XML;
        xml += CABECERA_PETICION;
        xml += CABECERA_PASSWORD + password + CABECERA_CIERRE_PASSWORD;
        xml += CABECERA_FORMATO_IMAGEN + FormatoImagen.RAW + CABECERA_CIERRE_FORMATO_IMAGEN;
        xml += CABECERA_CALIDAD_IMAGEN + 100 + CABECERA_CIERRE_CALIDAD_IMAGEN;
        xml += CABECERA_FORMATO_SONIDO + FormatoSonido.RAW + CABECERA_CIERRE_FORMATO_SONIDO;
        xml += CABECERA_CIERRE_PETICION;
    }

    public Peticion ( String password, FormatoImagen formatoImagen , int calidadImagen, FormatoSonido formatoSonido ){
        if(formatoImagen == null)
            formatoImagen = FormatoImagen.RAW;
        if(formatoSonido == null)
            formatoSonido = formatoSonido.RAW;
        xml = "";
        xml += CABECERA_XML;
        xml += CABECERA_PETICION;
        xml += CABECERA_PASSWORD + password + CABECERA_CIERRE_PASSWORD;
        xml += CABECERA_FORMATO_IMAGEN + formatoImagen + CABECERA_CIERRE_FORMATO_IMAGEN;
        xml += CABECERA_CALIDAD_IMAGEN + calidadImagen + CABECERA_CIERRE_CALIDAD_IMAGEN;
        xml += CABECERA_FORMATO_SONIDO + formatoSonido + CABECERA_CIERRE_FORMATO_SONIDO;
        xml += CABECERA_CIERRE_PETICION;
    }

    public String getXml () {
        return xml;
    }
}
