package spuzi.atenea.Client.Classes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by spuzi on 23/03/2017.
 */


public class XMLCameraParser {

    private String xml;
    private Camera camera;


    private final String CABECERA_MAC  = "mac";
    private final String CABECERA_IP_PUBLICA  = "ip_publica";
    private final String CABECERA_IP_PRIVADA  = "ip_privada";
    private final String CABECERA_PUERTO  = "puerto";
    private final String CABECERA_NOMBRE  = "nombre";
    private final String CABECERA_PASSWORD  = "password";


    private XmlPullParserFactory xmlPullParserFactory;
    private XmlPullParser parser;

    private int evento =-2;
    String tagAcutal = "";

    public XMLCameraParser ( ){
        camera = new Camera(  );
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware( true );
            parser = xmlPullParserFactory.newPullParser();
        } catch ( XmlPullParserException e ) {
            e.printStackTrace();
        }
    }

    public void setXML (String xml){
        this.xml= xml;
    }

    public void parse (){
        try {
            parser.setInput( new StringReader( this.xml ) );

            evento = parser.getEventType();
            while (evento != XmlPullParser.END_DOCUMENT)  {
                try{
                    switch ( evento ){
                        case XmlPullParser.START_TAG: tagAcutal = parser.getName();break;//nueva etiqueta
                        case XmlPullParser.TEXT://el texto de la etiqueta
                            switch ( tagAcutal ){


                                case CABECERA_MAC: camera.setMac( parser.getText() );break;
                                case CABECERA_IP_PUBLICA: camera.setPublicIP( parser.getText() );break;
                                case CABECERA_IP_PRIVADA: camera.setPrivateIP( parser.getText() );break;
                                case CABECERA_PUERTO: camera.setPort( Integer.parseInt( parser.getText() ) );break;
                                case CABECERA_NOMBRE: camera.setName( parser.getText() );break;
                                case CABECERA_PASSWORD: camera.setPassword( parser.getText() );break;

                            }
                            break;
                    }
                    evento = parser.next();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } catch ( XmlPullParserException e ) {
            e.printStackTrace();
        }
    }


    public Camera getCamera () {
        return camera;
    }
}
