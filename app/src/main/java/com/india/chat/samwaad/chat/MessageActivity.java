package com.india.chat.samwaad.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {

    private static final int REQUEST_AUDIO = 4;
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
    ImageButton btn_record;

    private static  final int REQUEST_IMAGE = 2;

    ValueEventListener seenListener,SeenListener1;


    @SuppressLint({"ClickableViewAccessibility", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v ->
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        );
        Window window = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.setStatusBarColor(getResources().getColor(R.color.emerald));
        }
        final MediaRecorder recorder = new MediaRecorder();

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
        btn_record = findViewById(R.id.btn_record);
        if (ContextCompat.checkSelfPermission(MessageActivity.this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){
            String audio_path = "/storage/emulated/0/Samwaad/Audio/" + "Audio_" + System.currentTimeMillis() + ".mp4";
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(audio_path);
            try {
                recorder.prepare();

                btn_record.setOnTouchListener((view, motionEvent) -> {

                    switch (motionEvent.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "Hold to record audio", Toast.LENGTH_SHORT).show();
                                recorder.start();
                            } else {
                                ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 5);
                            }

//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("*/*");
//            startActivityForResult(intent,REQUEST_AUDIO);
                            return false;
                        case MotionEvent.ACTION_UP:
                            recorder.stop();
                            recorder.reset();
                            recorder.release();
                            Uri uri = Uri.fromFile(new File(audio_path));
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build());
                            try {
                                mediaPlayer.setDataSource(getApplicationContext(), uri);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (IOException e) {
                                Log.e("MediaPlayIO", "Error IO", e);
                            }
                            return false;
                    }
                    return false;
                });
            } catch (IOException e) {
                Log.e("ErrorIO", "IO Error Occurred", e);
            } catch (IllegalStateException illegalStateException) {
                Log.e("IllegalState", "ErrorState", illegalStateException);
            }
        } else{
            ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 5);

        }
        btn_cam = findViewById(R.id.btn_cam);
        if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MessageActivity.this,new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }

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

            @Override
            public void afterTextChanged(Editable s) {

                Timer timer = new Timer();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //checking which permission is granted
        if (requestCode==2 || requestCode==5){
            Toast.makeText(MessageActivity.this, "Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void seenMessage(final String userid){
        String myid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(myid+"_"+userid);
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats").child(userid+"_"+myid);
        //updating message isseen field in database to true for (sender_id + receiver_id) node
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
        //updating message isseen field in database to true for (receiver_id + sender_id) node
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
                Log.e("seenError",error.getDetails(),error.toException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                //getting data from uri is data is not null
                if (data!=null){
                    final Uri uri = data.getData();
                    Log.d("uri image", "Uri: " + uri.toString());
                    StorageReference reference = FirebaseStorage.getInstance().getReference().child(fuser.getUid())
                            .child(fuser.getUid()+"_"+System.currentTimeMillis());
                    //uploading image to storage
                    putImageInStorage(reference, uri);
                }
            }
        } else if (requestCode==REQUEST_AUDIO){
            if (resultCode==RESULT_OK){
                if (data!=null){
                    Uri uri = data.getData();
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build());
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(),uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void putImageInStorage(@NonNull StorageReference storageReference, Uri uri) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        //uploading image to firebase storage using uri
        storageReference.putFile(uri).addOnCompleteListener(MessageActivity.this,
                task -> {
                    if (task.isSuccessful()) {
                        //getting download url from upload task
                        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getMetadata()).getReference()).getDownloadUrl()
                                .addOnCompleteListener(MessageActivity.this,
                                        task1 -> {

                                            if (task1.isSuccessful()) {
                                                //creating reference to internal storage for storing message image
                                                StorageReference reference1 = FirebaseStorage.getInstance().getReferenceFromUrl(Objects.requireNonNull(task1.getResult()).toString());
                                                File localFile = new File("/storage/emulated/0/Samwaad/Photos/"+fuser.getUid()+"/");
                                                //creating path if doesn't exist
                                                if (!localFile.exists()){
                                                    //noinspection ResultOfMethodCallIgnored
                                                    localFile.mkdirs();
                                                }
                                                File root_file = new File("/storage/emulated/0/Samwaad/Photos/"+fuser.getUid()+"/"+reference1.getName()+".jpg");
                                                try {
                                                    //noinspection ResultOfMethodCallIgnored
                                                    root_file.createNewFile();
                                                    Log.d("fileCPath", root_file.getPath());
                                                    Log.d("fileCreated",localFile.getPath());
                                                    //downloading image from firebase storage
                                                    reference1.getFile(root_file)
                                                            .addOnCompleteListener(task2 -> {
                                                                if (task2.isSuccessful()){
                                                                    Log.i("DownloadTask","Successful");
                                                                } else{
                                                                    Log.e("ErrorTask","Error Occurred",task2.getException());
                                                                }
                                                            });
                                                } catch(IOException e){
                                                    e.printStackTrace();
                                                }
//                                                ---------------------------------------
                                                //uploading message relevant information to database
                                                assert task1.getResult() != null;
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
                                                hashMap.put("imageUrl", task1.getResult().toString());
                                                hashMap.put("isseen", false);

                                                Log.d("ImageURL", task1.getResult().toString(), task1.getException());

                                                reference.child("Chats").child(sender+"_"+receiver).child(ts).setValue(hashMap);
                                                reference.child("Chats").child(receiver+"_"+sender).child(ts).setValue(hashMap);

                                            }
                                        });
                    } else {
                        Log.e("ImageUpload", "Image upload task was not successful.", task.getException());
                    }
                });
    }

    private void sendMessage(String sender, final String receiver, final String message){

        Date date = new Date();
        String ts = Long.toString(date.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //HashMap object for storing message and relevant information
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
    //Reading messages
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
                   //checking if the message belongs to sender and receiver
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        //adding chat object to chat list
                        mChat.add(chat);
                    }
                    //initialising message adapter and passing context and chat list
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
//                    if (chat.getImageUrl()==null){
//                        if (first) {
//
//                        } else{
//
//                        }
//                    } else if (chat.getImageUrl()!=null){
//                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
//                    }
                    //passing adapter to recycler view
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //method for updating status of logged-in user
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
        if (userid!=null){
            //initialising database reference to user/(receiver)userid node
            reference = FirebaseDatabase.getInstance().getReference("users").child(userid);
            //adding snapshot listener
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //getting value from snapshot
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    //passing receiver name to username TextView
                    username.setText(user.getUsername());
                    Log.d("username_11",user.getUsername());
                    //Loading image in profile_image ImageView
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .apply(new RequestOptions().error(R.drawable.ic_person))
                            .into(profile_image);
                    //passing receiver (online/offline/typing)status to status_dynamic TextView
                    status_dynamic.setText(user.getStatus());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MessageResumeDataError",error.getDetails());
                }
            });
        } else {
            Log.e("user_id_null","null");
        }
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