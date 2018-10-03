package osu.edu.cashout;

import android.content.Context;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

public class ScanActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mCamera = getCameraInstance(this);

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    public static Camera getCameraInstance(Context context){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Toast.makeText( context,"Failed to access camera",
                    Toast.LENGTH_SHORT).show();
        }
        return c; // returns null if camera is unavailable
    }
}
