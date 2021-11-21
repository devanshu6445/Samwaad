package com.india.chat.samwaad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.india.chat.samwaad.Model.Contacts;
import com.india.chat.samwaad.R;
import com.india.chat.samwaad.chat.MessageActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private Context context;
    private List<Contacts> contacts;


    public ContactsAdapter(Context context,List<Contacts> contacts){
        this.context = context;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contacts_item, parent, false);
        return new ContactsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        Contacts contact = contacts.get(position);
        holder.cont_name.setText(contact.getName());
        holder.cont_num.setText(contact.getPhone_number());
        if (contact.getImageURL()!=null){
            String imageurl = contact.getImageURL();
            if (imageurl.startsWith("gs://")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageurl);
                storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (task.getResult()!=null){
                            String downloadurl = task.getResult().toString();
                            Glide.with(holder.cont_img.getContext())
                                    .load(downloadurl)
                                    .apply(new RequestOptions().error(R.drawable.ic_person).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(holder.cont_img);
                        }
                    } else {
                        Log.d("LoadImageException", "failed", task.getException());
                    }
                });
            } else {
                Glide.with(holder.cont_img.getContext())
                        .load(contact.getImageURL())
                        .apply(new RequestOptions().error(R.drawable.ic_person).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(holder.cont_img);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("user_id", contact.getId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView cont_name;
        public TextView cont_num;
        public CircleImageView cont_img;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cont_name = itemView.findViewById(R.id.name_cont);
            cont_num = itemView.findViewById(R.id.num_cont);
            cont_img = itemView.findViewById(R.id.img_cont);
        }
    }
}
