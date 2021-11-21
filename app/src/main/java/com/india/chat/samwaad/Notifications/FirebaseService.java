package com.india.chat.samwaad.Notifications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

public class FirebaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        updateToken(token);
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Token token = new Token(refreshToken);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", token.getToken());
        reference.child(firebaseUser.getUid()).setValue(hashMap);
    }
}
