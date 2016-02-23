package jf.andro.ac;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;

import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
    Camera mCamera;
    Preview mPreview;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        mPreview = new Preview(this);
        setContentView(mPreview);
        
        // A faire dans un thread à part en principe
        boolean ok = safeCameraOpen(0);
        if (ok)
            mPreview.setCamera(mCamera);
    }   

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;
      
        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
                    qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;    
    }

    private void releaseCameraAndPreview() {
        mPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
