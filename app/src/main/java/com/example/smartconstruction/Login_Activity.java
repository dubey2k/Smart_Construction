package com.example.smartconstruction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login_Activity extends AppCompatActivity {

    TextInputEditText email;
    TextInputEditText pass;
    Button login,linkBtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ProgressDialog mDialog ;
    TextView ForRegisteration , ForgetPassword ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.user_email_login);
        pass=findViewById(R.id.user_pass_login);
        login=findViewById(R.id.loginBtn);
        ForRegisteration=findViewById(R.id.toRegister);
        ForgetPassword=findViewById(R.id.forget_pass);
        mDialog = new ProgressDialog(this);

        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alt = new AlertDialog.Builder(Login_Activity.this);
                LayoutInflater inflater = LayoutInflater.from(Login_Activity.this);
                View view = inflater.inflate(R.layout.forget_pass_dialog, null);
                alt.setView(view);
                final AlertDialog alertDialog=alt.create();

                final TextInputEditText emailLink = view.findViewById(R.id.forgetEmail);
                linkBtn=view.findViewById(R.id.forgetSendLinkBtn);
                linkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Email=emailLink.getText()+"";
                        if(TextUtils.isEmpty(Email)){
                            emailLink.setError("can't be Empty");
                        }

                        mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Login_Activity.this,"Link is sent to the Email",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }

                        });
                    }
                });
                alertDialog.show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                String Pass = pass.getText().toString().trim();

                if (TextUtils.isEmpty(Email)) {
                    email.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(Pass)) {
                    pass.setError("Required Field");
                    return;
                }
                mDialog.setMessage("Login you In...");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            mDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                });

            }
        });
        ForRegisteration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), Register_Activity.class), 123);
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String mail = mAuth.getCurrentUser().getEmail();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

}
