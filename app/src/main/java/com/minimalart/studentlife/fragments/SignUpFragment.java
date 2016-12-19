package com.minimalart.studentlife.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.LoginActivity;

public class SignUpFragment extends Fragment {

    private EditText emailTextField;
    private EditText passwordTextField;
    private EditText nameTextField;
    private EditText secNameTextField;
    private EditText ageTextField;
    private Button signUpButton;

    private String email;
    private String password;
    private String name;
    private String secName;
    private String age;

    public SignUpFragment() {

    }

    /**
     * @return a reference to this fragment
     */
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        emailTextField = (EditText)view.findViewById(R.id.email_signup);
        passwordTextField = (EditText)view.findViewById(R.id.password_signup);
        nameTextField = (EditText)view.findViewById(R.id.firstname_signup);
        secNameTextField = (EditText)view.findViewById(R.id.secondname_signup);
        ageTextField = (EditText)view.findViewById(R.id.age_signup);
        signUpButton = (Button) view.findViewById(R.id.sign_up_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkData())
                    signUp(emailTextField.getText().toString(),
                            passwordTextField.getText().toString(),
                            nameTextField.getText().toString(),
                            secNameTextField.getText().toString(),
                            ageTextField.getText().toString());
            }
        });

        return view;
    }

    /**
     * Trying to sign up user calling LoginActivity method
     */
    private void signUp(String nemail, String npassword, String nname, String nsecName, String nage){
        ((LoginActivity)getActivity()).signUpNewUser(nemail, npassword, nname, nsecName, nage);
    }

    /**
     * Checking data for signup
     * @return true if all fields are well completed
     */
    private boolean checkData() {

        emailTextField.setError(null);
        passwordTextField.setError(null);

        email = emailTextField.getText().toString();
        password = passwordTextField.getText().toString();
        name = nameTextField.getText().toString();
        secName = secNameTextField.getText().toString();
        age = ageTextField.getText().toString();

        boolean allGood = true;
        View focusView = null;

        /**
         * Checking email
         */
        if (!isEmailValid(email)) {
            if (TextUtils.isEmpty(email))
                emailTextField.setError(getString(R.string.error_field_required));
            else
                emailTextField.setError(getString(R.string.error_invalid_email));
            focusView = emailTextField;
            allGood = false;
        }

        /**
         * Checking PASSWORD
         */
        if(!isPasswordValid(password)){
            if (TextUtils.isEmpty(password))
                passwordTextField.setError(getString(R.string.error_pass_not_found));
            else
                passwordTextField.setError(getString(R.string.error_invalid_password));
            focusView = passwordTextField;
            allGood = false;
        }

        /**
         * Checking equality
         */
        if(email.equals(password)){
            passwordTextField.setError(getString(R.string.error_bad_data));
            focusView = passwordTextField;
            allGood = false;
        }

        /**
         * Checking names
         */
        if(!isNameValid(name)){
            if(TextUtils.isEmpty(name))
                nameTextField.setError(getString(R.string.error_bad_name_short));
            else
                nameTextField.setError(getString(R.string.error_bad_name));
            focusView = nameTextField;
            allGood = false;
        }

        if(!isNameValid(secName)){
            if(TextUtils.isEmpty(secName))
                secNameTextField.setError(getString(R.string.error_bad_secName_short));
            else
                secNameTextField.setError(getString(R.string.error_bad_secName));
            focusView = secNameTextField;
            allGood = false;
        }

        /**
         * Checking age
         */
        if(!isAgeValid(age)){
            if(TextUtils.isEmpty(age))
                ageTextField.setError(getString(R.string.error_bad_age_short));
            else
                ageTextField.setError(getString(R.string.error_bad_age));

            focusView = ageTextField;
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
     * Checking name string
     * @param name : name from nameView
     * @return
     */
    private boolean isNameValid(String name){
        return name.length() > 2;
    }

    /**
     * Checking age string
     * @param age : age from ageView
     * @return
     */
    private boolean isAgeValid(String age){
        return Integer.parseInt(age) > 12;
    }

}
