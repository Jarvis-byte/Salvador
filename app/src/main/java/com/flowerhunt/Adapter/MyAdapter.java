package com.flowerhunt.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flowerhunt.Model.FlowerList;
import com.flowerhunt.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.viewHolder> {
    View view;
    Context mContext;
    ArrayList<FlowerList> list = new ArrayList<>();

    public MyAdapter(Context mContext, ArrayList<FlowerList> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.card_rv_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.flower_name.setText(list.get(position).getFlower_name());
        holder.flower_number.setText(list.get(position).getNumber_of_roses());
        Glide.with(holder.flower_image.getContext())
                .load(list.get(position).getImage_url())
                .fitCenter()
                .placeholder(R.drawable.rose2)
                .into(holder.flower_image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView flower_name, flower_number;
        ImageView flower_image;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            flower_name = itemView.findViewById(R.id.flower_name);
            flower_number = itemView.findViewById(R.id.flower_number);
            flower_image = itemView.findViewById(R.id.flower_image);
        }
    }
}
