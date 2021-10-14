package com.india.chat.samwaad.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.india.chat.samwaad.Model.Chat;
import com.india.chat.samwaad.R;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT)
        {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {

        final Chat chat = mChat.get(position);
        if (chat.getImageUrl()!=null){
            String imageurl = chat.getImageUrl();
            if (imageurl.startsWith("gs://")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageurl);
                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            String downloadurl = task.getResult().toString();
                            Glide.with(holder.MessageImageView.getContext())
                                    .load(downloadurl)
                                    .apply(new RequestOptions().transform(new RoundedCorners(200)).error(R.drawable.ic_person).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(holder.MessageImageView);
                        } else {
                            Log.d("LoadImageException", "failed", task.getException());
                        }
                    }
                });
            } else {
                Glide.with(holder.MessageImageView.getContext())
                        .load(chat.getImageUrl())
                        .apply(new RequestOptions().transform(new RoundedCorners(200)).error(R.drawable.ic_person).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(holder.MessageImageView);
            }
            holder.show_messages.setVisibility(View.GONE);
        }
        if (chat.getMessage()!=null){
            holder.show_messages.setText(chat.getMessage());
            holder.MessageImageView.setVisibility(View.GONE);
            holder.roundView.setVisibility(View.GONE);
            holder.show_messages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Okay", Toast.LENGTH_SHORT).show();
                    PopupMenu popupMenu = new PopupMenu(mContext,holder.show_messages);
                    popupMenu.inflate(R.menu.message_manipulaton_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete:

                                    new AlertDialog.Builder(mContext)
                                            .setMessage("Are you sure you want to delete this message?")
                                            .setPositiveButton("Delete for everyone", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String unique_id = chat.getUnique_id();
                                                    Log.d("unique_id", unique_id);
                                                    String sender = chat.getSender();
                                                    String receiver = chat.getReceiver();
                                                    deleteMessage(sender,receiver,unique_id);
                                                }
                                            })
                                            .setNegativeButton("Delete for me only", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String unique_id = chat.getUnique_id();
                                                    Log.d("unique_id", unique_id);
                                                    String sender = chat.getSender();
                                                    String receiver = chat.getReceiver();
                                                    deleteMessageForMe(sender, receiver, unique_id);
                                                }
                                            })
                                            .show();
                                    break;
                                case R.id.info:
                                    Toast.makeText(mContext, "You clicked info", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        if(imageurl.equals("null")){
            holder.profile_image.setImageResource(R.drawable.ic_person);
        }else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_messages;
        public ImageView profile_image;
        public ImageView MessageImageView;
        public  TextView txt_seen;
        public View roundView;

        public ViewHolder(View itemView){
            super(itemView);

            show_messages = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.text_seen);
            MessageImageView = itemView.findViewById(R.id.MessageImageView);
            roundView = itemView.findViewById(R.id.RoundMessageImageView);
        }


    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    private void deleteMessage(String sender, String receiver, String unique_id){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", null);
        hashMap.put("receiver", null);
        hashMap.put("message", null);
        hashMap.put("timestamp", null);
        hashMap.put("unique_id", null);
        hashMap.put("isseen",null);
        reference.child(sender+"_"+receiver).child(unique_id).setValue(hashMap);
        reference.child(receiver+"_"+sender).child(unique_id).setValue(hashMap);
    }

    private void deleteMessageForMe(String sender, String receiver, String unique_id){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", null);
        hashMap.put("receiver", null);
        hashMap.put("message", null);
        hashMap.put("timestamp", null);
        hashMap.put("unique_id", null);
        hashMap.put("isseen",null);
        reference.child(sender+"_"+receiver).child(unique_id).setValue(hashMap);
    }
}