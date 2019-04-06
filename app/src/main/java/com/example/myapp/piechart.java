package com.example.myapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class piechart extends AppCompatActivity {
    Button chartback;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference loginRef = db.collection("login").document("username");
    String userToUse;
    private CollectionReference recordRef;
    List<PieEntry> pieEntries=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);
        chartback = (Button) findViewById(R.id.chartback);
        chartback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showana();
            }
        });





    }
    private void setupPieChart() {
        recordRef = db.collection(userToUse +" Record");
        Toast.makeText(piechart.this,  userToUse, Toast.LENGTH_SHORT).show();

        recordRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()) {
                                String catName = documentSnapshot.getString("category");
                                String catAmt = documentSnapshot.get("amt").toString();
                                //recnames= recnames+","+catName;
                                //recamt=recamt+","+catAmt;
                                //int amt = Integer.parseInt(catAmt);
                                pieEntries.add(new PieEntry(Float.valueOf(catAmt),catName));
                                Toast.makeText(piechart.this, catAmt+ "", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(piechart.this, "category not found", Toast.LENGTH_SHORT).show();
                            }

                        }
                        /*String[] name=recnames.split(",");
                        String[] splitamt=recamt.split(",");
                        for(int i=0;i<splitamt.length;i++){
                            amt[i]=Integer.parseInt(splitamt[i]);
                        }*/
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        PieDataSet dataSet=new PieDataSet(pieEntries,"Expenditure");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData data=new PieData(dataSet);

        PieChart chart=(PieChart) findViewById(R.id.chart);
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();



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
                            setupPieChart();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(piechart.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public void showana(){
        Intent intent = new Intent(this, analysis.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void onBackPressed(){
        Intent intent = new Intent(this, analysis.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
/*for(int i=0;i<amt.length;i++){
            pieEntries.add(new PieEntry(amt[i],name[i]));
        }
        PieDataSet dataSet=new PieDataSet(pieEntries,"Expenditure");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData data=new PieData(dataSet);

        PieChart chart=(PieChart) findViewById(R.id.chart);
        chart.setData(data);
        chart.invalidate();*/