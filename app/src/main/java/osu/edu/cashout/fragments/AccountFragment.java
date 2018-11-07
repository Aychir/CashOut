package osu.edu.cashout.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashSet;
import java.util.Set;

import osu.edu.cashout.activities.LoginActivity;
//import osu.edu.cashout.activities.ScanActivity;
import osu.edu.cashout.R;
import osu.edu.cashout.dataModels.User;


public class AccountFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth mUserAuth;
    private DatabaseReference mDbReference;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mUsername;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private User curUser;
    private Context mContext;
    private Set<String> userEmails;
    private Set<String> mUsernames;
    private ProgressDialog dialog;

    //TODO: Rotating screen and being on first name field is wonky

    //TODO: Add signout button onto here

    @Override
    public void onAttach(Context c){
        super.onAttach(getContext());
        mContext = c;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        //Find views by ID
        Button mUpdateAccountButton = v.findViewById(R.id.update_button);
        Button mDeleteAccountButton = v.findViewById(R.id.delete_button);
        mFirstName = v.findViewById(R.id.first_name);
        mLastName = v.findViewById(R.id.last_name);
        mUsername = v.findViewById(R.id.username);
        mEmailField = v.findViewById(R.id.email);
        mPasswordField = v.findViewById(R.id.password);
        mConfirmPasswordField = v.findViewById(R.id.confirm_password);

        userEmails = new HashSet<>();
        mUsernames = new HashSet<>();

        //Set on click listeners for the buttons
        mUpdateAccountButton.setOnClickListener(this);
        mDeleteAccountButton.setOnClickListener(this);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Getting your account info...");
        dialog.show();
        //Set up necessary FireBase components
        mUserAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mUserAuth.getCurrentUser();
        if(currentUser != null) {
            //get the reference to the user that is signed-in
            mDbReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());



            //Read info from the database to display to the user
            mDbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Called initially, and then any time the information in fields change

                    //If there is a current user and all fields are empty (on fragment creation) will we want to pull info from the database
                    curUser = dataSnapshot.getValue(User.class);
                    if (curUser != null && mFirstName.getText().toString().isEmpty() && mLastName.getText().toString().isEmpty()
                            && mUsername.getText().toString().isEmpty() && mEmailField.getText().toString().isEmpty()){

                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }

                        mFirstName.setText(curUser.getFirstName());
                        mLastName.setText(curUser.getLastName());
                        mUsername.setText(curUser.getUsername());
                        mEmailField.setText(curUser.getEmail());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Failed to read value
                }
            });
        }

        //get the reference of the entire set of users
        DatabaseReference mDbReferenceUsers = FirebaseDatabase.getInstance().getReference("users");

        //Add all emails and usernames to a set to verify uniqueness of the attributes
        mDbReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user: dataSnapshot.getChildren()){
                    userEmails.add(user.child("email").getValue(String.class));
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
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.update_button:
                if(!validateForms()){
                    return;
                }
                else {
                    curUser.setEmail(mEmailField.getText().toString());
                    curUser.setFirstName(mFirstName.getText().toString());
                    curUser.setLastName(mLastName.getText().toString());
                    curUser.setUsername(mUsername.getText().toString());
                    if(mUserAuth.getCurrentUser() != null) {
                        //Change the user's email
                        mUserAuth.getCurrentUser().updateEmail(curUser.getEmail());

                        //Change the user's password
                        if (!mPasswordField.getText().toString().isEmpty()) {
                            mUserAuth.getCurrentUser().updatePassword(mPasswordField.getText().toString());
                        }

                        //Set the value of the referenced user to the updated user values
                        mDbReference.setValue(curUser);

                        Toast.makeText(getActivity(), "Account successfully updated.",
                                Toast.LENGTH_SHORT).show();

                        //Start the scan activity after a successful update
//                        Intent scanActivity = new Intent(getContext(), ScanActivity.class);
//                        startActivity(scanActivity);
                    }
                    else{
                        Toast.makeText(getActivity(), "Currently unable to update email and/or password, try again later.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.delete_button:
                //Delete the user in all tables as long as getting the current user is not null
                if(mUserAuth.getCurrentUser() != null) {
                    mUserAuth.getCurrentUser().delete();

                    //Delete the instance of the user in the realtime database
                    mDbReference.removeValue();

                    //Terminate the session associated to the now deleted user
                    FirebaseAuth.getInstance().signOut();

                    Intent loginIntent = new Intent(mContext, LoginActivity.class);
                    startActivity(loginIntent);
                }
                else{
                    Toast.makeText(getActivity(), "Currently unable to delete your account, try again later.",
                            Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

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
        else if(!email.equals(curUser.getEmail()) && userEmails.contains(email)){
            mEmailField.setError("An account with that email address already exists.");
            valid = false;
        }

        //Verify a long enough password and its confirmation
        String password = mPasswordField.getText().toString();
        if(!password.isEmpty() && password.length() < 6){
            mPasswordField.setError("Your password must be at least 6 characters long.");
            valid = false;
        }

        String passwordConfirmation = mConfirmPasswordField.getText().toString();
        if (!password.isEmpty() && passwordConfirmation.isEmpty()) {
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
        else if(!username.equals(curUser.getUsername()) && mUsernames.contains(username)){
            mUsername.setError("That username is already taken.");
            valid = false;
        }

        return valid;
    }
}
