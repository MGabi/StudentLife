package com.minimalart.studentlife.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
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
@SuppressLint("ValidFragment")
public class SaveNameDialog extends DialogFragment {

    private Button closeBtn;
    private Button saveBtn;
    private String n1;
    private String n2;
    private View snackView;

    public SaveNameDialog(){

    }

    public SaveNameDialog(String name1, String name2, View v) {
        this.n1 = name1;
        this.n2 = name2;
        this.snackView = v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.profile_change_name, null);

        closeBtn = (Button) dialogView.findViewById(R.id.close_btn);
        saveBtn = (Button) dialogView.findViewById(R.id.save_btn);
        final EditText name = (EditText) dialogView.findViewById(R.id.dialog_name);
        final EditText secName = (EditText) dialogView.findViewById(R.id.dialog_secname);

        name.setText(n1);
        secName.setText(n2);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(name.getText().toString(), secName.getText().toString());
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

    public void saveData(String s1, String s2){
        if(Utils.getInstance().isConnectedToNetwork(getContext())){
            DatabaseReference dbRef = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            if(!TextUtils.isEmpty(s1))
                dbRef.child("name").setValue(s1);
            if(!TextUtils.isEmpty(s2))
                dbRef.child("secName").setValue(s2);
            dismiss();
        }else {
            Snackbar.make(snackView, getResources().getString(R.string.error_no_network_connection), Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(android.R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
            dismiss();
        }
    }
}
