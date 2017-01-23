package com.minimalart.studentlife.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.fragments.LoginFragment;
import com.minimalart.studentlife.fragments.SignUpFragment;
import com.minimalart.studentlife.models.User;


public class LoginActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private static String USER_UID = "";

    public static LoginActivity newInstance() {
        LoginActivity activity = new LoginActivity();
        return activity;
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    openMainActivity();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        /**
         * Declaring firebase things
         */
        firebaseAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        Fragment fragment = fragmentManager.findFragmentById(R.id.activity_main_content);
        if(fragment == null){
            fragment = LoginFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.activity_main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("RESUME", "Called from starting first activity");
    }

    /**
     * Opening signup fragment
     */
    public void loadSignUpFragment(){
        SignUpFragment fragmentSignUp = SignUpFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.activity_main_content, fragmentSignUp).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
    }

    /**
     * Sign up new users and push data to Firebase database
     * If it fails, shows a toast with error message
     **/
    public void signUpNewUser(final String email, final String pass, final String firstName, final String secName, final String age){
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    registerNewUserToDatabase(firebaseAuth.getCurrentUser().getUid().toString(), email, firstName, secName, age);
                    Snackbar.make(findViewById(R.id.signUpLinearLayout), R.string.auth_successful, Snackbar.LENGTH_LONG)
                            .show();
                    fragmentManager.popBackStack();
                }
                else
                    Toast.makeText(LoginActivity.this, task
                            .getException()
                            .getLocalizedMessage(), Toast.LENGTH_LONG)
                            .show();
            }
        });
    }

    /**
     * Registering new user into database
     */
    private void registerNewUserToDatabase(String userID, String email, String name, String secName, String age){
        User user = new User(email, name, secName, age);
        databaseReference.child("users").child(userID).setValue(user);
    }

    /**
     * Log in user to database
     * If it fails, shows a toast with error message
     **/
    public void checkDatabaseLogIn(String email, String pass){
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    USER_UID = firebaseAuth.getCurrentUser().getUid().toString();
                    openMainActivity();
                }
                else
                    Toast.makeText(LoginActivity.this, task.getException()
                            .getLocalizedMessage(), Toast.LENGTH_LONG)
                            .show();
            }
        });
    }

    public void openMainActivity(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        int reqCODE = 1991;
        startActivityForResult(intent, reqCODE);
    }
}

