package com.minimalart.studentlife.fragments.navdrawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.MainActivity;
import com.minimalart.studentlife.models.CardRentAnnounce;

public class AddRentFragment extends Fragment {

    private EditText title;
    private EditText rooms;
    private EditText price;
    private EditText location;
    private EditText description;
    private Button addImageBtn;
    private CheckBox discountCB;
    private Button postOffertBtn;

    public AddRentFragment() {
        // Required empty public constructor
    }

    /**
     * @return a reference to this fragment
     */
    public static AddRentFragment newInstance() {
        AddRentFragment fragment = new AddRentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_rent, container, false);

        title = (EditText)view.findViewById(R.id.rent_title);
        rooms = (EditText)view.findViewById(R.id.rent_rooms);
        price = (EditText)view.findViewById(R.id.rent_price);
        location = (EditText)view.findViewById(R.id.rent_location);
        description = (EditText)view.findViewById(R.id.rent_description);
        addImageBtn = (Button)view.findViewById(R.id.rent_add_img);
        discountCB = (CheckBox)view.findViewById(R.id.rent_discount_cb);
        postOffertBtn = (Button)view.findViewById(R.id.add_rent_button);

        postOffertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields()){
                    boolean check = discountCB.isChecked();
                    ((MainActivity)getActivity())
                            .addNewRentAnnounce(new CardRentAnnounce(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),
                                    title.getText().toString(),
                                    rooms.getText().toString(),
                                    price.getText().toString(),
                                    location.getText().toString(),
                                    description.getText().toString(),
                                    check));
                }else{

                }
            }
        });


        return view;
    }

    public boolean checkFields(){
        boolean fieldsGood = true;
        if(!checkTitle())
            fieldsGood = false;
        if(!checkRooms())
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

    public boolean checkRooms(){
        if(TextUtils.isEmpty(rooms.getText())) {
            rooms.setError(getString(R.string.error_field_required));
            return false;
        }else if(Integer.parseInt(rooms.getText().toString())>10){
            rooms.setError(getString(R.string.error_rooms_too_big));
            return false;
        }else
            return true;
    }

    public boolean checkPrice(){
        if(TextUtils.isEmpty(price.getText())){
            price.setError(getString(R.string.error_field_required));
            return false;
        }else
            return true;
    }

    public boolean checkLocation(){
        if(TextUtils.isEmpty(location.getText())) {
            location.setError(getString(R.string.error_field_required));
            return false;
        }else if(location.getText().length()<10){
            location.setError(getString(R.string.error_loc_short));
            return false;
        }else
            return true;
    }

    public boolean checkDescription(){
        if(TextUtils.isEmpty(description.getText())) {
            description.setError(getString(R.string.error_field_required));
            return false;
        }else if(description.getText().length()<60){
            description.setError(getString(R.string.error_desc_short));
            return false;
        }else
            return true;
    }


}
