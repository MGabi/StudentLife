package com.minimalart.studentlife.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.LoginActivity;


public class LoginFragment extends Fragment{

    //Global static variables
    private static final int REQUEST_PERMISSIONS = 111;
    private static boolean PERMISSION_MODE = true;

    // UI references
    private EditText emailView;
    private EditText passwordView;
    private TextView signUpTextView;
    private View progressView;
    private View loginFormView;
    private Button logInButton;
    private MaterialRippleLayout signUpRipple;

    public LoginFragment() {

    }

    /**
     * @return a reference to this fragment
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        emailView = (EditText) view.findViewById(R.id.email);
        passwordView = (EditText) view.findViewById(R.id.password);
        logInButton = (Button) view.findViewById(R.id.log_in_button);
        loginFormView = view.findViewById(R.id.login_form);
        progressView = view.findViewById(R.id.login_progress);
        signUpTextView = (TextView)view.findViewById(R.id.sign_up_text);
        signUpRipple = (MaterialRippleLayout)view.findViewById(R.id.ripple_signup_text);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkForPermissions();

        if(PERMISSION_MODE)
            setupViews();
        else{
            Snackbar.make(loginFormView, R.string.error_need_permissions, Snackbar.LENGTH_INDEFINITE).show();
        }

        return view;
    }

    /**
     * Setting up the views if permissions granted
     */
    public void setupViews(){

        /**
         * Trying to log in user to firebase database
         */
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkData()) {
                    /*closeKeyboard();*/
                    logInUser(emailView.getText().toString(),
                            passwordView.getText().toString());
                }
            }
        });

        /**
         * Opening new registration fragment
         */
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)getActivity()).loadSignUpFragment();
            }
        });
    }

    /**
     * Closing keyboard if open
     */
    public void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Checking username and password for integrity.
     * @return true if all fields are completed
     */
    private boolean checkData() {

        emailView.setError(null);
        passwordView.setError(null);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean allGood = true;
        View focusView = null;

        /**
         * Checking email
         */
        if (!isEmailValid(email)) {
            if (TextUtils.isEmpty(email))
                emailView.setError(getString(R.string.error_field_required));
            else
                emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            allGood = false;
        }

        /**
         * Checking password
         */
        if(!isPasswordValid(password)){
            if (TextUtils.isEmpty(password))
                passwordView.setError(getString(R.string.error_pass_not_found));
            else
                passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            allGood = false;
        }

        /**
         * Checking equality
         */
        if(email.equals(password)){
            passwordView.setError(getString(R.string.error_bad_data));
            focusView = passwordView;
            allGood = false;
        }

        /**
         * If allGood is false, request focus to specified fields
         * @return allGood value
         */
        if(!allGood)
            focusView.requestFocus();

        return allGood;
    }

    /**
     * Checking email string
     * @param email : email from emailView
     * @return true if email is good
     */
    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    /**
     * Checking password string
     * @param password : password from passwordView
     * @return true if password is good
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    /**
     * Trying to login user calling LoginActivity method
     * @param user : user name
     * @param password : user password
     */
    public void logInUser(String user, String password){
        ((LoginActivity)getActivity()).checkDatabaseLogIn(user, password);
    }

    /**
     * Checking for permissions
     * if permissions are not granted, ask for them
     */
    public void checkForPermissions(){
        if((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, REQUEST_PERMISSIONS);
            Toast.makeText(getContext(), "req perm", Toast.LENGTH_SHORT).show();
        }
        else{
            PERMISSION_MODE = true;
            Toast.makeText(getContext(), "au deja permisiune", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checking request results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PERMISSION_MODE = true;
                } else
                    PERMISSION_MODE = false;
        }
    }

}
