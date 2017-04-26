package spuzi.atenea.Common;

/**
 * Created by spuzi on 01/04/2017.
 *
 * Do a work in a thread
 */

public abstract class Worker implements Runnable {

    private Thread thread;
    private boolean isRunning;

    /**
     * Overwrite what the worker has to do in the background
     */
    @Override
    public abstract void run ();

    /**
     * Start the thread
     */
    public void startWorker(){
        System.out.println("Starting thread " + getClass().getName());
        this.thread = new Thread(this);
        this.isRunning = true;
        thread.start();
    }

    /**
     * Stop the thread
     */
    public void stopWorker(){
        boolean stop = true;
        this.isRunning = false;
        while(stop) {
            try {
                System.out.println("Ending thread " + getClass().getName());
                this.thread.join();
                stop = false;
            } catch ( InterruptedException e ) {
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * Return true if the thread is running
     */
    public boolean isRunning(){
        return this.isRunning;
    }


}
