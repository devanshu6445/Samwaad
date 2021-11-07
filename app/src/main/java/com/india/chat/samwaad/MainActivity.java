package com.india.chat.samwaad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.FirebaseFirestore;
import com.india.chat.samwaad.ui.UserProfile;
import com.india.chat.samwaad.ui.dashboard.DashboardFragment;
import com.india.chat.samwaad.ui.home.HomeFragment;
import com.india.chat.samwaad.ui.notifications.NotificationsFragment;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    DatabaseReference reference;
    FirebaseUser fuser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        navView.setSelectedItemId(R.id.chat);
        fragmentManager.beginTransaction()
                .add(R.id.container,userProfile,getString(R.string.user_profile)).hide(userProfile)
                .add(R.id.container,notificationsFragment,getString(R.string.search)).hide(notificationsFragment)
                .add(R.id.container,dashboardFragment,getString(R.string.status)).hide(dashboardFragment)
                .add(R.id.container,homeFragment,getString(R.string.chat))
                .commit();

    }
    DashboardFragment dashboardFragment = new DashboardFragment();
    HomeFragment homeFragment = new HomeFragment();
    NotificationsFragment notificationsFragment = new NotificationsFragment();
    UserProfile userProfile = new UserProfile();
    Fragment activeFragment = homeFragment;

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

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.chat:
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                return true;
            case R.id.status:
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(dashboardFragment).commit();
                activeFragment = dashboardFragment;
                return true;
            case R.id.search:
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(notificationsFragment).commit();
                activeFragment = notificationsFragment;
                return true;
            case R.id.user_profile:
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(userProfile).commit();
                activeFragment = userProfile;
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }
}