package spuzi.atenea.Common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by spuzi on 18/04/2017.
 */

public abstract class OrientationChecker extends AppCompatActivity {

    private int orientation ;
    public boolean hasRotated;


    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        orientation =  getResources().getConfiguration().orientation;
    }

    /*
    @Override
    public void onResume(){
        super.onResume();

    }
    */

    @Override
    protected void onPause () {
        super.onPause();

        if(isFinishing()){//exit the application
            System.out.println("The activity is being closed");
        }
        else{
            checkRotation();
            if (hasRotated){//It's an orientation change.
                System.out.println("se ha rotado " + this.getClass().getName());
            }
        }

    }

    private void checkRotation(){
        int currentOrientation = getResources().getConfiguration().orientation;
        if( orientation != currentOrientation){
            hasRotated = true;
            orientation = currentOrientation;
        }else{
            hasRotated = false;
        }

    }
}
