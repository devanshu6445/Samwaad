package com.india.chat.samwaad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class login_to_samwaad extends AppCompatActivity {

    Toolbar toolbar;

    EditText login_email_1,login_password_1;
    ImageButton signin;
    TextView forget_password;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.login_to_samwaad);
        Objects.requireNonNull(getSupportActionBar()).hide();
        toolbar = findViewById(R.id.toolbar);
        final View view1 = findViewById(R.id.llProgressBar);
        view1.setVisibility(View.GONE);
        toolbar.setTitle("Login");
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_to_samwaad.this, UserRegistration.class));
            }
        });
        forget_password = findViewById(R.id.forget_password);
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_to_samwaad.this, password_reset.class));
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        login_email_1 = findViewById(R.id.email_1);
        login_password_1 = findViewById(R.id.password_1);
        signin = findViewById(R.id.login_user);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
                            .addOnCompleteListener(login_to_samwaad.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                view1.setVisibility(View.GONE);
                                Log.d("Working", "Working");
                                Toast.makeText(login_to_samwaad.this, "Done", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login_to_samwaad.this, MainActivity.class));
                            } else {

                                AlertDialog.Builder alert = new AlertDialog.Builder(login_to_samwaad.this).
                                        setMessage("Please enter correct details")
                                        .setTitle("Invalid Details").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alert.create();
                                alertDialog.show();
                                view1.setVisibility(View.GONE);
                                Log.d("Login Failed", "Login Failed");
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            startActivity(new Intent(login_to_samwaad.this, MainActivity.class));
        }
    }
}