package com.example.myapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class settings3 extends AppCompatActivity {
    private static final String TAG = "settings3";
    private static final String KEY_CAT = "category";
    private static final String KEY_AMT = "amount";
    TextView tv_showCat;
    String userToUse;
    Button bt_show;
    private Button back, showcat3, addlt3, addcat3;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference loginRef = db.collection("login").document("username");
    private CollectionReference showCatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings3);

        bt_show = findViewById(R.id.set3_showbtn);
        tv_showCat = findViewById(R.id.set3_showcat);
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhome();
            }
        });
        showcat3 = (Button) findViewById(R.id.showcat3);
        showcat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcategory();
            }
        });
        addlt3 = (Button) findViewById(R.id.addlt3);
        addlt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addlimit();
            }
        });
        addcat3 = (Button) findViewById(R.id.addcat3);
        addcat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addcategory();
            }
        });

        bt_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyCat();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                        Toast.makeText(settings3.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showMyCat() {
        showCatRef = db.collection(userToUse+" Categories");
        showCatRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String showCat = "";

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if(documentSnapshot.exists()) {
                                    String catName = documentSnapshot.getString("category");
                                    String catAmt = documentSnapshot.get("amount").toString();

                                    showCat += catName + " : " + catAmt + "\n\n";
                                }else{
                                    Toast.makeText(settings3.this, "some error", Toast.LENGTH_SHORT).show();
                                }
                            }
                            tv_showCat.setText(showCat);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(settings3.this, "Some Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void openhome() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }

    public void showcategory() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void addlimit() {
        Intent intent = new Intent(this, settings4.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void addcategory() {
        Intent intent = new Intent(this, settings2.class);
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
