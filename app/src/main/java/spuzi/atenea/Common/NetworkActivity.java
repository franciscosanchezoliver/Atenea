package spuzi.atenea.Common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import spuzi.atenea.Client.Interfaces.OnNetworkStatusChecked;
import spuzi.atenea.R;
import spuzi.atenea.Server.Interfaces.OnEventListener;
import spuzi.atenea.Server.Screens.DialogScreen;

/**
 * Created by spuzi on 30/03/2017.
 */

public abstract class NetworkActivity extends AppCompatActivity implements OnNetworkStatusChecked {

    public StatusNetwork statusNetwork  = new StatusNetwork( this );//Check if this device's network (LAN, Internet, Not connected)
    private boolean networkChecked ;//prevent the thread to check again the state of the network
    private NetworkStatusEnum stateOfTheNetwork ; //NO_NETWORK or NO_INTERNET or CONNECTED

    /**
     * Save the state of the network so we don't have to check it over and over again
     */
    @Override
    protected void onSaveInstanceState ( Bundle outState ) {
        super.onSaveInstanceState( outState );

        //If the private ip hasnt been checked then the network hasnt been checked
        if(statusNetwork.getPrivateIP() == null){
            outState.putBoolean( "NETWORK_ALREADY_CHECKED"  , false);
        }else {
            outState.putBoolean( "NETWORK_ALREADY_CHECKED", true );
            outState.putString( "MAC", statusNetwork.getMac() );
            outState.putString( "PUBLIC_IP", statusNetwork.getPublicIP() );
            outState.putString( "PRIVATE_IP", statusNetwork.getPrivateIP() );
        }

    }

    /**
     * Restore the state of the network if it has been checked already
     */
    @Override
    protected void onRestoreInstanceState ( Bundle savedInstanceState ) {
        super.onRestoreInstanceState( savedInstanceState );

        this.networkChecked =  savedInstanceState.getBoolean( "NETWORK_ALREADY_CHECKED" );

        if(networkChecked) {
            this.statusNetwork.setMac( savedInstanceState.getString( "MAC" ) );
            this.statusNetwork.setPrivateIP( savedInstanceState.getString( "PRIVATE_IP" ) );
            this.statusNetwork.setPublicIP( savedInstanceState.getString( "PUBLIC_IP" ) );
        }else{
            statusNetwork.checkNetworkStatus();
        }

    }

    /**
     * Run the code that check the status of the network  if it hasn't been run yet
     */
    @Override
    public void onResume () {
        super.onResume();
        if(!networkChecked) {//if the network hasn't been check already
            showLoadingElements();//shows the loading elements in the interface
            statusNetwork.checkNetworkStatus();//check the status of the network
        }
    }

    /**
     * Execute a code depending of the state of the network : not connected, connected to LAN, connected to Internet
     */
    @Override
    public void onNetworkChecked ( NetworkStatusEnum status ) {
        this.stateOfTheNetwork = status;
        switch ( this.stateOfTheNetwork ){
            case NO_NETWORK:
                showNoNetworkDialog();
                showLoadingElements();//don't let the user use the interface, so we show the loading bar or the elements the interface has
                break;
            case NO_INTERNET:
                showNoInternetDialog();
                hideLoadingElements();//let the user use the interface but we tell him that he doesn't have internet
                break;
            case CONNECTED:
                System.out.println("Connected to internet. ");
                hideLoadingElements();
                break;
        }

    }

    /**
     * The Screen must have elements like progressBar that won't hide until the device has internet o is connectected
     * to a LAN
     */
    protected abstract void hideLoadingElements ();

    /**
     * The Screen must have elements like progressBar that show when the program start and hide when the status of the
     * network is CONNECTED or NO_INTERNET
     */
    protected abstract void showLoadingElements();

    /**
     * Shows a dialog that tells the user that he is not connected to a LAN so he is not connected to
     * internet neither
     */
    protected void showNoNetworkDialog () {
        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                DialogScreen dialogScreen = new DialogScreen( NetworkActivity.this ,
                                                              getString( R.string.no_network_title ),
                                                              getString( R.string.no_network ) );
                dialogScreen.setPossitiveButton( getString( R.string.ok ), new OnEventListener() {
                    @Override
                    public void onEvent () {
                        System.out.println("Checking if there is connection now...");
                        statusNetwork.checkNetworkStatus();
                    }
                } );

                dialogScreen.show();
            }
        } );
    }

    /**
     * Shows a dialog that tells the user that he is not connected to internet
     */
    protected void showNoInternetDialog () {
        runOnUiThread( new Runnable() {
            @Override
            public void run () {
                DialogScreen dialogScreen = new DialogScreen( NetworkActivity.this ,
                                                              getString( R.string.no_internet_title ),
                                                              getString( R.string.no_internet ) );
                dialogScreen.setPossitiveButton( getString( R.string.ok ), new OnEventListener() {
                    @Override
                    public void onEvent () {
                        System.out.println("This device is not connected to internet.");
                    }
                } );

                dialogScreen.show();
            }
        } );
    }



}
