package com.minimalart.studentlife.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.HomeUserAnnouncesAdapter;
import com.minimalart.studentlife.adapters.HomeUserFoodAdapter;
import com.minimalart.studentlife.fragments.OpenFoodAnnounceFragment;
import com.minimalart.studentlife.fragments.OpenRentAnnounceFragment;
import com.minimalart.studentlife.fragments.TestFragment;
import com.minimalart.studentlife.fragments.navdrawer.AboutFragment;
import com.minimalart.studentlife.fragments.navdrawer.AddRentFragment;
import com.minimalart.studentlife.fragments.navdrawer.ContactFragment;
import com.minimalart.studentlife.fragments.navdrawer.FoodAlertFragment;
import com.minimalart.studentlife.fragments.navdrawer.SearchFoodFragment;
import com.minimalart.studentlife.fragments.navdrawer.HomeFragment;
import com.minimalart.studentlife.fragments.navdrawer.MyProfileFragment;
import com.minimalart.studentlife.fragments.navdrawer.SearchRentFragment;
import com.minimalart.studentlife.interfaces.HelperInterface;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.models.User;
import com.minimalart.studentlife.others.Utils;
import com.minimalart.studentlife.transitions.DetailsTransition;

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

    private static final String TAG_HOME = "HOME";
    private static final String TAG_SEARCH_RENT = "RENTS";
    private static final String TAG_ADD_RENT = "ADD_RENTS";
    private static final String TAG_SEARCH_FOOD = "FOOD";
    private static final String TAG_ADD_FOOD = "ADD_FOOD";
    private static final String TAG_ABOUT = "ABOUT";
    private static final String TAG_CONTACT = "CONTACT";
    private static final String TAG_SETTINGS = "SETTINGS";

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
    private User currentLoggedUser;
    private String currentLoggedUserUID;

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
            case "l_blue":
                setTheme(R.style.AppTheme_LightBlue);
                break;
            case "l_green":
                setTheme(R.style.AppTheme_LightGreen);
                break;
            case "yellow":
                setTheme(R.style.AppTheme_Yellow);
                break;
            case "amber":
                setTheme(R.style.AppTheme_Amber);
                break;
            case "brown":
                setTheme(R.style.AppTheme_Brown);
                break;
            case "gray":
                setTheme(R.style.AppTheme_Gray);
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
        currentLoggedUser = null;

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        headerEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.current_user_email_header);
        headerName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.current_user_name_header);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });
        if(Utils.getInstance().isConnectedToNetwork(getBaseContext()))
            getUserDataFromFirebase();
        else{
            //TODO: show no network fragment.
            Snackbar.make(drawer, getResources().getString(R.string.error_network_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }}).show();
        }
    }

    public void openFirstFragment(){
        fragment = fragmentManager.findFragmentById(R.id.content_main);
        if (fragment == null) {
            fragment = HomeFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.content_main, fragment).commitAllowingStateLoss();
            toolbar.setTitle(TITLES[0]);
        }
    }

    public void getUserDataFromFirebase(){
        DatabaseReference dbRef;
        try {
            dbRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            setCurrentUserUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }catch(NullPointerException e){
            Toast.makeText(getBaseContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
            return;
        }

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    User user = dataSnapshot.getValue(User.class);
                    setCurrentLoggedUser(user);
                    setAboutUserData(user.getName(), user.getEmail());
                    openFirstFragment();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setCurrentLoggedUser(User user){
        this.currentLoggedUser = user;
    }

    public User getCurrentLoggedUser(){
        return currentLoggedUser;
    }

    public void setCurrentUserUID(String s){
        this.currentLoggedUserUID =  s;
    }

    public String getCurrentUserUID(){
        return currentLoggedUserUID;
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
        String TAG = null;
        int t = 0;
        switch (ID) {
            case R.id.nav_home:
                fragment = HomeFragment.newInstance();
                TAG = TAG_HOME;
                t = 0;
                toolbar.setTitle(TITLES[t]);
                break;
            case R.id.nav_search_rent:
                fragment = SearchRentFragment.newInstance();
                TAG = TAG_SEARCH_RENT;
                t = 1;
                toolbar.setTitle(TITLES[t]);
                break;
            case R.id.nav_add_rent:
                fragment = AddRentFragment.newInstance();
                TAG = TAG_ADD_RENT;
                needToolbar = false;
                break;
            case R.id.nav_search_food:
                fragment = SearchFoodFragment.newInstance();
                TAG = TAG_SEARCH_FOOD;
                t = 3;
                break;
            case R.id.nav_add_food:
                fragment = FoodAlertFragment.newInstance();
                TAG = TAG_ADD_FOOD;
                needToolbar = false;
                break;
            case R.id.nav_about:
                fragment = AboutFragment.newInstance();
                TAG = TAG_ABOUT;
                t = 5;
                toolbar.setTitle(TITLES[t]);
                break;
            case R.id.nav_contact:
                fragment = ContactFragment.newInstance();
                TAG = TAG_CONTACT;
                t = 6;
                toolbar.setTitle(TITLES[t]);
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
                fragment = MyProfileFragment.newInstance();
                needToolbar = false;
                break;
            case R.id.nav_settings:
                fragment = null;
                break;
            default:
                fragment = HomeFragment.newInstance();
                t = 0;
                toolbar.setTitle(TITLES[t]);
                break;
        }

        if(t == 0)
            appBarLayout.setElevation(4);
        else
            appBarLayout.setElevation(0);

        if (needToolbar && fragment != null)
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.content_main, fragment, TAG)
                    .addToBackStack(null)
                    .commit();
        else if (fragment != null) {
            fragment.setEnterTransition(new Slide());
            fragment.setExitTransition(new Slide());
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .add(R.id.content_main_without_toolbar, fragment, TAG)
                    .addToBackStack(null)
                    .commit();
        }
        else if(fragment == null){
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

        final DatabaseReference nrRentRef = databaseReference.child("users-details")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("rent-numbers");
        nrRentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class) == null){
                    long val = 1;
                    nrRentRef.setValue(val);
                }else{
                    long currAnnounces = dataSnapshot.getValue(Long.class);
                    nrRentRef.setValue(currAnnounces+1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    /**
     * Adding a new food announce to the database
     * @param cardFoodZone : current announce to be added
     * @param image : current image in byte-format, ready to be uploaded to cloud storage
     */
    public void addNewFood(CardFoodZone cardFoodZone, byte[] image) {
        DatabaseReference newRef = databaseReference.child(REF_FOOD).push();
        newRef.setValue(cardFoodZone);

        final DatabaseReference nrFoodRef = databaseReference.child("users-details")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("food-numbers");
        nrFoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class) == null){
                    long val = 1;
                    nrFoodRef.setValue(val);
                }else{
                    long currAnnounces = dataSnapshot.getValue(Long.class);
                    nrFoodRef.setValue(currAnnounces+1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
    public void openRentAnnounceFragment(CardRentAnnounce cardRentAnnounce, ImageView image) {
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

    public void openTestFragment(ImageView image){
        TestFragment fragment = TestFragment.newInstance();
        fragment.setPoz(0);
        fragment.setSharedElementEnterTransition(new DetailsTransition());
        fragment.setSharedElementReturnTransition(new DetailsTransition());
        fragment.setEnterTransition(new Slide());
        fragment.setExitTransition(new Slide());


        fragmentManager.beginTransaction()
                .addSharedElement(image, image.getTransitionName())
                .replace(R.id.content_main, fragment)
                .addToBackStack(null)
                .commit();

    }
}
