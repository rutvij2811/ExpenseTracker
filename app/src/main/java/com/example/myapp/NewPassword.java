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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class NewPassword extends AppCompatActivity {
    TextView verifiedUser;
    EditText et_newPass, et_confNewPass;
    Button btn_update;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        SharedPreferences sharedPref = getSharedPreferences("userVerify", Context.MODE_PRIVATE);
        String userVerified = sharedPref.getString("verifiedUser", "");
        verifiedUser = findViewById(R.id.verifiedUser);
        verifiedUser.setText(userVerified);
        et_newPass = findViewById(R.id.et_newPass);
        et_confNewPass = findViewById(R.id.et_confNewPass);
        btn_update = findViewById(R.id.btn_update);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserPassword();
            }
        });
    }

    private void updateUserPassword() {
        userRef = db.collection("ExpenseTracker").document("user " + verifiedUser.getText().toString());
        if (et_newPass.getText().toString().matches(et_confNewPass.getText().toString())) {
            User user = new User();
            user.setPassword(et_newPass.getText().toString());

            Map<String, Object> usr = new HashMap<>();
            usr.put(KEY_PASSWORD,user.getPassword());

            userRef.set(usr, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NewPassword.this, "Password Reset Successful", Toast.LENGTH_SHORT).show();
                            gotoMain();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewPassword.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "Password and confirm password not same", Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoMain() {
        startActivity(new Intent(NewPassword.this,MainActivity.class));
    }
}