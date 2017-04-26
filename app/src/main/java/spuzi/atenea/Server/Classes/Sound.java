package spuzi.atenea.Server.Classes;

import android.util.Base64;

/**
 * Created by spuzi on 09/03/2017.
 */

public class Sound {
    private byte[] content;
    private long time; //when the sound was recorded

    public Sound () {
        content = new byte[0];
    }

    public Sound ( byte[] content ){
        this.content = content;
        this.time = System.nanoTime();
    }

    public byte[] getContent () {
        return content;
    }
    public void setContent ( byte[] content ) {
        this.content = content;
    }

    public int getLength () {
        return content.length;
    }


    public long getTime () {
        return time;
    }

    public void setTime ( long time ) {
        this.time = time;
    }

}
