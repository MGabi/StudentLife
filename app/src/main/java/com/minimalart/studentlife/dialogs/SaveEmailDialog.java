package com.minimalart.studentlife.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.others.Utils;

/**
 * Created by ytgab on 02.02.2017.
 */

public class SaveEmailDialog extends DialogFragment {

    private Button saveBtn;
    private Button closeBtn;
    private String s;
    private View snackView;

    public SaveEmailDialog(String s, View v){
        this.s = s;
        this.snackView = v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.profile_change_email, null);

        closeBtn = (Button) dialogView.findViewById(R.id.close_btn);
        saveBtn = (Button) dialogView.findViewById(R.id.save_btn);
        final EditText email = (EditText) dialogView.findViewById(R.id.dialog_email);

        email.setText(s);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(email.getText().toString());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        return builder.create();
    }

    public void saveData(String s1){
        if(Utils.getInstance().isConnectedToNetwork(getContext())){
            DatabaseReference dbRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            if(!TextUtils.isEmpty(s1))
                dbRef.child("email").setValue(s1);
            dismiss();
        }else {
            Snackbar.make(snackView, getResources().getString(R.string.error_no_network_connection), Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(android.R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {}})
                    .show();
            dismiss();
        }
    }

}
