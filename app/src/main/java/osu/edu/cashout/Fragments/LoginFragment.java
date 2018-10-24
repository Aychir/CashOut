package osu.edu.cashout.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import osu.edu.cashout.Activities.ScanActivity;
import osu.edu.cashout.Activities.SignupActivity;
import osu.edu.cashout.R;

@SuppressWarnings({"LogNotTimber"})
public class LoginFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "LoginFragment";

    //Member variables
    private FirebaseAuth mUserAuth;
    private FirebaseUser currentUser;

    private EditText mEmailField;
    private EditText mPasswordField;

    private Context mContext;


    //Need to ensure fragment is attached to activity before anything so other activities can
    //      start by using the context from loginActivity
    @Override
    public void onAttach(Context c){
        super.onAttach(getContext());

        Log.v(TAG, "Logging onAttach()");

        mContext = c;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        Log.v(TAG, "Logging onCreateView()");

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mUserAuth = FirebaseAuth.getInstance();

        mEmailField = v.findViewById(R.id.email);
        mPasswordField = v.findViewById(R.id.password);

        //Set onclick listeners to the buttons on the screen
        Button signup = v.findViewById(R.id.signup_button);
        signup.setOnClickListener(this);

        Button login = v.findViewById(R.id.login_button);
        login.setOnClickListener(this);

        currentUser = mUserAuth.getCurrentUser();

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        //If there is a current user, sign in and skip authentication

        //Check for a current user and we need to check if the user is in the database
        if(currentUser != null){
            Log.v("User signed in", "User not null ");
            startScanActivity();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Logging onResume()");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Logging onPause()");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Logging onStop()");
    }



    @Override
    public void onClick(View v){
        int selectedView = v.getId();

        if(selectedView == R.id.login_button){
            createUserSession(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
        else if(selectedView == R.id.signup_button){
            //Want to launch sign up activity
            Intent signupIntent = new Intent(mContext, SignupActivity.class);
            startActivity(signupIntent);
        }
    }

    private void createUserSession(String email, String password){
        if(!validateForms(email, password)){
            return;
        }

        if(getActivity() != null) {
            mUserAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the camera
                                Intent cameraIntent = new Intent(mContext, ScanActivity.class);
                                startActivity(cameraIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getContext(), "Failed to sign in, please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        else{
            Toast.makeText(getContext(), "Failed to attach activity.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Validate the validity of the information submitted
    private boolean validateForms(String email, String password){
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("An email is required.");
            valid = false;
        }
        //Check the format of the email address
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailField.setError("Please enter a valid email.");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("A password is required.");
            valid = false;
        }

        return valid;
    }

    private void startScanActivity(){
        Intent cameraIntent = new Intent(getActivity(), ScanActivity.class);
        startActivity(cameraIntent);
        if(getActivity() != null) {
            getActivity().finish();
        }
    }
}
