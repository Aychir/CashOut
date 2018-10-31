package osu.edu.cashout.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import osu.edu.cashout.AsyncFindProduct;
import osu.edu.cashout.R;

public class ManualSearchFragment extends Fragment implements View.OnClickListener{

    private EditText mSearchField;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manual_search, container, false);

        Button mSearchButton = v.findViewById(R.id.manual_search_button);
        mSearchButton.setOnClickListener(this);

        mSearchField = v.findViewById(R.id.input_upc);

        return v;
    }

    @Override
    public void onClick(View v){
        if(validateForm()){
            AsyncFindProduct findProduct = new AsyncFindProduct(getActivity());
            findProduct.execute(mSearchField.getText().toString());
        }
    }

    private boolean validateForm(){
        boolean valid = true;
        if(mSearchField.getText().toString().isEmpty()){
            valid = false;
        }
        return valid;
    }
}
