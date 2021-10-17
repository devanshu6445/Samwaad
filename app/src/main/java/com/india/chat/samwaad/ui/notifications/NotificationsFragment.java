package com.india.chat.samwaad.ui.notifications;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.india.chat.samwaad.Adapter.ContactsAdapter;
import com.india.chat.samwaad.Model.Contacts;
import com.india.chat.samwaad.Model.User;
import com.india.chat.samwaad.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationsFragment extends Fragment {

    List<User> userList;
    RecyclerView recyclerView;
    ContactsAdapter contactsAdapter;
    List<Contacts> contacts;

    private NotificationsViewModel notificationsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = root.findViewById(R.id.cont_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            addContacts();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }


    public void addContacts() throws JSONException {
        JSONObject obj = new JSONObject();
        try {
            Cursor phones =getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            contacts = new ArrayList<>();
            while (phones.moveToNext()){
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contacts contacts1 = new Contacts(name,phoneNumber);
                contacts.add(contacts1);
            }
            phones.close();
            contactsAdapter = new ContactsAdapter(getContext(),contacts);
            recyclerView.setAdapter(contactsAdapter);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}