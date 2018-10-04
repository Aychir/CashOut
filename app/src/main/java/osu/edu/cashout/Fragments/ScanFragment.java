package osu.edu.cashout.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import osu.edu.cashout.Activities.LoginActivity;
import osu.edu.cashout.Activities.ScanActivity;
import osu.edu.cashout.CameraPreview;
import osu.edu.cashout.R;

public class ScanFragment extends Fragment implements View.OnClickListener{
    private static final int CAMERA_PERMISSION = 200;

    private Camera mCamera;
    private CameraPreview mPreview;

    private Context mContext;
    private View mView;

    @Override
    public void onAttach(Context c){
        super.onAttach(getContext());

        mContext = c;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.fragment_scan, container, false);

        //In the case that the user hasn't permitted the camera at this point
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION);
        }
        //If permission was granted then start the preview of the camera
        else{
            startCameraPreview(mView);
        }

        mView.findViewById(R.id.signout_button).setOnClickListener(this);

        return mView;
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
                    startCameraPreview(mView);
                } else {
                    // permission denied, tell the user we failed to access camera
                    Toast.makeText( mContext,"Failed to access camera",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v){
        int id = v.getId();

        if(id == R.id.signout_button){
            FirebaseAuth.getInstance().signOut();

            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    private void startCameraPreview(View v){
        mCamera = getCameraInstance(mContext);

        mPreview = new CameraPreview(mContext, mCamera);
        FrameLayout preview = v.findViewById(R.id.camera_preview);
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
