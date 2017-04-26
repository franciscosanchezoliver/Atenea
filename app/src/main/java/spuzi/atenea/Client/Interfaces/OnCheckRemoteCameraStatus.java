package spuzi.atenea.Client.Interfaces;

import spuzi.atenea.Client.Classes.Camera;
import spuzi.atenea.Client.Classes.RemoteCameraStatus;

/**
 * Created by spuzi on 27/03/2017.
 */

public interface OnCheckRemoteCameraStatus {
    public void onRemoteCameraStatusRecieved ( RemoteCameraStatus cameraStatus, Camera camera );
}
