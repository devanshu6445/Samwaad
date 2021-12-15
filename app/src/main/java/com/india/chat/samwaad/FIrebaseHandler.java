package com.india.chat.samwaad;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FIrebaseHandler extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance()
                .setPersistenceEnabled(true);
    }
}
