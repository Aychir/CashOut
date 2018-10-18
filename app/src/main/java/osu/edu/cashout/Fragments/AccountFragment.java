package osu.edu.cashout.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import osu.edu.cashout.R;

public class AccountFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth mUserAuth;
    private Button mUpdateAccountButton;
    private Button mDeleteAccountButton;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        mUserAuth = FirebaseAuth.getInstance();
        mUpdateAccountButton = v.findViewById(R.id.update_button);
        mDeleteAccountButton = v.findViewById(R.id.delete_button);
        mEmailField = v.findViewById(R.id.email);
        mPasswordField = v.findViewById(R.id.password);

        mUpdateAccountButton.setOnClickListener(this);
        mDeleteAccountButton.setOnClickListener(this);

        FirebaseUser currentUser = mUserAuth.getCurrentUser();
        mEmailField.setText(currentUser.getEmail());

        return v;
    }

    @Override
    public void onClick(View v){

    }
}
