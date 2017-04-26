package spuzi.atenea.Client.Interfaces;

import spuzi.atenea.Common.NetworkStatusEnum;

/**
 * Created by spuzi on 29/03/2017.
 */

public interface OnNetworkStatusChecked {
    public void onNetworkChecked( NetworkStatusEnum status);
}
