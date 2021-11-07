package com.india.chat.samwaad.ui.dashboard;

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
import com.india.chat.samwaad.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    RecyclerView ts_rv_search;
    EditText ts_search;
    ContactsAdapter contactsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ts_rv_search = root.findViewById(R.id.ts_rv_search);
        ts_search = root.findViewById(R.id.ts_search);
        ts_rv_search.setHasFixedSize(true);
        ts_rv_search.setLayoutManager(new LinearLayoutManager(getContext()));
        ts_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(ts_search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return root;

    }
    private void searchUsers(String toString){
        List<Contacts> refCont;
        refCont = new ArrayList<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference searchRef = firebaseFirestore.collection("users");
        String search = ts_search.getText().toString();
        Query query = searchRef.whereGreaterThanOrEqualTo("phoneNumber",toString)
                .whereLessThanOrEqualTo("phoneNumber",toString+'\uf8ff');
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                    contactsAdapter = new ContactsAdapter(getContext(),refCont);
                    ts_rv_search.setAdapter(contactsAdapter);


                }
            }

        });
    }
}