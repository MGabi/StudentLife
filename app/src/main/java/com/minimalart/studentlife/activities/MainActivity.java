package com.minimalart.studentlife.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.View;
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
import com.minimalart.studentlife.fragments.navdrawer.AboutFragment;
import com.minimalart.studentlife.fragments.navdrawer.AddRentFragment;
import com.minimalart.studentlife.fragments.navdrawer.ContactFragment;
import com.minimalart.studentlife.fragments.navdrawer.FoodAlertFragment;
import com.minimalart.studentlife.fragments.navdrawer.FoodZoneFragment;
import com.minimalart.studentlife.fragments.navdrawer.HomeFragment;
import com.minimalart.studentlife.fragments.navdrawer.SearchRentFragment;
import com.minimalart.studentlife.interfaces.CardLinks;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.models.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CardLinks {

    private static final String REF_RENT = "rent-announces";
    private static final String REF_FOOD = "food-announces";
    private static final String REF_RENT_IMAGES = "rent-images";
    private static final String REF_FOOD_IMAGES = "food-images";
    private static final String GPLUS_URL = "https://plus.google.com/+GabiMatkoRO";
    private static final String DEV_TITLE = "Contact STUDENT LIFE";
    private static final String GPLAY_URL = "https://play.google.com/store/apps/dev?id=6865542268483195156";

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    /**
     * Titles for navdrawer items
     */
    private final static String[] TITLES = {
            "Acasă",
            "Chirii",
            "Adaugă anunț chirie",
            "Caută mese",
            "Adaugă ofertă masă",
            "Despre",
            "Contact"
    };

    private static MainActivity mainActivityInstance = new MainActivity();

    public static MainActivity newInstance() {
        return mainActivityInstance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_main);

        if(fragment == null){
            fragment = HomeFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.content_main, fragment).commit();
            toolbar.setTitle(TITLES[0]);
        }
    }

    public void showUser(User user){
        Log.v("DSREF", user.getName() + ", " + user.getSecName() + ", " + user.getEmail() + ", " + user.getAge());
    }
    /**
     * Handling the action on pressing the back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            /*finish();*/
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

    /**
     * Loading the navmenu fragment by clicking drawer-items
     * @param ID : ID of the clicked button
     */
    public void loadNavMenuFragments(int ID){
        Fragment fragment;
        int t = 0;
        int visibility;
        switch(ID){
            case R.id.nav_home:
                fragment = HomeFragment.newInstance();
                visibility = View.VISIBLE;
                t=0;
                break;
            case R.id.nav_search_rent:
                fragment = SearchRentFragment.newInstance();
                visibility = View.VISIBLE;
                t=1;
                break;
            case R.id.nav_add_rent:
                fragment = AddRentFragment.newInstance();
                visibility = View.GONE;
                t=2;
                break;
            case R.id.nav_search_food:
                fragment = FoodZoneFragment.newInstance();
                visibility = View.VISIBLE;
                t=3;
                break;
            case R.id.nav_add_food:
                fragment = FoodAlertFragment.newInstance();
                visibility = View.GONE;
                t=4;
                break;
            case R.id.nav_about:
                fragment = AboutFragment.newInstance();
                visibility = View.VISIBLE;
                t=5;
                break;
            case R.id.nav_contact:
                fragment = ContactFragment.newInstance();
                visibility = View.VISIBLE;
                t=6;
                break;
            case R.id.nav_logout:
                Toast.makeText(getBaseContext(), "Should be logged out", Toast.LENGTH_LONG).show();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            default:
                fragment = HomeFragment.newInstance();
                visibility = View.VISIBLE;
                t=0;
        }
        toolbar.setTitle(TITLES[t]);
        toolbar.setVisibility(visibility);
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
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
     * @param cardRentAnnounce : current announce to be added
     */
    public void addNewRentAnnounce(CardRentAnnounce cardRentAnnounce, byte[] image){
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

    public void addNewFood(CardFoodZone cardFoodZone, byte[] image){
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

    @Override
    public void openGPLUS() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GPLUS_URL));
        Intent chooser = Intent.createChooser(browserIntent, "Alege browser");
        startActivity(chooser);
    }

    @Override
    public void openEMAILSender() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ytgabi98@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, DEV_TITLE);
        Intent chooser = Intent.createChooser(emailIntent, "Trimite e-mail...");
        startActivity(chooser);
    }

    @Override
    public void openGPLAY() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GPLAY_URL));
        Intent chooser = Intent.createChooser(browserIntent, null);
        startActivity(chooser);
    }
}
