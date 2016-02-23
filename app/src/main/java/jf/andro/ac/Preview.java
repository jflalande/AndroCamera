package jf.andro.ac;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

class Preview extends ViewGroup implements SurfaceHolder.Callback {

  SurfaceView mSurfaceView;
  SurfaceHolder mHolder;
  Camera mCamera;
  List<Size> mSupportedPreviewSizes;
  Size mPreviewSize;

  Preview(Context context) {
    super(context);

    mSurfaceView = new SurfaceView(context);
    addView(mSurfaceView);

    // Install a SurfaceHolder.Callback so we get notified
    // when the
    // underlying surface is created and destroyed.
    mHolder = mSurfaceView.getHolder();
    mHolder.addCallback(this);
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  public void setCamera(Camera camera) {
    if (mCamera == camera) { return; }

    stopPreviewAndFreeCamera();

    mCamera = camera;

    if (mCamera != null) {
      List<Size> localSizes = 
          mCamera.getParameters().getSupportedPreviewSizes();
      mSupportedPreviewSizes = localSizes;
      requestLayout();

      try {
        mCamera.setPreviewDisplay(mHolder);
      } catch (IOException e) {
        e.printStackTrace();
      }

      /*
              Important: Call startPreview() to start
              updating the preview surface. Preview must 
              be started before you can take a picture.
       */
      mCamera.startPreview();
    }
  }

  /**
   * When this function returns, mCamera will be null.
   */
  private void stopPreviewAndFreeCamera() {

    if (mCamera != null) {
      /*
             Call stopPreview() to stop updating 
             the preview surface.
       */
      mCamera.stopPreview();

      /*
             Important: Call release() to release the 
             camera for use by other applications. 
             Applications should release the camera 
             immediately in onPause() (and re-open() it in
             onResume()).
       */
      mCamera.release();

      mCamera = null;
    }
  }

  @Override
  public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    // TODO Auto-generated method stub

  }

  @Override
  public void surfaceCreated(SurfaceHolder arg0) {
    // The Surface has been created, acquire the camera and tell it where
    // to draw.
    try {
      if (mCamera != null) {
        mCamera.setPreviewDisplay(mHolder);
      }
    } catch (IOException exception) {
      Log.e("W", "IOException caused by setPreviewDisplay()", exception);
    }
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    // Surface will be destroyed when we return, so stop the preview.
    if (mCamera != null) {
      /*
              Call stopPreview() to stop updating the preview surface.
       */
      mCamera.stopPreview();
    }
  }

  @Override
  protected void onLayout(boolean changed,  int l, int t, int r, int b) {
    if (changed && getChildCount() > 0) {
      final View child = getChildAt(0);

      final int width = r - l;
      final int height = b - t;

      int previewWidth = width;
      int previewHeight = height;
      if (mPreviewSize != null) {
        previewWidth = mPreviewSize.width;
        previewHeight = mPreviewSize.height;
      }

      // Center the child SurfaceView within the parent.
      if (width * previewHeight > height * previewWidth) {
        final int scaledChildWidth = previewWidth * height / previewHeight;
        child.layout((width - scaledChildWidth) / 2, 0,
            (width + scaledChildWidth) / 2, height);
      } else {
        final int scaledChildHeight = previewHeight * width / previewWidth;
        child.layout(0, (height - scaledChildHeight) / 2,
            width, (height + scaledChildHeight) / 2);
      }
    }

  }

}