package osu.edu.cashout.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import osu.edu.cashout.Activities.LoginActivity;
import osu.edu.cashout.Activities.SignupActivity;
import osu.edu.cashout.R;
import osu.edu.cashout.User;

import static android.content.ContentValues.TAG;

public class AccountFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth mUserAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseFirestore mFirestore;
    private Button mUpdateAccountButton;
    private Button mDeleteAccountButton;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mUsername;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private User curUser;
    private Context mContext;

    @Override
    public void onAttach(Context c){
        super.onAttach(getContext());
        mContext = c;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        //Find views by ID
        mUpdateAccountButton = v.findViewById(R.id.update_button);
        mDeleteAccountButton = v.findViewById(R.id.delete_button);
        mFirstName = v.findViewById(R.id.first_name);
        mLastName = v.findViewById(R.id.last_name);
        mUsername = v.findViewById(R.id.username);
        mEmailField = v.findViewById(R.id.email);
        mPasswordField = v.findViewById(R.id.password);
        mConfirmPasswordField = v.findViewById(R.id.confirm_password);

        //Set on click listeners for the buttons
        mUpdateAccountButton.setOnClickListener(this);
        mDeleteAccountButton.setOnClickListener(this);

        //Set up necessary FireBase components
        mUserAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mUserAuth.getCurrentUser();
        if(currentUser != null) {
            DocumentReference userRef = mFirestore.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    curUser = documentSnapshot.toObject(User.class);
                    mFirstName.setText(curUser.getFirstName());
                    mLastName.setText(curUser.getLastName());
                    mUsername.setText(curUser.getUsername());
                    mEmailField.setText(curUser.getEmail());
                }
            });
        }

        //Read info from the database to display to the user
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Called initially, and then any time the information in fields change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Failed to read value
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
                    //May want to add some error handling/listeners
                    mUserAuth.getCurrentUser().updateEmail(curUser.getEmail())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "User email address updated.");
                                    }
                                }
                            });
                    if(!mPasswordField.getText().toString().isEmpty()) {
                        mUserAuth.getCurrentUser().updatePassword(mPasswordField.getText().toString());
                    }
                    mFirestore.collection("users").document(mUserAuth.getCurrentUser().getUid()).set(curUser);
                    mUserAuth.getCurrentUser().reauthenticate(EmailAuthProvider
                            .getCredential(curUser.getEmail(), "password1234"));
                }
                break;
            case R.id.delete_button:
                //This line is null after changing sign-in information
                String uid = mUserAuth.getCurrentUser().getUid();
                mUserAuth.getCurrentUser().delete();
                mFirestore.collection("users").document(uid).delete();
                //BUG: Updating the email will not delete the user from the firestore, need to see
                //  how updating email is done or if mUserAuth needs to be updated

                //Issue is because changing the login credentials requires that we re-sign in the user,
                //  otherwise currentUser returns null
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(mContext, LoginActivity.class);
                startActivity(loginIntent);
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
        return valid;
    }
}
