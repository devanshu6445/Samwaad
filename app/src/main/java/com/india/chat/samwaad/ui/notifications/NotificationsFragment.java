package com.india.chat.samwaad.ui.notifications;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.india.chat.samwaad.Adapter.ContactsAdapter;
import com.india.chat.samwaad.Model.Contacts;
import com.india.chat.samwaad.Model.User;
import com.india.chat.samwaad.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationsFragment extends Fragment {


    RecyclerView recyclerView;
    ContactsAdapter contactsAdapter;
    List<Contacts> contacts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = root.findViewById(R.id.cont_rv);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){

            try {
                addContacts();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            requestPermissions(new String[]
                    {Manifest.permission.READ_CONTACTS},2);
        }

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==2){
            Toast.makeText(getActivity(), "Okay", Toast.LENGTH_SHORT).show();
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    addContacts();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(),"Denied",Toast.LENGTH_SHORT);
            }
        }
    }

    public void addContacts() throws JSONException {
        JSONObject obj = new JSONObject();

        try {
            Cursor phones = requireContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            contacts = new ArrayList<>();
            while (phones.moveToNext()){
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contacts contacts1 = new Contacts(name,phoneNumber);
                contacts.add(contacts1);
            }
            phones.close();
            readContacts(contacts);


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private List<Contacts> refCont;
    ContactsAdapter contactsAdapter1;
    public void readContacts(List<Contacts> contacts){

        for (Contacts contact : contacts){
            refCont = new ArrayList<>();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference searchRef = firebaseFirestore.collection("users");
            Query query = searchRef.whereEqualTo("phoneNumber",contact.getPhone_number());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())){
                            String id = Objects.requireNonNull(documentSnapshot.get("Id")).toString();
                            String name = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.get("name")).toString());
                            String phone_number = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.get("phoneNumber")).toString());

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
                        contactsAdapter = new ContactsAdapter(getContext(),refCont);
                        contactsAdapter1 = contactsAdapter;

                    }
                    recyclerView.setAdapter(contactsAdapter);
                }

            });


        }

    }
}