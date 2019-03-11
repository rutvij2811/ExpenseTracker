package com.example.myapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static android.content.ContentValues.TAG;

public class home extends AppCompatActivity {
    private Button logout,settings,profile,manlog,analysis,addRec;
    private EditText et_cat,et_amt,et_date;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference recordRef;
    private DocumentReference loginRef;
    String userToUse;
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_AMT= "amt";
    private static final String KEY_DATE = "date";
    private static final String TAG = "home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getLoginDetails();
        et_cat = findViewById(R.id.home_cat);
        //To make sp_cat spinner by getting the list of categories. First work on adding categories.
        et_amt = findViewById(R.id.home_amt);
        et_date = findViewById(R.id.home_date);
        addRec = findViewById(R.id.home_add);

        addRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecord();

            }
        });
        logout =  findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmain();
            }
        });
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opensettings();
            }
        });
        profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilemanage();
            }
        });
        manlog = findViewById(R.id.loginman);
        manlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginmanage();
            }
        });
        analysis = findViewById(R.id.analysis);
        analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openanalysis();
            }
        });
    }

    private void addRecord() {
//        getCategoryList();
        String catName = et_cat.getText().toString();
        if(et_cat.getText().toString().isEmpty() || et_amt.getText().toString().isEmpty() || et_date.getText().toString().isEmpty()){
            Toast.makeText(this, "Make sure none of the fields are empty", Toast.LENGTH_SHORT).show();
        }
        else{
            recordRef = db.collection(userToUse +" Record").document();
            Map<String, Object> insertRecord = new HashMap<>();
            insertRecord.put(KEY_CATEGORY, et_cat.getText().toString());
            insertRecord.put(KEY_AMT,Integer.parseInt(et_amt.getText().toString()));
            insertRecord.put(KEY_DATE, getDateFromString(et_date.getText().toString()));
            recordRef.set(insertRecord)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(home.this, "Record added", Toast.LENGTH_SHORT).show();
//                            getCategoryList();
                            et_amt.setText(null);
                            et_date.setText(null);
                            et_cat.setText(null);
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


    private void getLoginDetails() {
        loginRef = db.collection("login").document("username");
        loginRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            userToUse = documentSnapshot.getString("Username");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void openmain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void opensettings(){
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void profilemanage(){
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void loginmanage(){
        Intent intent = new Intent(this, manlog.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void openanalysis(){
        Intent intent = new Intent(this, analysis.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    static final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    public Date getDateFromString(String datetoSaved){

        try {
            Date date = format.parse(datetoSaved);
            return date ;
        } catch (ParseException e){
            return null ;
        }

    }

}
