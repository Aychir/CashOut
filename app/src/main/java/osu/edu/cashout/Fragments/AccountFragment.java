package osu.edu.cashout.Fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import osu.edu.cashout.R;

public class AccountFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth mUserAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Button mUpdateAccountButton;
    private Button mDeleteAccountButton;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mUsername;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        //Find views by ID
        mUpdateAccountButton = v.findViewById(R.id.update_button);
        mDeleteAccountButton = v.findViewById(R.id.delete_button);
        mFirstName = v.findViewById(R.id.first_name);
        mLastName = v.findViewById(R.id.last_name);
        mUsername = v.findViewById(R.id.username);
        mEmailField = v.findViewById(R.id.email);
        mPasswordField = v.findViewById(R.id.password);

        //Set on click listeners for the buttons
        mUpdateAccountButton.setOnClickListener(this);
        mDeleteAccountButton.setOnClickListener(this);

        //Set up necessary FireBase components
        mUserAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        FirebaseUser currentUser = mUserAuth.getCurrentUser();
        if(currentUser != null) {
            mEmailField.setText(currentUser.getEmail());
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
                String firstName = mFirstName.getText().toString();
                String lastName = mLastName.getText().toString();
                String username = mUsername.getText().toString();
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
        }

    }
}
