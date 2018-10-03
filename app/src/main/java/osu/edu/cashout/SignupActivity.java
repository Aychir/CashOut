package osu.edu.cashout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        //mEmailField = (EditText) findViewById()
        //mPasswordField = (EditText) findViewById()
        //mConfirmPasswordField = (EditText) findViewById()
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

//        if(id == R.id.signupButton){
//            create an account if the button was clicked
//            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
//        }
    }

    private void createAccount(String email, String password){
        //Check if format of information was valid
        if(!validateForm()){
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

    private boolean validateForm() {
        //TODO: Modify this to check for validity of length for ALL fields, format of ALL fields, and matching passwords
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        //Add passwordConfirmation checks

        return valid;
    }
}
