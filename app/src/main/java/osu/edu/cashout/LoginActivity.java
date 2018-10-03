package osu.edu.cashout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //TODO: Check if user is signed in and skip directly to the scan activity

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
            Intent cameraIntent = new Intent(LoginActivity.this, ScanActivity.class);
            startActivity(cameraIntent);
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
        mUserAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mUserAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
