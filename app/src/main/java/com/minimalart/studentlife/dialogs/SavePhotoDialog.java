package com.minimalart.studentlife.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.fragments.navdrawer.MyProfileFragment;
import com.minimalart.studentlife.others.Utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ytgab on 04.02.2017.
 */
@SuppressLint("ValidFragment")
public class SavePhotoDialog extends DialogFragment {

    private static final int PICK_IMAGE = 131;
    private static final String REF_USER_IMAGES = "user-images";
    private View snackView;
    private Button saveBtn;
    private Button closeBtn;
    private ImageView dialogImage;
    private ImageButton chooseImageBtn;
    private Drawable userPic;
    private byte[] finalIMGByte;
    private DialogInterface.OnDismissListener listener;

    public SavePhotoDialog(){

    }

    public SavePhotoDialog(View v, Drawable d){
        this.snackView = v;
        this.userPic = d;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.profile_change_image, null);

        closeBtn = (Button) dialogView.findViewById(R.id.close_btn);
        saveBtn = (Button) dialogView.findViewById(R.id.save_btn);
        dialogImage = (ImageView) dialogView.findViewById(R.id.dialog_photo);
        chooseImageBtn = (ImageButton) dialogView.findViewById(R.id.myprofile_select_image);
        finalIMGByte =  null;
        dialogImage.setImageDrawable(userPic);

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageAdded()){
                    if(Utils.getInstance().isConnectedToNetwork(getContext()))
                        uploadImage(finalIMGByte);
                    else{
                        Snackbar.make(snackView, getResources().getString(R.string.error_no_network_connection), Snackbar.LENGTH_LONG)
                                .setAction(getResources().getString(android.R.string.ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {}})
                                .show();
                        dismiss();
                    }
                } else{
                    Snackbar.make(snackView, getResources().getString(R.string.error_no_image_added), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(android.R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {}})
                            .show();
                    dismiss();
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        return builder.create();
    }

    public void uploadImage(byte[] image){
        final StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference()
                .child(REF_USER_IMAGES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        UploadTask uploadTask = storageReference.putBytes(image);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(snackView, getResources().getString(R.string.error_unknown), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(android.R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}})
                        .show();
                dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Snackbar.make(snackView, getResources().getString(R.string.image_added_success), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(android.R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}})
                        .show();
                dismiss();
            }
        });
    }

    public void chooseImage(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDismiss(dialog);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final int RESULT_OK = -1;
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
                dialogImage.setImageBitmap(finalBitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                finalIMGByte = baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return true if image was retrieved from phone
     */
    public Boolean isImageAdded(){
        return finalIMGByte != null;
    }
}
