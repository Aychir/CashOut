package osu.edu.cashout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Button loginButton = findViewById(R.id.loginButton);
        //Button signupButton = findViewById(R.id.goToSignup);
        findViewById(R.id.signup_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        int selectedView = v.getId();

        if(selectedView == R.id.login_button){
            return;
        }
        else if(selectedView == R.id.signup_button){
            //Want to launch sign up activity
            Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(signupIntent);
        }
    }
}
