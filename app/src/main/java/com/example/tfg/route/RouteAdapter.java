package com.example.tfg.route;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.adminProfile.AdminProfileActivity;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private final List<RouteDetails> routeList;

    public RouteAdapter(List<RouteDetails> routeList) {
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        RouteDetails routeDetails = routeList.get(position);
        holder.bind(routeDetails);
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        TextView routeTextView;
        ImageView removeImage;
        ImageView modifyImage;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            routeTextView = itemView.findViewById(R.id.routeTextView);
            removeImage = itemView.findViewById(R.id.removeImage);
            modifyImage = itemView.findViewById(R.id.modImage);
        }

        public void bind(RouteDetails routeDetails) {
            routeTextView.setText(routeDetails.getTitulo());
            modifyImage.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ((AdminProfileActivity) v.getContext()).modifyRoute(position);
                }
            });
            removeImage.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ((AdminProfileActivity) v.getContext()).removeRoute(position);
                }
            });
        }
    }
}
