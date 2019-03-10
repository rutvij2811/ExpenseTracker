package com.example.myapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {
    EditText et_phone, et_addr;
    private Button back, btn_save;
    private static final String TAG = "profile";
    private DocumentReference loginRef,userRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tv_username,et_dob;
    private static final String KEY_PHONE ="phone";
    private static final String KEY_ADDR = "address";
    private static final String KEY_DOB = "dob";
    DatePickerDialog.OnDateSetListener mDateSetListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        et_dob =  findViewById(R.id.pro_dob);
        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        profile.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog,
                        mDateSetListner,
                        year, month, day);
                dialog.show();
            }
        });
        mDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String dob = dayOfMonth + "-" + month + "-" + year;
                et_dob.setText(dob);

            }
        };
        et_phone =  findViewById(R.id.pro_phone);
        et_addr = findViewById(R.id.pro_addr);
        tv_username =findViewById(R.id.user_pro);
        btn_save =  findViewById(R.id.pro_save);
        loginRef = db.collection("login").document("username");
        loginRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String userToUse = documentSnapshot.getString("Username");
                            Toast.makeText(profile.this, userToUse, Toast.LENGTH_SHORT).show();
                            tv_username.setText(userToUse);
                            getUserData();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateValues();
            }
        });

        back =  findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhome();
            }
        });
    }

    private void getUserData() {
        userRef = db.collection("ExpenseTracker").document("user "+tv_username.getText().toString());
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
//                            String temp_dob = documentSnapshot.getString("dob");
                            et_dob.setText(documentSnapshot.getString("dob"));
                            et_phone.setText(documentSnapshot.getString("phone"));
                            et_addr.setText(documentSnapshot.getString("address"));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void updateValues() {
        userRef = db.collection("ExpenseTracker").document("user "+tv_username.getText().toString());

        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put(KEY_DOB,et_dob.getText().toString());
        userUpdate.put(KEY_PHONE,et_phone.getText().toString());
        userUpdate.put(KEY_ADDR,et_addr.getText().toString());
        if(et_phone.getText().toString().length() != 10){
            Toast.makeText(this, "The phone number should have 10 digits.", Toast.LENGTH_SHORT).show();
        }
        else{

            userRef.set(userUpdate, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(profile.this, "Updated", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    });
        }

    }


    public void openhome() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

