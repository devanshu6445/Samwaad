package com.india.chat.samwaad.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.india.chat.samwaad.EditPersonalDetails;
import com.india.chat.samwaad.R;
import com.india.chat.samwaad.login_to_samwaad;
import com.india.chat.samwaad.user.ChangePassword;

import java.util.HashMap;


public class UserProfile extends Fragment {

    ImageView imageView;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore;
    TextView name_tv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.user_profile, container, false);
        final View ViewProgressBar = root.findViewById(R.id.progress_Bar2);
        ViewProgressBar.setVisibility(View.GONE);
        imageView = root.findViewById(R.id.profileCircleImageView);
        TextView editProfile = root.findViewById(R.id.editProfile);
        TextView signout = root.findViewById(R.id.signout);
        TextView changePassword = root.findViewById(R.id.changePassword);
        name_tv = root.findViewById(R.id.usernameTextVie);
        firebaseFirestore = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        String image_url = preferences.getString("image_url","image_url");
        String name = preferences.getString("name","0");
        Glide.with(imageView.getContext())
               .load(image_url)
                .apply(new RequestOptions().error(R.drawable.ic_person))
                .into(imageView);
        if(!name.equals("0")){
            name_tv.setText(name);
        }else{
            name_tv.setText("Please set your name");
        }



        changePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePassword.class);
            startActivity(intent);
        });
        signout.setOnClickListener(v -> {
            status("offline");
            FirebaseAuth user = FirebaseAuth.getInstance();
            Intent intent = new Intent(getActivity(), login_to_samwaad.class);
            intent.putExtra("signout",true);
            startActivity(intent);


        });
        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditPersonalDetails.class);
            startActivity(intent);
        });
        return root;

    }
    DatabaseReference reference;
    private void status(String status){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser!=null){
            reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            firebaseFirestore.collection("users").document(fuser.getUid())
                    .update(hashMap);
            reference.updateChildren(hashMap);
        }
    }
}
