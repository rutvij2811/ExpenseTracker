package com.example.myapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class analysis extends AppCompatActivity {
    private Button ana_back,analyze;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordRef;
    String userToUse;

    TextView showrecord,ana_cat;
    TextView et_date1,et_date2;
    DatePickerDialog.OnDateSetListener mDateSetListner1,mDateSetListner2;
    private DocumentReference loginRef = db.collection("login").document("username");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        ana_back = (Button) findViewById(R.id.ana_back);
        ana_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhome();
            }
        });

        et_date1 = (TextView) findViewById(R.id.et_date1);
        et_date2 = (TextView) findViewById(R.id.et_date2);
        ana_cat = (TextView) findViewById(R.id.ana_cat);

        /*Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/

        analyze = (Button) findViewById(R.id.analyze);
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyrec();
            }
        });
        showrecord = findViewById(R.id.tv_ana);

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
                et_date1.setText(dob);

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
                et_date2.setText(dob);

            }
        };

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
                        Toast.makeText(analysis.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void openhome() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void onBackPressed(){
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    private void showMyrec() {

        recordRef = db.collection(userToUse +" Record");
        recordRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int flag=0;
                        String showrec = "";
                        Date date1 = getDateFromString(et_date1.getText().toString());
                        Date date2 = getDateFromString(et_date1.getText().toString());
                        //Timestamp date_1= new date1.getTime();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()) {
                                String catName = documentSnapshot.getString("category");
                                String catAmt = documentSnapshot.get("amt").toString();
                                Timestamp catdate = documentSnapshot.getTimestamp("date");

                                //Date d = new Date(timestamp );
                                if (catName.equals(ana_cat.getText().toString())){
                                    showrec += catName + " : " + catAmt + " on " + catdate + "\n\n";
                                    flag=1;
                                }
                            }else{
                                Toast.makeText(analysis.this, "category not found", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if(flag==1){
                            showrecord.setText(showrec);}
                        if(flag==0){
                            showrecord.setText("Category Not found");
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
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