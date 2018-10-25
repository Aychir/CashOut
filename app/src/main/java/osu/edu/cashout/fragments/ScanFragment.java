package osu.edu.cashout.fragments;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.List;

import osu.edu.cashout.activities.AccountActivity;
import osu.edu.cashout.activities.LoginActivity;
import osu.edu.cashout.CameraPreview;
import osu.edu.cashout.R;

@SuppressWarnings({"LogNotTimber"})
public class ScanFragment extends Fragment implements View.OnClickListener{
    private static final int CAMERA_PERMISSION = 200;

    private static final String TAG = "ScanFragment";
    private Camera mCamera;
    private Context mContext;
    private View mView;

    @Override
    public void onAttach(Context c){
        super.onAttach(getContext());

        Log.v(TAG, "Logging onAttach()");

        mContext = c;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.v(TAG, "Logging onCreateView()");
        mView = inflater.inflate(R.layout.fragment_scan, container, false);

        mView.findViewById(R.id.signout_button).setOnClickListener(this);
        mView.findViewById(R.id.button_capture).setOnClickListener(this);
        mView.findViewById(R.id.account_button).setOnClickListener(this);

        return mView;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "Logging onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "Logging onResume()");
        if (getActivity() != null) {
            //In the case that the user hasn't permitted the camera at this point
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION);

            }
            //If permission was granted then start the preview of the camera
            else {
                //TODO: Think about moving this into a background process in the future
                startCameraPreview(mView);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Logging onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        //If we have an instance of the camera, release it when activity is no longer on screen
        if(mCamera != null){
            mCamera.release();
        }
        Log.d(TAG, "Logging onStop()");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
        else if(id == R.id.button_capture){
            Log.v(TAG, "Camera null?");
            if(mCamera != null){
                Log.v(TAG, "Camera not null");
                //Need to release camera in multiple places, look at onPictureTaken()
                mCamera.takePicture(null, null, mPicture);
            }
        }
        else if(id == R.id.account_button){
            Intent accountIntent = new Intent(mContext, AccountActivity.class);
            startActivity(accountIntent);
        }
    }

    //Interface that provides information on the image taken
    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.v(TAG, "Picture taken " + data.length);
            //TODO: Here release the camera and start a background process to contact the API
            camera.release();

            FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                    .setWidth(480)   // 480x360 is typically sufficient for
                    .setHeight(360)  // image recognition
                    .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                    .build();


            //Create firebase image object from the byte array
            FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(data, metadata);

            Log.v(TAG, "" + image);

            //Create an instance of the barcode detector
            FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                    .getVisionBarcodeDetector();

            //Attempt to retrieve the result from the detector
            Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                            // Task completed successfully
                            // ...
                            Log.v(TAG, barcodes.size() + "");
                            for(FirebaseVisionBarcode barcode: barcodes){
                                Log.v(TAG, "" + barcode);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                            Log.v(TAG, "FAILED");
                        }
                    });

            Log.v(TAG, "Results " + result);
            //TODO: Create some sort of progress box while we grab UPC code

            //TODO: Once the asynctask returns, we launch a new activity to view the product info or restart camera preview (invalid response)
        }
    };

    //Programmatically assign the camera preview to the FrameLayout in the layout
    private void startCameraPreview(View v){
        mCamera = getCameraInstance(mContext);

        CameraPreview mPreview = new CameraPreview(mContext, mCamera);
        FrameLayout preview = v.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    //Get an instance of the camera for the camera preview
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
