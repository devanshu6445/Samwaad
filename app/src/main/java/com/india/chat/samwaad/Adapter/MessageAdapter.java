package com.india.chat.samwaad.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.india.chat.samwaad.Model.Chat;
import com.india.chat.samwaad.R;
import com.ramijemli.percentagechartview.PercentageChartView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private static final String TAG = "MessageAdapter";

    private Context mContext;
    private List<Chat> mChat;

    private Handler handler = new Handler();

    FirebaseUser fuser;


    public MessageAdapter(Context mContext, List<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT)
        {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        return new ViewHolderSend(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolderReceive(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Chat chat = mChat.get(position);

        switch (holder.getItemViewType()){
            case 0:
                receiveView(chat,holder,position);
                break;
            case 1:
                sendView(chat,holder,position);
                break;
        }

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolderSend extends RecyclerView.ViewHolder{

        public TextView show_messages_send;
        public ImageView MessageImageView_send;
        public TextView txt_seen_send;
        public TextView txt_seen_msg_send;
        public View roundView_send;
        public View media;
        public Button start;
        public Button pause;
        public Button audioDownloadSend;
        public PercentageChartView audioDownloadingSend;
        public View audioDownloadingSection;
        public View audioSection;

        public ViewHolderSend(View itemView) {
            super(itemView);
            media = itemView.findViewById(R.id.mediaSend);
            start = itemView.findViewById(R.id.AudioResume);
            pause = itemView.findViewById(R.id.AudioPause);
            audioDownloadSend = media.findViewById(R.id.AudioDownload);
            audioDownloadingSection = media.findViewById(R.id.AudioDownloadSection);
            audioDownloadingSend = media.findViewById(R.id.AudioDownloading);
            audioSection = media.findViewById(R.id.Audio);
            txt_seen_msg_send = itemView.findViewById(R.id.text_seen_msg_send);
            show_messages_send = itemView.findViewById(R.id.show_message_send);
            txt_seen_send = itemView.findViewById(R.id.text_seen_send);
            MessageImageView_send = itemView.findViewById(R.id.MessageImageView_send);
            roundView_send = itemView.findViewById(R.id.RoundMessageImageView_send);
        }

    }
    public class ViewHolderReceive extends RecyclerView.ViewHolder{

        public TextView show_messages_receive;
        public ImageView MessageImageView_receive;
        public TextView txt_seen_receive;
        public TextView txt_seen_msg_receive;
        public View roundView_receive;
        public View mediaReceive;
        public Button startReceive;
        public Button pauseReceive;
        public Button audioDownloadReceive;
        public PercentageChartView audioDownloadingReceive;
        public View audioDownloadingSection;
        public View audioSection;

        public ViewHolderReceive(View itemView){
            super(itemView);
            mediaReceive = itemView.findViewById(R.id.media);
            startReceive = mediaReceive.findViewById(R.id.AudioResume);
            pauseReceive = mediaReceive.findViewById(R.id.AudioPause);
            audioDownloadReceive = mediaReceive.findViewById(R.id.AudioDownload);
            audioDownloadingSection = mediaReceive.findViewById(R.id.AudioDownloadSection);
            audioDownloadingReceive = mediaReceive.findViewById(R.id.AudioDownloading);
            audioSection = mediaReceive.findViewById(R.id.Audio);
            txt_seen_msg_receive = itemView.findViewById(R.id.text_seen_msg_receive);
            show_messages_receive = itemView.findViewById(R.id.show_message_receive);
            txt_seen_receive = itemView.findViewById(R.id.text_seen_receive);
            MessageImageView_receive = itemView.findViewById(R.id.MessageImageView_receive);
            roundView_receive = itemView.findViewById(R.id.RoundMessageImageView_receive);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public int addMessage(Chat chat){
        if (mChat.contains(chat)){
            return mChat.size();
        } else {
            mChat.add(chat);
            notifyDataSetChanged();
            notifyItemInserted(mChat.size());
            return mChat.size();
        }
    }

    private void sendView(@NonNull Chat chat, RecyclerView.ViewHolder holder, int position) {
        ViewHolderSend viewHolderSend = (ViewHolderSend) holder;
        if (chat.getMessage() != null) {
            viewHolderSend.show_messages_send.setText(chat.getMessage());
            viewHolderSend.MessageImageView_send.setVisibility(View.GONE);
            viewHolderSend.roundView_send.setVisibility(View.GONE);
            viewHolderSend.media.setVisibility(View.GONE);
            viewHolderSend.start.setVisibility(View.GONE);
            viewHolderSend.pause.setVisibility(View.GONE);
            viewHolderSend.show_messages_send.setOnClickListener(v ->
            {
                Toast.makeText(mContext, "Okay", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolderSend.show_messages_send);
                popupMenu.inflate(R.menu.message_manipulaton_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.delete:

                            new AlertDialog.Builder(mContext)
                                    .setMessage("Are you sure you want to delete this message?")
                                    .setPositiveButton("Delete for everyone", (dialog, which) -> {
                                        String unique_id = chat.getUnique_id();
                                        Log.d("unique_id", unique_id);
                                        String sender = chat.getSender();
                                        String receiver = chat.getReceiver();
                                        deleteMessage(sender, receiver, unique_id);
                                    })
                                    .setNegativeButton("Delete for me only", (dialog, which) -> {
                                        String unique_id = chat.getUnique_id();
                                        Log.d("unique_id", unique_id);
                                        String sender = chat.getSender();
                                        String receiver = chat.getReceiver();
                                        deleteMessageForMe(sender, receiver, unique_id);
                                    })
                                    .show();
                            break;
                        case R.id.info:
                            Toast.makeText(mContext, "You clicked info", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });
        }

        else if (chat.getImageUrl() != null) {
            String imageurl = chat.getImageUrl();
            if (imageurl.startsWith("gs://")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageurl);

                storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if(task.getResult() != null){
                            String download_url = task.getResult().toString();
                            loadImage(viewHolderSend.MessageImageView_send, download_url, chat.getSender());
                        }
                    } else {
                        Log.d("LoadImageException", "failed", task.getException());
                    }
                });
            } else {
            loadImage(viewHolderSend.MessageImageView_send, chat.getImageUrl(), chat.getSender());
            viewHolderSend.show_messages_send.setVisibility(View.GONE);
            viewHolderSend.media.setVisibility(View.GONE);
            viewHolderSend.start.setVisibility(View.GONE);
            viewHolderSend.pause.setVisibility(View.GONE);
        }
    }

        else if (chat.getAudioUrl()!=null){
            String url = chat.getAudioUrl();
            if(audioExist(url)) {
                viewHolderSend.audioDownloadingSection.setVisibility(View.GONE);
                audioPlay(viewHolderSend,viewHolderSend.media, viewHolderSend.start, viewHolderSend.pause, chat.getAudioUrl());
            }else{
                viewHolderSend.audioSection.setVisibility(View.GONE);
                audioDownload(url,viewHolderSend);
            }
            viewHolderSend.MessageImageView_send.setVisibility(View.GONE);
            viewHolderSend.roundView_send.setVisibility(View.GONE);
            viewHolderSend.show_messages_send.setVisibility(View.GONE);
        }
        if (position == mChat.size()-1){
            try {
                if (chat.getMessage()!=null){
                    if (chat.isIsseen()){
                        viewHolderSend.txt_seen_send.setVisibility(View.GONE);
                        viewHolderSend.txt_seen_msg_send.setText("Seen");
                    } else {
                        viewHolderSend.txt_seen_send.setVisibility(View.GONE);
                        viewHolderSend.txt_seen_msg_send.setText("Delivered");
                    }
                } else if (chat.getImageUrl()!=null) {
                    if (chat.isIsseen()) {
                        viewHolderSend.txt_seen_msg_send.setVisibility(View.GONE);
                        viewHolderSend.txt_seen_send.setText("Seen");
                    } else {
                        viewHolderSend.txt_seen_msg_send.setVisibility(View.GONE);
                        viewHolderSend.txt_seen_send.setText("Delivered");
                    }
                } else {
                    viewHolderSend.txt_seen_msg_send.setVisibility(View.GONE);
                    viewHolderSend.txt_seen_send.setVisibility(View.GONE);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        } else {


            viewHolderSend.txt_seen_send.setVisibility(View.GONE);
            viewHolderSend.txt_seen_msg_send.setVisibility(View.GONE);
        }

    }
    private void receiveView(@NonNull Chat chat, RecyclerView.ViewHolder holder, int position){
        ViewHolderReceive viewHolderReceive = (ViewHolderReceive) holder;
        if(chat.getMessage()!=null) {
            Log.d("MessageChat",chat.getMessage());
            viewHolderReceive.show_messages_receive.setText(chat.getMessage());
            viewHolderReceive.MessageImageView_receive.setVisibility(View.GONE);
            viewHolderReceive.roundView_receive.setVisibility(View.GONE);
            viewHolderReceive.show_messages_receive.setOnClickListener(v -> {
                Toast.makeText(mContext, "Okay", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(mContext,viewHolderReceive.show_messages_receive);
                popupMenu.inflate(R.menu.message_manipulaton_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.delete:

                            new AlertDialog.Builder(mContext)
                                    .setMessage("Are you sure you want to delete this message?")
                                    .setPositiveButton("Delete for everyone", (dialog, which) -> {
                                        String unique_id = chat.getUnique_id();
                                        Log.d("unique_id", unique_id);
                                        String sender = chat.getSender();
                                        String receiver = chat.getReceiver();
                                        deleteMessage(sender,receiver,unique_id);
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
                });
                popupMenu.show();
            });
        }

        else if (chat.getImageUrl()!=null){
            Log.d("ImageChat",chat.getImageUrl());
            loadImage(viewHolderReceive.MessageImageView_receive, chat.getImageUrl(), chat.getReceiver());
            viewHolderReceive.show_messages_receive.setVisibility(View.GONE);
        }

        else if(chat.getAudioUrl()!=null) {
            Log.d("AudioUrl",chat.getAudioUrl());

            viewHolderReceive.MessageImageView_receive.setVisibility(View.GONE);
            viewHolderReceive.roundView_receive.setVisibility(View.GONE);
            viewHolderReceive.show_messages_receive.setVisibility(View.GONE);

            String url = chat.getAudioUrl();
            if (audioExist(url)) {
                viewHolderReceive.audioDownloadingSection.setVisibility(View.GONE);
                audioPlay(viewHolderReceive,viewHolderReceive.mediaReceive, viewHolderReceive.startReceive, viewHolderReceive.pauseReceive, chat.getAudioUrl());
            }
            else {

                viewHolderReceive.audioSection.setVisibility(View.GONE);
                viewHolderReceive.audioDownloadingReceive.setVisibility(View.GONE);
                viewHolderReceive.audioDownloadReceive.setOnClickListener(v -> {
                    viewHolderReceive.audioDownloadingReceive.setVisibility(View.VISIBLE);
                    viewHolderReceive.audioDownloadReceive.setVisibility(View.GONE);
                    audioDownload(url,viewHolderReceive);
                });

            }

        }

        if (position == mChat.size()-1){
            try {
                if (chat.getMessage()!=null){
                    if (chat.isIsseen()){
                        viewHolderReceive.txt_seen_receive.setVisibility(View.GONE);
                        viewHolderReceive.txt_seen_msg_receive.setText("Seen");
                    } else {
                        viewHolderReceive.txt_seen_receive.setVisibility(View.GONE);
                        viewHolderReceive.txt_seen_msg_receive.setText("Delivered");
                    }
                } else if (chat.getImageUrl()!=null) {
                    if (chat.isIsseen()) {
                        viewHolderReceive.txt_seen_msg_receive.setVisibility(View.GONE);
                        viewHolderReceive.txt_seen_receive.setText("Seen");
                    } else {
                        viewHolderReceive.txt_seen_msg_receive.setVisibility(View.GONE);
                        viewHolderReceive.txt_seen_receive.setText("Delivered");
                    }
                } else {
                    viewHolderReceive.txt_seen_msg_receive.setVisibility(View.GONE);
                    viewHolderReceive.txt_seen_receive.setVisibility(View.GONE);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            viewHolderReceive.txt_seen_receive.setVisibility(View.GONE);
            viewHolderReceive.txt_seen_msg_receive.setVisibility(View.GONE);
        }
    }
    private Boolean audioExist(String url){
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        File checkFile = new File("/storage/emulated/0/Samwaad/Audio/" + reference.getName() + ".mp4");
        return  checkFile.exists();
    }
    private void audioDownload(String url,RecyclerView.ViewHolder viewHolder) {
        //Downloading Audio file on different thread to reduce the work on main thread
        DownloadThread downloadThread = new DownloadThread(url,viewHolder);
        downloadThread.start();
    }

    class DownloadThread extends Thread {

        private String url;

        private ViewHolderReceive viewHolderReceive = null;
        private ViewHolderSend viewHolderSend = null;

        public DownloadThread(String url,RecyclerView.ViewHolder viewHolder){
            this.url = url;
            if(viewHolder instanceof ViewHolderReceive)
                this.viewHolderReceive = (ViewHolderReceive) viewHolder;
            else if(viewHolder instanceof ViewHolderSend)
                this.viewHolderSend = (ViewHolderSend) viewHolder;
        }



        @Override
        public void run() {

            //downloading audio file on a different thread

            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            try{
                File folder = new File("/storage/emulated/0/Samwaad/Audio/");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                String path = "/storage/emulated/0/Samwaad/Audio/" + reference.getName() + ".mp4";
                File audioFile = new File(path);
                audioFile.createNewFile();
                reference.getFile(audioFile)
                        .addOnProgressListener(taskSnapshot -> {
                            //changing download progress to percentage
                            double  progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            //using handler to update ProgressBar(PercentageProgressBar) with newly download progress
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //updating progress according to the ViewHolder object(i.e send view or receive view)
                                    if (viewHolderReceive!=null)
                                        viewHolderReceive.audioDownloadingReceive.setProgress((float)progress,true);
                                    else
                                        viewHolderSend.audioDownloadingSend.setProgress((float)progress,true);
                                }
                            });
                        })

                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                handler.post(() -> {
                                    //setting visibility of downloading section to gone and audio
                                    // controlling ui to visible

                                    //calling AudioPlay method to play audio after it is downloaded
                                    // because if we don't call AudioPlay method we can't play audio immediately
                                    //we will have re-come to the activity to play it just after it is downloaded
                                   if(viewHolderReceive!=null){

                                       viewHolderReceive.audioDownloadingReceive.setVisibility(View.GONE);
                                       viewHolderReceive.startReceive.setVisibility(View.VISIBLE);

                                       audioPlay(viewHolderReceive,viewHolderReceive.mediaReceive,
                                               viewHolderReceive.startReceive,
                                               viewHolderReceive.pauseReceive,
                                               url);
                                   }else{
                                       viewHolderSend.audioDownloadingSend.setVisibility(View.GONE);
                                       viewHolderSend.start.setVisibility(View.GONE);
                                       audioPlay(viewHolderSend,
                                               viewHolderSend.media,
                                               viewHolderSend.start,
                                               viewHolderSend.pause,url);
                                   }


                                });
                                Log.d("FileDownloadSuccess", task.getResult().toString());

                            } else {
                                Log.d("AudioDownloadError", task.getResult().toString(), task.getException());
                            }
                        })
                        .addOnFailureListener((error)->{
                            Toast.makeText(mContext, "Download Failed : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                        .addOnCanceledListener(() -> {
                            Log.d(TAG, "Download Canceled");
                            Toast.makeText(mContext, "Download Canceled ", Toast.LENGTH_SHORT).show();
                        });
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void audioPlay(RecyclerView.ViewHolder viewHolder,View view, Button start,Button pause,String url){

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        String path = "/storage/emulated/0/Samwaad/Audio/" + reference.getName() +".mp4";
        android.net.Uri uri = android.net.Uri.fromFile(new File(path));

        Log.d("UriPath",uri.toString());
        //Creating AudioPlayerThread to reduce the load on main thread
        AudioPlayerThread audioPlayerThread = new AudioPlayerThread(viewHolder,uri);
        audioPlayerThread.start();

    }

    class AudioPlayerThread extends Thread{

        Uri uri;
        ViewHolderReceive viewHolderReceive = null;
        ViewHolderSend viewHolderSend = null;

        public AudioPlayerThread(RecyclerView.ViewHolder viewHolder, Uri uri){
            //Down-casting viewHolder
            if (viewHolder instanceof ViewHolderReceive)
                this.viewHolderReceive = (ViewHolderReceive) viewHolder;
            else
                this.viewHolderSend = (ViewHolderSend) viewHolder;
            this.uri = uri;
        }

        @Override
        public void run() {
            MediaPlayer mediaPlayer = new MediaPlayer();
            //setting AudioAttributes
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            try {
                //moving mediaPlayer object from idle state to initialized state
                mediaPlayer.setDataSource(mContext,uri);
                //preparing mediaPlayer object
                mediaPlayer.prepare();

                //using handler to interact with main thread for starting and pausing the audio
                handler.post(()->{

                    //according to viewHolder
                    if(viewHolderSend!=null){
                        viewHolderSend.start.setOnClickListener(view -> {
                            mediaPlayer.start();
                            viewHolderSend.start.setVisibility(View.GONE);
                            viewHolderSend.pause.setVisibility(View.VISIBLE);
                        });
                        viewHolderSend.pause.setOnClickListener(view -> {
                            mediaPlayer.pause();
                            viewHolderSend.pause.setVisibility(View.GONE);
                            viewHolderSend.start.setVisibility(View.VISIBLE);
                        });
                    }else{
                        viewHolderReceive.startReceive.setOnClickListener(view -> {
                            mediaPlayer.start();
                            viewHolderReceive.startReceive.setVisibility(View.GONE);
                            viewHolderReceive.pauseReceive.setVisibility(View.VISIBLE);
                        });
                        viewHolderReceive.pauseReceive.setOnClickListener(view -> {
                            mediaPlayer.pause();
                            viewHolderReceive.pauseReceive.setVisibility(View.GONE);
                            viewHolderReceive.startReceive.setVisibility(View.VISIBLE);
                        });
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOMedia","error",e);
            }
        }
    }
    private void loadImage(ImageView imgView,String imageUrl,String folder){

        //loading image into ImageView after downloading the image if image is not already on device
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        File localFile = new File("/storage/emulated/0/Samwaad/Photos/"+folder+"/");
        if (!localFile.exists()){
            localFile.mkdirs();
        }
        File rootfile = new File("/storage/emulated/0/Samwaad/Photos/"+folder+"/"+reference.getName()+".jpg");
        //checking if image exists on device
        if (rootfile.exists()){
            Log.d("ImageLoaded","Loading...");
            //if exists loading it into the ImageView
            Glide.with(mContext)
                    .load(rootfile)
                    .apply(new RequestOptions().transform(new RoundedCorners(50)).error(R.drawable.ic_person))
                    .placeholder(R.drawable.glide_placeholder)
                    .priority(Priority.HIGH)
                    .into(imgView);
        } else{
            //if not downloading it
            try {
                rootfile.createNewFile();
                Log.d("fileCPath", rootfile.getPath());
                Log.d("fileCreated",localFile.getPath());
                reference.getFile(rootfile)
                        .addOnProgressListener(taskSnapshot -> {
                            //loading it into ImageView if downloaded
                            if (taskSnapshot.getBytesTransferred() == taskSnapshot.getTotalByteCount()){
                                Log.d("ImageDown","Downloaded");
                                Glide.with(mContext)
                                        .load(rootfile)
                                        .apply(new RequestOptions().transform(new RoundedCorners(50)).error(R.drawable.ic_person))
                                        .placeholder(R.drawable.glide_placeholder)
                                        .priority(Priority.HIGH)
                                        .into(imgView);
                            }
                        }).addOnCanceledListener(() -> Toast.makeText(mContext, "Task canceled", Toast.LENGTH_SHORT).show());
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}