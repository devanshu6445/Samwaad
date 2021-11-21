package com.india.chat.samwaad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.india.chat.samwaad.Model.User;

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
    ImageView user_regImage;
    private static  final int REQUEST_IMAGE = 2;
    Uri uriRegImage;
    TextView regtv;


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
        user_regImage = findViewById(R.id.user_regImage);
        regtv = findViewById(R.id.regtv);



        Objects.requireNonNull(getSupportActionBar()).hide();
        user_regImage.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE);
        });
        user_register.setOnClickListener(v -> {

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
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user!=null){
                            StorageReference reference = FirebaseStorage.getInstance().getReference().child(user.getUid())
                                    .child(uriRegImage.getLastPathSegment());
                            putRegImage(reference,uriRegImage,user,name,user_mobile_number.getText().toString());
                        }
                    } else {
                        Toast.makeText(UserRegistration.this, "An unknown error has occured", Toast.LENGTH_LONG).show();
                    }
                });

            }

        });
        user_login_d.setOnClickListener(v -> startActivity(new Intent(UserRegistration.this, login_to_samwaad.class)));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE){
            if (resultCode == RESULT_OK){
                if (data!=null){
                    uriRegImage = data.getData();
                    Log.d("URIREG",uriRegImage.toString());
                }
            }
        }
    }
    private void profileSet(String name, String uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(uri))
                    .build();
            user.updateProfile(profileChangeRequest);
        }

    }
    String imageURL;
    private void putRegImage(StorageReference storageReference, Uri uri,FirebaseUser user,String name,String number){

        storageReference.putFile(uri).addOnCompleteListener(UserRegistration.this,
                task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult()!=null){
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(UserRegistration.this,
                                            task1 -> {
                                                if (task1.isSuccessful()) {
                                                    imageURL = task1.getResult().toString();
                                                    Log.d("ImageUrrl",imageURL, task1.getException());
                                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString("uid",user.getUid());
                                                    editor.putString("image_url",imageURL);
                                                    editor.putString("name",name);
                                                    editor.putString("number",number);
                                                    editor.apply();
                                                    FirebaseFirestore user_db = FirebaseFirestore.getInstance();
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                                    profileSet(name,imageURL);
                                                    User information1;
                                                    if (imageURL == null){
                                                        information1 = new User(user.getUid(), name, null, "online",name.toLowerCase());
                                                    } else {
                                                        information1 = new User(user.getUid(), name, imageURL, "online",name.toLowerCase());
                                                    }
                                                    user_db.collection("users").document(user.getUid())
                                                            .set(information1)
                                                            .addOnSuccessListener(unused -> Log.d("firestore_db_success","Success")).addOnFailureListener(e -> Log.d("firestore_db_fail", "failed", e));
                                                    databaseReference.child(user.getUid()).setValue(information1);

                                                    Intent intent = new Intent(UserRegistration.this, MainActivity.class);
                                                    startActivity(intent);
                                                    Toast.makeText(UserRegistration.this, "Registration done correctly", Toast.LENGTH_LONG).show();
                                                }
                                            });
                        }
                    } else {
                        Log.w("ImageUpload", "Image upload task was not successful.",
                                task.getException());
                    }
                });
    }
}
