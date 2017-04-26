package spuzi.atenea.Client.Classes;

/**
 * Created by spuzi on 23/03/2017.
 */

public class Cronometro {

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
