package com.codebook.wallyapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class WallpaperAdapter extends ArrayAdapter<DataHandler> {
    Context c;
    int resource;
    List<DataHandler> dataHandlerList;



    public WallpaperAdapter(Context context, int resource, List<DataHandler> list) {
        super(context, resource, list);
        this.c = context;
        this.resource = resource;
        this.dataHandlerList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(c);
        @SuppressLint("ViewHolder") View view=layoutInflater.inflate(resource,null,false);
        DataHandler data=dataHandlerList.get(position);
        TextView titleTV=view.findViewById(R.id.title);
        ImageView imageIV=view.findViewById(R.id.listImage);
        titleTV.setText(data.getTitle());
        Glide.with(c).load(data.thumbnail).centerCrop()
                .error(R.drawable.no_image)
                .placeholder(R.drawable.ic_loading)
                .into(imageIV);
        return view;
    }
}
