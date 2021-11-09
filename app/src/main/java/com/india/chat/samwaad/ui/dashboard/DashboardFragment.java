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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.india.chat.samwaad.Adapter.StoryAdapter;
import com.india.chat.samwaad.Model.Contacts;
import com.india.chat.samwaad.Model.StoryMember;
import com.india.chat.samwaad.R;

import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    private Button addStory;
    private RecyclerView recyclerView_story;
    private List<StoryMember> memberList;
    private StoryAdapter storyAdapter;
    DatabaseReference reference;
    private final int STORY_REQUEST_CODE=1;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        addStory = root.findViewById(R.id.addStory);
        recyclerView_story = root.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_story.setLayoutManager(linearLayoutManager);
        storyAdapter = new StoryAdapter();
        recyclerView_story.setAdapter(storyAdapter);
        addStory.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent,STORY_REQUEST_CODE);
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

        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    if(task.getResult()!=null){
                        task.getResult().getMetadata().getReference().getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String imageurl = task.getResult().toString();
                                        Log.d("ImageUpload",imageurl);
                                        addStory(imageurl,type);
                                    }
                                });
                    }
                }
            }
        });
    }
    private void addStory(String posturl,String type){
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            long timec = System.currentTimeMillis();
            String timeCreated = String.valueOf(timec);
            String timeEnd = String.valueOf(timec + 86400);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            StoryMember storyMember = new StoryMember(posturl, user.getUid(), timeEnd, timeCreated, type, user.getUid());
            reference.child("story").child(user.getUid()).setValue(storyMember).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Story upload successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"An error has occured",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (DatabaseException e){
            e.printStackTrace();
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
            if (task.isSuccessful()){
                Log.d("Sucess12","success");
                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())){
                    String id = Objects.requireNonNull(documentSnapshot.get("Id")).toString();
                    String name = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.get("name")).toString());
                    String phone_number = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.get("phoneNumber")).toString());
                    Log.d("ID_DD",id);
                    String ImageUrl;
                    if (documentSnapshot.get("ImageURL").toString()==null){
                        ImageUrl = null;
                    } else {
                        ImageUrl = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.get("ImageURL")).toString());

                    }
                    Contacts contacts1 = new Contacts(name, phone_number,ImageUrl,id);
                    refCont.add(contacts1);
                    Log.d("successId", String.valueOf(refCont.size()));
                }
                Log.d("successId3",String.valueOf(refCont.size()));


            }
        });
    }
}