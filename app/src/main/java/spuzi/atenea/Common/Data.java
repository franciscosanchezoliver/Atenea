package spuzi.atenea.Common;

/**
 * Created by spuzi on 23/03/2017.
 */

public abstract class Data {
    protected byte[] content;

    public Data(){
        this.content = new byte[0];
    }

    public Data( byte[] newByte){
        this.content = newByte;
    }

    public byte[] getContent(){
        return this.content;
    }

    public void setContent( byte[] newContent ){
        this.content = newContent;
    }

    public int getLength(){
        return this.content.length;
    }


}
