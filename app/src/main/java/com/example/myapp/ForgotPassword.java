package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPassword extends AppCompatActivity {
    private EditText et_fp_user,et_fp_secret;
    private Button btn_verify;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        et_fp_user = findViewById(R.id.et_fp_user);
        et_fp_secret = findViewById(R.id.et_fp_secret);
        btn_verify = findViewById(R.id.btn_verify);

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_fp_user.getText().toString().isEmpty() || et_fp_secret.getText().toString().isEmpty()){
                    Toast.makeText(ForgotPassword.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else{

                    verifyUser();
                }
            }
        });

    }

    private void verifyUser() {
        userRef = db.collection("ExpenseTracker").document("user "+ et_fp_user.getText().toString());

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists()){
                            Toast.makeText(ForgotPassword.this, "No such user found", Toast.LENGTH_SHORT).show();
                        }
                        String userSecret = et_fp_secret.getText().toString();
                        String actualSecret = documentSnapshot.getString("secret");
                        if(userSecret.equalsIgnoreCase(actualSecret)){
                            Toast.makeText(ForgotPassword.this, "User Verified", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPref = getSharedPreferences("userVerify", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor =sharedPref.edit();
                            editor.putString("verifiedUser",et_fp_user.getText().toString());
                            editor.apply();

                            gotoNewPass();
                        }else{
                            Toast.makeText(ForgotPassword.this, "Wrong Secret or username", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassword.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void gotoNewPass() {
        startActivity(new Intent(ForgotPassword.this, NewPassword.class));
    }

}
