package spuzi.atenea.Client.Classes;

import android.app.ProgressDialog;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.StringTokenizer;

import spuzi.atenea.Client.Screens.CameraViewer;
import spuzi.atenea.Common.Buffer;
import spuzi.atenea.Common.Sound;
import spuzi.atenea.Common.Image;
import spuzi.atenea.Common.Worker;

import static spuzi.atenea.Client.Classes.Speaker.criticalSection;

/**
 * Created by spuzi on 23/03/2017.
 */


public class Connector extends Worker{
    private static final int TIME_LIMIT = 10000;
    private int MAX_CONECTION_ATTEMPT = 15;
    private Socket socket ;
    private String password;
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    private BufferedInputStream input=null;
    private CameraViewer activity;


    static Image imagen;
    static Sound sonido;


    static Buffer bufferSounds;
    //variable que indica cuando el hilo se esta ejecutando o no.
    private boolean canAccessToServer = false;



    private int expectedSize = -1;

    byte[] data = new byte[ 0 ];
    int totalRead;
    int read;

    private String IP ;
    private int port;



    public Connector ( String IP , int puerto , String password , CameraViewer activity) {
        this.password = password;
        socket = new Socket(  );//abrimos un socket para comunicarnos con el servidor
        this.port = puerto;
        this.IP = IP;
        bufferSounds  = new Buffer( 30 );

        this.activity = activity;
    }

