package spuzi.atenea.Server.Classes;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;

import spuzi.atenea.Server.Interfaces.OnPortsClosedListener;
import spuzi.atenea.Server.Interfaces.OnPortsOpenListener;

/**
 * Created by spuzi on 15/03/2017.
 */

public class PortOpenerService extends Service{

    private final Binder binder = new LocalBinder();
    private String privateIP;
    private int port;
    UpnpService upnpService;
    PortMapping[] desiredMapping ;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder{
        public PortOpenerService getService(){
            // Return this instance of LocalService so clients can call public methods
            return PortOpenerService.this;
        }
    }

    /** Return the IBinder, this is an interface that the client can use to interact with this service  */
    @Nullable
    @Override
    public IBinder onBind ( Intent intent ) {
        return binder;
    }

    /** Methods for clients  */

    /** Forward the ports of the NAT to let client connect with the server from outside the LAN  */
    public void forwardNATPorts ( String privateIP, int port ){
        this.privateIP = privateIP;
        this.port = port;
        desiredMapping = new PortMapping[2];

        try {
            System.out.println("Trying to forward the port " + this.port +" with IP " + this.privateIP );

            //creates a port mapping configuration with the external/internal port, an internal host IP, the protocol and an optional description
            desiredMapping[ 0 ] = new PortMapping( port, privateIP, PortMapping.Protocol.TCP );
            desiredMapping[ 1 ] = new PortMapping( port, privateIP, PortMapping.Protocol.UDP );

            //starting the UPnP service
            upnpService = new UpnpServiceImpl( new AndroidUpnpServiceConfiguration() );
            RegistryListener registryListener = new PortMappingListener( desiredMapping );
            upnpService.getRegistry().addListener( registryListener );
            upnpService.getControlPoint().search();

            System.out.println("Port forwarded successfully " + this.port +" with IP " + this.privateIP );
        }catch ( Exception e ){
            Log.e( "ERROR:" , "Couldn't forward " + this.port +" with IP " + this.privateIP );
            System.out.println(e.getMessage());

        }
    }

    /** Close the forwarded NAT ports */
    public void closeForwardedPorts ( final OnPortsClosedListener OnPortsClosedListener ){

        Thread t = new Thread(){
            public void run(){

                if(upnpService != null) {
                    upnpService.shutdown();
                    System.out.println("Port " + port +" with IP " + privateIP + " closed." );
                    OnPortsClosedListener.onPortsClosed();
                    System.out.println("Closing service PortOpenerService" );
                    stopSelf();
                }
            }
        };
        t.start();

    }



}




