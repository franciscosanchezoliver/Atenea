package spuzi.atenea.Server.Classes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by spuzi on 09/03/2017.
 */


public class XMLPullParser{

    private String xml;
    private ClientRequest clientRequest;
    private final String HEADER_PASSWORD = "password";
    private String tagActual ="";

    public XMLPullParser( String xml){
        this.xml = xml;
        clientRequest = new ClientRequest(  );
    }

    public ClientRequest parse (){
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware( true );
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            parser.setInput( new StringReader( this.xml ) );

            int evento = parser.getEventType();
            while (evento != XmlPullParser.END_DOCUMENT){
                switch ( evento ){
                    case XmlPullParser.START_TAG: tagActual = parser.getName(); break; //nueva etiqueta
                    case XmlPullParser.TEXT: //el texto de la etiqueta
                        switch ( tagActual ){
                            case HEADER_PASSWORD: clientRequest.setPassword( parser.getText() );break;
                        }
                        break;
                }
                evento = parser.next();
            }
        } catch ( XmlPullParserException e ) {
            e.printStackTrace();
        }
        catch ( IOException e){
            e.printStackTrace();
        }
        return this.clientRequest;
    }


}
