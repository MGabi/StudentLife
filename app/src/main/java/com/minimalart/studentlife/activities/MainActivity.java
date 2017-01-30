package com.minimalart.studentlife.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.fragments.OpenFoodAnnounceFragment;
import com.minimalart.studentlife.fragments.OpenRentAnnounceFragment;
import com.minimalart.studentlife.fragments.navdrawer.AboutFragment;
import com.minimalart.studentlife.fragments.navdrawer.AddRentFragment;
import com.minimalart.studentlife.fragments.navdrawer.ContactFragment;
import com.minimalart.studentlife.fragments.navdrawer.FoodAlertFragment;
import com.minimalart.studentlife.fragments.navdrawer.FoodZoneFragment;
import com.minimalart.studentlife.fragments.navdrawer.HomeFragment;
import com.minimalart.studentlife.fragments.navdrawer.SearchRentFragment;
import com.minimalart.studentlife.interfaces.HelperInterface;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HelperInterface {

    private static final String REF_RENT = "rent-announces";
    private static final String REF_FOOD = "food-announces";
    private static final String REF_RENT_IMAGES = "rent-images";
    private static final String REF_FOOD_IMAGES = "food-images";
    private static final String GPLUS_URL = "https://plus.google.com/+GabiMatkoRO";
    private static final String DEV_TITLE = "Contact STUDENT LIFE";
    private static final String GPLAY_URL = "https://play.google.com/store/apps/dev?id=6865542268483195156";
    private static final String COLOR_KEY = "key_color_preference";
    private static final String ACCENT_KEY = "key_accent_preference";

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Fragment fragment = null;
    private Fragment viewRentDetailedFragment;
    private Fragment viewFoodDetailedFragment;
    private AppBarLayout appBarLayout;
    private TextView headerName;
    private TextView headerEmail;
    private SharedPreferences preferences;

    /**
     * Titles for navdrawer items
     */
    private static String[] TITLES;

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
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(getBaseContext(), R.xml.settings, false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TITLES = getResources().getStringArray(R.array.toolbar_titles);

        appBarLayout = (AppBarLayout)findViewById(R.id.appbar_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.current_user_email_header);
        headerName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.current_user_name_header);


        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_main);

        if (fragment == null) {
            fragment = HomeFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.content_main, fragment).commit();
            toolbar.setTitle(TITLES[0]);
        }
    }

    /**
     * Settings fields in navdrawer header
     * @param name
     * @param email
     */
    public void setAboutUserData(String name, String email){
        headerEmail.setText(email);
        headerName.setText(name);
    }

    /**
     * Handling the action on pressing the back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            //fragmentManager.popBackStack();
            super.onBackPressed();
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

    /**
     * Loading the navmenu fragment by clicking drawer-items
     *
     * @param ID : ID of the clicked button
     */
    public void loadNavMenuFragments(int ID) {
        boolean needToolbar = true;
        fragment = null;
        int t = 0;
        switch (ID) {
            case R.id.nav_home:
                fragment = HomeFragment.newInstance();
                t = 0;
                break;
            case R.id.nav_search_rent:
                fragment = SearchRentFragment.newInstance();
                t = 1;
                break;
            case R.id.nav_add_rent:
                fragment = AddRentFragment.newInstance();
                needToolbar = false;
                t = 2;
                break;
            case R.id.nav_search_food:
                fragment = FoodZoneFragment.newInstance();
                t = 3;
                break;
            case R.id.nav_add_food:
                fragment = FoodAlertFragment.newInstance();
                needToolbar = false;
                t = 4;
                break;
            case R.id.nav_about:
                fragment = AboutFragment.newInstance();
                t = 5;
                break;
            case R.id.nav_contact:
                fragment = ContactFragment.newInstance();
                t = 6;
                break;
            case R.id.nav_logout:
                Toast.makeText(getBaseContext(), "Should be logged out", Toast.LENGTH_LONG).show();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case R.id.nav_my_profile:
                break;
            case R.id.nav_settings:
                t = 7;
                fragment = null;
                needToolbar = true;
                break;
            default:
                fragment = HomeFragment.newInstance();
                t = 0;
                break;
        }
        toolbar.setTitle(TITLES[t]);

        if(t == 0)
            appBarLayout.setElevation(4);
        else
            appBarLayout.setElevation(0);

        if (needToolbar && fragment != null)
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.content_main, fragment)
                    .addToBackStack(null)
                    .commit();
        else if (fragment != null)
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .add(R.id.content_main_without_toolbar, fragment)
                    .addToBackStack(null)
                    .commit();
        else if(fragment == null && t == 7){
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
        }

    }

    /**
     * Listener for navigation items
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        loadNavMenuFragments(item.getItemId());


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * Adding a new rent announcement to the database
     *
     * @param cardRentAnnounce : current announce to be added
     * @param image : current image in byte-format, ready to be uploaded to cloud storage
     */
    public void addNewRentAnnounce(CardRentAnnounce cardRentAnnounce, byte[] image) {
        DatabaseReference newRef = databaseReference.child(REF_RENT).push();
        newRef.setValue(cardRentAnnounce);

        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageReference = firebaseStorage.getReference().child(REF_RENT_IMAGES).child(newRef.getKey());

        UploadTask uploadTask = storageReference.putBytes(image);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("FBSTORAGE", "RENT Failure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.v("FBSTORAGE", "RENT Success");
            }
        });
    }

    /**
     * Adding a new food announce to the database
     * @param cardFoodZone : current announce to be added
     * @param image : current image in byte-format, ready to be uploaded to cloud storage
     */
    public void addNewFood(CardFoodZone cardFoodZone, byte[] image) {
        DatabaseReference newRef = databaseReference.child(REF_FOOD).push();
        newRef.setValue(cardFoodZone);

        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageReference = firebaseStorage.getReference().child(REF_FOOD_IMAGES).child(newRef.getKey());
        UploadTask uploadTask = storageReference.putBytes(image);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("FBSTORAGE", "FOOD Failure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.v("FBSTORAGE", "FOOD Success");
            }
        });
    }

    /**
     * interface callback for opening GPLUS developer page
     */
    @Override
    public void openGPLUS() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GPLUS_URL));
        Intent chooser = Intent.createChooser(browserIntent, "Alege browser");
        startActivity(chooser);
    }

    /**
     * interface callback for opening an email intent to developer
     */
    @Override
    public void openEMAILSender() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ytgabi98@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, DEV_TITLE);
        Intent chooser = Intent.createChooser(emailIntent, getResources().getString(R.string.email_intent));
        startActivity(chooser);
    }

    /**
     * interface callback for opening GPLAY developer page
     */
    @Override
    public void openGPLAY() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GPLAY_URL));
        Intent chooser = Intent.createChooser(browserIntent, null);
        startActivity(chooser);
    }

    /**
     * interface callback for opening the detailedViewFragment for rent announces
     * @param cardRentAnnounce : announce that will be displayed in fragment
     */
    @Override
    public void openRentAnnounceFragment(CardRentAnnounce cardRentAnnounce) {
        viewRentDetailedFragment = OpenRentAnnounceFragment.newInstance(cardRentAnnounce);
        fragmentManager.beginTransaction()
                .replace(R.id.content_main_without_toolbar, viewRentDetailedFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * interface callback for opening the detailedViewFragment for food alerts
     * @param cardFoodZone : alert that will be displayed in fragment
     */
    @Override
    public void openFoodAnnounce(CardFoodZone cardFoodZone) {
        viewFoodDetailedFragment = OpenFoodAnnounceFragment.newInstance(cardFoodZone);
        fragmentManager.beginTransaction()
                .replace(R.id.content_main_without_toolbar, viewFoodDetailedFragment)
                .addToBackStack(null)
                .commit();
    }
}
