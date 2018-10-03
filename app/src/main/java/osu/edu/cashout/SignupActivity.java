package osu.edu.cashout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;

    //private Button mSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        //Finding the forms from the layout
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mConfirmPasswordField = findViewById(R.id.password_confirmation);

        //Set onclick listener for the signup button
        findViewById(R.id.signup_button).setOnClickListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser curUser = mAuth.getCurrentUser();
        //Check if user is signed in, if not
        //updateUI(curUser);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();

        //if(id == R.id.backbutton){
            //go back to login activity
        //}

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
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI and launch Scan Activity
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            Toast.makeText(SignupActivity.this, "Authentication success!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Update the UI in any way?
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }

    /*
      This method checks the validity of the form fields of the sign up layout,
        if there are any errors, an account will not be created.
     */
    private boolean validateForms() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("An email is required.");
            valid = false;
        }
        //Check the format of the email address
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailField.setError("Please enter a valid email.");
            valid = false;
        }
//        else{
//            mEmailField.setError(null);
//        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("A password is required.");
            valid = false;
        }
        else if(password.length() < 6){
            mEmailField.setError("Your password must be at least 6 characters long.");
            valid = false;
        }
//        else {
//            mPasswordField.setError(null);
//        }

        String passwordConfirmation = mConfirmPasswordField.getText().toString();
        if (TextUtils.isEmpty(passwordConfirmation)) {
            mConfirmPasswordField.setError("You must confirm your password.");
            valid = false;
        }
        else if(!passwordConfirmation.equals(password)){
            mConfirmPasswordField.setError("Your password entries did not match, please try again.");
            valid = false;
        }
//        else {
//            mConfirmPasswordField.setError(null);
//        }
        return valid;
    }
}
