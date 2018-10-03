package osu.edu.cashout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mUserAuth;
    private FirebaseUser mCurrentUser;

    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserAuth = FirebaseAuth.getInstance();

        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);

        findViewById(R.id.signup_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();

        mCurrentUser = mUserAuth.getCurrentUser();

        //If there is a current user, sign in and skip authentication
        if(mCurrentUser != null){
            Log.d("User signed in", "USer");
            startScanActivity();
        }
    }

    @Override
    public void onClick(View v){
        int selectedView = v.getId();

        if(selectedView == R.id.login_button){
            createUserSession(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
        else if(selectedView == R.id.signup_button){
            //Want to launch sign up activity
            Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(signupIntent);
        }
    }

    private void createUserSession(String email, String password){
        if(!validateForms()){
            return;
        }

        mUserAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the camera
                            Intent cameraIntent = new Intent(LoginActivity.this, ScanActivity.class);
                            startActivity(cameraIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Failed to sign you in.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private boolean validateForms(){
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

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("A password is required.");
            valid = false;
        }

        return valid;
    }

    private void startScanActivity(){
        Intent cameraIntent = new Intent(LoginActivity.this, ScanActivity.class);
        startActivity(cameraIntent);
    }
}
