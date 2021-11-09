package com.india.chat.samwaad.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.india.chat.samwaad.Adapter.MessageAdapter;
import com.india.chat.samwaad.MainActivity;
import com.india.chat.samwaad.Model.Chat;
import com.india.chat.samwaad.Model.User;


import com.india.chat.samwaad.R;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference,databaseReference;

    ImageButton btn_send;
    EditText text_send;
    TextView status_dynamic;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Intent intent;

    ImageButton btn_cam;

    private static  final int REQUEST_IMAGE = 2;

    ValueEventListener seenListener,SeenListener1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v ->
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        );
        Window window = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.setStatusBarColor(getResources().getColor(R.color.emerald));
        }

        intent = getIntent();
        final String userid = intent.getStringExtra("user_id");
        recyclerView = findViewById(R.id.recycler_view1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile_image = findViewById(R.id.profile_image1);
        username = findViewById(R.id.username1);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        status_dynamic = findViewById(R.id.status_dynamic);
        btn_cam = findViewById(R.id.btn_cam);

        btn_cam.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE);
        });

        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                status("online");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                status("Typing...");
            }
            private Timer timer = new Timer();

            @Override
            public void afterTextChanged(Editable s) {

                timer = new Timer();
                long DELAY = 2000;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        status("online");
                    }
                },DELAY);
            }
        });

        intent = getIntent();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        assert userid != null;
        reference = FirebaseDatabase.getInstance().getReference("users").child(userid);
        readMessages(fuser.getUid(), userid);
        btn_send.setOnClickListener(v -> {
            String msg = text_send.getText().toString();
            if (!msg.isEmpty()){
                sendMessage(fuser.getUid(), userid, msg);
                text_send.setText("");
            } else {
                Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
        });


        seenMessage(userid);
    }

    private void seenMessage(final String userid){
        String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(myid+"_"+userid);
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats").child(userid+"_"+myid);
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SeenListener1 = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                if (data!=null){
                    final Uri uri = data.getData();
                    Log.d("uri image", "Uri: " + uri.toString());
                    StorageReference reference = FirebaseStorage.getInstance().getReference().child(fuser.getUid())
                            .child(uri.getLastPathSegment());
                    putImageInStorage(reference, uri);
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("upload_status","uploading");
        editor.apply();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        final String userid = intent.getStringExtra("user_id");
        storageReference.putFile(uri).addOnCompleteListener(MessageActivity.this,
                task -> {
                    if (task.isSuccessful()) {
                        task.getResult().getMetadata().getReference().getDownloadUrl()
                                .addOnCompleteListener(MessageActivity.this,
                                        new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    assert task.getResult() != null;
                                                    intent = getIntent();
                                                    final String receiver = intent.getStringExtra("user_id");
                                                    String sender = fuser.getUid();

                                                    Date date = new Date();
                                                    String ts = Long.toString(date.getTime());
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("sender", sender);
                                                    hashMap.put("receiver", receiver);
                                                    hashMap.put("message", null);
                                                    hashMap.put("timestamp", ServerValue.TIMESTAMP);
                                                    hashMap.put("unique_id", ts);
                                                    hashMap.put("imageUrl", task.getResult().toString());
                                                    hashMap.put("isseen", false);

                                                    Log.d("ImageURL", task.getResult().toString(), task.getException());

                                                    reference.child("Chats").child(sender+"_"+receiver).child(ts).setValue(hashMap);
                                                    reference.child("Chats").child(receiver+"_"+sender).child(ts).setValue(hashMap);
                                                    editor.putString("upload_status","uploaded");
                                                    editor.apply();
                                                }
                                            }
                                        });
                    } else {
                        Log.w("ImageUpload", "Image upload task was not successful.",
                                task.getException());
                    }
                });
    }

    private void sendMessage(String sender, final String receiver, final String message){

        Date date = new Date();
        String ts = Long.toString(date.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("unique_id", ts);
        hashMap.put("isseen", false);

        reference.child("Chats").child(sender+"_"+receiver).child(ts).setValue(hashMap);
        reference.child("Chats").child(receiver+"_"+sender).child(ts).setValue(hashMap);

    }

    private void readMessages(final String myid, final String userid){
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(myid+"_"+userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String pref = preferences.getString("upload_status","Nothing");

                    if (chat.getImageUrl()==null){
                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                    } else if (chat.getImageUrl()!=null){
                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat,pref);
                    }

                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void status(String status){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userid = intent.getStringExtra("user_id");
        username = findViewById(R.id.username1);
        status_dynamic = findViewById(R.id.status_dynamic);
        reference = FirebaseDatabase.getInstance().getReference("users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                Log.d("username_11",user.getUsername());
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);

                status_dynamic.setText(user.getStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });Log.d("userid",userid);
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        databaseReference.removeEventListener(SeenListener1);
        status("offline");
    }

}