    @Override
    public void run () {
        while(super.isRunning()){
            try {
                tryToConnectWithServer( IP );

                //connect to server
                try {
                    socket.connect( new InetSocketAddress( IP, port )
                            , TIME_LIMIT );
                }catch(SocketException e){
                    if(e.getMessage().equals( "Already connected" )){
                        while ( super.isRunning() ) {
                            try {
                                //leemos las imagenes que envia el servidor
                                totalRead = 0;

                                while ( totalRead < expectedSize ) {
                                    read = input.read( data, totalRead, expectedSize - totalRead );
                                    if ( read == -1 ) {
                                        // throw new IOException( "No hay suficientes datos en el stream" );
                                    }
                                    totalRead += read;
                                }

                                //creamos un objeto imagen con los datos que nos ha enviado
                                imagen = new Image();
                                imagen.setContent( data );

                                //indicamos al servidor que ya hemos recibido la imagen y que puede seguir enviando datos
                                // dataOutputStream.writeUTF( "OK" );
                            }catch(Exception excepcion){
                                System.out.println( excepcion.getMessage() );
                            }

                        }
                    }
                    else{
                        //OTROS ERRORES SIN TRATAR
                        while(super.isRunning()){
                            System.out.println(e.getMessage());
                        }
                    }

                }

                //buffer para escribir al servidor
                this.dataOutputStream = new DataOutputStream( socket.getOutputStream() );

                //buffer para recibir del servidor
                input = new BufferedInputStream( socket.getInputStream() );
                dataInputStream = new DataInputStream( socket.getInputStream() );

                //creamos una peticion para el servidor con las credenciales
                Peticion peticion = new Peticion( password );

                //enviamos la peticion
                dataOutputStream.writeBytes( peticion.getXml() );

                Cronometro cronometro = new Cronometro();
                Cronometro bucle1 = new Cronometro();
                Cronometro bucle2 = new Cronometro();

                while ( super.isRunning() ) {
                    System.out.println("Tiempo:" + bucle1.getCurrentInMiliseconds());

                    bucle1.start();

                    expectedSize = 100000;
                    totalRead = 0;
                    data = new byte[expectedSize];

                    int lengthImage =-1;
                    int legnthSound = -1;

                    while ( totalRead < expectedSize ) {
                        read = input.read( data, totalRead, expectedSize - totalRead );
                        if ( read == -1 ) {
                            throw new IOException( "No hay suficientes datos en el stream" );
                        }
                        totalRead += read;

                        if(lengthImage == -1 && legnthSound == -1){

                            byte[] tamanioImagen = Arrays.copyOfRange( data , 0, 4 );
                            lengthImage = ByteBuffer.wrap( tamanioImagen ).getInt();

                            byte[] tamanioSonido = Arrays.copyOfRange( data, 4, 8 );
                            legnthSound = ByteBuffer.wrap( tamanioSonido ).getInt();

                            expectedSize = lengthImage + legnthSound + 8;
                        }

                    }


                    byte[] soundData = null;
                    byte[] imageData = null;

                    try {
                        imageData = Arrays.copyOfRange( data, 8 , 8 + lengthImage );
                        soundData= Arrays.copyOfRange( data, 8 + lengthImage , expectedSize );
                    }catch(Exception e){
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }

                    sonido = new Sound( soundData );
                    if( sonido.getLength() > 0  && criticalSection ) {
                        bufferSounds.add( sonido );
                        //System.out.println("sonido insertado: " + bufferSounds.size() );
                    }

                    //creamos un objeto imagen con los datos que nos ha enviado
                    imagen = new Image( imageData , 0 , 0);

                    //indicamos al servidor que ya hemos recibido la imagen y que puede seguir enviando datos
                    dataOutputStream.writeUTF( "OK" );
                }

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

    }

    public void cerrarComunicacion(){
        try{
            if(dataOutputStream != null)
                dataOutputStream.close();
            if(dataInputStream != null)
                dataInputStream.close();

            stopWorker();

            if(input != null)
                input.close();

            socket.close();
        } catch (IOException e) {
            Log.e( "ERROR:", "al cerrar el socket " + this.socket.getRemoteSocketAddress() );
            e.printStackTrace();
        }
    }


    @Override
    public void stopWorker () {
        super.stopWorker();
        imagen = null;
    }


    public String ping(String direccion){
        String resultado ="";
        try {
            Process process = Runtime.getRuntime().exec( "/system/bin/ping -c 10 " + direccion );
            BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
            int i;
            char[] buffer = new char[ 4096 ];
            StringBuffer output = new StringBuffer();
            while ( ( i = reader.read( buffer ) ) > 0 )
                output.append( buffer, 0, i );
            reader.close();
            resultado = output.toString();
        }catch ( IOException e ){
            Log.e("Error", "Al hacer ping al servidor");
            e.printStackTrace();
        }
        return resultado;
    }


    public boolean analyzePing ( String str ){
        StringTokenizer tokenizer = new StringTokenizer( str, "," );
        tokenizer.nextToken();
        str = tokenizer.nextToken();
        tokenizer = new StringTokenizer( str," " );
        int numberOfPacketRecieveFromServer = Integer.parseInt( tokenizer.nextToken() );
        if(numberOfPacketRecieveFromServer == 0) {
            Log.e( "Error:", "server unreachable" );
            return false;
        }
        else{
            System.out.println("server reachable.");
            return true;
        }
    }

    public void tryToConnectWithServer ( String serverIP ) {
        String str = "";
        int numberAttemptsOfConnection = 0 ;

        //the activity shows a ProgressDialog
        this.activity.showProgressDialog();

        System.out.println("Trying to connect with server, this may take a few minutes");
        while( numberAttemptsOfConnection < MAX_CONECTION_ATTEMPT && !canAccessToServer ){
            System.out.println("Attempt " + numberAttemptsOfConnection + "of " + MAX_CONECTION_ATTEMPT);
            str = ping( serverIP );
            canAccessToServer =  analyzePing( str );
            numberAttemptsOfConnection++;
            activity.incrementProgressDialog( MAX_CONECTION_ATTEMPT / numberAttemptsOfConnection );
        }
        if( canAccessToServer ) {
            System.out.println( "Connected!" );
            activity.hideProgressDialog();
        }
        else {
            System.out.println( "No se pudo conectar, apagando la aplicación" );
            activity.showsMessageCouldNotConnectWithServer();
        }
    }


}
