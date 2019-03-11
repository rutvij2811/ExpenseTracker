package com.example.myapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class settings4 extends AppCompatActivity {
    private Button back,addlt4,showcat4,addcat4;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference loginRef,updateCatRef;
    private static final String TAG = "settings4";
    private static final String KEY_CAT = "category";
    private static final String KEY_AMT = "amount";
    String userToUse;
    private CollectionReference getCatRef;
    private ArrayList<String> categoryList = new ArrayList<>();


    EditText et_cat,et_amt;
    Button btn_update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings4);

        et_cat = findViewById(R.id.set4_cat);
        et_amt = findViewById(R.id.set4_amt);
        btn_update = findViewById(R.id.set4_update);

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
                        Toast.makeText(settings4.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategory();
            }
        });

        back =  findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhome();
            }
        });
        addlt4 = findViewById(R.id.addlt4);
        addlt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addlimit();
            }
        });
        addcat4 = findViewById(R.id.addcat4);
        addcat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addcategory();
            }
        });
        showcat4 = findViewById(R.id.showcat4);
        showcat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcategory();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCategoryList();
    }

    private void getCategoryList() {
        categoryList.clear();
        getCatRef = db.collection(userToUse +" Categories");
        getCatRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String catName = documentSnapshot.getId();
                            categoryList.add(catName);
                        }
                    }
                });
    }

    private void updateCategory() {
        updateCatRef = db.collection(userToUse +" Categories").document(et_cat.getText().toString());

        Map<String, Object> updateCat = new HashMap<>();
        updateCat.put(KEY_CAT,et_cat.getText().toString());
        updateCat.put(KEY_AMT,Integer.parseInt(et_amt.getText().toString()));

        if( et_cat.getText().toString().isEmpty() || et_amt.getText().toString().isEmpty()){
            Toast.makeText(this, "Make sure no field is empty", Toast.LENGTH_SHORT).show();
        }
        else if(!categoryList.contains(et_cat.getText())){
            Toast.makeText(this, "Category not yet defined", Toast.LENGTH_SHORT).show();
        }
        else{
            updateCatRef.set(updateCat, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(settings4.this, "Amount added to "+ et_cat.getText().toString(), Toast.LENGTH_SHORT).show();
                            getCategoryList();
                            et_cat.setText(null);
                            et_amt.setText(null);
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
    }
    public void addlimit() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
    public void addcategory() {
        Intent intent = new Intent(this, settings2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
    public void showcategory() {
        Intent intent = new Intent(this, settings3.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
