package com.india.chat.samwaad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.india.chat.samwaad.Model.StoryMember;
import com.india.chat.samwaad.R;
import com.india.chat.samwaad.StoriesActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int createdTime;
    private int endTime;
    private String storyUrl;
    private String uid;
    private Context context;
    private long count;
    List<StoryMember> storyList;
    QuerySnapshot value;

    public StoryAdapter(){};

    public StoryAdapter(Context context, List<StoryMember> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    public StoryAdapter(Context context, List<StoryMember> storyList, QuerySnapshot value) {
        this.context = context;
        this.storyList = storyList;
        this.value = value;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_recyclerview_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoryMember storyMember = storyList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        Date date = new Date();
        long timeCreated = Long.parseLong(storyMember.getTimeUpload());
        long currentTime = date.getTime()/1000;
        long difference = currentTime-timeCreated;
        if (difference<=3600){
            Log.i("StoryTime",String.valueOf(difference));
            int minutes = (int)difference/60;
            String time = minutes +" minutes ago";
            viewHolder.storyCreatedTime.setText(time);
        } else if(difference<=86400){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timeCreated);
            Date d = c.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String time1 = simpleDateFormat.format(d);
            viewHolder.storyCreatedTime.setText(time1);
        } else{
            Log.d("storyNotFound",String.valueOf(difference));
            viewHolder.storyCreatedTime.setVisibility(View.GONE);
            viewHolder.name_user.setVisibility(View.GONE);
            viewHolder.storyImageView.setVisibility(View.GONE);
            viewHolder.storyView.setVisibility(View.GONE);
        }


        Glide.with(context)
                .load(storyMember.getPostUri())
                .into(viewHolder.storyImageView);
        viewHolder.name_user.setText(storyMember.getName());

        viewHolder.storyView.setOnClickListener(view -> {
            loadStory(value);
        });
    }

    private void loadStory(QuerySnapshot value){
        int i=0;

        for (QueryDocumentSnapshot snap : value){
            i++;
        }
        int j=0;
        String[] urls = new String[i];
        Date currentDate = new Date();
        long current_time = currentDate.getTime()/1000;
        for(QueryDocumentSnapshot snapshot : value){
            String url = snapshot.get("postUri").toString();
            Log.i("urlStory",url);
            long timeEnd = Long.parseLong(snapshot.get("timeEnd").toString());
            long difference = timeEnd-current_time;
            Log.i("timeEnd",String.valueOf(timeEnd));
            Log.i("currentTime",String.valueOf(System.currentTimeMillis()));
            if (difference>0) {
                if (j <= i-1) {
                    urls[j] = url;
                }
                Log.d("postUrl", urls[j]);
                j++;
            }
        }
        for(String url:urls){
            Log.d("urlCon",url);
            Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(context, StoriesActivity.class);
        intent.putExtra("array",urls);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView storyImageView;
        public TextView name_user;
        public TextView storyCreatedTime;
        public View storyView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            storyImageView = itemView.findViewById(R.id.storyImageView);
            name_user = itemView.findViewById(R.id.textView_name);
            storyCreatedTime = itemView.findViewById(R.id.textView_created_time);
            storyView = itemView.findViewById(R.id.storyView);

        }
    }
}
