package com.example.myapp;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class manlog extends AppCompatActivity {
    private Button manlog_back, btn_savechanges;
    private TextView tv_user_ML;
    private EditText et_oldPassML, et_newPassML;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String KEY_PASSWORD = "password";
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manlog);

        tv_user_ML = findViewById(R.id.tv_user_ML);
        SharedPreferences userNamePref = getSharedPreferences("username", MODE_PRIVATE);
        String userName = userNamePref.getString("userName", "");
        tv_user_ML.setText(userName);
        et_oldPassML = findViewById(R.id.et_oldPassML);
        et_newPassML = findViewById(R.id.et_newPassML);
        btn_savechanges = findViewById(R.id.btn_savechanges);

        btn_savechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_oldPassML.getText().toString().isEmpty() || et_newPassML.getText().toString().isEmpty()) {
                    Toast.makeText(manlog.this, "Make sure none of the fields are empty.", Toast.LENGTH_SHORT).show();
                } else if (et_oldPassML.getText().toString().length() < 6 || et_newPassML.getText().toString().length() < 6) {
                    Toast.makeText(manlog.this, "Min password length is 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    checkOldPass();
                }
            }
        });


        manlog_back = (Button) findViewById(R.id.manlog_back);
        manlog_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhome();
            }
        });
    }

    private void checkOldPass() {
        userRef = db.collection("ExpenseTracker").document("user " + tv_user_ML.getText().toString());
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String actualPass = documentSnapshot.getString("password");
                        if (actualPass.matches(et_oldPassML.getText().toString())) {
                            allowUpdateOnPass();
                        }
                        else {
                            Toast.makeText(manlog.this, "Old password is wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(manlog.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void allowUpdateOnPass() {
        userRef = db.collection("ExpenseTracker").document("user " + tv_user_ML.getText().toString());
        User user = new User();
        user.setPassword(et_newPassML.getText().toString());

        Map<String, Object> usr = new HashMap<>();
        usr.put(KEY_PASSWORD,user.getPassword());

        userRef.set(usr, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(manlog.this, "Success", Toast.LENGTH_SHORT).show();
                        openhome();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(manlog.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void openhome() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

