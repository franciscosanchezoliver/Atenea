package spuzi.atenea.Common;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by spuzi on 29/03/2017.
 *
 * Check the state of the network
 */

public class StatusNetwork extends Worker {

    private final String URL_PUBLIC_IP = "http://spuzi.esy.es/checkPublicIP.php";//To check the public ip, it sends a request a webpage
    private String mac;
    private String publicIP;
    private String privateIP;
    private NetworkActivity activity;


    public StatusNetwork ( NetworkActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void run () {
        checkPrivateIP();
        if(privateIP != null) {//if there is not private ip then we can't continue
            checkPublicIP();
            checkMacAddress();
        }
        super.stopWorker();
    }

    /**
     * Set the private IP if the device is connected to a network
     */
    private void checkPrivateIP () {
        System.out.println("Getting private IP");
        ConnectivityManager connectivityManager = (ConnectivityManager) this.activity.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo                 = connectivityManager.getActiveNetworkInfo();


        //Check if there is connection to a Network , you can be connected to a LAN without being connected to internet
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            WifiManager wifiManager = (WifiManager) this.activity.getSystemService( WIFI_SERVICE );
            privateIP = Formatter.formatIpAddress( wifiManager.getConnectionInfo().getIpAddress() );
            System.out.println("Connected to local network IP:"+ privateIP +" " );
        }else{
            System.out.println("Not connected to any network.");
            this.activity.onNetworkChecked( NetworkStatusEnum.NO_NETWORK );
        }
    }

    /**
     * Set the public IP if the device is connected to Internet
     */
    private void checkPublicIP () {
        URL url;
        HttpURLConnection urlConnection = null;
        InputStream in;

        try {
            System.out.println("Getting public IP.");
            url = new URL( URL_PUBLIC_IP);
            urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream( urlConnection.getInputStream() );
            byte[] array = new byte[ 50 ];
            int size = in.read( array, 0, 50 );
            array = Arrays.copyOfRange( array, 0, size );
            publicIP = ( new String( array, "UTF-8" ) );
            System.out.println("Public IP : " + publicIP );
            this.activity.onNetworkChecked( NetworkStatusEnum.CONNECTED );

        } catch ( Exception e ) {
            publicIP = "SIN IP PUBLICA";
            System.out.println("Without public IP.");
            Log.e( "ERROR:", "getting the public ip" );
            e.printStackTrace();
            this.activity.onNetworkChecked( NetworkStatusEnum.NO_INTERNET );
        } finally {
            urlConnection.disconnect();
        }

    }

    /**
     * Set the device's MAC address
     */
    private void checkMacAddress () {
        try {
            System.out.println("Getting the MAC address.");
            List<NetworkInterface > all = Collections.list( NetworkInterface.getNetworkInterfaces() );
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    System.out.println("Can't get the MAC.");
                    mac = "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                mac = res1.toString();
                System.out.println("The MAC is " + mac);
            }
        } catch (Exception ex) {
            System.out.println("Can't get the MAC.");
        }
    }


    public void checkNetworkStatus(){
        super.startWorker();
    }

    /**  GETTERS AND SETTERS */

    public String getMac () {
        return mac;
    }

    public String getPublicIP () {
        return publicIP;
    }

    public String getPrivateIP () {
        return privateIP;
    }

    public void setMac ( String mac ) {
        this.mac = mac;
    }

    public void setPublicIP ( String publicIP ) {
        this.publicIP = publicIP;
    }

    public void setPrivateIP ( String privateIP ) {
        this.privateIP = privateIP;
    }
}
