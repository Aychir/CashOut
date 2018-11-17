package osu.edu.cashout.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import osu.edu.cashout.fragments.SignupFragment;
import osu.edu.cashout.R;

@SuppressWarnings({"LogNotTimber"})
public class SignupActivity extends AppCompatActivity{

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setTitle(R.string.sign_up);

        Log.v(TAG, "Logging onCreate() method");

        //If there is an existing state, we do not need to create a new fragment
        FragmentManager fm = getSupportFragmentManager();
        //If there is an existing state, we do not need to create a new fragment
        Fragment frag = fm.findFragmentById(R.id.fragment_container);

        if(frag == null) {
            SignupFragment signupFragment = new SignupFragment();

            fm.beginTransaction()
                    .add(R.id.fragment_container, signupFragment)
                    .commit();
        }

    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.v(TAG, "Logging onStart() method");
    }

    @Override
    protected void onResume(){
        super.onResume();

        Log.v(TAG, "Logging onResume() method");
    }

    @Override
    protected void onPause(){
        super.onPause();

        Log.v(TAG, "Logging onPause() method");
    }

    @Override
    protected void onStop(){
        super.onStop();

        Log.v(TAG, "Logging onStop() method");
    }

    @Override
    protected void onRestart(){
        super.onRestart();

        Log.v(TAG, "Logging onRestart() method");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        Log.v(TAG, "Logging onDestroy() method");
    }
}
