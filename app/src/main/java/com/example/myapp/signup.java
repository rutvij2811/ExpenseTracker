package com.example.myapp;

import android.app.DatePickerDialog;
import android.content.ContentValues;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class signup extends AppCompatActivity {
    EditText edit_username, edit_pass;
    Button btn_createAcc;
    TextView edit_dob;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;



    DatePickerDialog.OnDateSetListener mDateSetListner;
    private static final String TAG = "signup";
    private static final String KEY_USERNAME ="username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_DOB = "dob";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edit_username = findViewById(R.id.username);
        edit_dob = findViewById(R.id.dob);


        edit_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        signup.this,
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
                edit_dob.setText(dob);

            }
        };

        edit_pass = findViewById(R.id.pass);
        btn_createAcc = findViewById(R.id.createAcc);

        btn_createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setUsername(edit_username.getText().toString());
                user.setPassword(edit_pass.getText().toString());
                user.setDob(edit_dob.getText().toString());


                Map<String, Object> usr = new HashMap<>();
                usr.put(KEY_USERNAME,user.getUsername());
                usr.put(KEY_DOB,user.getDob());
                usr.put(KEY_PASSWORD,user.getPassword());
                userRef = db.collection("ExpenseTracker").document("user "+user.getUsername());

                if (user.getDob().isEmpty() || user.getUsername().isEmpty() || user.getPassword().isEmpty()){
                    Toast.makeText(signup.this, "Make sure none of the field is empty", Toast.LENGTH_SHORT).show();
                }
                else if (user.getUsername().length() < 3){
                    Toast.makeText(signup.this, "Minimum username length should be 3 characters", Toast.LENGTH_SHORT).show();
                }
                else if (user.getPassword().length() < 6){
                    Toast.makeText(signup.this, "Minimum pass length is 6 characters", Toast.LENGTH_SHORT).show();
                }
                else{
                    userRef.set(usr)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(signup.this, "Success", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(signup.this, "Failed to register", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG,e.toString());
                                }
                            });
                }

            }
        });
    }

    private void gotomain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
