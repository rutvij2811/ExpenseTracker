package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class message extends AppCompatActivity {
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private String userToUse;
    private Button msg_add,msg_back;
    private EditText msgfield;
    private TextToSpeech mTTS;
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_AMT = "amt";
    private static final String KEY_DATE = "date";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference recordRef;
    private static final String TAG = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mTTS =new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTS.setLanguage(Locale.ENGLISH);
            }
        });

        msg_add=findViewById(R.id.msg_add);
        msg_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addmsg();
            }
        });
        msg_back=findViewById(R.id.msg_back);
        msg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgback();
            }
        });

        msgfield=findViewById(R.id.msgfield);
        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);

        userToUse = sp.getString("username", "");

    }
    public void addmsg() {
        String message = msgfield.getText().toString();
        //for atm/pos
        String on = " on ";  // We expect these two pieces of text to be embedded.
        String pos = " POS txn";
        String atm = " ATM txn";
        //for paytm
        String  paytm = "PAYTM";
        String rs = "Rs ";
        String tran = " transferred";
        String end = ".";
        //for tez
        String tez ="Google Pay";
        String pe="PhonePe";

        String ama="Amazon";
        String flip="Flipkart";
        String myn="Myntra";
        String snap="SnapDeal";
        String has=" has";

        String zo="Zomato";
        String ub="UberEats";
        String sw="Swiggy";
        String fo="FoodPanda";

        String in="INOX";
        String im="IMAX";
        String pv="PVR";

        if(msgfield.getText().toString().isEmpty()){
//            Toast.makeText(message.this, "Please enter a text message.", Toast.LENGTH_SHORT).show();
            String text = "Please enter a text message.";
            mTTS.setPitch(1);
            mTTS.setSpeechRate(1);
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
        else{
// Verify that our expected pieces of text are present.
        if ((!(message.contains(on) && message.contains(pos))) && (!(message.contains(on) && message.contains(atm)))
                && (!(message.contains(on) && message.contains(end))) && (!(message.contains(rs) && message.contains(tran)))) {
//            Toast.makeText(message.this, "Enter a valid message.", Toast.LENGTH_SHORT).show();
            String text = "Enter a valid message.";
            mTTS.setPitch(1);
            mTTS.setSpeechRate(1);
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            return;
        }
        if (message.contains(rs) && message.contains(on) && message.contains(pos)) {
            int indexRs = message.indexOf(rs);
            int indexOn = message.indexOf(on);
            int indexPOS = message.indexOf(pos);

            String extracted_date = message.substring(indexOn + on.length(), indexPOS);
            Date date = getDateFromString(extracted_date);
            String extracted_amt = message.substring(indexRs + rs.length(), indexOn);
            int amount = Integer.parseInt(extracted_amt);


//            Toast.makeText(message.this, "POS transaction.", Toast.LENGTH_SHORT).show();
            String text = "POS transaction.";
            mTTS.setPitch(1);
            mTTS.setSpeechRate(1);
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

            recordRef = db.collection(userToUse + " Record").document();
            Map<String, Object> insertRecord = new HashMap<>();
            insertRecord.put(KEY_CATEGORY, "POS transaction");
            insertRecord.put(KEY_AMT, amount);
            insertRecord.put(KEY_DATE, date);

                recordRef
                        .set(insertRecord)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(message.this, "Record added", Toast.LENGTH_SHORT).show();
                                String text = "Record added.";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                msgfield.setText(null);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        });


        }
        if (message.contains(rs) && message.contains(on) && message.contains(atm)) {
            int indexRs = message.indexOf(rs);
            int indexOn = message.indexOf(on);
            int indexATM = message.indexOf(atm);

            String extracted_date = message.substring(indexOn + on.length(), indexATM);
            Date date = getDateFromString(extracted_date);
            String extracted_amt = message.substring(indexRs + rs.length(), indexOn);
            int amount = Integer.parseInt(extracted_amt);


//            Toast.makeText(message.this, "ATM transaction", Toast.LENGTH_SHORT).show();
            String text = "ATM transaction.";
            mTTS.setPitch(1);
            mTTS.setSpeechRate(1);
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

            recordRef = db.collection(userToUse + " Record").document();
            Map<String, Object> insertRecord = new HashMap<>();
            insertRecord.put(KEY_CATEGORY, "ATM transaction");
            insertRecord.put(KEY_AMT, amount);
            insertRecord.put(KEY_DATE, date);

            recordRef
                    .set(insertRecord)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(message.this, "Record added", Toast.LENGTH_SHORT).show();
                            String text = "Record added.";
                            mTTS.setPitch(1);
                            mTTS.setSpeechRate(1);
                            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                            msgfield.setText(null);
                            msgfield.setText(null);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    });

        }

            if (message.contains(rs) && message.contains(tran) && message.contains(on) && message.contains(end) && message.contains(tez)) {
                int indexRs = message.indexOf(rs);
                int indexTran = message.indexOf(tran);
                int indexAt = message.indexOf(on);
                int indexEnd = message.indexOf(end);

                String extracted_date = message.substring(indexAt + on.length(), indexEnd);
                Date date = getDateFromString(extracted_date);
                String extracted_amt = message.substring(indexRs + rs.length(), indexTran);
                int amount = Integer.parseInt(extracted_amt);

//                Toast.makeText(message.this, "Shop name: Google Pay", Toast.LENGTH_SHORT).show();
                String text = "Shop name: Google Pay";
                mTTS.setPitch(1);
                mTTS.setSpeechRate(1);
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                recordRef = db.collection(userToUse + " Record").document();
                Map<String, Object> insertRecord = new HashMap<>();
                insertRecord.put(KEY_CATEGORY, "GooglePay");
                insertRecord.put(KEY_AMT, amount);
                insertRecord.put(KEY_DATE, date);

                recordRef
                        .set(insertRecord)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(message.this, "Record added", Toast.LENGTH_SHORT).show();
                                String text = "Record added.";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                msgfield.setText(null);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        });

            }
            if (message.contains(rs) && message.contains(tran) && message.contains(on) && message.contains(end) && message.contains(pe)) {
                int indexRs = message.indexOf(rs);
                int indexTran = message.indexOf(tran);
                int indexAt = message.indexOf(on);
                int indexEnd = message.indexOf(end);

                String extracted_date = message.substring(indexAt + on.length(), indexEnd);
                Date date = getDateFromString(extracted_date);
                String extracted_amt = message.substring(indexRs + rs.length(), indexTran);
                int amount = Integer.parseInt(extracted_amt);

//                Toast.makeText(message.this, "Shop name: PhonePe", Toast.LENGTH_SHORT).show();
                String text = "Shop name: PhonePe";
                mTTS.setPitch(1);
                mTTS.setSpeechRate(1);
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                recordRef = db.collection(userToUse + " Record").document();
                Map<String, Object> insertRecord = new HashMap<>();
                insertRecord.put(KEY_CATEGORY, "PhonePe");
                insertRecord.put(KEY_AMT, amount);
                insertRecord.put(KEY_DATE, date);

                recordRef
                        .set(insertRecord)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(message.this, "Record added", Toast.LENGTH_SHORT).show();
                                String text = "Record added.";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                msgfield.setText(null);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        });

            }
            if (message.contains(rs) && message.contains(tran) && message.contains(on) && message.contains(end) && message.contains(pe)) {
                int indexRs = message.indexOf(rs);
                int indexTran = message.indexOf(tran);
                int indexAt = message.indexOf(on);
                int indexEnd = message.indexOf(end);

                String extracted_date = message.substring(indexAt + on.length(), indexEnd);
                Date date = getDateFromString(extracted_date);
                String extracted_amt = message.substring(indexRs + rs.length(), indexTran);
                int amount = Integer.parseInt(extracted_amt);

//                Toast.makeText(message.this, "Shop name: PhonePe", Toast.LENGTH_SHORT).show();
                String text = "Shop name: PhonePe";
                mTTS.setPitch(1);
                mTTS.setSpeechRate(1);
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                recordRef = db.collection(userToUse + " Record").document();
                Map<String, Object> insertRecord = new HashMap<>();
                insertRecord.put(KEY_CATEGORY, "PhonePe");
                insertRecord.put(KEY_AMT, amount);
                insertRecord.put(KEY_DATE, date);

                recordRef
                        .set(insertRecord)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(message.this, "Record added", Toast.LENGTH_SHORT).show();
                                String text = "Record added.";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                msgfield.setText(null);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        });

            }
            if (message.contains(rs) && message.contains(has) && message.contains(on) && message.contains(end) && (message.contains(flip)
                    || message.contains(ama)|| message.contains(myn)|| message.contains(snap))) {
                int indexRs = message.indexOf(rs);
                int indexhas = message.indexOf(has);
                int indexAt = message.indexOf(on);
                int indexEnd = message.indexOf(end);

                String extracted_date = message.substring(indexAt + on.length(), indexEnd);
                Date date = getDateFromString(extracted_date);
                String extracted_amt = message.substring(indexRs + rs.length(), indexhas);
                int amount = Integer.parseInt(extracted_amt);

//            Toast.makeText(message.this, "Shop name: PAYTM", Toast.LENGTH_SHORT).show();
                String text="Other";
                if(message.contains("Amazon")){ text = "Shop name: Amazon";Toast.makeText(message.this, "Shop name: amazon", Toast.LENGTH_SHORT).show();}
                if(message.contains("Flipkart")){ text = "Shop name: Flipkart";Toast.makeText(message.this, "Shop name: flipkart", Toast.LENGTH_SHORT).show();}
                if(message.contains("SnapDeal")){ text = "Shop name: Snap deal";Toast.makeText(message.this, "Shop name: snapdeal", Toast.LENGTH_SHORT).show();}
                if(message.contains("Myntra")){ text = "Shop name: Myntra";Toast.makeText(message.this, "Shop name: myntra", Toast.LENGTH_SHORT).show();}
                mTTS.setPitch(1);
                mTTS.setSpeechRate(1);
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                msgfield.setText(null);

                recordRef = db.collection(userToUse + " Record").document();
                Map<String, Object> insertRecord = new HashMap<>();
                insertRecord.put(KEY_CATEGORY, "shopping");
                insertRecord.put(KEY_AMT, amount);
                insertRecord.put(KEY_DATE, date);

                recordRef
                        .set(insertRecord)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                            Toast.makeText(message.this, "Record added", Toast.LENGTH_SHORT).show();
                                String text = "Record added to shopping.";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                msgfield.setText(null);
                                msgfield.setText(null);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        });

            }
            if (message.contains(rs) && message.contains(has) && message.contains(on) && message.contains(end) && (message.contains(ub)
                    || message.contains(zo)|| message.contains(sw)|| message.contains(fo))) {
                int indexRs = message.indexOf(rs);
                int indexhas = message.indexOf(has);
                int indexAt = message.indexOf(on);
                int indexEnd = message.indexOf(end);

                String extracted_date = message.substring(indexAt + on.length(), indexEnd);
                Date date = getDateFromString(extracted_date);
                String extracted_amt = message.substring(indexRs + rs.length(), indexhas);
                int amount = Integer.parseInt(extracted_amt);

//            Toast.makeText(message.this, "Shop name: PAYTM", Toast.LENGTH_SHORT).show();
                String text="Other";
                if(message.contains(ub)){
                    text = "Shop name: uber eats";
                    Toast.makeText(message.this, "Shop name: uber eats", Toast.LENGTH_SHORT).show();
                }
                if(message.contains(zo)){
                    text = "Shop name: zomato";
                    Toast.makeText(message.this, "Shop name: zomato", Toast.LENGTH_SHORT).show();
                }
                if(message.contains(sw)){
                    text = "Shop name: swiggy";
                    Toast.makeText(message.this, "Shop name: swiggy", Toast.LENGTH_SHORT).show();
                }
                if(message.contains(fo)){
                    text = "Shop name: food panda";
                    Toast.makeText(message.this, "Shop name: food panda", Toast.LENGTH_SHORT).show();
                }

                recordRef = db.collection(userToUse + " Record").document();
                Map<String, Object> insertRecord = new HashMap<>();
                insertRecord.put(KEY_CATEGORY, "food");
                insertRecord.put(KEY_AMT, amount);
                insertRecord.put(KEY_DATE, date);

                recordRef
                        .set(insertRecord)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                            Toast.makeText(message.this, "Record added", Toast.LENGTH_SHORT).show();
                                String text = "Record added to food.";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                msgfield.setText(null);
                                msgfield.setText(null);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        });

            }
            if (message.contains(rs) && message.contains(has) && message.contains(on) && message.contains(end) && (message.contains(in)
                    || message.contains(im)|| message.contains(pv))) {
                int indexRs = message.indexOf(rs);
                int indexhas = message.indexOf(has);
                int indexAt = message.indexOf(on);
                int indexEnd = message.indexOf(end);

                String extracted_date = message.substring(indexAt + on.length(), indexEnd);
                Date date = getDateFromString(extracted_date);
                String extracted_amt = message.substring(indexRs + rs.length(), indexhas);
                int amount = Integer.parseInt(extracted_amt);

//            Toast.makeText(message.this, "Shop name: PAYTM", Toast.LENGTH_SHORT).show();
                String text;
                if(message.contains(in)){
                    text = "Shop name: INOX";
                    Toast.makeText(message.this, "Shop name: INOX", Toast.LENGTH_SHORT).show();
                }
                if(message.contains(im)){
                    text = "Shop name: IMAX";
                    Toast.makeText(message.this, "Shop name: IMAX", Toast.LENGTH_SHORT).show();
                }
                if(message.contains(pv)){
                    text = "Shop name: PVR";
                    Toast.makeText(message.this, "Shop name: P V R", Toast.LENGTH_SHORT).show();
                }
                recordRef = db.collection(userToUse + " Record").document();
                Map<String, Object> insertRecord = new HashMap<>();
                insertRecord.put(KEY_CATEGORY, "movies");
                insertRecord.put(KEY_AMT, amount);
                insertRecord.put(KEY_DATE, date);

                recordRef
                        .set(insertRecord)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                            Toast.makeText(message.this, "Record added", Toast.LENGTH_SHORT).show();
                                String text = "Record added to movies.";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                msgfield.setText(null);
                                msgfield.setText(null);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        });

            }
            else {

                if (message.contains(rs) && message.contains(has) && message.contains(on) && message.contains(end)) {
                    int indexRs = message.indexOf(rs);
                    int indexhas = message.indexOf(has);
                    int indexAt = message.indexOf(on);
                    int indexEnd = message.indexOf(end);

                    String extracted_date = message.substring(indexAt + on.length(), indexEnd);
                    Date date = getDateFromString(extracted_date);
                    String extracted_amt = message.substring(indexRs + rs.length(), indexhas);
                    int amount = Integer.parseInt(extracted_amt);

//            Toast.makeText(message.this, "Shop name: PAYTM", Toast.LENGTH_SHORT).show();

                    recordRef = db.collection(userToUse + " Record").document();
                    Map<String, Object> insertRecord = new HashMap<>();
                    insertRecord.put(KEY_CATEGORY, "other");
                    insertRecord.put(KEY_AMT, amount);
                    insertRecord.put(KEY_DATE, date);

                    recordRef
                            .set(insertRecord)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(message.this, "category has been set to other", Toast.LENGTH_SHORT).show();

                                    String text = "Record added to other category. You can change the category by clicking transfer button in analysis page";

                                    mTTS.setPitch(1);
                                    mTTS.setSpeechRate(1);
                                    mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                    msgfield.setText(null);
                                    msgfield.setText(null);
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
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void msgback() {
        Intent intent = new Intent(this, home.class);
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
    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
}
