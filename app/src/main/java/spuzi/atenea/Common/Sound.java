package spuzi.atenea.Common;

/**
 * Created by spuzi on 23/03/2017.
 */

public class Sound extends Data {

    private long time; //when the sound was recorded in nanoseconds

    public Sound (){
        super();
        this.time = System.nanoTime();
    }

    public Sound ( byte[] content ){
        super(content);
        this.time = System.nanoTime();
    }

    /** GETTERS Y SETTERS **/
    public long getTime () {
        return time;
    }

    public void setTime ( long time ) {
        this.time = time;
    }
}
