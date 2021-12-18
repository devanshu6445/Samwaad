package com.india.chat.samwaad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class login_to_samwaad extends AppCompatActivity {

    Toolbar toolbar;

    EditText login_email_1,login_password_1;
    ImageButton signin;
    TextView forget_password;
    Button signup;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent i = getIntent();
        boolean sign_out = i.getBooleanExtra("signout",false);
        if (sign_out){
            if (user!=null){
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                finishAffinity();
            }
        }else if (user!=null){
            startActivity(new Intent(login_to_samwaad.this, MainActivity.class));
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.login_to_samwaad);

        Objects.requireNonNull(getSupportActionBar()).hide();

        toolbar = findViewById(R.id.toolbar);
        final View view1 = findViewById(R.id.llProgressBar);
        view1.setVisibility(View.GONE);
        toolbar.setTitle("Login");
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(v ->
                startActivity(new Intent(login_to_samwaad.this, UserRegistration.class))
        );
        forget_password = findViewById(R.id.forget_password);
        forget_password.setOnClickListener(v ->
                startActivity(new Intent(login_to_samwaad.this, password_reset.class))
        );
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        login_email_1 = findViewById(R.id.email_1);
        login_password_1 = findViewById(R.id.password_1);
        signin = findViewById(R.id.login_user);
        signin.setOnClickListener(view -> {
            String email = login_email_1.getText().toString();
            String pwd = login_password_1.getText().toString();
            if (email.isEmpty() && pwd.isEmpty()) {
                Toast.makeText(login_to_samwaad.this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                login_email_1.setError("Please enter email Id");
                login_email_1.requestFocus();
            } else if (pwd.isEmpty()) {
                login_password_1.setError("Please enter password");
                login_password_1.requestFocus();
            } else {
                view1.setVisibility(View.VISIBLE);
                Log.d("No Error", "Working");
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                firebaseAuth.signInWithEmailAndPassword(email,pwd)
                        .addOnCompleteListener(login_to_samwaad.this, task -> {
                            if (task.isSuccessful()) {
                                view1.setVisibility(View.GONE);
                                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

                                if (user1!=null) {
                                    DocumentReference reference = FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(user1.getUid());
                                    try {
                                        reference.get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Log.d("LoginDetailsSuccess",task.getResult().toString());
                                                        try {
                                                            DocumentSnapshot snapshot = task1.getResult();
                                                            assert snapshot != null;
                                                            String name = Objects.requireNonNull(snapshot.get("name")).toString();
                                                            Log.d("LoginDetailName",name);
                                                            //If SharedPreferences is not updating because imageURL is generating NullPointerException
                                                            String imageURL = Objects.requireNonNull(snapshot.get("imageURL")).toString();
                                                            //String number = Objects.requireNonNull(snapshot.get("phoneNumber")).toString();
                                                            Toast.makeText(login_to_samwaad.this, name, Toast.LENGTH_SHORT).show();
                                                            //Todo: Correct SharedPreferences code

                                                            editor.putString("uid", user1.getUid());
                                                            editor.putString("image_url", imageURL);
                                                            editor.putString("name", name);
                                                            editor.apply();
                                                            //editor.putString("number", number);
                                                        } catch (NullPointerException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else{
                                                        Log.e("LoginDetailsError",task.getResult().toString(),task.getException());
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.d("ReadFailure",e.getMessage(),e);
                                                })
                                                .addOnCanceledListener(() -> {
                                                    Log.d("ReadCancel","Canceled");
                                                });

                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Toast.makeText(login_to_samwaad.this, "Welcome to samwaad", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login_to_samwaad.this, MainActivity.class));

                            } else {

                                AlertDialog.Builder alert = new AlertDialog.Builder(login_to_samwaad.this).
                                        setMessage("Please enter correct details")
                                        .setTitle("Invalid Details").setPositiveButton("Ok", (dialog, which) ->
                                                dialog.cancel());
                                AlertDialog alertDialog = alert.create();
                                alertDialog.show();
                                view1.setVisibility(View.GONE);
                                Log.d("Login Failed", "Login Failed");
                            }
                        });
            }
        });

    }
}