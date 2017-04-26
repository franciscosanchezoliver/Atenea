package spuzi.atenea.Common;

/**
 * Created by spuzi on 09/03/2017.
 */

public class Chronometer {

    private long timeStart;

    public void start(){
        timeStart = System.currentTimeMillis();
    }

    public long getCurrentInMiliseconds(){
        return System.currentTimeMillis() - timeStart  ;
    }

    public int getCurrentInSeconds(){
        return (int) ((System.currentTimeMillis() - timeStart)/1000);
    }


}