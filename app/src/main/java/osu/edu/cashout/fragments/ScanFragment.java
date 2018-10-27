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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;


import osu.edu.cashout.BarcodeGraphic;
import osu.edu.cashout.BarcodeGraphicTracker;
import osu.edu.cashout.CameraSourcePreview;
import osu.edu.cashout.GraphicOverlay;
import osu.edu.cashout.activities.AccountActivity;
import osu.edu.cashout.activities.LoginActivity;
import osu.edu.cashout.CameraPreview;
import osu.edu.cashout.R;
import osu.edu.cashout.BarcodeTrackerFactory;

//TODO: Back button should re-open the camera, not close the app


@SuppressWarnings({"LogNotTimber"})
public class ScanFragment extends Fragment implements View.OnClickListener, BarcodeGraphicTracker.BarcodeUpdateListener{
    private static final int CAMERA_PERMISSION = 200;

    private static final String TAG = "ScanFragment";
    private Camera mCamera;
    private Context mContext;
    private View mView;

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        //do something with barcode data returned
        Log.v(TAG, barcode + " barcode");
    }

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

        mPreview = mView.findViewById(R.id.camera_preview);

        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) mView.findViewById(R.id.graphicOverlay);

//        BarcodeDetector detector = new BarcodeDetector.Builder(getContext()).build();
//
//        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, getContext());
//        detector.setProcessor(
//                new MultiProcessor.Builder<>(barcodeFactory).build());
//
//        if(!detector.isOperational()) {
//            Log.v(TAG, "Could not set up the detector!");
//        }

        //Barcode b = barcodeFactory.returnBarcode();

        //Log.v(TAG, "" + b);

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
//            FirebaseVisionBarcodeDetectorOptions options =
//                    new FirebaseVisionBarcodeDetectorOptions.Builder()
//                            .setBarcodeFormats(
//                                    FirebaseVisionBarcode.FORMAT_UPC_A,
//                                    FirebaseVisionBarcode.FORMAT_UPC_E,
//                                    FirebaseVisionBarcode.FORMAT_QR_CODE)
//                            .build();

//            Log.v(TAG, "Picture taken " + new String(Base64.getEncoder().encode(data)));

            //Initialize the barcode detector
//            BarcodeDetector detector = new BarcodeDetector.Builder(getContext()).build();
//
//            BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, getContext());
//            detector.setProcessor(
//                    new MultiProcessor.Builder<>(barcodeFactory).build());


            //TODO: Here release the camera and start a background process to contact the API
            camera.release();

//            FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()// image recognition
//                    .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
//                    .setRotation(FirebaseVisionImageMetadata.ROTATION_0)
//                    .build();
//
//
//            //Create firebase image object from the byte array
//            FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(data, metadata);
//
//            Log.v(TAG, "" + image);
//
//            //Create an instance of the barcode detector
//            FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
//                    .getVisionBarcodeDetector(options);
//
//            //Attempt to retrieve the result from the detector
//            Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
//                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
//                        @Override
//                        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
//                            // Task completed successfully
//                            // ...
//                            Log.v(TAG, barcodes.size() + "");
//                            for(FirebaseVisionBarcode barcode: barcodes){
//                                Log.v(TAG, "" + barcode);
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Task failed with an exception
//                            // ...
//                            Log.v(TAG, "FAILED");
//                        }
//                    });
//
//            Log.v(TAG, "Results " + result);
            //TODO: Create some sort of progress box while we grab UPC code

            //TODO: Once the asynctask returns, we launch a new activity to view the product info or restart camera preview (invalid response)
        }
    };

    //Programmatically assign the camera preview to the FrameLayout in the layout
    private void startCameraPreview(View v){
        mCamera = getCameraInstance(mContext);

        BarcodeDetector detector = new BarcodeDetector.Builder(getContext()).build();

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, getContext());
        detector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());


        CameraPreview mPreview = new CameraPreview(mContext, mCamera);
        CameraSourcePreview preview = v.findViewById(R.id.camera_preview);
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

    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);  // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestDistance = distance;
            }
        }

        if (best != null) {
            Intent data = new Intent();
            //data.putExtra(BarcodeObject, best);
            //setResult(CommonStatusCodes.SUCCESS, data);
            //finish();
            return true;
        }
        return false;
    }

}
