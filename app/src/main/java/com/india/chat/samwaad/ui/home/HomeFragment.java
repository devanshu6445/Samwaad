package com.india.chat.samwaad.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.india.chat.samwaad.Adapter.UserAdapter;
import com.india.chat.samwaad.Model.Chat;
import com.india.chat.samwaad.Model.User;
import com.india.chat.samwaad.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private CircleImageView img_profile_home;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EditText search_users;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private List<String> usersList;
    SharedPreferences pref;


    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        AppBarLayout app_lay = root.findViewById(R.id.app_lay);
        app_lay.setOutlineProvider(null);
        pref = getActivity().getSharedPreferences("setting",Context.MODE_PRIVATE);
        img_profile_home = root.findViewById(R.id.img_profile_home);
        Log.d("user_iddd",user.getUid());
        String img_url = pref.getString("image_url","image_url");
        Glide.with(img_profile_home.getContext())
                .load(img_url)
                .placeholder(R.drawable.ic_person)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_profile_home);


        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        usersList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        Chat chat = snapshot1.getValue(Chat.class);

                        if (chat.getSender().equals(fuser.getUid())){
                            usersList.add(chat.getReceiver());
                        }
                        if (chat.getReceiver().equals(fuser.getUid())){
                            usersList.add(chat.getSender());
                        }
                    }
                }
                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        search_users = root.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return root;
    }


    private void searchUsers(String toString) {
        if (toString.equals("")) {
            readUsers();
        } else{
            final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
            Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("search")
                    .startAt(toString)
                    .endAt(toString + "\uf8ff");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        if (!user.getId().equals(fuser.getUid())) {
                            mUsers.add(user);
                        }
                    }

                    userAdapter = new UserAdapter(getContext(), mUsers, true);
                    recyclerView.setAdapter(userAdapter);
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
                if (search_users.getText().toString().equals("")) {
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
                    userAdapter = new UserAdapter(getContext(), mUsers, true);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}