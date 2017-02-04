package com.minimalart.studentlife.fragments.navdrawer;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.dialogs.SavePhoneDialog;
import com.minimalart.studentlife.dialogs.SavePhotoDialog;
import com.minimalart.studentlife.fragments.ModifyRentsFragment;
import com.minimalart.studentlife.models.User;
import com.minimalart.studentlife.dialogs.SaveEmailDialog;
import com.minimalart.studentlife.dialogs.SaveNameDialog;
import com.minimalart.studentlife.others.Utils;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final String REF_USER_IMAGES = "user-images";

    private boolean isTheTitleVisible = false;

    private TextView title;
    private TextView upTitle;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ImageButton backButton;
    private User user;
    private String currentUserUID;
    private ImageButton editName;
    private ImageButton editEmail;
    private ImageButton editPhone;
    private ImageButton editPhoto;
    private ImageButton editRents;
    private ImageButton editFood;
    private CircleImageView mainImage;

    public MyProfileFragment() {

    }

    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.myprofile_toolbar);
        title = (TextView) view.findViewById(R.id.myprofile_textview_title);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.myprofile_appbar);
        mainImage = (CircleImageView)view.findViewById(R.id.myprofile_main_image);
        backButton = (ImageButton) view.findViewById(R.id.my_profile_back);
        appBarLayout.addOnOffsetChangedListener(this);

        getUserDataFromFirebase(view);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                //getImageURLandSetToImageView(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });

        startAlphaAnimation(title, 0, View.INVISIBLE);
        return view;
    }

    public void getUserDataFromFirebase(final View v){
        if(Utils.getInstance().isConnectedToNetwork(getContext())){
            getImageURLandSetToImageView(FirebaseAuth.getInstance().getCurrentUser().getUid());
            DatabaseReference dbRef;
            try {
                dbRef = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }catch(NullPointerException e){
                Toast.makeText(getContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                return;
            }

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null){
                        User user = dataSnapshot.getValue(User.class);
                        setUser(user);
                        initViews(v);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Snackbar.make(v, getResources().getString(R.string.error_network_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }}).show();
        }
    }

    public void getImageURLandSetToImageView(String imageID){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(REF_USER_IMAGES).child(imageID);
        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .placeholder(R.drawable.person_no_photo)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(mainImage);
    }

    public void setUser(User u){
        this.user = u;
    }

    public User getUser(){
        return user;
    }

    public void initViews(final View view){
        upTitle = (TextView) view.findViewById(R.id.myprofile_textview_title_dissapearing);
        editName = (ImageButton)view.findViewById(R.id.myprofile_editname);
        editEmail = (ImageButton)view.findViewById(R.id.myprofile_editemail);
        editPhone = (ImageButton)view.findViewById(R.id.myprofile_editphone);
        editPhoto = (ImageButton)view.findViewById(R.id.myprofile_editphoto);
        editRents = (ImageButton)view.findViewById(R.id.myprofile_editrents);
        editFood = (ImageButton)view.findViewById(R.id.myprofile_editfood);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveNameDialog dialog = new SaveNameDialog(user.getName(), user.getSecName(), view);
                dialog.show(getActivity().getSupportFragmentManager(), "DIAG_CHANGE_NAME");
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveEmailDialog dialog = new SaveEmailDialog(user.getEmail(), view);
                dialog.show(getActivity().getSupportFragmentManager(), "DIAG_CHANGE_EMAIL");
            }
        });

        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavePhoneDialog dialog = new SavePhoneDialog("0749838460", view);
                dialog.show(getActivity().getSupportFragmentManager(), "DIAG_CHANGE_PHONE");
            }
        });

        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavePhotoDialog dialog = new SavePhotoDialog(view, mainImage.getDrawable());
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        getImageURLandSetToImageView(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }
                });
                dialog.show(getActivity().getSupportFragmentManager(), "DIAG_CHANGE_PHOTO");
            }
        });

        editRents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .add(R.id.content_main_without_toolbar, ModifyRentsFragment.newInstance(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        title.setText(user.getName());
        upTitle.setText(user.getName() + " " + user.getSecName());
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!isTheTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                isTheTitleVisible = true;
            }

        } else {

            if (isTheTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                isTheTitleVisible = false;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

}
