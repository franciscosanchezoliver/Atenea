package spuzi.atenea.Client.Classes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import spuzi.atenea.Client.Screens.ConnectTo;
import spuzi.atenea.Common.Worker;


/**
 * Created by spuzi on 23/03/2017.
 *
 * Check if the remote camera exist
 */

public class RemoteCameraStatusChecker extends Worker {
    private final String USER_AGENT = "Mozilla/5.0";
    private final String url = "http://spuzi.esy.es/camara/findCameraByName.php";
    private String name = "nombre=";
    private ConnectTo client;


    public RemoteCameraStatusChecker ( String name, ConnectTo client ){
        this.name += name;
        this.client = client;
    }

    @Override
    public void run () {
        try {
            URL obj = new URL( url );
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod( "POST" );
            con.setRequestProperty( "User-Agent", USER_AGENT );
            con.setRequestProperty( "Accept-Language", "en-US,en;q=0.5" );

            String urlParameters = name;
            //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

            // Send post request
            con.setDoOutput( true );
            DataOutputStream wr = new DataOutputStream( con.getOutputStream() );
            wr.writeBytes( urlParameters );
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println( "\nSending 'POST' request to URL : " + url );
            System.out.println( "Post parameters : " + urlParameters );
            System.out.println( "Response Code : " + responseCode );

            BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ( ( inputLine = in.readLine() ) != null ) {
                response.append( inputLine );
            }
            in.close();

            String xml = response.toString();

            if(xml.equals( "NOT FOUND" )){//this camera was not found in the DB so it doesnt exist
                this.client.onRemoteCameraStatusRecieved( RemoteCameraStatus.NOT_FOUND, null );
            }
            else if(!xml.equals( "ERROR" )) {
                XMLCameraParser xmlCameraParser = new XMLCameraParser();
                xmlCameraParser.setXML( xml );
                xmlCameraParser.parse();
                Camera camera = xmlCameraParser.getCamera();
                this.client.onRemoteCameraStatusRecieved( RemoteCameraStatus.AVAILABLE , camera );
            }

        } catch ( Exception e ) {
            System.out.println( e.getMessage() );
        }
        finally {
            super.stopWorker();
        }
    }



}
