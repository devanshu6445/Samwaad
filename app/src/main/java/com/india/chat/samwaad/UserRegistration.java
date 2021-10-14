package com.india.chat.samwaad;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.india.chat.samwaad.Model.User;
import com.india.chat.samwaad.ui.home.HomeFragment;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRegistration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    TextView user_login_d;
    EditText user_email;
    EditText user_mobile_number;
    EditText user_name;
    Button user_register;
    EditText user_password;
    FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        spinner = findViewById(R.id.spinner1);
        user_login_d = findViewById(R.id.user_login_d1);
        user_email = findViewById(R.id.user_registration_email1);
        user_name = findViewById(R.id.user_registration_name1);
        user_mobile_number = findViewById(R.id.user_registration_mobile_number1);
        user_register = findViewById(R.id.user_registration_processor1);
        user_password = findViewById(R.id.user_registration_password1);


        Objects.requireNonNull(getSupportActionBar()).hide();
        user_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth = FirebaseAuth.getInstance();

                String email = user_email.getText().toString();
                String password = user_password.getText().toString();
                final String name = user_name.getText().toString();
                String phone = user_mobile_number.getText().toString();
                if(email.isEmpty() && password.isEmpty() && name.isEmpty() && phone.isEmpty()){
                    user_email.setError("Email address");
                    user_email.requestFocus();
                    user_password.setError("Enter Password");
                    user_password.requestFocus();
                    user_name.setError("Enter Name");
                    user_mobile_number.setError("Please enter valid number");
                    user_mobile_number.requestFocus();
                }else if(name.isEmpty()){
                    user_name.setError("Enter Name");
                    user_name.requestFocus();
                } else if(phone.length() != 10){
                    user_mobile_number.setError("Please enter valid number");
                    user_mobile_number.requestFocus();
                } else if (email.isEmpty()){
                    user_email.setError("Email address");
                    user_email.requestFocus();
                }else  if(password.isEmpty()){
                    user_password.setError("Enter Password");
                    user_password.requestFocus();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                User information = new User(user.getUid(), name, null, "online");
                                databaseReference.child(user.getUid()).setValue(information);
                                Intent intent = new Intent(UserRegistration.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(UserRegistration.this, "Registration done correctly", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(UserRegistration.this, "An unknown error has occured", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }

            }
        });
        user_login_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserRegistration.this, login_to_samwaad.class));
            }
        });

        List<String> gender = new ArrayList<>();
        gender.add("Male");
        gender.add("Female");
        gender.add("Undefined");

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gender);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter1);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
