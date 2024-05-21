package com.example.tfg.route;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.binDetails.BinDetails;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BinAdapter extends RecyclerView.Adapter<BinAdapter.BinViewHolder> {

    private List<BinDetails> binList;

    public BinAdapter(List<BinDetails> binList) {
        this.binList = binList;
    }

    @NonNull
    @Override
    public BinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bin, parent, false);
        return new BinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BinViewHolder holder, int position) {
        BinDetails binDetails = binList.get(position);
        holder.bind(binDetails);
    }

    @Override
    public int getItemCount() {
        return binList.size();
    }

    public static class BinViewHolder extends RecyclerView.ViewHolder {

        TextView binTextView;
        ImageView removeImage;
        public BinViewHolder(@NonNull View itemView) {
            super(itemView);
            binTextView = itemView.findViewById(R.id.binTextView);
            removeImage = itemView.findViewById(R.id.removeImage);
        }

        public void bind(BinDetails binDetails) {
            binTextView.setText(String.format("%s: %s", binDetails.getTitulo(), binDetails.getUbicacion()));
            removeImage.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ((RouteActivity) v.getContext()).removeBin(position);
                }
            });
        }
    }
}

