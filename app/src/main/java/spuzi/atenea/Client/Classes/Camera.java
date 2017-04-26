package spuzi.atenea.Client.Classes;

/**
 * Created by spuzi on 23/03/2017.
 */


public class Camera {

    private String mac;
    private String privateIP;
    private String publicIP;
    private int port;
    private String name;
    private String password;

    public Camera () {
    }

    public Camera ( String mac, String privateIP, String publicIP, int port, String name, String password ) {
        this.mac = mac;
        this.privateIP = privateIP;
        this.publicIP = publicIP;
        this.port = port;
        this.name = name;
        this.password = password;
    }

    public void printInfo (){
        System.out.println("Camera Information:");
        System.out.println("\tMac:" + this.mac);
        System.out.println("\tPrivate IP:" + this.privateIP);
        System.out.println("\tPublic IP:" + this.publicIP);
        System.out.println("\tPort:" + this.port);
        System.out.println("\tPassword:" + this.password);
    }

    /** GETTERS AND SETTERS  */

    public String getMac () {
        return mac;
    }

    public void setMac ( String mac ) {
        this.mac = mac;
    }

    public String getPrivateIP () {
        return privateIP;
    }

    public void setPrivateIP ( String privateIP ) {
        this.privateIP = privateIP;
    }

    public String getPublicIP () {
        return publicIP;
    }

    public void setPublicIP ( String publicIP ) {
        this.publicIP = publicIP;
    }

    public int getPort () {
        return port;
    }

    public void setPort ( int port ) {
        this.port = port;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword ( String password ) {
        this.password = password;
    }
}

