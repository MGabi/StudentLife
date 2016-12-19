package com.minimalart.studentlife.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.fragments.navdrawer.AboutFragment;
import com.minimalart.studentlife.fragments.navdrawer.AddRentFragment;
import com.minimalart.studentlife.fragments.navdrawer.ContactFragment;
import com.minimalart.studentlife.fragments.navdrawer.FoodAlertFragment;
import com.minimalart.studentlife.fragments.navdrawer.FoodZoneFragment;
import com.minimalart.studentlife.fragments.navdrawer.HomeFragment;
import com.minimalart.studentlife.fragments.navdrawer.SearchRentFragment;
import com.minimalart.studentlife.models.CardRentAnnounce;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<String> announceKeys;
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

        announceKeys = new ArrayList<>();

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
        }
    }

    /**
     * Loading the navmenu fragment by clicking drawer-items
     * @param ID : ID of the clicked button
     */
    public void loadNavMenuFragments(int ID){
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_main);
        int t = 0;
        switch(ID){
            case R.id.nav_home:
                fragment = HomeFragment.newInstance();
                t=0;
                break;
            case R.id.nav_search_rent:
                fragment = SearchRentFragment.newInstance();
                t=1;
                break;
            case R.id.nav_add_rent:
                fragment = AddRentFragment.newInstance();
                t=2;
                break;
            case R.id.nav_search_food:
                fragment = FoodZoneFragment.newInstance();
                t=3;
                break;
            case R.id.nav_add_food:
                fragment = FoodAlertFragment.newInstance();
                t=4;
                break;
            case R.id.nav_about:
                fragment = AboutFragment.newInstance();
                t=5;
                break;
            case R.id.nav_contact:
                fragment = ContactFragment.newInstance();
                t=6;
                break;
            default:
                fragment = HomeFragment.newInstance();
                t=0;
        }
        toolbar.setTitle(TITLES[t]);
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
    public void addNewRentAnnounce(CardRentAnnounce cardRentAnnounce){
        DatabaseReference newRef = databaseReference.child("rent-announces").push();
        newRef.setValue(cardRentAnnounce);
        //announceKeys.add(newRef.getKey());
    }
}
