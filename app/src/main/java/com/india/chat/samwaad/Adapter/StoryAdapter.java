package com.india.chat.samwaad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.india.chat.samwaad.Model.StoryMember;
import com.india.chat.samwaad.R;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int createdTime;
    private int endTime;
    private String storyUrl;
    private String uid;
    private Context context;
    List<StoryMember> storyList;

    public StoryAdapter(){};

    public StoryAdapter(Context context, List<StoryMember> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_recyclerview_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView storyImageView;
        public TextView name_user;
        public TextView storyCreatedTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storyImageView = itemView.findViewById(R.id.storyImageView);
            name_user = itemView.findViewById(R.id.textView_name);
            storyCreatedTime = itemView.findViewById(R.id.textView_created_time);

        }
    }
}
