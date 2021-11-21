package com.india.chat.samwaad;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class password_reset extends AppCompatActivity {

    EditText reset_email;
    Button resetPassword;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset);

        reset_email = findViewById(R.id.password_reset);
        resetPassword = findViewById(R.id.resetPassword);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String email = reset_email.getText().toString();
                if (email.isEmpty()){
                    reset_email.setError("Enter email");
                    reset_email.requestFocus();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        View view = findViewById(android.R.id.content);
                                        Snackbar.make(view, "Reset email sent", BaseTransientBottomBar.LENGTH_SHORT).setAction("Action", null).show();
                                    } else {
                                        Toast.makeText(password_reset.this, "Enter correct email", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }

            }
        });
    }
}
