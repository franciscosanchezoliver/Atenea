package spuzi.atenea.Server.Classes;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by spuzi on 09/03/2017.
 */


public class Message {

    private Image image;
    private Sound sound;
    private byte[] message;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream( );

    /**
     * CONSTRUCTORS
     */

    public Message ( Image image, Sound sound ) {
        //if image/sound is null then create a empty image/sound
        this.image = (image == null ) ? new Image( ) : image;
        this.sound = (sound == null) ? new Sound( ) : sound;
        createMessage();
    }


    public byte[] getMessage () {
        return message;
    }

    /**
     *  0-4 bytes -> image's length
     *  4-8 byte -> sound's length
     */
    private void createMessage(){
        byte imageLength[]  = ByteBuffer.allocate( 4 ).putInt( image.getLength() ).array();
        byte soundLength[]  = ByteBuffer.allocate( 4 ).putInt( sound.getLength() ).array();

        try {
            baos.write( imageLength );
            baos.write( soundLength );
            baos.write( image.getContent() );
            baos.write( sound.getContent() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        message =  baos.toByteArray() ;
    }



}

