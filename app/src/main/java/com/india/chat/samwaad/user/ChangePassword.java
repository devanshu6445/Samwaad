package com.india.chat.samwaad.user;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.india.chat.samwaad.R;

public class ChangePassword extends AppCompatActivity {

    EditText OldPassword;
    EditText NewPassword;
    EditText ConfirmPass;
    Button changePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        OldPassword = findViewById(R.id.OldPassword);
        NewPassword = findViewById(R.id.NewPassword);
        ConfirmPass = findViewById(R.id.ConfirmPassword);
        final View ViewProgressbar = findViewById(R.id.progress_Bar1);
        ViewProgressbar.setVisibility(View.GONE);
        changePassword = findViewById(R.id.ChangePass);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewProgressbar.setVisibility(View.VISIBLE);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                String email = user.getEmail();
                final String password = OldPassword.getText().toString();
                if(password.isEmpty()){
                    ViewProgressbar.setVisibility(View.GONE);
                    OldPassword.requestFocus();
                    OldPassword.setError("Please your old passowrd");
                } else {
                assert email != null;
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            String newpass = NewPassword.getText().toString();
                            String confirmpass = ConfirmPass.getText().toString();
                            if (newpass.isEmpty() && confirmpass.isEmpty()){
                                ViewProgressbar.setVisibility(View.GONE);
                                NewPassword.setError("Please enter your new password");
                                ConfirmPass.setError("Please confirm your new password");
                            } else if(newpass.isEmpty()){
                                ViewProgressbar.setVisibility(View.GONE);
                                NewPassword.requestFocus();
                                NewPassword.setError("Please enter your new password");
                            } else if(confirmpass.isEmpty()){
                                ViewProgressbar.setVisibility(View.GONE);
                                ConfirmPass.requestFocus();
                                ConfirmPass.setError("Please confirm your password");
                            }else if (!newpass.equals(confirmpass)){
                                ViewProgressbar.setVisibility(View.GONE);
                                Toast.makeText(ChangePassword.this, "Your NEW PASSWORD is not same as CONFIRM PASSWORD", Toast.LENGTH_SHORT).show();
                            }else if(newpass.equals(confirmpass)){
                                user.updatePassword(confirmpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ViewProgressbar.setVisibility(View.GONE);
                                        Toast.makeText(ChangePassword.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                ViewProgressbar.setVisibility(View.GONE);
                                Toast.makeText(ChangePassword.this, "An unknown error has occured", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            ViewProgressbar.setVisibility(View.GONE);
                            Toast.makeText(ChangePassword.this, "Please enter your old password correctly", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                }
            }
        });

    }
}
