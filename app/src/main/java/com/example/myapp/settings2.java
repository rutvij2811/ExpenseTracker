package com.example.myapp;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class settings2 extends AppCompatActivity {
    private Button back,addcat2,showcat2,addlt2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button bt_addCat;
    private EditText et_catName;
    private TextToSpeech mTTS;
    String userToUse;
    private DocumentReference loginRef,catRef;
    private static final String TAG = "settings2";
    private static final String KEY_CAT = "category";
    private static final String KEY_AMT = "amount";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        mTTS =new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTS.setLanguage(Locale.ENGLISH);
            }
        });

        et_catName = findViewById(R.id.set2_cat);
        bt_addCat = findViewById(R.id.set2_add);

        bt_addCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoginDetails();
            }
        });


        back = (Button) findViewById(R.id.back2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhome();
            }
        });
        addcat2 = (Button) findViewById(R.id.addcat2);
        addcat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addcategory();
            }
        });
        showcat2 = (Button) findViewById(R.id.showcat2);
        showcat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcategory();
            }
        });
        addlt2 = (Button) findViewById(R.id.addlt2);
        addlt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addlimit();            }
        });
    }

    private void getLoginDetails() {
        loginRef = db.collection("login").document("username");
        loginRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            userToUse = documentSnapshot.getString("Username");
//                            Toast.makeText(settings2.this, "User :"+userToUse, Toast.LENGTH_SHORT).show();
                            createCatRef();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(settings2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createCatRef() {
        catRef = db.collection(userToUse+" Categories").document(et_catName.getText().toString());

        Map<String, Object> addCat = new HashMap<>();
        addCat.put(KEY_CAT,et_catName.getText().toString());
        addCat.put(KEY_AMT,0);
        catRef.set(addCat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(settings2.this, "Cat Added", Toast.LENGTH_SHORT).show();
                        String text = "Category added.";
                        mTTS.setPitch(1);
                        mTTS.setSpeechRate(1);
                        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        et_catName.setText(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void openhome() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
    public void addcategory() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
    public void showcategory() {
        Intent intent = new Intent(this, settings3.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
    public void addlimit() {
        Intent intent = new Intent(this, settings4.class);
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

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
}
