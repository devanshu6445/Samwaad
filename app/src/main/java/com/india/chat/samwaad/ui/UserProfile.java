package com.india.chat.samwaad.ui;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.india.chat.samwaad.EditPersonalDetails;
import com.india.chat.samwaad.R;
import com.india.chat.samwaad.login_to_samwaad;
import com.india.chat.samwaad.user.ChangePassword;

import java.util.HashMap;


public class UserProfile extends Fragment {

    ImageView imageView;
    FirebaseUser fuser;
    DatabaseReference reference;

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
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                startActivity(intent);
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth user = FirebaseAuth.getInstance();
                status("offline");
                Intent intent = new Intent(getActivity(), login_to_samwaad.class);
                user.signOut();
                startActivity(intent);

            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditPersonalDetails.class);
                startActivity(intent);
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user!=null;
        Uri photoUrl = user.getPhotoUrl();
        if(photoUrl != null){
            Glide.with(this)
                    .load(photoUrl)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_user);
        }
        TextView tempname = root.findViewById(R.id.usernameTextView);
        String profileName = user.getDisplayName();
        tempname.setText(profileName);
        return root;

    }
    private void status(String status){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }
}
