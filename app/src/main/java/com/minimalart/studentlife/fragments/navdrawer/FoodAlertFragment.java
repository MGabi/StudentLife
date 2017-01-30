package com.minimalart.studentlife.fragments.navdrawer;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.MainActivity;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.services.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FoodAlertFragment extends Fragment {

    private TextView title;
    private TextView desc;
    private TextView price;
    private TextView restaurant;
    private Button addImgBtn;
    private CheckBox studentCheckBox;
    private Button postFoodBtn;
    private ImageButton exitBtn;
    private byte[] finalIMGByte;

    private static final int PICK_IMAGE = 191;

    public FoodAlertFragment() {
        // Required empty public constructor
    }

    /**
     * @return a reference to this fragment
     */
    public static FoodAlertFragment newInstance() {
        FoodAlertFragment fragment = new FoodAlertFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_alert, container, false);

        initializeViews(view);
        setClickListeners();

        return view;
    }

    /**
     * Initializing the necessary views
     * @param view : context view, responsible for findViewById method
     */
    public void initializeViews(View view){
        title = (TextView) view.findViewById(R.id.food_title);
        desc = (TextView) view.findViewById(R.id.food_description);
        price = (TextView) view.findViewById(R.id.food_price);
        restaurant = (TextView) view.findViewById(R.id.food_location);
        addImgBtn = (Button) view.findViewById(R.id.food_alert_img);
        postFoodBtn = (Button) view.findViewById(R.id.add_food_alert_button);
        studentCheckBox = (CheckBox) view.findViewById(R.id.food_alert_checkbox);
        exitBtn = (ImageButton) view.findViewById(R.id.food_alert_exit_btn);
        finalIMGByte = null;
    }

    /**
     * Setting click listeners for every button
     * and defining personal action for any of them
     */
    public void setClickListeners(){
        postFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean networkState = Utils.getInstance().isConnectedToNetwork(getContext());
                if(checkFields() && networkState && isImageAdded()){
                    boolean check = studentCheckBox.isChecked();
                    ((MainActivity)getActivity())
                            .addNewFood(new CardFoodZone(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    title.getText().toString(),
                                    price.getText().toString(),
                                    desc.getText().toString(),
                                    restaurant.getText().toString(),
                                    check), finalIMGByte);
                    Toast.makeText(getContext(), R.string.food_added_success, Toast.LENGTH_LONG).show();
                }else {
                    if(!networkState)
                        Toast.makeText(getContext(), R.string.error_no_network_connection, Toast.LENGTH_LONG).show();
                    if(!isImageAdded()){
                        Toast.makeText(getContext(), R.string.error_no_image, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
                Fragment fragment = HomeFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main, fragment)
                        .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                android.support.v7.app.ActionBar ab = ((MainActivity)getActivity()).getSupportActionBar();
                ab.setTitle("Acasă");
                ab.show();
            }
        });
    }

    /**
     * Opening a chooser activity for grabbing an image from the phone media
     * This intent will allow only images to be displayed
     * This intent allow choosing only one image
     */
    public void openImageChooser(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    /**
     * Callback for imageChooser activity
     * @param requestCode : initial code which was used to start the intent ( PICK_IMAGE )
     * @param resultCode : result code of the activity
     * @param data : an intent with the result of choosing images from phone
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int RESULT_OK = ((MainActivity)getActivity()).RESULT_OK;
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                final int standardWIDTH = 1080;
                float reduceBy;

                reduceBy = Float.valueOf(String.valueOf(standardWIDTH + ".0f")) / Float.valueOf(String.valueOf(bitmap.getWidth() + ".0f"));
                if(reduceBy > 1)
                    reduceBy = 1;
                int finalWidth = (int)(bitmap.getWidth() * reduceBy);
                int finalHeight = (int)(bitmap.getHeight() * reduceBy);

                Bitmap finalBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                finalIMGByte = baos.toByteArray();
                } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return if image was retrieved from phone
     */
    public Boolean isImageAdded(){
        return finalIMGByte != null;
    }

    /**
     * @return the integrity of fields
     */
    public boolean checkFields(){
        boolean fieldsGood = true;
        if(!checkTitle())
            fieldsGood = false;
        if(!checkPrice())
            fieldsGood = false;
        if(!checkLocation())
            fieldsGood = false;
        if(!checkDescription())
            fieldsGood = false;

        //return fieldsGood;
        return true;
    }

    /**
     * @return true if title is good, and false if title is too short
     */
    public boolean checkTitle(){
        if(TextUtils.isEmpty(title.getText())){
            title.setError(getString(R.string.error_field_required));
            return false;
        }else if(title.getText().length() < 10) {
            title.setError(getString(R.string.error_title_too_short));
            return false;
        }else
            return true;

    }

    /**
     * @return true if the price is not null
     */
    public boolean checkPrice(){
        if(TextUtils.isEmpty(price.getText())){
            price.setError(getString(R.string.error_field_required));
            return false;
        }else
            return true;
    }

    /**
     * @return true if location is good ( in lenght )
     */
    public boolean checkLocation(){
        if(TextUtils.isEmpty(restaurant.getText())) {
            restaurant.setError(getString(R.string.error_field_required));
            return false;
        }else if(restaurant.getText().length()<10){
            restaurant.setError(getString(R.string.error_loc_short));
            return false;
        }else
            return true;
    }

    /**
     * @return true if the announce has a detailed description
     */
    public boolean checkDescription(){
        if(TextUtils.isEmpty(desc.getText())) {
            desc.setError(getString(R.string.error_field_required));
            return false;
        }else if(desc.getText().length()<30){
            desc.setError(getString(R.string.error_desc_short));
            return false;
        }else
            return true;
    }
}
