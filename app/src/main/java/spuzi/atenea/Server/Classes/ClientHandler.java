package spuzi.atenea.Server.Classes;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import spuzi.atenea.Common.Sound;
import spuzi.atenea.Common.Image;

import static spuzi.atenea.Server.Classes.CameraPreview.BUFFER_IMAGES;
import static spuzi.atenea.Server.Screens.CameraOnline.tcpServer;

/**
 * Created by spuzi on 09/03/2017.
 */


public class ClientHandler implements Runnable {

    Socket socket;
    String clientMessage = ""; //message recieve form the client

    //buffer to save the request from the client
    byte[] bufferCliente = new byte[500];
    Message message = null;

    Image image;
    Sound sound;
    long lastSoundTime = -1 ;

    ClientRequest clientRequest;
    Integer messageLength = -1;
    BufferedInputStream input = null;
    BufferedOutputStream output = null;

    DataInputStream dataInputStream = null;
    Thread thread;
    private boolean run;
    private String password;



    public ClientHandler ( Socket socket , String password){
        this.socket = socket;
        this.password = password;
    }

    @Override
    public void run() {
        System.out.println( "New client connected : " + socket.getRemoteSocketAddress() );
        //inform the tcp server that a new client has come
        tcpServer.addNewClient();
        while ( run ) {
            try {
                input = new BufferedInputStream( socket.getInputStream() );
                output = new BufferedOutputStream( socket.getOutputStream() );
                dataInputStream = new DataInputStream( socket.getInputStream() );

                //read the client's request
                messageLength = input.read( bufferCliente );
                clientMessage += new String( bufferCliente, 0, messageLength );
            } catch ( IOException e ) {
                Log.e( "ERROR:", "initializing the communication's buffers with client " + this.socket.getRemoteSocketAddress() );
                System.out.println(e.getMessage());
                stopThread();
            }

            //parse the client's request
            XMLPullParser xmlParser = new XMLPullParser( clientMessage );
            clientRequest = xmlParser.parse();

            //if the password is not correct then close the communication then end the thread
            if ( !password.equals( clientRequest.getPassword() ) )
                stopThread();
            else //execute a infinte loop sending data to client
                sendData();

        }
    }

    /**
     *  Send the data the client
     */
    public void sendData (){
        while ( run ) {
            image = null;
            while(run && image == null) {
                image = (Image) BUFFER_IMAGES.getFirst();// Last image capture by the camera
            }
            sound = (Sound) Microphone.bufferSonidos.getLast();

            if(sound.getTime() == lastSoundTime) {//check if the sound hadn't sent already
                sound = null;
            }else{
                lastSoundTime = sound.getTime();//update the time of the last sound sent
            }

            message = new Message( image , sound );

            try {
                output.write( message.getMessage() , 0, message.getMessage().length );//send the image to the client
                // dataInputStream.readUTF();//wait for the client to ask for more images
                Thread.sleep( 20 );
            } catch ( Exception e ) {
                Log.e( "ERROR:", "sending data to client." );
                e.printStackTrace();
                stopThread();
            }

        }
    }

    public void startThread(){
        this.thread = new Thread(this);
        setRun(true);
        thread.start();
    }

    public void stopThread(){
        boolean stop = true;
        setRun( false );
        closeCommunication();
        System.out.println("Closing the connection with the client " + socket.getRemoteSocketAddress() );
        while(stop) {
            try {
                this.thread.join();
                stop = false;
            } catch ( InterruptedException e ) {
                Log.e("ERROR:" , "trying to stop the connection with the client " + socket.getRemoteSocketAddress() );
                e.printStackTrace();
                break;
            }
        }
    }

    public void setRun ( boolean run ) {
        this.run = run;
    }

    private void closeCommunication (){
        try {
            output.close();
            input.close();
            socket.close();
            tcpServer.removeClient();
        } catch (IOException e) {
            Log.e("ERROR:","can't close the socket " + this.socket.getRemoteSocketAddress());
            e.printStackTrace();
        }
    }


}
