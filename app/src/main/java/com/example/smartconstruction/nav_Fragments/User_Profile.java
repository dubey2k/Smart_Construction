package com.example.smartconstruction.nav_Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartconstruction.R;
import com.example.smartconstruction.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class User_Profile extends Fragment {

    CircleImageView profile_pic;
    TextView USER_name, ACCType, Email, Phone;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference cr = FirebaseFirestore.getInstance().collection("Users");
    private StorageReference reference = FirebaseStorage.getInstance().getReference("Images/UserImages");
    private StorageReference tempref;
    private int PICK_IMAGE_REQUEST = 1;

    Uri newimageUri;


    Bitmap bit;
    boolean isImageLoaded = false;

    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile, container, false);
        profile_pic = view.findViewById(R.id.USER_image);
        profile_pic.setImageResource(R.drawable.default_user_image);
        USER_name = view.findViewById(R.id.USER_name);
        Email = view.findViewById(R.id.USER_email);
        Phone = view.findViewById(R.id.USER_phone);
        ACCType = view.findViewById(R.id.Acc_type);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        cr.document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    user = documentSnapshot.toObject(User.class);
                    USER_name.setText(user.getName());
                    Email.setText(user.getEmail());
                    Phone.setText(user.getPhoneNumber());
                    ACCType.setText(user.getMemberType());
                    if (!user.getProfilePicture().equals("")) {
                        tempref = FirebaseStorage.getInstance().getReferenceFromUrl(user.getProfilePicture());
                        ImageLoadTask imageLoadTask = new ImageLoadTask(user.getProfilePicture(), profile_pic);
                        imageLoadTask.execute();
                    } else
                        tempref = null;
                }
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
                bottomSheetDialog.setContentView(R.layout.bottomsheet_userimage);
                if (isImageLoaded) {
                    TextView ViewPic = bottomSheetDialog.findViewById(R.id.View_ProfilePic);
                    ViewPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alt = new AlertDialog.Builder(getActivity());
                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                            View view = inflater.inflate(R.layout.view_pic_dialog, null);
                            alt.setView(view);
                            final AlertDialog alertDialog = alt.create();

                            ImageView image = view.findViewById(R.id.ViewPic_DialogImage);
                            if (bit != null)
                                image.setImageBitmap(bit);
                            else
                                image.setImageResource(R.drawable.default_user_image);
                            alertDialog.show();

                        }
                    });
                }

                TextView NewPic = bottomSheetDialog.findViewById(R.id.New_ProfilePic);
                NewPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tempref != null)
                            tempref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    profile_pic.setImageResource(R.drawable.default_user_image);
                                    chooseImage();
                                    UploadTask upload = reference.child(user.getEmail()).putFile(newimageUri);

                                    upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            reference.child(user.getEmail()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    cr.document(user.getEmail()).update("profilePicture", uri.toString());
                                                    ImageLoadTask imageLoadTask = new ImageLoadTask(user.getProfilePicture(), profile_pic);
                                                    imageLoadTask.execute();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        else {

                            chooseImage();
                            UploadTask upload = reference.child(user.getEmail()).putFile(newimageUri);

                            upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.child(user.getEmail()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            cr.document(user.getEmail()).update("profilePicture", uri.toString());
                                            ImageLoadTask imageLoadTask = new ImageLoadTask(user.getProfilePicture(), profile_pic);
                                            imageLoadTask.execute();
                                        }
                                    });
                                }
                            });
                        }

                    }
                });
                TextView RemovePic = bottomSheetDialog.findViewById(R.id.Remove_pic);
                bottomSheetDialog.show();
            }
        });
        super.onActivityCreated(savedInstanceState);

    }


    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            bit = result;
            isImageLoaded = true;
            imageView.setImageBitmap(result);
        }

    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            newimageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), newimageUri);

                profile_pic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
