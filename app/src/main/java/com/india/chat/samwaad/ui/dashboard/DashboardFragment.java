package com.india.chat.samwaad.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.india.chat.samwaad.Adapter.StoryAdapter;
import com.india.chat.samwaad.Model.Chat;
import com.india.chat.samwaad.Model.Contacts;
import com.india.chat.samwaad.Model.StoryMember;
import com.india.chat.samwaad.Model.User;
import com.india.chat.samwaad.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DashboardFragment extends Fragment {

    private Button addStory;
    private RecyclerView recyclerView_story;
    private List<StoryMember> memberList;
    private StoryAdapter storyAdapter;
    DatabaseReference reference;
    private List<String> usersList;
    private List<User> mUsers;
    private final int STORY_REQUEST_CODE=1;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        addStory = root.findViewById(R.id.addStory);

        recyclerView_story = root.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_story.setLayoutManager(linearLayoutManager);

        addStory.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent,STORY_REQUEST_CODE);
        });
        mUsers = new ArrayList<>();

        usersList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Chat chat = snapshot1.getValue(Chat.class);

                            if (chat.getSender().equals(fuser.getUid())) {
                                usersList.add(chat.getReceiver());
                            }
                            if (chat.getReceiver().equals(fuser.getUid())) {
                                usersList.add(chat.getSender());
                            }
                        }
                    }
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==STORY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                if (data!=null){
                    Uri uri = data.getData();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                            .child("stories")
                            .child(user.getUid())
                            .child(uri.getLastPathSegment());
                    putImagetoStorage(uri,storageReference,data.getType());
                }
            }
        }
    }
    private void putImagetoStorage(Uri uri,StorageReference reference, String type){

        try {
            reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    if(task.getResult()!=null){
                        task.getResult().getMetadata().getReference().getDownloadUrl()
                                .addOnCompleteListener(task1 -> {
                                    String imageurl = task1.getResult().toString();
                                    Log.d("ImageUpload",imageurl);
                                    addStory(imageurl,type);
                                });
                    }
                }
            }
        });
        } catch(NullPointerException e){
            e.printStackTrace();
            Log.d("UploadNull",e.toString());
        }
    }
    private void addStory(String posturl,String type){
        try {
            FirebaseFirestore firestore  =FirebaseFirestore.getInstance();
            long timec = System.currentTimeMillis();
            String timeCreated = String.valueOf(timec);
            String timeEnd = String.valueOf(timec + 86400000);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            StoryMember storyMember = new StoryMember(posturl, user.getUid(), timeEnd, timeCreated, type, user.getUid());
            String uuid = UUID.randomUUID().toString();
            firestore.collection("story").document(user.getUid()).collection(user.getUid())
                    .document(timeCreated).set(storyMember).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Story uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (DatabaseException | NullPointerException e){
            if(e instanceof DatabaseException){
                e.getMessage();
            }
        }
    }

    private void testStory(List<User> mUsers1) {
        memberList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        memberList.clear();
        for (User user : mUsers1) {
            CollectionReference reference = firestore.collection("story").document(user.getId())
                    .collection(user.getId());
            Log.d("userid",user.getId());
            Query query = reference.whereEqualTo("uid",user.getId());
            query.addSnapshotListener((value, error) -> {

                int i=0;
                assert value != null;
                try {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                        if (i == 0) {
                            String name = Objects.requireNonNull(queryDocumentSnapshot.get("name")).toString();
                            String uid = Objects.requireNonNull(queryDocumentSnapshot.get("uid")).toString();
                            String postUri = Objects.requireNonNull(queryDocumentSnapshot.get("postUri")).toString();
                            String timeEnd = Objects.requireNonNull(queryDocumentSnapshot.get("timeEnd")).toString();
                            String timeUpload = Objects.requireNonNull(queryDocumentSnapshot.get("timeUpload")).toString();
                            StoryMember storyMember = new StoryMember(postUri, name, timeEnd, timeUpload, null, uid);
                            memberList.add(storyMember);
                            i++;
                        }
                        if (i==1){
                            break;
                        }
                    }
                } catch(NullPointerException e){
                    e.printStackTrace();
                }

                storyAdapter = new StoryAdapter(getContext(),memberList,value);
                recyclerView_story.setAdapter(storyAdapter);
            });
        }
    }

    private void searchUsers(String toString){
        List<Contacts> refCont;
        refCont = new ArrayList<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference searchRef = firebaseFirestore.collection("users");
        Query query = searchRef.whereGreaterThanOrEqualTo("phoneNumber",toString)
                .whereLessThanOrEqualTo("phoneNumber",toString+'\uf8ff');
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Sucess12", "success");
                try {
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        String id = Objects.requireNonNull(documentSnapshot.get("Id")).toString();
                        String name = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.get("name")).toString());
                        String phone_number = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.get("phoneNumber")).toString());
                        Log.d("ID_DD", id);
                        String ImageUrl;
                        if (Objects.requireNonNull(documentSnapshot.get("ImageURL")).toString() == null) {
                            ImageUrl = null;
                        } else {
                            ImageUrl = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.get("ImageURL")).toString());

                        }
                        Contacts contacts1 = new Contacts(name, phone_number, ImageUrl, id);
                        refCont.add(contacts1);
                        Log.d("successId", String.valueOf(refCont.size()));
                    }
                    Log.d("successId3", String.valueOf(refCont.size()));
                } catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void showStory(@NonNull List<User> mUsers){
        memberList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        memberList.clear();
        for (User user : mUsers){
            reference = FirebaseDatabase.getInstance().getReference("story").child(user.getId());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                String name = snapshot.child("name").getValue(String.class);
                                String uid = snapshot.child("uid").getValue(String.class);
                                String postUri = snapshot.child("postUri").getValue(String.class);
                                String type = snapshot.child("type").getValue(String.class);
                                String timeEnd = snapshot.child("timeEnd").getValue(String.class);
                                String timeUpload = snapshot.child("timeUpload").getValue(String.class);
                                StoryMember storyMember = new StoryMember(postUri,name,timeEnd,timeUpload,type,uid);
                                memberList.add(storyMember);

                            }
                            storyAdapter = new StoryAdapter(getContext(),memberList);
                            recyclerView_story.setAdapter(storyAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        for (String id : usersList){
                            assert user != null;
                            if(user.getId().equals(id)){
                                if(mUsers.size()!=0){
                                    int flag=0;
                                    for(User u : mUsers) {
                                        if (user.getId().equals(u.getId())) {
                                            flag = 1;
                                            break;
                                        }
                                    }
                                    if(flag==0)
                                        mUsers.add(user);
                                }else{

                                    mUsers.add(user);
                                }
                            }
                        }

                    }
                    //here
                testStory(mUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}