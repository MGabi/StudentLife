package com.minimalart.studentlife.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
    private SharedPreferences preferences;
    private Toolbar toolbar;

    private static final String COLOR_KEY = "key_color_preference";
    private static final String ACCENT_KEY = "key_accent_preference";

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

    /**
     * initializing basic views
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme = preferences.getString(COLOR_KEY, "red");
        switch(theme){
            case "red":
                setTheme(R.style.AppTheme_Red);
                break;
            case "pink":
                setTheme(R.style.AppTheme_Pink);
                break;
            case "purple":
                setTheme(R.style.AppTheme_Purple);
                break;
            case "deep_blue":
                setTheme(R.style.AppTheme_DarkBlue);
                break;
            default:
                setTheme(R.style.AppTheme_Red);
                break;
        }

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

        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));


        Fragment fragment = fragmentManager.findFragmentById(R.id.activity_main_content);
        if(fragment == null){
            fragment = LoginFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.activity_main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }

    }

    /**
     * Opening signup fragment
     */
    public void loadSignUpFragment(){
        enableBackButton();
        SignUpFragment fragmentSignUp = SignUpFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.activity_main_content, fragmentSignUp).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
    }

    public void enableBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back, getTheme()));
    }

    public void disableBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
                    openMainActivity();
                }
                else
                    Toast.makeText(LoginActivity.this, task.getException()
                            .getLocalizedMessage(), Toast.LENGTH_LONG)
                            .show();
            }
        });
    }

    /**
     * opening MainActivty when needed
     */
    public void openMainActivity(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        int reqCODE = 1991;
        startActivityForResult(intent, reqCODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                disableBackButton();
                break;
        }
        return true;
    }
}

