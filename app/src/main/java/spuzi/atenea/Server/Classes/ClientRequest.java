package spuzi.atenea.Server.Classes;

/**
 * Created by spuzi on 09/03/2017.
 */


public class ClientRequest {
    private String password;

    public ClientRequest () {
    }

    public ClientRequest ( String password ) {
        this.password = password;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword ( String password ) {
        this.password = password;
    }
}
