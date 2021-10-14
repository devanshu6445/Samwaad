package com.india.chat.samwaad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class customListView extends ArrayAdapter {
    int[] imageArray;
    String[] titleArray;
    int[] imagearray1;
    public customListView(Context context, String[] titles1, int[] imagearray1, int[] img1){
        super(context, R.layout.custom_list_view, R.id.idTitle, titles1);
        this.imageArray=img1;
        this.titleArray=titles1;
        this.imagearray1=imagearray1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_list_view, parent, false);

        ImageView myImage = row.findViewById(R.id.idPic);
        TextView myTitle = row.findViewById(R.id.idTitle);
        ImageView myforward = row.findViewById(R.id.idforward);
        myImage.setImageResource(imageArray[position]);
        myTitle.setText(titleArray[position]);
        myforward.setImageResource(imagearray1[position]);
        return row;
    }
}
