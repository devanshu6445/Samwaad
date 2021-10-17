package com.india.chat.samwaad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.india.chat.samwaad.Model.Contacts;
import com.india.chat.samwaad.R;

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
        holder.cont_img.setImageResource(R.drawable.ic_person);
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
