package com.india.chat.samwaad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.india.chat.samwaad.Model.Chat;
import com.india.chat.samwaad.Model.User;
import com.india.chat.samwaad.Model.UserFirestore;
import com.india.chat.samwaad.R;
import com.india.chat.samwaad.chat.MessageActivity;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<UserFirestore> mUsers;
    private boolean inchat;

    String theLastMessage;
    String theLastMessageFromYou;

    public UserAdapter(Context mContext, List<UserFirestore> mUsers, boolean inchat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.inchat = inchat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserFirestore user = mUsers.get(position);

        holder.username.setText(user.getName());
        if (user.getImageURL()==null){
            holder.profile_image.setImageResource(R.drawable.ic_person);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        showTime(user.getId(), holder.timestamp);

        if(user.getStatus().equals("Typing...")){
            holder.timestamp.setVisibility(View.GONE);
            holder.last_msg.setTextColor(Color.parseColor("#27C274"));
            holder.last_msg.setText("typing...");
        }else {
            if (inchat) {
                lastMessage(user.getId(), holder.last_msg,holder.username);

            } else {
                holder.last_msg.setVisibility(View.GONE);
            }
        }

        if (inchat){
            if (user.getStatus().equals("online") || user.getStatus().equals("Typing...")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_off.setVisibility(View.GONE);
                holder.img_on.setVisibility(View.GONE);
                holder.stat.setVisibility(View.GONE);

            }
        } else {
            holder.img_off.setVisibility(View.GONE);
            holder.img_on.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("user_id", user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        View stat;
        TextView timestamp;


        public ViewHolder(View itemView){
            super(itemView);

            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            stat = itemView.findViewById(R.id.stat);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
    private  void lastMessage(final String userid, final TextView last_msg,final TextView name){
        theLastMessageFromYou = "default";
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(firebaseUser.getUid()+"_"+userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        theLastMessage = chat.getMessage();
                        if (chat.getMessage()==null){

                            if(chat.getImageUrl()== null){
                                Log.d("NullImageURL","NULLIMAGEURL");
                                last_msg.setVisibility(View.GONE);
                            } else if (chat.getImageUrl()!= null){
                                Log.d("NonNullImageURL","NONNULLIMAGEURL");
                                last_msg.setText("Sent an attachment");
                            }
                        }else {
                            switch (theLastMessage){
                                case "default" :
                                    last_msg.setText("No Message");
                                    break;

                                default:
                                    if (theLastMessage.length()<=20){
                                        last_msg.setText(theLastMessage);
                                    }else{
                                        String last_message = theLastMessage.substring(0,20);
                                        last_msg.setText(last_message + "...");
                                        break;
                                    }
                            }
                        }
                    } else if (chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessageFromYou = chat.getMessage();
                        if (chat.getMessage()==null){
                            if(chat.getImageUrl()== null){
                                Log.d("NullImageURL","NULLIMAGEURL");
                                last_msg.setVisibility(View.GONE);
                            } else if (chat.getImageUrl()!= null){
                                last_msg.setText("You: Sent an attachment");
                            }
                        }else {
                            switch (theLastMessageFromYou){
                                case "default" :
                                    last_msg.setText("No Message");
                                    break;

                                default:
                                    if (theLastMessageFromYou.length()<25){
                                        last_msg.setText("You: "+theLastMessageFromYou);
                                    }else{
                                        String last_message = theLastMessageFromYou.substring(0,25);
                                        last_msg.setText("You: "+last_message + "...");
                                        break;
                                    }
                            }
                        }
                    }
                }
                theLastMessage = "default";
                theLastMessageFromYou = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showTime(final String userid, final TextView timestamp){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(firebaseUser.getUid() + "_" + userid);
            reference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Chat chat = dataSnapshot.getValue(Chat.class);

                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                            if (chat.getMessage() == null) {
                                if (chat.getImageUrl() == null) {
                                    timestamp.setVisibility(View.GONE);
                                }
                            } else {
                                long ts = chat.getTimestamp();

                                LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), TimeZone.getDefault().toZoneId());
                                DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
                                String day = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US);
                                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                                String datetime = localDateTime.format(formatter);

                                timestamp.setText(day);
                            }
                        } else if (chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            if (chat.getMessage() == null) {
                                if (chat.getImageUrl() == null) {
                                    timestamp.setVisibility(View.GONE);
                                }
                            } else {
                                long ts = chat.getTimestamp();
                                //converts timestamp to local date time
                                LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), TimeZone.getDefault().toZoneId());
                                //converts local date time to day of the week
                                DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
                                String day = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US);
                                //will format local date time to readable date but in use now
                                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                                String datetime = localDateTime.format(formatter);

                                timestamp.setText(day);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
