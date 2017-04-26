package spuzi.atenea.Server.Classes;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import spuzi.atenea.Common.Worker;

/**
 * Created by spuzi on 09/03/2017.
 */


public class TCPServer extends Worker {

    private ServerSocket socketAccept; //socket to accept new connections
    private Integer numberClients = 0 ;
    private CameraPreview cameraPreview; //stops the camera if there are no clients connected
    private int port;//listening port for new clients
    private UpdateDBInCloud updateDBInCloud;//update the data of this device: mac, private and public ip, password and port
    private String mac;
    private String privateIP;
    private String publicIP;
    private String password;
    private String name;

    public TCPServer ( String mac, String privateIP, String publicIP, String password, int port , CameraPreview cameraPreview ){
        this.cameraPreview = cameraPreview;
        this.mac = mac;
        this.privateIP = privateIP;
        this.publicIP = publicIP;
        this.password = password;
        this.port = port;
        this.name = mac.replace(":" , ""); //the device's name is the mac without ":"
        System.out.println("Data of this device:");
        System.out.println("\tMAC:" + mac);
        System.out.println("\tprivate IP:" + privateIP );
        System.out.println("\tpublic IP:" + publicIP );
        System.out.println("\tport:" + port);
        System.out.println("\tname:" + this.name);
        System.out.println("\tpassword:" + password);
    }

    @Override
    public void run() {
        openPort();//open port to listen to new clients

        while(super.isRunning()){
            Log.d( "Server TCP:" , "listening for new clients..." );
            try {
                //Se espera a un cliente nuevo, si se acepta la conexión se devuelve un socket, este
                //socket se utilizará para futuras comunicaciones con el cliente.
                Socket newSocket = socketAccept.accept();

                //A Thread that attend the new client
                ClientHandler clientHandler = new ClientHandler( newSocket , this.password);
                clientHandler.startThread();

            } catch (IOException e) {
                Log.e("ERROR:", "error accepting new clients.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Open a port to listen to new clients
     */
    private void openPort (){
        try {
            this.socketAccept = new ServerSocket(); // create an unbound socket first
            this.socketAccept.setReuseAddress( true );
            this.socketAccept.bind( new InetSocketAddress( port ) ); // now bind it
            System.out.println("Server: port " + port + " open." );

            //update in the cloud the data of this device
            updateDBInCloud = new UpdateDBInCloud( mac, publicIP, privateIP, port, name , password  );
            updateDBInCloud.startWorker();
        } catch (Exception e) {
            Log.e("ERROR:", "error opening port "+ port +" ." );
            e.printStackTrace();
        }
    }

    @Override
    public void stopWorker () {
        try {
            System.out.println("Closing socket that were listening to new clients.");
            socketAccept.close();
        } catch ( IOException e ) {
            System.out.println("Closed.");
        }
        super.stopWorker();
    }


    public void addNewClient(){
        synchronized ( numberClients ){
            numberClients++;
            startCameraIfFirstClient();
        }
    }

    public void removeClient(){
        synchronized ( numberClients ){
            numberClients--;
            stopCameraIfNoClients();
        }
    }

    /**
     * If there are no clients connected to the server then it is no sense to keep the camera on.
     * It would make the device to overheat
     */
    private void stopCameraIfNoClients(){
        //if the camera was running but there is no clients now, then stops the camera
        if( numberClients == 0 && cameraPreview.isRunning() ){
            cameraPreview.pause();
        }
    }

    private void startCameraIfFirstClient (){
        //start the camera when the first client come and the camera wasn't running
        if(numberClients== 1 && !cameraPreview.isRunning()){
            cameraPreview.resume();
        }
    }

}
