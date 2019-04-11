package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.opencensus.tags.Tag;


public class MainActivity extends AppCompatActivity {

    private Button login, signup;
    private TextToSpeech mTTS;
    private TextView forgotPass;
    private EditText username,password;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef,loginRef;
    boolean doubleBackToExitPressedOnce = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTTS =new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTS.setLanguage(Locale.ENGLISH);
            }
        });

        username = (EditText)findViewById(R.id.main_username);
        password = (EditText)findViewById(R.id.main_pass);
        forgotPass = findViewById(R.id.forgotPass);
        login = (Button) findViewById(R.id.log);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhome();
            }
        });
        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opensignup();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoForgotPass();
            }
        });

    }

    private void gotoForgotPass() {
            startActivity(new Intent(MainActivity.this, ForgotPassword.class));
    }

    public void openhome() {

        userRef = db.collection("ExpenseTracker").document("user "+username.getText().toString());

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String usernamedb = documentSnapshot.getString("username");
                            String passworddb = documentSnapshot.getString("password");

                            String etuser = username.getText().toString();
                            String etpass = password.getText().toString();
                            if(etuser.equals(usernamedb) && etpass.equals(passworddb)){
//                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                String text = "Success";
                                mTTS.setPitch(1);
                                mTTS.setSpeechRate(1);
                                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                loginRef = db.collection("login").document("username");
                                Map<String, Object> logUser = new HashMap<>();
                                logUser.put("Username",etuser);
                                loginRef.set(logUser);
                                SharedPreferences sp = getSharedPreferences("user",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("username",etuser);
                                editor.apply();
                                gotoHome();
                            }
                        }else{
//                            Toast.makeText(MainActivity.this, "Please enter valid username or password", Toast.LENGTH_SHORT).show();
                            String text = "Please enter valid username or password.";
                            mTTS.setPitch(1);
                            mTTS.setSpeechRate(1);
                            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,e.toString());
                    }
                });

    }

    private void gotoHome() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }

    public void opensignup() {
        Intent intent = new Intent(this, signup.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            System.exit(0);
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
