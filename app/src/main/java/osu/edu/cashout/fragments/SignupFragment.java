package osu.edu.cashout.fragments;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

import osu.edu.cashout.activities.ScanActivity;
import osu.edu.cashout.R;
import osu.edu.cashout.dataModels.User;

@SuppressWarnings({"LogNotTimber"})
public class SignupFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignupFragment";

    private Context mContext;
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mUsername;
    private Set<String> mEmails;
    private Set<String> mUsernames;

    private DatabaseReference mDatabase;

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
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        //Finding the forms from the layout
        mEmailField = v.findViewById(R.id.email);
        mPasswordField = v.findViewById(R.id.password);
        mConfirmPasswordField = v.findViewById(R.id.password_confirmation);
        mFirstName = v.findViewById(R.id.first_name);
        mLastName = v.findViewById(R.id.last_name);
        mUsername = v.findViewById(R.id.username);
        mEmails = new HashSet<>();
        mUsernames = new HashSet<>();

        Button signupButton = v.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(this);

        //Add all emails and usernames to a set to verify uniqueness of the attributes
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user: dataSnapshot.getChildren()){
                    mEmails.add(user.child("email").getValue(String.class));
                    mUsernames.add(user.child("username").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                                //Here we want to create a user object for our database
                                User user = createUser();

                                // Get reference to the collection and set a user instance into the database
                                if(mAuth.getUid() != null) {
                                    Log.v(TAG, "Valid User ID " + mAuth.getUid());
                                    mDatabase.child(mAuth.getUid()).setValue(user);
                                }

                                //Launch the camera after user is authenticated and added to the database
                                Intent scanIntent = new Intent(mContext, ScanActivity.class);
                                /*
                                * We do this because otherwise the login activity will remain in the stack
                                * (unfinished) and when pressing the back button on the scan activity, it will go
                                * back to the login activity that called the sign up activity. This arises because
                                * we deliberately do not finish login activity upon starting sign up activity so
                                * the user may go back to it using the back button. So we make the new activity
                                * the root of a cleared activity stack.
                                * */
                                scanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                scanIntent.putExtra("auth", mAuth.getUid());
                                startActivity(scanIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                //Update the UI in any way?
                                Toast.makeText(mContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
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
        //Check if email address is already taken
        else if(mEmails.contains(email)){
            mEmailField.setError("An account with that email address already exists.");
            valid = false;
        }

        //Verify a long enough password and its confirmation
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

        //Validate existence and uniqueness of the username attribute
        String username = mUsername.getText().toString();
        if(username.isEmpty()){
            mUsername.setError("A username is required.");
            valid = false;
        }
        else if(mUsernames.contains(username)){
            mUsername.setError("That username is already taken.");
            valid = false;
        }
        return valid;
    }

    //Class to create a user instance in our db with the filled in fields
    private User createUser(){
        User newUser = new User();

        String email = mEmailField.getText().toString();
        if (!email.isEmpty()) {
            newUser.setEmail(email);
        }

        String firstName = mFirstName.getText().toString();
        if (!firstName.isEmpty()) {
            newUser.setFirstName(firstName);
        }

        String lastName = mLastName.getText().toString();
        if (!lastName.isEmpty()) {
            newUser.setLastName(lastName);
        }

        String username = mUsername.getText().toString();
        if (!username.isEmpty()) {
            newUser.setUsername(username);
        }

        return newUser;
    }
}
