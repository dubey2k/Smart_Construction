package com.example.smartconstruction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class Register_Activity extends AppCompatActivity {

    TextInputEditText email;
    TextInputEditText pass;
    TextInputEditText name;
    TextInputEditText phone;
    ImageView addpic;
    Button register;
    RadioGroup radioGroup;
    RadioButton radioButton;
    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    private User user;
    public String member;
    private StorageReference ref;
    private Uri imageUri;
    private CollectionReference cr = FirebaseFirestore.getInstance().collection("Users");
    private int PICK_IMAGE_REQUEST = 1;
    private boolean imagePicked = false;
    private String TAG = "MyTag";

    public static boolean isAgreed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        email = findViewById(R.id.user_email_register);
        pass = findViewById(R.id.user_pass_register);
        register = findViewById(R.id.registerBtn);
        name = findViewById(R.id.user_name_register);
        phone = findViewById(R.id.phoneNumber);
        addpic = findViewById(R.id.profilePic);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);
        radioGroup = findViewById(R.id.memberType);
        radioButton = findViewById(R.id.Customer);


        addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            public StorageReference tempref = FirebaseStorage.getInstance().
                    getReference("Images/UserImages/");

            @Override
            public void onClick(View v) {

                final String Email = email.getText().toString().trim();
                final String Name = name.getText().toString().trim();
                final String Pass = pass.getText().toString().trim();
                final String Phone = phone.getText().toString().trim();

                if (TextUtils.isEmpty(Email)) {
                    email.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(Pass)) {
                    pass.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(Phone)) {
                    pass.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(Name)) {
                    pass.setError("Required Field");
                    return;
                }
                if (Phone.length() != 10) {
                    phone.setError("invalid phone number");
                }
                mDialog.setMessage("Registering...");
                mDialog.show();


                mAuth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            member = (String) radioButton.getText();

                            UserProfileChangeRequest userPCR = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.getText().toString()).build();
                            mAuth.getCurrentUser().updateProfile(userPCR);

                            if (!imagePicked) {
                                user = new User(Name, Email, Pass, Phone, member);
                                getSupportFragmentManager().beginTransaction().add(R.id.Register_Activity, new termsANDconditions_Fragment())
                                        .commit();
                                mDialog.show();
                                cr.document(Email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDialog.dismiss();
                                    }
                                });

                                finish();
                            } else {
                                mDialog.dismiss();
                                getSupportFragmentManager().beginTransaction().add(R.id.Register_Activity, new termsANDconditions_Fragment())
                                        .commit();
                                mDialog.show();

                                UploadTask upload = tempref.child(Email).putFile(imageUri);

                                upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.d(TAG, "onSuccess: upload");
                                        tempref.child(Email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Log.d(TAG, "onSuccess: uri");
                                                Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_SHORT).show();
                                                user = new User(Name, Email, Pass, Phone, uri.toString(), member);
                                                cr.document(Email).set(user);
                                                mDialog.dismiss();
                                                finish();
                                            }
                                        });
                                    }
                                });

                            }


                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                });

            }
        });

    }

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
    }


    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                ImageView imageView = findViewById(R.id.profilePic);
                imageView.setImageBitmap(bitmap);
                imagePicked = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

