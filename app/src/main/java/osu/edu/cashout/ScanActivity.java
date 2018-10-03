package osu.edu.cashout;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener{

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

        findViewById(R.id.signout_button).setOnClickListener(this);
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
}
