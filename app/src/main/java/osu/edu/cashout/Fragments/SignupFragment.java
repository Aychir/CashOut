package osu.edu.cashout.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import osu.edu.cashout.Activities.ScanActivity;
import osu.edu.cashout.R;

@SuppressWarnings({"LogNotTimber"})
public class SignupFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SignupFragment";

    private Context mContext;
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;

    @Override
    public void onAttach(Context c){
        super.onAttach(getContext());

        Log.v(TAG, "Logging onAttach()");

        mContext = c;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.v(TAG, "Logging onCreateView()");

        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();

        //Finding the forms from the layout
        mEmailField = v.findViewById(R.id.email);
        mPasswordField = v.findViewById(R.id.password);
        mConfirmPasswordField = v.findViewById(R.id.password_confirmation);

        Button signupButton = v.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "Logging onStart()");
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
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Logging onResume()");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.signup_button){
            //create an account if the button was clicked
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }

    /*
     * Code from Firebase tutorial on creating an account with email and password
     * */
    private void createAccount(String email, String password){
        //Check if format of information was valid
        if(!validateForms()){
            return;
        }

        // [START create_user_with_email]
        if(getActivity() != null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI and launch Scan Activity
                                Toast.makeText(mContext, "Authentication success!",
                                        Toast.LENGTH_SHORT).show();

                                Intent cameraIntent = new Intent(mContext, ScanActivity.class);
                                startActivity(cameraIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                //Update the UI in any way?
                                Toast.makeText(mContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                        }
                    });
        }
        else{
            Toast.makeText(mContext, "Fragment not attached to activity.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /*
  This method checks the validity of the form fields of the sign up layout,
    if there are any errors, an account will not be created.
 */
    private boolean validateForms() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (email.isEmpty()) {
            mEmailField.setError("An email is required.");
            valid = false;
        }
        //Check the format of the email address
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailField.setError("Please enter a valid email.");
            valid = false;
        }

        String password = mPasswordField.getText().toString();
        if (password.isEmpty()) {
            mPasswordField.setError("A password is required.");
            valid = false;
        }
        else if(password.length() < 6){
            mEmailField.setError("Your password must be at least 6 characters long.");
            valid = false;
        }

        String passwordConfirmation = mConfirmPasswordField.getText().toString();
        if (passwordConfirmation.isEmpty()) {
            mConfirmPasswordField.setError("You must confirm your password.");
            valid = false;
        }
        else if(!passwordConfirmation.equals(password)){
            mConfirmPasswordField.setError("Your password entries did not match, please try again.");
            valid = false;
        }
        return valid;
    }
}
