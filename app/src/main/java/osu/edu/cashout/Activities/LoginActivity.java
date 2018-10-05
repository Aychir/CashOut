package osu.edu.cashout.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import osu.edu.cashout.Fragments.LoginFragment;
import osu.edu.cashout.R;

@SuppressWarnings({"LogNotTimber"})
public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.v(TAG, "Logging onCreate() method");

        FragmentManager fm = getSupportFragmentManager();

        Fragment frag = fm.findFragmentById(R.id.fragment_container);

        if(frag == null) {
            LoginFragment loginFragment = new LoginFragment();

            fm.beginTransaction()
                    .add(R.id.fragment_container, loginFragment)
                    .commit();
        }

    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.v(TAG, "Logging onStart() method");
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

        if (isFinishing()) {
            // do stuff
            Log.v(TAG, "Logging finishing");
        }

    }

}
