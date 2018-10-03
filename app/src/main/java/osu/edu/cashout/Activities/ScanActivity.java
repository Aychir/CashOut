package osu.edu.cashout.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import osu.edu.cashout.CameraPreview;
import osu.edu.cashout.R;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int CAMERA_PERMISSION = 200;

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //In the case that the user hasn't permitted the camera at this point
        if(ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ScanActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION);
        }
        //If permission was granted then start the preview of the camera
        else{
            startCameraPreview();
        }

        findViewById(R.id.signout_button).setOnClickListener(this);
    }


    @Override
    public void onClick(View v){
        int id = v.getId();

        //If the user signs out, redirect to the login screen
        if(id == R.id.signout_button){
            FirebaseAuth.getInstance().signOut();

            Intent loginIntent = new Intent(ScanActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, open camera preview
                    startCameraPreview();
                } else {
                    // permission denied, tell the user we failed to access camera
                    Toast.makeText( ScanActivity.this,"Failed to access camera",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startCameraPreview(){
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
