package com.example.myapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;


public class analysis extends AppCompatActivity {
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    String userToUse;
    TextView showrecord;
    TextView et_date1, et_date2;
    EditText ana_cat;
    private TextToSpeech mTTS;
    DatePickerDialog.OnDateSetListener mDateSetListner1, mDateSetListner2;
    private Button ana_back, analyze,piebtn,change;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordRef;
    private String sample = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        mTTS =new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTS.setLanguage(Locale.ENGLISH);
            }
        });

        ana_back = (Button) findViewById(R.id.ana_back);
        ana_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhome();
            }
        });

        piebtn = findViewById(R.id.piechartbtn);
        piebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPieActivity();
            }
        });
        et_date1 = (TextView) findViewById(R.id.et_date1);
        et_date2 = (TextView) findViewById(R.id.et_date2);
        ana_cat = findViewById(R.id.ana_cat);
        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);

        userToUse = sp.getString("username", "");
        recordRef = db.collection(userToUse + " Record");


//        Toast.makeText(this, "User :" + userToUse, Toast.LENGTH_SHORT).show();
        et_date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        analysis.this,
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
                    et_date1.setText(finalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //et_date1.setText(dob);

            }
        };
        et_date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        analysis.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog,
                        mDateSetListner2,
                        year, month, day);
                dialog.show();
            }
        });

        mDateSetListner2 = new DatePickerDialog.OnDateSetListener() {
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
                    et_date2.setText(finalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //et_date2.setText(dob);

            }
        };

        /*Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/

        analyze = (Button) findViewById(R.id.analyze);
        showrecord = findViewById(R.id.tv_ana);
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_date1.getText().toString().isEmpty() || et_date2.getText().toString().isEmpty()){
//                    Toast.makeText(analysis.this, "Please set a date range", Toast.LENGTH_SHORT).show();
                    String text = "Please set a Date range.";
                    mTTS.setPitch(1);
                    mTTS.setSpeechRate(1);
                    mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
                else{

                    showMyrec();
                }
            }
        });
        change=(Button)findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openupdate();
            }
        });


    }

    private void openupdate() {
        Intent intent = new Intent(this, update.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private void gotoPieActivity() {
        startActivity(new Intent(analysis.this,piechart.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void openhome() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

//    public void onItemSelected(AdapterView<?> parent, View view,
//                               int pos, long id) {
//        // An item was selected. You can retrieve the selected item using
//        // parent.getItemAtPosition(pos)
//    }
//
//    public void onNothingSelected(AdapterView<?> parent) {
//        // Another interface callback
//    }

    private void showMyrec() {
        Date date1 = getDateFromString(et_date1.getText().toString());
//        long fromDate = date1.getTime();
        Date date2 = getDateFromString(et_date2.getText().toString());
//        long toDate = date2.getTime();

        if(TextUtils.isEmpty(ana_cat.getText())){
            recordRef
                    .whereGreaterThanOrEqualTo("date", date1)
                    .whereLessThanOrEqualTo("date", date2)
                    .orderBy("date")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            sample = "";
                            if (queryDocumentSnapshots.isEmpty()) {
//                                Toast.makeText(analysis.this, "No record in the date range", Toast.LENGTH_SHORT).show();
                                String text = "No record in the date range.";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                            }
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String cat = documentSnapshot.getString("category");
                                String amt = documentSnapshot.get("amt").toString();
//                            String date = documentSnapshot.getDate("date").toString();

                                String pattern = "dd-MM-yyyy";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String date = simpleDateFormat.format(documentSnapshot.getDate("date"));

                                sample += amt + " for " + cat + " on " + date + "\n";

                            }
                            showrecord.setText(sample);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("analysis", "Error: " + e.toString());

                        }
                    });
        }else{
            String catName = ana_cat.getText().toString();
            recordRef
                    .whereEqualTo("category", catName)
                    .whereGreaterThanOrEqualTo("date", date1)
                    .whereLessThanOrEqualTo("date", date2)
                    .orderBy("date")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            sample = "";
                            if (queryDocumentSnapshots.isEmpty()) {
//                                Toast.makeText(analysis.this, "No record in the date range", Toast.LENGTH_SHORT).show();
                                String text = "No record in the date range.";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                            }
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String cat = documentSnapshot.getString("category");
                                String amt = documentSnapshot.get("amt").toString();
//                            String date = documentSnapshot.getDate("date").toS tring();

                                String pattern = "dd-MM-yyyy";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String date = simpleDateFormat.format(documentSnapshot.getDate("date"));

                                sample += amt + " for " + cat + " on " + date + "\n";

                            }
                            showrecord.setText(sample);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("analysis", "Error: " + e.toString());

                        }
                    });
        }

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
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
}