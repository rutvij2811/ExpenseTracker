package com.example.myapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;

public class update extends AppCompatActivity {
    private Button up_back,transfer,delete;
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    TextView tran_date, del_date;
    TextView from_cat, to_cat,del_cat;
    String cat_from;
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_DATE = "date";
    DatePickerDialog.OnDateSetListener mDateSetListner1, mDateSetListner2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference loginRef = db.collection("login").document("username");
    String userToUse;
    String rec_id,cat_del;
    private CollectionReference recordRef;
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        tran_date = (TextView) findViewById(R.id.tran_date);
        del_date = (TextView) findViewById(R.id.del_date);

        from_cat = (TextView) findViewById(R.id.from_cat);
        to_cat = (TextView) findViewById(R.id.to_cat);
        del_cat = (TextView) findViewById(R.id.del_cat);

        up_back = (Button) findViewById(R.id.up_back);
        up_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openana();
            }
        });

        transfer=(Button) findViewById(R.id.transfer);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cat_tran();
            }
        });

        delete=(Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cat_del();
            }
        });

        tran_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        update.this,
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
                    tran_date.setText(finalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        del_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        update.this,
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
                    del_date.setText(finalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };


        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        userToUse = sp.getString("username", "");
        recordRef = db.collection(userToUse + " Record");
    }
    public void cat_tran(){
        final Date date_tran = getDateFromString(tran_date.getText().toString());
        cat_from = from_cat.getText().toString();
        recordRef.whereEqualTo("date",date_tran)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            if(documentSnapshot.exists()) {

                                if(documentSnapshot.getString("category").equals(cat_from)) {
                                    rec_id = documentSnapshot.getId();
                                    docRef = db.collection(userToUse + " Record").document(rec_id);
                                    Map<String, Object> updateRecord = new HashMap<>();
                                    updateRecord.put(KEY_CATEGORY, to_cat.getText().toString());
                                    docRef.set(updateRecord, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(update.this, "Transferred the record successfully", Toast.LENGTH_SHORT).show();
                                            tran_date.setText(null);
                                            from_cat.setText(null);
                                            to_cat.setText(null);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(update.this, "Record couldn't be transferred ", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }else{
                                Toast.makeText(update.this, "error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    public void cat_del(){
        final Date date_del = getDateFromString(del_date.getText().toString());
        cat_del = del_cat.getText().toString();
        recordRef.whereEqualTo("date",date_del)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            if(documentSnapshot.exists()) {
                                if(documentSnapshot.getString("category").equals(cat_del)){
                                    rec_id=documentSnapshot.getId();
                                    docRef=recordRef.document(rec_id);
                                    docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(update.this, "Deleted the record", Toast.LENGTH_SHORT).show();
                                            del_cat.setText(null);
                                            del_date.setText(null);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(update.this, "Record couldn't be deleted ", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                            }else{
                                Toast.makeText(update.this, "error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    public void onBackPressed() {
        Intent intent = new Intent(this, analysis.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void openana() {
        Intent intent = new Intent(this, analysis.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public Date getDateFromString(String datetoSaved) {

        try {
            Date date = format.parse(datetoSaved);
            return date;
        } catch (ParseException e) {
            return null;
        }

    }
}