package com.example.myapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import static android.content.ContentValues.TAG;

public class home extends AppCompatActivity {
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_AMT = "amt";
    private static final String KEY_DATE = "date";
    private static final String TAG = "home";
    boolean doubleBackToExitPressedOnce = false;
    private String userToUse;
    private TextToSpeech mTTS;
    DatePickerDialog.OnDateSetListener mDateSetListner1;
    private Button logout, settings, profile, manlog, analysis, addRec,msg;
    private EditText et_cat, et_amt;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference recordRef;
    private DocumentReference loginRef;
    private TextView et_date;
    private ArrayList<String> categoryList = new ArrayList<>();
    private CollectionReference catListRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoginDetails();
        setContentView(R.layout.activity_home);
        mTTS =new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTS.setLanguage(Locale.ENGLISH);
            }
        });
        et_cat = findViewById(R.id.home_cat);
        //To make sp_cat spinner by getting the list of categories. First work on adding categories.
        et_amt = findViewById(R.id.home_amt);
        et_date = findViewById(R.id.home_date);
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        home.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog,
                        mDateSetListner1,
                        year, month, day);
                dialog.show();
            }
        });
        mDateSetListner1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String dob = dayOfMonth + "/" + month + "/" + year;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/yyyy");
                Date tmpDate = null;
                try {
                    tmpDate = simpleDateFormat.parse(dob);
                    SimpleDateFormat simpleDateFormatNew = new SimpleDateFormat("dd/MM/yyyy");
                    String finalDate = simpleDateFormatNew.format(tmpDate);
                    et_date.setText(finalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //et_date.setText(dob);

            }
        };
        addRec = findViewById(R.id.home_add);
        addRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecord();

            }
        });
        logout = findViewById(R.id.logout);
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
        msg = findViewById(R.id.msg);
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addmsg();
            }
        });
        analysis = findViewById(R.id.analysis);
        analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openanalysis();
            }
        });
        msg=findViewById(R.id.msg);
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmsg();
            }
        });
    }

    private void addRecord() {
        String catName = et_cat.getText().toString();
        if (et_cat.getText().toString().isEmpty() || et_amt.getText().toString().isEmpty() || et_date.getText().toString().isEmpty()) {
//            Toast.makeText(this, "Make sure none of the fields are empty", Toast.LENGTH_SHORT).show();
            String text = "Make sure none of the fields are empty.";
            mTTS.setPitch(1);
            mTTS.setSpeechRate(1);
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            recordRef = db.collection(userToUse + " Record").document();
            Map<String, Object> insertRecord = new HashMap<>();
            insertRecord.put(KEY_CATEGORY, et_cat.getText().toString());
            insertRecord.put(KEY_AMT, Integer.parseInt(et_amt.getText().toString()));
            insertRecord.put(KEY_DATE, getDateFromString(et_date.getText().toString()));
            if (!categoryList.contains(catName)) {
//                Toast.makeText(this, "Category not in the database, please add it first.", Toast.LENGTH_SHORT).show();
                String text = "Category not in the database, please add it first.";
                mTTS.setPitch(1);
                mTTS.setSpeechRate(1);
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            } else {
                recordRef
                        .set(insertRecord)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(home.this, "Record added", Toast.LENGTH_SHORT).show();
                                String text = "Record added";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
    }

    private void catNameList() {
        categoryList.clear();
        catListRef = db.collection(userToUse + " Categories");
        catListRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            categoryList.add(documentSnapshot.getId());
                        }
                    }
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
                            SharedPreferences userNamePref = getSharedPreferences("username",MODE_PRIVATE);
                            SharedPreferences.Editor editor = userNamePref.edit();
                            editor.putString("userName",userToUse);
                            editor.apply();
                            //Toast.makeText(home.this, "User :"+userToUse, Toast.LENGTH_SHORT).show();
                            catNameList();
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

    public void opensettings() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void openmsg() {
        Intent intent = new Intent(this, message.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void profilemanage() {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void loginmanage() {
        Intent intent = new Intent(this, manlog.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void addmsg() {
        Intent intent = new Intent(this, message.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void openanalysis() {
        Intent intent = new Intent(this, analysis.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public Date getDateFromString(String datetoSaved) {

        try {
            Date date = format.parse(datetoSaved);
            return date;
        } catch (ParseException e) {
            return null;
        }

    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
