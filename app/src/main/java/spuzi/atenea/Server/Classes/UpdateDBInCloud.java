package spuzi.atenea.Server.Classes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import spuzi.atenea.Common.Worker;

/**
 * Created by spuzi on 09/03/2017.
 * Connects to a database in the cloud and updates the data for this device ( mac , ip public and private, port , name and password)
 * To communicate with the webpage this class send a POST request
 */

public class UpdateDBInCloud extends Worker {
    private final String USER_AGENT = "Mozilla/5.0";
    private final String url = "http://spuzi.esy.es/camara/addCamara.php"; //adds or update a device in the DB
    private String mac = "mac=";
    private String publicIP = "ip_publica=";
    private String privateIP = "ip_privada=";
    private String port = "puerto=";
    private String name = "nombre=";
    private String password = "password=";

    public UpdateDBInCloud ( String mac , String publicIP, String privateIP, int port , String name , String password ){
        this.mac += mac;
        this.publicIP += publicIP;
        this.privateIP += privateIP;
        this.port += port;
        this.name += name;
        this.password += password;
    }

    @Override
    public void run () {
            try {
                URL obj = new URL( url );
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //add reuqest header
                con.setRequestMethod( "POST" );
                con.setRequestProperty( "User-Agent", USER_AGENT );
                con.setRequestProperty( "Accept-Language", "en-US,en;q=0.5" );

                String urlParameters = mac + "&" + publicIP + "&" + privateIP + "&" + port + "&" + name + "&" + password;

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

                System.out.println( response.toString() );
                super.stopWorker();
            } catch ( Exception e ) {
                System.out.println( e.getMessage() );
            }
    }

}
