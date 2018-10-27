package osu.edu.cashout.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import osu.edu.cashout.R;
import osu.edu.cashout.activities.MakeReviewActivity;

public class InfoFragment extends Fragment implements View.OnClickListener {
    Button mCreateReviewButton;
    private FirebaseAuth mUserAuth;
    private DatabaseReference mDbReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_info, container, false);

        mUserAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mUserAuth.getCurrentUser();

        //Set onclick listeners to the buttons on the screen
        mCreateReviewButton = v.findViewById(R.id.create_review_button);
        mCreateReviewButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.create_review_button:
                Intent makeReviewActivity = new Intent(getContext(), MakeReviewActivity.class);
                startActivity(makeReviewActivity);
                break;
        }
    }
}
